package com.adoptme.pets.preferences;

import com.adoptme.pets.Pet;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pet attribute with an assigned value, which could be none (the empty string).
 */
public class PetAttribute {
    private String mName;
    private String mAssignedValue;

    public PetAttribute(String name) {
        mName = name;
        mAssignedValue = "";
    }

    public PetAttribute(String name, String assignedValue) {
        mName = name;
        mAssignedValue = assignedValue;
    }

    public static List<PetAttribute> getDefaultAttributes() {
        List<PetAttribute> attributes = new ArrayList<>();

        for (String attributeName : Pet.getPreferencesAttributes())
            attributes.add(new PetAttribute(attributeName));

        return attributes;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAssignedValue() {
        return mAssignedValue;
    }

    public void setAssignedValue(String assignedValue) {
        mAssignedValue = assignedValue;
    }

    public boolean isAssignedValueEmpty() {
        return mAssignedValue.isEmpty();
    }
}
