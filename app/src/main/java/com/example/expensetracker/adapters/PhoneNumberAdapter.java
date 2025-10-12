package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Member;

import java.util.List;

public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.PhoneNumberViewHolder> {
    private Context context;
    private List<Member> members;

    public PhoneNumberAdapter(Context context, List<Member> members) {
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public PhoneNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phone_number, parent, false);
        return new PhoneNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneNumberViewHolder holder, int position) {
        Member member = members.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<Member> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    static class PhoneNumberViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName;
        TextView tvPhoneNumber;

        public PhoneNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
        }

        public void bind(Member member) {
            tvMemberName.setText(member.getMemberName());
            if (member.getPhoneNumber() != null && !member.getPhoneNumber().isEmpty()) {
                tvPhoneNumber.setText(member.getPhoneNumber());
            } else {
                tvPhoneNumber.setText("No phone number");
                tvPhoneNumber.setTextColor(itemView.getContext().getResources().getColor(R.color.gray));
            }
        }
    }
}
