package com.system.hakeem.Service.EmergencySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceRequest;
import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceResponse;
import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Model.EmergencySystem.AmbulanceLocation;
import com.system.hakeem.Model.EmergencySystem.AmbulanceStatus;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.EmergencySystem.AmbulanceLocationRepository;
import com.system.hakeem.Repository.EmergencySystem.AmbulanceRepository;
import com.system.hakeem.Exceptions.BadRequestException;
import com.system.hakeem.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AmbulanceService {

    private static final Logger logger = LoggerFactory.getLogger(AmbulanceService.class);

    private final AmbulanceRepository ambulanceRepository;
    private final AmbulanceLocationRepository ambulanceLocationRepository;
    private final GeometryFactory geometryFactory;

    @Transactional
    public AmbulanceLocationDto updateAmbulanceLocation(AmbulanceLocationDto ambulanceLocationDto) {
        // Validate coordinates
        validateCoordinates(ambulanceLocationDto.getLatitude(), ambulanceLocationDto.getLongitude());

        Point point = geometryFactory
                .createPoint(new Coordinate(ambulanceLocationDto.getLongitude(), ambulanceLocationDto.getLatitude()));
        point.setSRID(4326);

        Ambulance ambulance = ambulanceRepository.findById(ambulanceLocationDto.getAmbulanceId())
                .orElseThrow(() -> new NotFoundException(
                        "Ambulance not found with id: " + ambulanceLocationDto.getAmbulanceId()));

        AmbulanceLocation location = ambulanceLocationRepository
                .findAmbulanceLocationByAmbulance_AmbulanceId(ambulanceLocationDto.getAmbulanceId());

        // Fix Issue 1: Check if location exists, create if missing
        if (location == null) {
            logger.warn("Location record not found for ambulanceId={}, creating new location record",
                    ambulanceLocationDto.getAmbulanceId());
            location = AmbulanceLocation.builder()
                    .ambulance(ambulance)
                    .build();
        }

        // Validate speed and direction ranges
        validateSpeedAndDirection(ambulanceLocationDto.getSpeed(), ambulanceLocationDto.getDirection());

        location.setLocation(point);
        location.setSpeed(ambulanceLocationDto.getSpeed());
        location.setDirection(ambulanceLocationDto.getDirection());
        location.setRecordedAt(LocalDateTime.now()); // Fix Issue 3: Update timestamp
        ambulanceLocationRepository.save(location);

        logger.debug("Updated ambulance location: ambulanceId={}, speed={}, direction={}",
                ambulanceLocationDto.getAmbulanceId(), location.getSpeed(), location.getDirection());
        return ambulanceLocationDto;
    }

    public Ambulance findClosestAvailableAmbulance(double latitude, double longitude) {
        // Validate coordinates
        validateCoordinates(latitude, longitude);

        Point userPoint = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        userPoint.setSRID(4326);

        // getX() returns longitude, getY() returns latitude
        return ambulanceRepository.findClosestAvailableAmbulance(userPoint.getX(), userPoint.getY());

    }

    public Optional<Ambulance> getAmbulanceById(int id) {
        return ambulanceRepository.findById(id);
    }

    /**
     * Updates the status of an ambulance
     * 
     * @param ambulanceId The ID of the ambulance
     * @param status      The new status to set
     * @return The updated ambulance
     * @throws NotFoundException if ambulance is not found
     */
    @Transactional
    public Ambulance updateAmbulanceStatus(int ambulanceId, AmbulanceStatus status) {
        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new NotFoundException("Ambulance not found with id: " + ambulanceId));

        AmbulanceStatus oldStatus = ambulance.getStatus();
        ambulance.setStatus(status);
        ambulanceRepository.save(ambulance);

        logger.info("Ambulance status updated: ambulanceId={}, oldStatus={}, newStatus={}",
                ambulanceId, oldStatus, status);

        return ambulance;
    }

    /**
     * Gets ambulance assigned to the currently authenticated paramedic
     * 
     * @return The ambulance assigned to the paramedic
     * @throws NotFoundException if paramedic has no assigned ambulance
     */
    public Ambulance getMyAmbulance() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User paramedic = (User) auth.getPrincipal();

        return ambulanceRepository.findByParamedic(paramedic)
                .orElseThrow(() -> new NotFoundException("No ambulance assigned to paramedic"));
    }

    public Ambulance getAmbulanceByPlateNumber(String plateNumber) {
        Optional<Ambulance> ambulanceOptional = ambulanceRepository.findAmbulanceByPlateNumber(plateNumber);
        if (ambulanceOptional.isEmpty()) {
            throw new NotFoundException("No Ambulance found with plate number " + plateNumber);
        }
        return ambulanceOptional.get();
    }

    @Transactional
    public CreateAmbulanceResponse createAmbulance(CreateAmbulanceRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User paramedic = (User) auth.getPrincipal();

        // Validate coordinates
        validateCoordinates(request.getLatitude(), request.getLongitude());

        // Validate plate number
        if (request.getPlateNumber() == null || request.getPlateNumber().trim().isEmpty()) {
            throw new BadRequestException("Plate number is required");
        }

        // Check if paramedic already has an ambulance
        Optional<Ambulance> existingAmbulance = ambulanceRepository.findByParamedic(paramedic);
        if (existingAmbulance.isPresent()) {
            throw new BadRequestException("Paramedic already has an assigned ambulance");
        }

        // Check if plate number already exists
        Optional<Ambulance> existingPlate = ambulanceRepository.findAmbulanceByPlateNumber(request.getPlateNumber());
        if (existingPlate.isPresent()) {
            throw new BadRequestException(
                    "Ambulance with plate number " + request.getPlateNumber() + " already exists");
        }

        Ambulance ambulance = Ambulance.builder()
                .status(AmbulanceStatus.AVAILABLE)
                .plateNumber(request.getPlateNumber())
                .paramedic(paramedic)
                .build();

        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);

        AmbulanceLocation ambulanceLocation = AmbulanceLocation.builder()
                .ambulance(ambulance)
                .direction(0.0)
                .speed(0.0)
                .recordedAt(LocalDateTime.now())
                .location(point)
                .build();

        ambulanceRepository.save(ambulance);
        ambulanceLocationRepository.save(ambulanceLocation);

        return CreateAmbulanceResponse.builder()
                .ambulance_id(ambulance.getAmbulanceId())
                .paramedicId(ambulance.getParamedic().getId())
                .ambulanceStatus(ambulance.getStatus())
                .plateNumber(ambulance.getPlateNumber())
                .paramedicName(ambulance.getParamedic().getName())
                .build();
    }

    /**
     * Validates GPS coordinates are within valid ranges
     * 
     * @param latitude  Latitude coordinate (-90 to 90)
     * @param longitude Longitude coordinate (-180 to 180)
     * @throws IllegalArgumentException if coordinates are invalid
     */
    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                    String.format("Latitude must be between -90 and 90. Received: %f", latitude));
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                    String.format("Longitude must be between -180 and 180. Received: %f", longitude));
        }
    }

    /**
     * Validates speed and direction values are within reasonable ranges
     * 
     * @param speed     Speed in km/h (0 to 300)
     * @param direction Direction in degrees (0 to 360)
     * @throws IllegalArgumentException if values are invalid
     */
    private void validateSpeedAndDirection(double speed, double direction) {
        if (speed < 0 || speed > 300) {
            throw new IllegalArgumentException(
                    String.format("Speed must be between 0 and 300 km/h. Received: %f", speed));
        }
        if (direction < 0 || direction > 360) {
            throw new IllegalArgumentException(
                    String.format("Direction must be between 0 and 360 degrees. Received: %f", direction));
        }
    }

}
