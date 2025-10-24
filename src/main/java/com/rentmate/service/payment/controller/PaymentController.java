package com.rentmate.service.payment.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmate.service.payment.data.PaymentData;
import com.rentmate.service.payment.service.UserService;
import com.rentmate.service.payment.service.PaymentService;
import com.rentmate.service.payment.refund.RefundData;
import com.rentmate.service.payment.status.PaymentStatus;
import com.rentmate.service.payment.status.Status;
import com.rentmate.service.payment.stripefactory.StripeFactory;
import com.rentmate.service.payment.user.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController("/payment")
public class PaymentController {
    @Value("${stripe.api.key}")
    private String stripeKey;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StripeFactory stripeFactory;

    @Autowired
    UserService userService;

    @DeleteMapping("/delete")
    public Status delete (@RequestBody User user) throws StripeException {
        Stripe.apiKey = stripeKey;
        Account account = Account.retrieve(user.getStripeAccountId());
        account.delete();
        return new Status(PaymentStatus.SUCCESS);
    }

    @PostMapping("/createrenter")
    public Status createrenter(@RequestBody User user) throws StripeException {
        Stripe.apiKey = stripeKey;
        Customer customer = stripeFactory.createCustomer(user);
        if (customer == null){
            return new Status(PaymentStatus.FAILED);
        }
        return new Status(PaymentStatus.SUCCESS);
    }

    @PostMapping("/createowner")
    public ResponseEntity<?> createOwner(@RequestBody User user) throws StripeException {
        Stripe.apiKey = stripeKey;
        Account account = stripeFactory.createAccount(user);
        if (account == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "Status", PaymentStatus.FAILED.toString(),
                            "error", user.getErrorMessage()
                    ));
        }
        AccountLink accountLink = stripeFactory.createAccountLink(account);
        if (accountLink == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "Status", PaymentStatus.FAILED.toString(),
                            "error", "Failed to create account"
                    ));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(accountLink.getUrl()));
        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }


    @PostMapping("/createpayment")
    public Status createPayment(@RequestBody PaymentData paymentdata) throws StripeException {
        if (paymentdata.getRentalId() == null){
            return new Status(PaymentStatus.FAILED, "rentalId is not mentioned");
        }
        paymentdata.setPaymentData(userService);
        paymentdata = paymentService.createPaymentIntent(paymentdata);
        if (paymentdata.getErrorMessage() != null){
            return new Status(PaymentStatus.FAILED, paymentdata.getErrorMessage());
        }
        return new Status(paymentService.createPaymentIntent(paymentdata).getStatus());
    }

    @PostMapping("/refund")
    public Status refund(@RequestBody RefundData refundData) throws StripeException {
        return new Status(paymentService.refund(refundData).getStatus());
    }

    @GetMapping("/getuser/{id}")
    public User getUserById(@PathVariable Long id){
        return User.userEntityToUser(userService.getUserById(id).get());
    }

    @GetMapping("/success")
    public ResponseEntity<?> success(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "Status", PaymentStatus.FAILED.toString(),
                        "error", "Failed to create account"
                ));
    }

}
