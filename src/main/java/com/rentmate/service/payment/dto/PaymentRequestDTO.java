package com.rentmate.service.payment.dto;

import java.util.Map;

public class    PaymentRequestDTO {
    private String eventType;
    private Object data;

    public String geteventType() { return eventType; }
    public void setAction(String action) { this.eventType = action; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
