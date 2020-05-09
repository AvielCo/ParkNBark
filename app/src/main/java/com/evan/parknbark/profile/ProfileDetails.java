package com.evan.parknbark.profile;

import android.net.Uri;

public class ProfileDetails extends Profile {
    private Uri profilePicture;

    public ProfileDetails(String dogName, String dogBreed, String dogAge, Uri profilePicture) {
        super(dogName, dogBreed, dogAge);
        setProfilePicture(profilePicture);
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    private void setProfilePicture(Uri profilePicture) {
        this.profilePicture = profilePicture;
    }
}
