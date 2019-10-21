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

public class facialLogin extends AppCompatActivity {

    private final String TAG = "FacialLogin";
    private final int PICK_IMAGE = 1;

    private final String apiEndpoint = "https://tryface.cognitiveservices.azure.com/face/v1.0";
    private final String subscriptionKey = "40fef214516e4321bff17da500858306";
    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    private UUID selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_login);
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(
                intent, "Select Picture"), PICK_IMAGE);
    }

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

//                // Comment out for tutorial
//                facialID = detectAndFrame(bitmap);
                selectedImage = detect(bitmap);

//                Log.e("facialLogin", Id1.toString());

//                verify(Id0,Id1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private UUID detect(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        final UUID[] mFaceId0 = new UUID[1];
        AsyncTask<InputStream, String, Face[]> detectTask =
                new  AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
//                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    null          // returnFaceAttributes:
                                    /* new FaceServiceClient.FaceAttributeType[] {
                                        FaceServiceClient.FaceAttributeType.Age,
                                        FaceServiceClient.FaceAttributeType.Gender }
                                    */
                            );
                            if (result == null){
//                                publishProgress(
//                                        "Detection Finished. Nothing detected");
                                return null;
                            }
//                            publishProgress(String.format(
//                                    "Detection Finished. %d face(s) detected",
//                                    result.length));
                            return result;
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

                    private static final String LOG_TAG = "LogActivity";

                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
//                        detectionProgressDialog.dismiss();

                        if(!exceptionMessage.equals("")){
//                            showError(exceptionMessage);
                        }
                        if (result == null) return;

                        for (Face face : result) {
                            UUID mFaceId = face.faceId;
                            mFaceId0[0] = mFaceId;
//                            Log.d(LOG_TAG, mFaceId.toString());
                        }

                        //TextView textView = findViewById(R.id.verifyText);
                        //textView.setText(result[1].toString());
                    }
                };
        try{
            return detectTask.execute(inputStream).get()[0].faceId;
        }catch (Exception e){
            return null;
        }
    }

    public void back(View view){
        finish();
    }

    public void login(View view){

        final String email = ((EditText)findViewById(R.id.editText1)).getText().toString();
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

}
