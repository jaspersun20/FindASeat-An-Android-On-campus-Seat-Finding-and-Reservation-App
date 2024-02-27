package com.example.finaaseat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReservationRecord extends AppCompatActivity {

    // Firebase reference
    private static DatabaseReference mDatabase;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_record);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewReservations);
        // Set the LayoutManager to the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationRecord.this, Profile.class);
            startActivity(intent);
        });

        Button btnCompleted = findViewById(R.id.btn_completed_reservations);
        Button btnCanceled = findViewById(R.id.btn_canceled_reservations);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnCompleted.setOnClickListener(view -> fetchReservations(false));
        btnCanceled.setOnClickListener(view -> fetchReservations(true));

        fetchReservations(false);
    }

    private void fetchReservations(boolean fetchCanceled) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String uscId = sharedPreferences.getString("userId", "");
        mDatabase.child("users").child(uscId).child("Reservation")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Reservations> pastReservations = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Reservations reservation = snapshot.getValue(Reservations.class);
                            if (reservation != null ) {
                                // 0 is canceled, 1 is completed
                                if ((fetchCanceled && reservation.getStatusInt() == 0) || (!fetchCanceled && reservation.getStatusInt() == 1 && reservation.isPastReservation())) {
                                    pastReservations.add(reservation);
                                }
                            }
                        }
                        Collections.sort(pastReservations, new Comparator<Reservations>() {
                            @Override
                            public int compare(Reservations r1, Reservations r2) {
                                // "2023-11-06 1:00PM"
                                return r2.getDateTime().compareTo(r1.getDateTime());
                            }
                        });

                        if (pastReservations.size() > 10) {
                            pastReservations = pastReservations.subList(0, 10);
                        }

                        updateUI(pastReservations, fetchCanceled);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void updateUI(List<Reservations> reservations, boolean fetchCanceled) {
        recyclerView.setAdapter(new ReservationsAdapter(reservations));
    }

    public static class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

        private final List<Reservations> reservationsList;

        public ReservationsAdapter(List<Reservations> reservationsList) {
            this.reservationsList = reservationsList;
        }

        @NonNull
        @Override
        public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
            Reservations reservation = reservationsList.get(position);
            holder.tvReservationLocation.setText(String.format("Location: %s %s", reservation.getBuildingName(), reservation.getCategory()));
            holder.tvReservationTime.setText(String.format("Time: %s %s", reservation.getDate(), reservation.getTimeslot()));
            holder.tvReservationStatus.setText(String.format("Status: %s", reservation.getStatus()));
        }


        @Override
        public int getItemCount() {
            return reservationsList.size();
        }

        public class ReservationViewHolder extends RecyclerView.ViewHolder {

            TextView tvReservationLocation;
            TextView tvReservationTime;
            TextView tvReservationStatus;

            public ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvReservationLocation = itemView.findViewById(R.id.tvReservationLocation);
                tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
                tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
            }
        }
    }


    public static class Reservations {
        private String BuildingName;
        private int Status;
        private String Category;
        private String Date;
        private String Timeslot;

        public Reservations() {
        }

        public String getBuildingName() {
            return BuildingName;
        }

        public void setBuildingName(String buildingName) {
            this.BuildingName = buildingName;
        }

        public String getStatus() {
            return Status == 1 ? "Completed" : "Canceled";
        }

        public void setStatus(int status) {
            this.Status = status;
        }

        public int getStatusInt() {
            return Status;
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

}
