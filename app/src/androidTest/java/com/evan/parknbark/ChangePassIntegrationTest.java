package com.evan.parknbark;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.emailpassword.ChangePassActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class ChangePassIntegrationTest extends ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<ChangePassActivity> activity = new ActivityTestRule<>(ChangePassActivity.class);

    @Test
    public void ChangePassTest() {
        AtomicReference<Integer> result = new AtomicReference<>(0);
        FirebaseAuth mAuth = activity.getActivity().mAuth;
        FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful())
                        result.set(RESULT_OK);
                    else result.set(RESULT_BAD);
                    checkResult(result.get());
                });
            }
        });

    }

    private void checkResult(int result) {
        if (result == -1)
            assertNotEquals(RESULT_OK, result);
        else assertEquals(RESULT_OK, result);
    }
}
