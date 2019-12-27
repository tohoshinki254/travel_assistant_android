package com.ygaps.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListStopPointAdapter extends RecyclerView.Adapter<ListStopPointAdapter.ListStopPointViewHolder> {
    private ArrayList<StopPoint> listStopPoint;
    private Context context;
    private onStopPointClickListener mStopPointClickListener;

    public  ListStopPointAdapter(ArrayList<StopPoint> stopPoints, Context context, onStopPointClickListener stopPointClickListener) {
        this.listStopPoint = stopPoints;
        this.context = context;
        this.mStopPointClickListener = stopPointClickListener;
    }

    @NonNull
    @Override
    public ListStopPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stop_point_layout, parent, false);
        return new ListStopPointViewHolder(itemView, mStopPointClickListener);
    }

    public void onBindViewHolder(@NonNull ListStopPointViewHolder holder, int position) {
        StopPoint sp = listStopPoint.get(position);
        String temp;

        holder.tvName.setText(sp.name);
        if (sp.arrivalAt != null && sp.leaveAt != null) {
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            Date dateArrive = new Date(sp.arrivalAt);
            Date dateLeave = new Date(sp.leaveAt);
            temp = simple.format(dateArrive) + " - " + simple.format(dateLeave);
            holder.tvCalendar.setText(temp);
        }
        else{
            holder.tvCalendar.setText("Not Available");
        }
        temp = "" + sp.address;
        holder.tvAddress.setText(temp);

        temp = sp.minCost + " - " + sp.maxCost;
        holder.tvMoney.setText(temp);

        if (sp.serviceTypeId == 1)
            holder.tvService.setText("Restaurant");
        else if (sp.serviceTypeId == 2)
            holder.tvService.setText("Hotel");
        else if (sp.serviceTypeId == 3)
            holder.tvService.setText("Rest Station");
        else
            holder.tvService.setText("Other");

    }

    public int getItemCount() {
        return listStopPoint.size();
    }

    public class ListStopPointViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvName;
        public TextView tvCalendar;
        public TextView tvAddress;
        public TextView tvMoney;
        public TextView tvService;
        public onStopPointClickListener stopPointClickListener;

        public ListStopPointViewHolder(@NonNull View itemView, onStopPointClickListener stopPointClickListener) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCalendar = (TextView) itemView.findViewById(R.id.tvCalendar);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
            tvService = (TextView) itemView.findViewById(R.id.tvService);
            this.stopPointClickListener = stopPointClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            stopPointClickListener.onStopPointClick(getAdapterPosition());
        }
    }

    public interface onStopPointClickListener{
        void onStopPointClick(int i);
    }
}
