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

public class ListMemberAdapter extends RecyclerView.Adapter<ListMemberAdapter.ListMemberViewHolder> {

    private ArrayList<Member> listMembers;
    private Context context;

    public  ListMemberAdapter(ArrayList<Member> memberArrayList, Context context) {
        this.listMembers = memberArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListMemberAdapter.ListMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_layout, parent, false);
        return new ListMemberAdapter.ListMemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMemberAdapter.ListMemberViewHolder holder, int position) {
        Member member = listMembers.get(position);

        if(member.avatar == null)
            member.avatar = "https://img.icons8.com/pastel-glyph/2x/person-male.png";

        Picasso.get()
                .load(member.avatar)
                .error(R.drawable.person_icon)
                .into(holder.imvAvatar);

        holder.tvName.setText("Name:" + member.name);
        holder.tvId.setText("ID:" + member.id);
        holder.tvPhone.setText("Phone:" + member.phone);

        if (member.isHost)
            holder.tvHost.setText("Host: True");
        else {

        }

    }

    @Override
    public int getItemCount() {
        return listMembers.size();
    }

    public class ListMemberViewHolder extends RecyclerView.ViewHolder {
        public TextView tvId;
        public ImageView imvAvatar;
        public TextView tvName;
        public TextView tvPhone;
        public TextView tvHost;
        public ListMemberViewHolder(@NonNull View itemView) {
            super(itemView);

            tvId = (TextView) itemView.findViewById(R.id.tvIdInMember);
            imvAvatar = (ImageView) itemView.findViewById(R.id.avatarInMemberList);
            tvName = (TextView) itemView.findViewById(R.id.tvNameInMember);
            tvPhone = (TextView) itemView.findViewById(R.id.tvPhoneInComment);
            tvHost = (TextView) itemView.findViewById(R.id.tvHostInComment);
        }
    }
}
