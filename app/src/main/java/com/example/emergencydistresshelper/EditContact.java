package com.example.emergencydistresshelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class EditContact extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewOldName, textViewOldPhone, textViewOldMessage;
    private EditText editTextNewName, editTextNewPhone, editTextNewMessage;
    private TextView cancelButton, applyButton;
    private FirebaseUser user;
    private DatabaseReference dbReference;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        textViewOldName = findViewById(R.id.old_contact_name);
        textViewOldPhone = findViewById(R.id.old_contact_phone);
        textViewOldMessage = findViewById(R.id.old_contact_message);
        editTextNewName = findViewById(R.id.new_contact_name);
        editTextNewPhone = findViewById(R.id.new_contact_phone);
        editTextNewMessage = findViewById(R.id.new_contact_message);

        cancelButton = findViewById(R.id.btn_cancel_contact);
        cancelButton.setOnClickListener(this);
        applyButton = findViewById(R.id.btn_apply_contact_changes);
        applyButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            textViewOldName.setText(extras.getString("name"));
            textViewOldPhone.setText(extras.getString("phone"));
            textViewOldMessage.setText(extras.getString("message"));
            index = extras.getInt("index");
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(user.getUid())
                .child("Contacts");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // Go back to the contacts page
            case R.id.btn_cancel_contact:
                startActivity(new Intent(this, Contacts.class));
                break;

            // Apply the contact info changes, then navigate back to the contacts page.
            case R.id.btn_apply_contact_changes:
                editContact(index);
                startActivity(new Intent(this, Contacts.class));
                break;
        }

    }

    // Method to determine if a phone number is valid
    private boolean isValidMobile(String phone) {
        return Pattern.matches("[0-9]{10,11}$", phone);
    }

    private void editContact(int contactIndex) {
        HashMap<String, Object> update = new HashMap<>();
        String fullName = editTextNewName.getText().toString().trim();
        String phone = editTextNewPhone.getText().toString().trim();
        String message = editTextNewMessage.getText().toString().trim();

        // User wants to update contact's name
        if(!fullName.isEmpty())
        {
            update.put("name", fullName);
        }

        // User wants to update contact's phone
        if(!phone.isEmpty()) {
            // Validation to check if contact phone number matches expected phone number patterns
            if(!(isValidMobile(phone))) {
                editTextNewPhone.setError("Please provide valid phone number!");
                editTextNewPhone.requestFocus();
                return;
            }

            update.put("phoneNumber", phone);
        }

        // User wants to update contact's message
        if(!message.isEmpty()) {
            update.put("message", message);
        }

        // Update contact's information in Firebase
        dbReference.child(String.valueOf(contactIndex)).updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Toast.makeText(EditContact.this, "Failed to update contact's information.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditContact.this, "Successfully changed contact's information!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}