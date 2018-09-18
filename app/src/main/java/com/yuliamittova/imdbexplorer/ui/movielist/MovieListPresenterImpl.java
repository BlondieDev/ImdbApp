package com.yuliamittova.imdbexplorer.ui.movielist;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.yuliamittova.imdbexplorer.BuildConfig;
import com.yuliamittova.imdbexplorer.dataobject.SearchResult;
import com.yuliamittova.imdbexplorer.dataobject.SearchResultList;
import com.yuliamittova.imdbexplorer.network.ApiService;
import com.yuliamittova.imdbexplorer.network.RetroClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieListPresenterImpl implements MovieListPresenter, MovieSelectedListener {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final MovieListPresenter.View mView;
    private final ApiService mApiService;
    private Disposable mDisposable;
    private Scheduler mProcessScheduler;
    private Scheduler mAndroidScheduler;

    private List<SearchResult> mSearchResults = new ArrayList<>();
    private boolean mIsFilterAsc = true;

    MovieListPresenterImpl(final MovieListPresenter.View view) {
        mView = view;
        mApiService = RetroClient.getApiService();
        mProcessScheduler = Schedulers.io();
        mAndroidScheduler = AndroidSchedulers.mainThread();
    }

    @VisibleForTesting
    MovieListPresenterImpl(final @NonNull ApiService apiService,
                           final @NonNull MovieListPresenter.View view,
                           final Scheduler processScheduler,
                           final Scheduler androidScheduler) {
        mApiService = apiService;
        mView = view;
        mProcessScheduler = processScheduler;
        mAndroidScheduler = androidScheduler;
    }

    @Override
    public void onCreate() {
        mView.showLoading();

        mApiService.getMovieList(BuildConfig.API_KEY)
                .subscribeOn(mProcessScheduler)
                .observeOn(mAndroidScheduler)
                .subscribe(new Observer<SearchResultList>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(SearchResultList searchResults) {
                        if (mView == null) {
                            return;
                        }
                        mSearchResults.clear();
                        mSearchResults.addAll(searchResults.getResults());

                        if (mSearchResults.isEmpty()) {
                            mView.showError("No results found");
                        } else {
                            mView.showResults(mSearchResults);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        mView.showError(t.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onFilterApplied() {
        if (mSearchResults == null || mSearchResults.isEmpty()) {
            Log.e("TAG", "No results to filter");
            return;
        }

        mIsFilterAsc = !mIsFilterAsc;
        Collections.sort(mSearchResults, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult one, SearchResult two) {
                final Date dateOne = getDateFromString(one.getReleaseDate());
                final Date dateTwo = getDateFromString(two.getReleaseDate());
                int comparisonResult;

                if (dateOne == null) {
                    comparisonResult = dateTwo == null ? 0 : 1;
                } else if (dateTwo == null) {
                    comparisonResult = -1;
                } else {
                    comparisonResult = dateOne.compareTo(dateTwo);
                }

                return mIsFilterAsc ? comparisonResult : (-1) * comparisonResult;
            }
        });
        mView.showResults(mSearchResults);
        mView.showFilterState(mIsFilterAsc);
    }

    private Date getDateFromString(final String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void onItemSelected(final @NonNull Integer movieId) {
        mView.openMovieDetails(movieId);
    }
}
