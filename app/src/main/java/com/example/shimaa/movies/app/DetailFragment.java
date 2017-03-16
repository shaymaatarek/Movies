package com.example.shimaa.movies.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Shimaa on 18/11/2016.
 */
public  class DetailFragment extends Fragment implements ClickInterface {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private  Movie mMovie;
    private String reviews;
    private TrailerAdapter trailerinfo;
    private  TextView review_text;
    private RecyclerView trailers;
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        String[] result=new String[2];
        Bundle sentBundle =  getArguments();
        if (sentBundle != null && !sentBundle.isEmpty())
        {

            mMovie=(Movie) sentBundle.getSerializable("EXTRA");
            LinearLayout f1=(LinearLayout)rootView.findViewById(R.id.second_part);
            ((TextView) f1.findViewById(R.id.over_view_detail))
                    .setText(mMovie.getOverview());
            LinearLayout l1=(LinearLayout)rootView.findViewById(R.id.first_part);
            LinearLayout l1_2=(LinearLayout)l1.findViewById(R.id.first_part_2);
            LinearLayout l1_3=(LinearLayout)l1_2.findViewById(R.id.first_part_3);
            LinearLayout l1_4=(LinearLayout)l1_3.findViewById(R.id.first_part_4);

            ((TextView) l1_4.findViewById(R.id.title_detail))
                    .setText(mMovie.getOriginaltitle());
            ((TextView) l1_4.findViewById(R.id.releaseddate_detail))
                    .setText(mMovie.getReleasedate());
            ((TextView) l1_4.findViewById(R.id.voterate_detail))
                    .setText(String.valueOf( mMovie.getVoterate()));
            ImageView poster=(ImageView)l1.findViewById(R.id.poster_detail);
            Picasso.with(getActivity()).load(mMovie.getPosterpath())
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(poster);

            MoviesDetailTask mMoviesDetailTask=new MoviesDetailTask();
            mMoviesDetailTask.execute(mMovie.getmId());
            LinearLayout l3=(LinearLayout)rootView.findViewById(R.id.third_part);

            LinearLayout l4=(LinearLayout)rootView.findViewById(R.id.trailers);
            review_text= ((TextView) l3.findViewById(R.id.review_detail));
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

            trailers=(RecyclerView)l4.findViewById(R.id.trailer_list);
            trailers.setLayoutManager(mLayoutManager);
            trailers.setItemAnimator(new DefaultItemAnimator());

            ImageView btn1=(ImageView)l1.findViewById(R.id.fav);
            btn1.setOnClickListener(new View.OnClickListener() {
                    String ot = mMovie.getOriginaltitle();
                    String pp = mMovie.getPosterpath();
                    String ov = mMovie.getOverview();
                    double vr = mMovie.getVoterate();
                    String rd = mMovie.getReleasedate();
                    String rev= mMovie.getReviews();
                    int i     = mMovie.getmId();
                    @Override
                    public void onClick(View v) {
                        if(mMovie.isFav())
                        {

                            new Delete().from(Movie.class).where("movieid = ?", i).execute();
                            Movie m=new Movie( ot, pp, ov, vr, rd,i,false,rev);
                            m.save();

                            boolean exists =
                                    new Select()
                                            .from(Movie.class)
                                            .where("favourite = ?",false)
                                            .exists();
                            if (exists) {
                                Toast toast=Toast.makeText(getActivity(),"REMOVED FROM FAVOURITE",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }else{
                        new Delete().from(Movie.class).where("movieid = ?", i).execute();
                        Movie m=new Movie( ot, pp, ov, vr, rd,i,true,rev);
                        m.save();
                        boolean exists =
                                new Select()
                                        .from(Movie.class)
                                        .where("favourite = ?",true)
                                        .exists();
                        if (exists) {
                            Toast toast=Toast.makeText(getActivity(),"ADDED TO FAVOURITE",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        }

                    }
                });

        }
        return rootView;
    }

    @Override
    public void userItemClick(String id) {
         Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(webIntent);

    }

    public  class MoviesDetailTask extends AsyncTask<Integer, Void, String[]>
    {
        private final String LOG_TAG = MoviesDetailTask.class.getSimpleName();

        public String[] getMovieDataFromJson(String s) throws JSONException {

            final String TAG_REVIEWS="reviews";
            final String TAG_RESULTS = "results";
            final String TAG_AUTHOR = "author";
            final String TAG_CONTENT = "content";
            final String TAG_VIDEO = "videos";
            final String TAG_KEY="key";
            JSONObject moviesJson = new JSONObject(s);
            JSONObject reviewsobject = moviesJson.getJSONObject(TAG_REVIEWS);
            JSONArray resultsArray = reviewsobject.getJSONArray(TAG_RESULTS);
            String[] reviews=new String[2];

            if(resultsArray.length()>0) {
                String[] authors = new String[resultsArray.length()];
                String[] contents = new String[resultsArray.length()];
                reviews[0]="";
                for (int i = 0; i < resultsArray.length(); i++) {
                    String author;
                    String content;
                    JSONObject movieInfo = resultsArray.getJSONObject(i);
                    author = movieInfo.getString(TAG_AUTHOR);
                    content = movieInfo.getString(TAG_CONTENT);
                    authors[i] = author;
                    contents[i] = content;
                    reviews[0]+="A review by "+author+"\n"+content+"\n";
                }}
            if(reviews[0]==""||reviews[0]==null)
                reviews[0]="We don't have any reviews ";

            JSONObject videos_object = moviesJson.getJSONObject(TAG_VIDEO);
            JSONArray videos_Array = videos_object.getJSONArray(TAG_RESULTS);
            reviews[1]="";
            if(videos_Array.length()>0) {
               for (int i = 0; i < videos_Array.length(); i++) {
                    JSONObject videoInfo = videos_Array.getJSONObject(i);
                    String moviekey = videoInfo.getString(TAG_KEY);
                    reviews[1]+=moviekey+",";
                }}

            return reviews;
}

        @Override
        protected String[] doInBackground(Integer... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;
            try {

                final String TMDB_BASE_URL_ = "http://api.themoviedb.org/3/movie/"+params[0].toString()+"?";
                final String APPID_PARAM = "api_key";
                final String appid="b61faf5a9ec24cba5189261629354988";
                Uri builtUri = Uri.parse(TMDB_BASE_URL_).buildUpon()
                        .appendQueryParameter(APPID_PARAM,appid)
                        .build();




                URL url = new URL(builtUri.toString()+"&append_to_response=videos,reviews");
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
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
                movieJsonStr = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            }
            finally {
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
            return  getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] review)
        {
            super.onPostExecute(review);
            reviews=review[0];
            review_text.setText(reviews);
            final String[] trailers_array = review[1].split(",");
            trailerinfo=new TrailerAdapter(getActivity(),trailers_array.length,trailers_array);
            trailers.setAdapter(trailerinfo);
            trailerinfo.notifyDataSetChanged();

        }
    }


    public class TrailerAdapter extends RecyclerView.Adapter
    {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView trailer;

            public MyViewHolder(View view,final ClickInterface clickInterface) {
                super(view);
                trailer=(ImageView)view.findViewById(R.id.list_item_trailer);
                trailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickInterface.userItemClick(trailers[getPosition()]);

                    }
                });

            }
        }
        int count;
        String[] trailers;
        Context mContext;
        public TrailerAdapter(Context c,int count,String[] list) {
            mContext=c;
            this.count=count;
            trailers=list;
        }



        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_item, viewGroup, false);
            ClickInterface ck= DetailFragment.this;
            return new MyViewHolder(itemView,ck);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return count;
        }



    }
}
