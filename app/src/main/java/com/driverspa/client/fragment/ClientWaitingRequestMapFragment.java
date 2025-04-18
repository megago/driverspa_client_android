package com.driverspa.client.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.skyfishjy.library.RippleBackground;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import com.squareup.otto.Subscribe;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.activity.CityChooseActivity;
import com.driverspa.adapter.BidsAdapter;
import com.driverspa.assist.BaseAssist;
import com.driverspa.client.activity.ClientWaitingRequestActivity;
import com.driverspa.model.FareRequest;
import com.driverspa.model.NotificationType;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.AcceptFareRequestBid;
import com.driverspa.util.otto.NewFareRequestBidEvent;
import com.driverspa.util.otto.ws.FareRequestAcceptFareEvent;
import com.driverspa.util.otto.ws.FareRequestAcceptFareResponseEvent;
import com.driverspa.util.otto.ws.FareRequestCancelEvent;
import com.driverspa.util.otto.ws.FareRequestCancelResponseEvent;
import com.driverspa.util.otto.ws.FareRequestIncreaseFareEvent;
import com.driverspa.util.otto.ws.FareRequestIncreaseFareResponseEvent;
import com.driverspa.util.otto.ws.FareRequestNotAcceptFareEvent;
import com.driverspa.util.otto.ws.HideBidsListLayoutEvent;
import com.driverspa.util.otto.ws.WashersNearMapRequestEvent;
import com.driverspa.util.otto.ws.WashersNearMapResponseEvent;
import com.driverspa.view.MySupportMapFragment;

