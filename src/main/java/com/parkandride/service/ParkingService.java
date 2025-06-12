package com.parkandride.service;

import com.parkandride.model.ParkingSpot;
import com.parkandride.model.Reservation;
import com.parkandride.model.Reservation.ReservationStatus;
import com.parkandride.model.Reservation.ReservationType;
import com.parkandride.model.User;
import com.parkandride.repository.ParkingSpotRepository;
import com.parkandride.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {
    
    private final ParkingSpotRepository parkingSpotRepository;
    private final ReservationRepository reservationRepository;
    
    @Autowired
    public ParkingService(ParkingSpotRepository parkingSpotRepository, ReservationRepository reservationRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void addParkingSpot(ParkingSpot spot) {
        parkingSpotRepository.save(spot);
    }
    
    @Transactional(readOnly = true)
    public List<ParkingSpot> getAllParkingSpots() {
        return parkingSpotRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<ParkingSpot> getAvailableSpots(String location) {
        return parkingSpotRepository.findByLocationAndIsOccupied(location, false);
    }
    
    @Transactional(readOnly = true)
    public Optional<ParkingSpot> getParkingSpotById(String spotId) {
        return parkingSpotRepository.findById(spotId);
    }

    @Transactional
    public Reservation createReservation(User user, String spotId, LocalDateTime startTime, 
                                       LocalDateTime endTime, ReservationType type) {
        Optional<ParkingSpot> spotOpt = parkingSpotRepository.findById(spotId);
        if (spotOpt.isEmpty() || spotOpt.get().isOccupied()) {
            throw new IllegalStateException("Parking spot is not available");
        }
        
        ParkingSpot spot = spotOpt.get();
        Reservation reservation = new Reservation(user, spot, startTime, endTime, type);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        
        // Ensure total cost is calculated
        double totalCost = reservation.calculateTotalCost();
        
        // Apply subscription discount if applicable
        if (user.hasActiveSubscription()) {
            double discount = user.getSubscriptionDiscount();
            totalCost = totalCost * (1 - discount);
        }
        
        reservation.setTotalCost(totalCost);
        
        // Add reward points (1 point per dollar spent)
        int pointsEarned = (int) Math.floor(totalCost);
        user.addRewardPoints(pointsEarned);
        
        spot.setOccupied(true);
        spot.setCurrentReservationId(reservation.getReservationId());
        
        parkingSpotRepository.save(spot);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public boolean cancelReservation(String reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty() || !reservationOpt.get().canBeCancelled()) {
            return false;
        }
        
        Reservation reservation = reservationOpt.get();
        ParkingSpot spot = reservation.getParkingSpot();
        
        spot.setOccupied(false);
        spot.setCurrentReservationId(null);
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        
        // Calculate and set refund amount
        double refundAmount = calculateRefundAmount(reservationId);
        reservation.setRefundAmount(refundAmount);
        
        parkingSpotRepository.save(spot);
        reservationRepository.save(reservation);
        
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<Reservation> getReservation(String reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getUserReservations(User user) {
        return reservationRepository.findByUser(user);
    }

    @Transactional
    public void completeReservation(String reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            ParkingSpot spot = reservation.getParkingSpot();
            
            spot.setOccupied(false);
            spot.setCurrentReservationId(null);
            
            reservation.setStatus(ReservationStatus.COMPLETED);
            
            parkingSpotRepository.save(spot);
            reservationRepository.save(reservation);
        }
    }

    @Transactional
    public double calculateRefundAmount(String reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty() || !reservationOpt.get().canBeCancelled()) {
            return 0.0;
        }

        Reservation reservation = reservationOpt.get();
        // Return full refund for all cancellations regardless of timing
        return reservation.getTotalCost();
    }
    
    @Transactional
    public void initializeSampleData() {
        // Only add sample data if repository is empty
        if (parkingSpotRepository.count() == 0) {
            // Add some sample parking spots
            addParkingSpot(new ParkingSpot("SPOT1", "Metro Station A", 5.0, 40.0, 300.0));
            addParkingSpot(new ParkingSpot("SPOT2", "Metro Station A", 5.0, 40.0, 300.0));
            addParkingSpot(new ParkingSpot("SPOT3", "Metro Station B", 5.0, 40.0, 300.0));
            addParkingSpot(new ParkingSpot("SPOT4", "Metro Station B", 6.0, 45.0, 330.0));
            addParkingSpot(new ParkingSpot("SPOT5", "Metro Station C", 7.0, 50.0, 350.0));
            addParkingSpot(new ParkingSpot("SPOT6", "Metro Station C", 7.0, 50.0, 350.0));
        }
    }

    @Transactional
    public void releaseExpiredReservation(String reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            return;
        }
        
        Reservation reservation = reservationOpt.get();
        ParkingSpot spot = reservation.getParkingSpot();
        
        // Update reservation status
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        // Release the parking spot
        spot.setOccupied(false);
        spot.setCurrentReservationId(null);
        parkingSpotRepository.save(spot);
        
        System.out.println("Released expired reservation: " + reservationId);
    }

    @Transactional
    public boolean checkInReservation(String reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty() || reservationOpt.get().getStatus() != ReservationStatus.CONFIRMED) {
            return false;
        }
        
        Reservation reservation = reservationOpt.get();
        reservation.setCheckedIn(true);
        reservationRepository.save(reservation);
        
        return true;
    }
} 