package com.anushka.parcel.tracking.service;

import com.anushka.parcel.tracking.dto.*;
import com.anushka.parcel.tracking.exception.ResourceNotFoundException;
import com.anushka.parcel.tracking.model.TrackingEvent;
import com.anushka.parcel.tracking.repository.TrackingEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackingService {

    private final TrackingEventRepository repo;

    public TrackingService(TrackingEventRepository repo) { this.repo = repo; }

    public TrackingEventResponse add(TrackingEventRequest r) {
        TrackingEvent e = new TrackingEvent();
        e.setTrackingNumber(r.trackingNumber());
        e.setStatus(r.status());
        e.setLocation(r.location());
        e.setNote(r.note());
        e.setUpdatedBy(r.updatedBy());
        return TrackingEventResponse.from(repo.save(e));
    }

    public List<TrackingEventResponse> history(String trackingNumber) {
        List<TrackingEvent> events = repo.findByTrackingNumberOrderByCreatedAtAsc(trackingNumber);
        if (events.isEmpty()) {
            throw new ResourceNotFoundException("No tracking history for " + trackingNumber);
        }
        return events.stream().map(TrackingEventResponse::from).toList();
    }
}
