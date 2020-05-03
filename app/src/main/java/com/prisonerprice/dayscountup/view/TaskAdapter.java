package com.prisonerprice.dayscountup.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.database.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> mTaskEntries;
    private Context mContext;
    private DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private ItemClickListener mItemClickListener;

    public TaskAdapter(Context context, ItemClickListener itemClickListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = mTaskEntries.get(position);
        String description = task.getDescription();
        String updatedAt = format.format(task.getUpdatedAt());

        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
    }

    public List<Task> getTasks() {
        return mTaskEntries;
    }

    @Override
    public int getItemCount() {
        if(mTaskEntries == null) return 0;
        return mTaskEntries.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView taskDescriptionView;
        TextView updatedAtView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDescriptionView = itemView.findViewById(R.id.description_tv);
            updatedAtView = itemView.findViewById(R.id.updated_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

    public interface ItemClickListener{
        void onItemClickListener(int itemId);
    }
}
