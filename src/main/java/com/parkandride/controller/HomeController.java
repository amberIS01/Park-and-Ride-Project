package com.parkandride.controller;

import com.parkandride.service.ParkingService;
import com.parkandride.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ParkingService parkingService;
    private final UserService userService;

    @Autowired
    public HomeController(ParkingService parkingService, UserService userService) {
        this.parkingService = parkingService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Initialize sample data
        parkingService.initializeSampleData();
        userService.initializeSampleData();
        
        // Add available locations to the model
        model.addAttribute("locations", parkingService.getAllParkingSpots()
                .stream()
                .map(spot -> spot.getLocation())
                .distinct()
                .toArray());
        
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/parking/dashboard";
    }
} 