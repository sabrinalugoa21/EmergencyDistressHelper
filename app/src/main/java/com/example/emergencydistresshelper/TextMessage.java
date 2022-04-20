package com.example.emergencydistresshelper;


import android.util.Base64;
import android.util.Log;

import java.io.IOException;

import okhttp3.*;

public class TextMessage {
    private static final String ACCOUNT_SID = "ACd51283a55fad386b1cadeb8e395fffff";
    private static final String AUTH_TOKEN = "";
    private static final String fromNumber = "+19205192974";

    public static String sendTextMessage(Double Latitude, Double Longitude) {
        //get number using getPhoneNumber() from Contact.java
        String toNumber = "";
        String messageBody = "EDH - Twilio SOS Button";
        String Result = "ERROR! Alert not sent!";
        messageBody = messageBody +
                "\nLatitude: " + Latitude +
                "\nLongitude: " + Longitude;

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
}
