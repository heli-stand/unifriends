package com.example.unifriends.friendFinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.unifriends.R;

import java.util.ArrayList;

public class FindFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends2);

        RecyclerView findFriendsView = (RecyclerView) findViewById(R.id.friend_recycler_view);
        findFriendsView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        ArrayList<Friend> tempList = new ArrayList<>();

        ArrayList<String> tempInterests = new ArrayList<>();
        ArrayList<String> tempSubjects = new ArrayList<>();

        tempInterests.add("Sports");
        tempInterests.add("Gaming");
        tempInterests.add("Workouts");
        tempInterests.add("Skiing");
        tempSubjects.add("Computing and Software Systems");
        tempSubjects.add("Painting");
        tempSubjects.add("Subject3");
        tempSubjects.add("Subject4");

        tempList.add(new Friend("John", "Doe", "CompSci", "Male", tempInterests, tempSubjects));
        tempList.add(new Friend("Jane", "Watson", "Computer Science", "Female", tempInterests, tempSubjects));

        FriendAdapter adapter = new FriendAdapter(tempList);

        findFriendsView.setAdapter(adapter);
        findFriendsView.setLayoutManager(layoutManager);
    }

    public void viewMap(View view){
        Intent intent = new Intent(this, FriendFinderMap.class);
        startActivity(intent);
        finish();
    }
}
