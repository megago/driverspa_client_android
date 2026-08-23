package com.driverspa.client.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import android.widget.LinearLayout.LayoutParams;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.assist.BaseAssist;
import com.driverspa.client.activity.ClientActiveBookingInfoActivity;
import com.driverspa.model.AdminSearchFilter;
import com.driverspa.model.BookInfo;
import com.driverspa.model.CarItem;
import com.driverspa.model.ClientInfo;
import com.driverspa.model.Image;
import com.driverspa.model.ImageDetail;
import com.driverspa.model.PhotoParcelable;
import com.driverspa.model.ReversePrices;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.ServiceItem;
import com.driverspa.model.User;
import com.driverspa.model.WantedWashers;
import com.driverspa.model.Washer;
import com.driverspa.model.Washer.Contact;
import com.driverspa.model.WasherPublic;
import com.driverspa.model.api.request.AddReviewRequest;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.util.Constants;
import com.driverspa.util.Functions;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.HttpClient;
import com.driverspa.util.RetrofitClient;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.TransformationCircle;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ServiceSelectionEvent;
import com.driverspa.util.otto.TimeTableSelectionEvent;
import com.driverspa.util.otto.WantedWashersResponseEvent;
import com.driverspa.util.otto.ws.AddReviewRequestEvent;
import com.driverspa.util.otto.ws.BookRequestEvent;
import com.driverspa.util.otto.ws.BookResponseEvent;
import com.driverspa.util.otto.ws.ClientRequestEvent;
import com.driverspa.util.otto.ws.ClientResponseEvent;
import com.driverspa.util.otto.ws.FavouriteWasherRequestEvent;
import com.driverspa.util.otto.ws.FavouriteWasherResponseEvent;
import com.driverspa.util.otto.ws.UnfavouriteWasherRequestEvent;
import com.driverspa.util.otto.ws.UnfavouriteWasherResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;
import com.driverspa.util.otto.ws.WasherFavouriteRequestEvent;
import com.driverspa.util.otto.ws.WasherFavouriteResponseEvent;
import com.driverspa.util.otto.ws.WasherInfoRequestEvent;
import com.driverspa.util.otto.ws.WasherInfoResponseEvent;
import com.driverspa.view.NDSpinner;

import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;
import static com.driverspa.util.Constants.URL_PREFIX;


public class ClientWasherInfoFragment extends ClientBaseFragment {

    public static final String EXTRA_WASHER_ID = "washer_id";
    public static final int ZOOM_LEVEL = 14;
	DecimalFormat formatter = new DecimalFormat("#,###.##");

	public interface ActivityActions{
		public void openCampaignInfo(String washerId,String washer);
		public void home();
		public void showProgressBar(boolean set);
		public void startBooking(Washer washer);
		public void showReview(Washer washer);
		public void showTimeTable(Washer washer);
		public void showPrices(Washer washer);
		public void showMapMarker(Washer washer);
		public void showWasherImages(ArrayList<PhotoParcelable> washerPhotos);
		public void hideShowMenuBookButton(boolean show);
		public void login();
		public void addReview(Washer washer);
		public void writeToWashme(String email, String title);
	}

	private GoogleMap mMap;
	private Washer washer;

	@BindView(R.id.txtWasherNameTitle)
	TextView washerTitleName;
	@BindView(R.id.txtWasherName)
	TextView washerName;
	@BindView(R.id.txtWasherEmail)
	TextView washerEmail;
	@BindView(R.id.txtWasherWeb)
	TextView washerWeb;
	@BindView(R.id.txtWasherPhone)
    TextView washerPhone;
	@BindView(R.id.txtWasherAddress)
    TextView washerAddress;
	@BindView(R.id.txtWasherTime)
    TextView washerTime;
	@BindView(R.id.txtWasherWifi)
    TextView washerWifi;
	@BindView(R.id.txtWasherKafe)
    TextView washerKafe;
	@BindView(R.id.layoutWifi)
	View layoutWifi;
	@BindView(R.id.layoutKafe)
	View layoutKafe;
	@BindView(R.id.layoutTime)
	View layoutTime;
	@BindView(R.id.layoutWeb)
	View layoutWeb;
	@BindView(R.id.layoutEmail)
	View layoutEmail;
	@BindView(R.id.btnBookStart2)
	View buttonBookStart2;
	@BindView(R.id.btnBookStart)
	View buttonBookStart;
	@BindView(R.id.imgFavourite)
	ImageView favourite;
	@BindView(R.id.imgWasherAvatar)
	ImageView washerImage;
	@BindView(R.id.txtWasherReview)
	TextView washerReview;
	@BindView(R.id.txtWasherFavourite)
	TextView washerFavourite;
	@BindView(R.id.images_count)
	TextView imagesCount;
	@BindView(R.id.txtExtraServices)
	TextView txtExtraServices;
	@BindView(R.id.addInfoView)
	LinearLayout addInfoView;

	@BindView(R.id.images_count_rect)
	View imagesCountRect;
	@BindView(R.id.images_circle)
	View imagesCircle;
	String washerId;
    @BindView(R.id.btnCall)
	View btnCall;

	@BindView(R.id.countsLayout)
	View countsLayout;
	@BindView(R.id.countsDateLayout)
	View countsDateLayout;

	@BindView(R.id.txtFinishedBooksDate)
	TextView finishedBooksDate;
	@BindView(R.id.txtFinishedBooksCount)
	TextView finishedBooksCount;
	ClientInfo client;

	@BindView(R.id.discount)
	TextView discount;
	@BindView(R.id.discountDetail)
	TextView discountDetail;
	@BindView(R.id.discountLayout)
	View discountLayout;

	@BindView(R.id.txtDeposit)
	TextView depositText;
	@BindView(R.id.txtBonus)
	TextView bonusText;
	@BindView(R.id.txtDiscount)
	TextView discountClientText;
	@BindView(R.id.clientLayout)
	View clientLayout;


