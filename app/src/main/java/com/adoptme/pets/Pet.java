package com.adoptme.pets;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adoptme.pets.preferences.PetAttribute;
import com.adoptme.pets.preferences.PreferencesMenuFragment;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.BinaryHttpResponseHandler;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * Represents a Pet for adoption.
 */
@ParseClassName("Pet")
public class Pet extends ParseObject {
    public static final String KEY_TYPE = "type";
    public static final String KEY_SIZE = "size";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_AGE = "age";
    public static final String KEY_COLOR = "color";
    public static final String KEY_NAME = "name";
    public static final String KEY_BREED = "breed";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_USER = "user";

    private static final String TAG = Pet.class.getSimpleName();

    private boolean mUserLiked;
    private int mLikesCount;
    private Date mPublishedAt;
    private boolean mIsPetFinderData = false;

    public Pet(String type, String size, String gender, String age, String color, String name,
               String breed, String description, LatLng location, File photoFile, ParseUser user) {
        setType(type);
        setSize(size);
        setGender(gender);
        setAge(age);
        setColor(color);
        setName(name);
        setBreed(breed);
        setDescription(description);
        setLocation(new ParseGeoPoint(location.latitude, location.longitude));
        setPhoto(new ParseFile(photoFile));
        setUser(user);
    }

    public Pet(JSONObject jsonObject, Context context, boolean async) throws JSONException {
        mIsPetFinderData = true;
        String UNKNOWN = "unknown";

        String type = jsonObject.has("type") ?
                jsonObject.getString("type") : UNKNOWN;

        String size = jsonObject.has("size") ?
                jsonObject.getString("size") : UNKNOWN;

        String gender = jsonObject.has("gender") ?
                jsonObject.getString("gender") : UNKNOWN;

        String age = jsonObject.has("age") ?
                jsonObject.getString("age") : UNKNOWN;

        JSONObject colorsObject = jsonObject.getJSONObject("colors");
        String color = colorsObject.getString("primary");
        if (color.equals("null")) color = UNKNOWN;

        String name = jsonObject.has("name") ?
                jsonObject.getString("name") : UNKNOWN;

        JSONObject breedsObject = jsonObject.getJSONObject("colors");
        String breed = breedsObject.getString("primary");
        if (breed.equals("null")) breed = UNKNOWN;

        JSONObject contactObject = jsonObject.getJSONObject("contact");
        if (async) setLocationInBackground(contactObject.getJSONObject("address"), context);
        else {
            ParseGeoPoint location = addressToGeoPoint(contactObject.getJSONObject("address"), context);
            setLocation(location);
        }

        String description = jsonObject.has("description") ?
                jsonObject.getString("description") : UNKNOWN;

        String publishedAt = jsonObject.getString("published_at");
        int dateLength = publishedAt.length();
        String formattedDate = publishedAt.substring(0, dateLength - 2) + ":" + publishedAt.substring(dateLength - 2);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        TemporalAccessor accessor = dateTimeFormatter.parse(formattedDate);
        setPublishedAt(Date.from(Instant.from(accessor)));

        ParseUser user = new ParseUser();
        user.setEmail(contactObject.getString("email"));
        String phone = contactObject.getString("phone").split(", ")[0];
        user.put("phone", phone.equals("null") ? UNKNOWN : phone);

        savePetPhoto(jsonObject);

        setType(type);
        setSize(size);
        setGender(gender);
        setAge(age);
        setColor(color);
        setName(name);
        setBreed(breed);
        setDescription(description);
        setUser(user);
    }

    public Pet() {
    }

    public boolean isPetFinderData() {
        return mIsPetFinderData;
    }

    private void setPublishedAt(Date publishedAt) {
        mPublishedAt = publishedAt;
    }

    public static List<Pet> fromJSONArrayAsync(JSONArray results, Context context) throws JSONException {
        return fromJSONArray(results, context, true);
    }

    public static List<Pet> fromJSONArray(JSONArray results, Context context) throws JSONException {
        return fromJSONArray(results, context, false);
    }

    public static List<Pet> fromJSONArray(JSONArray results, Context context, boolean async) throws JSONException {
        List<Pet> pets = new ArrayList<>();

        int length = results.length();
        for (int i = 0; i < length; i++) {
            pets.add(new Pet(results.getJSONObject(i), context, async));
        }

        return pets;
    }

    private String getStrAddress(JSONObject addressObject) throws JSONException {
        String strAddress = "";

        String address1 = addressObject.getString("address1");
        if (!address1.equals("null"))
            strAddress += address1 + ", ";

        strAddress += addressObject.getString("city") + ", "
                + addressObject.getString("state") + ", "
                + addressObject.getString("postcode") + ", "
                + addressObject.getString("country");

        return strAddress;
    }

