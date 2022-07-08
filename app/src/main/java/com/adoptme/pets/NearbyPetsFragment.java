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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.slider.Slider;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Displays nearby pets up to a certain radius relative to the user's location. Radius can be
 * changed.
 */
public class NearbyPetsFragment extends PetsMapContainerFragment {
    private final static int PAGE_SIZE = 2;
    private final static String TAG = NearbyPetsFragment.class.getSimpleName();
    private final static int DEFAULT_RADIUS_IN_METERS = 5000;
    private final static int MINIMUM_RADIUS_IN_METERS = 1000;
    private final static int MAXIMUM_RADIUS_IN_METERS = 100000;

    private int mPageNumberToLoad = 0;
    private final List<Pet> mPets = new ArrayList<>();
    private final Map<Marker, Pet> mMarkerToPet = new HashMap<>();
    private Circle mCurrentCircle;
    private FragmentNearbyPetsBinding mBinding;
    private double mCurrentRadiusInMeters = DEFAULT_RADIUS_IN_METERS;

    public NearbyPetsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setOnMarkerClickListener(marker -> {
            // If it's the user's marker, don't launch the pet fragment.
            // TODO: Launch user profile (stretch feature)
            if (marker.equals(getMarker())) return false;

            PetDetailsFragment.launch(getFragmentManager(), mMarkerToPet.get(marker));
            return true;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.radiusSlider.setValueFrom(MINIMUM_RADIUS_IN_METERS);
        mBinding.radiusSlider.setValueTo(MAXIMUM_RADIUS_IN_METERS);
        mBinding.radiusSlider.setValue(DEFAULT_RADIUS_IN_METERS);

        mBinding.radiusSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                resetMap(slider.getValue());
            }
        });

        mBinding.radiusSlider.addOnChangeListener((slider, value, fromUser) -> {
            mCurrentRadiusInMeters = value;
            refreshCircle();
        });
        mBinding.loadMoreButton.setOnClickListener(v -> queryAndRefreshPets());
    }

    private void resetMap(double radiusInMeters) {
        mCurrentRadiusInMeters = radiusInMeters;
        mPageNumberToLoad = 0;
        mBinding.loadMoreButton.setVisibility(View.VISIBLE);
        mBinding.radiusSlider.setValue((float) mCurrentRadiusInMeters);
        queryAndRefreshPets();
    }


    @Override
    public void onMarkerUpdated() {
        resetMap(DEFAULT_RADIUS_IN_METERS);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentNearbyPetsBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    private void queryAndRefreshPets() {
        ParseQuery<Pet> query = ParseQuery.getQuery(Pet.class);
        query.include(Pet.KEY_USER);
        query.addDescendingOrder("createdAt");

        // Pagination
        query.setSkip(PAGE_SIZE * mPageNumberToLoad);
        // Query one more element to know if it's the last page.
        query.setLimit(PAGE_SIZE + 1);

        LatLng latLng = getLocation();
        query.whereWithinKilometers(Pet.KEY_LOCATION,
                new ParseGeoPoint(latLng.latitude, latLng.longitude), mCurrentRadiusInMeters / 1000);

        query.findInBackground((pets, e) -> {
            if (e != null) {
                Log.e(TAG, "Could not query pets", e);
                return;
            }

            if (mPageNumberToLoad == 0) mPets.clear();
            mPets.addAll(pets);

            mPageNumberToLoad++;

            // This means that the queried page is the last one.
            if (pets.size() != PAGE_SIZE + 1) mBinding.loadMoreButton.setVisibility(View.GONE);
                // Don't display the last pet, since it'll be displayed in the subsequent page.
            else mPets.remove(mPets.size() - 1);

            refreshPets();
        });
    }

    private void refreshCircle() {
        if (mCurrentCircle != null) mCurrentCircle.remove();
        // Set up map zoom according to radius. Keep track of drawn circle for future deletion.
        mCurrentCircle = setZoom(mCurrentRadiusInMeters);
    }

    private void refreshPets() {
        for (Marker marker : mMarkerToPet.keySet()) marker.remove();
        mMarkerToPet.clear();

        // Create a pet marker for each pet and add them to the map. Keep track of markers for future
        // deletion in mMarkerToPet, as well as their corresponding pets for usage in onMapReady's
        // click handler.
        for (Pet pet : mPets) {
            Marker marker = addPetMarker(pet);
            mMarkerToPet.put(marker, pet);
        }

        refreshCircle();
    }
}