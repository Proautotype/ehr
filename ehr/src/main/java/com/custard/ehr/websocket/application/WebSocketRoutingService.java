package com.custard.ehr.websocket.application;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebSocketRoutingService {

    public String labResultTopic(UUID patientId) {
        return "/topic/lab/" + patientId;
    }

    public String patientQueue(UUID patientId) {
        return "/queue/patient/" + patientId;
    }
}
