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
import java.util.Map;


public class Contacts extends AppCompatActivity implements View.OnClickListener{

    private TextView defaultContact, textViewName, textViewPhone, textViewMessage;
    private TextView homeButton;
    private FirebaseUser user;
    private DatabaseReference dbReference;
    private long numOfContacts;
    private Hashtable<String, Integer> hashtable;

    ImageButton arrow1;
    LinearLayout hiddenView1;
    CardView cardView1;

    private TextView addContact;
    private TextView editContact;
    private TextView changeDefaultContact;
    private TextView deleteContact;
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        homeButton = (Button) findViewById(R.id.home_button);
        homeButton.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(user.getUid())
                .child("Contacts");

        hashtable = new Hashtable<String, Integer>();

        updateContactsList();

        defaultContact = findViewById(R.id.current_default_text);

        cardView1 = findViewById(R.id.base_cardview1);
        arrow1 = findViewById(R.id.arrow_button1);
        hiddenView1 = findViewById(R.id.hidden_view1);

        arrow1.setOnClickListener(this);

        addContact = (Button) findViewById(R.id.btn_add_a_contact);
        addContact.setOnClickListener(this);
        editContact = (Button) findViewById(R.id.btn_edit_a_contact);
        editContact.setOnClickListener(this);
        changeDefaultContact = (Button) findViewById(R.id.btn_change_default_contact);
        changeDefaultContact.setOnClickListener(this);
        deleteContact = (Button) findViewById(R.id.btn_delete_a_contact);
        deleteContact.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_button:
                startActivity(new Intent(this, Homepage.class));
                break;

            case R.id.btn_add_a_contact:
                startActivity(new Intent(this, CreateContact.class));
                break;

            case R.id.arrow_button1:
                expand_and_collapse_actions();
                break;

            case R.id.btn_edit_a_contact:
                contactEditPage();
                break;

            case R.id.btn_change_default_contact:
                contactToDefault();
                break;

            case R.id.btn_delete_a_contact:
                deleteContact();
                break;
        }
    }

    public void updateDefaultContactLabel() {
        dbReference.getParent().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String index = snapshot.child("defaultContactIndex").getValue(String.class);

                if (index != null) {
                    defaultContact.setText(snapshot
                            .child("Contacts")
                            .child(index)
                            .child("name")
                            .getValue(String.class));
                } else {
                    // User does not have any contacts
                    defaultContact.setText("N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Log", "Failed to get snapshot for parent of dbReference");
            }
        });
    }


    public void contactEditPage() {

        // Create a popup AlertDialogue for user to enter contact name into
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name of contact to edit:");
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
                        Contact contact = null;
                        int index = 0;
                        boolean foundMatch = false;

                        // Find the index of the contact which matches user input
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            contact = ds.getValue(Contact.class);
                            if (contact.getName().equals(m_Text)) {
                                foundMatch = true;
                                break;
                            } else {
                                index++;
                            }
                        }

                        // Exit if no contact match for user input was found
                        if (!foundMatch) {
                            Toast.makeText(Contacts.this, "You have no contact matching that name!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(Contacts.this, EditContact.class);
                        intent.putExtra("name", contact.getName());
                        intent.putExtra("phone", contact.getPhoneNumber());
                        intent.putExtra("message", contact.getMessage());
                        intent.putExtra("index", index);
                        startActivity(intent);

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

    public void contactToDefault() {

        // Create a popup AlertDialogue for user to enter contact name into
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name of contact to default:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();

                // Search the database for the contact the user wants to set as default
                dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Contact contact;
                        String index = "";
                        boolean foundMatch = false;

                        // Find the index of the contact which matches user input
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            contact = ds.getValue(Contact.class);
                            if (contact.getName().equals(m_Text)) {
                                index = ds.getKey();
                                foundMatch = true;
                                break;
                            }
                        }

                        // Exit if no contact match for user input was found
                        if (!foundMatch) {
                            Toast.makeText(Contacts.this, "You have no contact matching that name!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the defaultContactIndex value in the database
                        HashMap<String, Object> update = new HashMap<>();
                        update.put("defaultContactIndex", index);

                        dbReference.getParent().updateChildren(update, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error != null) {
                                    Toast.makeText(Contacts.this, "Failed to change default contact.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Contacts.this, "Successfully changed default contact!", Toast.LENGTH_SHORT).show();
                                }
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
                        boolean foundMatch = false;

                        // Find the index of the contact which matches user input
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            contact = ds.getValue(Contact.class);
                            if (contact.getName().equals(m_Text)) {
                                foundMatch = true;
                                break;
                            } else {
                                index++;
                            }
                        }

                        // Exit if no contact match for user input was found
                        if (!foundMatch) {
                            Toast.makeText(Contacts.this, "You have no contact matching that name!", Toast.LENGTH_SHORT).show();
                            return;
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

                                    // Delete the contact
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
        updateDefaultContactLabel();
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
            childLL.setWeightSum(10);
            LinearLayout.LayoutParams nameP = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2);
            nameP.setMargins(0, 10, 0, 10);
            LinearLayout.LayoutParams phoneP = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    3);
            phoneP.setMargins(0, 10, 0, 10);
            LinearLayout.LayoutParams messageP = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    5);
            messageP.setMargins(0, 15, 0, 15);
            int id;

            TextView textName = new TextView(this);
            textName.setText("Name" + i);
            id = View.generateViewId();
            textName.setId(id);
            hashtable.put("Name" + i, id);
            textName.setLayoutParams(nameP);
            childLL.addView(textName);

            TextView textPhone = new TextView(this);
            textPhone.setText("Phone" + i);
            id = View.generateViewId();
            textPhone.setId(id);
            hashtable.put("Phone" + i, id);
            textPhone.setLayoutParams(phoneP);
            childLL.addView(textPhone);

            TextView textMessage = new TextView(this);
            textMessage.setText("Messsage" + i);
            id = View.generateViewId();
            textMessage.setId(id);
            hashtable.put("Message" + i, id);
            textMessage.setLayoutParams(messageP);
            childLL.addView(textMessage);

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
