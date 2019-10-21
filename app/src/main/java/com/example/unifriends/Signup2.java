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

public class Signup2 extends AppCompatActivity {

    private static final String TAG = "Signup2";
    private UUID facialID;
    private final int PICK_IMAGE = 1;
    private Bitmap bitmap;


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

//                // Comment out for tutorial
                facialID = detectAndFrame(bitmap);
//                UUID Id1 = detect(bitmap2);
//                verify(Id0,Id1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private UUID detectAndFrame(final Bitmap imageBitmap) {

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
            return detectTask.execute(inputStream).get()[0].faceId;
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


    public void updateUserInfo(View view){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://unifriends-d63b5.appspot.com/>");
        // Create a reference to "mountains.jpg"

        StorageReference mountainsRef = storageRef.child("usersImage/" + FirebaseAuth.getInstance().getUid() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
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
                startActivity(intent);
            }
        });
    }
}
