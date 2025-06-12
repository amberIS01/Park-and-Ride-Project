package com.parkandride.config;

import com.parkandride.model.ParkingSpot;
import com.parkandride.model.User;
import com.parkandride.repository.ParkingSpotRepository;
import com.parkandride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;
    
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        // Add test users if none exist
        if (userRepository.count() == 0) {
            System.out.println("Initializing sample users...");
            
            User adminUser = new User(
                "admin",
                passwordEncoder.encode("admin"),
                "Admin",
                "User",
                "admin@parkandride.com",
                "1234567890",
                "HR-01-XY-7890"
            );
            
            User testUser = new User(
                "testuser",
                passwordEncoder.encode("password"),
                "Test",
                "User",
                "test@parkandride.com",
                "9876543210",
                "UP-07-ZW-3456"
            );
            
            userRepository.save(adminUser);
            userRepository.save(testUser);
            
            System.out.println("Created user: admin/admin");
            System.out.println("Created user: testuser/password");
        }
        
        // Add sample parking spots if none exist
        if (parkingSpotRepository.count() == 0) {
            System.out.println("Initializing sample parking spots...");
            
            ParkingSpot spot1 = new ParkingSpot("A001", "Metro Station North", 5.0, 25.0, 400.0);
            ParkingSpot spot2 = new ParkingSpot("A002", "Metro Station North", 5.0, 25.0, 400.0);
            ParkingSpot spot3 = new ParkingSpot("B001", "Metro Station South", 4.0, 20.0, 350.0);
            ParkingSpot spot4 = new ParkingSpot("B002", "Metro Station South", 4.0, 20.0, 350.0);
            ParkingSpot spot5 = new ParkingSpot("C001", "Downtown Garage", 6.0, 30.0, 500.0);
            
            parkingSpotRepository.save(spot1);
            parkingSpotRepository.save(spot2);
            parkingSpotRepository.save(spot3);
            parkingSpotRepository.save(spot4);
            parkingSpotRepository.save(spot5);
            
            System.out.println("Created 5 sample parking spots");
        }
    }
} 