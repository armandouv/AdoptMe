package com.adoptme.pets;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
}
