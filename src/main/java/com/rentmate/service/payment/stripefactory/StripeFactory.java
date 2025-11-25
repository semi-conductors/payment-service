package com.rentmate.service.payment.stripefactory;

import com.rentmate.service.payment.entity.UserEntity;
import com.rentmate.service.payment.service.UserService;
import com.rentmate.service.payment.user.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Customer;
import com.stripe.model.CustomerBalanceTransaction;
import com.stripe.param.CustomerBalanceTransactionCreateParams;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeFactory {
    UserService userService;
    public StripeFactory(UserService userService) {
        this.userService = userService;
    }
    public Account createAccount(User user){
        UserEntity userEntity = userService.getUserById(user.getUserId()).orElse(null);
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("transfers", Map.of("requested", true));
        Map<String, Object> params= new HashMap<>();
        params.put("type", "express");
        params.put("email", user.getEmail());
        params.put("country", "US");
        params.put("capabilities", capabilities);
        Account account;
        try{
            account = Account.create(params);
        }catch (StripeException e){
            user.setErrorMessage(e.getMessage());
            return null;
        }
        user.setStripeAccountId(account.getId());
        if (userEntity != null){
            userEntity.setAccountId(account.getId());
            userService.saveCustomer(userEntity);
        }else {
            userService.saveCustomer(userService.userToUserEntity(user));
        }
        return account;
    }

    public AccountLink createAccountLink(Account account){
        Map<String, Object> accountLinkParams = new HashMap<>();
        accountLinkParams.put("account", account.getId());
        accountLinkParams.put("type", "account_onboarding");
        accountLinkParams.put("refresh_url", "http://localhost:4200/");
        accountLinkParams.put("return_url", "http://localhost:4200/profile"); /// TODO: CHANGE IT TO user profile
        AccountLink accountLink;
        try {
            accountLink = AccountLink.create(accountLinkParams);
        }catch(StripeException e){
            return null;
        }
        return accountLink;
    }

    public Customer createCustomer(User user){
        UserEntity curUser = userService.getUserById(user.getUserId()).orElse(null);

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getName());
        params.put("email", user.getEmail());
        Customer customer;
        try {
            customer = Customer.create(params);
        }catch (StripeException e){
            user.setErrorMessage(e.getMessage());
            return null;
        }
        user.setStripeCustomerId(customer.getId());
        if (curUser != null){
            curUser.setCustomerId(customer.getId());
            userService.saveCustomer(curUser);
        }else {
            userService.saveCustomer(userService.userToUserEntity(user));
        }
        return customer;
    }
}
