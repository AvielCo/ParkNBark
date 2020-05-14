package com.evan.parknbark.map_profile.profile;

import android.net.Uri;

import java.io.Serializable;

public class Profile implements Serializable {
    private String firstName;
    private String lastName;
    private String dogName;
    private String dogBreed;
    private String dogAge;
    private String profilePicture;

    public Profile() {
    }

    public Profile(String firstName, String lastName, String dogName, String dogBreed, String dogAge, String profilePicture) {
        setFirstName(firstName);
        setLastName(lastName);
        setDogName(dogName);
        setDogBreed(dogBreed);
        setDogAge(dogAge);
        setProfilePicture(profilePicture);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getDogBreed() {
        return dogBreed;
    }

    public void setDogBreed(String dogBreed) {
        this.dogBreed = dogBreed;
    }

    public String getDogAge() {
        return dogAge;
    }

    public void setDogAge(String dogAge) {
        this.dogAge = dogAge;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
