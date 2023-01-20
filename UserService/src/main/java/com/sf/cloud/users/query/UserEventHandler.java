package com.sf.cloud.users.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import com.sf.cloud.core.data.PaymentDetails;
import com.sf.cloud.core.data.User;
import com.sf.cloud.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UserEventHandler {

    @QueryHandler
    public User findUserDetails(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery){

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("0432 267 8987 234")
                .cvv("788")
                .name("Sunny Wong")
                .validUntilMonth(11)
                .validUntilYear(2025)
                .build();

        User userRe = User.builder()
                .firstName("Sunny")
                .lastName("Wong")
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        return userRe;
    }

}
