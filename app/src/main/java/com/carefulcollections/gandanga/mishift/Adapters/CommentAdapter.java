package com.carefulcollections.gandanga.mishift.Adapters;

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
import com.carefulcollections.gandanga.mishift.Helpers.Credentials;
import com.carefulcollections.gandanga.mishift.Helpers.ImageFullScreen;
import com.carefulcollections.gandanga.mishift.Models.Comment;
import com.carefulcollections.gandanga.mishift.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.iamhabib.easy_preference.EasyPreference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        CircleImageView profile_picture_post_comment;
        ExpandableTextView article_text;

        public MyViewHolder(View v) {
            super(v);
            full_name = (TextView) v.findViewById(R.id.user_fullname);
            publication_date = v.findViewById(R.id.publication_date);
            img = v.findViewById(R.id.profile_picture_post_comment);
            comment_image = v.findViewById(R.id.comment_image);
            article_text = (ExpandableTextView) v.findViewById(R.id.expand_text_view);
            profile_picture_post_comment = v.findViewById(R.id.profile_picture_post_comment);
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
        Credentials credentials = EasyPreference.with(this.ctx).getObject("server_details", Credentials.class);
        final String url = credentials.server_url;
        Glide.with(ctx.getApplicationContext()).load(url + comment.user_picture_url)
                .into(holder.profile_picture_post_comment);
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
        try {

            if (!comment.picture_url.equals("none")) {
                Glide.with(ctx.getApplicationContext()).load(url+ comment.picture_url)
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

