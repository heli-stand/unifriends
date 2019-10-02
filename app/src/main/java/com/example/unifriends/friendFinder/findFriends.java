package com.example.unifriends.friendFinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.unifriends.R;

public class FindFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends2);
    }

    public void viewMap(View view){
        Intent intent = new Intent(this, FriendFinderMap.class);
        startActivity(intent);
        finish();
    }
}
