package com.adoptme.pets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoptme.databinding.FragmentPetsTimelineBinding;
import com.adoptme.pets.preferences.PetAttribute;
import com.adoptme.pets.preferences.PreferencesMenuFragment;
import com.adoptme.users.LoginActivity;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays the 500 most recent Posts.
 */
public class PetsTimelineFragment extends Fragment {

    public static final String TAG = PetsTimelineFragment.class.getSimpleName();
    protected final List<Pet> mPets = new ArrayList<>();
    protected FragmentPetsTimelineBinding mBinding;
    protected PetsAdapter mPetsAdapter;

    // If set to false, the preferences menu will be hidden and ranking will not be performed.
    private boolean mIsRankingActive = true;

    public PetsTimelineFragment() {
        // Required empty public constructor
    }

    public void setRankingActive(boolean isRankingActive) {
        mIsRankingActive = isRankingActive;
    }

    /**
     * Populates the 500 most recent pets and sorts them according to the specified user preferences.
     *
     * @param isRefreshing Whether the query is done to serve a refresh or it's the initial one.
     */
    protected void populatePetsAndSort(boolean isRefreshing) {
        ParseQuery<Pet> query = ParseQuery.getQuery(Pet.class);
        query.include(Pet.KEY_USER);
        query.setLimit(500);
        query.addDescendingOrder("createdAt");

        query.findInBackground((pets, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query pets", e);
                return;
            }

            if (isRefreshing) {
                mPets.clear();
                mBinding.swipeContainer.setRefreshing(false);
            }

            mPets.addAll(pets);
            // Sort after data arrives
            sortPetsByPreferences();

            mPetsAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.logoutButton.setOnClickListener(v ->
                ParseUser.logOutInBackground(e -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Couldn't log out", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                    LoginActivity.launchAndClear(getActivity());
                }));

        // Set up or hide preferences menu
        if (mIsRankingActive)
            mBinding.preferencesButton.setOnClickListener(v ->
                    PreferencesMenuFragment.launch(getFragmentManager()));
        else mBinding.preferencesButton.setVisibility(View.GONE);

        mPetsAdapter = new PetsAdapter(getContext(), mPets, (v, position) -> {
            Pet pet = mPets.get(position);
            PetDetailsFragment.launch(getFragmentManager(), pet);
        });

        mBinding.swipeContainer.setOnRefreshListener(() -> populatePetsAndSort(true));

        mBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mBinding.postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.postsView.setAdapter(mPetsAdapter);

        // Only populate timeline once (refreshing will update it if needed)
        if (mPets.isEmpty()) {
            // Note: sorting is done inside populatePetsAndSort after data arrives.
            populatePetsAndSort(false);
            return;
        }

        // Sort existing data (only needed if ranking is performed, since user preferences may change)
        if (mIsRankingActive) sortPetsByPreferences();
    }

    private void sortPetsByPreferences() {
        Map<Pet, Integer> scores = computePetScores();

        // Sort in decreasing order by score (the larger the score is, the better the match with
        // user preferences). If a Pet does not have a score assigned in the map, it will be 0 by
        // default.
        Sorting.sort(mPets, (pet1, pet2) -> scores.get(pet2) - scores.get(pet1));
    }

    private Map<Pet, Integer> computePetScores() {
        Map<Pet, Integer> scores = new HashMap<>();

        for (Pet pet : mPets) {
            // Since attribute names in PetAttribute are not hardcoded, we first need to convert
            // each Pet's attributes to a map (attributeName -> attributeValue), so that we can
            // compare their values.
            Map<String, String> petAttributes = pet.getPreferencesAttributesMap();

            int score = 0, increase = (int) Math.pow(2, PreferencesMenuFragment.sAttributes.size() + 1);
            for (PetAttribute attribute : PreferencesMenuFragment.sAttributes) {
                increase /= 2;

                // If the attribute was not assigned a value it will not increase the score.
                if (attribute.isAssignedValueEmpty()) continue;

                // Check if the preferred attribute value matches the pet's value. If it doesn't,
                // it will not increase the score.
                String petAttributeValue = petAttributes.get(attribute.getName());
                if (!attribute.getAssignedValue().equals(petAttributeValue)) continue;

                // Increase score according to the attribute's position.
                score += increase;
            }
            scores.put(pet, score);
        }

        return scores;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPetsTimelineBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }
}