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
import com.carefulcollections.gandanga.orbit.EmployeesManager.EditProfile;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Managers.EmployeeProfile;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.R;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.iamhabib.easy_preference.EasyPreference;
//import com.ceylonlabs.imageviewpopup.ImagePopup;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gandanga on 2018-04-12.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> implements Filterable {
    private List<User> users;
    private Context ctx;

   private List<User> arraylist;

    public EmployeeAdapter(List<User> users, Context context){
        this.ctx = context;
        this.users = users;
        this.arraylist = users;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_row_layout, parent, false);
        return new EmployeeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final User user = arraylist.get(position);

        if (user != null) {
            holder.full_name.setText(WordUtils.capitalizeFully(user.name )+ " " + WordUtils.capitalizeFully(user.surname));
            holder.email.setText(user.email);
            holder.contact_number.setText(user.contact_number);

                    try {
                        String picture_url = user.picture_url;
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                        requestOptions.placeholder(R.drawable.placeholder);
                        requestOptions.centerCrop();
                        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
//        UserPref local_pref = EasyPreference.with(EditProfile.this).getObject("user_pref", UserPref.class);
                        final String url = credentials.server_url;
                        Glide.with(ctx).load( url+picture_url)
                                .apply(requestOptions)
                                .into(holder.user_picture_url);
                        final ImagePopup imagePopup = new ImagePopup(ctx);
                        imagePopup.setWindowHeight(400); // Optional
                        imagePopup.setWindowWidth(400); // Optional
                        imagePopup.setBackgroundColor(Color.BLACK);  // Optional
                        imagePopup.setFullScreen(true); // Optional
                        imagePopup.initiatePopupWithPicasso( picture_url);
                        holder.user_picture_url.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imagePopup.viewPopup();
                            }
                        });
                    } catch (Exception e) {
                        //Log.d("No pictures", "no picture as yet");
                    }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    User user_selected = user;
                    Intent intent = new Intent(ctx, EmployeeProfile.class);
                    intent.putExtra("teacher", user_selected);
                    ctx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView full_name, email,contact_number;
        private ImageView user_picture_url;

        public MyViewHolder(View v) {
            super(v);
            full_name = v.findViewById(R.id.full_name);
            email = v.findViewById(R.id.email);
            user_picture_url = v.findViewById(R.id.profile_picture);
            contact_number = v.findViewById(R.id.contact_number);
        }
    }
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint.toString().isEmpty()) {
                    arraylist = users;
                }else
                {
                    List<User> results = new ArrayList<>();
                    for (final User user : users) {
                        String user_name = user.name + " " + user.surname;
                        if (user_name.toLowerCase().contains(constraint.toString())){
                            //Log.d("Match","Found here");
                            results.add(user);
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
                arraylist = (List<User>) results.values;
                //Log.d("Publidshing",String.valueOf(arraylist.size()));
                notifyDataSetChanged();
            }
        };
    }

}
