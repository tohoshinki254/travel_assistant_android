package com.example.travelassistant;


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

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private ArrayList<Tour> toursOrigin;
    private ArrayList<Tour> toursResult;
    private Context context;

    public TourAdapter(ArrayList<Tour> toursOrigin, Context context) {
        this.toursOrigin = toursOrigin;
        this.context = context;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tour_layout, parent, false);
        return new TourViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour t = toursOrigin.get(position);

        if(t.avatar == null)
            t.avatar = "https://icon-library.net/images/no-image-available-icon/no-image-available-icon-6.jpg";

        Picasso.get()
                .load(t.avatar)
                .error(R.drawable.no_image)
                .into(holder.ivTourImg);

        holder.tvName.setText(t.name);
        String temp = t.startDate + " - " + t.endDate;
        holder.tvCalendar.setText(temp);
        temp = "" + t.adults;
        holder.tvAdults.setText(temp);
        temp = t.minCost + " - " + t.maxCost;
        holder.tvMoney.setText(temp);

    }

    @Override
    public int getItemCount() {
        return toursOrigin.size();
    }

    public class TourViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvCalendar;
        public TextView tvAdults;
        public TextView tvMoney;
        public ImageView ivTourImg;
        public TourViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCalendar = (TextView) itemView.findViewById(R.id.tvCalendar);
            tvAdults = (TextView) itemView.findViewById(R.id.tvAdults);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
            ivTourImg = (ImageView) itemView.findViewById(R.id.ivTourImg);
        }
    }
}
