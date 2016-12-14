package com.example.smit.fbpage.model;

public class Profile {

    private String profileId, profileName;

    public Profile(String id, String name)
    {
        profileId = id;
        profileName = name;
    }

    public Profile(){}

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
