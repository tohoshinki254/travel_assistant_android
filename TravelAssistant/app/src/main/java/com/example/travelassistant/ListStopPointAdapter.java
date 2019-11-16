package com.example.travelassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListStopPointAdapter extends RecyclerView.Adapter<ListStopPointAdapter.ListStopPointViewHolder> {
    private ArrayList<StopPoint> listStopPoint;
    private Context context;

    public  ListStopPointAdapter(ArrayList<StopPoint> stopPoints, Context context) {
        this.listStopPoint = stopPoints;
        this.context = context;
    }

    @NonNull
    @Override
    public ListStopPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stop_point_layout, parent, false);
        return new ListStopPointViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull ListStopPointViewHolder holder, int position) {
        StopPoint sp = listStopPoint.get(position);

        holder.tvName.setText(sp.name);
        String temp = sp.arrivalAt + " - " + sp.leaveAt;
        holder.tvCalendar.setText(temp);
        temp = "" + sp.provinceId;
        holder.tvAddress.setText(temp);
        temp = sp.minCost + " - " + sp.maxCost;
        holder.tvMoney.setText(temp);
        temp = "" + sp.serviceTypeId;
        holder.tvService.setText(temp);
    }

    public int getItemCount() {
        return listStopPoint.size();
    }

    public class ListStopPointViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvCalendar;
        public TextView tvAddress;
        public TextView tvMoney;
        public TextView tvService;
        public ListStopPointViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCalendar = (TextView) itemView.findViewById(R.id.tvCalendar);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
            tvService = (TextView) itemView.findViewById(R.id.tvService);
        }
    }
}
