package com.adoptme.pets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adoptme.R;
import com.adoptme.databinding.FragmentPetsTimelineBinding;
import com.adoptme.pets.preferences.PreferencesMenuFragment;
import com.adoptme.users.LoginActivity;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the 500 most recent Posts.
 */
public class PetsTimelineFragment extends Fragment {

    public static final String TAG = PetsTimelineFragment.class.getSimpleName();
    protected final List<Pet> mPets = new ArrayList<>();
    protected FragmentPetsTimelineBinding mBinding;
    protected PetsAdapter mPetsAdapter;

    public PetsTimelineFragment() {
        // Required empty public constructor
    }

    protected void populatePets(boolean isRefreshing) {
        ParseQuery<Pet> query = ParseQuery.getQuery(Pet.class);
        query.setLimit(500);
        query.addDescendingOrder("createdAt");

        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query pet", e);
                return;
            }

            if (isRefreshing) {
                mPets.clear();
                mBinding.swipeContainer.setRefreshing(false);
            }
            mPets.addAll(posts);
            mPetsAdapter.notifyDataSetChanged();
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                    goLoginActivity();
                }));

        mBinding.preferencesButton.setOnClickListener(v -> {
            PreferencesMenuFragment fragment = new PreferencesMenuFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        mPetsAdapter = new PetsAdapter(getContext(), mPets, (v, position) -> {
            // TODO: Go to PetDetailsActivity
        });

        mBinding.swipeContainer.setOnRefreshListener(() -> populatePets(true));

        mBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mBinding.postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.postsView.setAdapter(mPetsAdapter);

        // Only populate timeline once (refreshing will update it)
        if (mPets.isEmpty()) populatePets(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPetsTimelineBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }
}