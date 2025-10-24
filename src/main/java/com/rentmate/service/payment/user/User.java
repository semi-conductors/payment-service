package com.rentmate.service.payment.user;

import com.rentmate.service.payment.entity.UserEntity;

public class User {
    private String name;
    private String email;
    private Long userId;
    private String stripeCustomerId;
    private String stripeAccountId;
    private String errorMessage;
    private final String country = "us";

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public static User userEntityToUser(UserEntity user) {
        User retUser = new User();
        retUser.setEmail(user.getEmail());
        retUser.setName(user.getUserName());
        retUser.setUserId(user.getUserId());
        retUser.setStripeCustomerId(user.getCustomerId());
        retUser.setStripeAccountId(user.getAccountId());
        return retUser;
    }

}
