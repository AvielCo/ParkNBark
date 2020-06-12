package com.evan.parknbark;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.evan.parknbark.emailpassword.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginUnitTest {
    @Rule
    public ActivityTestRule<LoginActivity> activity = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void LoginTest() {
        onView(ViewMatchers.withId(R.id.button_login)).check(matches(isClickable())).check(matches(isDisplayed()));
        onView(withId(R.id.progressBar)).check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.button_register)).check(matches(isClickable())).check(matches(isDisplayed()));
        onView(withId(R.id.forgot_password_link)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.checkbox_remember_me)).check(matches(isNotChecked())).check(matches(isDisplayed()));
        onView(withId(R.id.text_input_password)).check(matches(isEnabled())).check(matches(isDisplayed()));
        onView(withId(R.id.text_input_email)).check(matches(isEnabled())).check(matches(isDisplayed()));
    }
}
