package com.example.mygrocerystore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.mygrocerystore.R;
import com.example.mygrocerystore.adapters.MessageAdapter;
import com.example.mygrocerystore.databinding.ActivityChatsBinding;
import com.example.mygrocerystore.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatsActivity extends AppCompatActivity {
    ImageButton affordance;
    ActivityChatsBinding binding;
    private MessageAdapter adapter;
    private ArrayList<Message> messages;
    private FirebaseDatabase databaseMessage;
    private FirebaseStorage storage;
    private String senderRoom, receiveRoom, senderUid, receiveUid = "admin";
    private RecyclerView recyclerViewChats;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = ChatsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(ChatsActivity.this, R.color.bg_header_chats));

        affordance = binding.affordance;
        affordance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatsActivity.this, MainActivity.class));
            }
        });
        recyclerViewChats = binding.recycleChats;

        databaseMessage = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        messages = new ArrayList();

        getMessages();
    }

    private void getMessages() {

        senderUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        senderRoom = senderUid + receiveUid;
        receiveRoom = receiveUid + senderUid;
        adapter = new MessageAdapter(ChatsActivity.this, messages, senderRoom, receiveRoom);
        recyclerViewChats.setAdapter(adapter);

        databaseMessage.getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messages.clear();
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            Message message = s.getValue(Message.class);
                            message.setMessageId(s.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        binding.buttonSend.setOnClickListener(view -> {
            String messageText = binding.editText.getText().toString();
            Date date = new Date();
            Message message = new Message(null, messageText, senderUid, null, date.getTime());

            binding.editText.setText(null);
            String key = databaseMessage.getReference().push().getKey();
            HashMap<String, Object> lastMsgObj = new HashMap<>();
            lastMsgObj.put("lastMsg", message.getMessage());
            lastMsgObj.put("lastMsgTime", date.getTime());

            databaseMessage.getReference().child("chats").child(senderRoom)
                    .updateChildren(lastMsgObj);
            databaseMessage.getReference().child("chats").child(receiveRoom)
                    .updateChildren(lastMsgObj);
            assert key != null;
            databaseMessage.getReference().child("chats").child(senderRoom)
                    .child("messages").child(key).setValue(message).addOnSuccessListener(aVoid -> databaseMessage.getReference().child("chats").child(receiveRoom)
                            .child("messages").child(key).setValue(message));

        });

        binding.attach.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 25);
        });
    }
}