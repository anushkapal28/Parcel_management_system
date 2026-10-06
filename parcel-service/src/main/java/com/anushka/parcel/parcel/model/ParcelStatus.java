package com.anushka.parcel.parcel.model;

/** Order matters: a parcel can only move forward (or be CANCELLED early). */
public enum ParcelStatus { CREATED, ASSIGNED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED }
