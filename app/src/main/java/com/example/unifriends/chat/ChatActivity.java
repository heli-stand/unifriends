package com.example.unifriends.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.unifriends.R;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    public static int SIGN_IN_REQUEST_CODE = 10;
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayChatMessages();

    }

    public void fabClick(View view) {
        EditText input = findViewById(R.id.input);

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance()
                .getReference().child("messages")
                .push()
                .setValue(new ChatMessage(input.getText().toString(),
                        Objects.requireNonNull(FirebaseAuth.getInstance()
                                .getCurrentUser())
                                .getDisplayName())
                );

        // Clear the input
        input.setText("");
        displayChatMessages();
    }

    private void displayChatMessages() {

        ListView listOfMessages = findViewById(R.id.list_of_messages);
        Query query = FirebaseDatabase.getInstance().getReference().child("messages");
        // Get references to the views of message.xml
        // Set their text
        // Format the date before showing it
        FirebaseListOptions<ChatMessage> options =
                new FirebaseListOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .setLifecycleOwner(ChatActivity.this)
                        .setLayout(R.layout.message)
                        .build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }

}
