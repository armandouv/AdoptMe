package com.adoptme.pets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adoptme.R;
import com.adoptme.maps.PetsMapContainerFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * Displays nearby pets up to a certain radius relative to the user's location. Radius can be
 * changed.
 */
public class NearbyPetsFragment extends PetsMapContainerFragment {
    private final int mPageNumberToLoad = 1;
    private final static int PAGE_SIZE = 30;
    private final static String TAG = NearbyPetsFragment.class.getSimpleName();
    private final List<Pet> mPets = new ArrayList<>();

    public NearbyPetsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby_pets, container, false);
    }

    private void queryPets(int radius) {
        ParseQuery<Pet> query = ParseQuery.getQuery(Pet.class);
        query.include(Pet.KEY_USER);
        query.addDescendingOrder("createdAt");

        // Pagination
        query.setSkip(PAGE_SIZE * (mPageNumberToLoad - 1));
        query.setLimit(PAGE_SIZE);

        LatLng latLng = getLocation();
        query.whereWithinMiles(Pet.KEY_LOCATION,
                new ParseGeoPoint(latLng.latitude, latLng.longitude), radius);

        query.findInBackground((pets, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query pets", e);
                return;
            }

            mPets.addAll(pets);
        });
    }
}