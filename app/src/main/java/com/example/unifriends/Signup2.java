package com.example.unifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

/**
 * Author: Li He
 * Email: lhe3@student.unimelb.edu.au
 * This class contains the functions to let user choose and upload a photo
 * for profile use and facial recognition use
 */
public class Signup2 extends AppCompatActivity {

    private static final String TAG = "Signup2";
    private UUID facialID;
    private final int PICK_IMAGE = 1;
    private Bitmap bitmap;

    static String apiEndpoint = "https://tryface.cognitiveservices.azure.com/face/v1.0";
    static String subscriptionKey = "40fef214516e4321bff17da500858306";
    static final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

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
        setContentView(R.layout.activity_signup2);
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
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), uri);

                ImageView imageView = findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);

                detectAndFrame(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }

    /**
     * function takes in the photo selected, and draw a rectangle on the face
     * @param imageBitmap selected photo
     * @return the uuid (facial id) of the person
     */
    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        DetectTask detectTask = new DetectTask();

        try{
            ImageView imageView = findViewById(R.id.imageView1);
            Face[] faces = detectTask.execute(inputStream).get();
            if (faces.length == 0){
                Log.d(TAG, "Detection Failed, nobody is detected");
            }else{
                imageView.setImageBitmap(
                        drawFaceRectanglesOnBitmap(imageBitmap, faces));
                facialID =  faces[0].faceId;
            }
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }
    }

    /**
     * function draws an red rectangle on the faces
     * @param originalBitmap the source image
     * @param faces the data generated by the Microsoft
     * @return the processed bitmap
     */
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
            FaceRectangle faceRectangle = faces[0].faceRectangle;
            canvas.drawRect(
                    faceRectangle.left,
                    faceRectangle.top,
                    faceRectangle.left + faceRectangle.width,
                    faceRectangle.top + faceRectangle.height,
                    paint);

        }
        return bitmap;
    }


    /**
     *  click method,upload the image to the firebase storage
     * @param view clicked view
     */
    public void updateUserInfo(View view){
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://unifriends-d63b5.appspot.com");
        // Create a reference to "mountains.jpg"

        StorageReference mountainsRef = storageRef.child("usersImage/" + FirebaseAuth.getInstance().getUid() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                Log.d(TAG, "Image upload done");

                Intent intent = new Intent(Signup2.this, Signup3.class);
                intent.putExtra("facialID", facialID.toString());
                /* pass the facial id to the next activity*/
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                startActivity(intent);
            }
        });
    }
}
