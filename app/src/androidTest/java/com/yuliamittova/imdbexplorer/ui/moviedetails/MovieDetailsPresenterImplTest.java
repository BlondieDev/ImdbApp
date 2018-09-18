package com.yuliamittova.imdbexplorer.ui.moviedetails;

import com.yuliamittova.imdbexplorer.dataobject.MovieDetails;
import com.yuliamittova.imdbexplorer.network.ApiService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MovieDetailsPresenterImplTest {

    @Mock
    private ApiService mApiService;
    @Mock
    private MovieDetailsPresenter.View mView;
    private TestScheduler mTestScheduler;

    @Before
    public void setUp() {
        mTestScheduler = new TestScheduler();
    }

    @Test
    public void testOnCreateWithNonEmptyResult() {
        // when
        final int movieId = 5;
        final MovieDetails movieDetails = generateMovieDetails();
        when(mApiService.getMovieDetails(eq(movieId), anyString())).thenReturn(Observable.just(movieDetails));
        final MovieDetailsPresenterImpl presenter = new MovieDetailsPresenterImpl(mApiService, mView, movieId, mTestScheduler, mTestScheduler);

        // then
        presenter.onCreate();
        mTestScheduler.triggerActions();

        // check
        verify(mView).showLoading();
        verify(mView).showMovie(movieDetails);
    }

    @Test
    public void testOnCreateWithEmptyServerResponse() {
        // when
        final int movieId = 5;
        final MovieDetails movieDetails = new MovieDetails();
        when(mApiService.getMovieDetails(eq(movieId), anyString())).thenReturn(Observable.just(movieDetails));
        final MovieDetailsPresenterImpl presenter = new MovieDetailsPresenterImpl(mApiService, mView, movieId, mTestScheduler, mTestScheduler);

        // then
        presenter.onCreate();
        mTestScheduler.triggerActions();

        // check
        verify(mView).showLoading();
        verify(mView, never()).showMovie(movieDetails);
        verify(mView).showError(anyString());
    }

    @Test
    public void testOnCreateWithErrorFromServer() {
        // when
        final Observable errorObservable = Observable.<MovieDetails>error(new IllegalStateException("State"));
        when(mApiService.getMovieDetails(anyInt(), anyString())).thenReturn(errorObservable);
        final MovieDetailsPresenterImpl presenter = new MovieDetailsPresenterImpl(mApiService, mView, 5, mTestScheduler, mTestScheduler);

        // then
        presenter.onCreate();
        mTestScheduler.triggerActions();

        // check
        verify(mView).showLoading();
        verify(mView, never()).showMovie(any(MovieDetails.class));
        verify(mView).showError(anyString());
    }

    private MovieDetails generateMovieDetails() {
        final MovieDetails movieDetails = new MovieDetails();
        movieDetails.setId(5);
        return movieDetails;
    }
}