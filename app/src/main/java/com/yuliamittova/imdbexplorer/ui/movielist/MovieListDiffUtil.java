package com.yuliamittova.imdbexplorer.ui.movielist;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.yuliamittova.imdbexplorer.dataobject.SearchResult;

import java.util.List;

public class MovieListDiffUtil extends DiffUtil.Callback {

    private List<SearchResult> mOldList;
    private List<SearchResult> mNewList;

    MovieListDiffUtil(final @NonNull List<SearchResult> oldList,
                      final @NonNull List<SearchResult> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.size() < oldItemPosition
                && mNewList.size() < newItemPosition
                && (mOldList.get(oldItemPosition).getId().equals(mNewList.get(newItemPosition).getId()));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final SearchResult oldItem = mOldList.get(oldItemPosition);
        final SearchResult newItem = mNewList.get(newItemPosition);

        // comparing only visible fields
        return oldItem.getTitle().equals(newItem.getTitle())
                && oldItem.getOverview().equals(newItem.getOverview())
                && oldItem.getReleaseDate().equals(newItem.getReleaseDate())
                && oldItem.getPosterPath().equals(newItem.getPosterPath());
    }
}
