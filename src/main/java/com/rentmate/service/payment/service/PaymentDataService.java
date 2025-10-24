package com.rentmate.service.payment.service;

import com.rentmate.service.payment.data.PaymentData;
import com.rentmate.service.payment.entity.PaymentDataEntity;
import com.rentmate.service.payment.repo.PaymentRepository;
import com.rentmate.service.payment.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentDataService {
    private final PaymentRepository paymentRepository;
    public PaymentDataService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    public PaymentDataEntity savePayment(PaymentData paymentData) {
        if (paymentData.getRentalId() == null){
            throw new RuntimeException("RentalID is nullXD");
        }
        PaymentDataEntity paymentDataEntity = PaymentDataEntity.paymentDataToPaymentDataEntity(paymentData);
        if (paymentDataEntity.getRentalID() == null){
            throw new RuntimeException("Rental ID is null");
        }
        return paymentRepository.save(paymentDataEntity);
    }
}
