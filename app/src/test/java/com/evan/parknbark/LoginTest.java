package com.evan.parknbark;

import com.evan.parknbark.emailpassword.LoginActivity;
import com.google.firebase.auth.FirebaseAuthException;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

public class LoginTest {
    private String email;
    private String password;

    @Before
    public void setUp() throws IOException {
        email = "user@eeexxxaaammmpppllleee.com";
        password = "LoGiNtEsTiNg";
    }

    @Test
    public void loginTest() throws FirebaseAuthException {
        LoginActivity activity = new LoginActivity();
        assertTrue("Error test failed!", activity.signIn(email, password, true));

    }
}
