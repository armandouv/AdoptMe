package com.adoptme.users;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adoptme.MainActivity;
import com.adoptme.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

/**
 * Allows a User to log in.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
            finish();
            return;
        }

        mBinding.loginButton.setOnClickListener((view) -> {
            Editable username = mBinding.username.getText();
            Editable password = mBinding.password.getText();

            if (username.toString().isEmpty()) {
                Toast.makeText(this, "Username must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.toString().isEmpty()) {
                Toast.makeText(this, "Password must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username.toString(), password.toString());

            username.clear();
            password.clear();
        });

        mBinding.goSignupButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Toast.makeText(this, "Couldn't log in", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            goMainActivity();
            finish();
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}