	@BindView(R.id.btnExpand)
	ImageView buttonExpand;
	@BindView(R.id.carSpinner)
	NDSpinner carSpinner;
	@BindView(R.id.serviceSpinner)
	NDSpinner serviceSpinner;
	@BindView(R.id.myCarText)
	TextView myCarText;
	@BindView(R.id.services)
	TextView services;
	@BindView(R.id.bookTime)
	TextView bookTime;
	@BindView(R.id.bookTimeLayout)
	View bookTimeLayout;
	@BindView(R.id.bookService)
	View bookService;
	@BindView(R.id.bookServiceClose)
	View bookServiceClose;
	@BindView(R.id.bookTimeClose)
	View bookTimeClose;
	@BindView(R.id.bookServiceRightArrow)
	View bookServiceRightArrow;
	@BindView(R.id.bookTimeRightArrow)
	View bookTimeRightArrow;

	@BindView(R.id.btnMain)
	Button mainButton;
	@BindView(R.id.mainLayout)
	View mainLayout;
	@BindView(R.id.mainFieldsLayout)
	View mainFieldsLayout;
	@BindView(R.id.informationLayout)
	View informationLayout;
	@BindView(R.id.booksFieldLayout)
	View booksFieldLayout;
	@BindView(R.id.btnRequireWashme)
	Button requireWashmeButton;
	@BindView(R.id.reqireWashmeText)
	TextView reqireWashmeText;
	HashMap<String, String> wantedWashers;


	GPSTracker gps;
	TextView titleView;
	private ArrayList<PhotoParcelable> washerPhotos;
	boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
	Toolbar mToolbar;
	private ActivityActions activityActions;
	private boolean washerUpdate = true;
	boolean shown = false;
	ClientServiceDialogFragment serviceDialogFragment;
	ClientTimetableDialogFragment timetableDialogFragment;

	private boolean isSpinnerTouched = false;
	public static final int UP = 0;
	public static final int DOWN = 1;
	int expandButtonPosition = UP;
	BookingRequest request;

	@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_client_washer_info, container,false);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
		}

	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	        ButterKnife.bind(this, view);
			gps = new GPSTracker(getActivity());
			mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
	        Intent intent = getActivity().getIntent();
	        washerId = intent.getStringExtra(EXTRA_WASHER_ID);
			String washerInfo = intent.getStringExtra(Constants.PUSH_DATA);
			if(!TextUtils.isEmpty(washerInfo) && !shown){
				AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
				dialog.setMessage(washerInfo);
				dialog.setPositiveButton(BA.str(R.string.ok_word),null);
				dialog.show();
				shown = true;
			}
