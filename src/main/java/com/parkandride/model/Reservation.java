package com.parkandride.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @Column(length = 36)
    private String reservationId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "spot_id", nullable = false)
    private ParkingSpot parkingSpot;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
    
    @Column(name = "total_cost", nullable = false)
    private double totalCost;
    
    @Column(name = "refund_amount")
    private double refundAmount;
    
    @Column(name = "checked_in")
    private boolean checkedIn = false;
    
    @Column(name = "qr_code")
    private String qrCode;
    
    @Column(name = "grace_period_end")
    private LocalDateTime gracePeriodEnd;
    
    @Transient
    private String userId; // Used for backward compatibility

    public enum ReservationType {
        HOURLY,
        DAILY,
        MONTHLY
    }

    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }
    
    // Default constructor for JPA
    public Reservation() {
    }

    // Constructor for backward compatibility
    public Reservation(String userId, String spotId, LocalDateTime startTime, LocalDateTime endTime, ReservationType type) {
        this.reservationId = UUID.randomUUID().toString();
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.status = ReservationStatus.PENDING;
        this.totalCost = 0.0; // Will be calculated when parking spot is set
    }
    
    // New constructor with User and ParkingSpot entities
    public Reservation(User user, ParkingSpot parkingSpot, LocalDateTime startTime, LocalDateTime endTime, ReservationType type) {
        this.reservationId = UUID.randomUUID().toString();
        this.user = user;
        this.parkingSpot = parkingSpot;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.status = ReservationStatus.PENDING;
        this.calculateTotalCost();
        this.qrCode = generateQRCode();
        this.gracePeriodEnd = startTime.plusMinutes(30); // 30 minutes grace period
    }
    
    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
        this.totalCost = calculateTotalCost();
    }

    public double calculateTotalCost() {
        if (parkingSpot == null) {
            return 0.0;
        }
        
        switch (type) {
            case HOURLY:
                long hours = Duration.between(startTime, endTime).toHours();
                // Ensure at least 1 hour is charged
                hours = Math.max(1, hours);
                return hours * parkingSpot.getHourlyRate();
                
            case DAILY:
                long days = Duration.between(startTime, endTime).toDays();
                // Ensure at least 1 day is charged
                days = Math.max(1, days);
                return days * parkingSpot.getDailyRate();
                
            case MONTHLY:
                // Calculate the number of months (approximate)
                long days30 = Duration.between(startTime, endTime).toDays();
                long months = (days30 / 30) + (days30 % 30 > 0 ? 1 : 0);
                // Ensure at least 1 month is charged
                months = Math.max(1, months);
                return months * parkingSpot.getMonthlyRate();
                
            default:
                return 0.0;
        }
    }

    private String generateQRCode() {
        // Generate a unique QR code based on reservation ID
        return "PR-" + this.reservationId.substring(0, 8).toUpperCase();
    }

    // Getters and Setters
    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // For backward compatibility
    public String getUserId() {
        return user != null ? user.getId().toString() : userId;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }
    
    // For backward compatibility
    public String getSpotId() {
        return parkingSpot != null ? parkingSpot.getSpotId() : null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ReservationType getType() {
        return type;
    }
    
    public void setType(ReservationType type) {
        this.type = type;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public boolean canBeCancelled() {
        return status == ReservationStatus.PENDING || 
               status == ReservationStatus.CONFIRMED;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public LocalDateTime getGracePeriodEnd() {
        return gracePeriodEnd;
    }

    public void setGracePeriodEnd(LocalDateTime gracePeriodEnd) {
        this.gracePeriodEnd = gracePeriodEnd;
    }

    public boolean isGracePeriodExpired() {
        return LocalDateTime.now().isAfter(gracePeriodEnd);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", userId='" + getUserId() + '\'' +
                ", spotId='" + getSpotId() + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", type=" + type +
                ", status=" + status +
                ", totalCost=" + totalCost +
                ", refundAmount=" + refundAmount +
                ", checkedIn=" + checkedIn +
                ", qrCode='" + qrCode + '\'' +
                ", gracePeriodEnd=" + gracePeriodEnd +
                '}';
    }
} 