package com.carefulcollections.gandanga.mishift.EmployeesManager;

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

import com.carefulcollections.gandanga.mishift.Managers.ManageTeams;
import com.carefulcollections.gandanga.mishift.Models.Team;
import com.carefulcollections.gandanga.mishift.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gandanga on 2018-04-17.
 */

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.MyViewHolder> implements Filterable {

private List<Team> teams;
private Context ctx;

private List<Team> arraylist;

public TeamsAdapter(List<Team> teams, Context context){
        this.ctx = context;
        this.teams = teams;
        this.arraylist = teams;

        }

@NonNull
@Override
public TeamsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.teams_row_layout, parent, false);
        return new TeamsAdapter.MyViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Team team = arraylist.get(position);
        if (team != null) {
            holder.city_name.setText(WordUtils.capitalizeFully(team.city_name ));
            holder.team_name.setText(WordUtils.capitalizeFully(team.team_name ));
            holder.team_description.setText(WordUtils.capitalizeFully(team.team_description ));
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Team team_selected = team;

                    Intent intent = new Intent(ctx, ViewTeams.class);
                    intent.putExtra("team", team_selected);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.getApplicationContext().startActivity(intent);

                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView city_name, team_name, team_description;

    public MyViewHolder(View v) {
        super(v);
        city_name = v.findViewById(R.id.city_name);
        team_name = v.findViewById(R.id.team_name);
        team_description = v.findViewById(R.id.team_description);
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
                    arraylist = teams;
                }else
                {
                    List<Team> results = new ArrayList<>();
                    for (final Team team : teams) {
                        String team_name = team.team_name + " " + team.city_name;
                        if (team_name.toLowerCase().contains(constraint.toString())){
                            //Log.d("Match","Found here");
                            results.add(team);
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
                arraylist = (List<Team>) results.values;
                //Log.d("Publidshing",String.valueOf(arraylist.size()));
                notifyDataSetChanged();
            }
        };
    }
}
