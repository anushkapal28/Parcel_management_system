package com.anushka.parcel.parcel.service;

import com.anushka.parcel.parcel.dto.TrackingEventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Calls tracking-service (found through Eureka). A tracking failure must never break parcel operations. */
@Component
public class TrackingClient {

    private static final Logger log = LoggerFactory.getLogger(TrackingClient.class);

    private final RestTemplate restTemplate;
    private final String internalKey;

    public TrackingClient(RestTemplate restTemplate, @Value("${app.internal-key}") String internalKey) {
        this.restTemplate = restTemplate;
        this.internalKey = internalKey;
    }

    public void publish(String trackingNumber, String status, String location, String note, String updatedBy) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Key", internalKey);
            var body = new TrackingEventRequest(trackingNumber, status, location, note, updatedBy);
            restTemplate.postForEntity("http://tracking-service/api/tracking/internal/events",
                    new HttpEntity<>(body, headers), Void.class);
        } catch (Exception e) {
            log.warn("Could not record tracking event for {}: {}", trackingNumber, e.getMessage());
        }
    }
}
