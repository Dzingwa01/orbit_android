package com.carefulcollections.gandanga.orbit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.carefulcollections.gandanga.orbit.Helpers.ImageFullScreen;
import com.carefulcollections.gandanga.orbit.Models.Comment;
import com.carefulcollections.gandanga.orbit.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.iamhabib.easy_preference.EasyPreference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Gandanga on 2018-05-02.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<Comment> comments;
    Context ctx;

    ImageView profile_image;
    ArrayList<Comment> comments_list;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView full_name;
        RelativeTimeTextView publication_date;
        ImageView comment_image, img;
        ExpandableTextView article_text;

        public MyViewHolder(View v) {
            super(v);
            full_name = (TextView) v.findViewById(R.id.full_name);
            publication_date = v.findViewById(R.id.publication_date);
            img = v.findViewById(R.id.profile_picture_post_comment);
            comment_image = v.findViewById(R.id.comment_image);
            article_text = (ExpandableTextView) v.findViewById(R.id.expand_text_view);
        }
    }

    public CommentAdapter(List<Comment> posts_list, Context ctx) {
        this.comments = posts_list;
        this.ctx = ctx;
    }

    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row_layout, parent, false);
        return new CommentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.MyViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        holder.article_text.setText(comment.comment_text);
        holder.full_name.setText(comment.first_name + " " + comment.last_name);

        if (comment.created_at != null) {
            long now = System.currentTimeMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            try {
                Date converted = dateFormat.parse(comment.created_at);
                c.setTime(converted);
                c.add(Calendar.HOUR_OF_DAY, 2);
                String new_date = dateFormat.format(c.getTime());
                holder.publication_date.setReferenceTime(c.getTime().getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                holder.publication_date.setText((String.valueOf(comment.created_at)));
            }

        }
//        UsersPicturesPref pref = EasyPreference.with(ctx.getApplicationContext()).getObject("users_pictures", UsersPicturesPref.class);
//        for (int i = 0; i < pref.user_pictures.size(); i++) {
//            UserPictures user_picture = pref.user_pictures.get(i);
//            if (comment.author_id.equals(user_picture._id)) {
//                try {
//                    RequestOptions requestOptions = new RequestOptions();
//                    requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
//                    requestOptions.placeholder(R.drawable.placeholder);
//                    requestOptions.centerCrop();
//                    String picture_url = user_picture.picture_url;
//                    Glide.with(ctx.getApplicationContext()).load("http://ec2-18-220-230-232.us-east-2.compute.amazonaws.com/" + picture_url)
//                            .apply(requestOptions)
//                            .into(holder.img);
//
//                } catch (Exception e) {
//                    //Log.d("No pictures", "no picture as yet");
//                    Glide.with(ctx.getApplicationContext()).load(R.drawable.placeholder)
//                            .into(holder.img);
//                }
//            }
//        }
        try {
            if (!comment.picture_url.equals("none")) {
                Glide.with(ctx.getApplicationContext()).load("http://ec2-18-220-230-232.us-east-2.compute.amazonaws.com/" + comment.picture_url)
                        .into(holder.comment_image);
                holder.comment_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ctx.getApplicationContext(), ImageFullScreen.class);
                        intent.putExtra("picture_url", comment.picture_url);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.getApplicationContext().startActivity(intent);
                    }
                });
            } else {
                holder.comment_image.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            holder.comment_image.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

}