public class ClientWaitingRequestMapFragment extends ClientBaseFragment implements ClusterManager.OnClusterClickListener<WasherPublic>,
		ClusterManager.OnClusterItemClickListener<WasherPublic>,
		ClusterManager.OnClusterItemInfoWindowClickListener<WasherPublic>{

	public static ClientWaitingRequestMapFragment init(String fareRequest){
		ClientWaitingRequestMapFragment fragment = new ClientWaitingRequestMapFragment();
		Bundle b = new Bundle();
		b.putString(ClientWaitingRequestActivity.REQUEST_DATA,fareRequest);
		fragment.setArguments(b);
		return fragment;
	}

	public interface ActivityActions {
		public void openActiveBookInfo(String bookId);
	}

	private ActivityActions activityActions;
	DecimalFormat decimalFormatter = new DecimalFormat("#,###.##");
	private static View view;
	private static GoogleMap mMap;
	private static ClusterManager<WasherPublic> mClusterManager;
	private Bitmap bMap;
	private Bitmap bMapPartner;
	private Bitmap bMapPartnerDiscount;
	private Bitmap bMMap;
	Bitmap [] baseBitmaps = new Bitmap[1];		
	float density;
	DisplayMetrics metrics;
	private static final int ZOOM_LEVEL = 14;
	Location currentLoc;
	LatLngBounds bounds = null;

	@InjectView (R.id.myCar)
	TextView myCar;
	@InjectView (R.id.services)
	TextView services;
	@InjectView (R.id.fare)
	TextView fare;
	@InjectView (R.id.tempFare)
	TextView tempFare;
	@InjectView (R.id.comment)
	TextView comment;
	@InjectView (R.id.requestInfo)
	TextView requestInfo;
	@InjectView (R.id.btnChangeFare)
	Button btnChangeFare;
	@InjectView (R.id.plusPrice)
	Button plusPrice;
	@InjectView (R.id.minusPrice)
	Button minusPrice;
	@InjectView (R.id.rippleBackground)
	RippleBackground rippleBackground;
	@InjectView (R.id.circle)
	ImageView circle;
	@InjectView (R.id.listLayout)
	View listLayout;
	@InjectView (R.id.list_view)
	ListView listView;
	@InjectView (R.id.durationLayout)
	View durationLayout;
	@BindView(R.id.txtWasherAddress)
	TextView address;
	@BindView(R.id.txtWasherName)
	TextView name;
	@BindView(R.id.txtDistance)
	TextView distance;
	@BindView(R.id.review)
	RatingBar review;

	FareRequest fareRequest;
	double tempChangedFare = 0;
	Marker myMarker;

	private LruCache<String, Bitmap> mMemoryCache;
	BitmapDescriptor infoBitmapDescriptor;
	BitmapDescriptor partnerBitmapDescriptor;
	BitmapDescriptor partnerDiscountBitmapDescriptor;
	private SearchFilter filter = new SearchFilter();
	List<WasherPublic> washers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

		bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker);
		bMapPartner = BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_partner);
		bMapPartnerDiscount = BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_partner_discount);
		bMMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_client_map);
		baseBitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clustermarker);
		addBitmapToMemoryCache("info",bMap);
		addBitmapToMemoryCache("partner",bMapPartner);
		addBitmapToMemoryCache("partnerDiscount",bMapPartnerDiscount);
		addBitmapToMemoryCache("mine",bMMap);
		addBitmapToMemoryCache("cluster",baseBitmaps[0]);

		metrics = getResources().getDisplayMetrics();
	    density = metrics.density;
	    
	    if (container == null) {
	        return null;
	    }
	    view = (RelativeLayout) inflater.inflate(R.layout.fragment_client_waiting_request, container, false);
	
	    setUpMapIfNeeded(); // For setting up the MapFragment
	     
	    return view;
	}
	
	/***** Sets up the map if it is possible to do so *****/
	public void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	        // Try to obtain the map from the SupportMapFragment.
			((MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap googleMap) {
					mMap = googleMap;
					if(currentLoc != null)
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude()), ZOOM_LEVEL));
					setUpMap();
				}
			});
	    }
	}
	
	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap() {
		mClusterManager = new ClusterManager<WasherPublic>(getActivity(), mMap);
		mClusterManager.setRenderer(new WasherModelRenderer());

		UiSettings ui = mMap.getUiSettings();
		ui.setCompassEnabled(false);
		ui.setZoomControlsEnabled(false);
		ui.setMyLocationButtonEnabled(false);
		ui.setAllGesturesEnabled(false);
		mMap.setOnCameraChangeListener(mClusterManager);
		mMap.setOnMarkerClickListener(mClusterManager);
		mMap.setOnInfoWindowClickListener(mClusterManager);
		mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
		mMap.setPadding(0, 0, 0, 100);
		mClusterManager.setOnClusterClickListener(this);
		mClusterManager.setOnClusterItemClickListener(this);
		mClusterManager.setOnClusterItemInfoWindowClickListener(this);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		
	    if (mMap != null)
	        setUpMap();
	
	    if (mMap == null) {
	        // Try to obtain the map from the SupportMapFragment.
			((MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap googleMap) {
					mMap = googleMap;
					setUpMap();
				}
			});
	    }

		if(!TextUtils.isEmpty(getArguments().getString(ClientWaitingRequestActivity.REQUEST_DATA))){
			fareRequest = FareRequest.deserialize(getArguments().getString(ClientWaitingRequestActivity.REQUEST_DATA));
			setData(fareRequest);
			loadData();
		}

		rippleBackground.startRippleAnimation();
		infoBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache("info"));
		partnerBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache("partner"));
		partnerDiscountBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache("partnerDiscount"));

	}

	private void initialBidRequestList(){
		if(!TextUtils.isEmpty(UserPreferences.getActiveFareRequest(BA.getContext()))) {
			FareRequest fr = FareRequest.deserialize(UserPreferences.getActiveFareRequest(BA.getContext()));
			if(!fr.getId().equals(fareRequest.getId())){
				UserPreferences.putActiveFareRequestBooking(BA.getContext(),fareRequest.serialize());
			}
			else{
				if(fr.getActgiveBids()!= null && fr.getActgiveBids().size() > 0) {
					Iterator<FareRequest.BidObject> iter = fr.getActgiveBids().iterator();
					while (iter.hasNext()) {
						FareRequest.BidObject b = iter.next();
						//If notification is old, check for creation time, reject if it is bigger than 20 sec
						if (System.currentTimeMillis() - b.getInitialTime() > 20*1000) {
							iter.remove();
							FareRequest frTemp = new FareRequest();
							frTemp.setId(fr.getId());
							frTemp.setAcceptedBid(b.getId());
							BA.getEventBus().post(new FareRequestNotAcceptFareEvent(frTemp));
						}
						else{
							//If less than 20 sec, give another 10 sec chance
							b.setInitialTime(System.currentTimeMillis());
						}
					}
					UserPreferences.putActiveFareRequestBooking(BA.getContext(), fr.serialize());
				}
			}
		}
		else{
			UserPreferences.putActiveFareRequestBooking(BA.getContext(), fareRequest.serialize());
		}
		generateBidRequestList();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		BA.setNotificationType(NotificationType.NOTHING);
        mMap = null;
		BA.getEventBus().unregister(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		BA.setNotificationType(NotificationType.FARE_REQUEST);
		BA.getEventBus().register(this);
		initialBidRequestList();
//		BA.getEventBus().post(new LocationRequestEvent());
		 if (mMap == null) {
			 ((MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
				 @Override
				 public void onMapReady(GoogleMap googleMap) {
					 mMap = googleMap;
					 setUpMap();
				 }
			 });
		 }
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

	private void setData(FareRequest fareRequest){
		if(fareRequest.getFare() < 500){
			requestInfo.setText("Возможно #AMOUNT# ₸. не заинтересует автомоек, рекомендуем поднять цену".replace("#AMOUNT#",decimalFormatter.format(fareRequest.getFare())));
		}else{
			requestInfo.setText("Предлагаем вашу цену автомойкам, пожалуйста подождите");
		}
		myCar.setText(fareRequest.getClientKey());
		String servicesTxt = "";
		if(fareRequest.getServices() != null && fareRequest.getServices().size() > 0) {
			for (Integer service : fareRequest.getServices()) {
				servicesTxt = servicesTxt + BA.getReference().getServices().get(service) + "+";
			}
			servicesTxt = servicesTxt.substring(0, servicesTxt.length() - 1);
			services.setText(servicesTxt);
		}
		if(!TextUtils.isEmpty(fareRequest.getComment())){
			comment.setText(fareRequest.getComment());
		}
		else
			comment.setVisibility(View.GONE);

		fare.setText(decimalFormatter.format(fareRequest.getFare())+"");
		tempFare.setText(decimalFormatter.format(fareRequest.getFare())+"");
		tempChangedFare = fareRequest.getFare();

		if(tempChangedFare == fareRequest.getFare()){
			btnChangeFare.setEnabled(false);
			minusPrice.setEnabled(false);
		}
		if(mMap != null){
			LatLng l = new LatLng(fareRequest.getLonLat().get(1),fareRequest.getLonLat().get(0));
//			myMarker = mMap.addMarker(new MarkerOptions()
//					.position(l)
//					.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_client_map)))
//					.title(null));
			currentLoc = new Location("");
			currentLoc.setLatitude(l.latitude);
			currentLoc.setLongitude(l.longitude);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, ZOOM_LEVEL));
		}
	}

	@OnClick(R.id.btnChangeFare)
	public void onButtonChangeFareClicked(){
		if(tempChangedFare > fareRequest.getFare()){
			setWaitScreen(true);
			fareRequest.setFare(tempChangedFare);
			BA.getEventBus().post(new FareRequestIncreaseFareEvent(fareRequest));
		}
	}


	@OnClick(R.id.plusPrice)
	public void onButtonPlusFareClicked(){
		tempChangedFare = tempChangedFare + 50;
		minusPrice.setEnabled(true);
		btnChangeFare.setEnabled(true);
		tempFare.setText(decimalFormatter.format(tempChangedFare));
	}
	@OnClick(R.id.minusPrice)
	public void onButtonMinusFareClicked(){
			tempChangedFare = tempChangedFare - 50;
			tempFare.setText(decimalFormatter.format(tempChangedFare));
			if(tempChangedFare == fareRequest.getFare()){
				minusPrice.setEnabled(false);
				btnChangeFare.setEnabled(false);
			}
	}

	@OnClick(R.id.cancelRequest)
	public void onButtoncancelRequestClicked(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		dialog.setTitle("Отменить предложение?");
		dialog.setNeutralButton("Нет", null);
		dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setWaitScreen(true);
				BA.getEventBus().post(new FareRequestCancelEvent(fareRequest));
			}
		});
		dialog.show();

	}

	@Subscribe
	public void onFareRequestCancelResponseReceived(FareRequestCancelResponseEvent event){
		setWaitScreen(false);
		if(event.getData() != null){
			if(BaseAssist.isSuccess(event.getData())) {
				ToastUtil.display(getActivity(), "Предложение отменено");
				UserPreferences.putActiveFareRequestBooking(BA.getContext(),null);
				getActivity().finish();
			}
			else
				ToastUtil.display(getActivity(), event.getData().getMessage());
		}
		else{
			ToastUtil.display(getActivity(), "Ошибка");
		}
	}

	@Subscribe
	public void onFareRequestIncreaseFareResponseReceived(FareRequestIncreaseFareResponseEvent event){
		setWaitScreen(false);
		if(event.getData() != null){
			if(BaseAssist.isSuccess(event.getData())) {
				setData(fareRequest);
			}
			else
				ToastUtil.display(getActivity(), event.getData().getMessage());
		}
		else{
			ToastUtil.display(getActivity(), "Ошибка");
		}
	}

	private void addMarkers(List<WasherPublic> washers){
		if(mMap != null) {
			synchronized (mClusterManager) {
				mMap.clear();
				mClusterManager.clearItems();
			}

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			if (washers != null && washers.size() > 0) {
				for (WasherPublic model : washers) {
					builder.include(new LatLng(Double.parseDouble(model.getLonLat().get(1).toString()), Double.parseDouble(model.getLonLat().get(0).toString())));
					mClusterManager.addItem(model);
				}
				bounds = builder.build();
				mClusterManager.cluster();
			}
		}
	}

	private class WasherModelRenderer extends DefaultClusterRenderer<WasherPublic> {
		public WasherModelRenderer() {
			super(getActivity(), mMap, mClusterManager);
		}

		@Override
		protected void onBeforeClusterItemRendered(WasherPublic model, MarkerOptions markerOptions) {
			if(model.getStatus().equals("information"))
				markerOptions.icon(infoBitmapDescriptor);
			else {
				if(model.getActiveCampaign() != null)
					markerOptions.icon(partnerDiscountBitmapDescriptor);
				else
					markerOptions.icon(partnerBitmapDescriptor);
			}
		}

		@Override
		protected void onBeforeClusterRendered(Cluster<WasherPublic> cluster, MarkerOptions markerOptions) {
			markerOptions.icon(getClusterIcon(cluster.getSize()));
		}

		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			// Always render clusters.
			return cluster.getSize() > 1000;
		}
	}

	@Override
	public void onClusterItemInfoWindowClick(WasherPublic item) {
	}

	@Override
	public boolean onClusterItemClick(final WasherPublic item) {
		return true;
	}

	@Override
	public boolean onClusterClick(Cluster<WasherPublic> cluster) {
		return false;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	private void loadData() {
		this.filter.setOffset("0");
		this.filter.setLimit("400");
		String localCity = UserPreferences.getCity(BA.getContext());
		if(currentLoc != null) {
			String foundCity = CityChooseActivity.findNearestCity(currentLoc);
			if (!TextUtils.isEmpty(localCity)&&!TextUtils.isEmpty(foundCity) && localCity.equals(foundCity) && filter.isDefaultValues()) {
				String location = String.format(Locale.US, "%f,%f", currentLoc.getLongitude(), currentLoc.getLatitude());
				this.filter.setCity(null);
				filter.setLonLat(location);
			}
			else{
				filter.setLonLat(null);
				filter.setCity(localCity);
			}
		}
		else{
			filter.setLonLat(null);
			filter.setCity(localCity);
		}
		BA.getEventBus().post(new WashersNearMapRequestEvent(filter));
	}

	@Subscribe
	public void onNearMapWashersReceived(WashersNearMapResponseEvent event) {
		if(event.getData() != null){
			if(BaseAssist.isSuccess(event.getData())) {
				mMap.clear();
				synchronized (mClusterManager) {
					mClusterManager.clearItems();
				}
				washers = event.getData().getResponse().getResult();
				addMarkers(washers);
			}
			else{
				synchronized (mClusterManager) {
					mMap.clear();
					mClusterManager.clearItems();
				}
			}
		}else{
			synchronized (mClusterManager) {
				mMap.clear();
				mClusterManager.clearItems();
			}
		}
	}

	public BitmapDescriptor getClusterIcon(int markersCount) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Rect bounds = new Rect();
		paint.setColor(Color.BLACK);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize((int)(density*13));
		Bitmap base = getBitmapFromMemCache("cluster");
		Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);
		String text = null;
		if(markersCount < 100)
			text = String.valueOf(markersCount);
		else
			text = "+99";
		paint.getTextBounds(text, 0, text.length(), bounds);
		float x = bitmap.getWidth() / 2.0f;
		float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top - density*4;
		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(text, x, y, paint);
		BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
		return icon;
	}

	private void showBoundsOfMarkers(LatLngBounds bounds){
		int padding = (int) Functions.dipToPixels(metrics, 100); // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		if(mMap != null)
			mMap.moveCamera(cu);
	}

	class CustomAdapterForClusterItems implements GoogleMap.InfoWindowAdapter {
		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker arg0) {

			return null;
		}
	}

	private void generateBidRequestList(){
		if(!TextUtils.isEmpty(UserPreferences.getActiveFareRequest(BA.getContext()))) {
			FareRequest fr = FareRequest.deserialize(UserPreferences.getActiveFareRequest(BA.getContext()));
			if(fr.getActgiveBids()!= null && fr.getActgiveBids().size() > 0 && adapter == null) {
				listLayout.setVisibility(View.VISIBLE);
				adapter = new BidsAdapter(getActivity(), fareRequest.getLonLat());
				adapter.set(fr.getActgiveBids());
				listView.setAdapter(adapter);
			}
			else if(fr.getActgiveBids()!= null && fr.getActgiveBids().size() > 0 && adapter != null){
				listLayout.setVisibility(View.VISIBLE);
				adapter.set(fr.getActgiveBids());
//				  adapter.addItem();
				adapter.notifyDataSetChanged();
			}
			else{
				listLayout.setVisibility(View.GONE);
			}
		}
		else{
			listLayout.setVisibility(View.GONE);
		}
	}

	@Subscribe
	public void onNewBidRequestEvent(NewFareRequestBidEvent event){
		generateBidRequestList();
	}

	@Subscribe
	public void onHideBidsListRequestEvent(HideBidsListLayoutEvent event){
		if(adapter!=null) {
			adapter.stopTimer();
			adapter = null;
		}

		listLayout.setVisibility(View.GONE);
	}

	@Subscribe
	public void onAcceptBidEvent(AcceptFareRequestBid event){
		if(event!= null && event.getBidObject() != null){
			durationLayout.setVisibility(View.VISIBLE);
			FareRequest.BidObject item = event.getBidObject();
			acceptedBid = item;
			review.setRating((float)item.getCarwashRating().doubleValue());
			name.setText(item.getCarwashName().toLowerCase().contains("автомойка")?item.getCarwashName():"Автомойка "+item.getCarwashName());
			if(!TextUtils.isEmpty(item.getCarwashAddress())){
				address.setText(item.getCarwashAddress());
			}
			if(TextUtils.isEmpty(item.getCarwashAddress()))
				address.setText(Functions.getCityDescription("адрес не указан"));

			try{
				Location washerLocation = new Location("A");
				washerLocation.setLatitude(Double.parseDouble(item.getCarwashLonLat().get(1).toString()));
				washerLocation.setLongitude(Double.parseDouble(item.getCarwashLonLat().get(0).toString()));
				Location requestLocationB = new Location("B");
				requestLocationB.setLatitude(Double.parseDouble(fareRequest.getLonLat().get(1).toString()));
				requestLocationB.setLongitude(Double.parseDouble(fareRequest.getLonLat().get(0).toString()));

				float dist = requestLocationB.distanceTo(washerLocation);
				distance.setText(String.format("%s км", decimalFormatter.format(dist/1000).replaceAll(",", " ")));
			}
			catch(Exception e){
				distance.setText("- км");
			}
		}
	}

	@OnClick(R.id.min5)
	public void min5Clicked(){
		setWaitScreen(true);
		fareRequest.setDurationArrival(5);
		fareRequest.setAcceptedBid(acceptedBid.getId());
		BA.getEventBus().post(new FareRequestAcceptFareEvent(fareRequest));
	}

	@OnClick(R.id.min10)
	public void min10Clicked(){
		setWaitScreen(true);
		fareRequest.setDurationArrival(10);
		fareRequest.setAcceptedBid(acceptedBid.getId());
		BA.getEventBus().post(new FareRequestAcceptFareEvent(fareRequest));
	}

	@OnClick(R.id.min20)
	public void min20Clicked(){
		setWaitScreen(true);
		fareRequest.setDurationArrival(20);
		fareRequest.setAcceptedBid(acceptedBid.getId());
		BA.getEventBus().post(new FareRequestAcceptFareEvent(fareRequest));
	}

	@OnClick(R.id.min30)
	public void min30Clicked(){
		setWaitScreen(true);
		fareRequest.setDurationArrival(30);
		fareRequest.setAcceptedBid(acceptedBid.getId());
		BA.getEventBus().post(new FareRequestAcceptFareEvent(fareRequest));
	}

	@Subscribe
	public void onAcceptedBidResponseEventReceived(FareRequestAcceptFareResponseEvent event) {
		setWaitScreen(false);
		if(event.getData() != null){
			if(BaseAssist.isSuccess(event.getData())) {
			   activityActions.openActiveBookInfo(event.getData().getData().getBooking());
				UserPreferences.putActiveFareRequestBooking(BA.getContext(),null);
			   getActivity().finish();
			}
			else{
				ToastUtil.display(getActivity(),event.getData().getMessage());
			}
		}else{
			ToastUtil.display(getActivity(),"Ошибка");
		}
	}

	FareRequest.BidObject acceptedBid;
	BidsAdapter adapter;
}