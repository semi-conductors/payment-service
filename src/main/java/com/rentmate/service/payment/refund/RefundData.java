package com.rentmate.service.payment.refund;

import com.rentmate.service.payment.status.PaymentStatus;

public class RefundData {
    private long rentalId;
    private String ownerId;
    private String renterId;
    private Long amount;
    private String paymentID;
    private PaymentStatus status;
    private String errorMessaeg;

    public String getErrorMessaeg() {
        return errorMessaeg;
    }

    public void setErrorMessaeg(String errorMessaeg) {
        this.errorMessaeg = errorMessaeg;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }


    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public long getRentalId() {
        return rentalId;
    }

    public void setRentalId(long rentalId) {
        this.rentalId = rentalId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
