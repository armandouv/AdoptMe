package com.adoptme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.adoptme.databinding.ActivityMainBinding;


/**
 * Displays different Fragments depending on the option selected by the user in the navigation menu.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                // TODO: Assign fragments.
            }

            //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return true;
        });

        mBinding.bottomNavigation.setSelectedItemId(R.id.home_icon);
    }
}