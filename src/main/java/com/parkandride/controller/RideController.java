package com.parkandride.controller;

import com.parkandride.model.User;
import com.parkandride.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rides")
public class RideController {

    private final UserService userService;

    @Autowired
    public RideController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/last-mile")
    public String lastMileRides(Model model) {
        // Add any needed model attributes
        return "rides/last-mile";
    }
    
    @GetMapping("/my-rides")
    public String myRides(Model model) {
        // This will just render the template, as the ride data is stored in localStorage
        return "rides/my-rides";
    }
} 