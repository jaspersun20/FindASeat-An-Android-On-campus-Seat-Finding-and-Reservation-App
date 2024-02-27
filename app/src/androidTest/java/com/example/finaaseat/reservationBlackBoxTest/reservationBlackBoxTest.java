package com.example.finaaseat.reservationBlackBoxTest;

import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import org.junit.Rule;
import org.junit.Test;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import org.hamcrest.Matcher;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import android.widget.LinearLayout;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.example.finaaseat.MainActivity;
import com.example.finaaseat.R;

public class reservationBlackBoxTest {


    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);


    public void login(){
        onView(withId(R.id.uscid_login)).perform(click(),typeText("1111111111"),pressBack());

        onView(withId(R.id.password_login)).perform(click(),typeText("123123"),pressBack());
        onView(allOf(withId(R.id.password_login),withText("123123"))).check(matches(isDisplayed()));
        sleep(2000);
        onView(withId(R.id.register_button)).perform(click());

    }

    //check if user can reserve a sit if every condition is met
    @Test
    public void checkBooking(){
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(5000);
        int row = 7/* your cell row */;
        int column = 5/* your cell column */;
        int numColumns = 8/* number of columns in your grid */;
        int cellIndex = 42;

        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex, "indoor"));
        sleep(2000);
        onView(withText("Reservation Confirmation")).check(matches(isDisplayed()));
    }


    //check if the user can book the both indoor and outdoor sitting at the same timeslot
    @Test
    public void checkIndoorOutdoorConflict(){
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(5000);
        int row = 6/* your cell row */;
        int column = 1/* your cell column */;
        int numColumns = 8/* number of columns in your grid */;
        int cellIndex = 41;

        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex, "indoor"));
        sleep(2000);
        onView(withText("Yes")).perform(click());
        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex, "outdoor"));
        onView(withText("CONFLICT")).check(matches(isDisplayed()));
    }


    //check if the user can book the both indoor and outdoor sitting at the same timeslot
    @Test
    public void checkMaximum() {
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(5000);

        int cellIndex1 = 43;
        int cellIndex2 = 51;
        int cellIndex3 = 59;
        int cellIndex4 = 67;
        int cellIndex5 = 75;


        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex1, "indoor"));
        sleep(2000);
        onView(withText("Yes")).perform(click());

        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex2, "outdoor"));
        sleep(2000);
        onView(withText("Yes")).perform(click());

        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex3, "outdoor"));
        sleep(2000);
        onView(withText("Yes")).perform(click());

        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex4, "outdoor"));
        sleep(2000);
        onView(withText("Yes")).perform(click());

        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex5, "outdoor"));
        sleep(2000);
        onView(withText("WARNING!!!")).check(matches(isDisplayed()));

    }

    //check if the user can book seats at the same timeslot but different building, already booked at Doheny
    @Test
    public void checkTimeConflictDifferentBuilding() {
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(5000);
        int cellIndex1 = 41;
        onView(withId(R.id.gridLayout)).perform(clickChildAtIndex(cellIndex1, "indoor"));
        sleep(2000);
        onView(withText("WARNING!!!")).check(matches(isDisplayed()));
    }

    //check if it can switch to home page and profile page
    @Test
    public void checkSwitchingBetweenPages () {
        onView(withId(R.id.reserveButton)).perform(click());
        login();
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(5000);
        //go back to the home page
        onView(withId(R.id.buildingPhoto)).perform(click());
        sleep(5000);
        //go back to the reservation page
        onView(withId(R.id.reserveButton)).perform(click());
        sleep(5000);
        //go back to the profile page
        onView(withId(R.id.profile1)).perform(click());
        sleep(5000);
        onView(withText("Future Reservations")).check(matches(isDisplayed()));

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