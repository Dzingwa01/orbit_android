package com.carefulcollections.gandanga.orbit.Managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.HomeActivity;
import com.carefulcollections.gandanga.orbit.EmployeesManager.InboxFragment;
import com.carefulcollections.gandanga.orbit.LoginActivity;
import com.carefulcollections.gandanga.orbit.EmployeesManager.MainActivity;
import com.carefulcollections.gandanga.orbit.R;

import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.Helpers.ViewPagerAdapter;
import com.iamhabib.easy_preference.EasyPreference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
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
        setContentView(R.layout.activity_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.managerTabLayout); // get the reference of TabLayout
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);
        Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("user_pref", Credentials.class);
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

        sp = getSharedPreferences("login", MODE_PRIVATE);
        //Log.d("Payments", "Payments");
        if (!sp.getBoolean("logged", true)) {
            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            back_pressed_count++;
            if (back_pressed_count == 1) {
                Toast.makeText(ManagerActivity.this, "Press Back again to Logout", Toast.LENGTH_SHORT).show();
            } else {
                sp = getSharedPreferences("login", MODE_PRIVATE);
                sp.edit().putBoolean("logged", false).apply();
                finish();
            }

        }
    }
    public void showLogoutConfirmDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ManagerActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ManagerActivity.this);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manager, menu);
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // Handle the camera action
            showLogoutConfirmDialog();
        }else if(id==R.id.nav_employees){
            Intent intent = new Intent(ManagerActivity.this, EmployeeProfile.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_manage_teams){
            Intent intent = new Intent(ManagerActivity.this, ManageTeams.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_training_material){
            Intent intent = new Intent(ManagerActivity.this, TrainingMaterials.class);
            startActivity(intent);
        }

        else if(id==R.id.nav_employee_roles){
            Intent intent = new Intent(ManagerActivity.this, EmployeeRoles.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            feed_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            feed_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    feed_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            feed_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            feed_progress.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(ManagerActivity.this);
                Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
                final String URL = credentials.server_url+"api/login_user";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", mEmail);
                jsonBody.put("password", mPassword);
                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        try {//
                            String id = response.optString("id");
                            Log.d("Check ID",id);
                            if(!id.equals("700")&&!id.equals("701")&&!id.equals("702")){
                                sp.edit().putBoolean("logged", true).apply();
                                sp.edit().putString("user_name", mEmail).apply();
                                sp.edit().putString("password", mPassword).apply();
                                int role_id = response.optInt("role_id");
                                sp.edit().putInt("role_id", role_id).apply();
                                UserPref prefs = new UserPref(response.optString("id"),role_id, response.optString("name"), response.optString("surname"), mEmail, response.optString("contact_number"),response.optString("gender"), response.optString("picture_url"),response.optString("city"));
                                EasyPreference.with(getApplicationContext())
                                        .addObject("user_pref", prefs)
                                        .save();
                                if(role_id==3){
                                    Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
                                    String image_url = Base_URL+"photos" + response.optString("picture_url");
                                    intent.putExtra("picture_url", image_url);
                                    startActivity(intent);
                                }
                                else if(role_id==2){
                                    Intent intent = new Intent(ManagerActivity.this, ManagerActivity.class);
                                    String image_url = Base_URL+"photos" + response.optString("picture_url");
                                    intent.putExtra("picture_url", image_url);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(ManagerActivity.this, HomeActivity.class);
                                    String image_url = Base_URL+"photos" + response.optString("picture_url");
                                    intent.putExtra("picture_url", image_url);
                                    startActivity(intent);
                                }
                            }
                            else{
                                Toast.makeText(ManagerActivity.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();
//                                mPasswordView.setError("Incorrect credentials");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.toString());
                        showProgress(false);
                        Toast.makeText(ManagerActivity.this, "The credentials do not match our records", Toast.LENGTH_SHORT).show();

                    }
                });
                requestQueue.add(loginRequest);
            } catch (JSONException e) {
                Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
//            return null;
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success){
        }

        @Override
        protected void onCancelled() {
            showProgress(false);

        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return HomeActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ManagerShifts(), "Shifts");
        adapter.addFragment(new TeamsFragment(), "Teams");

        adapter.addFragment(new InboxFragment(), "Inbox");
        viewPager.setAdapter(adapter);
    }

    public ManagerActivity() {
        super();
    }


}
