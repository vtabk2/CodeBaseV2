package com.sabo.test

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdsUiTest {
    @Test
    fun adControls_showDeterministicNoCacheResults() {
        ActivityScenario.launch(AdsUiTestActivity::class.java).use {
            onView(withId(R.id.show_interstitial_button)).check(matches(isDisplayed()))
            onView(withId(R.id.show_rewarded_button)).check(matches(isDisplayed()))
            onView(withId(R.id.show_app_open_button)).check(matches(isDisplayed()))

            onView(withId(R.id.show_interstitial_button)).perform(click())
            onView(withId(R.id.status_text)).check(matches(withText("interstitial:false")))

            onView(withId(R.id.show_rewarded_button)).perform(click())
            onView(withId(R.id.status_text)).check(matches(withText("rewarded:false:false")))

            onView(withId(R.id.show_app_open_button)).perform(click())
            onView(withId(R.id.status_text)).check(matches(withText("app_open:false")))
        }
    }
}
