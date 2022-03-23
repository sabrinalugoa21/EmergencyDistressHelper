package com.example.emergencydistresshelper;
import com.example.emergencydistresshelper.User;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUp
{
    //should have a person data type
    //need to make a person class
    public Boolean getSignUp(User client)
    {
        //User customer = new User();
        Connection connection;
        String url = "jdbc:mysql://localhost:3306/EDH";
        String username = "root";
        String password = "";
        Boolean status = false;

        System.out.println("Connecting database..");

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");

            //Statement findUser = connection.createStatement();
            connection.setAutoCommit(false);
            PreparedStatement query = connection.prepareStatement("SELECT COUNT(*) FROM Users WHERE FirstName = ? AND LastName = ?" +
                    "AND Email = ?");
            query.setString(1, client.FirstName);
            query.setString(2, client.LastName);
            query.setString(3, client.Email);
            ResultSet rs = query.executeQuery();
            rs.next();

            try {
                if (rs.getInt(1) == 0) {
                    query = connection.prepareStatement("INSERT INTO Users (FirstName, LastName, PhoneNumber, Email, " +
                            "Street, City, State, ZipCode) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
                    query.setString(1, client.FirstName);
                    query.setString(2, client.LastName);
                    query.setString(3, client.PhoneNumber);
                    query.setString(4, client.Email);
                    query.setString(5, client.Street);
                    query.setString(6, client.City);
                    query.setString(7, client.State);
                    query.setString(8, client.ZipCode);
                    query.executeUpdate();
                    connection.commit();
                    connection.close();
                    status = true;

                } else {
                    status = false;
                }
            } catch (SQLException se) {
                connection.rollback();
            }

        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot connect the database!", ex);
        }
        return status;
    }
}