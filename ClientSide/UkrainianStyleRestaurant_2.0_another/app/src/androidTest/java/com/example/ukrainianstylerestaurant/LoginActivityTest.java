package com.example.ukrainianstylerestaurant;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void LogAct_isDisplayed(){
        onView(withId(R.id.et_username)).check(matches(isDisplayed()));
        onView(withId(R.id.et_password)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).check(matches(withText("Увійти")));
    }

    @Test
    public void LogAct_isTyped(){
        onView(withId(R.id.et_username)).perform(typeText("testUser"), closeSoftKeyboard());
        onView(withId(R.id.et_username)).check(matches(withText("testUser")));
    }
}
