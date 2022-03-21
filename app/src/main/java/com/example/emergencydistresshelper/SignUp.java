package com.example.emergencydistresshelper;
import com.example.emergencydistresshelper.User;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUp {
    //should have a person data type
    //need to make a person class
    public void getSignUp(User client)
    {
        User customer = new User();
        Connection connection;
        String url = "jdbc:mysql://localhost:3306/javabase";
        String username = "root";
        String password = "toor";

        System.out.println("Connecting database..");


        try
        {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");

            //Statement findUser = connection.createStatement();
            PreparedStatement query = connection.prepareStatement("SELECT COUNT(*) FROM Users WHERE email = ?");
            query.setString(1, client.Email);
            ResultSet rs = query.executeQuery();
            rs.next();

            try
            {
                if (rs.getInt(1) == 0) {
                    query = connection.prepareStatement("INSERT INTO User (FirstN, LastN, PhoneNum, Email, Address)" +
                                                        "VALUES(?, ?, ?, ?)");
                    query.setString(1, client.FirstName);
                    query.setString(2, client.LastName);
                    query.setString(3, client.PhoneNumber);
                    query.setString(4, client.Email);
                    query.setString(5, client.Address);
                    query.executeQuery();
                    connection.commit();
                }
            }
            catch (SQLException se)
            {
                connection.rollback();
            }

        }
        catch (SQLException ex)
        {
            throw new IllegalStateException("Cannot connect the database!", ex);
        }

    }

}
