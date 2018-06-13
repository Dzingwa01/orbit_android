package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.R;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.Helpers.ViewPagerAdapter;
import com.iamhabib.easy_preference.EasyPreference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    SharedPreferences sp;
    ProgressBar feed_progress;
    int back_pressed_count;
    private String user_id, picture_url;
    private String user_email, first_name, id_number, gender, phone_number, last_name;
    TextView user_details;
    TextView user_email_textview;
    CircleImageView img;
    View hView;
    String Base_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.homeTabLayout); // get the reference of TabLayout
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);
        Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
        Base_URL = credentials.server_url;
        back_pressed_count = 0;
        UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
        String name = pref.first_name;
        String surname = pref.last_name;
        last_name = surname;
        String email = pref.email;
//            id_number = pref.id_number;
        phone_number = pref.phone_number;
        gender = pref.gender;
        user_id = pref.id;
        picture_url = pref.picture_url;
        //Log.d("Picture URL", picture_url);
        user_email = email;
        user_details = (TextView) hView.findViewById(R.id.user_name);
        user_email_textview = (TextView) hView.findViewById(R.id.user_email);
        img = hView.findViewById(R.id.user_image);
        Picasso.with(getApplicationContext()).load(Base_URL+picture_url).placeholder(R.drawable.placeholder).into(img);
        user_details.setText(name + "  " + surname);
        user_email_textview.setText(email);
        first_name = name;//
        if (shouldAskPermissions()) {
            askPermissions();
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            back_pressed_count++;
            if (back_pressed_count == 1) {
                Toast.makeText(MainActivity.this, "Press Back again to Logout", Toast.LENGTH_SHORT).show();
            } else {
                sp = getSharedPreferences("login", MODE_PRIVATE);
                sp.edit().putBoolean("logged", false).apply();
                finish();
            }

        }
    }
    public void showLogoutConfirmDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View view = inflater.inflate(R.layout.logout_dialog, null);
        alertDialogBuilder.setView(view);
//        alertDialogBuilder.setTitle("Image Caption");
//        final EditText captionText = view.findViewById(R.id.caption_text);
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                    }
                });
            }
        });
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().putBoolean("logged", false).apply();
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CurrentScheduleFragment(), "Today");
        adapter.addFragment(new EmployeeScheduleFragment(), "Schedule");
        adapter.addFragment(new ChatFragment(), "Chat");
        adapter.addFragment(new InboxFragment(), "Requests");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.action_messages){
            Intent intent = new Intent(MainActivity.this,EmployeeMessages.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_signout) {
            // Handle the camera action
            showLogoutConfirmDialog();
        }
        else if(id == R.id.nav_my_teams){
            Intent intent = new Intent(MainActivity.this, EmployeeTeams.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_profile){
            Intent intent = new Intent(MainActivity.this, MyProfile.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_swap_shift){
            Intent intent = new Intent(MainActivity.this,SwapOfferActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.nav_training_material){
            Intent intent = new Intent(MainActivity.this,EmployeeTrainingMaterial.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
