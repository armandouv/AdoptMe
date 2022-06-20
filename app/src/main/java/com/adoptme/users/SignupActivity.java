package com.adoptme.users;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adoptme.databinding.ActivitySignupBinding;
import com.parse.ParseUser;

/**
 * Allows a User to sign up.
 */
public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            // TODO: Go to Main Activity
        }

        mBinding.signupButton.setOnClickListener((view) -> {
            Editable username = mBinding.usernameSignup.getText();
            Editable password = mBinding.passwordSignup.getText();
            signupUser(username.toString(), password.toString());

            username.clear();
            password.clear();
        });
    }

    private void signupUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(e -> {
            if (e != null) {
                Toast.makeText(this, "Couldn't sign up", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            // TODO: Go to Main Activity
        });
    }
}