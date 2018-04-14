package com.carefulcollections.gandanga.orbit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.carefulcollections.gandanga.orbit.Managers.ManageTeams;
import com.carefulcollections.gandanga.orbit.Models.Team;
import com.carefulcollections.gandanga.orbit.Models.TrainingMaterial;
import com.carefulcollections.gandanga.orbit.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gandanga on 2018-04-13.
 */

public class TrainingMaterialAdapter extends RecyclerView.Adapter<TrainingMaterialAdapter.MyViewHolder> implements Filterable {

    private List<TrainingMaterial> materials;
    private Context ctx;

    private List<TrainingMaterial> arraylist;

    public TrainingMaterialAdapter(List<TrainingMaterial> materials, Context context){
        this.ctx = context;
        this.materials = materials;
        this.arraylist = materials;

    }

    @NonNull
    @Override
    public TrainingMaterialAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.material_row_layout, parent, false);
        return new TrainingMaterialAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, description, created_at;

        public MyViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            description = v.findViewById(R.id.description);
            created_at = v.findViewById(R.id.created_at);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingMaterialAdapter.MyViewHolder holder, int position) {
        final TrainingMaterial material = arraylist.get(position);

        if (material != null) {
            holder.name.setText(WordUtils.capitalizeFully(material.name ));
            holder.description.setText(material.description );
            holder.created_at.setText(material.created_at);
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    TrainingMaterial material_selected = material;
                    Intent intent = new Intent(ctx, TrainingMaterial.class);
                    intent.putExtra("material", material);
                    ctx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint.toString().isEmpty()) {
                    arraylist = materials;
                }else
                {
                    List<TrainingMaterial> results = new ArrayList<>();
                    for (final TrainingMaterial material : materials) {
                        String name = material.name + " " + material.description;
                        if (name.toLowerCase().contains(constraint.toString())){
                            //Log.d("Match","Found here");
                            results.add(material);
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
                arraylist = (List<TrainingMaterial>) results.values;
                //Log.d("Publidshing",String.valueOf(arraylist.size()));
                notifyDataSetChanged();
            }
        };
    }
}
