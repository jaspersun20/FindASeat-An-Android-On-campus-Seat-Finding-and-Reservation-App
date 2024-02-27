package com.example.finaaseat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Profile extends AppCompatActivity {

    private FirebaseDatabase root = FirebaseDatabase.getInstance();
    private DatabaseReference dreference = root.getReference();
    private ImageView profileImage;
    private EditText username, email;
    private TextView uscID;
    private Button editProfileButton;
    private RecyclerView recyclerView;
    private TextView noReservationsTextView;
    private StorageReference storageReference;
    private String defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/findaseat-a81b4.appspot.com/o/trojan.jpg?alt=media&token=32d699ea-5fc8-41a5-9905-c87529b2e9b3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storageReference = FirebaseStorage.getInstance().getReference();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String uscId = sharedPreferences.getString("userId", "");


        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        uscID = findViewById(R.id.display_uscid);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startForResult.launch(intent);
            }
        });

        // Set the listener for the back element
        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Profile activity
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Set the listener for the back element
        View historyButton = findViewById(R.id.reservation_history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Profile activity
                Intent intent = new Intent(Profile.this, ReservationRecord.class);
                startActivity(intent);
            }
        });

        // Set the listener for the back element
        View logOut = findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Profile activity
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", "");
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Load profile picture from Firebase database or set default if not exists
        dreference.child("users").child(uscId).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    Glide.with(Profile.this)
                            .load(imageUrl)
                            .into(profileImage);
                } else {
                    Glide.with(Profile.this)
                            .load(defaultProfileImageUrl)
                            .into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to load profile image.", Toast.LENGTH_SHORT).show();
            }
        });




        // Set a TextWatcher on username EditText
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Update the username in the database
                String newUsername = editable.toString().trim().replaceFirst("^Name: ", "");
                dreference.child("users").child(uscId).child("name").setValue(newUsername);
            }
        });

        // Set a TextWatcher on email EditText
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Update the email in the database
                String newEmail = editable.toString().trim().replaceFirst("^Email: ", "");
                dreference.child("users").child(uscId).child("email").setValue(newEmail);
            }
        });

        // Fetching user data and populating fields
        dreference.child("users").child(uscId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String usc = dataSnapshot.child("uscid").getValue(String.class);
                    String mail = dataSnapshot.child("email").getValue(String.class);

                    username.setText(" Name: " + name);
                    uscID.setText(getString(R.string.usc_id_format, usc));
                    email.setText(" Email: " + mail);
                } else {
                    Toast.makeText(Profile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewFutureReservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noReservationsTextView = findViewById(R.id.noReservationsTextView);
        fetchFutureReservations();

    }
    private void fetchFutureReservations() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String uscId = sharedPreferences.getString("userId", "");
        dreference.child("users").child(uscId).child("Reservation")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Reservations> futureReservations = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Reservations reservation = snapshot.getValue(Reservations.class);
                            if (reservation != null ) {
                                reservation.setKey(snapshot.getKey());
                                if (!reservation.isPastReservation()) {
                                    futureReservations.add(reservation);
                                }
                            }
                        }

                        if (futureReservations.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noReservationsTextView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noReservationsTextView.setVisibility(View.GONE);
                            updateUI(futureReservations);
                        }
                        Collections.sort(futureReservations, new Comparator<Reservations>() {
                            @Override
                            public int compare(Reservations r1, Reservations r2) {
                                // "2023-11-06 1:00PM"
                                return r2.getDateTime().compareTo(r1.getDateTime());
                            }
                        });
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void updateUI(List<Reservations> reservations) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String uscId = sharedPreferences.getString("userId", "");
        recyclerView.setAdapter(new ReservationsAdapter(reservations, dreference, uscId));
    }

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                String uscId = sharedPreferences.getString("userId", "");
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    StorageReference fileRef = storageReference.child("users").child(uscId).child("profile.jpg");
                    fileRef.putFile(imageUri)
                            .continueWithTask(task -> {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                // Continue with the task to get the download URL
                                return fileRef.getDownloadUrl();
                            })
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    dreference.child("users").child(uscId).child("profile").setValue(downloadUri.toString());
                                    // Update imageview with the new profile pic
                                    Glide.with(Profile.this)
                                            .load(downloadUri)
                                            .into(profileImage);
                                } else {
                                    Toast.makeText(Profile.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

    public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

        private final List<Reservations> reservationsList;
        private DatabaseReference dreference;
        private String uscId;

        public ReservationsAdapter(List<Reservations> reservationsList, DatabaseReference dreference, String uscId) {
            this.reservationsList = reservationsList;
            this.dreference = dreference;
            this.uscId = uscId;
        }

        @NonNull
        @Override
        public ReservationsAdapter.ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item_future, parent, false);
            return new ReservationsAdapter.ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationsAdapter.ReservationViewHolder holder, int position) {
            Reservations reservation = reservationsList.get(position);
            holder.tvReservationLocation.setText(String.format("Location: %s %s", reservation.getBuildingName(), reservation.getCategory()));
            holder.tvReservationTime.setText(String.format("Time: %s %s", reservation.getDate(), reservation.getTimeslot()));
            holder.tvReservationStatus.setText(String.format("Status: %s", reservation.getStatus()));
//            holder.btnCancel.setVisibility(View.GONE);
            if(reservation.getStatus().equals("Canceled")){
                Log log = null;
                log.d("gone ","gone");
                reservation.setCanceled(1);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);
            }
            else {
                Log log = null;
                log.d("visible","visible");
                reservation.setCanceled(0);
                holder.btnCancel.setVisibility(View.VISIBLE);
            }

