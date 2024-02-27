package com.example.finaaseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    FirebaseDatabase root = FirebaseDatabase.getInstance();
    DatabaseReference dreference = root.getReference();
    private final String defaultProfilePicUrl = "https://firebasestorage.googleapis.com/v0/b/findaseat-a81b4.appspot.com/o/trojan.jpg?alt=media&token=32d699ea-5fc8-41a5-9905-c87529b2e9b3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText name = findViewById((R.id.name));
        final EditText email = findViewById((R.id.email));
        final EditText uscid = findViewById((R.id.uscid_register));
        final EditText password = findViewById((R.id.password_register));
        final Button registerButton = findViewById((R.id.register_button));
        final TextView loginacc = findViewById((R.id.login_account));



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameTxt = name.getText().toString();
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String uscTxt = uscid.getText().toString();

                if(nameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty() || uscTxt.isEmpty()){
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                if(!uscTxt.matches("\\d{10}")) {
                    Toast.makeText(Register.this, "Invalid USC ID. Must be 10 digit number.", Toast.LENGTH_SHORT).show();
                    // Stop further processing
                    return;
                }
                else {
//                    dreference.child("users").setValue("111");

                    dreference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(uscTxt)){
                                Toast.makeText(Register.this, "USCID is already registered", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                // use email as unique identity
                                dreference.child("users").child(uscTxt).child("name").setValue(nameTxt);
                                dreference.child("users").child(uscTxt).child("email").setValue(emailTxt);
                                dreference.child("users").child(uscTxt).child("uscid").setValue(uscTxt);
                                dreference.child("users").child(uscTxt).child("password").setValue(passwordTxt);
                                dreference.child("users").child(uscTxt).child("profile").setValue(defaultProfilePicUrl);


                                Toast.makeText(Register.this, "Registered Successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

        loginacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }
}