package com.evan.parknbark.unittest;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.R;
import com.evan.parknbark.emailpassword.RegisterActivity;

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
public class RegisterUnitTest {
    @Rule
    public ActivityTestRule<RegisterActivity> activity = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void RegisterTest() {
        onView(ViewMatchers.withId(R.id.button_register)).check(matches(isClickable())).check(matches(isDisplayed()));
        onView(withId(R.id.text_input_fname)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.text_input_lname)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.text_input_email)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.text_input_password)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.progressBar)).check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }
}
