package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view){

        String email = ((TextView)findViewById(R.id.editTextEmail)).getText().toString();
        String password = ((TextView)findViewById(R.id.editTextPassword)).getText().toString();

        final FirebaseAuth mAuth =  FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Sign in:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Sign in:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    public void signup(View view) { startActivity(new Intent(Login.this, Signup.class));}

    public void facialLogin(View view){startActivity(new Intent(Login.this,
            facialLogin.class)); }
}
