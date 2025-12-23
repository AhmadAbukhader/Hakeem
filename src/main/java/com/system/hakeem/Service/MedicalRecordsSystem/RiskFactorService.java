package com.system.hakeem.Service.MedicalRecordsSystem;

import com.system.hakeem.Dto.MedicalRecordsSystem.CreateRiskFactorRequest;
import com.system.hakeem.Dto.MedicalRecordsSystem.RiskFactorResponse;
import com.system.hakeem.Dto.MedicalRecordsSystem.UpdateRiskFactorRequest;
import com.system.hakeem.Model.MedicalRecordsSystem.RiskFactor;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.MedicalRecordsSystem.RiskFactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RiskFactorService {

    private final RiskFactorRepository riskFactorRepository;

    @Transactional
    public RiskFactorResponse createRiskFactor(CreateRiskFactorRequest request) {
        // Get the current authenticated user (patient)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User patient = (User) auth.getPrincipal();

        // Validate factor name
        if (request.getFactorName() == null || request.getFactorName().trim().isEmpty()) {
            throw new IllegalArgumentException("Factor name is required");
        }

        // Create and save risk factor
        RiskFactor riskFactor = RiskFactor.builder()
                .patient(patient)
                .factorName(request.getFactorName().trim())
                .build();

        RiskFactor savedRiskFactor = riskFactorRepository.save(riskFactor);

        return buildRiskFactorResponse(savedRiskFactor);
    }

    public RiskFactorResponse getRiskFactorById(Integer id) {
        Optional<RiskFactor> riskFactorOpt = riskFactorRepository.findById(id);
        if (riskFactorOpt.isEmpty()) {
            throw new IllegalArgumentException("Risk factor not found with id: " + id);
        }

        RiskFactor riskFactor = riskFactorOpt.get();

        // Verify that the current user owns this risk factor
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (riskFactor.getPatient().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("You do not have permission to access this risk factor");
        }

        return buildRiskFactorResponse(riskFactor);
    }

    public List<RiskFactorResponse> getRiskFactorsByPatientId(Integer patientId) {
        // Validate patientId
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("Invalid patient ID: " + patientId);
        }

        // Verify that the current user is requesting their own risk factors
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (patientId != currentUser.getId()) {
            throw new IllegalArgumentException("You can only view your own risk factors");
        }

        List<RiskFactor> riskFactors = riskFactorRepository.findByPatientId(patientId);

        return riskFactors.stream()
                .map(this::buildRiskFactorResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RiskFactorResponse updateRiskFactor(Integer id, UpdateRiskFactorRequest request) {
        Optional<RiskFactor> riskFactorOpt = riskFactorRepository.findById(id);
        if (riskFactorOpt.isEmpty()) {
            throw new IllegalArgumentException("Risk factor not found with id: " + id);
        }

        RiskFactor riskFactor = riskFactorOpt.get();

        // Verify that the current user owns this risk factor
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (riskFactor.getPatient().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("You do not have permission to update this risk factor");
        }

        // Validate factor name
        if (request.getFactorName() != null && !request.getFactorName().trim().isEmpty()) {
            riskFactor.setFactorName(request.getFactorName().trim());
        } else {
            throw new IllegalArgumentException("Factor name is required");
        }

        RiskFactor updatedRiskFactor = riskFactorRepository.save(riskFactor);

        return buildRiskFactorResponse(updatedRiskFactor);
    }

    @Transactional
    public void deleteRiskFactor(Integer id) {
        Optional<RiskFactor> riskFactorOpt = riskFactorRepository.findById(id);
        if (riskFactorOpt.isEmpty()) {
            throw new IllegalArgumentException("Risk factor not found with id: " + id);
        }

        RiskFactor riskFactor = riskFactorOpt.get();

        // Verify that the current user owns this risk factor
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if (riskFactor.getPatient().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("You do not have permission to delete this risk factor");
        }

        riskFactorRepository.delete(riskFactor);
    }

    private RiskFactorResponse buildRiskFactorResponse(RiskFactor riskFactor) {
        return RiskFactorResponse.builder()
                .id(riskFactor.getId())
                .patientId(riskFactor.getPatient().getId())
                .factorName(riskFactor.getFactorName())
                .build();
    }
}
