package com.ygaps.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListCommentAdapter extends RecyclerView.Adapter<ListCommentAdapter.ListCommentViewHolder>{
    private ArrayList<Comment> listComments;
    private Context context;

    public  ListCommentAdapter(ArrayList<Comment> commentArrayList, Context context) {
        this.listComments = commentArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListCommentAdapter.ListCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_layout, parent, false);
        return new ListCommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCommentAdapter.ListCommentViewHolder holder, int position) {
        Comment cm = listComments.get(position);

        if(cm.avatar == null)
            cm.avatar = "https://img.icons8.com/pastel-glyph/2x/person-male.png";

        Picasso.get()
                .load(cm.avatar)
                .error(R.drawable.person_icon)
                .into(holder.imvAvatar);

        holder.tvName.setText(cm.name);
        holder.tvComment.setText(cm.comment);
    }


    @Override
    public int getItemCount() {
        return listComments.size();
    }


    public class ListCommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView imvAvatar;
        public TextView tvName;
        public TextView tvComment;
        public ListCommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imvAvatar = (ImageView) itemView.findViewById(R.id.avatarInCommentList);
            tvName = (TextView) itemView.findViewById(R.id.tvNameInComment);
            tvComment = (TextView) itemView.findViewById(R.id.tvCommentInComment);
        }
    }
}
