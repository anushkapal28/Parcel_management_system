package com.anushka.parcel.parcel.repository;

import com.anushka.parcel.parcel.model.Parcel;
import com.anushka.parcel.parcel.model.ParcelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    Optional<Parcel> findByTrackingNumber(String trackingNumber);
    Page<Parcel> findByStatus(ParcelStatus status, Pageable pageable);
    Page<Parcel> findByAssignedAgentEmail(String email, Pageable pageable);
    Page<Parcel> findByCreatedBy(String email, Pageable pageable);
}
