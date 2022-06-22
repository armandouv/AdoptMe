package com.adoptme.users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adoptme.MainActivity;
import com.adoptme.databinding.ActivitySignupBinding;
import com.parse.ParseUser;

/**
 * Allows a User to sign up.
 */
public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding mBinding;

    /**
     * Launches a SignupActivity from the specified source activity.
     *
     * @param activity The source activity in which the SignupActivity will start.
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SignupActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            MainActivity.launchAndClear(this);
            return;
        }

        mBinding.signupButton.setOnClickListener((view) -> {
            Editable username = mBinding.usernameSignup.getText();
            Editable password = mBinding.passwordSignup.getText();

            if (username.toString().isEmpty()) {
                Toast.makeText(this, "Username must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.toString().isEmpty()) {
                Toast.makeText(this, "Password must not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

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
            MainActivity.launchAndClear(this);
        });
    }
}