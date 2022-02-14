package com.example.emergencydistresshelper;
import java.lang.String;
public class User {

    //User information
    public String FirstName;
    public String LastName;
    public String PhoneNumber;
    public String Email;
    public String Address;

    public User(String First, String Last, String phone, String em, String add)
    {
        FirstName = First;
        LastName = Last;
        PhoneNumber = phone;
        Email = em;
        Address = add;

    }

    public User()
    {
        FirstName = "";
        LastName = "";
        PhoneNumber = "";
        Email = "";
        Address = "";
    }
    //Setting functions
    public void setFirstName(String First){ FirstName = FirstName; }
    public void setLastName(String Last){LastName = Last; }
    public void setPhoneNumber(String phone){PhoneNumber = phone;}
    public void setEmail(String em) {Email = em; }
    public void setAddress(String add) {Address = add; }

    //Getting functions
    public String getFirstName() {return FirstName; }
    public String getLastName() {return LastName; }
    public String getPhoneNumber(){return PhoneNumber; }
    public String getEmail(){return Email; }
    public String getAddress(){return Address; }

}
