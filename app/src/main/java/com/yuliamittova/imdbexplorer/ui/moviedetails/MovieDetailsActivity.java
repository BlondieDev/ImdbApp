package com.yuliamittova.imdbexplorer.ui.moviedetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yuliamittova.imdbexplorer.BuildConfig;
import com.yuliamittova.imdbexplorer.R;
import com.yuliamittova.imdbexplorer.dataobject.MovieDetails;

public class MovieDetailsActivity extends AppCompatActivity implements MovieDetailsPresenter.View {

    public static final String MOVIE_ID_KEY = "movie_id_key";

    private View mLoading;
    private View mErrorContainer;
    private TextView mErrorDescription;
    private View mMovieDetailsContainer;
    private ImageView mMovieIcon;
    private TextView mMovieDetailsTitle;
    private TextView mMovieDetailsBody;
    private TextView mMovieBudget;

    private MovieDetailsPresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Integer id = getIntent().getIntExtra(MOVIE_ID_KEY, -1);
        if (id < 0) {
            Toast.makeText(this, "No id passed to open a movie", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mLoading = findViewById(R.id.movie_details_loading);
        mErrorContainer = findViewById(R.id.movie_details_error_container);
        mErrorDescription = findViewById(R.id.movie_details_error_description);
        mMovieDetailsContainer = findViewById(R.id.movie_details_data);
        mMovieIcon = findViewById(R.id.movie_details_icon);
        mMovieDetailsTitle = findViewById(R.id.movie_details_title);
        mMovieDetailsBody = findViewById(R.id.movie_details_body);
        mMovieBudget = findViewById(R.id.movie_details_budget);

        mPresenter = new MovieDetailsPresenterImpl(this, id);
        mPresenter.onCreate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.GONE);
        mMovieDetailsContainer.setVisibility(View.GONE);
    }

    @Override
    public void showError(final @NonNull String errorText) {
        mLoading.setVisibility(View.GONE);
        mMovieDetailsContainer.setVisibility(View.GONE);

        mErrorContainer.setVisibility(View.VISIBLE);
        mErrorDescription.setText(errorText);
    }

    @Override
    public void showMovie(final @NonNull MovieDetails movieDetails) {
        Glide.with(this).load(BuildConfig.IMAGE_PATH + movieDetails.getPosterPath()).into(mMovieIcon);
        mMovieDetailsTitle.setText(movieDetails.getTitle());
        mMovieDetailsBody.setText(movieDetails.getOverview());
        mMovieBudget.setText(String.format(getString(R.string.movie_budget), movieDetails.getBudget()));

        mLoading.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.GONE);
        mMovieDetailsContainer.setVisibility(View.VISIBLE);
    }
}
