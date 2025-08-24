package com.system.hakeem.Service.EmergancySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceRequest;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Model.EmergencySystem.AmbulanceLocation;
import com.system.hakeem.Model.EmergencySystem.AmbulanceStatus;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.EmergancySystem.AmbulanceLocationRepository;
import com.system.hakeem.Repository.EmergancySystem.AmbulanceRepository;
import com.system.hakeem.Repository.EmergancySystem.AmbulanceUnitRepository;
import lombok.Builder;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Builder
@Service
public class AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final AmbulanceLocationRepository ambulanceLocationRepository;
    private final AmbulanceUnitRepository ambulanceUnitRepository;
    private final GeometryFactory geometryFactory;

    public AmbulanceLocation updateAmbulanceLocation(int ambulanceId, double latitude, double longitude,
                                                     double speed, double direction) {
        Point point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
        point.setSRID(4326);

        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new RuntimeException("Ambulance not found"));

        AmbulanceLocation location = new AmbulanceLocation();
        location.setAmbulance(ambulance);
        location.setLocation(point);
        location.setSpeed(speed);
        location.setDirection(direction);

        return ambulanceLocationRepository.save(location);
    }

    public Ambulance findClosestAvailableAmbulance(double latitude, double longitude) {
        Point userPoint = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
        userPoint.setSRID(4326);

        return ambulanceRepository.findClosestAvailableAmbulance(userPoint.getX(),userPoint.getY());

    }


    public Optional<Ambulance> getAmbulanceById(int id) {
        return ambulanceRepository.findById(id);
    }

    public Ambulance createAmbulance(CreateAmbulanceRequest request){
        Optional<AmbulanceUnit> unitOptional = ambulanceUnitRepository.findById(request.getUnitId());
        if(unitOptional.isEmpty()){
            throw new RuntimeException("No Such Unit");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User paramedic = (User) auth.getPrincipal();

        AmbulanceUnit unit = unitOptional.get();
        Ambulance ambulance =  Ambulance.builder()
                                        .status(AmbulanceStatus.AVAILABLE)
                                        .unit(unit)
                                        .plateNumber(request.getPlateNumber())
                                        .paramedic(paramedic)
                                        .build();

        Point point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);

        AmbulanceLocation ambulanceLocation = AmbulanceLocation.builder()
                .ambulance(ambulance)
                .direction(0.0)
                .speed(0.0)
                .recordedAt(LocalDateTime.now())
                .location(point)
                .build();

        ambulanceLocationRepository.save(ambulanceLocation);
        ambulanceRepository.save(ambulance);
        return ambulance;
    }


}
