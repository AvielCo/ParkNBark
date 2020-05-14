package com.evan.parknbark;

import android.app.LauncherActivity;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.emailpassword.LoginActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoginIntegrationTest {
    private String email;
    private String password;
    private LoginActivity activity;

    @Before
    public void setUp() throws Exception {
        email = "ltest@ltest.test";
        password = "LoGiNtEsTiNg";
    }

    @Test
    public void loginTest(){
        assertTrue(activity.signIn(email, password, true));
    }
}
