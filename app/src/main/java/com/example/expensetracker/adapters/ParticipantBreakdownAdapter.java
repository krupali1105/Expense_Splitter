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
import com.example.expensetracker.models.ParticipantBreakdown;

import java.text.DecimalFormat;
import java.util.List;

public class ParticipantBreakdownAdapter extends RecyclerView.Adapter<ParticipantBreakdownAdapter.ParticipantBreakdownViewHolder> {

    private Context context;
    private List<ParticipantBreakdown> participantBreakdowns;
    private DecimalFormat currencyFormat;

    public ParticipantBreakdownAdapter(Context context, List<ParticipantBreakdown> participantBreakdowns) {
        this.context = context;
        this.participantBreakdowns = participantBreakdowns;
        this.currencyFormat = new DecimalFormat("$#,##0.00");
    }

    @NonNull
    @Override
    public ParticipantBreakdownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participant_breakdown, parent, false);
        return new ParticipantBreakdownViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantBreakdownViewHolder holder, int position) {
        ParticipantBreakdown breakdown = participantBreakdowns.get(position);
        holder.bind(breakdown);
    }

    @Override
    public int getItemCount() {
        return participantBreakdowns.size();
    }

    public class ParticipantBreakdownViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName, tvAmountPaid, tvAmountOwed, tvNetBalance;

        public ParticipantBreakdownViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvAmountPaid = itemView.findViewById(R.id.tvAmountPaid);
            tvAmountOwed = itemView.findViewById(R.id.tvAmountOwed);
            tvNetBalance = itemView.findViewById(R.id.tvNetBalance);
        }

        public void bind(ParticipantBreakdown breakdown) {
            tvMemberName.setText(breakdown.getParticipantName());
            tvAmountPaid.setText("Paid: " + currencyFormat.format(breakdown.getAmountPaid()));
            tvAmountOwed.setText("Owes: " + currencyFormat.format(breakdown.getAmountOwed()));

            double netBalance = breakdown.getNetBalance();
            if (netBalance > 0) {
                tvNetBalance.setText("Owes " + currencyFormat.format(netBalance));
                tvNetBalance.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else if (netBalance < 0) {
                tvNetBalance.setText("Gets back " + currencyFormat.format(Math.abs(netBalance)));
                tvNetBalance.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else {
                tvNetBalance.setText("Settled up");
                tvNetBalance.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
        }
    }
}
