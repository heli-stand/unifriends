package com.example.unifriends.events;


import android.content.Intent;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.MainActivity;
import com.example.unifriends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Calendar extends AppCompatActivity {

    public final String TAG = "Calendar";

    private EditText editDate;
    private EditText editName;
    private EditText editSubject;
    private EditText editDescription;
    private EditText editTime;
    private Button buttonConfirm;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GregorianCalendar cal_month, cal_month_copy;
    private com.example.unifriends.events.HwAdapter hwAdapter;
    private TextView tv_month;
    private String groupId;

    private String[][] events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        groupId = getIntent().getStringExtra("groupId");
        getEvents(groupId);


        editDate = findViewById(R.id.date);
        editName = findViewById(R.id.name);
        editSubject = findViewById(R.id.subject);
        editDescription = findViewById(R.id.description);
        editTime = findViewById(R.id.time);

        buttonConfirm = findViewById(R.id.button_confirm);


        editDate.addTextChangedListener(loginTextWatcher);
        editName.addTextChangedListener(loginTextWatcher);
        editSubject.addTextChangedListener(loginTextWatcher);
        editDescription.addTextChangedListener(loginTextWatcher);
        editTime.addTextChangedListener(loginTextWatcher);

        com.example.unifriends.events.HomeCollection.date_collection_arr=new ArrayList<com.example.unifriends.events.HomeCollection>();
//        HomeCollection.date_collection_arr.add( new HomeCollection("2019-10-10" ,"Study session","Study_session","this is a study sesh","11:00am"));
//        HomeCollection.date_collection_arr.add( new HomeCollection("2019-10-10" ,"Team meeting","Team meeting","this is team meeting","12:00pm"));
        com.example.unifriends.events.HomeCollection.date_collection_arr.add( new com.example.unifriends.events.HomeCollection("2019-10-15" ,"Event 1","comp90018","ERC","2:00pm"));

        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
        cal_month_copy = (GregorianCalendar) cal_month.clone();
        hwAdapter = new com.example.unifriends.events.HwAdapter(this, cal_month, com.example.unifriends.events.HomeCollection.date_collection_arr);

        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));


        ImageButton previous = (ImageButton) findViewById(R.id.ib_prev);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cal_month.get(GregorianCalendar.MONTH) == 4&&cal_month.get(GregorianCalendar.YEAR)==2017) {
                    //cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
                    Toast.makeText(Calendar.this, "Event Detail is available for current session only.", Toast.LENGTH_SHORT).show();
                }
                else {
                    setPreviousMonth();
                    refreshCalendar();
                }


            }
        });







        ImageButton next = (ImageButton) findViewById(R.id.Ib_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cal_month.get(GregorianCalendar.MONTH) == 5&&cal_month.get(GregorianCalendar.YEAR)==2018) {
                    //cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
                    Toast.makeText(Calendar.this, "Event Detail is available for current session only.", Toast.LENGTH_SHORT).show();
                }
                else {
                    setNextMonth();
                    refreshCalendar();
                }
            }
        });
        GridView gridview = (GridView) findViewById(R.id.gv_calendar);
        gridview.setAdapter(hwAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String selectedGridDate = com.example.unifriends.events.HwAdapter.day_string.get(position);
                ((com.example.unifriends.events.HwAdapter) parent.getAdapter()).getPositionList(selectedGridDate, Calendar.this);
            }

        });
    }

    private void createEvent(String groupId, String newDate, String newName, String newSubject, String newDescription, String newTime){
        db.collection("group").document(groupId);
    }

    protected void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH, cal_month.get(GregorianCalendar.MONTH) - 1);
        }
    }

    public void refreshCalendar() {
        hwAdapter.refreshDays();
        hwAdapter.notifyDataSetChanged();
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
    }









    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String dateInput = editDate.getText().toString().trim();
            String nameInput = editName.getText().toString().trim();
            String subjectInput = editSubject.getText().toString().trim();
            String descriptionInput = editDescription.getText().toString().trim();
            String timeInput = editTime.getText().toString().trim();


            buttonConfirm.setEnabled(!dateInput.isEmpty() && !nameInput.isEmpty()
                    && !subjectInput.isEmpty() && !descriptionInput.isEmpty() && !timeInput.isEmpty());
        }



        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void getEvents(String groupId){

        Log.e(TAG, "DocumentSnapshot data: " + groupId);
        db.collection("group").document(groupId).collection("events")
         .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        com.example.unifriends.events.HomeCollection
                                .date_collection_arr
                                .add(new com.example.unifriends.events.HomeCollection(document.get("date").toString(), document.get("name").toString(), document.get("subject").toString(),
                                        document.get("location").toString(), document.get("time").toString()));
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    refreshCalendar();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    public void createEvent(View view){
        final String newDate = editDate.getText().toString();
        final String newName = editName.getText().toString();
        final String newSubject = editSubject.getText().toString();
        final String newDescription = editDescription.getText().toString();
        final String newTime = editTime.getText().toString();

        com.example.unifriends.events.HomeCollection.date_collection_arr.add( new com.example.unifriends.events.HomeCollection(newDate, newName, newSubject,
                newDescription, newTime));

        refreshCalendar();

        Map<String, Object> update = new HashMap<>();

        update.put("date", newDate);
        update.put("name",newName);
        update.put("subject", newSubject);
        update.put("location", newDescription);
        update.put("time", newTime);


        db.collection("group").document(groupId).collection("events")
                .add(update)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
//        ArrayList<String[]> temp = new ArrayList<String[]>(Arrays.asList(events));
//        String[] newEvent = {newDate, newName, newSubject, newDescription, newTime};
//        temp.add(newEvent);
//        update.put("interets", Arrays.asList(temp));
//        db.collection("group").document(groupId).update().
    }
}
