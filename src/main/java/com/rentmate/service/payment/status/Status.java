package com.rentmate.service.payment.status;

public class Status {
    private String status;
    private Long rentalId;
    private String eventType;

    public Status(){

    }

    public Status(PaymentStatus paymentStatus){
        this.status = paymentStatus.toString();
    }

    public Status(Long rentalId, String eventType){
        this.rentalId = rentalId;
        this.eventType = eventType;
    }

    public void setEventType(String eventType){
        this.eventType = eventType;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setrentalId(Long rentalId) {
        this.rentalId = rentalId;
    }
    public Long getRentalId(){
        return rentalId;
    }
    public String getEventType(){
        return this.eventType;
    }
}
