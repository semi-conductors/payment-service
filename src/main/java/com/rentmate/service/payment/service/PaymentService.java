package com.rentmate.service.payment.service;

import com.rentmate.service.payment.data.PaymentData;
import com.rentmate.service.payment.entity.PaymentDataEntity;
import com.rentmate.service.payment.repo.PaymentRepository;
import com.rentmate.service.payment.status.PaymentStatus;
import com.rentmate.service.payment.refund.RefundData;
import com.stripe.exception.StripeException;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.Transfer;
import com.stripe.param.RefundCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    @Value("${stripe.api.key}")
    private String stripekey;
    @Autowired
    PaymentDataService paymentDataService;
    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentService(PaymentDataService paymentDataService) {
        this.paymentDataService = paymentDataService;
    }

    public PaymentData createPaymentIntent(PaymentData paymentData) throws StripeException {
        Stripe.apiKey = stripekey;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", paymentData.getAmount());
        params.put("currency", paymentData.getCurrency());
        params.put("customer", paymentData.getRetnerStripeAccountID());
        params.put("payment_method", "pm_card_visa");
        params.put("off_session", true);
        params.put("confirm", true);
        paymentData = getPayment(paymentData, params);
        if (paymentData.getStatus() != PaymentStatus.FAILED) {
            paymentData = doPayment(paymentData);
        }
        paymentData = setSucessPayment(paymentData);
        paymentDataService.savePayment(paymentData);
        return paymentData;
    }

    public PaymentData setSucessPayment(PaymentData paymentData){
        paymentData.setStatus(PaymentStatus.SUCCESS);
        return paymentData;
    }

    public PaymentData doPayment(final PaymentData paymentData) throws StripeException {
        Map<String, Object> transferParams = new HashMap<>();
        transferParams.put("amount", paymentData.getTransferedToOwner());
        transferParams.put("currency", "usd");
        transferParams.put("destination", paymentData.getOwnerStripeAccountID());
        Transfer transfer;
        try {
            transfer = Transfer.create(transferParams);
        } catch (StripeException e) {
            RefundData refundData = new RefundData();
            refundData.setPaymentID(paymentData.getRenterPaymentID());
            refundData.setAmount(paymentData.getAmount());
            refund(refundData);
            paymentData.setStatus(PaymentStatus.FAILED);
            return paymentData;
        }
        paymentData.setOwnerPaymentID(transfer.getId());
        setSucessPayment(paymentData);
        return paymentData;
    }

    public PaymentData getPayment(final PaymentData paymentData, final Map<String, Object> params) throws StripeException {
        paymentData.setStatus(PaymentStatus.PENDING);
        PaymentIntent payment;
        try {
            payment = PaymentIntent.create(params);
        } catch(StripeException e) {
            throw new RuntimeException(e);
//            paymentData.setStatus(PaymentStatus.FAILED);
//            paymentData.setErrorMessage(e.getMessage());
//            return paymentData;
        }
        paymentData.setRenterPaymentID(payment.getId());
        return paymentData;
    }

    public RefundData refund(final RefundData refundData) throws StripeException {
        Stripe.apiKey = stripekey;
        PaymentDataEntity paymentDataEntity = paymentRepository.findById(refundData.getRentalId()).orElse(null);
        if (paymentDataEntity == null) {
            refundData.setErrorMessaeg("Such rentalId Does not exist");
            refundData.setStatus(PaymentStatus.FAILED);
            return refundData;
        }
        String paymentId = paymentDataEntity.getRenterPaymentID();
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentId)
                .setAmount(paymentDataEntity.getInsurance())
                .build();
        refundData.setStatus(PaymentStatus.PENDING);
        try {
            Refund refund = Refund.create(params);
        }catch(StripeException e) {
            refundData.setStatus(PaymentStatus.FAILED);
            refundData.setErrorMessaeg(e.getMessage());
            return refundData;
        }
        refundData.setStatus(PaymentStatus.SUCCESS);
        paymentDataEntity.setStatus(PaymentStatus.REFUNDED.toString());
        paymentRepository.save(paymentDataEntity);
        return refundData;
    }
}
