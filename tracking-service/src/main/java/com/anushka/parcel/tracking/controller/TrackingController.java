package com.anushka.parcel.tracking.controller;

import com.anushka.parcel.tracking.dto.*;
import com.anushka.parcel.tracking.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService service;
    private final String internalKey;

    public TrackingController(TrackingService service, @Value("${app.internal-key}") String internalKey) {
        this.service = service;
        this.internalKey = internalKey;
    }

    /** Public: anyone with a tracking number can see the delivery history. */
    @GetMapping("/{trackingNumber}")
    public List<TrackingEventResponse> history(@PathVariable String trackingNumber) {
        return service.history(trackingNumber);
    }

    /** Manual update by staff (ADMIN / AGENT), e.g. "Arrived at Mumbai hub". */
    @PostMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public ResponseEntity<TrackingEventResponse> addEvent(@Valid @RequestBody TrackingEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request));
    }

    /** Service-to-service call from parcel-service, protected by a shared key. */
    @PostMapping("/internal/events")
    public ResponseEntity<TrackingEventResponse> addInternal(
            @RequestHeader(value = "X-Internal-Key", required = false) String key,
            @Valid @RequestBody TrackingEventRequest request) {
        if (key == null || !key.equals(internalKey)) {
            throw new AccessDeniedException("Invalid internal key");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request));
    }
}
