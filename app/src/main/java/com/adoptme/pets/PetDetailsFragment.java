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
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Pet pet = this.getArguments().getParcelable(Pet.class.getSimpleName());

        mBinding.petName.setText(pet.getName());
        Glide.with(requireContext())
                .load(pet.getPhoto().getUrl())
                .into(mBinding.petPhoto);
        mBinding.petType.setText(pet.getType());
        mBinding.petSize.setText(pet.getSize());
        mBinding.petGender.setText(pet.getGender());
        mBinding.petAge.setText(pet.getAge());
        mBinding.petColor.setText(pet.getColor());
        mBinding.petBreed.setText(pet.getBreed());
        // Location
        mBinding.petDescription.setText(pet.getDescription());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPetDetailsBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }
}