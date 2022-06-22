package com.adoptme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.adoptme.databinding.ActivityMainBinding;
import com.adoptme.pets.MyPetsFragment;
import com.adoptme.pets.NearbyPetsFragment;
import com.adoptme.pets.PetsTimelineFragment;
import com.adoptme.pets.PostPetFragment;


/**
 * Displays different Fragments depending on the option selected by the user in the navigation menu.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int itemId = item.getItemId();
            if (itemId == R.id.home_icon) {
                fragment = new MyPetsFragment();
            } else if (itemId == R.id.pets_icon) {
                fragment = new PetsTimelineFragment();
            } else if (itemId == R.id.pets_map_icon) {
                fragment = new NearbyPetsFragment();
            } else if (itemId == R.id.compose_icon) {
                fragment = new PostPetFragment();
            } else {
                fragment = new PetsTimelineFragment();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return true;
        });

        binding.bottomNavigation.setSelectedItemId(R.id.home_icon);
    }

    /**
     * Starts the MainActivity and clears all previously started activities.
     *
     * @param activity The source activity in which the MainActivity will start.
     */
    public static void launchAndClear(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }
}