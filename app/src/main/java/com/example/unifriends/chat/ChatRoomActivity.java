package com.example.unifriends.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifriends.MainActivity;
import com.example.unifriends.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    ChatRoomRepository chatRoomRepository;
    private RecyclerView chatRooms;
    private ChatRoomsAdapter adapter;
    private FirebaseUser user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        user = FirebaseAuth.getInstance().getCurrentUser();

        chatRoomRepository = new ChatRoomRepository(FirebaseFirestore.getInstance(), user.getUid());
        initUI();
        getChatRooms();

    }
    private void initUI() {
        chatRooms = findViewById(R.id.rooms);
        chatRooms.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getChatRooms() {
        chatRoomRepository.getRooms(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("ChatRoomActivity", "Listen failed.", e);
                    return;
                }

                List<ChatRoom> rooms = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    rooms.add(new ChatRoom(doc.getId(), doc.getString("name")));
                }
                adapter = new ChatRoomsAdapter(rooms, listener);
                chatRooms.setAdapter(adapter);
                //findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }
        });
    }

    ChatRoomsAdapter.OnChatRoomClickListener listener = new ChatRoomsAdapter.OnChatRoomClickListener() {
        @Override
        public void onClick(ChatRoom chatRoom) {
            Intent intent = new Intent(ChatRoomActivity.this, ChatActivity.class);
            intent.putExtra("roomId", chatRoom.getId());
            intent.putExtra("roomName", chatRoom.getName());
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ChatRoomActivity.this, MainActivity.class));
    }
}
