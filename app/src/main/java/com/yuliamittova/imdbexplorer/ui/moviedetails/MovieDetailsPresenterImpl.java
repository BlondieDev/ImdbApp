package com.yuliamittova.imdbexplorer.ui.moviedetails;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.yuliamittova.imdbexplorer.BuildConfig;
import com.yuliamittova.imdbexplorer.dataobject.MovieDetails;
import com.yuliamittova.imdbexplorer.network.ApiService;
import com.yuliamittova.imdbexplorer.network.RetroClient;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDetailsPresenterImpl implements MovieDetailsPresenter {

    private final View mView;
    private final ApiService mApiService;
    private Disposable mDisposable;
    private Scheduler mProcessScheduler;
    private Scheduler mAndroidScheduler;
    private final int mMovieId;

    MovieDetailsPresenterImpl(final View view, final int movieId) {
        mView = view;
        mMovieId = movieId;
        mApiService = RetroClient.getApiService();
        mProcessScheduler = Schedulers.io();
        mAndroidScheduler = AndroidSchedulers.mainThread();
    }

    @VisibleForTesting
    MovieDetailsPresenterImpl(final @NonNull ApiService apiService,
                              final View view,
                              final int movieId,
                              final Scheduler processScheduler,
                              final Scheduler androidScheduler) {
        mApiService = apiService;
        mView = view;
        mProcessScheduler = processScheduler;
        mAndroidScheduler = androidScheduler;
        mMovieId = movieId;
    }

    public void onCreate() {
        mView.showLoading();

        mApiService.getMovieDetails(mMovieId, BuildConfig.API_KEY)
                .subscribeOn(mProcessScheduler)
                .observeOn(mAndroidScheduler)
                .subscribe(new Observer<MovieDetails>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(final @Nullable MovieDetails movieDetails) {
                        if (movieDetails == null || movieDetails.getId() == null) {
                            mView.showError("No movie with id " + mMovieId + " found");
                        } else {
                            mView.showMovie(movieDetails);
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

    public void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
