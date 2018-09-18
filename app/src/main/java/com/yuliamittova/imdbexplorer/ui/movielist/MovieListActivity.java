package com.yuliamittova.imdbexplorer.ui.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.yuliamittova.imdbexplorer.R;
import com.yuliamittova.imdbexplorer.dataobject.SearchResult;
import com.yuliamittova.imdbexplorer.ui.moviedetails.MovieDetailsActivity;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MovieListActivity extends AppCompatActivity implements MovieListPresenter.View {

    private RecyclerView mMovieList;
    private View mLoading;
    private View mErrorContainer;
    private TextView mErrorDescription;
    private FloatingActionButton mFilterButton;

    private MovieListPresenterImpl mPresenter;
    private MovieListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMovieList = findViewById(R.id.movie_list_data);
        mLoading = findViewById(R.id.movie_list_loading);
        mErrorContainer = findViewById(R.id.movie_list_error_container);
        mErrorDescription = findViewById(R.id.movie_list_error_description);
        mFilterButton = findViewById(R.id.movie_list_filter);

        mPresenter = new MovieListPresenterImpl(this);
        mAdapter = new MovieListAdapter(mPresenter, this);
        mMovieList.setLayoutManager(new LinearLayoutManager(this));
        mMovieList.setAdapter(mAdapter);

        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onFilterApplied();
            }
        });

        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showResults(@NonNull List<SearchResult> results) {
        mErrorContainer.setVisibility(GONE);
        mLoading.setVisibility(GONE);
        mMovieList.setVisibility(VISIBLE);

        mAdapter.updateList(results);
    }

    @Override
    public void showError(final @NonNull String errorMessage) {
        mMovieList.setVisibility(GONE);
        mLoading.setVisibility(GONE);

        mErrorDescription.setText(errorMessage);
        mErrorContainer.setVisibility(VISIBLE);
    }

    @Override
    public void showLoading() {
        mErrorContainer.setVisibility(GONE);
        mMovieList.setVisibility(GONE);
        mLoading.setVisibility(VISIBLE);
    }

    @Override
    public void openMovieDetails(final @NonNull Integer movieId) {
        final Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE_ID_KEY, movieId);
        startActivity(intent);
    }

    @Override
    public void showFilterState(boolean isASC) {
        mFilterButton.setImageResource(isASC ? R.drawable.ic_filter_off : R.drawable.ic_filter_on);
    }
}
