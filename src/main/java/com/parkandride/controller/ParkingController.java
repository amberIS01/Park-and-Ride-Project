package com.parkandride.controller;

import com.parkandride.model.ParkingSpot;
import com.parkandride.model.Reservation;
import com.parkandride.model.Reservation.ReservationType;
import com.parkandride.model.User;
import com.parkandride.service.ParkingService;
import com.parkandride.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/parking")
public class ParkingController {
    
    private final ParkingService parkingService;
    private final UserService userService;
    
    @Autowired
    public ParkingController(ParkingService parkingService, UserService userService) {
        this.parkingService = parkingService;
        this.userService = userService;
    }
    
    @GetMapping("/spots")
    public String listAvailableSpots(@RequestParam String location, Model model) {
        List<ParkingSpot> availableSpots = parkingService.getAvailableSpots(location);
        model.addAttribute("spots", availableSpots);
        model.addAttribute("location", location);
        return "parking/spots";
    }
    
    @GetMapping("/reserve/{spotId}")
    public String showReservationForm(@PathVariable String spotId, Model model) {
        Optional<ParkingSpot> spotOpt = parkingService.getParkingSpotById(spotId);
        if (spotOpt.isEmpty() || spotOpt.get().isOccupied()) {
            return "redirect:/dashboard?error=Spot not available";
        }
        
        model.addAttribute("spot", spotOpt.get());
        model.addAttribute("reservationTypes", ReservationType.values());
        return "parking/reserve";
    }
    
    @PostMapping("/reserve")
    public String createReservation(
            @RequestParam String spotId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endTime,
            @RequestParam ReservationType type,
            RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        try {
            // Get current time in IST (India/Mumbai timezone) - UTC+5:30
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentISTTime = now.plusHours(5).plusMinutes(30);
            
            // Add a small buffer (5 minutes) to account for form submission time
            LocalDateTime bufferedTime = currentISTTime.minusMinutes(5);
            
            if (startTime.isBefore(bufferedTime)) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Cannot book a reservation in the past. Please select a future time.");
                return "redirect:/parking/reserve/" + spotId;
            }
            
            if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "End time must be after start time.");
                return "redirect:/parking/reserve/" + spotId;
            }
            
            Reservation reservation = parkingService.createReservation(user, spotId, startTime, endTime, type);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Reservation created successfully! Your reservation ID is: " + reservation.getReservationId());
            return "redirect:/dashboard";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/parking/reserve/" + spotId;
        }
    }
    
    @GetMapping("/my-reservations")
    public String viewMyReservations(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        // Get all user reservations
        List<Reservation> userReservations = parkingService.getUserReservations(user);
        
        // Sort the reservations by start time (most recent first)
        userReservations.sort((r1, r2) -> r2.getStartTime().compareTo(r1.getStartTime()));
        
        model.addAttribute("reservations", userReservations);
        model.addAttribute("user", user);
        return "parking/my-reservations";
    }
    
    @PostMapping("/cancel/{reservationId}")
    public String cancelReservation(@PathVariable String reservationId, RedirectAttributes redirectAttributes) {
        try {
            boolean cancelled = parkingService.cancelReservation(reservationId);
            if (cancelled) {
                Optional<Reservation> reservation = parkingService.getReservation(reservationId);
                if (reservation.isPresent()) {
                    redirectAttributes.addFlashAttribute("successMessage", 
                            "Reservation cancelled successfully! Refund amount: $" + 
                            String.format("%.2f", reservation.get().getRefundAmount()));
                } else {
                    redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled successfully!");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel reservation.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }
        return "redirect:/parking/my-reservations";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        // Add available locations to the model
        model.addAttribute("locations", parkingService.getAllParkingSpots()
                .stream()
                .map(spot -> spot.getLocation())
                .distinct()
                .toArray());
        
        // Get user's reservations
        List<Reservation> userReservations = parkingService.getUserReservations(user);
        
        // Get recent reservations (all, including cancelled) ordered by start time descending
        List<Reservation> recentReservations = userReservations.stream()
                .sorted((r1, r2) -> r2.getStartTime().compareTo(r1.getStartTime()))
                .limit(5) // Show only 5 most recent
                .collect(Collectors.toList());
        
        // Count total reservations (all statuses)
        int totalReservations = userReservations.size();
        
        // Calculate total spent
        double totalSpent = userReservations.stream()
                .filter(r -> r.getStatus() != Reservation.ReservationStatus.CANCELLED)
                .mapToDouble(Reservation::getTotalCost)
                .sum();
        
        model.addAttribute("reservations", recentReservations);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("user", user);
        model.addAttribute("totalSpent", totalSpent);
        
        return "dashboard";
    }
} 
