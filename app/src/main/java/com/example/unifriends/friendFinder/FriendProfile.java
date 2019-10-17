package com.example.unifriends.friendFinder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.unifriends.R;
import java.util.ArrayList;
import java.util.List;

public class FriendProfile extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_friend_profile);

        String name = getIntent().getStringExtra("name");
        int img = getIntent().getIntExtra("img", 0);
        List<String> ints = (ArrayList<String>) getIntent().getSerializableExtra("interests");


        ImageView image = findViewById(R.id.img);
        TextView nametext = findViewById(R.id.nametext);
        TextView interests = findViewById(R.id.interests);

        image.setImageResource(img);
        nametext.setText(name);
        interests.setText(TextUtils.join(", ", ints));
    }


}
