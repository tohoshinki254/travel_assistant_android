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

public class ListFeedBackAdapter extends RecyclerView.Adapter<ListFeedBackAdapter.ListFeedBackViewHolder>{
    private ArrayList<FeedBack> listFeedBacks;
    private Context context;

    public  ListFeedBackAdapter(ArrayList<FeedBack> feedBackArrayList, Context context) {
        this.listFeedBacks = feedBackArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListFeedBackAdapter.ListFeedBackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_layout, parent, false);
        return new ListFeedBackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListFeedBackAdapter.ListFeedBackViewHolder holder, int position) {
        FeedBack fb = listFeedBacks.get(position);

        if(fb.avatar == null)
            fb.avatar = "https://img.icons8.com/pastel-glyph/2x/person-male.png";

        Picasso.get()
                .load(fb.avatar)
                .error(R.drawable.person_icon)
                .into(holder.imvAvatar);

        holder.tvName.setText(fb.name);
        holder.tvFeedBack.setText(fb.feedback);
        holder.tvPoint.setText(fb.point);
    }


    @Override
    public int getItemCount() {
        return listFeedBacks.size();
    }


    public class ListFeedBackViewHolder extends RecyclerView.ViewHolder {
        public ImageView imvAvatar;
        public TextView tvName;
        public TextView tvFeedBack;
        public TextView tvPoint;
        public ListFeedBackViewHolder(@NonNull View itemView) {
            super(itemView);

            imvAvatar = (ImageView) itemView.findViewById(R.id.avatarInFeedBackList);
            tvName = (TextView) itemView.findViewById(R.id.tvNameInFeedBack);
            tvFeedBack = (TextView) itemView.findViewById(R.id.tvFeedBackInFeedBack);
            tvPoint = (TextView) itemView.findViewById(R.id.tvPointInFeedBack);
        }
    }
}
