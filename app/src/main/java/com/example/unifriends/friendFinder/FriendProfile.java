package com.example.unifriends.friendFinder;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Ints;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FriendProfile extends AppCompatActivity {

    private String[] intsStrings = {"Music", "Traveling", "Dancing"," Movies"," Reading"," Writing",
            " Going out"," Sports"," Gaming"," Blogging"," Foodie"," History"," Outdoors",
            " Technology"," Cooking"," Programming"," Pets"," Cars"," Gym"," Gardening"};

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
                image.setImageResource(R.drawable.alexis);
                int[] intsArray = Ints.toArray((List<Integer>) user.get("interests"));
                ArrayList<String> ints = new ArrayList<>();
                for (int i : intsArray) {
                    if (i==1) {
                        ints.add(intsStrings[i]);
                    }
                }
                interests.setText(TextUtils.join(", ", ints));
            }
        });

    }


}
