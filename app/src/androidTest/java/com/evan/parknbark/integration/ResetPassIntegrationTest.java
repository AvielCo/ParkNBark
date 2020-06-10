package com.evan.parknbark.integration;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.ExampleInstrumentedTest;
import com.evan.parknbark.emailpassword.ResetPassActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class ResetPassIntegrationTest extends ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<ResetPassActivity> activity = new ActivityTestRule<>(ResetPassActivity.class);

    @Test
    public void ResetPassTest() {
        FirebaseAuth mAuth = activity.getActivity().mAuth;
        AtomicReference<Integer> result = new AtomicReference<>(0);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.set(RESULT_OK);
            } else result.set(RESULT_BAD);
            checkResult(result.get());
        });
    }

    private void checkResult(int result) {
        if (result == -1)
            assertNotEquals(RESULT_OK, result);
        else assertEquals(RESULT_OK, result);
    }
}
