package com.adoptme.pets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adoptme.R;
import com.adoptme.databinding.FragmentPetDetailsBinding;
import com.adoptme.maps.PetsMapContainerFragment;
import com.adoptme.maps.PetsMapOptions;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import es.dmoral.toasty.Toasty;

/**
 * Displays a detailed view of a Pet.
 */
public class PetDetailsFragment extends PetsMapContainerFragment {

    private final GestureDetector mGestureDetector = new GestureDetector(getContext(), new DoubleTapListener());
    private Pet mPet;
    private FragmentPetDetailsBinding mBinding;

    public PetDetailsFragment() {
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseFile photo = mPet.getPhoto();
        Glide.with(requireContext())
                .load(photo != null ? photo.getUrl() : null)
                .placeholder(R.drawable.placeholder)
                .into(mBinding.petPhoto);

        if (mPet.isPetFinderData()) hideLikes();
        else setUpLikesFunctionality();

        mBinding.petType.setText(mPet.getFormattedType());
        mBinding.petSize.setText(mPet.getFormattedSize());
        mBinding.petGender.setText(mPet.getFormattedGender());
        mBinding.petAge.setText(mPet.getFormattedAge());
        mBinding.petColor.setText(mPet.getFormattedColor());
        mBinding.petBreed.setText(mPet.getFormattedBreed());

        mBinding.petDescription.setText(mPet.getFormattedDescription());

        mBinding.contactEmail.setText(mPet.getUser().getEmail());
        mBinding.contactPhone.setText(mPet.getUser().getString("phone"));
    }

    void hideLikes() {
        mBinding.petLikes.setVisibility(View.GONE);
        mBinding.likeButton.setVisibility(View.GONE);
    }

    private void toggleLike() {
        ParseRelation<ParseUser> relation = mPet.getRelation("users");
        ParseUser user = ParseUser.getCurrentUser();
        int likesCount = mPet.getLikesCount();

        if (mPet.isUserLiked()) {
            relation.remove(user);
            mPet.setLikesCount(likesCount - 1);
        } else {
            relation.add(user);
            mPet.setLikesCount(likesCount + 1);
        }

        mPet.saveInBackground(e -> {
            if (e != null)
                Toasty.error(requireContext(), "Error while liking or unliking the pet").show();

            mPet.toggleIsUserLiked();
            mBinding.petLikes.setText(getString(R.string.likes, mPet.getLikesCount()));
            mBinding.likeButton.setImageResource(mPet.isUserLiked() ?
                    R.drawable.heart_filled : R.drawable.heart_outline);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPetDetailsBinding.inflate(getLayoutInflater());

        mPet = this.getArguments().getParcelable(Pet.class.getSimpleName());
        // Set up map
        ParseGeoPoint location = mPet.getLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        PetsMapOptions options = new PetsMapOptions(false, latLng);
        setOptions(options);

        return mBinding.getRoot();
    }

    private void setLikesCount() {
        mPet.getRelation("users").getQuery()
                .countInBackground((likesCount, e1) -> {
                    mPet.setLikesCount(likesCount);
                    mBinding.petLikes.setText(getString(R.string.likes, likesCount));
                    // Allow user to toggle like only after loading the current liked state and likes count.
                    mBinding.likeButton.setOnClickListener(view -> toggleLike());
                });
    }

    /**
     * Sets up likes functionality as follows:
     * 1. Load whether the current user has liked the pet.
     * 2. Load the number of likes the pet has.
     * 3. Set up like toggling. This is done at the end because when a like is toggled, the liked
     * status will change, as well as the number of likes the pet has. Thus, these data must be
     * known before the updates happen.
     */
    private void setUpLikesFunctionality() {
        mBinding.petPhoto.setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));

        ParseUser user = ParseUser.getCurrentUser();
        mPet.getRelation("users").getQuery()
                .whereEqualTo(ParseUser.KEY_OBJECT_ID, user.getObjectId())
                .countInBackground((count, e) -> {
                    mPet.setUserLiked(count == 1);
                    mBinding.likeButton.setImageResource(mPet.isUserLiked() ?
                            R.drawable.heart_filled : R.drawable.heart_outline);

                    setLikesCount();
                });
    }

    private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            toggleLike();
            return true;
        }
    }
}