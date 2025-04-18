package com.driverspa.assist;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Locale;

import com.driverspa.BA;
import com.driverspa.model.api.request.WasherRequest;
import com.driverspa.model.api.response.BaseResponseHolder;
import com.driverspa.model.api.response.FareRequestResponseHolder;
import com.driverspa.model.api.response.FareRequestsResponseHolder;
import com.driverspa.model.api.response.NotificationResponseHolder;
import com.driverspa.model.api.response.WasherResponseHolder;
import com.driverspa.model.api.response.WasherTimeTableResponseHolder;
import com.driverspa.model.api.response.WashersResponseHolder;
import com.driverspa.util.RetrofitClient.WashmeApi;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.EditWasherRequestEvent;
import com.driverspa.util.otto.ws.EditWasherResponseEvent;
import com.driverspa.util.otto.ws.FareRequestAcceptFareEvent;
import com.driverspa.util.otto.ws.FareRequestAcceptFareResponseEvent;
import com.driverspa.util.otto.ws.FareRequestCancelEvent;
import com.driverspa.util.otto.ws.FareRequestCancelResponseEvent;
import com.driverspa.util.otto.ws.FareRequestEvent;
import com.driverspa.util.otto.ws.FareRequestIncreaseFareEvent;
import com.driverspa.util.otto.ws.FareRequestIncreaseFareResponseEvent;
import com.driverspa.util.otto.ws.FareRequestNotAcceptFareEvent;
import com.driverspa.util.otto.ws.FareRequestResponseEvent;
import com.driverspa.util.otto.ws.FareRequestsRequestEvent;
import com.driverspa.util.otto.ws.FareRequestsResponseEvent;
import com.driverspa.util.otto.ws.FavouriteWasherRequestEvent;
import com.driverspa.util.otto.ws.FavouriteWasherResponseEvent;
import com.driverspa.util.otto.ws.InitialWasherCreateRequestEvent;
import com.driverspa.util.otto.ws.InitialWasherCreateResponseEvent;
import com.driverspa.util.otto.ws.NotificationRequestEvent;
import com.driverspa.util.otto.ws.NotificationResponseEvent;
import com.driverspa.util.otto.ws.UnfavouriteWasherRequestEvent;
import com.driverspa.util.otto.ws.UnfavouriteWasherResponseEvent;
import com.driverspa.util.otto.ws.WasherAvailableRequestEvent;
import com.driverspa.util.otto.ws.WasherAvailableResponseEvent;
import com.driverspa.util.otto.ws.WasherCampaignsRequestEvent;
import com.driverspa.util.otto.ws.WasherCampaignsResponseEvent;
import com.driverspa.util.otto.ws.WasherFavouriteRequestEvent;
import com.driverspa.util.otto.ws.WasherFavouriteResponseEvent;
import com.driverspa.util.otto.ws.WasherInfoRequestEvent;
import com.driverspa.util.otto.ws.WasherInfoResponseEvent;
import com.driverspa.util.otto.ws.WasherLocalUpdateRequestEvent;
import com.driverspa.util.otto.ws.WasherPromoRequestEvent;
import com.driverspa.util.otto.ws.WasherPromoResponseEvent;
import com.driverspa.util.otto.ws.WasherTimeTableBookRequestEvent;
import com.driverspa.util.otto.ws.WasherTimeTableBookResponseEvent;
import com.driverspa.util.otto.ws.WasherTimeTableRequestEvent;
import com.driverspa.util.otto.ws.WasherTimeTableResponseEvent;
import com.driverspa.util.otto.ws.WashersNearMapRequestEvent;
import com.driverspa.util.otto.ws.WashersNearMapResponseEvent;
import com.driverspa.util.otto.ws.WashersNearRequestEvent;
import com.driverspa.util.otto.ws.WashersNearResponseEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WasherAssist extends BaseAssist {

	public WasherAssist(WashmeApi api, Bus eventBus) {
		super(api, eventBus);
	}

	@Subscribe
	public void onWashersFavouriteRequested(WasherFavouriteRequestEvent event) {
		getApi().washersSearchFavourite(
				event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new Callback<WashersResponseHolder>() {
					@Override
					public void success(WashersResponseHolder data, Response response) {
						if (isSuccess(data)) {
							logD("onWashersFavouriteRequested: success " + response.getUrl());
							BA.getEventBus().post(new WasherFavouriteResponseEvent(data.getResponse().getResult()));
						} else {
							BA.getEventBus().post(new WasherFavouriteResponseEvent(null));
							//logE("onWashersFavouriteRequested: error");
						}
					}

					@Override
					public void failure(RetrofitError error) {
						BA.getEventBus().post(new WasherFavouriteResponseEvent(null));
						onRetrofitError(error);
					}
				});
	}

	@Subscribe
	public void onWashersAvailableRequested(WasherAvailableRequestEvent event) {
		getApi().washersSearchAvailable(
				event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new Callback<WashersResponseHolder>() {
					@Override
					public void success(WashersResponseHolder data, Response response) {
						if (isSuccess(data)) {
							logD("onWashersAvailableRequested: success " + response.getUrl());
							BA.getEventBus().post(new WasherAvailableResponseEvent(data.getResponse().getResult()));
						} else {
							BA.getEventBus().post(new WasherAvailableResponseEvent(null));
							//logE("onWashersAvailableRequested: error");
						}
					}

					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new WasherAvailableResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onWashersPromoRequested(WasherPromoRequestEvent event) {
		getApi().washersSearchPromo(
				event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new Callback<WashersResponseHolder>() {
					@Override
					public void success(WashersResponseHolder data, Response response) {
						if (isSuccess(data)) {
							logD("onWashersPromoRequested: success " + response.getUrl());
							BA.getEventBus().post(new WasherPromoResponseEvent(data.getResponse().getResult()));
						} else {
							//logE("onWashersPromoRequested: error");
						}
					}

					@Override
					public void failure(RetrofitError error) {
						BA.getEventBus().post(new WasherPromoResponseEvent(null));
						onRetrofitError(error);
					}
				});
	}

	@Subscribe
	public void onWashersNearRequested(WashersNearRequestEvent event) {
		getApi().washersSearchNearBy(
				event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new retrofit.Callback<WashersResponseHolder>() {
					@Override
					public void success(WashersResponseHolder data, Response response) {
							BA.getEventBus().post(new WashersNearResponseEvent(data));
					}

					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new WashersNearResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onWashersNearMapRequested(WashersNearMapRequestEvent event) {
		  String location = String.format(Locale.US, "%f,%f", event.getLongitude(), event.getLatitide());
		getApi().washersOnMap(event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new retrofit.Callback<WashersResponseHolder>() {
					@Override
					public void success(WashersResponseHolder data, Response response) {
							BA.getEventBus().post(new WashersNearMapResponseEvent(data));
					}

					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new WashersNearMapResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onWasherCampaignsRequested(WasherCampaignsRequestEvent event) {
		getApi().washerCampaigns(
				event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new retrofit.Callback<WashersResponseHolder>() {
					@Override
					public void success(WashersResponseHolder data, Response response) {
						BA.getEventBus().post(new WasherCampaignsResponseEvent(data));
					}

					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new WasherCampaignsResponseEvent(null));
					}
				});
	}


	@Subscribe
	public void onWasherInfoRequested(WasherInfoRequestEvent event) {

		  getApi().washerInfo(event.getWasherId(),
				  new retrofit.Callback<WasherResponseHolder>() {
					  @Override
					  public void success(WasherResponseHolder data, Response response) {
						  if (isSuccess(data)) {
							  logD("onWasherFavouriteRequested: success " + response.getUrl());
							  BA.getEventBus().post(new WasherInfoResponseEvent(data.getResponse().getWasher()));
						  } else {
							  //logE("onWasherFavouriteRequested: error: " + data.getResponse().getMessage());
							  BA.getEventBus().post(new WasherInfoResponseEvent(null));
						  }
					  }

					  @Override
					  public void failure(RetrofitError error) {
						  onRetrofitError(error);
						  BA.getEventBus().post(new WasherInfoResponseEvent(null));
					  }
				  });
	}

	@Subscribe
	public void onWasherTimeTableRequested(WasherTimeTableRequestEvent event) {
		  getApi().washerTimeTable(event.getWasherId(),
				  event.getDay(),
				  new retrofit.Callback<WasherTimeTableResponseHolder>() {
					  @Override
					  public void success(WasherTimeTableResponseHolder data, Response response) {
						  if (isSuccess(data)) {
							  logD("onWasherTimeTableRequested: success " + response.getUrl());
							  BA.getEventBus().post(new WasherTimeTableResponseEvent(data.getResponse()));
						  } else {
							  BA.getEventBus().post(new WasherTimeTableResponseEvent(null));
						  }
					  }

					  @Override
					  public void failure(RetrofitError error) {
						  onRetrofitError(error);
						  BA.getEventBus().post(new WasherTimeTableResponseEvent(null));
					  }
				  });
	}

	@Subscribe
	public void onWasherTimeTableBookRequested(WasherTimeTableBookRequestEvent event) {

		  getApi().washerTimeTableWithBook(event.getWasherId(),
				  				   event.getDay(),
				new retrofit.Callback<WasherTimeTableResponseHolder>() {
			@Override
			public void success(WasherTimeTableResponseHolder data, Response response) {
				if ( isSuccess(data) ) {
					logD("onWasherTimeTableRequested: success " + response.getUrl());
					BA.getEventBus().post(new WasherTimeTableBookResponseEvent(data.getResponse()));
				} else {
					BA.getEventBus().post(new WasherTimeTableBookResponseEvent(null));
				}
			}

			@Override
			public void failure(RetrofitError error) {
				onRetrofitError(error);
				BA.getEventBus().post(new WasherTimeTableBookResponseEvent(null));
			}
		});
	}

	@Subscribe
	public void onWasherFavouriteRequested(FavouriteWasherRequestEvent event) {

		  getApi().addToFavourite(new WasherRequest(event.getWasherId()),
				new retrofit.Callback<WasherResponseHolder>() {
			@Override
			public void success(WasherResponseHolder data, Response response) {
				if ( isSuccess(data) ) {
					logD("onWasherFavouriteRequested: success " + response.getUrl());
					BA.getEventBus().post(new FavouriteWasherResponseEvent(true));
				} else {
					BA.getEventBus().post(new FavouriteWasherResponseEvent(false));
				}
			}

			@Override
			public void failure(RetrofitError error) {
				onRetrofitError(error);
				BA.getEventBus().post(new FavouriteWasherResponseEvent(false));
			}
		});
	}

	@Subscribe
	public void onWasherUnfavouriteRequested(UnfavouriteWasherRequestEvent event) {

		  getApi().removeFromFavourite(event.getWasherId(),
				  					   new retrofit.Callback<BaseResponseHolder>() {
			@Override
			public void success(BaseResponseHolder data, Response response) {
				if ( isSuccess(data) ) {
					logD("onWasherUnfavouriteRequested: success " + response.getUrl());
					BA.getEventBus().post(new UnfavouriteWasherResponseEvent(true));
				} else {
					BA.getEventBus().post(new UnfavouriteWasherResponseEvent(false));
				}
			}

			@Override
			public void failure(RetrofitError error) {
				onRetrofitError(error);
				BA.getEventBus().post(new UnfavouriteWasherResponseEvent(false));
			}
		});
	}

	@Subscribe
	public void onInitialWasherCreateRequested(InitialWasherCreateRequestEvent event) {
		getApi().createInitialWasher(
					 event.getRequest(),
						new Callback<WasherResponseHolder>() {

			@Override
			public void success(WasherResponseHolder data, Response response) {
				BA.getEventBus().post(new InitialWasherCreateResponseEvent(data));
			}

			@Override
			public void failure(RetrofitError error) {
				onRetrofitError(error);
				BA.getEventBus().post(new InitialWasherCreateResponseEvent(null));
			}
		});
	}

	@Subscribe
	public void onNotificaitonRequested(NotificationRequestEvent event) {
		getApi().notificationList(event.getSearchFilter() == null ? null : event.getSearchFilter().toMap(),
				new Callback<NotificationResponseHolder>() {
					@Override
					public void success(NotificationResponseHolder data, Response response) {
						BA.getEventBus().post(new NotificationResponseEvent(data));
					}
					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new NotificationResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onFareRequestEvent(FareRequestEvent event) {
		getApi().clientFareRequest(event.getFareRequest(),
				new Callback<FareRequestResponseHolder>() {
					@Override
					public void success(FareRequestResponseHolder data, Response response) {
						BA.getEventBus().post(new FareRequestResponseEvent(data));
					}
					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new FareRequestResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onFareRequestsEvent(FareRequestsRequestEvent event) {
		getApi().clientFareRequests(event.getStatus(),
				new Callback<FareRequestsResponseHolder>() {
					@Override
					public void success(FareRequestsResponseHolder data, Response response) {
						BA.getEventBus().post(new FareRequestsResponseEvent(data));
					}
					@Override
					public void failure(RetrofitError error) {
//						onRetrofitError(error);
						BA.getEventBus().post(new FareRequestsResponseEvent(null));
					}
				});
	}


	@Subscribe
	public void onFareRequestCancelEvent(FareRequestCancelEvent event) {
		getApi().fareRequestCancel(event.getFareRequest(),
				new Callback<FareRequestResponseHolder>() {
					@Override
					public void success(FareRequestResponseHolder data, Response response) {
						BA.getEventBus().post(new FareRequestCancelResponseEvent(data));
					}
					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new FareRequestCancelResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onFareRequestIncreaseEvent(FareRequestIncreaseFareEvent event) {
		getApi().fareRequestIncreaseFare(event.getFareRequest(),
				new Callback<FareRequestResponseHolder>() {
					@Override
					public void success(FareRequestResponseHolder data, Response response) {
						BA.getEventBus().post(new FareRequestIncreaseFareResponseEvent(data));
					}
					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new FareRequestIncreaseFareResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onFareRequestAcceptEvent(FareRequestAcceptFareEvent event) {
		getApi().fareRequestAcceptBidFare(event.getFareRequest(),
				new Callback<FareRequestResponseHolder>() {
					@Override
					public void success(FareRequestResponseHolder data, Response response) {
						BA.getEventBus().post(new FareRequestAcceptFareResponseEvent(data));
					}
					@Override
					public void failure(RetrofitError error) {
						onRetrofitError(error);
						BA.getEventBus().post(new FareRequestAcceptFareResponseEvent(null));
					}
				});
	}

	@Subscribe
	public void onFareRequestNotAcceptEvent(FareRequestNotAcceptFareEvent event) {
		getApi().fareRequestNotAcceptBidFare(event.getFareRequest(),
				new Callback<FareRequestResponseHolder>() {
					@Override
					public void success(FareRequestResponseHolder data, Response response) {

					}
					@Override
					public void failure(RetrofitError error) {
					}
				});
	}
}
