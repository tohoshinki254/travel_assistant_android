package com.example.travelassistant;

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

        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date dateArrive = new Date(sp.arrivalAt);
        Date dateLeave = new Date(sp.leaveAt);
        String temp = simple.format(dateArrive) + " - " + simple.format(dateLeave);
        holder.tvCalendar.setText(temp);

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
