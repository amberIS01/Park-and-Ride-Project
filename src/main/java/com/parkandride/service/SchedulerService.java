package com.parkandride.service;

import com.parkandride.model.Reservation;
import com.parkandride.model.Reservation.ReservationStatus;
import com.parkandride.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulerService {

    private final ReservationRepository reservationRepository;
    private final ParkingService parkingService;

    @Autowired
    public SchedulerService(ReservationRepository reservationRepository, ParkingService parkingService) {
        this.reservationRepository = reservationRepository;
        this.parkingService = parkingService;
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    @Transactional
    public void checkGracePeriodExpirations() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find all active reservations where the grace period has expired and user hasn't checked in
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndCheckedInAndGracePeriodEndBefore(
            ReservationStatus.CONFIRMED, false, now);
        
        if (!expiredReservations.isEmpty()) {
            System.out.println("Found " + expiredReservations.size() + " expired grace period reservations");
            
            for (Reservation reservation : expiredReservations) {
                // Release the parking spot
                parkingService.releaseExpiredReservation(reservation.getReservationId());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    @Transactional
    public void checkExpiredSubscriptions() {
        // This would check for and update expired subscriptions
        // Implementation details would depend on your subscription model
    }
} 