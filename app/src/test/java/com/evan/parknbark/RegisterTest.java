package com.evan.parknbark;

import com.evan.parknbark.emailpassword.LoginActivity;
import com.evan.parknbark.emailpassword.RegisterActivity;
import com.evan.parknbark.validation.EditTextValidator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegisterTest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Before
    public void setUp() throws Exception {
        firstName = "RegisterTest";
        lastName = "RegisterTest";
        email = "rtest@rtest.test";
        password = "ReGiSteRtEsTiNg";
    }

    @Test
    public void registerTest(){
        RegisterActivity activity = new RegisterActivity();
        assertTrue("Successfully registered!", activity.signUp(email, password, firstName, lastName, true));
    }
}
