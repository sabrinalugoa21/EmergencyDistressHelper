package com.example.emergencydistresshelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emergencydistresshelper.Login;
import com.example.emergencydistresshelper.R;
import com.example.emergencydistresshelper.User;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private TextView banner, registerUser;
    private EditText editTextFullName, editTextEmail, editTextPassword;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.tv_logo);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.btn_register);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.et_name);
        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextPassword = (EditText) findViewById(R.id.et_password);

        progressbar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.tv_logo:
                startActivity(new Intent(this, Login.class));
                progressbar.setVisibility(View.GONE);
                break;
            case R.id.btn_register:
                registerUser();
                progressbar.setVisibility(View.GONE);
                break;

        }
    }

    private void registerUser()
    {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(fullName.isEmpty())
        {
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }
        if(email.isEmpty())
        {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            editTextPassword.setError("Minimum password length is 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            User user = new User(fullName, email);


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(Register.this, "Registered successfully!", Toast.LENGTH_LONG).show();
                                        progressbar.setVisibility(View.GONE);
                                        startActivity(new Intent(Register.this, Login.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(Register.this, "Something went wrong with registration. Try again.", Toast.LENGTH_LONG).show();
                                        progressbar.setVisibility(View.GONE);
                                    }

                                }
                            });

                        }
                        if(!task.isSuccessful())
                        {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                Toast.makeText(Register.this, "User exists with email. Please login. ", Toast.LENGTH_LONG).show();
                                progressbar.setVisibility(View.GONE);
                            }
                            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(Register.this, "Use email with valid form. ", Toast.LENGTH_LONG).show();
                                progressbar.setVisibility(View.GONE);
                            }
                            else if (task.getException() instanceof FirebaseAuthWeakPasswordException)
                            {
                                Toast.makeText(Register.this, "Use a stronger password.  ", Toast.LENGTH_LONG).show();
                                progressbar.setVisibility(View.GONE);
                            }
                            else
                            {
                                Toast.makeText(Register.this, "Registration failed!", Toast.LENGTH_LONG).show();
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
}
