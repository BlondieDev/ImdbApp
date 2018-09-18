package com.yuliamittova.imdbexplorer.ui.movielist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yuliamittova.imdbexplorer.BuildConfig;
import com.yuliamittova.imdbexplorer.R;
import com.yuliamittova.imdbexplorer.dataobject.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieItemViewHolder> {

    private Context mContext;
    private MovieSelectedListener mListener;
    private List<SearchResult> mMovieItems = new ArrayList<>();

    MovieListAdapter(final MovieSelectedListener listener, final Context context) {
        mListener = listener;
        mContext = context;
    }

    @Override
    @NonNull
    public MovieItemViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_in_list_item, parent, false);
        return new MovieItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final @NonNull MovieItemViewHolder holder, int position) {
        holder.bind(mMovieItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieItems.size();
    }

    public void updateList(final List<SearchResult> newItems) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MovieListDiffUtil(mMovieItems, newItems));
        diffResult.dispatchUpdatesTo(this);

        mMovieItems.clear();
        mMovieItems.addAll(newItems);
    }

    class MovieItemViewHolder extends RecyclerView.ViewHolder {
        private View mMainContainer;
        private ImageView mIcon;
        private TextView mTitle;
        private TextView mSubtitle;
        private TextView mDate;

        MovieItemViewHolder(View view) {
            super(view);

            mMainContainer = view.findViewById(R.id.movie_item_main_container);
            mIcon = view.findViewById(R.id.movie_item_icon);
            mTitle = view.findViewById(R.id.movie_item_title);
            mSubtitle = view.findViewById(R.id.movie_item_subtitle);
            mDate = view.findViewById(R.id.movie_item_date);
        }

        void bind(final SearchResult item) {
            mTitle.setText(item.getTitle());
            mSubtitle.setText(item.getOverview());
            mDate.setText(item.getReleaseDate());
            Glide.with(mContext).load(BuildConfig.IMAGE_PATH + item.getPosterPath()).into(mIcon);

            mMainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemSelected(item.getId());
                }
            });
        }
    }
}
