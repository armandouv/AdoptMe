package com.adoptme.pets;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.adoptme.MainActivity;
import com.adoptme.databinding.FragmentPostPetBinding;
import com.adoptme.maps.PetsMapContainerFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Pet posting screen.
 */
public class PostPetFragment extends PetsMapContainerFragment {

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final String APP_TAG = PostPetFragment.class.getSimpleName();
    private FragmentPostPetBinding mBinding;
    private File mPhotoFile;

    public PostPetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPostPetBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    private void displayMissingAttributesToast() {
        Toast.makeText(getContext(), "Missing attributes", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.submitImage.setOnClickListener(v -> {
            List<String> missingAttributes = new ArrayList<>();

            String type = mBinding.inputType
                    .getText()
                    .toString();
            if (type.isEmpty()) missingAttributes.add("Type");

            String size = mBinding.inputSize
                    .getText()
                    .toString();
            if (size.isEmpty()) missingAttributes.add("Size");

            String gender = mBinding.inputGender
                    .getText()
                    .toString();
            if (gender.isEmpty()) missingAttributes.add("Gender");

            String age = mBinding.inputAge
                    .getText()
                    .toString();
            if (age.isEmpty()) missingAttributes.add("Age");

            String color = mBinding.inputColor
                    .getText()
                    .toString();
            if (color.isEmpty()) missingAttributes.add("Color");

            String name = mBinding.inputName
                    .getText()
                    .toString();
            if (name.isEmpty()) missingAttributes.add("Name");

            String breed = mBinding.inputBreed
                    .getText()
                    .toString();
            if (breed.isEmpty()) missingAttributes.add("Breed");

            String description = mBinding.inputDescription
                    .getText()
                    .toString();
            if (description.isEmpty()) missingAttributes.add("Description");

            if (!missingAttributes.isEmpty()) {
                displayMissingAttributesToast();
                // TODO: Display missing attributes.
                return;
            }

            if (getMarker() == null) {
                Toast.makeText(getContext(), "Location must be specified", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mPhotoFile == null || mBinding.inputImage.getDrawable() == null) {
                Toast.makeText(getContext(), "You must upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng currentLocation = getMarker().getPosition();
            ParseUser user = ParseUser.getCurrentUser();

            Pet pet = new Pet(type, size, gender, age, color, name, breed, description,
                    currentLocation, mPhotoFile, user);

            pet.saveInBackground(e -> {
                if (e != null) {
                    Toast.makeText(getContext(), "Could not create pet", Toast.LENGTH_SHORT).show();
                    return;
                }

                mBinding.inputType.setText("");
                mBinding.inputSize.setText("");
                mBinding.inputGender.setText("");
                mBinding.inputAge.setText("");
                mBinding.inputColor.setText("");
                mBinding.inputName.setText("");
                mBinding.inputBreed.setText("");
                mBinding.inputDescription.setText("");
                mBinding.inputImage.setImageResource(0);
                Toast.makeText(getContext(), "Pet created successfully", Toast.LENGTH_LONG).show();

                ((MainActivity) getActivity()).switchToPetsTimeline();
            });
        });

        mBinding.uploadImage.setOnClickListener(v -> launchCamera());
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoFile = getPhotoFileUri("photo.jpg");

        Uri fileProvider = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(requireContext().getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                mBinding.inputImage.setImageBitmap(takenImage);
            } else
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }
}