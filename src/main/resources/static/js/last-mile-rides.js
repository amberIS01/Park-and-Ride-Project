// Global variables
let selectedDestination = '';
let selectedRideType = '';
let selectedPaymentMethod = '';
let selectedSharingOption = '';
let appliedPromoCode = '';
let baseFare = 0;
let isLiveLocation = false;
let cabFare = 0;
let shuttleFare = 0;
let eRickshawFare = 0;

document.addEventListener('DOMContentLoaded', function() {
    // Initialize use current location button
    document.getElementById('useCurrentLocation').addEventListener('click', function() {
        detectCurrentLocation();
    });
    
    // Initialize with live location by default
    detectCurrentLocation();
    
    // Handle destination input changes
    document.getElementById('destination').addEventListener('input', function() {
        document.getElementById('summaryDestination').textContent = this.value || 'Not selected';
        selectedDestination = this.value;
        updateBookingSummary();
    });

    // Set up passenger count handler
    document.getElementById('passengerCount').addEventListener('change', updateBookingSummary);
    
    // Handle schedule radio buttons
    const scheduleNow = document.getElementById('scheduleNow');
    const scheduleLater = document.getElementById('scheduleLater');
    const scheduleDateTimeContainer = document.getElementById('scheduleDateTimeContainer');
    const scheduleDateTime = document.getElementById('scheduleDateTime');
    
    // Set default schedule time to current time + 1 hour
    const now = new Date();
    now.setHours(now.getHours() + 1);
    scheduleDateTime.value = now.toISOString().slice(0, 16);
    
    scheduleNow.addEventListener('change', function() {
        if (this.checked) {
            scheduleDateTimeContainer.classList.add('d-none');
            document.getElementById('summarySchedule').textContent = 'Now';
            updateBookingSummary();
        }
    });
    
    scheduleLater.addEventListener('change', function() {
        if (this.checked) {
            scheduleDateTimeContainer.classList.remove('d-none');
            updateScheduleSummary();
        }
    });
    
    scheduleDateTime.addEventListener('change', updateScheduleSummary);
});

// Detect current location
function detectCurrentLocation() {
    // In a real app, this would use the Geolocation API
    isLiveLocation = true;
    document.getElementById('pickupLocation').value = 'Current Location (Live)';
    document.getElementById('summaryPickup').textContent = 'Current Location (Live)';
    
    // Add visual indicator that live location is being used
    document.getElementById('pickupLocation').classList.add('border-success');
    document.getElementById('useCurrentLocation').classList.add('btn-success');
    document.getElementById('useCurrentLocation').classList.remove('btn-outline-secondary');
    updateBookingSummary();
}

