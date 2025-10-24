package com.rentmate.service.payment.status;

public class Status {
    private String status;
    private String errorMessage;

    public Status(PaymentStatus paymentStatus){
        this.status = paymentStatus.toString();
    }

    public Status(PaymentStatus paymentStatus, String errorMessage) {
        this.status = paymentStatus.toString();
        this.errorMessage = errorMessage;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
