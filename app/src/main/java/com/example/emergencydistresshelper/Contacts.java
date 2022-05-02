package com.example.emergencydistresshelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    ImageButton arrow1;
    LinearLayout hiddenView1;
    CardView cardView1;

    private TextView editContact;
    private TextView changeDefaultContact;
    private TextView deleteContact;
    private String m_Text = "";

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

        hashtable = new Hashtable<String, Integer>();

        updateContactsList();


        cardView1 = findViewById(R.id.base_cardview1);
        arrow1 = findViewById(R.id.arrow_button1);
        hiddenView1 = findViewById(R.id.hidden_view1);

        arrow1.setOnClickListener(this);

        addContact = (Button) findViewById(R.id.btn_add_a_contact);
        addContact.setOnClickListener(this);
        editContact = findViewById(R.id.btn_edit_a_contact);
        editContact.setOnClickListener(this);
        changeDefaultContact = findViewById(R.id.btn_change_default_contact);
        changeDefaultContact.setOnClickListener(this);
        deleteContact = findViewById(R.id.btn_delete_a_contact);
        deleteContact.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_a_contact:
                startActivity(new Intent(this, CreateContact.class));
                break;

            case R.id.arrow_button1:
                expand_and_collapse_actions();
                break;

            case R.id.btn_edit_a_contact:
                contactToEdit();
                break;

            case R.id.btn_change_default_contact:
                contactToDefault();
                break;

            case R.id.btn_delete_a_contact:
                deleteContact();
                break;
        }
    }


    public void contactToEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name of contact to edit:");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void contactToDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name of contact to set as default:");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void deleteContact() {

        // Create a popup AlertDialogue for user to enter contact name into
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name of contact to delete:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();

                // Search the database for the contact the user wants to delete
                dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Contact contact;
                        int index = 0;

                        // Find the index of the contact which matches user input
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            contact = ds.getValue(Contact.class);
                            if (contact.getName().equals(m_Text)) {
                                break;
                            } else {
                                index++;
                            }
                        }

                        // Check to see if the contact user wants to delete is the default contact
                        int finalIndex = index;
                        dbReference.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String defaultContactIndex = snapshot.child("defaultContactIndex").getValue(String.class);

                                if (Integer.parseInt(defaultContactIndex) == finalIndex) {
                                    Toast.makeText(Contacts.this, "Can't delete the default contact!", Toast.LENGTH_SHORT).show();

                                } else {

                                    // Delte the contact
                                    dbReference.child(String.valueOf(finalIndex)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(Contacts.this, "Successfully deleted the contact!", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(Contacts.this, "Failed to delete contact", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("Log", "Failed to retrieve snapshot for parent of dbReference");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Log", "Failed to retrieve snapshot for dbReference");
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void expand_and_collapse_actions() {
        // If the CardView is already expanded, set its visibility
        // to gone and change the expand less icon to expand more.
        if (hiddenView1.getVisibility() == View.VISIBLE) {

            // The transition of the hiddenView is carried out
            // by the TransitionManager class.
            // Here we use an object of the AutoTransition
            // Class to create a default transition.
            TransitionManager.beginDelayedTransition(cardView1,
                    new AutoTransition());
            hiddenView1.setVisibility(View.GONE);
            arrow1.setImageResource(R.drawable.ic_baseline_expand_less_24);
        }

        // If the CardView is not expanded, set its visibility
        // to visible and change the expand more icon to expand less.
        else {

            TransitionManager.beginDelayedTransition(cardView1,
                    new AutoTransition());
            hiddenView1.setVisibility(View.VISIBLE);
            arrow1.setImageResource(R.drawable.ic_baseline_expand_more_24);
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
}
