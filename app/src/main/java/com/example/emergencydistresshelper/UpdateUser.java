package com.example.emergencydistresshelper;

import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emergencydistresshelper.databinding.ActivityUpdateDataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class UpdateUser extends AppCompatActivity implements View.OnClickListener {
    private TextView banner;
    ActivityUpdateDataBinding binding;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        banner = (TextView) findViewById(R.id.logo);
        banner.setOnClickListener(this);

        binding.btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logo:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.btnReset:
                String email = binding.email.getText().toString();
                String old_password = binding.oldPassword.getText().toString();
                String new_password = binding.password.getText().toString();
                String re_password = binding.repassword.getText().toString();

                if (email.isEmpty()) {
                    binding.email.setError("Email is required!");
                    binding.email.requestFocus();
                    return;
                }

                if (old_password.isEmpty()) {
                    binding.oldPassword.setError("Password is required!");
                    binding.oldPassword.requestFocus();
                    return;
                }

                if (new_password.isEmpty()) {
                    binding.password.setError("Please enter new password!");
                    binding.password.requestFocus();
                    return;
                }

                if (re_password.isEmpty()) {
                    binding.repassword.setError("Please re-enter new password!");
                    binding.repassword.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.email.setError("Please provide valid email!");
                    binding.email.requestFocus();
                    return;
                }

                if (old_password.length() < 6) {
                    binding.oldPassword.setError("Minimum password length is 6 characters!");
                    binding.oldPassword.requestFocus();
                    return;
                }

                if (new_password.length() < 6) {
                    binding.password.setError("Minimum password length is 6 characters!");
                    binding.password.requestFocus();
                    return;
                }

                if (re_password.length() < 6) {
                    binding.repassword.setError("Minimum password length is 6 characters!");
                    binding.repassword.requestFocus();
                    return;
                }

                if(!new_password.equals(re_password))
                {
                    Toast.makeText(UpdateUser.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
                    return;
                }
                updateAuth(email, old_password, new_password);
                break;

        }

    }

    private void updateAuth(String email, String old_password, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
/*
        if (user == null) {

            Toast.makeText(UpdateUser.this, "User may not exist or information entered is incorrect. ", Toast.LENGTH_LONG).show();
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(UpdateUser.this, UpdateUser.class));
            overridePendingTransition(0, 0);

       */
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, old_password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UpdateUser.this, "Password successfully changed! ", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(UpdateUser.this, Login.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(UpdateUser.this, "Password did not change. Try again! ", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        if (!task.isSuccessful())
                        {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException)
                                Toast.makeText(UpdateUser.this, "User does not exist. ", Toast.LENGTH_LONG).show();
                            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                Toast.makeText(UpdateUser.this, "User does not exist. ", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(UpdateUser.this, "User does not exist. ", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(UpdateUser.this, "Password did not change. Try again! ", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}