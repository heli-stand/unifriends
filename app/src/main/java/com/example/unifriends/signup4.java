package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class signup4 extends AppCompatActivity {
    public final String TAG = "Interest Sign Up";

    private String[] interet_string = {"Music", "Traveling", "Dancing"," Movies"," Reading"," Writing",
            " Going out"," Sports"," Gaming"," Blogging"," Foodie"," History"," Outdoors",
            " Technology"," Cooking"," Programming"," Pets"," Cars"," Gym"," Gardening"};
    private Integer[] interests = new Integer[20];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);
        Arrays.fill(interests, 0);

        final LinearLayout ll = findViewById(R.id.linear_layout);

        LinearLayout sub_layout = new LinearLayout(this);
        sub_layout.setOrientation(LinearLayout.HORIZONTAL);
        for(int i = 0; i < 20; i++) {
            if (i % 3 == 0 && i != 0) {
                ll.addView(sub_layout);
                sub_layout = new LinearLayout(this);
                sub_layout.setOrientation(LinearLayout.HORIZONTAL);
            }
            final int index = i;
            CheckBox ch = new CheckBox(this);
            ch.setText(interet_string[i]);
            ch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = ((CheckBox) v).isChecked();
                    if (checked){interests[index] = 1;}else{interests[index] = 0;}
                    Log.d("Onclick", java.util.Arrays.toString(interests) );
                }
            });
            sub_layout.addView(ch);
        }
        ll.addView(sub_layout);

    }

    public void submit(View view){
        Map<String, Object> update = new HashMap<>();
        update.put("interets", Arrays.asList(interests));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .update(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}

