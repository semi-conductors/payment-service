package com.rentmate.service.payment.data;

import com.rentmate.service.payment.entity.PaymentDataEntity;
import com.rentmate.service.payment.entity.UserEntity;
import com.rentmate.service.payment.service.UserService;
import com.rentmate.service.payment.status.PaymentStatus;
import com.rentmate.service.payment.user.User;

public class PaymentData {
    private final String currency = "usd";
    private Long amount;
    private Long renterId;
    private Long ownerId;
    private String renterStripeAccountID;
    private String ownerStripeAccountID;
    private PaymentStatus status;
    private Long insurance;
    private String renterPaymentID;
    private Long rentalId;
    private String ownerPaymentId;
    private String errorMessage;
    private UserService userService;

    public Long getTransferedToOwner(){
        return this.amount - this.insurance;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRenterPaymentID() {
        return renterPaymentID;
    }

    public void setRenterPaymentID(String renterPaymentID) {
        this.renterPaymentID = renterPaymentID;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalID) {
        this.rentalId = rentalID;
    }


    public String getOwnerPaymentID() {
        return ownerPaymentId;
    }

    public void setOwnerPaymentID(String paymentID) {
        this.ownerPaymentId = paymentID;
    }


    public Long getInsurance() {
        return insurance;
    }

    public void setInsurance(Long insuranceAmount) {
        this.insurance= insuranceAmount;
    }


    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getCurrency(){
        return currency;
    }
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
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

    public String getRetnerStripeAccountID() {
        return renterStripeAccountID;
    }

    public void setRetnerStripeAccountID(String retnerStripeAccount) {
        this.renterStripeAccountID = retnerStripeAccount;
    }

    public String getOwnerStripeAccountID() {
        return ownerStripeAccountID;
    }

    public void setOwnerStripeAccountID(String ownerStripeAccount) {
        this.ownerStripeAccountID = ownerStripeAccount;
    }

    public void setPaymentData(UserService userService){
        UserEntity renter = userService.getUserById(this.renterId).orElse(null);
        UserEntity owner = userService.getUserById(this.ownerId).orElse(null);
        if (renter == null){
            this.errorMessage = "No such renter found in database";
            return;
        }else if (owner == null){
            this.errorMessage = "No such owner found in database";
            return;
        }
        this.renterStripeAccountID = renter.getCustomerId();
        this.ownerStripeAccountID = owner.getAccountId();
    }


}
