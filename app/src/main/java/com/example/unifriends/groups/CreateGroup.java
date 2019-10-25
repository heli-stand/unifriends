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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unifriends.friendFinder.FindFriends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.example.unifriends.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroup extends AppCompatActivity {
    public String userID;
    private static final String TAG = "CreateGroup";


    public ArrayList<String> matches = new ArrayList<>();
    public static ArrayList<String> test = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        userID = FirebaseAuth.getInstance().getUid();


        RecyclerView recyclerView = findViewById(R.id.allUsers);
        RecyclerView recyclerViewSubList = findViewById(R.id.friendsBySubList);

        final ArrayList<User> allUsers = new ArrayList<>();
        final ArrayList<User> matchesUser = new ArrayList<>();

        final LayoutAdapter myAdapter = new LayoutAdapter(this, allUsers);
        final MatchAdapter matchAdapter = new MatchAdapter(this, matchesUser);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
        recyclerViewSubList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSubList.setAdapter(matchAdapter);

        getAllUsers(allUsers, myAdapter, matchAdapter, matchesUser);

        Button createGroupButton = (Button) findViewById(R.id.createGroupButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup(allUsers, matchesUser);
            }
        });
    }

    public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.MyViewHolder> {
        Context context;
        ArrayList<User> users;

        public LayoutAdapter(Context context, ArrayList<User> users) {
            this.context = context;
            this.users = users;
        }

        @NonNull
        @Override
        public LayoutAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.creategroupcustomlayout, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LayoutAdapter.MyViewHolder holder, int position) {
            final User user = users.get(position);

            holder.textView.setText(user.name);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(user.isChecked());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    user.setChecked(b);
                }
            });
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
            final User user = matches.get(position);

            holder.textView.setText(user.name);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(user.isChecked());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    user.setChecked(b);
                }
            });
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

    /*
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
    */


    public void createGroup(ArrayList<User> allUsers, ArrayList<User> matchUsers) {
        Log.i("click", "this has been clicked");

        EditText editGroupName = findViewById(R.id.groupNameEditText);
        String groupName = editGroupName.getText().toString();

        DocumentReference newGroupRef = db.collection("group").document();
        Map<String, Object> data = new HashMap<>();

        //add current user to group
        ArrayList<String> selectedUsers = new ArrayList<>();

        for (User u : allUsers) {
            if (u.isChecked()) {
                if (!selectedUsers.contains(u.id)) {
                    selectedUsers.add(u.id);
                }
            }
        }

        for (User u : matchUsers) {
            if (u.isChecked()) {
                if (!selectedUsers.contains(u.id)) {
                    selectedUsers.add(u.id);
                }
            }
        }

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
                        startActivity(new Intent(CreateGroup.this, FindFriends.class));
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

    public void findMatches(final ArrayList<User> allUsers, ArrayList<String> usersSubjects, final HashMap<String, String[]> allUsersSubjects,
                            final HashMap<String, String> allUsersIdName, final ArrayList<User> matchesUser, final MatchAdapter matchAdapter) {
        Log.i(TAG, "findMatches Entered");

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

        matchAdapter.notifyDataSetChanged();
    }

    /*
    public void getUsersSubjects(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users").document(userID);
        final ArrayList<String> usersSubjects = new ArrayList<>();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.i("subjects tester", document.get("subjects").toString());
                        String test = document.get("subjects").toString();
                        String test2 = test.replaceAll("[^\\w\\s]", "");
                        String test3 = test2.trim();
                        String[] subjects = test3.split("\\s+");
                        for(String s: subjects) {
                            usersSubjects.add(s);
                        }

                    } else {
                        Log.i(TAG, "No such document");
                    }
                } else {
                    Log.i(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    */

    public void getAllUsers(final ArrayList<User> allUsers, final LayoutAdapter layoutAdapter, final MatchAdapter matchAdapter, final ArrayList<User> matchesUser) {
        final HashMap<String,String[]> allUsersSubjects = new HashMap<String, String[]>();
        final HashMap<String, String> allUsersIdName = new HashMap<>();
        final ArrayList<String> usersSubjects = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot results = task.getResult();

                    List<DocumentSnapshot> list = results.getDocuments();

                    for(DocumentSnapshot d: list) {
                        if (d.getId().equals(FirebaseAuth.getInstance().getUid())) {
                            String test = d.get("subjects").toString();
                            String test2 = test.replaceAll("[^\\w\\s]", "");
                            String test3 = test2.trim();
                            String[] subjects = test3.split("\\s+");
                            for(String s: subjects) {
                                usersSubjects.add(s);
                            }
                        } else {

                            Log.i(TAG, d.get("subjects").toString());
                            String test = d.get("subjects").toString();
                            String test2 = test.replaceAll("[^\\w\\s]", "");
                            String test3 = test2.trim();
                            String[] subjects = test3.split("\\s+");

                            if (subjects.length != 0) {

                                Log.i("particular user subjects", Arrays.toString((subjects)));

                                User u = d.toObject(User.class);
                                u.setId(d.getId());
                                allUsers.add(u);
                                allUsersSubjects.put(d.getId(), subjects);
                                allUsersIdName.put(d.getId(), d.get("name").toString());
                            }
                        }
                    }

                    layoutAdapter.notifyDataSetChanged();

                    findMatches(allUsers, usersSubjects, allUsersSubjects, allUsersIdName, matchesUser, matchAdapter);
                }

            }


        });
    }
}
