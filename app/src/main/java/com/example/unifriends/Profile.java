package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private String userID;
    private final String TAG = "Profile";
    private String facialID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userID = getIntent().getStringExtra("userID");
        getData(userID);
    }

    private void getData(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        setText(document.get("name").toString(), document.get("uni").toString(),
                                document.get("major").toString(), document.get("degree").toString()
                                , document.get("email").toString());
                        setFacialID(document.get("facialID").toString());
                        setPhoto(document.get("photo").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setText(String name, String uni, String major, String degree, String email){
        EditText editText = findViewById(R.id.editText);
        String display = name + uni + major+ degree + email;
        editText.setText(display);
    }

    private void setFacialID(String facialID){
        this.facialID = facialID;
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
                ImageView imageView = findViewById(R.id.imageView1);
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



    public void goToVerification(View view){
        Intent intent = new Intent(Profile.this, Verification.class);
        intent.putExtra("facialID", facialID);
        startActivity(intent);
    }


}
