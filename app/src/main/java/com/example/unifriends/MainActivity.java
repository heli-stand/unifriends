package com.example.unifriends;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.chat.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";


//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    TextView welcomeMessage, signOut, chatActivity;
    String uid = "";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            startActivity(new Intent(MainActivity.this, Login.class));
        }else{
            getUserId();
            setProfile();
            welcomeMessage = findViewById(R.id.greeting_msg);

            sp = getSharedPreferences("login", MODE_PRIVATE);

            signOut = findViewById(R.id.signOutButton);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    sp.edit().putBoolean("logged",false).apply();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }
            });

            chatActivity = findViewById(R.id.chatActivity);
            chatActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sp.edit().putBoolean("logged",true).apply();
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                    finish();
                }
            });
        }


    }




    private void getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
        }
    }

    private void setProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            welcomeMessage.setText(String.format("Welcome, %s!", doc.getString("name")));
                            sp.edit().putString("name",doc.getString("name")).apply();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void goToFacialSearch(View view){
        Intent intent = new Intent(MainActivity.this, FacialSearch.class);
        startActivity(intent);
    }


    public void goToProfile(View view){
        Intent intent = new Intent(MainActivity.this, Profile.class);
        intent.putExtra("userID", user.getUid());
        startActivity(intent);
    }

}
