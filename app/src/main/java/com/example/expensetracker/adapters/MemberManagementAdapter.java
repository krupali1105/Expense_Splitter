package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Member;

import java.text.DecimalFormat;
import java.util.List;

public class MemberManagementAdapter extends RecyclerView.Adapter<MemberManagementAdapter.MemberViewHolder> {
    private Context context;
    private List<Member> members;
    private OnMemberClickListener listener;
    private DecimalFormat currencyFormat;

    public interface OnMemberClickListener {
        void onMemberClick(Member member);
        void onMemberEdit(Member member);
        void onMemberDelete(Member member);
    }

    public MemberManagementAdapter(Context context, List<Member> members, OnMemberClickListener listener) {
        this.context = context;
        this.members = members;
        this.listener = listener;
        this.currencyFormat = new DecimalFormat("$#,##0.00");
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member_management, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = members.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<Member> newMembers) {
        this.members = newMembers;
        notifyDataSetChanged();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName;
        TextView tvEmail;
        TextView tvPhoneNumber;
        TextView tvBalance;
        TextView tvTotalOwed;
        TextView tvTotalOwing;
        MaterialButton btnEdit;
        MaterialButton btnDelete;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvBalance = itemView.findViewById(R.id.tvBalance);
            tvTotalOwed = itemView.findViewById(R.id.tvTotalOwed);
            tvTotalOwing = itemView.findViewById(R.id.tvTotalOwing);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMemberClick(members.get(position));
                    }
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMemberEdit(members.get(position));
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMemberDelete(members.get(position));
                    }
                }
            });
        }

        public void bind(Member member) {
            tvMemberName.setText(member.getMemberName());
            tvEmail.setText(member.getEmail() != null ? member.getEmail() : "No email");
            tvPhoneNumber.setText(member.getPhoneNumber() != null ? member.getPhoneNumber() : "No phone");
            
            double balance = member.getBalance();
            if (balance > 0) {
                tvBalance.setText("Owes " + currencyFormat.format(balance));
                tvBalance.setTextColor(context.getResources().getColor(R.color.red));
            } else if (balance < 0) {
                tvBalance.setText("Gets back " + currencyFormat.format(Math.abs(balance)));
                tvBalance.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                tvBalance.setText("Settled up");
                tvBalance.setTextColor(context.getResources().getColor(R.color.gray));
            }
            
            tvTotalOwed.setText("Owed: " + currencyFormat.format(member.getTotalOwed()));
            tvTotalOwing.setText("Owing: " + currencyFormat.format(member.getTotalOwing()));
        }
    }
}
