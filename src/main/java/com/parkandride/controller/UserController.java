package com.parkandride.controller;

import com.parkandride.model.User;
import com.parkandride.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        model.addAttribute("user", user);
        return "user/profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User existingUser = userService.findByUsername(auth.getName()).orElseThrow(() -> new IllegalStateException("User not found"));
        
        // Update only allowed fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setVehicleNumber(user.getVehicleNumber());
        
        userService.updateUser(existingUser);
        
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/user/profile";
    }
    
    @PostMapping("/convert-points")
    public String convertPointsToCredit(@RequestParam("points") int points, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (points <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter a valid number of points to convert.");
            return "redirect:/user/profile";
        }
        
        if (points > user.getRewardPoints()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You don't have enough points to convert. You have " + user.getRewardPoints() + " points.");
            return "redirect:/user/profile";
        }
        
        double creditAmount = userService.convertPointsToCredit(user, points);
        
        redirectAttributes.addFlashAttribute("successMessage", 
                "Successfully converted " + points + " points to $" + String.format("%.2f", creditAmount) + " credit!");
        
        return "redirect:/user/profile";
    }
    
    @GetMapping("/subscriptions")
    public String viewSubscriptions(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        model.addAttribute("user", user);
        return "user/subscriptions";
    }
    
    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("plan") String subscriptionType, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        try {
            userService.addSubscription(user, subscriptionType);
            redirectAttributes.addFlashAttribute("successMessage", "Subscription activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to activate subscription: " + e.getMessage());
        }
        
        return "redirect:/user/subscriptions";
    }
    
    @GetMapping("/rewards")
    public String viewRewards(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        model.addAttribute("user", user);
        return "user/rewards";
    }
    
    @PostMapping("/redeem-reward")
    public String redeemReward(@RequestParam("rewardType") String rewardType, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        try {
            boolean success = userService.redeemReward(user, rewardType);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Reward redeemed successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Insufficient points to redeem this reward.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to redeem reward: " + e.getMessage());
        }
        
        return "redirect:/user/rewards";
    }
} 