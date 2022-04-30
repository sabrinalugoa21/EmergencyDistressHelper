package com.example.emergencydistresshelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Hashtable;


public class Contacts extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewName, textViewPhone, textViewMessage;
    private TextView addContact, setDefault1, setDefault2;
    private FirebaseUser user;
    private DatabaseReference dbReference;
    private long numOfContacts;
    private Hashtable<String, Integer> hashtable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        addContact = (Button) findViewById(R.id.btn_show_add_contact_page);
        addContact.setOnClickListener(this);
        setDefault1 = (Button) findViewById(R.id.btn_set_default_contact_1);
        setDefault1.setOnClickListener(this);
        setDefault2 = (Button) findViewById(R.id.btn_set_default_contact_2);
        setDefault2.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(user.getUid())
                .child("Contacts");

        hashtable = new Hashtable<String, Integer>();

        updateContactsList();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_show_add_contact_page:
                startActivity(new Intent(this, CreateContact.class));
                break;

            case R.id.btn_set_default_contact_1:
                updateDefault1();
                break;

            case R.id.btn_set_default_contact_2:
                updateDefault2();
                break;
        }
    }

    public void updateContactsList() {
        getNumOfContacts(value -> {
            numOfContacts = value;
            makeContactRows();
        });
        updateContactFields();
    }


    public interface FirebaseCallback {
        void onCallback(long value);
    }

    private void getNumOfContacts(FirebaseCallback myCallback) {
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long value = snapshot.getChildrenCount();
                myCallback.onCallback(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeContactRows() {
        LinearLayout contactsListLL = (LinearLayout) findViewById(R.id.contacts_list);
        contactsListLL.removeAllViewsInLayout();

        for (long i = 0; i < numOfContacts; i++) {
            LinearLayout childLL = new LinearLayout(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f);
            int id;

            TextView textName = new TextView(this);
            textName.setText("Name" + i);
            id = View.generateViewId();
            textName.setId(id);
            hashtable.put("Name" + i, id);
            textName.setLayoutParams(p);
            childLL.addView(textName);
            Log.d("Testing", "ID of 'Name" + i + "': " + hashtable.get("Name" + i));

            TextView textPhone = new TextView(this);
            textPhone.setText("Phone" + i);
            id = View.generateViewId();
            textPhone.setId(id);
            hashtable.put("Phone" + i, id);
            textPhone.setLayoutParams(p);
            childLL.addView(textPhone);
            Log.d("Testing", "ID of 'Phone" + i + "': " + hashtable.get("Phone" + i));

            TextView textMessage = new TextView(this);
            textMessage.setText("Messsage" + i);
            id = View.generateViewId();
            textMessage.setId(id);
            hashtable.put("Message" + i, id);
            textMessage.setLayoutParams(p);
            childLL.addView(textMessage);
            Log.d("Testing", "ID of 'Message" + i + "': " + hashtable.get("Message" + i));

            contactsListLL.addView(childLL);
        }
    }

    private void updateContactFields() {
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Contact contact = ds.getValue(Contact.class);
                    textViewName = (TextView) findViewById(hashtable.get("Name" + index));
                    textViewPhone = (TextView) findViewById(hashtable.get("Phone" + index));
                    textViewMessage = (TextView) findViewById(hashtable.get("Message" + index));

                    textViewName.setText(contact.getName());
                    textViewPhone.setText(contact.getPhoneNumber());
                    textViewMessage.setText(contact.getMessage());

                    index++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateDefault1() {
        HashMap<String, Object> update = new HashMap<>();
        update.put("defaultContactIndex", "0");

        dbReference.getParent().updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Log.d("Testing", "Failed to change default contact. " + error.getMessage());
                } else {
                    Log.d("Testing", "Successfully changed default contact!");
                }
            }
        });
    }

    public void updateDefault2() {
        HashMap<String, Object> update = new HashMap<>();
        update.put("defaultContactIndex", "2");

        dbReference.getParent().updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Log.d("Testing", "Failed to change default contact. " + error.getMessage());
                } else {
                    Log.d("Testing", "Successfully changed default contact!");
                }
            }
        });
    }
}
