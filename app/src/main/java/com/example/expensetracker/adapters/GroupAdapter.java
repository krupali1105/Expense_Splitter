package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Group;

import java.text.DecimalFormat;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<Group> groups;
    private Context context;
    private OnGroupClickListener listener;
    private DecimalFormat currencyFormat;

    public interface OnGroupClickListener {
        void onGroupClick(Group group);
        void onEditGroup(Group group);
        void onDeleteGroup(Group group);
    }

    public GroupAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
        this.currencyFormat = new DecimalFormat("$#,##0.00");
    }

    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(List<Group> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName;
        private TextView tvTotalExpenses;
        private TextView tvDescription;
        private TextView tvMemberCount;
        private TextView tvCreatedDate;
        private ImageButton btnMoreOptions;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvTotalExpenses = itemView.findViewById(R.id.tvTotalExpenses);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onGroupClick(groups.get(position));
                    }
                }
            });

            btnMoreOptions.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        showPopupMenu(v, groups.get(position));
                    }
                }
            });
        }

        private void showPopupMenu(View view, Group group) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.group_options_menu, popupMenu.getMenu());
            
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit_group) {
                    listener.onEditGroup(group);
                    return true;
                } else if (item.getItemId() == R.id.action_delete_group) {
                    listener.onDeleteGroup(group);
                    return true;
                }
                return false;
            });
            
            popupMenu.show();
        }

        public void bind(Group group) {
            tvGroupName.setText(group.getGroupName());
            tvTotalExpenses.setText(currencyFormat.format(group.getTotalExpenses()));
            tvDescription.setText(group.getDescription() != null ? group.getDescription() : "No description");
            tvMemberCount.setText(group.getMemberCount() + " members");
            tvCreatedDate.setText(group.getCreatedDate() != null ? group.getCreatedDate() : "Unknown date");
        }
    }
}
