package com.adoptme;

import android.app.Application;

import com.adoptme.pets.Pet;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Represents the AdoptMe app.
 */
public class AdoptMeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Set up Parse
        ParseObject.registerSubclass(Pet.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.APP_ID)
                .clientKey(BuildConfig.CLIENT_KEY)
                .server(BuildConfig.SERVER_URL)
                .build());
    }
}