//            if(reservation.getCanceled() == 1){
//                holder.btnCancel.setVisibility(View.GONE);
//            }
//            else {
//                holder.btnCancel.setVisibility(View.VISIBLE);
//            }
//            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Perform the action when the button is clicked
//                    // Note that you no longer need to get the adapter position here
//                    // since 'position' is already provided in onBindViewHolder
//                    if (reservation.getStatus().equals(0)) {
//                        holder.btnCancel.setVisibility(View.GONE);
//                    }
//                }
//            });

        }


        @Override
        public int getItemCount() {
            return reservationsList.size();
        }

        public class ReservationViewHolder extends RecyclerView.ViewHolder {

            TextView tvReservationLocation;
            TextView tvReservationTime;
            TextView tvReservationStatus;
            Button btnCancel;
            Button btnEdit;

            public ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvReservationLocation = itemView.findViewById(R.id.tvReservationLocation);
                tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
                tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
                btnCancel = itemView.findViewById(R.id.cancel_button);
                btnEdit = itemView.findViewById(R.id.edit_button);

                btnEdit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // Get the position of the item clicked
                        int position = getAdapterPosition();
                        // Use position to get the reservation from your list
                        // Assuming Reservations is your data model class
                        Reservations reservation = reservationsList.get(position);

                        // Create an Intent to start Reservation Activity
                        Intent intent = new Intent(v.getContext(), UpdateReservation.class);

                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                        Log log = null;
//                        log.d("buildingugiugsofige: ", reservation.getBuildingName());
                        myEdit.putString("buildingtxt", reservation.getBuildingName());
                        myEdit.putString("uscidtxt", uscId);
                        myEdit.putString("indextxt", reservation.getKey());
                        myEdit.putString("categorytxt", reservation.getCategory());
                        myEdit.putString("datetxt", reservation.getDate());
                        myEdit.putString("timeslottxt", reservation.getTimeslot());
                        myEdit.apply();
                        v.getContext().startActivity(intent);
//                        here i want the profile page update and refresh but it does not do so for now with the finish what shou;ld i do?
                        finish();
                    }
                });


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the position of the item clicked
                        int position = getAdapterPosition();
                        // Use position to get the reservation from your list
                        Reservations reservation = reservationsList.get(position);

                        // Build the alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setTitle("Cancel Reservation");
                        builder.setMessage("Are you sure you want to cancel this reservation?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked Yes button
                                // Perform the status update and Firebase database update here
                                // Assuming you have a method to update the status in your Reservations model
                                reservation.setStatus(0);
                                reservation.setCanceled(1);
                                // Update Firebase
                                // You will need to pass a reference to the Firebase database and the user's ID to your adapter or use a callback to access it from your activity
                                notifyItemChanged(position);
                                dreference.child("users").child(uscId)
                                        .child("Reservation").child(reservation.getKey())
                                        .child("Status").setValue(0);
//                                hide the cancel button here
//                                btnCancel.setVisibility(View.GONE);
//                                btnEdit.setVisibility(View.GONE);
                                dreference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    public void onDataChange(@NonNull DataSnapshot snapshot){
                                        dreference.child("buildings").child(reservation.getBuildingName()).child("Operation Hour").child("Reservation_status").child(changeDateFormat(reservation.getDate())).child(reservation.getTimeslot()).child(reservation.getCategory()).setValue(snapshot.child("buildings").child(reservation.getBuildingName()).child("Operation Hour").child("Reservation_status").child(changeDateFormat(reservation.getDate())).child(reservation.getTimeslot()).child(reservation.Category).getValue(int.class)+1);
                                        dreference.child("users").child(uscId).child("Counter for 1 Day Reservation").child(changeDateFormat(reservation.getDate())).setValue(snapshot.child("users").child(uscId).child(
                                                "Counter for 1 Day Reservation").child(changeDateFormat(reservation.getDate())).getValue(int.class)-1);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked No button
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        }
    }
    public static class Reservations {
        private String BuildingName;
        private int Status;
        private String Category;
        private String Date;
        private String Timeslot;
        private String key;
        private int canceled=0;

        public Reservations() {
        }
        public int getCanceled() {
            return canceled;
        }

        public void setCanceled(int key) {
            this.canceled = key;
        }
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getBuildingName() {
            return BuildingName;
        }

        public void setBuildingName(String buildingName) {
            this.BuildingName = buildingName;
        }

        public String getStatus() {
            return Status == 1 ? "Active" : "Canceled";
        }

        public void setStatus(int status) {
            this.Status = status;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String category) {
            this.Category = category;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            this.Date = date;
        }

        public String getTimeslot() {
            return Timeslot;
        }

        public void setTimeslot(String timeslot) {
            this.Timeslot = timeslot;
        }

        public boolean isPastReservation() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mma", Locale.getDefault());
            Calendar reservationDateTime = Calendar.getInstance();
            Calendar currentDateTime = Calendar.getInstance();

            try {
                reservationDateTime.setTime(dateFormat.parse(this.Date + "/2023 " + this.Timeslot.toUpperCase()));
                return reservationDateTime.before(currentDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }

        public String getDateTime() {
            return this.Date + " " + this.Timeslot.toUpperCase(); // Example: "11/06 1:00PM"
        }

    }
    public static class User {
        private String BuildingName;
        private int Status;
        private String Category;
        private String Date;
        private String Timeslot;
        private String key;
        private String imageUrl;
        private int canceled = 0;
        private int registered = 1;
        private int uscid;
        private List<Reservations> activereservationlist = new ArrayList<>();
        private List<Reservations> cancelreservationlist = new ArrayList<>();
        private String defaultProfileImageUrl = "https://firebasestorage.googleapis.com/v0/b/findaseat-a81b4.appspot.com/o/trojan.jpg?alt=media&token=32d699ea-5fc8-41a5-9905-c87529b2e9b3";




        public int getTotalReservation() {
            return totalReservation;
        }

        public void setTotalReservation(int totalReservation) {
            this.totalReservation = 4;
        }
        public List<Reservations> getActiveReservationList() {
            return activereservationlist;
        }


        public int getCurrentDayReservation() {
            return currentDayReservation;
        }
        public Boolean reachMaximum(String uscID, String buildingName, String currentDate, String timeSlot, int currentDayReservation, int totalReservation){
            Boolean overflow = false;
            if(totalReservation > 4){
                overflow = true;
            }
            return overflow;
        }


        public void setCurrentDayReservation(int currentDayReservation) {
            this.currentDayReservation = currentDayReservation;
        }

        private int totalReservation;
        private int currentDayReservation;



        public User() {
            setImageUrl(defaultProfileImageUrl);
        }

        public int getCanceled() {
            return canceled;
        }

        public void setImageUrl(String key) {
            this.imageUrl = key;
        }
        public String getImageUrl() {
            return imageUrl;
        }

        public void setCanceled(int key) {
            this.canceled = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getBuildingName() {
            return BuildingName;
        }

        public void setBuildingName(String buildingName) {
            this.BuildingName = buildingName;
        }

        public String getStatus() {
            return Status == 1 ? "Active" : "Canceled";
        }

        public void setStatus(int status) {
            this.Status = status;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String category) {
            this.Category = category;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            this.Date = date;
        }
        public int getUscid() {
            return uscid;
        }

        public void setRegistered(int id) {
            this.registered = id;
        }
        public int getRegistered() {
            return registered;
        }

        public void setUscid(int id) {
            this.uscid = id;
        }

        public String getTimeslot() {
            return Timeslot;
        }

        public void setTimeslot(String timeslot) {
            this.Timeslot = timeslot;
        }
        public Boolean editReservation_user(String uscID, String buildingName, String currentDate, String timeSlot, int currentDayReservation, int totalReservation){
            Boolean temp = false;
            int temp2 = totalReservation-1;
            if(temp2 < totalReservation){
                temp = true;
            }
            return temp;
        }
        public int updateUser(String uscID, String buildingName, String currentDate, String timeSlot, int currentDayReservation, int totalReservation) {
            int temp=totalReservation+1;
            return temp;
        }
    }
    public static String changeDateFormat(String originalDate) {
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
}
