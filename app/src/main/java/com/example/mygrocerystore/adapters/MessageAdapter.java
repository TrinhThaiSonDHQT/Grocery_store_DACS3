package com.example.mygrocerystore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mygrocerystore.R;
import com.example.mygrocerystore.models.Message;

import java.util.ArrayList;
import java.util.Objects;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    Context context;
    ArrayList<Message> messages;
    String senderRoom;
    String receiverRoom;
    private int ITEM_SENT = 1;
    private int ITEM_RECEIVE = 2;

    public MessageAdapter(Context context, ArrayList<Message> messages,
                          String senderRoom, String receiverRoom) {
        if (messages != null) this.messages = messages;
        this.context = context;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageChats;
        LinearLayout linearLayoutChats;
        TextView message;

        public ViewHolder(View view) {
            super(view);

            imageChats = view.findViewById(R.id.imageChats);
            linearLayoutChats = view.findViewById(R.id.linearLayoutChats);
            message = view.findViewById(R.id.message);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message messages = this.messages.get(position);
        if (!Objects.equals(messages.getSenderId(), "admin")) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.sender_msg, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.receiver_msg, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getMessage().equals("photo")) {
            holder.imageChats.setVisibility(View.VISIBLE);
            holder.linearLayoutChats.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
            Glide.with(context).load(message.getImageUrl()).placeholder(R.drawable.placeholder)
                    .into(holder.imageChats);
        }
        holder.message.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
