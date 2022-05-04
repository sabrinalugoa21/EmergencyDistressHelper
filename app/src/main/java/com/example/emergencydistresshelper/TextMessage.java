package com.example.emergencydistresshelper;


import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import okhttp3.*;

public class TextMessage {
    private static final String ACCOUNT_SID = "ACd51283a55fad386b1cadeb8e395fffff";
    private static final String AUTH_TOKEN = "";
    private static final String fromNumber = "+19205192974";

    private static String toNumber = "";
    private static String messageBody = "EDH - Twilio SOS Button";
    private static String default_index = "none";
    private static String Result = "ERROR! Alert not sent!";

    private static FirebaseUser user;
    private static DatabaseReference dbReference;

    public static String sendTextMessage(Double Latitude, Double Longitude) {
        //checking if Auth Token is there to send texts
        if (AUTH_TOKEN.length() < 25){
            Result = "ERROR! Invalid Auth Token!";
            return Result;
        }
        //Checking if a valid number was updated
        if (toNumber.length() < 9){
            Result = "ERROR! No Default Contact!";
            return Result;
        }
        //checking if a valid location was updated
        if (Latitude != 0.0 && Longitude != 0.0){
            messageBody = messageBody +
                    "\n\nLatitude: " + Latitude +
                    "\nLongitude: " + Longitude;
        }
        else{
            messageBody = messageBody + "\n\nUser has not enabled location data";
        }

        //http request for message to be sent
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/SMS/Messages";
        String base64EncodedCredentials = "Basic " +
                Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        RequestBody body = new FormBody.Builder()
                .add("From", fromNumber)
                .add("To", toNumber)
                .add("Body", messageBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", base64EncodedCredentials)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d("TAG", "sendTextMessage: " + response.body().string());
        }
        catch (IOException e) {
            e.printStackTrace();
            return Result;
        }
        Result = "Success! Alert was sent!";
        return Result;
    }

    public static void update_number_and_message(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(user.getUid());


        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("defaultContactIndex")){
                    default_index = snapshot.child("defaultContactIndex").getValue(String.class);
                    Log.d("default_index: ", default_index);
                    if (snapshot.hasChild("Contacts/" + default_index)){
                        toNumber = "+1" + snapshot.child("Contacts/" + default_index + "/phoneNumber").getValue(String.class);
                        Log.d("toNumber: ", toNumber);
                        messageBody = snapshot.child("Contacts/" + default_index + "/message").getValue(String.class);
                        Log.d("messageBody: ", messageBody);
                    }
                    else {
                        toNumber = "none";
                        Log.d("toNumber: ", toNumber);
                        messageBody = "empty";
                        Log.d("messageBody: ", messageBody);
                        Log.d("----update", "no default Contact number");
                    }
                }
                else{
                    Log.d("----update", "no defaultContactIndex");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
