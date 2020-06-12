package com.evan.parknbark;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.emailpassword.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class LoginIntegrationTest extends ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<LoginActivity> activity = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void LoginTest() {
        AtomicReference<Integer> result = new AtomicReference<>(0);
        activity.getActivity().mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.set(RESULT_OK);
            } else result.set(RESULT_BAD);
            LoginIntegrationTest.this.checkResult(result.get());
        });
    }

    private void checkResult(int result) {
        if (result == -1)
            assertNotEquals(RESULT_OK, result);
        else assertEquals(RESULT_OK, result);
    }
}
