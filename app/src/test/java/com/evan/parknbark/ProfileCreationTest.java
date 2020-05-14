package com.evan.parknbark;

import com.evan.parknbark.emailpassword.RegisterActivity;
import com.evan.parknbark.map_profile.profile.ProfileActivity;
import com.evan.parknbark.validation.EditTextValidator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProfileCreationTest {
    private String dogName;
    private String dogBreed;
    private String dogAge;

    @Before
    public void setUp() throws Exception {
        dogName = "Test";
        dogBreed = "Test";
        dogAge = "5";
    }

    @Test
    public void registerTest_(){
        ProfileActivity activity = new ProfileActivity();
        assertTrue("Successfully created new profile!", activity.saveProfile(dogName, dogBreed, dogAge, true));
    }

}