//	        buttonBookStart.setEnabled(false);
	        buttonBookStart2.setVisibility(View.GONE);
			buttonBookStart.setVisibility(View.GONE);
	        setUpMapIfNeeded();
	        activityActions.showProgressBar(true);

			mainFieldsLayout.setVisibility(View.GONE);
			informationLayout.setVisibility(View.VISIBLE);
			booksFieldLayout.setVisibility(View.GONE);
			mainButton.setText(BA.str(R.string.route_build));
		}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				activityActions.home();
				break;
			case R.id.menu_add:
				activityActions.startBooking(washer);
				break;
		}
		return true;
	}

		/***** Sets up the map if it is possible to do so *****/
		public void setUpMapIfNeeded() {
		    // Do a null check to confirm that we have not already instantiated the map.
		    if (mMap == null) {
		        // Try to obtain the map from the SupportMapFragment.
				((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
					@Override
					public void onMapReady(GoogleMap googleMap) {
						mMap = googleMap;
						setUpMap();
					}
				});
		    }
		}

	    private void setUpMap() {
	        UiSettings ui = mMap.getUiSettings();
	        ui.setCompassEnabled(false);
	        ui.setZoomControlsEnabled(false);
	        ui.setMyLocationButtonEnabled(false);
	        ui.setAllGesturesEnabled(false);
//            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
            mMap.setMyLocationEnabled(false);
	        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
	            public void onMapClick(LatLng latLng) {
	              activityActions.showMapMarker(washer);
	            }
	        });

	        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
	            public boolean onMarkerClick(Marker marker) {
	            	activityActions.showMapMarker(washer);
	                return true;
	            }
	        });
	    }

		/**
		 * Received washer information
		 * @param event
		 */
		@Subscribe
		public void onWasherInfoReceived(WasherInfoResponseEvent event) {
			activityActions.showProgressBar(false);
			setWaitScreen(false);
			washerPhotos = new ArrayList<PhotoParcelable>();

			if(event.getWasher() != null){

//				buttonBookStart.setEnabled(true);
				buttonBookStart2.setVisibility(View.VISIBLE);
//				buttonBookStart.setVisibility(View.VISIBLE);
				btnCall.setVisibility(View.GONE);
				activityActions.hideShowMenuBookButton(true);
				washer = event.getWasher();
				setBookingData(washer);
				hideRequestItem();

				discountLayout.setVisibility(View.GONE);
				if(washer.getActiveCampaign()!=null){
					discountLayout.setVisibility(View.VISIBLE);
					discount.setText("-"+washer.getActiveCampaign().getCampaignDiscount()+"%");
					discountDetail.setPaintFlags(discountDetail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				}

				if(mMap != null){
    				Location washerLocation = new Location("A");
	  			    washerLocation.setLatitude(Double.parseDouble(washer.getLonLat().get(1).toString()));
				    washerLocation.setLongitude(Double.parseDouble(washer.getLonLat().get(0).toString()));
		            MarkerOptions marker = new MarkerOptions()
		            .position(new LatLng(washerLocation.getLatitude(), washerLocation.getLongitude()))
	                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
							washer.getStatus().equals("information")?R.drawable.ic_marker:(washer.getActiveCampaign()!=null?R.drawable.ic_map_partner_discount:R.drawable.ic_map_partner))
					));
		            mMap.addMarker(marker);
		            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(washerLocation.getLatitude(), washerLocation.getLongitude()), ZOOM_LEVEL));
				}
				titleView.setVisibility(View.VISIBLE);
				titleView.setText(washer.getName().toLowerCase().contains("автомойка")?washer.getName():BA.str(R.string.car_wash_label_sp)+washer.getName());
				washerTitleName.setVisibility(View.GONE);
				washerTitleName.setText(washer.getName());
				washerName.setText(washer.getName());
				washerAddress.setText(washer.getAddress());
				washerPhone.setText("");

				if(!TextUtils.isEmpty(washer.getCompanyClientId())){
					clientLayout.setVisibility(View.VISIBLE);
					if(washer.getCompanyClientDeposit() != null)
					discountClientText.setText(washer.getCompanyClientDiscount()+"%");
					if(washer.getCompanyClientDeposit() != null)
					depositText.setText(formatter.format(washer.getCompanyClientDeposit())+"");
					if(washer.getCompanyClientBonus() != null)
					bonusText.setText(formatter.format(washer.getCompanyClientBonus())+"");
				}

				if(washer.getReviews() != null && washer.getReviews().size() > 0){
					washerReview.setText(BA.str(R.string.reviews_paren)+washer.getReviews().size()+")");
				}
				else{
					washerReview.setText(BA.str(R.string.reviews_zero));
				}

				if(washer.getContacts() != null && washer.getContacts().size() > 0){
				for(Contact contact : washer.getContacts())
					if(contact.getType().contains("phone")&&!TextUtils.isEmpty(contact.getValue()))
				       washerPhone.setText(contact.getValue());
					else if(contact.getType().contains("web")&&!TextUtils.isEmpty(contact.getValue())){
					   layoutWeb.setVisibility(View.VISIBLE);
				       washerWeb.setText(contact.getValue());
					}
					else if(contact.getType().contains("email")&&!TextUtils.isEmpty(contact.getValue())){
						   layoutEmail.setVisibility(View.VISIBLE);
					       washerEmail.setText(contact.getValue());
						}
				}
				else
			    washerPhone.setText(BA.str(R.string.not_specified));
				String addInfoText = "";
				HashMap<String,String> allAddInfo = BA.getReference().getAdditionalInfo();
				HashMap<String,String> allPayOptions = BA.getReference().getPayoptions();

				if(washer.getSchedule() != null){
					if(washer.getSchedule().getToday() != null && washer.getSchedule().getToday().size() > 0) {
						int startTime = washer.getSchedule().getToday().get(0).getFrom();
						int endTime = washer.getSchedule().getToday().get(0).getTo();
						String time = String.format("%02d", startTime/60)+":"+String.format("%02d", startTime%60)+" - "+(endTime/60==0?"24":String.format("%02d", endTime/60))+":"+String.format("%02d", endTime%60);
						washerTime.setText(time);
					}
					else layoutTime.setVisibility(View.GONE);
					if(washer.getStatus().equals("information")) {
						countsLayout.setVisibility(View.GONE);
						countsDateLayout.setVisibility(View.GONE);
					}

					//TODO temproary use comment column for additional info for information carwash
					if(washer.getStatus().equals("information") && !TextUtils.isEmpty(washer.getSchedule().getComment())){
						StringTokenizer st = new StringTokenizer(washer.getSchedule().getComment(), ",");
						int j = 0;
						View views[] = new View[st.countTokens()];
						while (st.hasMoreElements()) {
							View tagView =  getActivity().getLayoutInflater().inflate(R.layout.tag_add_info, null,false);
							TextView newTextView = (TextView) tagView.findViewById(R.id.text_info);
							String token = st.nextElement().toString().trim().toLowerCase();
							try {
								if (token.contains("."))
									token = token.substring(0, token.indexOf("."));
								else if (token.contains("\n"))
									token = token.substring(0, token.indexOf("\n"));
								if (token.length() > 50)
									token.substring(0, 50);
							}
							catch(Exception e){}

							newTextView.setText(token);
							views[j] = tagView;
							j++;
						}
						populateText(addInfoView,views,getActivity());
					}
				}

				if(!washer.getStatus().equals("information") && washer.getSchedule().getComment()!=null && !washer.getSchedule().getComment().equals(""))
					washerTime.append("\n"+washer.getSchedule().getComment());

				View views[];
				if(washer.getAdditionalInfo() != null && washer.getAdditionalInfo().size() > 0 && washer.getPayOptions() != null && washer.getPayOptions().size() > 0)
				 views = new View[washer.getAdditionalInfo().size()+washer.getPayOptions().size()];
				else if(washer.getAdditionalInfo() != null && washer.getAdditionalInfo().size() > 0)
					views = new View[washer.getAdditionalInfo().size()];
				else if(washer.getPayOptions() != null && washer.getPayOptions().size() > 0)
					views = new View[washer.getPayOptions().size()];
				else
					views = new View[0];

				int i = 0;
				if(washer.getAdditionalInfo() != null && washer.getAdditionalInfo().size() > 0){
				  for(String info : washer.getAdditionalInfo()){
					View tagView =  getActivity().getLayoutInflater().inflate(R.layout.tag_add_info, null,false);
					TextView newTextView = (TextView) tagView.findViewById(R.id.text_info);
					newTextView.setText(allAddInfo.get(info));
					views[i] = tagView;
//					addInfoText += "<b><font style=\"border-radius: 10px; background:#BADA55;\" color=\"blue\">" + allAddInfo.get(info)+ "," + "</font></b>" ;
//					if(info.equals("wifi"))
//						layoutWifi.setVisibility(View.VISIBLE);
//					else if(info.equals("cafe"))
//						layoutKafe.setVisibility(View.VISIBLE);
					i++;
				  }
				}

				if(washer.getPayOptions() != null && washer.getPayOptions().size() > 0){
					for(String info : washer.getPayOptions()){
						View tagView =  getActivity().getLayoutInflater().inflate(R.layout.tag_add_info, null,false);
						TextView newTextView = (TextView) tagView.findViewById(R.id.text_info);
						newTextView.setText(allPayOptions.get(info));
						views[i] = tagView;
						i++;
					}
				}

				if(views.length > 0)
				  populateText(addInfoView,views,getActivity());

