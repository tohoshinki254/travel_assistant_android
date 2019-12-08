package com.ygaps.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder> {

    public ArrayList<InvitationModel> invitationList;
    private Context context;

    public InvitationAdapter(ArrayList<InvitationModel> invitationList, Context context) {
        this.invitationList = invitationList;
        this.context = context;
    }

    @NonNull
    @Override
    public InvitationAdapter.InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invitation_item_layout, parent, false);
        return new InvitationAdapter.InvitationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationAdapter.InvitationViewHolder holder, int position) {
        InvitationModel invitationModel = invitationList.get(position);
        if (invitationModel.hostAvatar == null)
            invitationModel.hostAvatar = "http://placehold.it/200x200&text=No%20Avatar";

        Picasso.get()
                .load(invitationModel.hostAvatar)
                .error(R.drawable.no_avatar)
                .into(holder.hostAvatar);

        if (invitationModel.hostName == null)
            invitationModel.hostName = "";

        holder.tvSenderName.setText(invitationModel.hostName);

        if (invitationModel.name == null)
            invitationModel.name = "";

        holder.tvTourName.setText(invitationModel.name);

        String temp = "";
        if (invitationModel.startDate != null && invitationModel.endDate != null) {
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
            Date dateArrive = new Date(Long.parseLong(invitationModel.startDate));
            Date dateLeave = new Date(Long.parseLong(invitationModel.endDate));
            temp = simple.format(dateArrive) + " - " + simple.format(dateLeave);
        }

        holder.tvTime.setText(temp);
    }

    @Override
    public int getItemCount() {
        return invitationList.size();
    }

    public class InvitationViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView hostAvatar;
        public TextView tvSenderName;
        public TextView tvTourName;
        public TextView tvTime;
        public Button btnConfirm;
        public Button btnDelete;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);

            hostAvatar = (CircleImageView) itemView.findViewById(R.id.profile_image);
            tvSenderName = (TextView) itemView.findViewById(R.id.tvSenderName);
            tvTourName = (TextView) itemView.findViewById(R.id.tvTourName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            btnConfirm = (Button) itemView.findViewById(R.id.btnConfirm);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Confirm -  " + getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Delete -  " + getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
