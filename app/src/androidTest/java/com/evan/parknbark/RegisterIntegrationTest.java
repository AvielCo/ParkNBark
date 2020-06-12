package com.evan.parknbark;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.emailpassword.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class RegisterIntegrationTest extends ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<RegisterActivity> activity = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void RegisterTest() {
        AtomicReference<Integer> result = new AtomicReference<>(0);
        FirebaseAuth mAuth = activity.getActivity().mAuth;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                result.set(RESULT_OK);
            else result.set(RESULT_BAD);
            checkResult(result.get());
        });
    }

    private void checkResult(int result) {
        if (result == -1)
            assertNotEquals(RESULT_OK, result);
        else assertEquals(RESULT_OK, result);
    }
}
