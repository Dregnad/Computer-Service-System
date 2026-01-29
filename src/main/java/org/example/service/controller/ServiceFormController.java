package org.example.service.controller;

import io.camunda.client.CamundaClient;
import org.example.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class ServiceFormController {

    @Autowired private CamundaClient client;
    @Autowired private DatabaseService dbService;

    // Start procesu
    @PostMapping("/start")
    public Map<String, String> startProcess(@RequestBody Map<String, Object> variables) throws Exception {
        String orderId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        variables.put("orderId", orderId);

        dbService.saveNewOrder(variables, orderId);

        client.newPublishMessageCommand()
                .messageName("NewServiceOrderMsg")
                .correlationKey("")
                .variables(variables)
                .send()
                .join();

        System.out.println("LOG: Start procesu dla OrderID: " + orderId);
        return Map.of("orderId", orderId);
    }

    // Symulacja otrzymania płatności
    @PostMapping("/simulate-payment")
    public void simulatePayment(@RequestParam String orderId) {
        System.out.println("LOG: Otrzymano żądanie symulacji płatności dla: " + orderId);

        client.newPublishMessageCommand()
                .messageName("msg_platnosc_otrzymana")
                .correlationKey(orderId)
                .send()
                .join();
    }
}