package com.example.finaaseat;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class UpdateReservation extends AppCompatActivity {

    FirebaseDatabase root = FirebaseDatabase.getInstance();
    DatabaseReference dreference = root.getReference();

    public String BuildingName = "Leavey Library";
    public String Indoor_Capacity = "null";
    public String Outdoor_Capacity = "null";
    private String Operation_hour;
    private String weekday_Operation_hour;
    private String userID = "8888888888";
    private String weekend_Operation_hour;
    private String current_Indoor_Availability ;
    private String current_Outdoor_Availability ;

    private String original_reservation_index = "7";
    private String original_reservation_category  = "indoor";
    private String original_reservation_date = "11/18";
    private String original_reservation_timeslot = "12:30pm";



    public void setUSCid(String id){
        this.userID = id;
    }

    public void setBuilding(String building){
        this.BuildingName = building;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String current_Date = getUserDate();
        String[] firstRowLabels = getDatesArray(current_Date);
        String day = getDayOfWeek(current_Date);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String uscId = sharedPreferences.getString("userId", "");
        BuildingName = sharedPreferences.getString("buildingtxt","");
        original_reservation_index = sharedPreferences.getString("indextxt","");
        original_reservation_category = sharedPreferences.getString("categorytxt","");
        original_reservation_date = sharedPreferences.getString("datetxt","");
        original_reservation_timeslot = sharedPreferences.getString("timeslottxt","");
        setUSCid(uscId);


        dreference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp1 = (String) changeDateFormat(current_Date);




//                setBuilding(building);
                String building_abbreviation = snapshot.child("buildings").child(BuildingName).child("Abbreviation").getValue(String.class);
                weekday_Operation_hour = "Monday-Friday: " + snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Monday-Friday").getValue(String.class);
                weekend_Operation_hour = "Saturday-Sunday: "+ snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Saturday-Sunday").getValue(String.class);
                String all_week_Operation_hour = weekday_Operation_hour + "\n\n" + weekend_Operation_hour;
                setOperation_hour(all_week_Operation_hour);

                Indoor_Capacity = " " + snapshot.child("buildings").child(BuildingName).child("Indoor_availability").getValue();
                Outdoor_Capacity = " " + snapshot.child("buildings").child(BuildingName).child("Outdoor_availability").getValue();
                setIndoor_Availability("Indoor:" +Indoor_Capacity);
                setOutdoor_Availability("Outdoor:" +Outdoor_Capacity);
                setBuildingName(building_abbreviation);


                String temp;
                if(day == "Saturday" ||day == "Sunday"){
                    temp = "Saturday-Sunday";
                }else{
                    temp = "Monday-Friday";
                }

                Operation_hour = snapshot.child("buildings").child(BuildingName).child("Operation Hour").child(temp).getValue(String.class);
                String[] times = Operation_hour.split("--");
                String opening_time = times[0];
                String closing_time = times[1];

                //delete the original reservation
                dreference.child("users").child(userID).child("Reservation").child(original_reservation_index).removeValue();
                //need to have a new database reference
                dreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                    updateDeleteUser(userID, original_reservation_date);
                    updateDeleteBuilding(BuildingName, original_reservation_date, original_reservation_category, original_reservation_timeslot);


                    GridLayout gridLayout = findViewById(R.id.gridLayout);
                    int numColumns = 8;
                    int numRows = 20;

                    // Define the labels for the first row and first column
                    for (int i = 0; i < numRows; i++) {
                        String currentTimeslot = getCurrentTimeslot(i);
                        for (int j = 0; j < numColumns; j++) {
                            String reservation_Date = firstRowLabels[j];

                            if (i == 0) {
                                TextView label = new TextView(UpdateReservation.this);
                                label.setText(firstRowLabels[j]);
                                label.setGravity(Gravity.CENTER);
                                gridLayout.addView(label);
                            } else if (j == 0) {
                                TextView label = new TextView(UpdateReservation.this);
                                String timeSlotLabel = calculateTimeSlot(i -1); // Subtract 1 because your loop starts at 0
                                label.setText(timeSlotLabel);
                                gridLayout.addView(label);
                            } else {
                                //get the timeslot and the timeperiod
                                String timeSlotLabel = calculateTimeSlot(i-1);
    //                            Log.d("sbsbsbs", timeSlotLabel);
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mma", Locale.US);
                                Date startDate = null;
                                try {
                                    startDate = sdf.parse(timeSlotLabel);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(startDate);
                                calendar.add(Calendar.MINUTE, 30);
                                String endTime = sdf.format(calendar.getTime());
                                String timePeriodLabel =  timeSlotLabel + "-" + endTime;
                                //end of getting the timeslot and timeperiod




                                boolean isWithinOperationHours = isWithinOperationHours(timeSlotLabel, opening_time, closing_time);
                                if (!isWithinOperationHours) {
                                    TextView unavailableTextView = new TextView(UpdateReservation.this);
                                    unavailableTextView.setText("Unavailable");
                                    unavailableTextView.setTextColor(Color.GRAY); // Same text color as your button text color for disabled state

                                    // Apply the same styling as the buttons
                                    unavailableTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Same text size as buttons
                                    unavailableTextView.setTypeface(null, Typeface.BOLD); // Same text style as buttons
                                    unavailableTextView.setGravity(Gravity.CENTER); // Center text like the button text

                                    // Set the same padding as your buttons
                                    int padding = (int) (16 * UpdateReservation.this.getResources().getDisplayMetrics().density);
                                    unavailableTextView.setPadding(padding, padding, padding, padding);

                                    // Set layout parameters similar to buttons if needed
                                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                                    layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                                    layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                                    layoutParams.setMargins(10, 10, 10, 10); // Set margins if needed
                                    unavailableTextView.setLayoutParams(layoutParams);
                                    gridLayout.addView(unavailableTextView);

                                }else {
                                    current_Indoor_Availability = " " + snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(changeDateFormat(reservation_Date)).child(timeSlotLabel).child("indoor").getValue();
                                    current_Outdoor_Availability = " " + snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(changeDateFormat(reservation_Date)).child(timeSlotLabel).child("outdoor").getValue();
                                    int current_Indoor_count = Integer.parseInt(current_Indoor_Availability.trim());
                                    int current_Outdoor_count = Integer.parseInt(current_Outdoor_Availability.trim());
                                    // Create a container for the two buttons
                                    LinearLayout container = new LinearLayout(UpdateReservation.this);
                                    container.setOrientation(LinearLayout.VERTICAL);

                                    // Set up the layout parameters for the container, if needed
                                    GridLayout.LayoutParams containerParams = new GridLayout.LayoutParams();
                                    containerParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                                    containerParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                                    container.setLayoutParams(containerParams);

                                    // Create the first button
                                    Button button1 = new Button(UpdateReservation.this);
                                    Button button2 = new Button(UpdateReservation.this);


                                    String indoorText = "Indoor: "
                                            + current_Indoor_Availability+ "/" + Indoor_Capacity+ "\n";
                                    String outdoorText = "Outdoor"
                                            + current_Outdoor_Availability + "/" + Outdoor_Capacity + "\n";
                                    String reserveNowText = "RESERVE NOW";
                                    SpannableString spannableStringIndoor = new SpannableString(indoorText + reserveNowText);
                                    spannableStringIndoor.setSpan(new ForegroundColorSpan(Color.YELLOW), indoorText.length(), (indoorText + reserveNowText).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    button1.setText(spannableStringIndoor);
                                    button1.setOnClickListener(v -> {
    //                                    if the same spot has already been selected
                                        if (!button1.isEnabled()) {
                                            return;
                                        }

                                        dreference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                AtomicReference<Boolean> isEdit = new AtomicReference<>(false);

                                                //check if conflict
                                                Boolean isconflict = false;
                                                for (DataSnapshot childSnapshot: snapshot.child("users").child(userID).child("Reservation").getChildren()) {
                                                    String date1 = childSnapshot.child("Date").getValue(String.class);
                                                    String time1 = childSnapshot.child("Timeslot").getValue(String.class);
                                                    Long status1 = (Long) childSnapshot.child("Status").getValue();
                                                    if (reservation_Date.equals(date1) && timeSlotLabel.equals(time1) && status1 == 1) {
                                                        isconflict = true;
                                                    }
                                                }
                                                if(isconflict.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You can't select two seating in the same timeslot!!")
                                                            .show();
                                                    return;
                                                }


                                                //Select a reservation with non-consecutive slots (e.g. 9-9:30am and 2-2:30pm) → infeasible
                                                Boolean notConsecutive = false;
                                                for (DataSnapshot childSnapshot: snapshot.child("users").child(userID).child("Reservation").getChildren()) {
                                                    String date1 = childSnapshot.child("Date").getValue(String.class);
                                                    String time1 = childSnapshot.child("Timeslot").getValue(String.class);
                                                    Long status1 = (Long) childSnapshot.child("Status").getValue();

                                                    if (reservation_Date.equals(date1) && status1 == 1) {
                                                        notConsecutive = isTimeDifferenceOneHourOrMore(timeSlotLabel, time1);
                                                    }
                                                }
                                                if(notConsecutive.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You can't select two seating not consecutive!!")
                                                            .show();
                                                    return;
                                                }

                                                //Select a reservation before current time → infeasible
                                                Boolean beforeCurrentTime1 = false;
                                                beforeCurrentTime1 = isCurrentTimeBefore(timeSlotLabel, reservation_Date);
                                                if(beforeCurrentTime1.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You can't select a seating before the current time!!")
                                                            .show();
                                                    return;
                                                }


                                                //Select a reservation with longer than 2 hours → infeasible
                                                Boolean reach_maximum = false;
                                                int num_reservation_in_that_day = 0;
                                                if(snapshot.child("users").child(userID).child("Counter for 1 Day Reservation").hasChild(changeDateFormat(reservation_Date))){
                                                    num_reservation_in_that_day = snapshot.child("users").child(userID).child("Counter for 1 Day Reservation").child(changeDateFormat(reservation_Date)).getValue(int.class);
                                                    if(num_reservation_in_that_day >=4){
                                                        reach_maximum = true;
                                                    }
                                                }
                                                if(reach_maximum.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You have reached the maximum!!")
                                                            .show();
                                                    return;
                                                }




                                                new AlertDialog.Builder(UpdateReservation.this)
                                                        .setTitle("Reservation Confirmation")
                                                        .setMessage("Are you sure you want to reserve " + BuildingName + " " + timePeriodLabel + " indoor?")
                                                        .setPositiveButton("Yes", (dialog, which) -> {
                                                            updateUserInfo(userID, BuildingName, original_reservation_index, reservation_Date, timeSlotLabel, "indoor", 1);
                                                            String tempp= "ID: " + String.valueOf(current_Indoor_count-1) + " ";
                                                            updateBuildingInfo(BuildingName, reservation_Date, "indoor", timeSlotLabel, current_Indoor_count);
                                                            button1.setText(tempp + reservation_Date + "\n" + timePeriodLabel);
                                                            button1.setBackgroundColor(Color.GREEN);
                                                            button1.setEnabled(false);
                                                            button2.setEnabled(false);
                                                            button2.setBackgroundColor(Color.GRAY);
                                                            button2.setText("CONFLICT");
//                                                            isEdit.set(true);
//                                                            Log.d("Is it edited?", String.valueOf(isEdit));
                                                            Intent intent = new Intent(UpdateReservation.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                            }
                                                        })
                                                        .show();
//

                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                    });



                                    // Create the second button
                                    SpannableString spannableStringOutdoor = new SpannableString(outdoorText + reserveNowText);
                                    spannableStringOutdoor.setSpan(new ForegroundColorSpan(Color.RED), outdoorText.length(), (outdoorText + reserveNowText).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    button2.setText(spannableStringOutdoor);
                                    button2.setOnClickListener(v -> {
                                        if (!button2.isEnabled()) {
                                            return;
                                        }
                                        dreference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                AtomicReference<Boolean> isEdit = new AtomicReference<>(false);

                                                //check if time conflict
                                                Boolean isconflict = false;
                                                for (DataSnapshot childSnapshot: snapshot.child("users").child(userID).child("Reservation").getChildren()) {
                                                    String date2 = childSnapshot.child("Date").getValue(String.class);
                                                    String time2 = childSnapshot.child("Timeslot").getValue(String.class);
                                                    Long status2 = (Long) childSnapshot.child("Status").getValue();
                                                    if (reservation_Date.equals(date2) && timeSlotLabel.equals(time2) && status2 ==1 ) {
                                                        isconflict = true;
                                                    }
                                                }
                                                if(isconflict.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You can't select two seatings in the same timeslot!!")
                                                            .show();
                                                    return;
                                                }

                                                //Select a reservation with non-consecutive slots (e.g. 9-9:30am and 2-2:30pm) → infeasible
                                                Boolean notConsecutive = false;
                                                for (DataSnapshot childSnapshot: snapshot.child("users").child(userID).child("Reservation").getChildren()) {
                                                    String date2 = childSnapshot.child("Date").getValue(String.class);
                                                    String time2 = childSnapshot.child("Timeslot").getValue(String.class);
                                                    Long status2 = (Long) childSnapshot.child("Status").getValue();

                                                    if (reservation_Date.equals(date2) && status2 == 1) {
                                                        notConsecutive = isTimeDifferenceOneHourOrMore(timeSlotLabel, time2);
                                                    }
                                                }
                                                if(notConsecutive.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You can't select two seatings not consecutive!!")
                                                            .show();
                                                    return;
                                                }

                                                //check if reach maximum
                                                Boolean reach_maximum = false;
                                                int num_reservation_in_that_day = 0;
                                                if(snapshot.child("users").child(userID).child("Counter for 1 Day Reservation").hasChild(changeDateFormat(reservation_Date))){
                                                    num_reservation_in_that_day = snapshot.child("users").child(userID).child("Counter for 1 Day Reservation").child(changeDateFormat(reservation_Date)).getValue(int.class);
                                                    if(num_reservation_in_that_day >=4){
                                                        reach_maximum = true;
                                                    }
                                                }
                                                if(reach_maximum.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You have reached the maximum!!")
                                                            .show();
                                                    return;
                                                }


                                                //Select a reservation before current time → infeasible
                                                Boolean beforeCurrentTime2 = false;
                                                beforeCurrentTime2 = isCurrentTimeBefore(timeSlotLabel, reservation_Date);
                                                if(beforeCurrentTime2.equals(true)){
                                                    new AlertDialog.Builder(UpdateReservation.this)
                                                            .setTitle("WARNING!!!")
                                                            .setMessage("You can't select a seating before the current time!!")
                                                            .show();
                                                    return;
                                                }

                                                new AlertDialog.Builder(UpdateReservation.this)
                                                        .setTitle("Reservation Confirmation")
                                                        .setMessage("Are you sure you want to reserve " + BuildingName + " " + timePeriodLabel + " outdoor?")
                                                        .setPositiveButton("Yes", (dialog, which) -> {
                                                            updateUserInfo(userID, BuildingName, original_reservation_index, reservation_Date, timeSlotLabel, "outdoor", 1);
                                                            String tempp= "ID: " + String.valueOf(current_Outdoor_count-1) + " ";
                                                            updateBuildingInfo(BuildingName, reservation_Date, "outdoor", timeSlotLabel, current_Outdoor_count);
                                                            button2.setText(tempp + reservation_Date + "\n" + timePeriodLabel);
                                                            button2.setBackgroundColor(Color.GREEN);
                                                            button2.setEnabled(false);
                                                            button1.setEnabled(false);
                                                            button1.setBackgroundColor(Color.GRAY);
                                                            button1.setText("CONFLICT");
//                                                            isEdit.set(true);
//                                                            Log.d("Is it edited?", String.valueOf(isEdit));
                                                            Intent intent = new Intent(UpdateReservation.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();

                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                            }
                                                        })
                                                        .show();
//                                                if(isEdit.equals(false)){
//                                                    updateUserInfo(userID, BuildingName, original_reservation_index, original_reservation_date, original_reservation_timeslot, original_reservation_category, 1 );
//                                                    String current_availability = snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(original_reservation_date).child(original_reservation_timeslot).child(original_reservation_category).getValue(String.class);
//                                                    dreference.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(original_reservation_date).child(original_reservation_timeslot).child(original_reservation_category).setValue(Integer.parseInt(current_availability)-1);
//                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    });

                                    // Add the buttons to the container
                                    container.addView(button1);
                                    container.addView(button2);

                                    // Add the container to the grid
                                    gridLayout.addView(container);
                                }
                            }
                        }
                    }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        setContentView(R.layout.activity_update_reservation);



        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(UpdateReservation.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to stop editing?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // User clicked "Yes"
                            dreference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    updateUserInfo(userID, BuildingName, original_reservation_index, original_reservation_date, original_reservation_timeslot, original_reservation_category, 1);
                                    String current_Availability = " " + snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(changeDateFormat(original_reservation_date)).child(original_reservation_timeslot).child(original_reservation_category).getValue();
                                    int current_count = Integer.parseInt(current_Availability.trim());
                                    Log.d("current_a", String.valueOf(current_count));
                                    updateBuildingInfo(BuildingName, original_reservation_date, original_reservation_category, original_reservation_timeslot, current_count);
                                    Intent intent = new Intent(UpdateReservation.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                        })
                        .show();
            }
        });
        // Set the listener for the profile element
        View pButton = findViewById(R.id.profile1);
        pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        updateUserInfo(userID, BuildingName, original_reservation_index, original_reservation_date, original_reservation_timeslot, original_reservation_category, 1);
                        String current_Availability = " " + snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(changeDateFormat(original_reservation_date)).child(original_reservation_timeslot).child(original_reservation_category).getValue();
                        int current_count = Integer.parseInt(current_Availability.trim());
                        Log.d("current_a", String.valueOf(current_count));
                        updateBuildingInfo(BuildingName, original_reservation_date, original_reservation_category, original_reservation_timeslot, current_count);
                        Intent intent = new Intent(UpdateReservation.this, Profile.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        View homeButton = findViewById(R.id.buildingPhoto);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dreference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        updateUserInfo(userID, BuildingName, original_reservation_index, original_reservation_date, original_reservation_timeslot, original_reservation_category, 1);
                        String current_Availability = " " + snapshot.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(changeDateFormat(original_reservation_date)).child(original_reservation_timeslot).child(original_reservation_category).getValue();
                        int current_count = Integer.parseInt(current_Availability.trim());
                        Log.d("current_a", String.valueOf(current_count));
                        updateBuildingInfo(BuildingName, original_reservation_date, original_reservation_category, original_reservation_timeslot, current_count);
                        Intent intent = new Intent(UpdateReservation.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

    }


    public void updateDeleteUser(String uscTxt, String original_reservation_date){
        dreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int user_after_day_count = 0;
                for (DataSnapshot childSnapshot: snapshot.child("users").child(uscTxt).child("Reservation").getChildren()) {
                    String date = childSnapshot.child("Date").getValue(String.class);
                    Log.d("date", date);
                    Log.d("original_data", original_reservation_date);
                    Long status = (Long)childSnapshot.child("Status").getValue();
                    Log.d("status", String.valueOf(status));
                    if (original_reservation_date.trim().equals(date.trim()) && status == 1  ) {
                        user_after_day_count++;
                    }
                }
                Log.d("after update count ", String.valueOf(user_after_day_count));
                dreference.child("users").child(uscTxt).child("Counter for 1 Day Reservation").child(changeDateFormat(original_reservation_date)).setValue(user_after_day_count);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void updateUserInfo (String uscTxt, String BuildingName, String reservation_index, String current_Date, String time_slot, String indoor, int value){
        dreference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dreference.child("users").child(uscTxt).child("Reservation").child(reservation_index).child("BuildingName").setValue(BuildingName);
                dreference.child("users").child(uscTxt).child("Reservation").child(reservation_index).child("Status").setValue(value);
                dreference.child("users").child(uscTxt).child("Reservation").child(reservation_index).child("Category").setValue(indoor);
                dreference.child("users").child(uscTxt).child("Reservation").child(reservation_index).child("Date").setValue(current_Date);
                dreference.child("users").child(uscTxt).child("Reservation").child(reservation_index).child("Timeslot").setValue(time_slot);

                int user_current_day_count = 1;
                for (DataSnapshot childSnapshot: snapshot.child(uscTxt).child("Reservation").getChildren()) {
                    String date = childSnapshot.child("Date").getValue(String.class);
                    Long status = (Long)childSnapshot.child("Status").getValue();
                    if (current_Date.equals(date) && status == 1  ) {
                        user_current_day_count++;
                    }
                }
                Log.d("after update count ", String.valueOf(user_current_day_count));
                dreference.child("users").child(uscTxt).child("Counter for 1 Day Reservation").child(changeDateFormat(current_Date)).setValue(user_current_day_count);

                Toast.makeText(UpdateReservation.this, "Reservation edit Successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void updateDeleteBuilding(String BuildingName, String Date, String indoor, String time_slot){
        dreference.child("buildings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String changed_date = changeDateFormat(Date).trim();
                Log.d("changed_date", changed_date);
                Long current_availability = (Long)snapshot.child(BuildingName).child("Operation Hour").child("Reservation_status").child(changed_date).child(time_slot).child(indoor).getValue();
                Log.d("availability", String.valueOf(current_availability));
                dreference.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(changed_date).child(time_slot).child(indoor).setValue(current_availability + 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void updateBuildingInfo (String BuildingName, String Date, String indoor, String time_slot, int original_value){
        dreference.child("buildings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String changed_date = changeDateFormat(Date).trim();
                dreference.child("buildings").child(BuildingName).child("Operation Hour").child("Reservation_status").child(changed_date).child(time_slot).child(indoor).setValue(original_value-1);
                Toast.makeText(UpdateReservation.this, "Reservation made Successfully.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static boolean isTimeDifferenceOneHourOrMore(String time1, String time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mma", Locale.getDefault());
        try {
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);
            if (date1 != null && date2 != null) {
                long timeDifference = Math.abs(date2.getTime() - date1.getTime()) / (1000 * 60);
                return timeDifference >= 60;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean isCurrentTimeBefore(String targetTime, String targetDateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mma", Locale.getDefault());
        try {
            String dateTime = targetDateStr + " " + targetTime;
            Log.d("raw dateTime", dateTime);

            Date targetDateTime = sdf.parse(dateTime);
            Log.d("parsed dateTime", String.valueOf(targetDateTime));

            if (targetDateTime != null) {
                Calendar targetCalendar = Calendar.getInstance();
                targetCalendar.setTime(targetDateTime);

                // Set the year to 2023
                targetCalendar.set(Calendar.YEAR, 2023);

                Calendar currentTime = Calendar.getInstance();
                Log.d("current time", String.valueOf(currentTime.getTime()));

                // Return true if current time is after the target time
                return !currentTime.before(targetCalendar);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }
    public String changeDateFormat(String originalDate) {
        // The original format of the date which is to be parsed
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd", Locale.ENGLISH);

        // The new format you want to convert to
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);

        try {
            // Parsing the original date string
            Date date = originalFormat.parse(originalDate);

            // Formatting the date to new format
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getCurrentTimeslot(int i){
        int startTimeMinutes = i * 30;
        int startHours = 9 + (startTimeMinutes / 60);
        int startMinutes = startTimeMinutes % 60;

        // End time calculations (each slot is 30 minutes long)
        int endTimeMinutes = startTimeMinutes + 30;
        int endHours = 9 + (endTimeMinutes / 60);
        int endMinutes = endTimeMinutes % 60;

        // AM/PM formatting for start time
        String startAmPm = "am";
        if (startHours >= 12) {
            startAmPm = "pm";
            if (startHours > 12) startHours -= 12;
        }

        // AM/PM formatting for end time
        String endAmPm = "am";
        if (endHours >= 12) {
            endAmPm = "pm";
            if (endHours > 12) endHours -= 12;
        }

        // Formatting minutes to always be two digits
        String startMinuteString = String.format("%02d", startMinutes);
        String endMinuteString = String.format("%02d", endMinutes);
        return startHours + ":" + startMinuteString + startAmPm + "--" + endHours + ":" + endMinuteString + endAmPm;
    }
    public String getUserDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        return formattedDate;
    }
    public String calculateTimeSlot(int halfHourIncrements) {
        int startingHour = 9; // Starting at 10:00 am
        int startingMinute = 0; // Starting at the 0th minute
        String amPm = "am";
        int hour = startingHour + (halfHourIncrements / 2);
        int minute = startingMinute + (30 * (halfHourIncrements % 2));

        // Wrap around if minute is 60
        if (minute >= 60) {
            hour += minute / 60;
            minute = minute % 60;
        }

        // Switch to PM if necessary
        if (hour >= 12) {
            amPm = "pm";
            if (hour > 12) {
                hour -= 12;
            }
        }
        return String.format("%d:%02d%s", hour, minute, amPm);
    }
    public void setOperation_hour(String allWeekOperationHour) {
        TextView BuildingNameTextView = findViewById(R.id.operation_hour);
        BuildingNameTextView.setText(allWeekOperationHour);
    }
    public void setBuildingName(String BuildingName) {
        TextView BuildingNameTextView = findViewById(R.id.buildingName);
        BuildingNameTextView.setText(BuildingName);
    }
    public void setIndoor_Availability(String Indoor_Availability) {
        TextView Indoor_AvailabilityTextView = findViewById(R.id.Indoor_Availability);
        Indoor_AvailabilityTextView.setText(Indoor_Availability);
    }
    public void setOutdoor_Availability(String Outdoor_Availability) {
        TextView Outdoor_AvailabilityTextView = findViewById(R.id.Outdoor_Availability);
        Outdoor_AvailabilityTextView.setText(Outdoor_Availability);
    }
    public static String[] getDatesArray(String startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        Calendar calendar = Calendar.getInstance();
        String[] datesArray = new String[8]; // One for empty string + 7 dates
        datesArray[0] = ""; // First element is an empty string

        try {
            calendar.setTime(sdf.parse(startDate));

            // Fill the array with dates
            for (int i = 1; i < datesArray.length; i++) {
                datesArray[i] = sdf.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1); // Increment the day
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datesArray;
    }
    public String getDayOfWeek(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.US);
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(sdf.parse(date));
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) -4;
            if (dayOfWeek <= 0){
                dayOfWeek = 7 + dayOfWeek;
            }
            String temp = dayOfWeekToString(dayOfWeek);
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }
    public String dayOfWeekToString(int dayOfWeek) {
        switch (dayOfWeek) {
            case SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case TUESDAY:
                return "Tuesday";
            case WEDNESDAY:
                return "Wednesday";
            case THURSDAY:
                return "Thursday";
            case FRIDAY:
                return "Friday";
            case SATURDAY:
                return "Saturday";
            default:
                return "Invalid day";
        }
    }
    public boolean isWithinOperationHours(String timeSlotLabel, String openingTime, String closingTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mma", Locale.US);
        try {
            Date slotTime = sdf.parse(timeSlotLabel);
            Date opening = sdf.parse(openingTime);
            Calendar closingCalendar = Calendar.getInstance();
            closingCalendar.setTime(sdf.parse(closingTime));
            closingCalendar.add(Calendar.MINUTE, -1); // Add 30 minutes to closing time
            Date closing = closingCalendar.getTime();

            return slotTime != null && !slotTime.before(opening) && !slotTime.after(closing);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}