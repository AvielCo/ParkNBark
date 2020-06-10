package com.evan.parknbark;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public final int RESULT_OK = 1,
            RESULT_BAD = -1;
    public String email;
    public String password;
    public String newPassword;

    @Before
    public void setUp() {
        email = "testing.email@just.testing.com";
        password = "this_is_a_super_secret_password_:)";
        newPassword = "hey_im_new_super_duper_secret_password_FU";
    }

    @Test
    public void dummy() {
        assertTrue(true);
    }
}
