package com.example.unifriends;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.unifriends.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.UUID;

public class Verification extends AppCompatActivity {

    ImageView imageView;
    private final String TAG = "Verification";
    private final String apiEndpoint = "https://tryface.cognitiveservices.azure.com/face/v1.0";
    private final String subscriptionKey = "40fef214516e4321bff17da500858306";
    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnCamera = (Button) findViewById(R.id.btnCamera);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        // SEND IMAGE FOR FACE ID MATCH

        imageView.setImageBitmap(bitmap);

        UUID photoTaken = detect(bitmap);
        verify(photoTaken);
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

    private void verify(final UUID photoTaken){
        final UUID uuidProfile = UUID.fromString(getIntent().getStringExtra("facialID"));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try  {
                    //Your code goes here
                    VerifyResult result;
                    result = new VerifyResult();
                    result = faceServiceClient.verify(uuidProfile, photoTaken);
                    DecimalFormat formatter = new DecimalFormat("#0.00");
                    String verificationResult = (result.isIdentical ? "The same person": "Different persons")
                            + ". The confidence is " + formatter.format(result.confidence);
                    Log.d(TAG, verificationResult);

                    if (result.isIdentical){
                        Log.d(TAG, "Identical Person");
                    }else{
                        Log.d(TAG, "Log In Failed.Different Person");
                    }

//                    TextView textView = findViewById(R.id.verifyText);
//                    textView.setText(verificationResult);
                } catch (Exception e) {
                    Log.d(TAG, "fail to verify" + e.toString());
//                    TextView textView = findViewById(R.id.verifyText);
//                    textView.setText("fail to verify");
                }
            }
        });
        thread.start();
    }
}
