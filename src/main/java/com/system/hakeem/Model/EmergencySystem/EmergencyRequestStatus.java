package com.system.hakeem.Model.EmergencySystem;

public enum EmergencyRequestStatus {
    PENDING, // Request created, waiting for ambulance assignment
    ASSIGNED, // Ambulance assigned, en route
    IN_PROGRESS, // Ambulance arrived, providing assistance
    COMPLETED, // Emergency handled, request completed
    CANCELLED // Request cancelled by patient or system
}
