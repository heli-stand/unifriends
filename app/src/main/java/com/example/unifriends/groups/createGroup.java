package com.example.unifriends.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.unifriends.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.example.unifriends.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class createGroup extends AppCompatActivity {
    public String userID;

    public static ArrayList<String> usersSubjects = new ArrayList<>();
    public static HashMap<String,String[]> allUsersSubjects = new HashMap<String, String[]>();
    public ArrayList<String> matches = new ArrayList<>();

    public static HashMap<String, String> allUsersIdName = new HashMap<>();

    public static ArrayList<User> allUsers = new ArrayList<>();


    public static ArrayList<String> test = new ArrayList<>();



    public static ArrayList<String> selectedAllUsers = new ArrayList<>();
    public static ArrayList<String> selectedUserBySub = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        userID = getIntent().getStringExtra("userID");






        //get all users

        for (String s: test) {
            Log.i("results in test in oncreate", s);
        }

        for(User user: allUsers) {
            Log.i("all users in create group", user.email);
        }

        for(String user: usersSubjects) {
            Log.i("all subjects in user group", user);
        }


        RecyclerView recyclerView = findViewById(R.id.allUsers);

        RecyclerView recyclerViewSubList = findViewById(R.id.friendsBySubList);


        MyAdapter myAdapter = new MyAdapter(this, allUsers);
        MatchAdapter matchAdapter = new MatchAdapter(this, matches);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        findMatches();
        recyclerViewSubList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSubList.setAdapter(matchAdapter);


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
            holder.checkBox.setId(position);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            CheckBox checkBox;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.nameText);
                checkBox = itemView.findViewById(R.id.selectorCheckBox);
            }
        }
    }

    public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MyViewHolder> {
        Context context;
        ArrayList<String> matches;

        public MatchAdapter(Context context, ArrayList<String> matches) {
            this.context = context;
            this.matches = matches;
        }

        @NonNull
        @Override
        public MatchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.creategroupcustomlayoutbysubject, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MatchAdapter.MyViewHolder holder, int position) {



            holder.textView.setText(allUsersIdName.get(matches.get(position)));
            holder.checkBox.setId(position);
        }

        @Override
        public int getItemCount() {
            return matches.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            CheckBox checkBox;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.nameText);
                checkBox = itemView.findViewById(R.id.selectorCheckBox);
            }
        }
    }


    public void checkboxClickedAllUsers(View view) {
        CheckBox c = ((CheckBox) view);
        Log.i("checked", Integer.toString(c.getId()) + allUsers.get(c.getId()).email );

        if(!selectedAllUsers.contains(allUsers.get(c.getId()).email)) {
            selectedAllUsers.add(allUsers.get(c.getId()).email);
        } else {
            selectedAllUsers.remove(allUsers.get(c.getId()).email);
        }

    }

    public void checkboxClickedBySubject(View view) {
        CheckBox c = ((CheckBox) view);
        Log.i("checked", Integer.toString(c.getId()) + matches.get(c.getId()));

        if(!selectedUserBySub.contains(matches.get(c.getId()))) {
            selectedUserBySub.add(matches.get(c.getId()));
        } else {
            selectedUserBySub.remove(matches.get(c.getId()));
        }

    }




//    public void createGroup(View view) {
//        Log.i("click", "this has been clicked");
//
//        for(String s: selectedUserBySub) {
//            Log.i("selected users by sub", s);
//        }
//
//        for(String s: selectedAllUsers) {
//            Log.i("selected all users", s);
//
//        }
//
//        EditText groupNameEditText = findViewById(R.id.groupNameEditText);
//
//
//        Map<String, Object> update = new HashMap<>();
//        update.put("name", groupNameEditText.getText());
//
//
//        update.put("major", major);
//        update.put("name", name);
//        update.put("uni", uni);
//        update.put("photo", "usersImage/" + FirebaseAuth.getInstance().getUid() + ".jpg");
//        update.put("facialID", getIntent().getStringExtra("facialID"));
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("users").document(id)
//                .update(update)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
//
//                        startActivity(intent);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
//
//                        Log.w(TAG, "Error writing document", e);
//                    }
//                });
//
//
//
//    }

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
            Log.i("users subs in creategroup", s);
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
                Log.i("user and sub", key + " " + s + allUsersSubjects.size());
            }
        }

        // WORKS

        for(String key: allUsersIdName.keySet()) {
            Log.i("user id and name", key + " " + allUsersIdName.get(key));
        }



        for(String match: matches) {
            Log.i("final match", match + " " + matches.size());
        }


    }
}
