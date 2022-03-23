package com.example.emergencydistresshelper;
import java.lang.String;

public class User {
    //User information
    public String FirstName;
    public String LastName;
    public String PhoneNumber;
    public String Email;
    public String Address;
    public String Street;
    public String City;
    public String State;
    public String ZipCode;

    public User(String First, String Last, String phone, String em, String str, String cit, String stat, String zip)
    {
        FirstName = First;
        LastName = Last;
        PhoneNumber = phone;
        Email = em;
        Street = str;
        City = cit;
        State = stat;
        ZipCode = zip;

    }

    public User()
    {
        FirstName = "";
        LastName = "";
        PhoneNumber = "";
        Email = "";
        Street = "";
        City = "";
        State = "";
        ZipCode = "";

    }
    //Setting functions
    public void setFirstName(String First){ FirstName = First; }
    public void setLastName(String Last){LastName = Last; }
    public void setPhoneNumber(String phone){PhoneNumber = phone;}
    public void setEmail(String em) {Email = em; }
    public void setStreet(String str) {Street = str; }
    public void setCity(String cit) {City = cit; }
    public void setState(String stat) {State = stat; }
    public void setZipCode(String zip) {ZipCode = zip; }

    //Getting functions
    public String getFirstName() {return FirstName; }
    public String getLastName() {return LastName; }
    public String getPhoneNumber(){return PhoneNumber; }
    public String getEmail(){return Email; }
    public String getAddress(){return Address; }
    public String getStreet() {return Street; }
    public String getCity() {return City; }
    public String getState() {return State; }
    public String getZipCode() { return ZipCode; }
}

