package com.carefulcollections.gandanga.orbit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.carefulcollections.gandanga.orbit.Managers.EmployeeProfile;
import com.carefulcollections.gandanga.orbit.Models.Shift;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.R;
import com.ceylonlabs.imageviewpopup.ImagePopup;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gandanga on 2018-04-17.
 */

public class CurrentShiftAdapter extends RecyclerView.Adapter<CurrentShiftAdapter.MyViewHolder> implements Filterable {
    private List<Shift> shifts;
    private Context ctx;

    private List<Shift> arraylist;

    public CurrentShiftAdapter(List<Shift> shifts, Context context){
        this.ctx = context;
        this.shifts = shifts;
        this.arraylist = shifts;

    }

    @NonNull
    @Override
    public CurrentShiftAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.current_schedule_layout, parent, false);
        return new CurrentShiftAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentShiftAdapter.MyViewHolder holder, int position) {
        final Shift shift = arraylist.get(position);

        if (shift != null) {
            if(shift.team_name=="none"){
                holder.shift_title.setText(WordUtils.capitalizeFully(shift.shift_title ));
            }
            else{
                holder.shift_title.setText(WordUtils.capitalizeFully(shift.shift_title ));
                holder.start_date.setText(shift.start_date.toString());
                holder.end_date.setText(shift.end_date.toString());
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
        private TextView shift_title;
        private TextView start_date;
        private TextView end_date;
        double shift_duration;
        private ImageView user_picture_url;

        public MyViewHolder(View v) {
            super(v);
            shift_title = v.findViewById(R.id.shift_title);
            start_date = v.findViewById(R.id.start_date);
            user_picture_url = v.findViewById(R.id.profile_picture);
            end_date = v.findViewById(R.id.end_date);
        }
    }
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint.toString().isEmpty()) {
                    arraylist = shifts;
                }else
                {
                    List<Shift> results = new ArrayList<>();
                    for (final Shift shift : shifts) {
                        String shift_title = shift.shift_title;
                        if (shift_title.toLowerCase().contains(constraint.toString())){
                            //Log.d("Match","Found here");
                            results.add(shift);
                        }
                    }
                    arraylist = results;
                    //Log.d("List_Lenght",String.valueOf(arraylist.size()));
                }
                FilterResults oReturn = new FilterResults();
                oReturn.values = arraylist;
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arraylist = (List<Shift>) results.values;
                //Log.d("Publidshing",String.valueOf(arraylist.size()));
                notifyDataSetChanged();
            }
        };
    }
}
