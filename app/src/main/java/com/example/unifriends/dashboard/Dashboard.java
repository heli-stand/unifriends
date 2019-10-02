package com.example.unifriends.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.unifriends.R;
import com.example.unifriends.friendFinder.FindFriends;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void findFriendsClick(View view) {
        Intent intent = new Intent(this, FindFriends.class);
        startActivity(intent);
        finish();
    }
}
