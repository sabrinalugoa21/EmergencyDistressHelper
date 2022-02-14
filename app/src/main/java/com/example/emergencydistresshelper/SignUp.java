package com.example.emergencydistresshelper;
import com.example.emergencydistresshelper.User;
public class SignUp {
    //should have a person data type
    //need to make a person class
    public void getSignUp()
    {
            User customer = new User();

            try
            {
                customer.setFirstName();
                customer.setLastName();
                customer.setPhoneNumber();
                customer.setEmail();
                customer.setAddress();

                try
                {
                    //connect to database
                }
                catch (Exception ex)
                {

                }
            }
            catch (Exception ex)
            {

            }

    }

}
