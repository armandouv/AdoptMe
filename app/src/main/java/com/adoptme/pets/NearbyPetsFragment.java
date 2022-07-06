package com.adoptme.pets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adoptme.databinding.FragmentNearbyPetsBinding;
import com.adoptme.maps.PetsMapContainerFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * Displays nearby pets up to a certain radius relative to the user's location. Radius can be
 * changed.
 */
public class NearbyPetsFragment extends PetsMapContainerFragment {
    private final static int PAGE_SIZE = 30;
    private final static String TAG = NearbyPetsFragment.class.getSimpleName();
    private final static int DEFAULT_RADIUS_IN_METERS = 5000;
    private final static int MINIMUM_RADIUS_IN_METERS = 1000;
    private final static int MAXIMUM_RADIUS_IN_METERS = 100000;

    private final int mPageNumberToLoad = 0;
    private final List<Pet> mPets = new ArrayList<>();
    private final List<Marker> mPetMarkers = new ArrayList<>();
    private Circle mCurrentRadius;
    private FragmentNearbyPetsBinding mBinding;

    public NearbyPetsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.radiusSlider.setValueFrom(MINIMUM_RADIUS_IN_METERS);
        mBinding.radiusSlider.setValueTo(MAXIMUM_RADIUS_IN_METERS);
        mBinding.radiusSlider.setValue(DEFAULT_RADIUS_IN_METERS);
        mBinding.radiusSlider.addOnChangeListener((slider, value, fromUser) -> queryPets(value));
    }

    @Override
    public void onMarkerUpdated() {
        queryPets(DEFAULT_RADIUS_IN_METERS);

        mBinding.radiusSlider.setValue(DEFAULT_RADIUS_IN_METERS);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentNearbyPetsBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    private void queryPets(double radiusInMeters) {
        ParseQuery<Pet> query = ParseQuery.getQuery(Pet.class);
        query.include(Pet.KEY_USER);
        query.addDescendingOrder("createdAt");

        // Pagination
        query.setSkip(PAGE_SIZE * mPageNumberToLoad);
        query.setLimit(PAGE_SIZE);

        LatLng latLng = getLocation();
        query.whereWithinKilometers(Pet.KEY_LOCATION,
                new ParseGeoPoint(latLng.latitude, latLng.longitude), radiusInMeters / 1000);

        query.findInBackground((pets, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query pets", e);
                return;
            }

            if (mPageNumberToLoad == 0) mPets.clear();
            mPets.addAll(pets);

            refreshPets(radiusInMeters);
        });
    }

    private void refreshPets(double radiusInMeters) {
        if (mCurrentRadius != null) mCurrentRadius.remove();

        mPetMarkers.forEach(Marker::remove);
        mPetMarkers.clear();

        // Create a pet marker for each pet and add them to the map. Keep track of markers for future
        // deletion in mPetMarkers.
        mPets.stream()
                .map(this::addPetMarker)
                .forEach(mPetMarkers::add);

        // Set up map zoom according to radius. Keep track of drawn circle for future deletion.
        mCurrentRadius = setZoom(radiusInMeters);
    }
}