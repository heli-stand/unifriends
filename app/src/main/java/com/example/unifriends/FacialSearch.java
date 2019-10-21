package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.UUID;

public class FacialSearch extends AppCompatActivity {

    private final int PICK_IMAGE = 1;
    private final String apiEndpoint = "https://tryface.cognitiveservices.azure.com/face/v1.0";
    private final String subscriptionKey = "40fef214516e4321bff17da500858306";
    private final String TAG = "Facial Search";
    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);
    private Face[] faces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_search);

        final ImageView iv = findViewById(R.id.imageView1);
        View.OnTouchListener otl = new View.OnTouchListener() {
            Matrix inverse = new Matrix();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                iv.getImageMatrix().invert(inverse);
                float[] pts = {
                        event.getX(), event.getY()
                };
                inverse.mapPoints(pts);
                Log.d("touch", "onTouch x: " + Math.floor(pts[0]) + ", y: " + Math.floor(pts[1]));
                for (Face face: faces){
//
                    if (faceClicked(face.faceRectangle,  Math.floor(pts[0]), Math.floor(pts[1]))){
                        Log.d("Face touched", face.faceId.toString());

                        searchFace(face.faceId);
                        break;
                    }
                }
                return false;
            }
        };
        findViewById(R.id.imageView1).setOnTouchListener(otl);
    }

    private boolean faceClicked(FaceRectangle faceRec, Double x, Double y){
        return (faceRec.top < y) && (faceRec.left < x) && ((faceRec.top+faceRec.height) > y)
                && (faceRec.left+faceRec.width > x);
    }

    public void chooseImage(View view){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(
                intent, "Select Picture"), PICK_IMAGE);
    }

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
                faces = detectAndFrame(bitmap);
//                UUID Id1 = detect(bitmap2);
//                verify(Id0,Id1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private Face[] detectAndFrame(final Bitmap imageBitmap) {

        String apiEndpoint = "https://tryface.cognitiveservices.azure.com/face/v1.0";
        String subscriptionKey = "40fef214516e4321bff17da500858306";
        final FaceServiceClient faceServiceClient =
                new FaceServiceRestClient(apiEndpoint, subscriptionKey);

        final UUID[] mFaceId0 = new UUID[1];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
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
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));
                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

//                    @Override
//                    protected void onPreExecute() {
//                        //TODO: show progress dialog
//                        detectionProgressDialog.show();
//                    }
//                    @Override
//                    protected void onProgressUpdate(String... progress) {
//                        //TODO: update progress
//                        detectionProgressDialog.setMessage(progress[0]);
//                    }

                    private static final String LOG_TAG = "LogActivity";

                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
//                        detectionProgressDialog.dismiss();

//                        if(!exceptionMessage.equals("")){
//                            showError(exceptionMessage);
//                        }
                        if (result == null) return;

                        ImageView imageView = findViewById(R.id.imageView1);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result));


                        for (Face face : result) {
                            UUID mFaceId = face.faceId;
                            mFaceId0[0] = mFaceId;
                            Log.d(LOG_TAG, mFaceId.toString());
                        }

                        //TextView textView = findViewById(R.id.verifyText);
                        //textView.setText(result[1].toString());
                    }
                };

        try{
            return detectTask.execute(inputStream).get();
        }catch (Exception e){
            return null;
        }
    }


    private static Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }

    private void searchFace(final UUID faceId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    QuerySnapshot document = task.getResult();
                    if (!task.getResult().isEmpty()) {
                        boolean macthed = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String uuidProfile = document.get("facialID").toString();
//                            final UUID uuidProfile = UUID.fromString(getIntent().getStringExtra("facialID"));
                            if (verify( document.get("facialID").toString(), faceId)){
                                Log.d(TAG, "Found, userId: " + document.getId() );
                                Intent intent = new Intent(FacialSearch.this, Profile.class);
                                intent.putExtra("userID", document.getId());
                                startActivity(intent);
                                macthed = true;
                                break;
                            }
                        }
                        Log.d(TAG, "Search Finished" );
                        if (!macthed){
                            Log.d(TAG, "Sorry this person is not registered" );
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private boolean verify(String uuidProfile, UUID faceID){
        final UUID selected = faceID;
        final UUID profilePhoto = UUID.fromString(uuidProfile);

//        try  {
//            //Your code goes here
//            VerifyResult result = faceServiceClient.verify(selected, profilePhoto);
//            DecimalFormat formatter = new DecimalFormat("#0.00");
//            String verificationResult = (result.isIdentical ? "The same person": "Different persons")
//                    + ". The confidence is " + formatter.format(result.confidence);
//            Log.d(TAG, verificationResult);
//
//            if (result.isIdentical){
//                Log.d(TAG, "Identical Person");
//                return true;
//            }else{
//                Log.d(TAG, "Log In Failed.Different Person");
//                return false;
//            }
//
////                    TextView textView = findViewById(R.id.verifyText);
////                    textView.setText(verificationResult);
//        } catch (Exception e) {
//            Log.d(TAG, "fail to verify" + e.toString());
////                    TextView textView = findViewById(R.id.verifyText);
////                    textView.setText("fail to verify");
//        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, VerifyResult> detectTask =
                new AsyncTask<InputStream, String, VerifyResult>() {
                    String exceptionMessage = "";

                    @Override
                    protected VerifyResult doInBackground(InputStream... params) {
                        try {
//                            publishProgress("Detecting...");
                            VerifyResult result = faceServiceClient.verify(selected, profilePhoto);
                            if (result == null){
                                publishProgress(
                                        "Detection Finished. Nothing detected");
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

//                    @Override
//                    protected void onPreExecute() {
//                        //TODO: show progress dialog
//                        detectionProgressDialog.show();
//                    }
//                    @Override
//                    protected void onProgressUpdate(String... progress) {
//                        //TODO: update progress
//                        detectionProgressDialog.setMessage(progress[0]);
//                    }

                    private static final String LOG_TAG = "LogActivity";


                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
//                        detectionProgressDialog.dismiss();

//                        if(!exceptionMessage.equals("")){
//                            showError(exceptionMessage);
//                        }

                        //TextView textView = findViewById(R.id.verifyText);
                        //textView.setText(result[1].toString());
                    }
                };

        try{
            return detectTask.execute(inputStream).get().isIdentical;
        }catch (Exception e){
            return false;
        }
    }

}
