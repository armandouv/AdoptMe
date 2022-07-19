package com.adoptme.users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;

import androidx.appcompat.app.AppCompatActivity;

import com.adoptme.MainActivity;
import com.adoptme.databinding.ActivitySignupBinding;
import com.parse.ParseUser;

import es.dmoral.toasty.Toasty;

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
                Toasty.error(this, "Username must not be empty").show();
                return;
            }

            if (password.toString().isEmpty()) {
                Toasty.error(this, "Password must not be empty").show();
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
                Toasty.error(this, "Couldn't sign up").show();
                return;
            }

            Toasty.success(this, "Signed up successfully!").show();
            MainActivity.launchAndClear(this);
        });
    }
}