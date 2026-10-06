package com.anushka.parcel.tracking.repository;

import com.anushka.parcel.tracking.model.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByTrackingNumberOrderByCreatedAtAsc(String trackingNumber);
}
