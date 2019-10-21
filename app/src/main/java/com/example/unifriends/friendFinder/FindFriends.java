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

import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindFriends extends AppCompatActivity {


    String[] NAMES = {"Alexis Mitchell", "Bec Cartright", "Chloe Diamond", "Greg Johnson", "Mike Stewart", "Sam Smith", "Steve Hawkins"};
    int[] IMAGES = {R.drawable.alexis, R.drawable.bec, R.drawable.chloe, R.drawable.greg, R.drawable.mike, R.drawable.sam, R.drawable.steve};
    String[] LOCATIONS = {"-37.798332, 144.958660", "-37.797782, 144.959302","-37.798344, 144.961287", "-37.799477, 144.958903", "-37.799570, 144.961666", "-37.797946, 144.962282", "-37.797056, 144.963586" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends2);

        ListView listView = (ListView)findViewById(R.id.listView);

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
        intent.putExtra("name", f.getName());
        intent.putExtra("img", f.getImg());
        intent.putExtra("interests", (ArrayList<String>) f.getInterests());
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
