package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.iamhabib.easy_preference.EasyPreference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    public TextView first_name,last_name,email,contact_number;
    Spinner gender;
    CircleImageView user_profile_picture;
    FloatingActionButton save_fab;
    FloatingActionButton change_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
//        email =findViewById(R.id.user_email);
        gender = findViewById(R.id.gender);
        save_fab = (FloatingActionButton) findViewById(R.id.save_fab);
        change_profile = (FloatingActionButton) findViewById(R.id.upload_new_image);

        contact_number = findViewById(R.id.phone_number);
        user_profile_picture = findViewById(R.id.edit_profile_picture);
        first_name.setText(pref.first_name);
        last_name.setText(pref.last_name);
//        email.setText(pref.email);
        contact_number.setText(pref.phone_number);
        if (pref.gender.equals("male")) {
            gender.setSelection(0);
        } else {
            gender.setSelection(1);
        }

        Picasso.with(getApplicationContext()).load(pref.picture_url).placeholder(R.drawable.placeholder).into(user_profile_picture);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
