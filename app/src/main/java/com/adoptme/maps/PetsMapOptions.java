package com.adoptme.maps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Contains different options to configure a Pets map.
 */
public class PetsMapOptions {
    /**
     * If set to true, users can long click an arbitrary point in the map and put a new marker, which
     * will replace the existing one.
     */
    private boolean mCanChangeMarker = true;
    /**
     * If specified, the initial marker's location will be set to this value. Else, it will be set to
     * the user's location.
     */
    private LatLng mInitialLocation;

    public PetsMapOptions(boolean canChangeMarker, LatLng initialLocation) {
        mCanChangeMarker = canChangeMarker;
        mInitialLocation = initialLocation;
    }

    public PetsMapOptions() {
    }

    public boolean canChangeMarker() {
        return mCanChangeMarker;
    }

    public LatLng getInitialLocation() {
        return mInitialLocation;
    }
}
