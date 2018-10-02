package com.carefulcollections.gandanga.mishift.Helpers;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
import com.carefulcollections.gandanga.mishift.R;
import com.iamhabib.easy_preference.EasyPreference;
import com.squareup.picasso.Picasso;

public class ImageFullScreen extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        imageView = findViewById(R.id.post_image);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(ImageFullScreen.this));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));
        Bundle extras = getIntent().getExtras();
        Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);

        final String url = credentials.server_url;

        Picasso.with(ImageFullScreen.this).load(url + extras.getString("picture_url")).into(imageView);

    }


}
