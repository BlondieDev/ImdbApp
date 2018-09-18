package com.yuliamittova.imdbexplorer.ui.movielist;

import android.support.annotation.NonNull;

import com.yuliamittova.imdbexplorer.dataobject.SearchResult;

import java.util.List;

public interface MovieListPresenter {

    void onCreate();

    void onDestroy();

    void onFilterApplied();

    interface View {

        void showResults(final @NonNull List<SearchResult> results);

        void showError(final @NonNull String errorMessage);

        void showLoading();

        void openMovieDetails(final @NonNull Integer movieId);

        void showFilterState(final boolean isASC);
    }
}
