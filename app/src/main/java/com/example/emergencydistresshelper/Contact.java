package com.example.emergencydistresshelper;

public class Contact {

    // Contact information
    private String name;
    private String phoneNumber;
    private String message;

    // Constructor
    public Contact(String name, String phoneNumber, String message) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    // No-args constructor
    public Contact() {
        this.name = "";
        this.phoneNumber = "";
        this.message = "";
    }

    // Getter methods
    public String getName() {
        return this.name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getMessage() {
        return this.message;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
