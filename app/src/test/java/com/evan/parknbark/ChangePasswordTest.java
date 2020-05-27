package com.evan.parknbark;

import com.evan.parknbark.emailpassword.ChangePassActivity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ChangePasswordTest {
    private String currentPass;
    private String newPass;

    @Before
    public void setUp() throws Exception {
        currentPass = "Some shitty old pass";
        newPass = "New password :)";
    }

    @Test
    public void changePassTest() {
        ChangePassActivity activity = new ChangePassActivity();
        assertTrue("Error test failed!", activity.changePassword(currentPass, newPass, true));
    }
}
