package com.example.unifriends.friendFinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifriends.R;
import com.example.unifriends.groups.User;
import com.example.unifriends.groups.createGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.unifriends.groups.createGroup.allUsers;
import static com.example.unifriends.groups.createGroup.allUsersIdName;
import static com.example.unifriends.groups.createGroup.allUsersSubjects;
import static com.example.unifriends.groups.createGroup.usersSubjects;


public class FindFriends extends AppCompatActivity {


    public String userID;
    String[] NAMES = {"Alexis Mitchell", "Bec Cartright", "Chloe Diamond", "Greg Johnson", "Mike Stewart", "Sam Smith", "Steve Hawkins"};
    int[] IMAGES = {R.drawable.alexis, R.drawable.bec, R.drawable.chloe, R.drawable.greg, R.drawable.mike, R.drawable.sam, R.drawable.steve};
    String[] LOCATIONS = {"-37.798332, 144.958660", "-37.797782, 144.959302","-37.798344, 144.961287", "-37.799477, 144.958903", "-37.799570, 144.961666", "-37.797946, 144.962282", "-37.797056, 144.963586" };
    public static ArrayList<String> usersGroups = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userID = getIntent().getStringExtra("userID");

        getAllUsers();
        getUsersSubjects();



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends2);

        ListView listView = (ListView)findViewById(R.id.allUsers);

        // Build list of friends with mock data
        ArrayList<Friend> friends = new ArrayList<>();

        for (int i = 0; i < NAMES.length; i++) {
            friends.add(new Friend(NAMES[i], LOCATIONS[i], IMAGES[i],
                    new ArrayList<String>(Arrays.asList("Dota 2", "Golf", "Cars", "Skydiving", "Sleeping"))));
        }


        CustomAdapter customAdapter = new CustomAdapter(this, friends);

        listView.setAdapter(customAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                //position tells you which item was clicked
                                                /*
                                                Log.i("Person Tapped", NAMES[position]);
                                                double[] selectedUserLocation = longLatSplitter(LOCATIONS[position]);
                                                String selectedUserName = NAMES[position];
                                                viewMap(selectedUserLocation, selectedUserName);
                                                */

                                                gotoProfile( ((Friend) parent.getItemAtPosition(position)) );
                                            }
                                        }
        );


        for(String s: usersGroups) {
            Log.i("hree are the groups in findfrines", s);
        }

        RecyclerView recyclerView = findViewById(R.id.usersGroupsList);
        MyAdapter myAdapter = new MyAdapter(this, usersGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);



    }




    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        Context context;
        ArrayList<String> usersGroups;

        public MyAdapter(Context context, ArrayList<String> usersGroups) {
            this.context = context;
            this.usersGroups = usersGroups;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.viewusersgroupslayout, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {


            holder.textView.setText(usersGroups.get(position));

        }

        @Override
        public int getItemCount() {
            return usersGroups.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;


            public MyViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.groupName);

            }
        }
    }

    public double[] longLatSplitter(String location) {
        double[] arr = new double[2];
        String loc = "-37.798332, 144.958660";
        String[] split = loc.split(", ");
        arr[0] = Double.parseDouble(split[0]);
        arr[1] = Double.parseDouble(split[1]);

        return arr;

    }

    public void getUsersSubjects() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users").document(userID);

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
                        Log.i("error", "No such document");


                    }
                } else {
                    Log.i("error", "get failed with ", task.getException());


                }
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
                        Log.i("result", d.get("subjects").toString());
                        String test = d.get("subjects").toString();
                        String test2 = test.replaceAll("[^\\w\\s]", "");
                        String test3 = test2.trim();
                        String[] subjects = test3.split("\\s+");

                        if(subjects.length != 0) {

                            Log.i("particular user subjects", Arrays.toString((subjects)));

                            User u = d.toObject(User.class);
                            u.setId(d.getId());
                            allUsers.add(u);
                            allUsersSubjects.put(d.getId(), subjects);
                            allUsersIdName.put(d.getId(), d.get("name").toString());
                        }
                    }
                }

            }


        });
    }

    public void gotoProfile(Friend f) {
        Log.i("User selected: ", f.toString());
        Intent intent = new Intent(this, FriendProfile.class);
        intent.putExtra("name", f.getName());
        intent.putExtra("img", f.getImg());
        intent.putExtra("interests", (ArrayList<String>) f.getInterests());
        startActivity(intent);
    }

    public void goToCreateGroup(View view) {
        Intent intent = new Intent(this, createGroup.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void viewMap(double[] selectedUserLocation, String selectedUserName){
        Intent intent = new Intent(this, FriendFinderMap.class);
        intent.putExtra("selectedUserLocation", selectedUserLocation);
        intent.putExtra("selectedUserName", selectedUserName);
        startActivity(intent);
    }

    class CustomAdapter extends BaseAdapter{
        private Context context;
        private List<Friend> list;

        public CustomAdapter(Context context, List<Friend> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Friend current = (Friend) getItem(position);

            convertView = getLayoutInflater().inflate(R.layout.customlayout,null);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            TextView textView_name = (TextView)convertView.findViewById(R.id.textView_name);

            imageView.setImageResource(current.getImg());
            textView_name.setText(current.getName());

            return convertView;
        }
    }

    class Friend {
        private String id;
        private String name;
        private String location;
        // TODO: Implement uploading and storing of images on Firebase first for something more sensible
        private int img;

        private List<String> interests;

        public Friend(String name, String loc, int img, List<String> interests) {
            this.name = name;
            this.location = loc;
            this.img = img;
            this.interests = interests;
        }

        public String getId(){
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }

        public int getImg() {
            return img;
        }

        public List<String> getInterests() {
            return interests;
        }
    }

}
