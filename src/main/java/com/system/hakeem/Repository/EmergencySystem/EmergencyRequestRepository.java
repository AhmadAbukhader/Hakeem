package com.system.hakeem.Repository.EmergencySystem;

import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Model.EmergencySystem.EmergencyRequest;
import com.system.hakeem.Model.EmergencySystem.EmergencyRequestStatus;
import com.system.hakeem.Model.UserManagement.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Integer> {

    Optional<EmergencyRequest> findByPatientAndStatusIn(User patient, List<EmergencyRequestStatus> statuses);

    List<EmergencyRequest> findByAmbulance(Ambulance ambulance);

    List<EmergencyRequest> findByStatus(EmergencyRequestStatus status);

    Optional<EmergencyRequest> findByPatientAndStatus(User patient, EmergencyRequestStatus status);
}
