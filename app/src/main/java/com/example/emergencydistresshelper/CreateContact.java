package com.example.emergencydistresshelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;

public class CreateContact extends AppCompatActivity implements View.OnClickListener {

    private TextView cancel, addContact;
    private EditText editTextFullName, editTextPhoneNumber, editTextMessage;

    private FirebaseUser user;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        DatabaseReference userContactRef = mDatabase
                .child("Users")
                .child(user.getUid())
                .child("Contacts");

        // Get number of contacts the user already has
        Task<DataSnapshot> numOfContactsTask = userContactRef.get();
        while (!numOfContactsTask.isComplete());
        long numOfContacts = numOfContactsTask.getResult().getChildrenCount();

        // Add new contact for user in the database
        userContactRef
                .child(String.valueOf(numOfContacts))
                .setValue(contact);

    }

}
