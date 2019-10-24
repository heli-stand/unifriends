package com.example.unifriends.friendFinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Ints;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FindFriends extends AppCompatActivity {
    private static final String TAG = "FindFriends";

    String[] NAMES = {"Alexis Mitchell", "Bec Cartright", "Chloe Diamond", "Greg Johnson", "Mike Stewart", "Sam Smith", "Steve Hawkins"};
    int[] IMAGES = {R.drawable.alexis, R.drawable.bec, R.drawable.chloe, R.drawable.greg, R.drawable.mike, R.drawable.sam, R.drawable.steve};
    String[] LOCATIONS = {"-37.798332, 144.958660", "-37.797782, 144.959302","-37.798344, 144.961287", "-37.799477, 144.958903", "-37.799570, 144.961666", "-37.797946, 144.962282", "-37.797056, 144.963586" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Entry.");
        setContentView(R.layout.activity_find_friends2);

        final ListView listView = (ListView)findViewById(R.id.listView);

        // Build list of friends with mock data
        /*
        ArrayList<Friend> friends = new ArrayList<>();

        for (int i = 0; i < NAMES.length; i++) {
            friends.add(new Friend(NAMES[i], LOCATIONS[i], IMAGES[i],
                    new ArrayList<String>(Arrays.asList("Dota 2", "Golf", "Cars", "Skydiving", "Sleeping"))));
        }
        */

        // tries to pull data from firebase
        final ArrayList<Friend> friends = new ArrayList<>();

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "Setting CustomAdapter");
        final CustomAdapter customAdapter = new CustomAdapter(this, friends);
        listView.setAdapter(customAdapter);

        Log.d(TAG, "Setting Listener to get Info");
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Friend currentUser = null;
                    QuerySnapshot results = task.getResult();
                    List<DocumentSnapshot> users = results.getDocuments();
                    Log.d(TAG, "Got documents, processing them");
                    for (DocumentSnapshot user : users) {
                        if (user.get("name") != null && user.get("location") != null && user.get("interests") != null) {
                            String id = user.getId();
                            String name = user.getString("name");
                            GeoPoint location = (GeoPoint) user.get("location");
                            // TODO: Get appropriate image from firebase and download it here so it can be displayed
                            int[] interests = Ints.toArray((List<Integer>) user.get("interests"));
                            Friend f = new Friend(id, name, location, R.drawable.alexis, interests);
                            if (f.getId().equals(auth.getCurrentUser().getUid())) {
                                currentUser = f;
                            } else {
                                friends.add(f);
                            }
                        }
                    }

                    if (currentUser != null) {
                        for (Friend f : friends) {
                            f.setOverlap(calcInterest(currentUser.getInterests(), f.getInterests()));
                        }

                        Collections.sort(friends, new Comparator<Friend>() {
                            @Override
                            public int compare(Friend friend, Friend t1) {
                                return friend.getOverlap().compareTo(t1.getOverlap());
                            }
                        });
                        Collections.reverse(friends);
                    }
                    Log.d(TAG, friends.toString());
                    Log.d(TAG, "Notifying adapter data has changed");
                    customAdapter.notifyDataSetChanged();
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
                                                            Log.d(TAG, "Setting Item Click Listeners");
                                                            gotoProfile( ((Friend) parent.getItemAtPosition(position)) );
                                                        }
                                                    }
                    );
                }
            }
        });

    }


    public double[] longLatSplitter(String location) {
        double[] arr = new double[2];
        String loc = "-37.798332, 144.958660";
        String[] split = loc.split(", ");
        arr[0] = Double.parseDouble(split[0]);
        arr[1] = Double.parseDouble(split[1]);

        return arr;

    }

    public void gotoProfile(Friend f) {
        Log.i("User selected: ", f.toString());
        Intent intent = new Intent(this, FriendProfile.class);
        intent.putExtra("id", f.getId());
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

    public int calcInterest(int[] user, int[] friend) {
        int i, overlap = 0;
        for (i = 0; i < user.length; i++) {
            if (user[i] == 1 && friend[i] == 1) {
                overlap += 1;
            }
        }

        return overlap;
    }

    class Friend {
        private String id;
        private String name;
        private GeoPoint location;
        // TODO: Implement uploading and storing of images on Firebase first for something more sensible
        private int img;
        private Integer overlap;

        private int[] interests;

        public Friend(String id, String name, GeoPoint loc, int img, int[] interests) {
            this.id = id;
            this.name = name;
            this.location = loc;
            this.img = img;
            this.interests = interests;
        }

        public Integer getOverlap() {
            return overlap;
        }

        public void setOverlap(int overlap) {
            this.overlap = overlap;
        }

        public String getId(){
            return id;
        }

        public String getName() {
            return name;
        }

        public GeoPoint getLocation() {
            return location;
        }

        public int getImg() {
            return img;
        }

        public int[] getInterests() {
            return interests;
        }
    }

}
