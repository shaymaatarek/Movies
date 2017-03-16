package com.example.shimaa.movies.app;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Shimaa on 21/10/2016.
 */
@Table(name = Movie.TABLE_NAME, id = "ID")
public class Movie extends Model implements Serializable {

    public static final String TABLE_NAME = "Movie";
    private static final String COLUMN_ORIGINAL_TITLE = "originaltitle";
    private static final String COLUMN_POSTER_PATH = "posterpath";
    private static final String COLUMN_OVERVIEW = "overview";
    private static final String COLUMN_VOTE_RATE = "voterate";
    private static final String COLUMN_RELEASE_DATE = "releasedate";
    private static final String COLUMN_REVIEWS = "reviews";
    private static final String COLUMN_MOVIE_ID = "movieid";
    private static final String COLUMN_FAV = "favourite";



    @Column(name = COLUMN_ORIGINAL_TITLE)
    private     String originaltitle;
    @Column(name = COLUMN_POSTER_PATH)
    private     String posterpath;
    @Column(name = COLUMN_OVERVIEW)
    private     String overview;
    @Column(name = COLUMN_VOTE_RATE)
    private     double voterate;
    @Column(name = COLUMN_RELEASE_DATE)
    private     String releasedate;
    @Column(name = COLUMN_REVIEWS)
    private     String  reviews;

    public boolean isFav() {
        return fav;
    }

    @Column(name = COLUMN_MOVIE_ID)
    private     int  mId;
    @Column(name = COLUMN_FAV)
    private     boolean fav;
    public  Movie(){super(); this.fav=false;    }
    public Movie(String originaltitle, String posterpath, String overview, double voterate, String releasedate,int i,boolean fav,String rev) {
        super();
        this.originaltitle = originaltitle;
        this.posterpath = posterpath;
        this.overview = overview;
        this.voterate = voterate;
        this.releasedate = releasedate;
        this.fav=fav;
        this.mId=i;
        this.reviews=rev;
    }

    public String getOriginaltitle() {
        return originaltitle;
    }

    public void setOriginaltitle(String originaltitle) {
        this.originaltitle = originaltitle;
    }

    public String getPosterpath() {
        return posterpath;
    }

    public void setPosterpath(String posterpath) {
        this.posterpath = "http://image.tmdb.org/t/p/w185"+posterpath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoterate() {
        return voterate;
    }

    public void setVoterate(double voterate) {
        this.voterate = voterate;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }
}
