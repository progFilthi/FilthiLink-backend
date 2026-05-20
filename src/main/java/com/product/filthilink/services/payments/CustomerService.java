package com.product.filthilink.services.payments;

import com.dodopayments.api.client.DodoPaymentsClient;
import com.dodopayments.api.models.customers.Customer;
import com.dodopayments.api.models.customers.CustomerCreateParams;
import com.product.filthilink.repositories.payments.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final SubscriptionRepository subscriptionRepository;

    private final DodoPaymentsClient dodoPaymentsClient;



    public String resolveOrCreateCustomer(String userId, String email){
        return subscriptionRepository.findByUserId(userId)
                .flatMap(
                        sub -> Optional.ofNullable(sub.getDodoCustomerId())
                ).orElseGet(() -> createCustomer(userId, email));
    }


    private String createCustomer(String userId, String email){
        CustomerCreateParams request = CustomerCreateParams.builder()
                .email(email)
                .name("User" + userId)
                .build();

        Customer customer = dodoPaymentsClient.customers().create(request);

        return customer.customerId();
    }
}
