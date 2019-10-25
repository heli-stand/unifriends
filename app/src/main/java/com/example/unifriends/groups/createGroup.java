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
import android.widget.Toast;

import com.example.unifriends.MainActivity;
import com.example.unifriends.friendFinder.FindFriends;
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



    public static List<String> selectedUsers = new ArrayList<>();


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ArrayList<User> matchesUser = new ArrayList<>();

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
        MatchAdapter matchAdapter = new MatchAdapter(this, matchesUser);

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
        ArrayList<User> matches;

        public MatchAdapter(Context context, ArrayList<User> matches) {
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
            User u = matches.get(position);

            holder.textView.setText(u.name);
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
        Log.i("checked", Integer.toString(c.getId()) + allUsers.get(c.getId()).id );

        if(!selectedUsers.contains(allUsers.get(c.getId()).id)) {
            selectedUsers.add(allUsers.get(c.getId()).id);
        } else {
            selectedUsers.remove(allUsers.get(c.getId()).id);
        }

    }

    public void checkboxClickedBySubject(View view) {
        CheckBox c = ((CheckBox) view);
        Log.i("checked", Integer.toString(c.getId()) + matchesUser.get(c.getId()).id);

        if(!selectedUsers.contains(matchesUser.get(c.getId()).id)) {
            selectedUsers.add(matchesUser.get(c.getId()).id);
        } else {
            selectedUsers.remove(matchesUser.get(c.getId()).id);
        }

    }




    public void createGroup(View view) {
        Log.i("click", "this has been clicked");

        EditText editGroupName = findViewById(R.id.groupNameEditText);
        String groupName = editGroupName.getText().toString();

        DocumentReference newGroupRef = db.collection("group").document();
        Map<String, Object> data = new HashMap<>();

        //add current user to group

       if(selectedUsers.size() == 0 || groupName.equalsIgnoreCase("")) {
           Toast.makeText(getApplicationContext(),"Please enter a group name or select users to add to group",Toast.LENGTH_LONG).show();
       } else {
           selectedUsers.add(userID);


           data.put("name",groupName );
           data.put("members", selectedUsers);
           data.put("events",null);

           newGroupRef.set(data);

           Log.i("new doc id", newGroupRef.getId());

           for(String id: selectedUsers) {
               addNewGroupToUser(id, newGroupRef.getId());
           }
       }

    }

    public void addNewGroupToUser(String userId, String groupRef) {
        //get users current groups
        final List<String> currentEvents = new ArrayList<String>();

        final DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i("users current groups", document.get("groups").toString());
                        String test = document.get("groups").toString();
                        String test2 = test.replaceAll("[^\\w\\s]", "");
                        String test3 = test2.trim();
                        String[] groups = test3.split("\\s+");

                        for(String s: groups) {
                            currentEvents.add(s);
                        }
                    }
                }
            }
        });


        //add group
        DocumentReference userRef = db.collection("users").document(userId);


        currentEvents.add(groupRef);

        userRef
                .update("groups", currentEvents)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("success", "DocumentSnapshot successfully updated!");
                        Toast.makeText(getApplicationContext(),"Group Successfully Created!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(createGroup.this, FindFriends.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("fail", "Error updating document", e);
                    }
                });


    }

    public void addGroupToExisting(String groupRef, String userId, List<String> currentEvents) {
        //add group
        DocumentReference userRef = db.collection("users").document(userId);


        currentEvents.add(groupRef);

        userRef
                .update("groups", currentEvents)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("success", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("fail", "Error updating document", e);
                    }
                });
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
            Log.i("users subs in creategroup", s);
        }



        for(User u: allUsers) {
            List<String> subjects = u.getSubjects();
            for(String s: subjects) {
                for(String userSub: usersSubjects) {
                    if(userSub.equalsIgnoreCase(s) && !u.id.equalsIgnoreCase(userID)) {
                        matchesUser.add(u);
                    }
                }
            }
        }

        for(User u: matchesUser) {
           Log.i("MATCHED USER", u.id);
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
