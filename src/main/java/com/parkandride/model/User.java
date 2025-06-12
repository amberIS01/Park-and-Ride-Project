package com.parkandride.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotEmpty
    private String username;
    
    @Column(nullable = false)
    @NotEmpty
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(unique = true)
    @Email
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    
    @Column(name = "credit_balance")
    private double creditBalance = 0.0;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    private SubscriptionType subscriptionType = SubscriptionType.NONE;
    
    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;
    
    @Column(name = "subscription_expiry_date")
    private LocalDateTime subscriptionExpiryDate;
    
    @Column(name = "reward_points")
    private int rewardPoints = 0;
    
    @ElementCollection
    @CollectionTable(name = "user_reward_coupons", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "coupon_type")
    @Column(name = "count")
    private Map<String, Integer> rewardCoupons = new HashMap<>();
    
    // Subscription types
    public enum SubscriptionType {
        NONE("No Subscription", 0.0),
        BASIC("Basic", 9.99),
        STANDARD("Standard", 19.99),
        PREMIUM("Premium", 29.99),
        MONTHLY("Monthly Pass", 49.99),
        QUARTERLY("Quarterly Pass", 129.99),
        ANNUAL("Annual Pass", 399.99);
        
        private final String displayName;
        private final double price;
        
        SubscriptionType(String displayName, double price) {
            this.displayName = displayName;
            this.price = price;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public double getPrice() {
            return price;
        }
        
        public static SubscriptionType fromString(String value) {
            for (SubscriptionType type : SubscriptionType.values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown subscription type: " + value);
        }
    }
    
    // Constructors
    public User() {
    }
    
    public User(String username, String password, String firstName, String lastName, String email, String phoneNumber, String vehicleNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    
    public double getCreditBalance() {
        return creditBalance;
    }
    
    public void setCreditBalance(double creditBalance) {
        this.creditBalance = creditBalance;
    }
    
    public void addCredit(double amount) {
        this.creditBalance += amount;
    }
    
    public boolean useCredit(double amount) {
        if (this.creditBalance >= amount) {
            this.creditBalance -= amount;
            return true;
        }
        return false;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }
    
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    
    public LocalDateTime getSubscriptionStartDate() {
        return subscriptionStartDate;
    }
    
    public void setSubscriptionStartDate(LocalDateTime subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }
    
    public LocalDateTime getSubscriptionExpiryDate() {
        return subscriptionExpiryDate;
    }
    
    public void setSubscriptionExpiryDate(LocalDateTime subscriptionExpiryDate) {
        this.subscriptionExpiryDate = subscriptionExpiryDate;
    }
    
    public int getRewardPoints() {
        return rewardPoints;
    }
    
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
    
    public void addRewardPoints(int points) {
        this.rewardPoints += points;
    }
    
    public Map<String, Integer> getRewardCoupons() {
        return rewardCoupons;
    }
    
    public void setRewardCoupons(Map<String, Integer> rewardCoupons) {
        this.rewardCoupons = rewardCoupons;
    }
    
    public void addRewardCoupon(String couponType) {
        int currentCount = rewardCoupons.getOrDefault(couponType, 0);
        rewardCoupons.put(couponType, currentCount + 1);
    }
    
    public boolean useRewardCoupon(String couponType) {
        int currentCount = rewardCoupons.getOrDefault(couponType, 0);
        if (currentCount > 0) {
            rewardCoupons.put(couponType, currentCount - 1);
            return true;
        }
        return false;
    }
    
    public boolean hasActiveSubscription() {
        return subscriptionType != SubscriptionType.NONE && 
               (subscriptionExpiryDate == null || LocalDateTime.now().isBefore(subscriptionExpiryDate));
    }
    
    public double getSubscriptionDiscount() {
        if (!hasActiveSubscription()) {
            return 0.0;
        }
        
        switch (subscriptionType) {
            case BASIC:
                return 0.05; // 5% discount
            case STANDARD:
                return 0.10; // 10% discount
            case PREMIUM:
                return 0.15; // 15% discount
            case MONTHLY:
                return 0.10; // 10% discount
            case QUARTERLY:
                return 0.15; // 15% discount
            case ANNUAL:
                return 0.25; // 25% discount
            default:
                return 0.0;
        }
    }
    
    // Helper methods
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setUser(this);
    }
    
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setUser(null);
    }
    
    // Convert points to credit
    public double convertPointsToCredit(int points) {
        if (points <= 0 || points > this.rewardPoints) {
            return 0.0;
        }
        
        // Convert at a rate of 10 points = $1
        double creditAmount = points / 10.0;
        this.rewardPoints -= points;
        this.creditBalance += creditAmount;
        
        return creditAmount;
    }
} 