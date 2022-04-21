package com.example.emergencydistresshelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_contact:
                startActivity(new Intent(this, Contacts.class));
                break;
            case R.id.btn_add_contact:
                addContact();
                break;
        }
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
        if(!(Patterns.PHONE.matcher(phone).matches())) {
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

        Contact contact = new Contact(fullName, phone, message);
        mDatabase.child("Users").child(user.getUid()).setValue(contact);

    }

}
