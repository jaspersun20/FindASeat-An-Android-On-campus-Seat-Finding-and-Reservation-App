package com.example.finaaseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    FirebaseDatabase root = FirebaseDatabase.getInstance();
    DatabaseReference dreference = root.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText uscid = findViewById((R.id.uscid_login));
        final EditText password = findViewById((R.id.password_login));
        final Button loginButton = findViewById((R.id.register_button));
        final TextView createAcc = findViewById((R.id.login_account));

        // Set the listener for the back element
        View backButton = findViewById(R.id.back_button_login);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Profile activity
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uscTxt = uscid.getText().toString();
                final String passwordTxt = password.getText().toString();


                if(uscTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(Login.this, "Please enter your USC ID and password", Toast.LENGTH_SHORT).show();
                }
                if(!uscTxt.matches("\\d{10}")) {
                    Toast.makeText(Login.this, "Invalid USC ID. Must be 10 digit number.", Toast.LENGTH_SHORT).show();
                    // Stop further processing
                    return;
                }
                else{
                    dreference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(uscTxt)){
                                final String getPassword = snapshot.child(uscTxt).child("password").getValue(String.class);
                                if(getPassword.equals(passwordTxt)){
                                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    //When logging in
                                    myEdit.putBoolean("isLoggedIn", true);
                                    myEdit.putString("userId", uscTxt);
                                    myEdit.apply();
                                    // test
                                    boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                                    Log Log = null;
                                    Log.d("login login status: ", String.valueOf(isLoggedIn));

                                    Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                    // open map
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(Login.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(Login.this, "USC ID Not Registered", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

    }
}