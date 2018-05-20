package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.iamhabib.easy_preference.EasyPreference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamMemberDetails extends AppCompatActivity {
    TextView user_fullname,gender,contact_number,package_name,company_name,city,user_email;
    CircleImageView profile_picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user_fullname = findViewById(R.id.user_fullname);
        gender = findViewById(R.id.gender);
        contact_number = findViewById(R.id.contact_number);
        package_name = findViewById(R.id.package_name);
        company_name = findViewById(R.id.company_name);
//        city = findViewById(R.id.city_name);
        user_email = findViewById(R.id.user_email);
        profile_picture = findViewById(R.id.profile_picture);
        Intent intent = getIntent();
        User pref = (User) intent.getSerializableExtra("user");

        Credentials credentials = EasyPreference.with(TeamMemberDetails.this).getObject("server_details", Credentials.class);
        final String url = credentials.server_url;
        user_fullname.setText(pref.name + " " + pref.surname);
        gender.setText(pref.gender);
        contact_number.setText(pref.contact_number);
//        package_name.setText(pref.);
        company_name.setText(pref.company_name);
        user_email.setText(pref.email);

        Picasso.with(getApplicationContext()).load(url+pref.picture_url).placeholder(R.drawable.placeholder).into(profile_picture);
        final ImagePopup imagePopup = new ImagePopup(TeamMemberDetails.this);
        imagePopup.setWindowHeight(400); // Optional
        imagePopup.setWindowWidth(400); // Optional
        imagePopup.setBackgroundColor(Color.BLACK);  // Optional
        imagePopup.setFullScreen(true); // Optional
        imagePopup.initiatePopupWithPicasso(pref.picture_url);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
