package com.rentmate.service.payment.dto;

public class PaymentResponseDTO {
    private Long rentalId;
    private String eventType;

    public PaymentResponseDTO(Long rentalId, String message) {
        this.rentalId = rentalId;
        this.eventType = message;
    }

    public String getEventType() { return eventType; }
    public Long getRentalId() { return rentalId; }
}
