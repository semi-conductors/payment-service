package com.rentmate.service.payment.dto;

public class PaymentResponseDTO {
    private String status;
    private String message;

    public PaymentResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
