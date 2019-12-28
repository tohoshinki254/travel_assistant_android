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

public class ListReviewAdapter extends RecyclerView.Adapter<ListReviewAdapter.ListReviewViewHolder>{
    private ArrayList<Review> listReviews;
    private Context context;

    public  ListReviewAdapter(ArrayList<Review> reviewArrayList, Context context) {
        this.listReviews = reviewArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListReviewAdapter.ListReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_layout, parent, false);
        return new ListReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListReviewAdapter.ListReviewViewHolder holder, int position) {
        Review cm = listReviews.get(position);

        if(cm.avatar == null)
            cm.avatar = "https://img.icons8.com/pastel-glyph/2x/person-male.png";

        Picasso.get()
                .load(cm.avatar)
                .error(R.drawable.person_icon)
                .into(holder.imvAvatar);

        holder.tvName.setText(cm.name);
        holder.tvComment.setText(cm.review);
    }


    @Override
    public int getItemCount() {
        return listReviews.size();
    }


    public class ListReviewViewHolder extends RecyclerView.ViewHolder {
        public ImageView imvAvatar;
        public TextView tvName;
        public TextView tvComment;
        public ListReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            imvAvatar = (ImageView) itemView.findViewById(R.id.avatarInCommentList);
            tvName = (TextView) itemView.findViewById(R.id.tvNameInComment);
            tvComment = (TextView) itemView.findViewById(R.id.tvCommentInComment);
        }
    }
}
