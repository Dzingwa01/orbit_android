package com.carefulcollections.gandanga.orbit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.carefulcollections.gandanga.orbit.EmployeesManager.TeamMemberDetails;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.Item;
import com.carefulcollections.gandanga.orbit.Models.LeaveRequest;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.R;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.iamhabib.easy_preference.EasyPreference;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

/**
 * Created by Gandanga on 2018-06-11.
 */

public class ManagerInboxAdapter extends RecyclerView.Adapter<ManagerInboxAdapter.MyViewHolder> {
    private ArrayList<LeaveRequest> itemList;
    Context ctx;

    public ManagerInboxAdapter(ArrayList<LeaveRequest> itemList,Context ctx){
        this.itemList = itemList;
        this.ctx = ctx;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.off_request_inbox, parent, false);
        return new ManagerInboxAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView shift_date;
        private TextView requestor_name;
        private TextView off_start_date,off_end_date;
        private Button off_accept;
        private Button more_details;
        public MyViewHolder(View v) {
            super(v);
            requestor_name = v.findViewById(R.id.requestor_name);
            off_start_date = v.findViewById(R.id.off_start_date);
            off_end_date = v.findViewById(R.id.off_end_date);
            off_accept = v.findViewById(R.id.accept);
            more_details = v.findViewById(R.id.more_details);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final LeaveRequest request = itemList.get(position);

        if (request != null) {
            holder.requestor_name.setText(WordUtils.capitalizeFully(request.name )+ " " + WordUtils.capitalizeFully(request.surname));
            holder.off_start_date.setText(request.off_start_date + " - " + request.off_start_time);
            holder.off_end_date.setText(request.off_end_date + " - "+ request.off_end_time);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LeaveRequest request_selected = request;
                    Intent intent = new Intent(ctx, TeamMemberDetails.class);
                    intent.putExtra("request", request_selected);
                    ctx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return itemList == null ? 0 : itemList.size();
    }
}
