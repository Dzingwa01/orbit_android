package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carefulcollections.gandanga.orbit.Helpers.ViewPagerAdapter;
import com.carefulcollections.gandanga.orbit.R;

public class SwapOfferActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_offer);
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.homeTabLayout); // get the reference of TabLayout
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SwapFragment(), "Swap");
        adapter.addFragment(new OfferShiftFragment(), "Offer Shift");
        adapter.addFragment(new RequestTimeOff(), "Time Off");
        viewPager.setAdapter(adapter);
    }
}
