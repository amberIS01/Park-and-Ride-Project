# Screenshot Guide for Park & Ride Documentation

This guide outlines the recommended screenshots to capture for the Park & Ride documentation and README. High-quality screenshots help users understand the application's features and functionality.

## Recommended Screenshots

### 1. Dashboard (dashboard.html)
- **Full Dashboard View**: Capture the entire dashboard showing all cards (Total Reservations, My Rewards, Last-Mile Rides, My Rides)
- **Dark Mode Dashboard**: The same dashboard view but with dark mode enabled

### 2. Parking Reservation (parking/reserve.html)
- **Reservation Form**: Show the complete reservation form with the date/time picker visible
- **Location Selection**: Capture the location dropdown expanded to show available locations
- **Confirmation Screen**: Show the confirmation message after successful booking

### 3. My Reservations (parking/my-reservations.html)
- **Reservations List**: Show a list of active reservations with their details
- **Cancellation Confirmation**: Display the cancellation confirmation dialog
- **Empty State**: If possible, show the empty state when no reservations exist

### 4. Last-Mile Rides (rides/last-mile.html)
- **Main Interface**: Capture the full Last-Mile Rides interface showing the map and destination options
- **Popular Destinations**: Show the popular destinations cards
- **Ride Type Selection**: Display the ride type options (Cab, Shuttle, E-Rickshaw)
- **Cab Sharing Options**: Show the cab sharing interface with both options
- **Booking Summary**: Capture the booking summary section with all details filled in
- **Ride Confirmation**: Show the booking confirmation modal

### 5. My Rides (rides/my-rides.html)
- **Rides History**: Display the complete rides history with different status types
- **Ride Details**: Show an expanded view of a single ride with all details
- **Filter Options**: Capture the filter dropdown in action

## Screenshot Requirements

- **Resolution**: Minimum 1280x720px, preferably 1920x1080px
- **Format**: PNG or JPG
- **Quality**: High quality, clear text, no artifacts
- **Browser**: Take screenshots in Chrome or Edge for consistency
- **Content**: Make sure to include realistic data in the screenshots
- **State**: Ensure the UI is in a clean state with no debug information visible

## Naming Convention

Use the following naming convention for screenshot files:

```
[section]_[feature]_[state].[extension]
```

Examples:
- `dashboard_main_light.png`
- `dashboard_main_dark.png`
- `parking_reservation_form.png`
- `lastmile_destinations.png`
- `myrides_history.png`

## File Location

Place all screenshots in the `/docs/images/` directory. Reference them in the README.md using relative paths:

```markdown
![Dashboard Overview](docs/images/dashboard_main_light.png)
```

## Final Checklist

Before submitting screenshots:

- [ ] All screenshots are high resolution and clear
- [ ] Personal information is removed or anonymized
- [ ] Dark mode and light mode variants are included where relevant
- [ ] All major features are represented
- [ ] File names follow the naming convention
- [ ] Images are optimized for web viewing (compressed without quality loss)

Thank you for helping document the Park & Ride application! 