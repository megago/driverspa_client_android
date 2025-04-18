package com.driverspa.assist;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import com.driverspa.BA;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.model.api.response.BaseResponse;
import com.driverspa.model.api.response.BookInfoResponseHolder;
import com.driverspa.model.api.response.BookResponseHolder;
import com.driverspa.model.api.response.BooksResponseHolder;
import com.driverspa.model.api.response.ClientResponseHolder;
import com.driverspa.model.api.response.QueuedBooksResponseHolder;
import com.driverspa.util.RetrofitClient.WashmeApi;
import com.driverspa.util.otto.ws.BookCancelRequestEvent;
import com.driverspa.util.otto.ws.BookCancelResponseEvent;
import com.driverspa.util.otto.ws.BookInfoRequestEvent;
import com.driverspa.util.otto.ws.BookInfoResponseEvent;
import com.driverspa.util.otto.ws.BookRequestEvent;
import com.driverspa.util.otto.ws.BookResponseEvent;
import com.driverspa.util.otto.ws.BooksRequestEvent;
import com.driverspa.util.otto.ws.BooksResponseEvent;
import com.driverspa.util.otto.ws.ClientRequestEvent;
import com.driverspa.util.otto.ws.ClientResponseEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BookAssist extends BaseAssist {

	public BookAssist(WashmeApi api, Bus eventBus) {
		super(api, eventBus);
	}

	@Subscribe
	public void onBookRequested(BookRequestEvent event) {
		getApi().bookRequest(event.getBookingRequest(),
							new Callback<BookResponseHolder>() {
			@Override
			public void success(BookResponseHolder data, Response response) {
					BA.getEventBus().post(new BookResponseEvent(data));					
			}
			
			@Override
			public void failure(RetrofitError error) {				
				BA.getEventBus().post(new BookResponseEvent(null));
				onRetrofitError(error);
			}
		});
	}


	@Subscribe
	public void onBookInfoRequested(BookInfoRequestEvent event) {
		getApi().bookingInfo(event.getBookId(),
				new Callback<BookInfoResponseHolder>() {
			@Override
			public void success(BookInfoResponseHolder data, Response response) {
				if( isSuccess(data) ) {
					logD("onBookRequested: success " + response.getUrl());					
					BA.getEventBus().post(new BookInfoResponseEvent(data.getResponse()));					
				} else {
					BA.getEventBus().post(new BookInfoResponseEvent(null));
				}
			}
			
			@Override
			public void failure(RetrofitError error) {				
				BA.getEventBus().post(new BookInfoResponseEvent(null));
				onRetrofitError(error);
			}
		});
	}
	
	@Subscribe
	public void onBooksRequested(BooksRequestEvent event) {
		getApi().booksList(event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
						   new Callback<BooksResponseHolder>() {
			@Override
			public void success(BooksResponseHolder data, Response response) {
				BA.getEventBus().post(new BooksResponseEvent(data));
			}
			@Override
			public void failure(RetrofitError error) {				
//				onRetrofitError(error);
				BA.getEventBus().post(new BooksResponseEvent(null));
			}
		});
	}

	@Subscribe
	public void onBookCancelRequested(BookCancelRequestEvent event) {
		getApi().bookingCancel(new BookingRequest(event.getBookId()),
				               new Callback<BookResponseHolder>() {
			@Override
			public void success(BookResponseHolder data, Response response) {
				BA.getEventBus().post(new BookCancelResponseEvent(data));
			}
			
			@Override
			public void failure(RetrofitError error) {				
				onRetrofitError(error);
				BA.getEventBus().post(new BookCancelResponseEvent(null));
			}
		});
	}


	@Subscribe
	public void onAdminClientRequested(ClientRequestEvent event) {
		getApi().clientSingleClient(event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new Callback<ClientResponseHolder>() {
					@Override
					public void success(ClientResponseHolder data, Response response) {
						BA.getEventBus().post(new ClientResponseEvent(data));
					}
					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new ClientResponseEvent(null));
					}
				});
	}
	
}
