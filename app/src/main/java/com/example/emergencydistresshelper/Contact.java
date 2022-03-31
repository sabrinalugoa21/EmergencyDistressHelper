package com.example.emergencydistresshelper;

public class Contact {

    // Contact information
    public String name;
    public String phoneNumber;
    public String message;
    public boolean isDefaultContact;

    // Constructor
    public Contact(String name, String phoneNumber, String message) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.isDefaultContact = false;
    }

    // No-args constructor
    public Contact() {
        this.name = "";
        this.phoneNumber = "";
        this.message = "";
        this.isDefaultContact = false;
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

    public boolean isDefaultContact() {
        return this.isDefaultContact;
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

    public void setIsDefaultContact(boolean isDefaultContact) {
        this.isDefaultContact = isDefaultContact;
    }
}
