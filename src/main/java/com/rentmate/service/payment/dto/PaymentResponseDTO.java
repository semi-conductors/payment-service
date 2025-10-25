package com.rentmate.service.payment.dto;

public class PaymentResponseDTO {
    private String eventType;
    private Long rentalId;

    public PaymentResponseDTO(Long rentalId, String message) {
        this.rentalId = rentalId;
        this.eventType = message;
    }

    public String getEventType() { return eventType; }
    public Long getRentalId() { return rentalId; }
}
