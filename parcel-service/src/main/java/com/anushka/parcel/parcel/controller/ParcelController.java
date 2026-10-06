package com.anushka.parcel.parcel.controller;

import com.anushka.parcel.parcel.dto.*;
import com.anushka.parcel.parcel.model.ParcelStatus;
import com.anushka.parcel.parcel.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

    private final ParcelService service;

    public ParcelController(ParcelService service) { this.service = service; }

    private static String role(Authentication a) {
        return a.getAuthorities().iterator().next().getAuthority(); // e.g. ROLE_ADMIN
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<ParcelResponse> create(@Valid @RequestBody CreateParcelRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth.getName()));
    }

    @GetMapping
    public PageResponse<ParcelResponse> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) ParcelStatus status,
                                             Authentication auth) {
        return service.list(auth.getName(), role(auth), status, page, size);
    }

    @GetMapping("/{trackingNumber}")
    public ParcelResponse get(@PathVariable String trackingNumber, Authentication auth) {
        return service.get(trackingNumber, auth.getName(), role(auth));
    }

    @PutMapping("/{trackingNumber}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ParcelResponse assign(@PathVariable String trackingNumber, @Valid @RequestBody AssignRequest request,
                                 Authentication auth) {
        return service.assign(trackingNumber, request.agentEmail(), auth.getName());
    }

    @PutMapping("/{trackingNumber}/status")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public ParcelResponse updateStatus(@PathVariable String trackingNumber, @Valid @RequestBody StatusUpdateRequest request,
                                       Authentication auth) {
        return service.updateStatus(trackingNumber, request, auth.getName(), role(auth));
    }
}
