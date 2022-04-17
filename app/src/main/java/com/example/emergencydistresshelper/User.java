package com.example.emergencydistresshelper;
import java.lang.String;

public class User {
    //User information
    public String fullName;
    public String email;
    public String username;



    public User(String fullName, String email, String username)
    {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
    }

    public User()
    {
        this.fullName = "";
        this.email = "";
        this.username = "";

    }
    //Setting functions
    public void setFullName(String fullName){ this.fullName = fullName; }
    public void setEmail(String email){this.email = email; }
    public void setUsername(String username){this.username = username;}

    //Getting functions
    public String getFullName() {return fullName; }
    public String getEmail() {return email; }
    public String getUsername() {return username; }
}

