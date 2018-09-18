package com.yuliamittova.imdbexplorer.ui.moviedetails;

import android.support.annotation.NonNull;

import com.yuliamittova.imdbexplorer.dataobject.MovieDetails;

public interface MovieDetailsPresenter {

    interface View {

        void showLoading();

        void showError(final @NonNull String errorText);

        void showMovie(final @NonNull MovieDetails movieDetails);
    }
}
