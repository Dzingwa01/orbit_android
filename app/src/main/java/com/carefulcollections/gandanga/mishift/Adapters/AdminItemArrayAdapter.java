package com.carefulcollections.gandanga.mishift.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carefulcollections.gandanga.mishift.Models.Item;
import com.carefulcollections.gandanga.mishift.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

/**
 * Created by Gandanga on 2018-04-19.
 */

public class AdminItemArrayAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    Context ctx;

    private ArrayList<Item> itemList;

    // Constructor of the class
    public AdminItemArrayAdapter(ArrayList<Item> itemList, Context ctx) {
        this.itemList = itemList;
        this.ctx = ctx;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    // determine which layout to use for the row
    @Override
    public int getItemViewType(int position) {
        Item item = itemList.get(position);
        if (item.getType() == Item.ItemType.ONE_ITEM) {
            return TYPE_ONE;
        } else if (item.getType() == Item.ItemType.TWO_ITEM) {
            return TYPE_TWO;
        } else {
            return -1;
        }
    }


    // specify the row layout file and click for each row
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_schedule_layout, parent, false);
            return new ItemArrayAdapter.ViewHolderOne(view);
        } else if (viewType == TYPE_TWO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_tasks_layout, parent, false);
            return new ItemArrayAdapter.ViewHolderTwo(view);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int listPosition) {
        switch (holder.getItemViewType()) {
            case TYPE_ONE:
                initLayoutOne((ItemArrayAdapter.ViewHolderOne) holder, listPosition);
                break;
            case TYPE_TWO:
                initLayoutTwo((ItemArrayAdapter.ViewHolderTwo) holder, listPosition);
                break;
            default:
                break;
        }
    }

    private void initLayoutOne(ItemArrayAdapter.ViewHolderOne holder, int pos) {
        final Item shift = itemList.get(pos);
        if (shift != null) {
//            if (shift.item_description == "none") {
//                holder.shift_title.setText(WordUtils.capitalizeFully(shift.item_name));
//            } else {
//                holder.shift_title.setText(WordUtils.capitalizeFully(shift.item_name));
//                holder.start_date.setText(shift.item_start_date.toString());
//                holder.end_date.setText(shift.item_end_date.toString());
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                    }
//                });
//            }

        }

    }

    private void initLayoutTwo(ItemArrayAdapter.ViewHolderTwo holder, int pos) {
        final Item task = itemList.get(pos);

        if (task != null) {
//            if(task.item_description=="none"){
//                holder.task_name.setText(task.item_name);
//            }
//            else{
//                holder.task_name.setText(task.item_name);
//                holder.task_description.setText(task.item_description);
//                holder.start_date.setText(task.item_start_date.toString());
//                holder.end_date.setText(task.item_end_date.toString());
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//            }

        }
    }


    // Static inner class to initialize the views of rows
    static class ViewHolderOne extends RecyclerView.ViewHolder {
        TextView shift_title;
        public TextView start_date;
        public TextView end_date;
        double shift_duration;
        public ImageView user_picture_url;

        public ViewHolderOne(View v) {
            super(v);
            shift_title = v.findViewById(R.id.shift_title);
            start_date = v.findViewById(R.id.start_date);
            user_picture_url = v.findViewById(R.id.profile_picture);
            end_date = v.findViewById(R.id.end_date);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {
        public TextView task_name;
        public TextView task_description;
        public TextView end_date;
        public TextView start_date;
        public ImageView picture_url;

        public ViewHolderTwo(View v) {
            super(v);
            task_name = v.findViewById(R.id.task_name);
            start_date = v.findViewById(R.id.start_date);
            picture_url = v.findViewById(R.id.picture_url);
            end_date = v.findViewById(R.id.end_date);
            task_description = v.findViewById(R.id.task_description);
        }
    }
}
