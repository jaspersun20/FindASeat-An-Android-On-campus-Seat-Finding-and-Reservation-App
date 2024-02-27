package com.example.finaaseat.profileBlackBoxTest;

import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import org.junit.Rule;
import org.junit.Test;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import static org.hamcrest.Matchers.not;


import androidx.annotation.NonNull;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.Matcher;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import android.widget.LinearLayout;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.example.finaaseat.MainActivity;
import com.example.finaaseat.R;
import com.example.finaaseat.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileBlackBoxTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);


    public void login(){
        onView(withId(R.id.uscid_login)).perform(click(),typeText("4444444444"),pressBack());

        onView(withId(R.id.password_login)).perform(click(),typeText("123123"),pressBack());
        onView(allOf(withId(R.id.password_login),withText("123123"))).check(matches(isDisplayed()));
        sleep(2000);
        onView(withId(R.id.register_button)).perform(click());
    }


    @Test
    public void testOnCheckProfilePic() {
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnCheckLogout() {
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(1000);
        onView(withId(R.id.uscid_login)).perform(click(),typeText("3333333333"),pressBack());
        onView(withId(R.id.password_login)).perform(click(),typeText("123123"),pressBack());
        onView(allOf(withId(R.id.password_login),withText("123123"))).check(matches(isDisplayed()));
        sleep(1000);
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.logout)).perform(click());
        onView(withId(R.id.reserveButton)).perform(click());
        onView(withId(R.id.uscid_login)).check(matches(isDisplayed()));
    }


    @Test
    public void testOnCheckEditProfile(){
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(1000);
        onView(withId(R.id.uscid_login)).perform(click(),typeText("6666666666"),pressBack());
        onView(withId(R.id.password_login)).perform(click(),typeText("123123"),pressBack());
        onView(allOf(withId(R.id.password_login),withText("123123"))).check(matches(isDisplayed()));
        sleep(1000);
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.profile)).perform(click());
        sleep(2000);
        onView(withId(R.id.username)).perform(typeText("trojan"), pressBack());
        sleep(1000);
        onView(withId(R.id.email)).perform(typeText("trojan@usc.edu"), pressBack());
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.profile)).perform(click());
        sleep(1000);
        onView(withId(R.id.username)).check(matches(withText(" Name: trojan")));
        onView(withId(R.id.email)).check(matches(withText(" Email: trojan@usc.edu")));
    }

    @Test
    public void testOnCheckEditButton(){
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(1000);
        onView(withId(R.id.uscid_login)).perform(click(),typeText("2222222222"),pressBack());
        onView(withId(R.id.password_login)).perform(click(),typeText("123123"),pressBack());
        onView(allOf(withId(R.id.password_login),withText("123123"))).check(matches(isDisplayed()));
        sleep(1000);
        onView(withId(R.id.register_button)).perform(click());
        sleep(1000);
        onView(withId(R.id.profile)).perform(click());
        sleep(1000);
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.operation_hour)).check(matches(isDisplayed()));
        onView(withId(R.id.buildingPhoto)).perform(click());
    }
    @Test
    public void testOnCheckCancelButton(){
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(1000);
        onView(withId(R.id.uscid_login)).perform(click(),typeText("1212121212"),pressBack());
        onView(withId(R.id.password_login)).perform(click(),typeText("123123"),pressBack());
        onView(allOf(withId(R.id.password_login),withText("123123"))).check(matches(isDisplayed()));
        sleep(1000);
        onView(withId(R.id.register_button)).perform(click());
        sleep(2000);
        onView(withId(R.id.profile)).perform(click());
        sleep(1000);
        onView(withId(R.id.cancel_button)).perform(click());
        sleep(1000);
        onView(withText("YES")).perform(click());
        sleep(1000);
        onView(withId(R.id.reservation_history_button)).perform(click());
        sleep(1000);
        onView(withId(R.id.btn_canceled_reservations)).check(matches(isDisplayed()));
    }



    public static ViewAction clickChildAtIndex ( final int index, final String category){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return instanceOf(GridLayout.class);
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified index.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                GridLayout gridLayout = (GridLayout) view;
                LinearLayout container = (LinearLayout) gridLayout.getChildAt(index);
                if (category.equals("indoor")) {
                    Button button = (Button) container.getChildAt(0); // First button in the container
                    button.performClick();

                } else if (category.equals("outdoor")) {
                    Button button = (Button) container.getChildAt(1); // First button in the container
                    button.performClick();
                }
            }
        };
    }


}
