package com.system.hakeem.Service.EmergancySystem;

import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceRequest;
import com.system.hakeem.Dto.EmergencySystem.CreateAmbulanceResponse;
import com.system.hakeem.Dto.EmergencySystem.LocationDto.AmbulanceLocationDto;
import com.system.hakeem.Model.EmergencySystem.Ambulance;
import com.system.hakeem.Model.EmergencySystem.AmbulanceLocation;
import com.system.hakeem.Model.EmergencySystem.AmbulanceStatus;
import com.system.hakeem.Model.EmergencySystem.AmbulanceUnit;
import com.system.hakeem.Model.UserManagement.User;
import com.system.hakeem.Repository.EmergancySystem.AmbulanceLocationRepository;
import com.system.hakeem.Repository.EmergancySystem.AmbulanceRepository;
import com.system.hakeem.Repository.EmergancySystem.AmbulanceUnitRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.apache.coyote.BadRequestException;
import org.locationtech.jts.geom.Coordinate;
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

    @Transactional
    public AmbulanceLocationDto updateAmbulanceLocation(AmbulanceLocationDto ambulanceLocationDto) {
        Point point = geometryFactory.createPoint(new Coordinate(ambulanceLocationDto.getLongitude(), ambulanceLocationDto.getLatitude()));
        point.setSRID(4326);

        Ambulance ambulance = ambulanceRepository.findById(ambulanceLocationDto.getAmbulanceId())
                .orElseThrow(() -> new RuntimeException("Ambulance not found"));

        AmbulanceLocation location = ambulanceLocationRepository.findAmbulanceLocationByAmbulance_AmbulanceId(ambulanceLocationDto.getAmbulanceId());

        location.setAmbulance(ambulance);
        location.setLocation(point);
        location.setSpeed(ambulanceLocationDto.getSpeed());
        location.setDirection(ambulanceLocationDto.getDirection());
        ambulanceLocationRepository.save(location);

        System.out.println("Updated DB: " + location.getSpeed() + ", " + location.getDirection());
        return ambulanceLocationDto;
    }

    public Ambulance findClosestAvailableAmbulance(double latitude, double longitude) {
        Point userPoint = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        userPoint.setSRID(4326);

        return ambulanceRepository.findClosestAvailableAmbulance(userPoint.getX(),userPoint.getY());

    }


    public Optional<Ambulance> getAmbulanceById(int id) {
        return ambulanceRepository.findById(id);
    }

    public Ambulance getAmbulanceByPlateNumber(String plateNumber) throws BadRequestException {
        Optional<Ambulance> ambulanceOptional = ambulanceRepository.findAmbulanceByPlateNumber(plateNumber);
        if (ambulanceOptional.isEmpty()){
            throw new BadRequestException("No Ambulance found with plate number " + plateNumber);
        }
        return ambulanceOptional.get();
    }

    public CreateAmbulanceResponse createAmbulance(CreateAmbulanceRequest request){
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
                .ambulanceUnitId(ambulance.getUnit().getUnitId())
                .build();
    }


}
