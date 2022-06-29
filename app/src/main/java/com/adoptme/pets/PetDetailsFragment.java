package com.adoptme.pets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adoptme.R;
import com.adoptme.databinding.FragmentPetDetailsBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

/**
 * Displays a detailed view of a Pet.
 */
public class PetDetailsFragment extends Fragment {

    private FragmentPetDetailsBinding mBinding;

    public PetDetailsFragment() {
        // Required empty public constructor
    }

    public static void launch(FragmentManager fragmentManager, Pet pet) {
        PetDetailsFragment fragment = new PetDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Pet.class.getSimpleName(), pet);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Pet pet = this.getArguments().getParcelable(Pet.class.getSimpleName());

        mBinding.petName.setText(pet.getFormattedName());
        Glide.with(requireContext())
                .load(pet.getPhoto().getUrl())
                .into(mBinding.petPhoto);
        mBinding.petType.setText(pet.getFormattedType());
        mBinding.petSize.setText(pet.getFormattedSize());
        mBinding.petGender.setText(pet.getFormattedGender());
        mBinding.petAge.setText(pet.getFormattedAge());
        mBinding.petColor.setText(pet.getFormattedColor());
        mBinding.petBreed.setText(pet.getFormattedBreed());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.pet_location);
        mapFragment.getMapAsync(googleMap -> {
            ParseGeoPoint location = pet.getLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Pet location"));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });

        mBinding.petDescription.setText(pet.getFormattedDescription());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPetDetailsBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }
}