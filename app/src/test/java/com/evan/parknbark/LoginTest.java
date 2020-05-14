package com.evan.parknbark;

import androidx.annotation.NonNull;

import com.evan.parknbark.emailpassword.LoginActivity;
import com.evan.parknbark.validation.EditTextValidator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest {
    private String email;
    private String password;

    @Before
    public void setUp() throws Exception {
        email = "ltest@ltest.test";
        password = "LoGiNtEsTiNg";
    }

    @Test
    public void loginTest_() {
        LoginActivity activity = new LoginActivity();
        assertTrue("Successfully logged in!", activity.signIn(email, password, true));
    }
}
