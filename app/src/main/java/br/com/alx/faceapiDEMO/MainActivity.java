package br.com.alx.faceapiDEMO;

import android.os.Bundle;

import java.io.*;
import java.text.DecimalFormat;
import java.util.UUID;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;
import android.util.Log;
import android.content.res.AssetManager;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

public class MainActivity extends Activity{

    private final String apiEndpoint = "https://tryface.cognitiveservices.azure.com/face/v1.0";

    private final String subscriptionKey = "40fef214516e4321bff17da500858306";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(
                        intent, "Select Picture"), PICK_IMAGE);
            }
        });

        detectionProgressDialog = new ProgressDialog(this);
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
                AssetManager assetManager = getAssets();
                InputStream is = assetManager.open("test.jpg");
                Bitmap bitmap2 = BitmapFactory.decodeStream(is);
                ImageView imageView = findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);

                // Comment out for tutorial
                UUID Id0 = detectAndFrame(bitmap);
                UUID Id1 = detect(bitmap2);
                verify(Id0,Id1);
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
        AsyncTask<InputStream, String, Face[]>  detectTask =
                new  AsyncTask<InputStream, String, Face[]>() {
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

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    private static final String LOG_TAG = "LogActivity";

                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();

                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (result == null) return;

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

    // Detect faces by uploading a face image.
    // Frame faces after detection.
    private UUID detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        final UUID[] mFaceId0 = new UUID[1];
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

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    private static final String LOG_TAG = "LogActivity";

                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();

                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (result == null) return;

                        ImageView imageView = findViewById(R.id.imageView1);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();

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



    private void verify(UUID Id0, UUID Id1){
        final UUID id1 = Id1;
        final UUID id0 = Id0;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String LOG_TAG = "LogActivity";
                try  {
                    //Your code goes here
                    VerifyResult result;
                    result = new VerifyResult();
                    result = faceServiceClient.verify(id0,id1);
                    DecimalFormat formatter = new DecimalFormat("#0.00");
                    String verificationResult = (result.isIdentical ? "The same person": "Different persons")
                            + ". The confidence is " + formatter.format(result.confidence);
                    Log.d(LOG_TAG, verificationResult);
                    TextView textView = findViewById(R.id.verifyText);
                    textView.setText(verificationResult);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "fail to verify" + e.toString());
                    TextView textView = findViewById(R.id.verifyText);
                    textView.setText("fail to verify");
                }
            }
        });
        thread.start();

    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .create().show();
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
}