//				populateViews
				txtExtraServices.setText(Html.fromHtml(addInfoText));
				washerImage.setVisibility(View.VISIBLE);
				if(washer.getImages() != null &&  washer.getImages().size() > 0){
				    for(Image photo: washer.getImages()){
				    	PhotoParcelable parcelablePhoto = PhotoParcelable.newPhoto(photo.getPhotoOrm(washer.getId()));
				    	washerPhotos.add(parcelablePhoto);
				    }

					    ImageDetail firstImage = washer.getImages().get(0).getThumb1();
						HttpClient.getPicasso()
						.load(RetrofitClient.API_URL_IMAGES+firstImage.getUrl())
						.fit()
						.centerCrop()
						.transform(new TransformationCircle(getActivity())) //Yerzhan
						.placeholder(R.drawable.ic_no_image)
						.into(washerImage, new Callback() {
							@Override
							public void onSuccess() {
								int cnt = washer.getImages().size();
								if(cnt > 1){
									imagesCircle.setVisibility(View.VISIBLE);
									imagesCount.setVisibility(View.VISIBLE);
									imagesCount.setText(cnt+"");
									imagesCountRect.setVisibility(View.VISIBLE);
								}
							}

							@Override
							public void onError() {
								washerPhotos.clear();
								imagesCircle.setVisibility(View.GONE);
								imagesCount.setVisibility(View.GONE);
								imagesCountRect.setVisibility(View.GONE);
							}
						});
				  }
				else{
					washerImage.setVisibility(View.VISIBLE);
					washerImage.setImageResource(R.drawable.ic_no_image);
				}

				if(washer.getSchedule() == null || washer.getMenu() == null) {
					buttonBookStart2.setVisibility(View.GONE);
					buttonBookStart.setVisibility(View.GONE);
					btnCall.setVisibility(View.VISIBLE);
					activityActions.hideShowMenuBookButton(false);
				}

				if(washer.getMenu() != null && washer.getMenu().size() == 0) {
					buttonBookStart2.setVisibility(View.GONE);
					buttonBookStart.setVisibility(View.GONE);
					btnCall.setVisibility(View.VISIBLE);
					activityActions.hideShowMenuBookButton(false);
				}

				if(washer.getStatus().equals("information")){
					buttonBookStart2.setVisibility(View.GONE);
					buttonBookStart.setVisibility(View.GONE);
					btnCall.setVisibility(View.VISIBLE);
					activityActions.hideShowMenuBookButton(false);
				}
			  }

			washerUpdate = false;
		}

		@OnClick(R.id.btnBookStart)
		public void onBookStart(){
			loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
			if(loggedIn) {
				if(mainFieldsLayout.getVisibility() == View.GONE)
				    showRequestItem();
			}
			else
				showLoginWarning();
		}

		@OnClick(R.id.btnBookStart2)
		public void onBookStart2(){
			onBookStart();
		}

	    @OnClick(R.id.btnCall)
		public void onCallButton(){
			onPhoneCallButtonClicked();
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		@Override
		public void onResume() {
			super.onResume();
			if(washerUpdate) {
				setWaitScreen(true);
				requestCounters();
				BA.getEventBus().post(new WasherInfoRequestEvent(washerId));
			}
			BA.getEventBus().post(new WasherFavouriteRequestEvent(new SearchFilter(false))); //Additional request for favourite washers
			BA.getEventBus().register(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			BA.getEventBus().unregister(this);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			activityActions = (ActivityActions) activity;
		}

		@Override
		public void onDetach() {
			super.onDetach();
			activityActions = null;
		}

	private void requestCounters() {
		AdminSearchFilter filter = new AdminSearchFilter();
		filter.setCarwash(washerId);
		BA.getEventBus().post(new ClientRequestEvent(filter));
	}


	@OnClick(R.id.favouriteLayout)
		public void favouriteButtonClicked(){
			loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
			if(loggedIn) {
				if (washer != null && !favourite.isSelected() )
					BA.getEventBus().post(new FavouriteWasherRequestEvent(washer.getId()));
				else
				   if(washer != null && favourite.isSelected() )
					BA.getEventBus().post(new UnfavouriteWasherRequestEvent(washer.getId()));
			}
			else
				showLoginWarning();
//			Builder dialog = new Builder(getActivity());
//			dialog.setTitle(favourite.isSelected()?"Удалить из Избранных?":"Добавить в Избранное?");
//			dialog.setNeutralButton("Нет", null);
//			dialog.setPositiveButton("Да", new OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				   setWaitScreen(true);
//				   if(!favourite.isSelected())
//                     BA.getEventBus().post(new FavouriteWasherRequestEvent(washer.getId()));
//				   else
//					 BA.getEventBus().post(new UnfavouriteWasherRequestEvent(washer.getId()));
//				}
//			});
//			dialog.show();
		}
		
		@OnClick(R.id.reviewLayout)
		public void onShowReviewButtonClicked(){
		  activityActions.showReview(washer);
		  washerUpdate = true;
		}		

		@OnClick(R.id.timeTableLayout)
		public void onTimeTableButtonClicked(){
		  activityActions.showTimeTable(washer);	
		}		
		
		@OnClick(R.id.imgWasherAvatar)
		public void onWasherImageClicked(){
			if(washerPhotos.size() > 0){
				activityActions.showWasherImages(washerPhotos);
			}
		}
		
		@OnClick(R.id.priceLayout)
		public void onPriceDetailButtonClicked(){
			if(washer != null) {
				if (washer.getMenu() != null && washer.getMenu().size() > 0) {
					activityActions.showPrices(washer);
				} else {
					ToastUtil.display(getActivity(), BA.str(R.string.prices_unavailable));
				}
			}
		}
		
		@Subscribe
		public void onAddFavouriteReceived(FavouriteWasherResponseEvent event){
			setWaitScreen(false);
			if(event.isSuccess()){
			   favourite.setSelected(true);			   
			}
			else{
				ToastUtil.display(getActivity(), BA.str(R.string.err_add_favorite));
		}
	 }
		
		@Subscribe
		public void onUnFavouriteReceived(UnfavouriteWasherResponseEvent event){
			setWaitScreen(false);
			if(event.isSuccess()){
			   favourite.setSelected(false);
			   washerFavourite.setText(BA.str(R.string.to_favorites));
			}
			else{
				ToastUtil.display(getActivity(), BA.str(R.string.err_remove_favorite));
		}
	 }
		
		/**
		 * Favourite washers received
		 * @param event
		 */
		@Subscribe
		public void onFavouriteWashersReceived(WasherFavouriteResponseEvent event) {
			if(event.getWashers() != null){
				for(WasherPublic item : event.getWashers()){
					if(item.getId().equals(washerId)){
						favourite.setSelected(true);	
						washerFavourite.setText(BA.str(R.string.remove_favorite));
					}
				}
		   }
      }	
		
	@OnClick(R.id.txtWasherPhone)
	public void onPhoneCallButtonClicked(){
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:"+washerPhone.getText().toString()));
		startActivity(intent);		
	}

	private void populateText(LinearLayout ll, View[] views , Context mContext) {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		ll.removeAllViews();
		int maxWidth = display.getWidth() - display.getWidth()/3 - (int)Functions.dipToPixels(getActivity(),30);

		LayoutParams params;
		LinearLayout newLL = new LinearLayout(mContext);
		newLL.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		newLL.setGravity(Gravity.LEFT);
		newLL.setOrientation(LinearLayout.HORIZONTAL);

		int widthSoFar = 0;

		for (int i = 0 ; i < views.length ; i++ ){
			LinearLayout LL = new LinearLayout(mContext);
			LL.setOrientation(LinearLayout.HORIZONTAL);
			LL.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
			LL.setLayoutParams(new ListView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			//my old code
			//TV = new TextView(mContext);
			//TV.setText(textArray[i]);
			//TV.setTextSize(size);  <<<< SET TEXT SIZE
			//TV.measure(0, 0);
			views[i].measure(0,0);
			params = new LayoutParams(views[i].getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
			//params.setMargins(5, 0, 5, 0);  // YOU CAN USE THIS
			//LL.addView(TV, params);
			LL.addView(views[i], params);
			LL.measure(0, 0);
			widthSoFar += views[i].getMeasuredWidth();// YOU MAY NEED TO ADD THE MARGINS
			if (widthSoFar >= maxWidth) {
				ll.addView(newLL);

				newLL = new LinearLayout(mContext);
				newLL.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				newLL.setOrientation(LinearLayout.HORIZONTAL);
				newLL.setGravity(Gravity.LEFT);
				params = new LayoutParams(LL
						.getMeasuredWidth(), LL.getMeasuredHeight());
				newLL.addView(LL, params);
				widthSoFar = LL.getMeasuredWidth();
			} else {
				newLL.addView(LL);
			}
		}
		ll.addView(newLL);
	}

	private void showLoginWarning(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		dialog.setTitle(BA.str(R.string.login_required));
		dialog.setPositiveButton(BA.str(R.string.login_word), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activityActions.login();
			}
		});
		dialog.setNegativeButton(BA.str(R.string.cancel_word),null);
		dialog.show();
	}

	public void showOptions(){
      if(washer != null){
		  if(washer.getStatus().equals(Constants.APPROVED) && washer.isBookable()) {
			  AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
			  b.setItems(R.array.approved_washer_info_options, new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialogInterface, int i) {
					  switch (i) {
						  case 0:

							  onBookStart();
							  break;
						  case 1:
							  onPhoneCallButtonClicked();
							  break;
						  case 2:
							  route();
							  break;
						  case 3:
							  loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
							  if(loggedIn) {
								  washerUpdate = true;
								  activityActions.addReview(washer);
							  }
							  else
								  showLoginWarning();
							  break;
						  case 4:
							  String email = "info@washme.kz";
							  String title = BA.str(R.string.report_wash)+washer.getName().toLowerCase().replace("автомойка","");
							  activityActions.writeToWashme(email,title);
							  break;
					  }
				  }
			  });
//			  b.setTitle(washer.getName().toLowerCase().contains("автомойка")?washer.getName():"Автомойка "+washer.getName());
//			  b.setTitle("Опции");
			  b.show();
		  }
		  else{
			  AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
			  b.setItems(R.array.information_washer_info_options, new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialogInterface, int i) {
					  switch (i) {
						  case 0:
							  onPhoneCallButtonClicked();
							  break;
						  case 1:
							  route();
							  break;
						  case 2:
							  loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
							  if(loggedIn) {
								  washerUpdate = true;
								  activityActions.addReview(washer);
							  }
							  else
								  showLoginWarning();
							  break;
						  case 3:
							  String email = "info@washme.kz";
							  String title = BA.str(R.string.report_wash)+washer.getName().toLowerCase().replace("автомойка","");
							  activityActions.writeToWashme(email,title);
							  break;
					  }
				  }
			  });
//			  b.setTitle(washer.getName().toLowerCase().contains("автомойка")?washer.getName():"Автомойка "+washer.getName());
//			  b.setTitle("Опции");
			  b.show();
		  }
	  }
	}

	private void route(){
		try {
			gps = new GPSTracker(getActivity());
			if (gps.canGetLocation()) {
				Location currentLoc = gps.getLocation();
				if (currentLoc != null) {
					String lat = Double.toString(washer.getLonLat().get(1));
					String lng = Double.toString(washer.getLonLat().get(0));
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude() + "&daddr=" + lat + "," + lng + ""));
					startActivity(intent);

				} else {
					gps.showSettingsAlert();
				}
			} else {
				gps.showSettingsAlert();
			}
		}
		catch(Exception e){}
	}

	@Subscribe
	public void onClientResponseReceived(ClientResponseEvent event){
		setWaitScreen(false);
		if(event.getResult() != null){
			if(BaseAssist.isSuccess(event.getResult())) {
				client = event.getResult().getClientInfo();
				if(client!=null){
						if(client.getFinishedBooksCount() != null){
						countsLayout.setVisibility(View.VISIBLE);
						finishedBooksCount.setText(client.getFinishedBooksCount()+BA.str(R.string.times_no_dot));
						if(client.getRegistered())
//							finishedBooksCount.setPaintFlags(finishedBooksCount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
						if(client.getLastFinishedBookDate() != null){
							countsDateLayout.setVisibility(View.VISIBLE);
//							finishedBooksDate.setPaintFlags(finishedBooksDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
							HashMap<String,String> timeMap = Functions.formatUTCDate(client.getLastFinishedBookDate());
							finishedBooksDate.setText(timeMap.get(Functions.DATE));
						}
					}
				}
			}
			else{
				countsLayout.setVisibility(View.GONE);
				countsDateLayout.setVisibility(View.GONE);
			}
		}
		else{
			countsLayout.setVisibility(View.GONE);
			countsDateLayout.setVisibility(View.GONE);
		}
	}

	@OnClick(R.id.discountLayout)
	public void onDiscountLaoutClicked(){
		 if(washer!=null){
			 activityActions.openCampaignInfo(washerId,washer.serialize());
		 }
	}

	@OnClick(R.id.btnExpand)
	public void expandButtonClicked(){
		loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
//		if(loggedIn) {
			if(mainFieldsLayout.getVisibility() == View.GONE){
				showRequestItem();
			}
			else{
				hideRequestItem();
			}
//		}
	}

	private Washer.GroupMenu getGroupServiceName(WasherPublic washer, String groupServiceId){
		for(Washer.GroupMenu groupMenu : washer.getGroupMenu()){
			if(groupMenu.getId().equals(groupServiceId)){
				return groupMenu;
			}
		}
		return null;
	}

	@Subscribe
	public void onSelectedServicesReceived(ServiceSelectionEvent event){
		setSelectedServices(event.getWasher(),event.getPriceDetails(),event.getServices(),event.getGroupServices(),event.getPrice(),event.getMinutes());
	}

	public void setSelectedServices(WasherPublic washer, List<BookInfo.PriceDetail> priceDetails, List<Integer> servicesList, List<String> groupServicesList, Double price, int minutes) {
		String groupServices = "";
		if(groupServicesList != null && groupServicesList.size() > 0) {
			for (String service : groupServicesList) {
				groupServices = groupServices + getGroupServiceName(washer,service).getName() + "+";
			}
			groupServices = groupServices.substring(0, groupServices.length() - 1);
		}

		String servicesText = "";
		if(servicesList != null && servicesList.size() > 0) {
			for (Integer service : servicesList) {
				servicesText = servicesText + BA.getReference().getServices().get(service) + "+";
			}
			servicesText = servicesText.substring(0, servicesText.length() - 1);
		}

		services.setText("");
		if(!TextUtils.isEmpty(groupServices)) {
			if(!TextUtils.isEmpty(servicesText))
				services.append(groupServices + "+" + servicesText);
			else
				services.append(groupServices);
		}
		else
			services.append(servicesText);

		services.append(BA.str(R.string.nl_for)+price + " ₸");//+" "+minutes+BA.str(R.string.min_dot_sp));
		bookServiceClose.setVisibility(View.VISIBLE);
		bookServiceRightArrow.setVisibility(View.GONE);
		if(request != null) {
			request.setServices(servicesList);
			request.setGroupServices(groupServicesList);
			request.setPriceDetails(priceDetails);
		}
	}

	@Subscribe
	public void onSelectedBookingTimeReceived(TimeTableSelectionEvent event){
		setBookingTime(event.getWasher(),event.getBookTime());
	}

	public void setBookingTime(WasherPublic washer, Date bookTimeDt) {
		bookTimeClose.setVisibility(View.VISIBLE);
		bookTimeRightArrow.setVisibility(View.GONE);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		String dateStr = dateFormatter.format(bookTimeDt);
		String timeStr = timeFormatter.format(bookTimeDt);
		if(DateUtils.isToday(bookTimeDt.getTime()))
			bookTime.setText(BA.str(R.string.today_paren)+dateStr+BA.str(R.string.paren_at)+timeStr);
		else bookTime.setText(dateStr+BA.str(R.string.space_at_space)+timeStr);
		if(request != null)
			request.setTime(bookTimeDt);
	}

	@OnClick(R.id.bookServiceClose)
	public void resetSelectedServices(){
		bookServiceClose.setVisibility(View.GONE);
		bookServiceRightArrow.setVisibility(View.VISIBLE);
		services.setText(BA.str(R.string.select_services));
		if(request != null) {
			request.setServices(null);
			request.setGroupServices(null);
			request.setPriceDetails(null);
		}

	}
	@OnClick(R.id.bookTimeClose)
	public void resetSelectedTime(){
		bookTimeClose.setVisibility(View.GONE);
		bookTimeRightArrow.setVisibility(View.VISIBLE);
		bookTime.setText(BA.str(R.string.choose_time));
		if(request != null)
			request.setTime(null);
	}

	@Subscribe
	public void onBookResponseReceived(BookResponseEvent event){
		setWaitScreen(false);
		if(event.getResult() != null){
			if(BaseAssist.isSuccess(event.getResult())) {
				getActivity().finish();
				openActiveBookInfo(event.getResult().getResponse().getId());
			}
			else
				ToastUtil.display(getActivity(), event.getResult().getMessage());
		}
		else{
			ToastUtil.display(getActivity(), BA.str(R.string.err_booking));
		}
	}

	public void openActiveBookInfo(String bookId) {
		Intent intent = new Intent(getActivity(),ClientActiveBookingInfoActivity.class);
		intent.putExtra(EXTRA_BOOKING_ID, bookId );
		startActivity(intent);
	}

	public void setBookingData(final Washer item) {
		request = new BookingRequest();
		request.setCarwash(URL_PREFIX+item.getId());

		mainButton.setVisibility(View.VISIBLE);
		bookTimeLayout.setVisibility(View.VISIBLE);
		bookService.setVisibility(View.VISIBLE);

		if(item.isBookable() && !item.getStatus().equals("information") && (item.getSchedule() != null)) {
			resetSelectedServices();
			resetSelectedTime();
			mainFieldsLayout.setVisibility(View.VISIBLE);
			informationLayout.setVisibility(View.GONE);
			serviceSpinner.setVisibility(View.GONE);
			booksFieldLayout.setVisibility(View.VISIBLE);
			mainButton.setText(BA.str(R.string.book_verb));
			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run() {
					createCarwashCarsSpinner(user, item);
				}
			},200);

			mainButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
					if(loggedIn) {
						if(request != null && request.getCarType()==-1) {
							ToastUtil.display(getActivity(), BA.str(R.string.select_car));
							return;
						}
						if (!(request.getServices() != null && request.getServices().size() > 0) && !(request.getGroupServices() != null && request.getGroupServices().size() > 0)) {
							ToastUtil.displayAtTop(getActivity(), BA.str(R.string.select_services));
							return;
						}
						if ((request.getTime() == null)) {
							ToastUtil.displayAtTop(getActivity(), BA.str(R.string.choose_time));
							return;
						}
						setWaitScreen(true);
						BA.getEventBus().post(new BookRequestEvent(request));
					}
					else
						showLoginWarning();
				}
			});
		}
		else {
			if(item.getUserWanted()) {
				if(item.getWantsCount() > 0) reqireWashmeText.setText(BA.str(R.string.wash_not_partner_prefix)+item.getWantsCount()+BA.str(R.string.people_want_suffix));
				requireWashmeButton.setVisibility(View.GONE);
				requireWashmeButton.setVisibility(View.GONE);
			}
			else if(wantedWashers !=null && wantedWashers.get(item.getId())!=null){
				reqireWashmeText.setText(BA.str(R.string.request_accepted));
				requireWashmeButton.setVisibility(View.GONE);
			}
			else{
				if(item.getWantsCount() > 0) reqireWashmeText.setText(BA.str(R.string.wash_not_partner_prefix)+item.getWantsCount()+BA.str(R.string.people_want_suffix_q));
				else reqireWashmeText.setText(BA.str(R.string.wash_not_partner));
				requireWashmeButton.setVisibility(View.VISIBLE);
			}

			requireWashmeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
					if(loggedIn) {
						wantedWashers.put(item.getId(),item.getId());
						UserPreferences.putWantedWashers(BA.getContext(),(new WantedWashers(wantedWashers)).serialize());
						requireWashmeButton.setVisibility(View.GONE);
						reqireWashmeText.setText(BA.str(R.string.thanks_request_accepted));
						AddReviewRequest request = new AddReviewRequest();
						request.setCarwash(URL_PREFIX+item.getId());
						request.setReviewType("wanted");
						BA.getEventBus().post(new AddReviewRequestEvent(request));
					}
					else
						showLoginWarning();
				}
			});

			mainFieldsLayout.setVisibility(View.GONE);
			informationLayout.setVisibility(View.VISIBLE);
			booksFieldLayout.setVisibility(View.GONE);
			mainButton.setText(BA.str(R.string.route_build));
			mainButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if (gps.canGetLocation()) {
							Location currentLoc = gps.getLocation();
							if (currentLoc != null) {
								String lat = Double.toString(item.getLonLat().get(1));
								String lng = Double.toString(item.getLonLat().get(0));
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude() + "&daddr=" + lat + "," + lng + ""));
								startActivity(intent);

							} else {
								gps.showSettingsAlert();
							}
						} else {
							gps.showSettingsAlert();
						}
					}
					catch(Exception e){}
				}
			});
		}
	}

	public void hideClusterItem(){
			resetSelectedServices();
			resetSelectedTime();
			mainButton.setEnabled(true);
			mainButton.setVisibility(View.GONE);
			bookTimeLayout.setVisibility(View.GONE);
			bookService.setVisibility(View.GONE);
			serviceSpinner.setVisibility(View.VISIBLE);

			if(expandButtonPosition == DOWN)
				upExpandButton();

			mainFieldsLayout.setVisibility(View.VISIBLE);
			informationLayout.setVisibility(View.GONE);
	}

	public void hideRequestItem(){
		if(expandButtonPosition == DOWN) {
			upExpandButton();
		}
		mainButton.setVisibility(View.GONE);
		mainFieldsLayout.setVisibility(View.GONE);
		informationLayout.setVisibility(View.GONE);
	}

	public void showRequestItem(){
		if(expandButtonPosition == DOWN)
			upExpandButton();
		else
			downExpandButton();
		mainButton.setVisibility(View.VISIBLE);
		mainFieldsLayout.setVisibility(View.VISIBLE);
		if(washer!=null && washer.getStatus().equals("information")){
			mainButton.setVisibility(View.GONE);
			informationLayout.setVisibility(View.VISIBLE);
		}
	}

	public void downExpandButton(){
		expandButtonPosition = DOWN;
		AnimationSet animSet = new AnimationSet(true);
		animSet.setInterpolator(new DecelerateInterpolator());
		animSet.setFillAfter(true);
		animSet.setFillEnabled(true);
		final RotateAnimation animRotate = new RotateAnimation(0f, 180f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animRotate.setDuration(300);
		animRotate.setFillAfter(true);
		animSet.addAnimation(animRotate);
		buttonExpand.startAnimation(animSet);
	}

	public void upExpandButton(){
		expandButtonPosition = UP;
		AnimationSet animSet = new AnimationSet(true);
		animSet.setInterpolator(new DecelerateInterpolator());
		animSet.setFillAfter(true);
		animSet.setStartOffset(100);
		animSet.setFillEnabled(true);
		final RotateAnimation animRotate = new RotateAnimation(180.0f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animRotate.setDuration(300);
		animRotate.setFillAfter(true);
		animSet.addAnimation(animRotate);
		buttonExpand.startAnimation(animSet);
	}

	private void createCarwashCarsSpinner(final User user,final Washer washer){

		final List<CarItem> carItemList = new ArrayList<CarItem>();
		if(user!= null && user.getCars() != null && user.getCars().size() > 0) {
			for (int i = 0; i < user.getCars().size(); i++) {
				if (user.getCars().get(i).getStatusDisabled() == null || (user.getCars().get(i).getStatusDisabled() != null && !user.getCars().get(i).getStatusDisabled())) {
					carItemList.add(user.getCars().get(i));
				}
			}
		}

		if(carSpinner != null && carItemList.size() > 0) {
			mainButton.setEnabled(true);
			carSpinner.setVisibility(View.VISIBLE);
			int size = carItemList.size();
			if(carItemList.size() > 3)
				size = 3;
			String[] valuesArray = new String[size];
			final Integer[] keysArray = new Integer[size];
			final String[] models = new String[size];
			final String[] carNumbers = new String[size];

			int j = 0;
			for(int i = carItemList.size()-1 ; i >= 0; i--){
				if(size == 0) break;
				valuesArray[j] = BA.getReference().getCarType().get(carItemList.get(i).getCarType())+", "+carItemList.get(i).getCarNumber();
				keysArray[j] = carItemList.get(i).getCarType();
				models[j] = carItemList.get(i).getCarModel();
				carNumbers[j] = carItemList.get(i).getCarNumber();
				size--;
				j++;
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, valuesArray);
			adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

			carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					if (!isSpinnerTouched) return;
					services.setText(BA.str(R.string.select_service));
					bookServiceClose.setVisibility(View.GONE);
					bookServiceRightArrow.setVisibility(View.VISIBLE);
					if(request != null) {
						request.setCarType(keysArray[pos]);
						request.setCarMark(models[pos]);
						request.setCarNo(carNumbers[pos]);
						request.setClientName(user.getFirstName());
						request.setClientKey(carNumbers[pos] + " (" + user.getFirstName() + ")");
					}

//                    createCarwashServices(washer,keysArray[pos]);
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			bookService.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(bookServiceClose.getVisibility() == View.GONE) {
						serviceDialogFragment = ClientServiceDialogFragment.newInstance(washer, keysArray[0]);
						serviceDialogFragment.setCancelable(false);
						serviceDialogFragment.show(getChildFragmentManager().beginTransaction(), "DialogFragment");
					}
				}
			});

			bookTimeLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(bookTimeClose.getVisibility() == View.GONE) {
						timetableDialogFragment = ClientTimetableDialogFragment.newInstance(washer);
						timetableDialogFragment.setCancelable(false);
						timetableDialogFragment.show(getChildFragmentManager().beginTransaction(), "DialogFragment");
					}
				}
			});

			if(request != null) {
				request.setCarType(keysArray[0]);
				request.setCarMark(models[0]);
				request.setCarNo(carNumbers[0]);
				request.setClientName(user.getFirstName());
				request.setClientKey(carNumbers[0] + " (" + user.getFirstName() + ")");
			}
