package com.anushka.parcel.parcel.service;

import com.anushka.parcel.parcel.dto.*;
import com.anushka.parcel.parcel.exception.BadRequestException;
import com.anushka.parcel.parcel.exception.ResourceNotFoundException;
import com.anushka.parcel.parcel.model.Parcel;
import com.anushka.parcel.parcel.model.ParcelStatus;
import com.anushka.parcel.parcel.repository.ParcelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ParcelService {

    private final ParcelRepository repo;
    private final TrackingClient tracking;

    public ParcelService(ParcelRepository repo, TrackingClient tracking) {
        this.repo = repo;
        this.tracking = tracking;
    }

    @Transactional
    public ParcelResponse create(CreateParcelRequest r, String email) {
        Parcel p = new Parcel();
        p.setTrackingNumber("PKG" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        p.setSenderName(r.senderName().trim());
        p.setReceiverName(r.receiverName().trim());
        p.setReceiverAddress(r.receiverAddress().trim());
        p.setWeightKg(r.weightKg());
        p.setStatus(ParcelStatus.CREATED);
        p.setCreatedBy(email);
        p = repo.save(p);
        tracking.publish(p.getTrackingNumber(), "CREATED", "Origin", "Parcel registered", email);
        return ParcelResponse.from(p);
    }

    /** Pagination: the client asks for a page, we never load the whole table. */
    @Transactional(readOnly = true)
    public PageResponse<ParcelResponse> list(String email, String role, ParcelStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Parcel> result = switch (role) {
            case "ROLE_ADMIN" -> status != null ? repo.findByStatus(status, pageable) : repo.findAll(pageable);
            case "ROLE_AGENT" -> repo.findByAssignedAgentEmail(email, pageable);
            default -> repo.findByCreatedBy(email, pageable);
        };
        return PageResponse.of(result.map(ParcelResponse::from));
    }

    @Transactional(readOnly = true)
    public ParcelResponse get(String trackingNumber, String email, String role) {
        Parcel p = find(trackingNumber);
        boolean allowed = switch (role) {
            case "ROLE_ADMIN" -> true;
            case "ROLE_AGENT" -> email.equals(p.getAssignedAgentEmail());
            default -> email.equals(p.getCreatedBy());
        };
        if (!allowed) throw new AccessDeniedException("Not your parcel");
        return ParcelResponse.from(p);
    }

    @Transactional
    public ParcelResponse assign(String trackingNumber, String agentEmail, String adminEmail) {
        Parcel p = find(trackingNumber);
        if (p.getStatus() != ParcelStatus.CREATED && p.getStatus() != ParcelStatus.ASSIGNED) {
            throw new BadRequestException("Parcel can no longer be (re)assigned, status is " + p.getStatus());
        }
        p.setAssignedAgentEmail(agentEmail.trim().toLowerCase());
        p.setStatus(ParcelStatus.ASSIGNED);
        p = repo.save(p);
        tracking.publish(trackingNumber, "ASSIGNED", "Origin hub", "Assigned to agent " + p.getAssignedAgentEmail(), adminEmail);
        return ParcelResponse.from(p);
    }

    @Transactional
    public ParcelResponse updateStatus(String trackingNumber, StatusUpdateRequest r, String email, String role) {
        Parcel p = find(trackingNumber);
        if ("ROLE_AGENT".equals(role) && !email.equals(p.getAssignedAgentEmail())) {
            throw new AccessDeniedException("Parcel is not assigned to you");
        }
        validateTransition(p.getStatus(), r.status(), p.getAssignedAgentEmail());
        p.setStatus(r.status());
        p = repo.save(p);
        tracking.publish(trackingNumber, r.status().name(), r.location(), r.note(), email);
        return ParcelResponse.from(p);
    }

    /** Business rule: DELIVERED / CANCELLED are final; otherwise only move forward. */
    private void validateTransition(ParcelStatus current, ParcelStatus next, String agent) {
        if (current == ParcelStatus.DELIVERED || current == ParcelStatus.CANCELLED) {
            throw new BadRequestException("Parcel is already " + current + " and cannot change");
        }
        if (next == ParcelStatus.ASSIGNED) {
            throw new BadRequestException("Use the /assign endpoint to assign an agent");
        }
        if (next == ParcelStatus.CANCELLED) {
            if (current.ordinal() > ParcelStatus.ASSIGNED.ordinal()) {
                throw new BadRequestException("Parcel already in transit and cannot be cancelled");
            }
            return;
        }
        if (agent == null) {
            throw new BadRequestException("Assign an agent before moving the parcel");
        }
        if (next.ordinal() <= current.ordinal()) {
            throw new BadRequestException("Cannot move status from " + current + " back to " + next);
        }
    }

    private Parcel find(String trackingNumber) {
        return repo.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found: " + trackingNumber));
    }
}
