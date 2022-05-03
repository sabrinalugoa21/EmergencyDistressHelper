package com.example.emergencydistresshelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.regex.Pattern;

public class CreateContact extends AppCompatActivity implements View.OnClickListener {

    private TextView cancel, addContact;
    private EditText editTextFullName, editTextPhoneNumber, editTextMessage;

    private FirebaseUser user;
    private DatabaseReference dbReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(user.getUid());

        cancel = (Button) findViewById(R.id.btn_cancel_contact);
        cancel.setOnClickListener(this);

        addContact = (Button) findViewById(R.id.btn_add_contact);
        addContact.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.add_contact_name);
        editTextFullName.setOnClickListener(this);

        editTextPhoneNumber = (EditText) findViewById(R.id.add_contact_phone);
        editTextPhoneNumber.setOnClickListener(this);

        editTextMessage = (EditText) findViewById(R.id.add_contact_message);
        editTextMessage.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // Go to the contacts page
            case R.id.btn_cancel_contact:
                startActivity(new Intent(this, Contacts.class));
                break;

            // Add the contact, then navigate to the contacts page.
            case R.id.btn_add_contact:
                addContact();
                startActivity(new Intent(this, Contacts.class));
                break;
        }
    }

    // Method to determine if a phone number is valid
    private boolean isValidMobile(String phone) {
        return Pattern.matches("[0-9]{10,11}$", phone);
    }

    private void addContact() {
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhoneNumber.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();

        // Validation to check if contact name is empty
        if(fullName.isEmpty())
        {
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }

        // Validation to check if contact phone number is empty
        if(phone.isEmpty())
        {
            editTextPhoneNumber.setError("Phone number is required!");
            editTextPhoneNumber.requestFocus();
            return;
        }

        // Validation to check if contact phone number matches expected phone number patterns
        if(!(isValidMobile(phone))) {
            editTextPhoneNumber.setError("Please provide valid phone number!");
            editTextPhoneNumber.requestFocus();
            return;
        }

        // Validation to check if contact message is empty
        if(message.isEmpty())
        {
            editTextMessage.setError("Message is required!");
            editTextMessage.requestFocus();
            return;
        }

        // Create new contact object and reference the
        // user's contacts in the database
        Contact contact = new Contact(fullName, phone, message);

        // Get the highest index of an existing contact
        Query latestContactQuery = dbReference.child("Contacts").orderByKey().limitToLast(1);
        latestContactQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int latestContactIndex = -1;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    latestContactIndex = Integer.parseInt(ds.getKey());
                }

                // If the user is adding a contact for the first time,
                // it must be the default contact
                if (snapshot.getChildrenCount() == 0) {
                    dbReference.child("defaultContactIndex").setValue("0");
                }

                // Add new contact for user in the database
                dbReference.child("Contacts").child(String.valueOf(latestContactIndex + 1)).setValue(contact, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Toast.makeText(CreateContact.this, "Failed to add the contact.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateContact.this, "Successfully added contact!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // only update if first contact
                if (snapshot.getChildrenCount() == 0) {
                    TextMessage.update_number_and_message();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Log", "Failed to retrieve snapshot for user contact list");
            }
        });
    }

}
