package com.carefulcollections.gandanga.mishift.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.carefulcollections.gandanga.mishift.Models.Shift;
import com.carefulcollections.gandanga.mishift.Models.Task;
import com.carefulcollections.gandanga.mishift.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gandanga on 2018-04-17.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {
    private List<Task> tasks;
    private Context ctx;

    private List<Task> arraylist;

    public TasksAdapter(List<Task> tasks, Context context){
        this.ctx = context;
        this.tasks = tasks;
        this.arraylist = tasks;
    }

    @NonNull
    @Override
    public TasksAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.current_tasks_layout, parent, false);
        return new TasksAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.MyViewHolder holder, int position) {
        final Task task = arraylist.get(position);

        if (task != null) {
            if(task.id==0){
                holder.task_name.setText(task.name);
            }
            else{
                holder.task_description.setText(task.description);
                holder.start_date.setText(task.start_date.toString());
                holder.end_date.setText(task.end_date.toString());
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView task_name;
        private TextView task_description;
        private TextView end_date;
        private  TextView start_date;
        private ImageView picture_url;

        public MyViewHolder(View v) {
            super(v);
            task_name = v.findViewById(R.id.task_name);
            start_date = v.findViewById(R.id.start_date);
            picture_url = v.findViewById(R.id.picture_url);
            end_date = v.findViewById(R.id.end_date);
            task_description = v.findViewById(R.id.task_description);
        }
    }

}
