package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Member;

import java.text.DecimalFormat;
import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private List<Member> members;
    private Context context;
    private DecimalFormat currencyFormat;
    private OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onMemberClick(Member member);
    }

    public BalanceAdapter(Context context, List<Member> members) {
        this.context = context;
        this.members = members;
        this.currencyFormat = new DecimalFormat("$#,##0.00");
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_balance, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
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

    public void setOnMemberClickListener(OnMemberClickListener listener) {
        this.listener = listener;
    }

    class BalanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMemberName;
        private TextView tvBalance;
        private TextView tvTotalOwed;
        private TextView tvTotalOwing;

        public BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvBalance = itemView.findViewById(R.id.tvBalance);
            tvTotalOwed = itemView.findViewById(R.id.tvTotalOwed);
            tvTotalOwing = itemView.findViewById(R.id.tvTotalOwing);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMemberClick(members.get(position));
                    }
                }
            });
        }

        public void bind(Member member) {
            tvMemberName.setText(member.getMemberName());
            tvTotalOwed.setText("Total owed: " + currencyFormat.format(member.getTotalOwed()));
            tvTotalOwing.setText("Total owing: " + currencyFormat.format(member.getTotalOwing()));

            // Set balance text and color based on balance
            double balance = member.getBalance();
            if (balance > 0) {
                tvBalance.setText("Owes " + currencyFormat.format(balance));
                tvBalance.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else if (balance < 0) {
                tvBalance.setText("Gets back " + currencyFormat.format(Math.abs(balance)));
                tvBalance.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else {
                tvBalance.setText("Settled up");
                tvBalance.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
        }
    }
}
