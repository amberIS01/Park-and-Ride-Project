package com.parkandride.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_spots")
public class ParkingSpot {
    @Id
    private String spotId;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private boolean isOccupied;
    
    @Column(name = "current_reservation_id")
    private String currentReservationId;
    
    @Column(name = "hourly_rate", nullable = false)
    private double hourlyRate;
    
    @Column(name = "daily_rate", nullable = false)
    private double dailyRate;
    
    @Column(name = "monthly_rate", nullable = false)
    private double monthlyRate;
    
    @OneToMany(mappedBy = "parkingSpot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
    
    // Default constructor for JPA
    public ParkingSpot() {
    }

    public ParkingSpot(String spotId, String location, double hourlyRate, double dailyRate, double monthlyRate) {
        this.spotId = spotId;
        this.location = location;
        this.isOccupied = false;
        this.currentReservationId = null;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
        this.monthlyRate = monthlyRate;
    }

    // Getters and Setters
    public String getSpotId() {
        return spotId;
    }
    
    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public String getCurrentReservationId() {
        return currentReservationId;
    }

    public void setCurrentReservationId(String currentReservationId) {
        this.currentReservationId = currentReservationId;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getDailyRate() {
        return dailyRate;
    }
    
    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }
    
    public void setMonthlyRate(double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    // Helper methods
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setParkingSpot(this);
    }
    
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "spotId='" + spotId + '\'' +
                ", location='" + location + '\'' +
                ", isOccupied=" + isOccupied +
                ", currentReservationId='" + currentReservationId + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", dailyRate=" + dailyRate +
                ", monthlyRate=" + monthlyRate +
                '}';
    }
} 