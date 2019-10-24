package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.UUID;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

/**
 * Author: Li He
 * Email: lhe3@student.unimelb,edu.au
 * This class processes the photo picked by the user which is then compared to the faceid stored
 * stored for that particular email address, sign  in the user if they are identical
 */
public class facialLogin extends AppCompatActivity {

    private final String TAG = "FacialLogin";
    private final int PICK_IMAGE = 1;

    private static final String apiEndpoint = "https://tryface.cognitiveservices.azure.com/face/v1.0";
    private static final String subscriptionKey = "40fef214516e4321bff17da500858306";
    private static final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    private UUID selectedImage;

    /* async task to detect the face in the photo */
    private static class DetectTask extends AsyncTask<InputStream, String, Face[]>{
        String exceptionMessage = "";

        @Override
        protected Face[] doInBackground(InputStream... inputStreams) {
            try {
//                            publishProgress("Detecting...");
                return faceServiceClient.detect(
                        inputStreams[0],
                        true,         // returnFaceId
                        false,        // returnFaceLandmarks
                        null          // returnFaceAttributes:
                                    /* new FaceServiceClient.FaceAttributeType[] {
                                        FaceServiceClient.FaceAttributeType.Age,
                                        FaceServiceClient.FaceAttributeType.Gender }
                                    */
                );
            } catch (Exception e) {
                exceptionMessage = String.format(
                        "Detection failed: %s", e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPreExecute() {
            //TODO: show progress dialog
//                        detectionProgressDialog.show();
        }
        @Override
        protected void onProgressUpdate(String... progress) {
            //TODO: update progress
//                        detectionProgressDialog.setMessage(progress[0]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_login);
    }

    /* onclick method to choose image*/
    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(
                intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * handling the photo selected
     * @param requestCode request code
     * @param resultCode result code
     * @param data data passed in
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), uri);

                ImageView imageView = findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);
                selectedImage = detect(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * detect the faces in the photo
     * @param imageBitmap the selected photo
     * @return the first face detected by the microsoft api
     */
    private UUID detect(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        try{
            DetectTask detectTask = new DetectTask();
            return detectTask.execute(inputStream).get()[0].faceId;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * onClick function handles the verification
     * @param view clicked view
     */
    public void login(View view){
        final String email = ((EditText)findViewById(R.id.editTextEmail)).getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() == null || task.getResult().size() == 0){
                                Log.d(TAG, "Log In Failed, no such user");
                            } else if (task.getResult().size() > 1){
                                Log.d(TAG, "Log In Fained, Multiple User Conflict");
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    verify(email, document.get("password").toString(),
                                            document.get("facialID").toString());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Using Microsoft api to verify if two persons are identical
     * @param email registered email
     * @param password user's password
     * @param uuid a string stored to construct uuid
     */
    private void verify(final String email, final String password, String uuid){
        final UUID uuidProfile = UUID.fromString(uuid);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String LOG_TAG = "LogActivity";
                try  {
                    //Your code goes here
                    VerifyResult result;
                    result = new VerifyResult();
                    result = faceServiceClient.verify(uuidProfile,selectedImage);
                    DecimalFormat formatter = new DecimalFormat("#0.00");
                    String verificationResult = (result.isIdentical ? "The same person": "Different persons")
                            + ". The confidence is " + formatter.format(result.confidence);
                    Log.d(LOG_TAG, verificationResult);

                    if (result.isIdentical){
                        Log.d(TAG, "Identical Person");
                        signin(email, password);
                    }else{
                        Log.d(TAG, "Log In Failed.Different Person");
                    }

//                    TextView textView = findViewById(R.id.verifyText);
//                    textView.setText(verificationResult);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "fail to verify" + e.toString());
//                    TextView textView = findViewById(R.id.verifyText);
//                    textView.setText("fail to verify");
                }
            }
        });
        thread.start();
    }

    /**
     * sign in the user using the user's email and password
     * @param email email address
     * @param password password
     */
    private void signin(String email, String password){
        final FirebaseAuth mAuth =  FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Sign in:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Sign in:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    public void onLoginClick(View View) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }


    public void onRegisterClick(View View) {
        startActivity(new Intent(this, Signup.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }
}
