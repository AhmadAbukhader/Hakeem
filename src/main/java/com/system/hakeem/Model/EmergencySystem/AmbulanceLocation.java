package com.system.hakeem.Model.EmergencySystem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ambulance_location", schema = "hakeem_schema")
public class AmbulanceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int locationId;

    @ManyToOne
    @JoinColumn(name = "ambulance_id", nullable = false)
    private Ambulance ambulance;

    @Column(nullable = false, columnDefinition = "geography(Point,4326)")
    private Point location;

    private Double speed;

    private Double direction;

    private LocalDateTime recordedAt = LocalDateTime.now();

}

