package com.rentmate.service.payment.entity;

import com.rentmate.service.payment.data.PaymentData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
@Entity
public class PaymentDataEntity {
    @Id
    @Column(name = "rental_id")
    private Long rentalID;
    private Long amount;
    private Long insurance;
    @Column(name = "renter_id")
    private Long renterId;
    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "renter_payment_id")
    private String renterPaymentID;
    @Column(name = "owner_payment_id")
    private String ownerPaymentID;
    @Column(name = "status")
    private String status;

    public Long getRentalID() {
        return rentalID;
    }

    public void setRentalID(Long rentalID) {
        this.rentalID = rentalID;
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

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getRenterPaymentID() {
        return renterPaymentID;
    }

    public void setRenterPaymentID(String renterPaymentID) {
        this.renterPaymentID = renterPaymentID;
    }

    public String getOwnerPaymentID() {
        return ownerPaymentID;
    }

    public void setOwnerPaymentID(String ownerPaymentID) {
        this.ownerPaymentID = ownerPaymentID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static PaymentDataEntity paymentDataToPaymentDataEntity(PaymentData paymentData) {
        PaymentDataEntity paymentDataEntity = new PaymentDataEntity();
        paymentDataEntity.setRentalID(paymentData.getRentalId());
        paymentDataEntity.setAmount(paymentData.getAmount());
        paymentDataEntity.setInsurance(paymentData.getInsurance());
        paymentDataEntity.setRenterPaymentID(paymentData.getRenterPaymentID());
        paymentDataEntity.setOwnerId(paymentData.getOwnerId());
        paymentDataEntity.setOwnerPaymentID(paymentData.getOwnerPaymentID());
        paymentDataEntity.setRenterId(paymentData.getRenterId());
        paymentDataEntity.setStatus(paymentData.getStatus().toString());
        return paymentDataEntity;
    }
}
