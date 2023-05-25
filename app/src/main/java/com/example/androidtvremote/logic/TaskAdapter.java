package com.example.androidtvremote.logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.example.androidtvremote.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    private final ArrayList<Task> tasks;
    private TaskViewHolder holder;

    public TaskAdapter(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View taskView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcycleritem_auto,parent,false);
        TaskViewHolder holder = new TaskViewHolder(taskView).linkAdapter(this);
        this.holder = holder;
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position){
        Task currentTask = tasks.get(position);
        holder.getAction().setText(currentTask.getActionName());
        int minute = currentTask.getTime().get(Calendar.MINUTE);
        int hour = currentTask.getTime().get(Calendar.HOUR_OF_DAY);
        holder.getTime().setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
    }

    @Override
    public int getItemCount(){
        return tasks.size();
    }
    protected static class TaskViewHolder extends RecyclerView.ViewHolder{
        private TextView time;
        private TextView action;
        private Button remove;
        private TaskAdapter adapter;

        public TaskViewHolder(@NonNull View itemView){
            super(itemView);
            time = itemView.findViewById(R.id.time);
            action = itemView.findViewById(R.id.action);
            remove = itemView.findViewById(R.id.remove);
            remove.setOnClickListener(this::removeItem);
        }
        public void removeItem(View view) {
            ATVHelper db = new ATVHelper(view.getContext());
            Task task = adapter.tasks.get(getAdapterPosition());
            db.deleteTask(task);
            db.close();
            adapter.tasks.remove(getAdapterPosition());
            adapter.notifyItemRemoved(getAdapterPosition());
            WorkManager workManager = WorkManager.getInstance();
            workManager.cancelWorkById(UUID.fromString(task.getId()));
        }


        public TaskViewHolder linkAdapter(TaskAdapter adapter){
            this.adapter = adapter;
            return this;
        }

        public TextView getAction() {
            return action;
        }

        public TextView getTime() {
            return time;
        }

        public void setTime(TextView time) {
            this.time = time;
        }

        public void setAction(TextView action) {
            this.action = action;
        }

        public void setRemove(Button remove) {
            this.remove = remove;
        }
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
