package com.example.shimaa.movies.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Intent sentIntent = getIntent();
            Bundle sentBundle = sentIntent.getExtras();
            DetailFragment mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(sentBundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerdetail,mDetailFragment,"" )
                    .commit();
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#990000")));

    }







}
