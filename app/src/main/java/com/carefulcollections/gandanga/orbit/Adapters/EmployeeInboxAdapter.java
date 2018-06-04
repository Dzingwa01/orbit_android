package com.carefulcollections.gandanga.orbit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carefulcollections.gandanga.orbit.EmployeesManager.ShiftDetails;
import com.carefulcollections.gandanga.orbit.Models.InboxItem;
import com.carefulcollections.gandanga.orbit.Models.Item;
import com.carefulcollections.gandanga.orbit.R;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Gandanga on 2018-05-29.
 */

public class EmployeeInboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    Context ctx;
    private ArrayList<InboxItem> itemList;

    // Constructor of the class
    public EmployeeInboxAdapter(ArrayList<InboxItem> itemList, Context ctx) {
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
        InboxItem item = itemList.get(position);
        if (item.getType() == InboxItem.ItemType.ONE_ITEM) {
            return TYPE_ONE;
        } else if (item.getType() == InboxItem.ItemType.TWO_ITEM) {
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
                    .inflate(R.layout.swap_requests_layout, parent, false);
            return new ViewHolderOne(view);
        } else if (viewType == TYPE_TWO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.off_request_layout, parent, false);
            return new ViewHolderTwo(view);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int listPosition) {
        switch (holder.getItemViewType()) {
            case TYPE_ONE:
                initLayoutOne((ViewHolderOne) holder, listPosition);
                break;
            case TYPE_TWO:
                initLayoutTwo((ViewHolderTwo) holder, listPosition);
                break;
            default:
                break;
        }
    }

    private void initLayoutOne(ViewHolderOne holder, int pos) {
        final InboxItem shift = itemList.get(pos);
        if (shift != null) {
            holder.shift_date.setText(shift.shift_date);
            holder.requestor_name.setText(shift.name + " - " + shift.surname);
            holder.swap_shift_date.setText(shift.with_shift);
            }
    }

    private void initLayoutTwo(ViewHolderTwo holder, int pos) {
        final InboxItem shift = itemList.get(pos);
        if (shift != null) {
            holder.shift_date.setText(shift.shift_date);
            holder.employee_name.setText(shift.name + " - " + shift.surname);
//            holder.swap_shift_date.setText(shift.shift_date);
        }
    }


    // Static inner class to initialize the views of rows
    static class ViewHolderOne extends RecyclerView.ViewHolder {
        private TextView shift_date;
        private TextView requestor_name;
        private TextView swap_shift_date;
        public ViewHolderOne(View v) {
            super(v);
            shift_date = v.findViewById(R.id.shift_date);
            requestor_name = v.findViewById(R.id.requestor_name);
            swap_shift_date = v.findViewById(R.id.swap_shift_date);

        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {
        private TextView shift_date;
        private TextView employee_name;
        private Button accept;
        private Button more_details;

        public ViewHolderTwo(View v) {
            super(v);
            shift_date = v.findViewById(R.id.shift_date);
            employee_name = v.findViewById(R.id.employee_name);
            accept = v.findViewById(R.id.accept);
            more_details = v.findViewById(R.id.more_details);
//            task_description = v.findViewById(R.id.task_description);
        }
    }
}