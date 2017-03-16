package com.example.shimaa.movies.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;


public class MainActivity extends ActionBarActivity implements MovieListener {

    boolean mIsTwoPane=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
             MovieFragment mMovieFragment = new MovieFragment();
             mMovieFragment.setMovieListener(this);
             getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containermain, mMovieFragment, "")
                    .commit();
        if(null !=findViewById(R.id.containerdetail)){
             mIsTwoPane=true;
        Bundle mbundledetail=new Bundle();
        mbundledetail.putSerializable("TABLET",(Serializable) mIsTwoPane);
        mMovieFragment.setArguments(mbundledetail);
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#990000")));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectMovieItem(Movie mMovie) {
        if (!mIsTwoPane){

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("EXTRA", (Serializable) mMovie);

            startActivity(intent);

        }
        else {
           DetailFragment mDetailFragment=new DetailFragment();
           Bundle mbundle=new Bundle();
           mbundle.putSerializable("EXTRA",(Serializable) mMovie);
           mDetailFragment.setArguments(mbundle);
           getSupportFragmentManager().beginTransaction().replace(R.id.containerdetail,mDetailFragment,"").commit();


        }

    }
}
