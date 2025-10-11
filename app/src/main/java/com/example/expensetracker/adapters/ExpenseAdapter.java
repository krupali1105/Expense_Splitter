package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Expense;

import java.text.DecimalFormat;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenses;
    private Context context;
    private OnExpenseClickListener listener;
    private DecimalFormat currencyFormat;

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
        this.currencyFormat = new DecimalFormat("$#,##0.00");
    }

    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void updateExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvExpenseName;
        private TextView tvAmount;
        private TextView tvPayer;
        private TextView tvDate;
        private TextView tvCategory;
        private TextView tvParticipants;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpenseName = itemView.findViewById(R.id.tvExpenseName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPayer = itemView.findViewById(R.id.tvPayer);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvParticipants = itemView.findViewById(R.id.tvParticipants);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onExpenseClick(expenses.get(position));
                    }
                }
            });
        }

        public void bind(Expense expense) {
            tvExpenseName.setText(expense.getExpenseName());
            tvAmount.setText(currencyFormat.format(expense.getAmount()));
            tvPayer.setText("Paid by: " + expense.getPayer());
            tvDate.setText(expense.getDate());
            tvCategory.setText(expense.getCategory() != null ? expense.getCategory() : "General");
            tvParticipants.setText("Split between: " + expense.getParticipants());
        }
    }
}
