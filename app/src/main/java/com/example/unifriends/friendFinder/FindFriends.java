package com.example.unifriends.friendFinder;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.unifriends.R;

public class FindFriends extends AppCompatActivity {


    String[] NAMES = {"Alexis Mitchell", "Bec Cartright", "Chloe Diamond", "Greg Johnson", "Mike Stewart", "Sam Smith", "Steve Hawkins"};
    int[] IMAGES = {R.drawable.alexis, R.drawable.bec, R.drawable.chloe, R.drawable.greg, R.drawable.mike, R.drawable.sam, R.drawable.steve};
    String[] LOCATIONS = {"-37.798332, 144.958660", "-37.797782, 144.959302","-37.798344, 144.961287", "-37.799477, 144.958903", "-37.799570, 144.961666", "-37.797946, 144.962282", "-37.797056, 144.963586" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends2);

        ListView listView = (ListView)findViewById(R.id.listView);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                //position tells you which item was clicked
                                                Log.i("Person Tapped", NAMES[position]);
                                                double[] selectedUserLocation = longLatSplitter(LOCATIONS[position]);
                                                String selectedUserName = NAMES[position];
                                                viewMap(selectedUserLocation, selectedUserName);

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

    public void viewMap(double[] selectedUserLocation, String selectedUserName){
        Intent intent = new Intent(this, FriendFinderMap.class);
        intent.putExtra("selectedUserLocation", selectedUserLocation);
        intent.putExtra("selectedUserName", selectedUserName);
        startActivity(intent);
        finish();
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return IMAGES.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customlayout,null);

            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            TextView textView_name = (TextView)convertView.findViewById(R.id.textView_name);

            imageView.setImageResource(IMAGES[position]);
            textView_name.setText(NAMES[position]);

            return convertView;
        }
    }


}
