package com.rentmate.service.payment.dto;

import com.rentmate.service.payment.data.PaymentData;
import com.rentmate.service.payment.refund.RefundData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String eventType;
    private Long rentalId;
    private Long ownerId;
    private Long renterId;
    private Long amount;
    private Long insurance;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getInsurance() {
        return insurance;
    }

    public void setInsurance(Long insurance) {
        this.insurance = insurance;
    }

    public PaymentData getPaymentData(){
        PaymentData paymentData = new PaymentData();
        paymentData.setRentalId(rentalId);
        paymentData.setOwnerId(ownerId);
        paymentData.setRenterId(renterId);
        paymentData.setAmount(amount);
        paymentData.setInsurance(insurance);
        return paymentData;
    }

    public RefundData getRefundData(){
        RefundData refundData = new RefundData();
        refundData.setRentalId(rentalId);
        refundData.setOwnerId(ownerId);
        refundData.setRenterId(renterId);
        return refundData;
    }

}
