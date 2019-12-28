package com.ygaps.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecommendedStopPointAdapter extends RecyclerView.Adapter<RecommendedStopPointAdapter.RecommendedStopPointViewHolder> {

    private ArrayList<RecommendedStopPoint> listRecommendedStopPoint;
    private Context context;
    private onRecommendedSPClickListener m_onRecommendedSPClickListener;


    public RecommendedStopPointAdapter(ArrayList<RecommendedStopPoint> recommendedStopPoints, Context context, onRecommendedSPClickListener m_OnRecommendedSPClickListener)
    {
        this.context = context;
        this.listRecommendedStopPoint = recommendedStopPoints;
        this.m_onRecommendedSPClickListener = m_OnRecommendedSPClickListener;
    }

    @NonNull
    @Override
    public RecommendedStopPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recommended_stop_point_detail, parent, false);
        return new RecommendedStopPointViewHolder(itemView, m_onRecommendedSPClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedStopPointViewHolder holder, int position) {
        RecommendedStopPoint recommendedSP = listRecommendedStopPoint.get(position);

        String temp = recommendedSP.stopPoint.name;
        holder.tvName.setText(temp);
        temp = recommendedSP.stopPoint.address;
        holder.tvAddress.setText(temp);
        holder.checkBoxIsChosen.setChecked(recommendedSP.isChosen);

        temp = recommendedSP.stopPoint.minCost.toString() + " - " + recommendedSP.stopPoint.maxCost.toString();
        holder.tvMoney.setText(temp);

        if (recommendedSP.stopPoint.serviceTypeId == 1)
            holder.tvService.setText("Restaurant");
        else if (recommendedSP.stopPoint.serviceTypeId == 2)
            holder.tvService.setText("Hotel");
        else if (recommendedSP.stopPoint.serviceTypeId == 3)
            holder.tvService.setText("Rest Station");
        else
            holder.tvService.setText("Other");
    }

    @Override
    public int getItemCount() {
        return listRecommendedStopPoint.size();
    }

    public class RecommendedStopPointViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView tvName;
        public TextView tvAddress;
        public TextView tvService;
        public TextView tvMoney;
        public CheckBox checkBoxIsChosen;
        public EditText edtSearchRecommendedSP;
        public onRecommendedSPClickListener mRecommendedSPClickListener;


        public RecommendedStopPointViewHolder(@NonNull View itemView, onRecommendedSPClickListener recommendedSPClickListener) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.textViewRecommendedSPName);
            tvAddress = (TextView) itemView.findViewById(R.id.textViewRecommendedSPAddress);
            tvService = (TextView) itemView.findViewById(R.id.textViewRecommendedSPService);
            tvMoney = (TextView) itemView.findViewById(R.id.textViewRecommendedSPMoney);
            checkBoxIsChosen = (CheckBox) itemView.findViewById(R.id.checkboxRecommendedSP);
            edtSearchRecommendedSP = (EditText) itemView.findViewById(R.id.edtSearchRecommendedStopPoint);
            this.mRecommendedSPClickListener = recommendedSPClickListener;
            itemView.setOnClickListener(this);


            checkBoxIsChosen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBoxIsChosen.isChecked())
                        listRecommendedStopPoint.get(getAdapterPosition()).isChosen = true;
                    else
                        listRecommendedStopPoint.get(getAdapterPosition()).isChosen = false;

                }

            });

        }

        @Override
        public void onClick(View view) {
            if (checkBoxIsChosen.isChecked()) {
                checkBoxIsChosen.setChecked(false);
                listRecommendedStopPoint.get(getAdapterPosition()).isChosen = false;
            }
            else {
                checkBoxIsChosen.setChecked(true);
                listRecommendedStopPoint.get(getAdapterPosition()).isChosen = true;
            }
            mRecommendedSPClickListener.onRecommendedClick(getAdapterPosition());
        }
    }

    public interface onRecommendedSPClickListener
    {
        void onRecommendedClick(int i);
    }
}
