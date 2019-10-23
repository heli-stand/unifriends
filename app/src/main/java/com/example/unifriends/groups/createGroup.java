package com.example.unifriends.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.unifriends.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class createGroup extends AppCompatActivity {
    public String userID;

    public static ArrayList<String> usersSubjects = new ArrayList<>();
    public static HashMap<String,String[]> allUsersSubjects = new HashMap<String, String[]>();
    public ArrayList<String> matches = new ArrayList<>();

    public static ArrayList<User> allUsers = new ArrayList<>();

    public static ArrayList<String> test = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        userID = getIntent().getStringExtra("userID");



        findMatches();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users").document(userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i("data", "DocumentSnapshot data: " + document.getData());
                        Log.i("subjects", document.get("subjects").toString());
                        String[] subjects = document.get("subjects").toString().split("[^a-zA-Z0-9\\s]+");

                        for(String s: subjects) {
                            usersSubjects.add(s);
                            Log.i("subjects in array", s);
                        }

                    } else {
                        Log.i("error", "No such document");


                    }
                } else {
                    Log.i("error", "get failed with ", task.getException());


                }
            }
        });

        //get all users

        for (String s: test) {
            Log.i("results in test in oncreate", s);
        }

        for(User user: allUsers) {
            Log.i("all users in create group", user.email);
        }
        
        RecyclerView recyclerView = findViewById(R.id.friendsbysublist);



        MyAdapter myAdapter = new MyAdapter(this, allUsers);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);




    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        Context context;
        ArrayList<User> users;

        public MyAdapter(Context context, ArrayList<User> users) {
            this.context = context;
            this.users = users;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.creategroupcustomlayout, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            User user = users.get(position);

            holder.textView.setText(user.name);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.nameText);
            }
        }
    }


    public void checkboxClicked(View view) {
        User u = (User) ((CheckBox) view).getTag();
        Log.i("checked", u.name);

    }

    public void getAllUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot results = task.getResult();

                    List<DocumentSnapshot> list = results.getDocuments();

                    for(DocumentSnapshot d: list) {
                        Log.i("result", d.getId());
                        test.add(d.getId());
                    }
                }

                for (String s: test) {
                    Log.i("results in test", s);
                }
            }


        });
    }


    public void findMatches() {
        Log.i("etnered", "etnered");

        for(String s: usersSubjects) {
            Log.i("users subs", s);
        }


        for (String key : allUsersSubjects.keySet()) {
            String[] subjects = allUsersSubjects.get(key);
            for (String subject : subjects) {
                for (String userSub : usersSubjects) {
                    if (userSub.equalsIgnoreCase(subject) && !key.equalsIgnoreCase(userID) && !userSub.equalsIgnoreCase(" ")) {
                        matches.add(key);
                    }
                }
            }
        }

        for(String key: allUsersSubjects.keySet()) {
            String[] subjects = allUsersSubjects.get(key);

            for(String s: subjects) {
                Log.i("user and sub", key + " " + s);
            }
        }



        for(String match: matches) {
            Log.i("matches", match);
        }


    }
}
