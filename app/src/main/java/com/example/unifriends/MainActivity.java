package com.example.unifriends;

import android.content.Intent;
import android.os.Bundle;

//import com.example.unifriends.ui.login.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DocSnippets";

//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth.getInstance().signOut();
        user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.println(Log.ASSERT, "a","helloworld");
//        if (user == null){
//            startActivity(new Intent(MainActivity.this, Login.class));
//        }
        startActivity(new Intent(MainActivity.this, Login.class));
//        Log.println(Log.ASSERT, "a","helloworld");

//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Snackbar snackbar1 = Snackbar.make(view, "Message is restored!", Snackbar.LENGTH_SHORT);
//                        snackbar1.show();
//                        Log.println(Log.ASSERT, "a","helloworld");
//                    }
//                }).show();
//            }
//        });

        // Choose authentication providers


    }



    public void sendMessage(View view) {
        // Do something in response to button click

//        startActivity(new Intent(MainActivity.this, MobileAuth.class));
//        Log.println(Log.ASSERT, "a","helloworld");
//
//        // Create a new user with a first, middle, and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("middle", "Mathison");
//        user.put("last", "Turing");
//        user.put("born", 1912);
//
//        // Add a new document with a generated ID
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
    }
}
