package com.example.shimaa.movies.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shimaa on 19/10/2016.
 */
public  class MovieFragment extends Fragment {

    private static final int DB_VERSION = 1;
    private MovieAdapter moviesinfo;
    private boolean connected;

    private MovieListener mListener;
    void setMovieListener(MovieListener ml){this.mListener=ml;}
    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Configuration dbConfig =
                new Configuration
                        .Builder(getActivity())
                        .setDatabaseName("movies")
                        .setDatabaseVersion(DB_VERSION)
                        .create();
        ActiveAndroid.initialize(dbConfig);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridView);

        moviesinfo=new MovieAdapter(getActivity());
        connected=isConnected();

        gridview.setAdapter(moviesinfo);



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(!connected) {
                   Toast toast=Toast.makeText(getActivity(),"NO NETWORK CONNECTION",Toast.LENGTH_SHORT);
                   toast.show();
               }
               else {
                Movie movie = (Movie)moviesinfo.getItem(position);
                mListener.selectMovieItem(movie);
               }
           }
        });
        return rootView;
    }

    private void updateMovies() {
        MoviesTask movieTask = new MoviesTask();

        movieTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private Movie[] getofflineMovies()
    {
        List<Movie> mMovies = new Select()
                .from(Movie.class)
                .execute();
        Movie[] mMoviesArray=new Movie[mMovies.size()];

        for (int i=0;i<mMovies.size();i++)
        {

            mMoviesArray[i]=mMovies.get(i);
        }

        return mMoviesArray;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
 public  class MoviesTask extends AsyncTask<Void,Void,Movie[]>
{


    private final String LOG_TAG = MoviesTask.class.getSimpleName();


        private Movie[] getFavuriteMovies()
        {
         List<Movie> mMovies = new Select()
                 .from(Movie.class)
                 .where("favourite = ?", true)
                 .execute();
            Movie[] mMoviesArray=new Movie[mMovies.size()];

            for (int i=0;i<mMovies.size();i++){

                mMoviesArray[i]=mMovies.get(i);
            }

            return mMoviesArray;
         }


        private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";
        final  String TAG_ID="id";
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        new Delete().from(Movie.class).where("favourite = ?",false).execute();
        Movie[] movies = new Movie[resultsArray.length()];



        for (int i = 0; i < resultsArray.length(); i++) {
            String originaltitle;
            String posterpath;
            String overview;
            String vote;
            String releasedate;
            String id;

            JSONObject movieInfo = resultsArray.getJSONObject(i);
            posterpath = movieInfo.getString(TAG_POSTER_PATH);
            originaltitle=movieInfo.getString(TAG_ORIGINAL_TITLE);
            overview=movieInfo.getString(TAG_OVERVIEW);
            vote=movieInfo.getString(TAG_VOTE_AVERAGE);
            releasedate=movieInfo.getString(TAG_RELEASE_DATE);
            id=movieInfo.getString(TAG_ID);
            movies[i]= new Movie();
            movies[i] .setOriginaltitle(originaltitle);
            movies[i].setPosterpath(posterpath);
            movies[i].setOverview(overview);
            movies[i].setReleasedate(releasedate);
            movies[i].setVoterate(Double.parseDouble(vote));
            movies[i].setmId(Integer.parseInt(id));
            movies[i].save();

        }

        return movies;
    }



    @Override
    protected Movie[] doInBackground(Void ...params) {

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pref_type = sharedPrefs.getString(
                getString(R.string.select),
                getString(R.string.select_2));
        if (!(pref_type.equals("top_rated") || pref_type.equals("popular")))
        {
            return getFavuriteMovies();

        }
        if(!connected){
            return getofflineMovies();
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        try {

            final String TMDB_BASE_URL;
            if (pref_type.equals("top_rated")) {
                TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";

            } else {
                TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
            }
          final String APPID_PARAM = "api_key";
          final String appid ="b61faf5a9ec24cba5189261629354988";
          Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, appid)
                    .build();




            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
       }


        @Override
    protected void onPostExecute(Movie[] strings) {
        super.onPostExecute(strings);
            if(strings!=null){
            moviesinfo.mMovies.clear();
            for (int i=0;i<strings.length;i++)
            {
                moviesinfo.mMovies.add(strings[i]);

            }

            moviesinfo.notifyDataSetChanged();

            Bundle sentBundle =  getArguments();

            if (sentBundle != null && !sentBundle.isEmpty()) {
                boolean mTwoPane=false;
                mTwoPane = (Boolean) sentBundle.getSerializable("TABLET");
                if(mTwoPane)
                  mListener.selectMovieItem(strings[0]);


            }
        }

    }


}

public class MovieAdapter extends BaseAdapter
{
    List<Movie> mMovies;
    Context mContext;
    public MovieAdapter(Context c) {
        mContext=c;
        mMovies=new ArrayList<Movie>();

    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;

        if(view==null)
        {
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.grid_item,parent,false);
        }
        ImageView posterImage=(ImageView)view.findViewById(R.id.grid_item_movie);
        Picasso.with(mContext).load(mMovies.get(position).getPosterpath())
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error)
                .into(posterImage);


        return view;
    }
}
}
