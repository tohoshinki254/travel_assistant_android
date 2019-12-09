package com.ygaps.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private ArrayList<Chat> listChat;
    private Context context;

    public ChatAdapter(ArrayList<Chat> chats, Context context)
    {
        this.listChat = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_detail, parent, false);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat c = listChat.get(position);
        holder.tvUsername.setText(c.name);
        holder.tvMessage.setText(c.notification);
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername;
        public TextView tvMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.chat_detail_username);
            tvMessage = (TextView) itemView.findViewById(R.id.chat_detail_message);
        }
    }
}
