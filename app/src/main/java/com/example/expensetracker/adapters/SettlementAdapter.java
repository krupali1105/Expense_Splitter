package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Settlement;

import java.text.DecimalFormat;
import java.util.List;

public class SettlementAdapter extends RecyclerView.Adapter<SettlementAdapter.SettlementViewHolder> {
    private Context context;
    private List<Settlement> settlements;
    private OnSettlementClickListener listener;
    private DecimalFormat currencyFormat;

    public interface OnSettlementClickListener {
        void onSettlementClick(Settlement settlement);
    }

    public SettlementAdapter(Context context, List<Settlement> settlements, OnSettlementClickListener listener) {
        this.context = context;
        this.settlements = settlements;
        this.listener = listener;
        this.currencyFormat = new DecimalFormat("$#,##0.00");
    }

    @NonNull
    @Override
    public SettlementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_settlement, parent, false);
        return new SettlementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettlementViewHolder holder, int position) {
        Settlement settlement = settlements.get(position);
        holder.bind(settlement);
    }

    @Override
    public int getItemCount() {
        return settlements.size();
    }

    public void updateSettlements(List<Settlement> newSettlements) {
        this.settlements = newSettlements;
        notifyDataSetChanged();
    }

    class SettlementViewHolder extends RecyclerView.ViewHolder {
        TextView tvSettlementDescription;
        TextView tvAmount;
        TextView tvStatus;
        TextView btnMarkSettled;

        public SettlementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSettlementDescription = itemView.findViewById(R.id.tvSettlementDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnMarkSettled = itemView.findViewById(R.id.btnMarkSettled);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onSettlementClick(settlements.get(position));
                    }
                }
            });

            btnMarkSettled.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onSettlementClick(settlements.get(position));
                    }
                }
            });
        }

        public void bind(Settlement settlement) {
            tvSettlementDescription.setText(settlement.getSettlementDescription());
            tvAmount.setText(currencyFormat.format(settlement.getAmount()));
            
            if (settlement.isSettled()) {
                tvStatus.setText("Settled");
                tvStatus.setTextColor(context.getResources().getColor(R.color.green, context.getTheme()));
                btnMarkSettled.setText("Settled");
                btnMarkSettled.setBackgroundColor(context.getResources().getColor(R.color.green, context.getTheme()));
                btnMarkSettled.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
            } else {
                tvStatus.setText("Pending");
                tvStatus.setTextColor(context.getResources().getColor(R.color.gray, context.getTheme()));
                btnMarkSettled.setText("Mark as Settled");
                btnMarkSettled.setBackgroundColor(context.getResources().getColor(R.color.purple_500, context.getTheme()));
                btnMarkSettled.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
            }
        }
    }
}