//            createCarwashServices(washer,keysArray[0]);
			carSpinner.setAdapter(adapter);
		}
		else{
//			mainButton.setEnabled(false);
			myCarText.setVisibility(View.VISIBLE);
			carSpinner.setVisibility(View.GONE);
		}
	}

	private void createCarwashServices(Washer washer, int carType){
		if(serviceSpinner != null && ((washer.getMenu() != null && washer.getMenu().size() > 0) || (washer.getGroupMenu()!=null && washer.getGroupMenu().size() > 0))) {
			//Services
			ArrayList<ServiceItem> serviceItems = new ArrayList<ServiceItem>();
			final LinkedHashMap<Integer,String> dictionary = BA.getReference().getServices();
			LinkedHashMap<Integer,ArrayList<Washer.Prices>> washerMenu = washer.getMenu();
			if(washerMenu != null && washerMenu.size() > 0){
				serviceItems.clear();

				for (Map.Entry<Integer, ArrayList<Washer.Prices>> entry : washerMenu.entrySet()) {
					Integer key = entry.getKey();
					if(key == carType){
						ArrayList<Washer.Prices> pricesMenu = entry.getValue();
						for(Washer.Prices priceMenu : pricesMenu){
							ServiceItem newItem = new ServiceItem();
							newItem.setServiceId(priceMenu.getType());
							newItem.setServiceName(dictionary.get(priceMenu.getType()));
							newItem.setPrice(priceMenu.getPrice());
							newItem.setTime(priceMenu.getTime());
							serviceItems.add(newItem);
						}
						break;
					}
				}
			}
			//Group services
			List<Washer.GroupMenu> groupServiceItems = new ArrayList<Washer.GroupMenu>();
			List<Washer.GroupMenu> washerGroup = washer.getGroupMenu();
			if(washerGroup != null && washerGroup.size() > 0){
				groupServiceItems.clear();
				for(Washer.GroupMenu g:washerGroup){
					for(ReversePrices p:g.getPrices()){
						if(p.getCarType() == carType){
							groupServiceItems.add(g);
						}
					}
				}
			}

			//First show group services, if count of group services exceeds 4, add normal services
			int size = user.getCars().size();
			if(groupServiceItems.size() >= 4)
				size = 4;
			else{
				if(serviceItems.size() > 0){
					size = groupServiceItems.size() + (4 - groupServiceItems.size());
				}
				else
					size = groupServiceItems.size();
			}

			int j = 0;
			int i = 0;
			String[] valuesArray = new String[size+1];
			final String[] keysArray = new String[size+1];
			valuesArray[i] = BA.str(R.string.popular_services);
			keysArray[i] = "";
			for (i = 1; i < valuesArray.length; i++){
				if(groupServiceItems.size() >= i+1) {
					double price = 0;
					int time = 0;
					for(ReversePrices p: groupServiceItems.get(i).getPrices()) {
						if(p.getCarType() == carType){
							price = p.getPrice();
							time = p.getTime();
						}
					}

					valuesArray[i] = groupServiceItems.get(i).getName()+BA.str(R.string.for_sp)+price+"₸.";//+time+BA.str(R.string.min_dot_sp);
					keysArray[i] = groupServiceItems.get(i).getId();
				}
				if(size > 0 && (groupServiceItems.size() - (i + 1) < 0) && serviceItems.size() > 0 && j < serviceItems.size()){
					double price = serviceItems.get(j).getPrice();
					int time = serviceItems.get(j).getTime();
					valuesArray[i] = BA.getReference().getServices().get(serviceItems.get(j).getServiceId())+BA.str(R.string.for_sp)+price+"₸.";//+time+BA.str(R.string.min_dot_sp);
					keysArray[i] = serviceItems.get(j).getServiceId()+"";
					j++;
				}
				size--;
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, valuesArray);
			adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

			serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					if (!isSpinnerTouched) return;
//                    if(pos == 0) filter.setOperationType(null);
//                    else filter.setOperationType(Integer.toString(keysArray[pos]));
//                    loadData(washer,startTimeDt,endTimeDt);
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			serviceSpinner.setAdapter(adapter);
		}
	}

	User user;
	@Subscribe
	public void onUserReceived(UserGetSelfResponseEvent event) {
		loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
		if (event != null && loggedIn && event.getUser() != null) {
			user = event.getUser();
			if(washer != null && washer.isBookable() && !washer.getStatus().equals("information") && (washer.getSchedule() != null))  createCarwashCarsSpinner(user, washer);
		}
		else{
			myCarText.setVisibility(View.VISIBLE);
			carSpinner.setVisibility(View.GONE);
		}
	}

//	@OnTouch(R.id.scrollView)
//	public boolean onRequestWindowClicked(){
//		hideRequestItem();
//		return true;
//	}

	@OnClick(R.id.mainLayout)
	public void onRequestWindowClick(){
		//Empty implementation
	}

	@Subscribe
	public void onWantedWashersReceived(WantedWashersResponseEvent event){
		if(event!=null && event.getWantedWashers() != null)
			wantedWashers = event.getWantedWashers();
	}
}
