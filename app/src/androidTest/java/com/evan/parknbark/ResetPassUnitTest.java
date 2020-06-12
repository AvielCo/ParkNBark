package com.evan.parknbark;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.emailpassword.ResetPassActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class ResetPassUnitTest {
    @Rule
    public ActivityTestRule<ResetPassActivity> activity = new ActivityTestRule<>(ResetPassActivity.class);

    @Test
    public void ResetPassTest() {
        onView(ViewMatchers.withId(R.id.button_send_reset_pass)).check(matches(isClickable())).check(matches(isDisplayed()));
        onView(withId(R.id.text_view_reset_pass)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.text_input_email_reset_pass)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.progressBar)).check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }
}
