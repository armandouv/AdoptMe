package com.adoptme.maps;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.adoptme.R;
import com.adoptme.pets.Pet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


/**
 * An abstract fragment that contains a configurable (see PetsMapOptions) Pets map. The map will
 * contain an initial marker, whose initial location will depend on the given configuration (can be
 * set to a specific LatLng or to the user's location). Functionality to replace the existing marker
 * by long-clicking on an arbitrary location can also be specified to be active or not.
 */
@RuntimePermissions
public abstract class PetsMapContainerFragment extends Fragment {
    private PetsMapOptions mOptions = new PetsMapOptions();
    private GoogleMap mMap;
    private Marker mCurrentMarker;
    private static final long DAY_IN_MILLISECONDS = 1000L * 60L * 60L * 24L;
    private static final long ADOPTION_INTERVAL_IN_MILLISECONDS = DAY_IN_MILLISECONDS * 10L;

    public void setOptions(PetsMapOptions options) {
        mOptions = options;
    }

    public void setMap(GoogleMap map) {
        mMap = map;
    }

    public Marker getMarker() {
        return mCurrentMarker;
    }

    public void setMarker(Marker marker) {
        mCurrentMarker = marker;
    }

    /**
     * The map fragment corresponding to this container. Its id must be "map".
     *
     * @return The map fragment corresponding to this container
     */
    public SupportMapFragment getMapFragment() {
        return (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void loadMap(PetsMapOptions options) {
        boolean canChangeMarker = options.canChangeMarker();
        LatLng initialLocation = options.getInitialLocation();

        setUpMap(canChangeMarker, initialLocation);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PetsMapContainerFragmentPermissionsDispatcher.loadMapWithPermissionCheck(this, mOptions);
    }


    /**
     * Obtains the user location and sets the marker to it.
     */
    @SuppressLint("MissingPermission")
    private void setUserLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Set up initial marker in the user's location,
        getFusedLocationProviderClient(requireContext()).getLastLocation()
                .addOnSuccessListener(location -> {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    updateMarker(userLocation);
                });
    }

    public void onMapReady(GoogleMap map) {
    }

    /**
     * Sets up the map in the specified container. A handler to change the marker on a long click
     * will be set if canChangeMarker is true. The initial marker will be set at initialLocation if
     * it is not null. Otherwise, the initial marker will be set to the user's current
     * location. Location permissions must have been given before this method is executed.
     *
     * @param canChangeMarker Whether this map will support marker changing on a long click.
     * @param initialLocation Location of the initial marker.
     */
    public void setUpMap(boolean canChangeMarker, LatLng initialLocation) {
        getMapFragment().getMapAsync(map -> {
            setMap(map);

            if (canChangeMarker)
                map.setOnMapLongClickListener(this::updateMarker);

            if (initialLocation != null) updateMarker(initialLocation);
            else setUserLocation();

            onMapReady(map);
        });
    }

    public void onMarkerUpdated() {
    }

    /**
     * Replaces the current marker with one located at latLng.
     *
     * @param latLng Location of the new marker.
     */
    private void updateMarker(LatLng latLng) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        options.position(latLng);
        options.zIndex(1);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        if (mCurrentMarker != null) mCurrentMarker.remove();
        setMarker(mMap.addMarker(options));
        onMarkerUpdated();
    }

    public Circle setZoom(double radiusInMeters) {
        CircleOptions options = new CircleOptions().center(getLocation())
                .radius(radiusInMeters).strokeColor(Color.RED);
        Circle circle = mMap.addCircle(options);
        circle.setVisible(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLocation(), getZoomLevel(circle)));
        return circle;
    }

    private int getZoomLevel(Circle circle) {
        double radius = circle.getRadius();
        double scale = radius / 500;
        return (int) (16 - Math.log(scale) / Math.log(2));
    }

    private BitmapDescriptor bitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Marker addPetMarker(Pet pet) {
        MarkerOptions options = new MarkerOptions();
        options.position(pet.getLocationAsLatLng());

        if (pet.matchesAllPreferences())
            options.icon(bitmapFromVector(getContext(), R.drawable.ic_baseline_star_24));
        else
            options.icon(BitmapDescriptorFactory.defaultMarker(getUrgencyColor(pet)));

        return mMap.addMarker(options);
    }

    private float getUrgencyColor(Pet pet) {
        Date createdAt = pet.getCreatedAt();
        long timeElapsedInMilliseconds = Math.abs(System.currentTimeMillis() - createdAt.getTime());

        if (timeElapsedInMilliseconds > ADOPTION_INTERVAL_IN_MILLISECONDS)
            return BitmapDescriptorFactory.HUE_RED;

        double colorInterval = Math.abs(BitmapDescriptorFactory.HUE_GREEN - BitmapDescriptorFactory.HUE_RED);
        double urgencyFactor = (double) timeElapsedInMilliseconds / ADOPTION_INTERVAL_IN_MILLISECONDS;

        // Assuming HUE_GREEN is larger than HUE_RED
        return BitmapDescriptorFactory.HUE_GREEN - (float) (urgencyFactor * colorInterval);
    }

    public LatLng getLocation() {
        return mCurrentMarker.getPosition();
    }
}
