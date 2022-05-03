package com.example.emergencydistresshelper;
import java.lang.String;

public class User {
    //User information
    public String fullName;
    public String email;



    public User(String fullName, String email)
    {
        this.fullName = fullName;
        this.email = email;
    }

    public User()
    {
        this.fullName = "";
        this.email = "";

    }
    //Setting functions
    public void setFullName(String fullName){ this.fullName = fullName; }
    public void setEmail(String email){this.email = email; }

    //Getting functions
    public String getFullName() {return fullName; }
    public String getEmail() {return email; }
}



