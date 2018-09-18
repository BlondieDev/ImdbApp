package com.yuliamittova.imdbexplorer.ui.movielist;

import android.support.annotation.NonNull;

import com.yuliamittova.imdbexplorer.dataobject.SearchResult;
import com.yuliamittova.imdbexplorer.dataobject.SearchResultList;
import com.yuliamittova.imdbexplorer.network.ApiService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MovieListPresenterImplTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private ApiService mApiService;
    @Mock
    private MovieListPresenter.View mView;
    private TestScheduler mTestScheduler;

    @Before
    public void setUp() {
        mTestScheduler = new TestScheduler();
    }

    @Test
    public void testOnCreateWithNonEmptyResults() {
        // when
        final SearchResultList searchResultList = generateSearchResultList(1);
        when(mApiService.getMovieList(anyString())).thenReturn(Observable.just(searchResultList));
        final MovieListPresenterImpl presenter = new MovieListPresenterImpl(mApiService, mView, mTestScheduler, mTestScheduler);

        // then
        presenter.onCreate();
        mTestScheduler.triggerActions();

        // check
        verify(mView).showLoading();
        verify(mView).showResults(eq(searchResultList.getResults()));
        verify(mView, never()).showError(anyString());
    }

    @Test
    public void testOnCreateWithZeroResults() {
        // when
        final SearchResultList searchResultList = generateSearchResultList(0);
        when(mApiService.getMovieList(anyString())).thenReturn(Observable.just(searchResultList));
        final MovieListPresenterImpl presenter = new MovieListPresenterImpl(mApiService, mView, mTestScheduler, mTestScheduler);

        // then
        presenter.onCreate();
        mTestScheduler.triggerActions();

        // check
        verify(mView).showLoading();
        verify(mView, never()).showResults(eq(searchResultList.getResults()));
        verify(mView).showError(anyString());
    }

    @Test
    public void testOnCreateWithErrorFromServer() {
        // when
        final Observable errorObservable = Observable.<SearchResultList>error(new IllegalStateException("State"));
        when(mApiService.getMovieList(anyString())).thenReturn(errorObservable);
        final MovieListPresenterImpl presenter = new MovieListPresenterImpl(mApiService, mView, mTestScheduler, mTestScheduler);

        // then
        presenter.onCreate();
        mTestScheduler.triggerActions();

        // check
        verify(mView).showLoading();
        verify(mView, never()).showResults(ArgumentMatchers.<SearchResult>anyList());
        verify(mView).showError(anyString());
    }

    @Test
    public void testMovieSelected() {
        // when
        final int movieId = 5;
        final MovieListPresenterImpl presenter = new MovieListPresenterImpl(mApiService, mView, mTestScheduler, mTestScheduler);

        // then
        presenter.onItemSelected(movieId);

        // check
        verify(mView).openMovieDetails(movieId);
    }

    @Test
    public void testFilterAppliedOnEmptyList() {
        // when
        final MovieListPresenterImpl presenter = new MovieListPresenterImpl(mApiService, mView, mTestScheduler, mTestScheduler);

        // then
        presenter.onFilterApplied();

        // check
        verify(mView, never()).showResults(ArgumentMatchers.<SearchResult>anyList());
        verify(mView, never()).showFilterState(anyBoolean());
    }

    @Test
    public void testFilterAppliedOnNonEmptyList() {
        // when
        final SearchResultList searchResultList = generateSearchResultList(3);
        when(mApiService.getMovieList(anyString())).thenReturn(Observable.just(searchResultList));
        final MovieListPresenterImpl presenter = new MovieListPresenterImpl(mApiService, mView, mTestScheduler, mTestScheduler);

        // then
        presenter.onCreate();
        mTestScheduler.triggerActions();
        presenter.onFilterApplied();

        // check
        mView.showResults(ArgumentMatchers.<SearchResult>anyList());
        mView.showFilterState(true);
    }

    private SearchResultList generateSearchResultList(final int size) {
        final SearchResultList resultList = new SearchResultList();
        resultList.setResults(new ArrayList<SearchResult>());

        for (int i = 0; i < size; i++) {
            resultList.getResults().add(generateSearchResult(i));
        }
        return resultList;
    }

    private SearchResult generateSearchResult(final int itemNumber) {
        final SearchResult searchResult = new SearchResult();
        searchResult.setId(itemNumber);
        searchResult.setTitle("title " + itemNumber);
        searchResult.setReleaseDate(convertDateToString(new Date()));
        return searchResult;
    }

    private String convertDateToString(final @NonNull Date date) {
        return DATE_FORMAT.format(date);
    }
}