// Update schedule summary
function updateScheduleSummary() {
    const dateTime = new Date(document.getElementById('scheduleDateTime').value);
    const formattedDateTime = dateTime.toLocaleString('en-IN', {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
    document.getElementById('summarySchedule').textContent = formattedDateTime;
    updateBookingSummary();
}

// Destination selection from cards
function selectDestination(destination) {
    selectedDestination = destination;
    
    // Highlight the selected destination card
    document.querySelectorAll('.destination-card').forEach(card => {
        card.classList.remove('border-primary');
        if (card.querySelector('.card-title').textContent === destination) {
            card.classList.add('border-primary');
        }
    });
    
    // Set the destination in the input field
    document.getElementById('destination').value = destination;
    document.getElementById('summaryDestination').textContent = destination;
    
    // Set specific fares for each ride type based on destination
    const destinationFares = {
        'Mumbai Central Station': { cab: 120, shuttle: 80, eRickshaw: 50 },
        'Bandra Kurla Complex': { cab: 200, shuttle: 120, eRickshaw: 80 },
        'Juhu Beach': { cab: 250, shuttle: 150, eRickshaw: 100 },
        'Powai Lake': { cab: 300, shuttle: 180, eRickshaw: 120 },
        'Lower Parel': { cab: 180, shuttle: 110, eRickshaw: 70 },
        'Gateway of India': { cab: 350, shuttle: 200, eRickshaw: 150 }
    };
    
    // Set fares based on selected destination
    if (destination in destinationFares) {
        const fares = destinationFares[destination];
        cabFare = fares.cab;
        shuttleFare = fares.shuttle;
        eRickshawFare = fares.eRickshaw;
        
        // Update ride card prices with the specific fares
        document.querySelectorAll('.ride-card').forEach(card => {
            const rideType = card.querySelector('.card-title').textContent.toLowerCase();
            if (rideType === 'cab') {
                card.querySelector('.card-text.fw-bold').textContent = '₹' + cabFare;
            } else if (rideType === 'shuttle') {
                card.querySelector('.card-text.fw-bold').textContent = '₹' + shuttleFare;
            } else if (rideType === 'e-rickshaw') {
                card.querySelector('.card-text.fw-bold').textContent = '₹' + eRickshawFare;
            }
        });
        
        // Set default base fare to cab fare initially
        baseFare = cabFare;
    } else {
        // Default if destination not in our list
        cabFare = 200;
        shuttleFare = 120;
        eRickshawFare = 80;
        baseFare = cabFare;
    }
    
    updateBookingSummary();
    
    // Scroll to booking form
    document.querySelector('#rideBookingForm').scrollIntoView({ 
        behavior: 'smooth' 
    });
}

// Ride type selection
function selectRideType(rideType) {
    selectedRideType = rideType;
    
    // Remove active class from all ride cards
    document.querySelectorAll('.ride-card').forEach(card => {
        card.classList.remove('border-primary');
    });
    
    // Add active class to selected card
    event.currentTarget.classList.add('border-primary');
    
    let rideTypeName = '';
    
    switch(rideType) {
        case 'cab':
            rideTypeName = 'Cab';
            baseFare = cabFare;
            // Show cab sharing options
            document.getElementById('cabSharingOptions').classList.remove('d-none');
            break;
        case 'shuttle':
            rideTypeName = 'Shuttle';
            baseFare = shuttleFare;
            // Hide cab sharing options
            document.getElementById('cabSharingOptions').classList.add('d-none');
            // Reset sharing option
            selectedSharingOption = '';
            document.querySelectorAll('.sharing-option').forEach(option => {
                option.classList.remove('border-primary');
            });
            break;
        case 'erickshaw':
            rideTypeName = 'E-Rickshaw';
            baseFare = eRickshawFare;
            // Hide cab sharing options
            document.getElementById('cabSharingOptions').classList.add('d-none');
            // Reset sharing option
            selectedSharingOption = '';
            document.querySelectorAll('.sharing-option').forEach(option => {
                option.classList.remove('border-primary');
            });
            break;
    }
    
    document.getElementById('summaryRideType').textContent = rideTypeName;
    updateBookingSummary();
}

// Cab sharing options
function selectSharingOption(option) {
    selectedSharingOption = option;
    
    // Remove active class from all sharing options
    document.querySelectorAll('.sharing-option').forEach(card => {
        card.classList.remove('border-primary');
    });
    
    // Add active class to selected option
    event.currentTarget.classList.add('border-primary');
    
    if (option === 'shared') {
        // Reduce fare by 40% for shared rides
        baseFare = Math.round(cabFare * 0.6);
        document.getElementById('summaryRideType').textContent = 'Cab (Shared)';
    } else {
        // Keep original fare for private rides
        baseFare = cabFare;
        document.getElementById('summaryRideType').textContent = 'Cab (Private)';
    }
    
    updateBookingSummary();
}

// Payment method selection
function selectPaymentMethod(method) {
    selectedPaymentMethod = method;
    
    // Remove active class from all payment options
    document.querySelectorAll('.payment-option').forEach(option => {
        option.classList.remove('border-primary');
    });
    
    // Add active class to selected option
    event.currentTarget.classList.add('border-primary');
    
    let paymentMethodName = '';
    
    switch(method) {
        case 'cash':
            paymentMethodName = 'Cash';
            break;
        case 'card':
            paymentMethodName = 'Card';
            break;
        case 'upi':
            paymentMethodName = 'UPI';
            break;
        case 'wallet':
            paymentMethodName = 'Wallet';
            break;
    }
    
    document.getElementById('summaryPayment').textContent = paymentMethodName;
    updateBookingSummary();
}

// Promo code selection
function selectPromoCode(code) {
    document.getElementById('promoCode').value = code;
}

// Apply promo code
function applyPromoCode() {
    const promoCode = document.getElementById('promoCode').value;
    if (promoCode) {
        appliedPromoCode = promoCode;
        updateBookingSummary();
        
        // Close the modal
        const promoModal = bootstrap.Modal.getInstance(document.getElementById('promoModal'));
        promoModal.hide();
        
        // Show success message - use alert for now, in a real app this would be a toast or notification
        alert('Promo code ' + promoCode + ' applied successfully!');
    } else {
        alert('Please enter a valid promo code');
    }
}

// Function to update booking summary
function updateBookingSummary() {
    // Get pickup location
    const pickupLocation = document.getElementById('pickupLocation').value || 'Park & Ride Parking Lot';
    
    // Update pickup in both summary sections
    document.getElementById('summaryPickup').textContent = pickupLocation;
    
    // Calculate taxes
    const taxes = Math.round(baseFare * 0.18);
    const total = baseFare + taxes;
    
    // Initial discount value
    let discount = 0;
    
    // Check if there's a promo code discount
    if (appliedPromoCode) {
        if (appliedPromoCode === 'FIRSTRIDE') {
            discount = Math.min(baseFare, 150);
        } else if (appliedPromoCode === 'WEEKEND25') {
            discount = Math.min(Math.round(baseFare * 0.25), 100);
        } else if (appliedPromoCode === 'MONSOON20') {
            discount = Math.min(Math.round(baseFare * 0.2), 75);
        }
    }
    
    // Calculate final price
    const finalTotal = total - discount;
    
    // Update both summary sections
    
    // 1. Update card summary on the form
    document.getElementById('summaryFare').textContent = '₹' + finalTotal;
    
    // 2. Update detailed summary in the side panel
    document.getElementById('summaryBaseFare').textContent = '₹' + baseFare;
    document.getElementById('summaryTaxes').textContent = '₹' + taxes;
    document.getElementById('summaryTotal').textContent = '₹' + finalTotal;
    
    // Handle discount display
    if (discount > 0) {
        document.getElementById('discountRow').classList.remove('d-none');
        document.getElementById('summaryDiscount').textContent = '-₹' + discount;
    } else {
        document.getElementById('summaryDiscount').textContent = '-₹0';
    }
    
    // Update modal fare details
    document.getElementById('modalBaseFare').textContent = '₹' + baseFare;
    document.getElementById('modalDistanceFee').textContent = '₹' + Math.round(baseFare * 0.7);
    document.getElementById('modalTimeFee').textContent = '₹' + Math.round(baseFare * 0.3);
    document.getElementById('modalTaxes').textContent = '₹' + taxes;
    document.getElementById('modalDiscount').textContent = '-₹' + discount;
    document.getElementById('modalTotal').textContent = '₹' + finalTotal;
}

// Book ride function
function bookRide() {
    // Validate required fields
    if (!selectedDestination) {
        alert('Please select a destination');
        return;
    }
    
    if (!selectedRideType) {
        alert('Please select a ride type');
        return;
    }
    
    if (selectedRideType === 'cab' && !selectedSharingOption) {
        alert('Please select a cab sharing option');
        return;
    }
    
    if (!selectedPaymentMethod) {
        alert('Please select a payment method');
        return;
    }
    
    // Get current date and time for ride record
    const now = new Date();
    const formattedDate = now.toISOString().slice(0, 10);
    const formattedTime = now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    
    // Get pickup location from form
    const pickupLocation = document.getElementById('pickupLocation').value || 'Park & Ride Parking Lot';
    
    // Get fare values
    const fareElement = document.getElementById('summaryFare');
    const fare = parseInt(fareElement.textContent.replace('₹', ''));
    
    // Create ride object to save
    const rideInfo = {
        id: 'RIDE' + Math.floor(Math.random() * 10000),
        date: formattedDate,
        time: formattedTime,
        pickup: pickupLocation,
        destination: selectedDestination,
        rideType: selectedRideType,
        sharing: selectedRideType === 'cab' ? selectedSharingOption : 'N/A',
        payment: selectedPaymentMethod,
        fare: fare,
        status: 'ongoing'
    };
    
    // Save ride to localStorage
    saveRideToHistory(rideInfo);
    
    // Prepare modal content before showing it
    document.getElementById('bookingSummaryDestination').textContent = selectedDestination;
    document.getElementById('bookingSummaryPickup').textContent = pickupLocation;
    
    // Show booking confirmation modal with simplified UI
    document.getElementById('rideBookedModal').addEventListener('show.bs.modal', function (event) {
        // Only update if this is the first time the event is triggered
        if (!event.target._initialized) {
            // Set ETA based on ride type
            let eta = '5-10';
            if (selectedRideType === 'shuttle') {
                eta = '10-15';
            } else if (selectedRideType === 'erickshaw') {
                eta = '2-5';
            }
            document.getElementById('driverArrivalTime').textContent = eta;
            
            // Hide driver photo section if present
            const driverPhotoEl = document.querySelector('#rideBookedModal .modal-body img');
            if (driverPhotoEl) {
                driverPhotoEl.closest('.me-3').style.display = 'none';
            }
            
            // Replace driver name with icon
            const driverNameEl = document.getElementById('driverName');
            if (driverNameEl) {
                let iconClass = 'fa-taxi';
                if (selectedRideType === 'shuttle') {
                    iconClass = 'fa-shuttle-van';
                } else if (selectedRideType === 'erickshaw') {
                    iconClass = 'fa-charging-station';
                }
                driverNameEl.innerHTML = `<i class="fas ${iconClass} me-2"></i> Your ${selectedRideType.charAt(0).toUpperCase() + selectedRideType.slice(1)} is on the way!`;
            }
            
            // Mark as initialized
            event.target._initialized = true;
        }
    });
    
    // Show modal
    const rideModal = new bootstrap.Modal(document.getElementById('rideBookedModal'));
    rideModal.show();
}

// Save ride to history in localStorage
function saveRideToHistory(rideInfo) {
    // Get existing rides or initialize empty array
    let rideHistory = JSON.parse(localStorage.getItem('rideHistory')) || [];
    
    // Add new ride to history
    rideHistory.unshift(rideInfo);
    
    // Save back to localStorage
    localStorage.setItem('rideHistory', JSON.stringify(rideHistory));
    
    // Update dashboard ride count if we're on dashboard page
    const dashboardRideCountElement = document.getElementById('dashboardRideCount');
    if (dashboardRideCountElement) {
        const currentCount = parseInt(dashboardRideCountElement.textContent) || 0;
        dashboardRideCountElement.textContent = currentCount + 1;
    }
} 