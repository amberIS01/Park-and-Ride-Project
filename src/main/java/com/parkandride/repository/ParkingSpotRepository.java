package com.parkandride.repository;

import com.parkandride.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, String> {
    List<ParkingSpot> findByLocationAndIsOccupied(String location, boolean isOccupied);
    List<ParkingSpot> findByLocation(String location);
} 