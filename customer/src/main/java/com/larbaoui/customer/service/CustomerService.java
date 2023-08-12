package com.larbaoui.customer.service;

import com.larbaoui.clients.fraud.FraudCheckResponse;
import com.larbaoui.clients.fraud.FraudClient;
import com.larbaoui.clients.notification.NotificationClient;
import com.larbaoui.clients.notification.NotificationRequest;
import com.larbaoui.customer.dto.CustomerRequest;
import com.larbaoui.customer.model.Customer;
import com.larbaoui.customer.repository.CustomerRepository;
import com.larbaoui.messagequeue.RabbitMQMessageProducer;
import org.springframework.stereotype.Service;

@Service
public record CustomerService (
        CustomerRepository customerRepository,
        FraudClient fraudClient,
        RabbitMQMessageProducer rabbitMQMessageProducer) {
    public void registerCustomer(CustomerRequest customerRequest) {
        Customer customer = Customer.builder()
                .firstName(customerRequest.firstName())
                .lastName(customerRequest.lastName())
                .email(customerRequest.email())
                .build();

        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest =  new NotificationRequest(customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to my chanel...", customer.getFirstName())
        );

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

    }
}
