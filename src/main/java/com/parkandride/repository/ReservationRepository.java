package com.parkandride.repository;

import com.parkandride.model.Reservation;
import com.parkandride.model.Reservation.ReservationStatus;
import com.parkandride.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByParkingSpot_SpotId(String spotId);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.checkedIn = :isCheckedIn AND r.gracePeriodEnd < :dateTime")
    List<Reservation> findByStatusAndCheckedInAndGracePeriodEndBefore(
        @Param("status") ReservationStatus status, 
        @Param("isCheckedIn") boolean isCheckedIn, 
        @Param("dateTime") LocalDateTime dateTime);
} 