package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup2 extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "Signup";

    private Spinner degreeSpinner;
    private Spinner majorSpinner;

    private EditText nameEditText;
    private EditText uniEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        mAuth = FirebaseAuth.getInstance();


        nameEditText = findViewById(R.id.editText3);
        uniEditText = findViewById(R.id.editText4);

        degreeSpinner = findViewById(R.id.degree_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> degreeAdapter = ArrayAdapter.createFromResource(this,
                R.array.degree_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        degreeSpinner.setAdapter(degreeAdapter);

        majorSpinner = findViewById(R.id.major_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(this,
                R.array.major_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        majorSpinner.setAdapter(majorAdapter);

    }

//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        // On selecting a spinner item
//        String item = parent.getItemAtPosition(position).toString();
//
//        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//    }




    public void submit(View view){
//        String email = emailEditText.getText().toString();
//        String password = passwordEditText.getText().toString();
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//
//                            updateUserInfo();
////                            FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(Signup.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//                    }
//                });

    }

    public void updateUserInfo(View view){
        String degree = degreeSpinner.getSelectedItem().toString();
        String major = majorSpinner.getSelectedItem().toString();
        String name = nameEditText.getText().toString();
        String uni = uniEditText.getText().toString();

        String id = mAuth.getUid();

        Log.d(TAG, id);

        Map<String, Object> update = new HashMap<>();
        update.put("degree", degree);
        update.put("major", major);
        update.put("name", name);
        update.put("uni", uni);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(id)
                .update(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }
}
