package com.example.emergencydistresshelper;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

import org.w3c.dom.Text;


public class Contacts extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewName, textViewPhone, textViewMessage;
    private FirebaseUser user;
    private DatabaseReference dbReference;
    private long numOfContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(user.getUid())
                .child("Contacts");

        numOfContacts = getNumOfContacts();
        Log.d("Testing", "Yay, num of contacts is: " + numOfContacts);

        makeContactRows();
    }

    @Override
    public void onClick(View view) {

    }


    private long getNumOfContacts() {
        Task<DataSnapshot> numOfContactsTask = dbReference.get();
        while (!numOfContactsTask.isComplete());
        return numOfContactsTask.getResult().getChildrenCount();
    }

    private void makeContactRows() {
        LinearLayout contactsListLL = (LinearLayout) findViewById(R.id.contactsList);

        for (long i = 0; i < numOfContacts; i++) {
            LinearLayout childLL = new LinearLayout(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f);

            TextView textName = new TextView(this);
            textName.setText("Name" + (i + 1));
            textName.setId(View.generateViewId());
            textName.setLayoutParams(p);
            childLL.addView(textName);

            TextView textPhone = new TextView(this);
            textPhone.setText("Phone" + (i + 1));
            textPhone.setId(View.generateViewId());
            textPhone.setLayoutParams(p);
            childLL.addView(textPhone);

            TextView textMessage = new TextView(this);
            textMessage.setText("Messsage" + (i + 1));
            textMessage.setId(View.generateViewId());
            textMessage.setLayoutParams(p);
            childLL.addView(textMessage);

            contactsListLL.addView(childLL);
        }

    }

    private void updateContactList() {
        textViewName = (TextView) findViewById(R.id.txtContactName1);
//        textViewName.setOnClickListener(this);

        textViewPhone = (TextView) findViewById(R.id.txtContactMessage1);
//        textViewPhone.setOnClickListener(this);

        textViewMessage = (TextView) findViewById(R.id.txtContactMessage1);
//        textViewMessage.setOnClickListener(this);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numOfContacts = snapshot.getChildrenCount();
                Contact contact = snapshot.getValue(Contact.class);

                textViewName.setText(contact.getName());
                textViewPhone.setText(contact.getPhoneNumber());
                textViewMessage.setText(contact.getMessage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });
    }
}
