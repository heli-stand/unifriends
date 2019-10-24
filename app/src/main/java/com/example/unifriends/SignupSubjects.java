package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupSubjects extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_subjects);
    }

    public void updateSubjectInfo(View view) {
        EditText sub1Input = (EditText)findViewById(R.id.editTextSub1);
        EditText sub2Input = (EditText)findViewById(R.id.editTextSub2);
        EditText sub3Input = (EditText)findViewById(R.id.editTextSub3);
        EditText sub4Input = (EditText)findViewById(R.id.editTextSub4);

        String sub1 = sub1Input.getText().toString();
        String sub2 = sub2Input.getText().toString();
        String sub3 = sub3Input.getText().toString();
        String sub4 = sub4Input.getText().toString();

        if(sub1.equalsIgnoreCase("") || sub2.equalsIgnoreCase("") || sub3.equalsIgnoreCase("") || sub4.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),"Please enter a total of 4 subjects",Toast.LENGTH_LONG).show();
        } else {


            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String id = mAuth.getUid();

            /* create a map object to store the data*/
            Map<String, Object> update = new HashMap<>();
            List<String> subjects = new ArrayList<>();

            subjects.add(sub1);
            subjects.add(sub2);
            subjects.add(sub3);
            subjects.add(sub4);


            update.put("subjects", subjects);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(id)
                    .update(update)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("outcome", "DocumentSnapshot successfully written!");
                            Intent intent = new Intent(getApplicationContext(), Signup4.class);
                            // to the next step
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                            Log.i("outcome", "Error writing document", e);
                        }
                    });

        }


    }
}
