package com.example.movieaapp.Domain;

public class Seat {
    private int id;
    private String status; // Possible values: "available", "booked", "selected"

    // Constructor to initialize the Seat object
    public Seat(int id, String status) {
        this.id = id;
        this.status = status;
    }

    // Getter for the Seat ID
    public int getId() {
        return id;
    }

    // Setter for the Seat ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter for the Seat status
    public String getStatus() {
        return status;
    }

    // Setter for the Seat status
    public void setStatus(String status) {
        this.status = status;
    }

    // Method to check if the seat is available
    public boolean isAvailable() {
        return "available".equalsIgnoreCase(status);
    }

    // Method to book the seat if it is available
    public void bookSeat() {
        if (isAvailable()) {
            setStatus("booked");
        }
    }

    // Method to select the seat if it is available
    public void selectSeat() {
        if (isAvailable()) {
            setStatus("selected");
        }
    }

    // Method to unselect the seat if it was selected
    public void unselectSeat() {
        if ("selected".equalsIgnoreCase(status)) {
            setStatus("available");
        }
    }

    // Method to check if the seat is booked
    public boolean isBooked() {
        return "booked".equalsIgnoreCase(status);
    }

    // Method to check if the seat is selected
    public boolean isSelected() {
        return "selected".equalsIgnoreCase(status);
    }
}

