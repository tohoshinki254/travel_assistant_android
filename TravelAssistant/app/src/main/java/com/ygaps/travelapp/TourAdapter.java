package com.ygaps.travelapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> implements Filterable {

    public ArrayList<Tour> toursOrigin;
    public ArrayList<Tour> toursResult;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();
    private onItemClickListener mOnItemClickListener;
    public TourAdapter(ArrayList<Tour> tours, Context context, onItemClickListener itemClickListener) {
        this.toursResult = tours;
        this.toursOrigin = new ArrayList<>(tours);
        this.context = context;
        this.mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tour_layout, parent, false);
        return new TourViewHolder(itemView, mOnItemClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour t = toursResult.get(position);

        if(t.avatar == null)
            t.avatar = "http://i.pinimg.com/736x/3b/d9/fd/3bd9fd6dc12a4a08a928d6a31387d056.jpg";

        Picasso.get()
                .load(t.avatar)
                .error(R.drawable.background)
                .into(holder.ivTourImg);

        if (t.name.equals(""))
            t.name = "Not Available!";

        holder.tvName.setText(t.name);

        holder.tvTourId.setText("" + t.id);


        String temp = "";
        if (t.startDate != null && t.endDate != null) {
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
            Date dateArrive = new Date(Long.parseLong(t.startDate));
            Date dateLeave = new Date(Long.parseLong(t.endDate));
            temp = simple.format(dateArrive) + " - " + simple.format(dateLeave);
        }

        holder.tvCalendar.setText(temp);
        temp = "" + t.adults;
        holder.tvAdults.setText(temp);
        temp = t.minCost + " - " + t.maxCost;
        holder.tvMoney.setText(temp);

    }

    @Override
    public int getItemCount() {
        return toursResult.size();
    }


    public class TourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName;
        public TextView tvCalendar;
        public TextView tvAdults;
        public TextView tvMoney;
        public TextView tvTourId;
        public ImageView ivTourImg;
        public onItemClickListener itemClickListener;

        public TourViewHolder(@NonNull View itemView, onItemClickListener itemClickListener) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCalendar = (TextView) itemView.findViewById(R.id.tvCalendar);
            tvAdults = (TextView) itemView.findViewById(R.id.tvAdults);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
            ivTourImg = (ImageView) itemView.findViewById(R.id.ivTourImg);
            tvTourId = (TextView) itemView.findViewById(R.id.tvTourId);
            this.itemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            ArrayList <Tour>  filedList = new ArrayList<>();

            if(constraint != null && constraint.length() > 0)
            {
                String filterString = constraint.toString().toUpperCase();


                for(int i = 0; i < toursOrigin.size(); i++)
                {
                    if(toursOrigin.get(i).name.toUpperCase().contains(filterString) || ("" + toursOrigin.get(i).id).toUpperCase().contains(filterString))
                    {
                        Tour t = new Tour(toursOrigin.get(i));
                        filedList.add(t);
                    }
                }

                results.count = filedList.size();
                results.values =filedList;
            }
            else
            {
                results.count = toursOrigin.size();
                results.values = new ArrayList<>(toursOrigin);
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            toursResult = (ArrayList<Tour>) results.values;
            notifyDataSetChanged();
        }
    }

    public interface onItemClickListener {
        void onItemClick(int i);
    }
}
