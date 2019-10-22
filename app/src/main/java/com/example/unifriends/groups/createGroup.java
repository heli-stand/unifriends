package com.example.unifriends.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;

import com.example.unifriends.R;

public class createGroup extends AppCompatActivity {

    final FirebaseAuth mAuth =  FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //get data for the by subject



    }
}
