package com.example.unifriends.chat;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ChatRoomRepository {
    private static final String TAG = "ChatRoomRepo";

    private FirebaseFirestore db;
    private String uid;


    public ChatRoomRepository(FirebaseFirestore db, String uid) {
        this.db = db;
        this.uid = uid;
    }


    public void getRooms(EventListener<QuerySnapshot> listener) {
        db.collection("group")
                .whereArrayContains("members", uid)
                .addSnapshotListener(listener);
    }
}
