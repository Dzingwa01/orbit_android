package com.carefulcollections.gandanga.mishift.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.carefulcollections.gandanga.mishift.Models.Role;
import com.carefulcollections.gandanga.mishift.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gandanga on 2018-04-16.
 */

public class EmployeeRoleAdapter extends RecyclerView.Adapter<EmployeeRoleAdapter.MyViewHolder> implements Filterable {
    private List<Role> roles;
    private Context ctx;

    private List<Role> arraylist;

    public EmployeeRoleAdapter(List<Role> roles, Context context){
        this.ctx = context;
        this.roles = roles;
        this.arraylist = roles;

    }

    @NonNull
    @Override
    public EmployeeRoleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.roles_layout, parent, false);
        return new EmployeeRoleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeRoleAdapter.MyViewHolder holder, int position) {
        final Role role = arraylist.get(position);

        if (roles != null) {
            holder.role_name.setText(WordUtils.capitalizeFully(role.role_name ));
            holder.role_description.setText(role.role_description);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView role_name, role_description;


        public MyViewHolder(View v) {
            super(v);
            role_name = v.findViewById(R.id.role_name);
            role_description = v.findViewById(R.id.role_description);
        }
    }
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint.toString().isEmpty()) {
                    arraylist = roles;
                }else
                {
                    List<Role> results = new ArrayList<>();
                    for (final Role role : roles) {
                        String role_name = role.role_name + " " + role.role_description;
                        if (role_name.toLowerCase().contains(constraint.toString())){
                            //Log.d("Match","Found here");
                            results.add(role);
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
                arraylist = (List<Role>) results.values;
                //Log.d("Publidshing",String.valueOf(arraylist.size()));
                notifyDataSetChanged();
            }
        };
    }
}