    private void setLocationInBackground(JSONObject addressObject, Context context) {
        new Thread(() -> {
            ParseGeoPoint geoPoint = new ParseGeoPoint();
            try {
                geoPoint = addressToGeoPoint(addressObject, context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setLocation(geoPoint);
        }).start();
    }

    private ParseGeoPoint addressToGeoPoint(JSONObject addressObject, Context context) throws JSONException {
        String strAddress = getStrAddress(addressObject);

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        ParseGeoPoint geoPoint = new ParseGeoPoint();

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) return geoPoint;

            Address location = address.get(0);
            geoPoint.setLatitude(location.getLatitude());
            geoPoint.setLongitude(location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return geoPoint;
    }

    private void savePetPhoto(JSONObject jsonObject) throws JSONException {
        JSONArray photosArray = jsonObject.getJSONArray("photos");
        if (photosArray.length() == 0) return;

        String photoUrl = jsonObject.getJSONArray("photos")
                .getJSONObject(0).getString("medium");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(photoUrl, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, Response response) {
                ParseFile photo = null;
                try {
                    photo = new ParseFile(response.body().bytes());
                    photo.save();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Couldn't save image from PetFinder");
                }
                setPhoto(photo);
            }

            @Override
            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
                Log.e(TAG, "Couldn't download image from PetFinder");
            }
        });
    }

    public boolean matchesAllPreferences() {
        Map<String, String> petAttributes = getPreferencesAttributesMap();
        boolean atLeastOneAssigned = false;

        for (PetAttribute attribute : PreferencesMenuFragment.sAttributes) {
            if (attribute.isAssignedValueEmpty()) continue;

            atLeastOneAssigned = true;

            String petAttributeValue = petAttributes.get(attribute.getName());
            if (!attribute.getAssignedValue().equals(petAttributeValue))
                return false;
        }

        // If no preferences were specified, we don't consider it a match
        return atLeastOneAssigned;
    }

    public boolean isUserLiked() {
        return mUserLiked;
    }

    public void setUserLiked(boolean mUserLiked) {
        this.mUserLiked = mUserLiked;
    }

    public void toggleIsUserLiked() {
        mUserLiked = !mUserLiked;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public void setLikesCount(int mLikesCount) {
        this.mLikesCount = mLikesCount;
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public String getSize() {
        return getString(KEY_SIZE);
    }

    public void setSize(String size) {
        put(KEY_SIZE, size);
    }

    public String getGender() {
        return getString(KEY_GENDER);
    }

    public void setGender(String gender) {
        put(KEY_GENDER, gender);
    }

    public String getAge() {
        return getString(KEY_AGE);
    }

    public void setAge(String age) {
        put(KEY_AGE, age);
    }

    public String getColor() {
        return getString(KEY_COLOR);
    }

    public void setColor(String color) {
        put(KEY_COLOR, color);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getBreed() {
        return getString(KEY_BREED);
    }

    public void setBreed(String breed) {
        put(KEY_BREED, breed);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getPhoto() {
        return getParseFile(KEY_PHOTO);
    }

    public void setPhoto(ParseFile photo) {
        put(KEY_PHOTO, photo);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    @Override
    public Date getCreatedAt() {
        Date createdAt = super.getCreatedAt();
        return createdAt != null ?
                createdAt : mPublishedAt;
    }

    public static List<String> getPreferencesAttributes() {
        List<String> attributes = new ArrayList<>();

        attributes.add(KEY_TYPE);
        attributes.add(KEY_SIZE);
        attributes.add(KEY_GENDER);
        attributes.add(KEY_AGE);
        attributes.add(KEY_COLOR);
        attributes.add(KEY_BREED);

        return attributes;
    }

    public Map<String, String> getPreferencesAttributesMap() {
        Map<String, String> attributes = new HashMap<>();

        for (String attributeName : Pet.getPreferencesAttributes()) {
            attributes.put(attributeName, getString(attributeName));
        }

        return attributes;
    }

    public LatLng getLocationAsLatLng() {
        ParseGeoPoint location = getLocation();
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private String getFormatted(String value) {
        if (value.isEmpty()) return value;
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public String getFormattedType() {
        return getFormatted(getType());
    }

    public String getFormattedSize() {
        return getFormatted(getSize());
    }

    public String getFormattedGender() {
        return getFormatted(getGender());
    }

    public String getFormattedAge() {
        return getFormatted(getAge());
    }

    public String getFormattedName() {
        return getFormatted(getName());
    }

    public String getFormattedBreed() {
        return getFormatted(getBreed());
    }

    public String getFormattedDescription() {
        return getFormatted(getDescription());
    }

    public String getFormattedColor() {
        return getFormatted(getColor());
    }
}
