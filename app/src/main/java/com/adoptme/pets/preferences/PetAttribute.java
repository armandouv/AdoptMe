package com.adoptme.pets.preferences;

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
        attributes.add(new PetAttribute("type"));
        attributes.add(new PetAttribute("size"));
        attributes.add(new PetAttribute("gender"));
        attributes.add(new PetAttribute("age"));
        attributes.add(new PetAttribute("color"));
        attributes.add(new PetAttribute("breed"));
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
}
