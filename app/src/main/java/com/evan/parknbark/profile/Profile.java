package com.evan.parknbark.profile;

import java.io.Serializable;

public class Profile implements Serializable {
    private String dogName;
    private String dogBreed;
    private String dogAge;

    public Profile() {
    }

    public Profile(String dogName, String dogBreed, String dogAge) {
        setDogName(dogName);
        setDogBreed(dogBreed);
        setDogAge(dogAge);
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
}
