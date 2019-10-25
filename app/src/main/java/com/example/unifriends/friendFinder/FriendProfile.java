package com.example.unifriends.friendFinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Ints;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FriendProfile extends AppCompatActivity {
    private static final String TAG = "FriendProfile";

    private String[] intsStrings = {"Music", "Traveling", "Dancing"," Movies"," Reading"," Writing",
            " Going out"," Sports"," Gaming"," Blogging"," Foodie"," History"," Outdoors",
            " Technology"," Cooking"," Programming"," Pets"," Cars"," Gym"," Gardening"};

    public GeoPoint location;
    public String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_friend_profile);

        String id = getIntent().getStringExtra("id");

        final ImageView image = findViewById(R.id.img);
        final TextView nametext = findViewById(R.id.nametext);
        final TextView interests = findViewById(R.id.interests);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(id).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot user = task.getResult();
                nametext.setText(user.getString("name"));
                userName = user.getString("name");
                setPhoto(user.get("photo").toString(), image);
                int[] intsArray = Ints.toArray((List<Integer>) user.get("interests"));
                ArrayList<String> ints = new ArrayList<>();
                for (int i = 0; i < intsArray.length; i++) {
                    if (intsArray[i] == 1) {
                        ints.add(intsStrings[i]);
                    }
                }
                interests.setText(TextUtils.join(", ", ints));
                location = user.getGeoPoint("location");
            }
        });



    }

    private void setPhoto(String source, final ImageView imageView){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(source);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, exception.toString());
            }
        });
    }


    public void goToMap(View view) {


        Intent intent = new Intent(this, FriendFinderMap.class);
        intent.putExtra("lat", Double.toString(location.getLatitude()));
        intent.putExtra("lon", Double.toString(location.getLongitude()));
        intent.putExtra("selectedUserName", userName);
        startActivity(intent);
    }


}
