package com.example.unifriends;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.unifriends.friendFinder.FindFriends;

import com.example.unifriends.events.Calendar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.chat.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.unifriends.friendFinder.FindFriends.usersGroups;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";


    FirebaseFirestore db = FirebaseFirestore.getInstance();
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

        getUsersGroups();



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
                }
            });
        }

        Log.i("current user", FirebaseAuth.getInstance().getCurrentUser().getUid());



    }

    public void getUsersGroups(){
        final List<String> currentEvents = new ArrayList<String>();

        final DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i("usersgroups in find friends current groups", document.get("groups").toString());
                        String test = document.get("groups").toString();
                        String test2 = test.replaceAll("[^\\w\\s]", "");
                        String test3 = test2.trim();
                        String[] groups = test3.split("\\s+");

                        for(String s: groups) {
                            usersGroups.add(s);
                        }
                    }
                }
            }
        });
    }




    private void getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
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
                            setPhoto(doc.get("photo").toString());
                            sp.edit().putString("name",doc.getString("name")).apply();
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

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

    public void goToFindFriends(View view) {
        Intent intent = new Intent(MainActivity.this, FindFriends.class);
        intent.putExtra("userID", user.getUid());
        startActivity(intent);
    }

    private void setPhoto(String source){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(source);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ImageView imageView = findViewById(R.id.profileImage);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, exception.toString());
            }
        });
    }

    public void goToEvent(View view){
        Intent intent = new Intent(MainActivity.this, Calendar.class);
        startActivity(intent);
    }

}
