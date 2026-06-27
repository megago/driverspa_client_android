package com.driverspa.client.fragment;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.OvershootInterpolator;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.LruCache;
import androidx.core.app.ActivityCompat;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.activity.CityChooseActivity;
import com.driverspa.assist.BaseAssist;
import com.driverspa.client.activity.ClientHomeActivity;
import com.driverspa.model.BookInfo;
import com.driverspa.model.FareRequest;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.LocationResponseEvent;
import com.driverspa.util.otto.NearSearchEvent;
import com.driverspa.util.otto.WantedWashersResponseEvent;
import com.driverspa.util.otto.ws.BooksRequestEvent;
import com.driverspa.util.otto.ws.BooksResponseEvent;
import com.driverspa.util.otto.ws.FareRequestsRequestEvent;
import com.driverspa.util.otto.ws.FareRequestsResponseEvent;
import com.driverspa.util.otto.ws.FilterGetResponseEvent;
import com.driverspa.util.otto.ws.FilterResponseEvent;
import com.driverspa.util.otto.ws.WashersNearMapRequestEvent;
import com.driverspa.util.otto.ws.WashersNearMapResponseEvent;
import com.driverspa.view.MySupportMapFragment;

import com.squareup.otto.Subscribe;

import static com.driverspa.util.Constants.APPROVED;
import static com.driverspa.util.Constants.PENDING;
import static com.driverspa.util.Constants.QUEUED;
import static com.driverspa.util.Constants.QUEUED_APPROVED;

public class ClientMapInTabFragment extends ClientBaseHomeFragment implements ClusterManager.OnClusterClickListener<WasherPublic>,
		ClusterManager.OnClusterItemClickListener<WasherPublic>,
		ClusterManager.OnClusterItemInfoWindowClickListener<WasherPublic> {

	@Override
	public int getTitleResourceId() {
		return 0;
	}
	@Override
	public void load() {}

	public interface ActivityActions {
		public void showProgressBar(boolean set);

		public void showClusterItems(final List<WasherPublic> washers, final GoogleMap map, Location currentLoc);

		public void showClusterItem(final WasherPublic washer, final GoogleMap map, Location currentLoc);

		public void openProfileWasher(String washerId);

		public void openBookInfo(String bookId);

		public void openFareRequestInfo(String fareRequestId, FareRequest fareRequest);

		public void openActiveBookInfo(String bookId);

		public void hideShowFilterButton(int tab);
	}

	private ActivityActions activityActions;
	private static View view;
	private static GoogleMap mMap;
	private static ClusterManager<WasherPublic> mClusterManager;
	private Bitmap bMap;
	private Bitmap bMapPartner;
	private Bitmap bMapPartnerDiscount;
	private Bitmap bMMap;
	Bitmap[] baseBitmaps = new Bitmap[1];
	float density;
	DisplayMetrics metrics;
	private static final int ZOOM_LEVEL = 14;
	Location currentLoc;
	private SearchFilter filter = new SearchFilter();
	LatLngBounds bounds = null;
	WasherPublic clickedClusterItem;
	GPSTracker gps;
	List<WasherPublic> washers;
	boolean fragmentVisible;
	boolean useNoGPSOption = false;
	Marker myMarker;
	Toolbar toolbar;
	TextView titleView;
	String localCity;
	ProgressBar progress;
	BitmapDescriptor infoBitmapDescriptor;
	BitmapDescriptor partnerBitmapDescriptor;
	BitmapDescriptor partnerDiscountBitmapDescriptor;
	HashMap<String, String> wantedWashers;
	private LruCache<String, Bitmap> mMemoryCache;

	private WasherModelRenderer mRenderer;
	private Marker selectedMarker;
	private WasherPublic selectedItem;
	private ValueAnimator markerAnimator;
	private static final float SELECTED_MARKER_SCALE = 1.6f;
	private static final int SELECTED_MARKER_DARKEN_ALPHA = 90; // ~35% black tint

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkLocationPermission();
		MapsInitializer.initialize(getActivity().getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		BA.getEventBus().post(new FareRequestsRequestEvent(PENDING));
		SearchFilter ff = new SearchFilter(false);
		ff.setLimit(Integer.toString(1));
		BA.getEventBus().post(new BooksRequestEvent(ff));

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

		localCity = UserPreferences.getCity(BA.getContext());
//		filter.setMobile(UserPreferences.getUserPhone(BA.getContext()));
		bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker);
		bMapPartner = BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_partner);
		bMapPartnerDiscount = BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_partner_discount);
		bMMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_client_map);
		baseBitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clustermarker);

		addBitmapToMemoryCache("info", bMap);
		addBitmapToMemoryCache("partner", bMapPartner);
		addBitmapToMemoryCache("partnerDiscount", bMapPartnerDiscount);
		addBitmapToMemoryCache("mine", bMMap);
		addBitmapToMemoryCache("cluster", baseBitmaps[0]);

		metrics = getResources().getDisplayMetrics();

		density = metrics.density;
		toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		titleView = (TextView) toolbar.findViewById(R.id.action_bar_title);
//		titleView.setVisibility(View.GONE);
		progress = (ProgressBar) toolbar.findViewById(R.id.progress);

		if (container == null) {
			return null;
		}

		view = (FrameLayout) inflater.inflate(R.layout.fragment_map_intab, container, false);
		view.findViewById(R.id.btnMapMyLocationCenter).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCenterButtonClicked();
				((ClientHomeActivity) getActivity()).hideClusterItem();

			}
		});
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
					setUpMap();
				}
			});
		}
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public void onCenterButtonClicked() {
		if (mMap != null && currentLoc != null) {
			LatLng l = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, ZOOM_LEVEL));
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap() {
		mMap.setOnMapClickListener(latLng -> {
			resetSelectedMarker();
			((ClientHomeActivity) getActivity()).hideClusterItem();
		});

		mClusterManager = new ClusterManager<>(getActivity(), mMap);
		//mClusterManager.setRenderer(new WasherModelRenderer(getActivity(), mMap, mClusterManager));
		mRenderer = new WasherModelRenderer();
		mClusterManager.setRenderer(mRenderer);

		// Map UI settings
		UiSettings ui = mMap.getUiSettings();
		ui.setCompassEnabled(false);
		ui.setZoomControlsEnabled(false);
		ui.setMapToolbarEnabled(false);
		ui.setMyLocationButtonEnabled(false);
		ui.setAllGesturesEnabled(true);

		// Camera change listeners - modern way
		mMap.setOnCameraIdleListener(mClusterManager);
		mMap.setOnMarkerClickListener(mClusterManager);
		mMap.setOnInfoWindowClickListener(mClusterManager);
		mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

		mMap.setPadding(0, 0, 0, 100);

		// Cluster item interaction callbacks
		mClusterManager.setOnClusterClickListener(this);
		mClusterManager.setOnClusterItemClickListener(this);
		mClusterManager.setOnClusterItemInfoWindowClickListener(this);

		// Add markers or fetch data
		if (washers != null && !washers.isEmpty()) {
			addMarkers(washers);
		} else {
			if (gps != null && gps.canGetLocation()) {
				currentLoc = gps.getLocation();
			}
			if (mMap != null) {
				activityActions.showProgressBar(true);
				getLocationAndUpdateListWithoutClear();
			}
		}

		// Optional default camera move
		// mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.1801, 71.44598), 14.0f));
	}


	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

	public boolean checkLocationPermission() {
		if (checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			requestPermissions(
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					MY_PERMISSIONS_REQUEST_LOCATION);

			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission was granted, yay! Do the
					// location-related task you need to do.
					if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {

						if(gps == null)
							gps = new GPSTracker(getActivity());
						if(!gps.canGetLocation()){
							gps.showSettingsAlert();
						}
						if(TextUtils.isEmpty(localCity)){
							localCity = UserPreferences.getCity(BA.getContext());
							getLocationAndUpdateListWithoutClear();
						}
						if (mMap == null) {
							((MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
								@Override
								public void onMapReady(GoogleMap googleMap) {
									mMap = googleMap;
									setUpMap();
//                                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                        // TODO: Consider calling
//                                        //    ActivityCompat#requestPermissions
//                                        // here to request the missing permissions, and then overriding
//                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                        //                                          int[] grantResults)
//                                        // to handle the case where the user grants the permission. See the documentation
//                                        // for ActivityCompat#requestPermissions for more details.
//                                        return;
//                                    }
									//TODO Come here
                                    //mMap.setMyLocationEnabled(true);
								}
							});
						}
					}

				} else {
					// permission denied, boo! Disable the
					// request it again
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
							MY_PERMISSIONS_REQUEST_LOCATION);
				}
				return;
			}
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
//
//	    if (mMap == null) {
//	        // Try to obtain the map from the SupportMapFragment.
//			((MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
//				@Override
//				public void onMapReady(GoogleMap googleMap) {
//					mMap = googleMap;
//					setUpMap();
//				}
//			});
//	    }

		infoBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache("info"));
		partnerBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache("partner"));
		partnerDiscountBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache("partnerDiscount"));
	}
	
	/**** The mapfragment's id must be removed from the FragmentManager
	 **** or else if the same it is passed on the next time then 
	 **** app will crash ****/
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    if(gps!=null)
	    	gps.stopUsingGPS();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(progress!=null)
		   progress.setVisibility(View.GONE);

		if (checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			mMap = null;

			if (gps != null)
				gps.stopUsingGPS();
		}
		BA.getEventBus().unregister(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {

			if(gps == null)
				gps = new GPSTracker(getActivity());
			if(!gps.canGetLocation()){
				gps.showSettingsAlert();
			}

			if(TextUtils.isEmpty(localCity)){
				localCity = UserPreferences.getCity(BA.getContext());
				getLocationAndUpdateListWithoutClear();
			}
			if (mMap == null) {
				((MySupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapThis)).getMapAsync(new OnMapReadyCallback() {
					@Override
					public void onMapReady(GoogleMap googleMap) {
						mMap = googleMap;
						setUpMap();
//                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
						//TODO COme here
//                        mMap.setMyLocationEnabled(true);
					}
				});
			}
		}

		BA.getEventBus().register(this);
	}

	@Subscribe
	public void onLocationResponseEvent(LocationResponseEvent event){
		currentLoc = event.getLocation();
		if(myMarker != null){
			myMarker.remove();
		}

		myMarker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(event.getLocation().getLatitude(),event.getLocation().getLongitude()))
				.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_client_map)))
				.title(null));
	}

	public void getLocationAndUpdateList(){
		   if(gps != null && gps.canGetLocation()){
			    activityActions.showProgressBar(true);
			   if(mMap != null)
   			      mMap.clear();
				currentLoc = gps.getLocation();
  			    loadData();
		   }
		   else {
//			   gps.showSettingsAlert();
		   }
	   }

	public void getLocationAndUpdateListWithoutClear(){
		if(gps != null && gps.canGetLocation()){
			activityActions.showProgressBar(true);
			currentLoc = gps.getLocation();
			loadData();
		}
		else {
//			gps.showSettingsAlert();
		}
	 }

	  private void loadData() {
		  if(fragmentVisible && progress!=null) progress.setVisibility(View.VISIBLE);
		  this.filter.setOffset("0");
		  this.filter.setLimit("200");
		  mapCameraAdjust();
		  String localCity = UserPreferences.getCity(BA.getContext());
		  if(currentLoc != null) {
		  String foundCity = CityChooseActivity.findNearestCity(currentLoc);
		  if (!TextUtils.isEmpty(localCity)&&!TextUtils.isEmpty(foundCity) && localCity.equals(foundCity) && filter.isMapDefaultValues()) {
				 String location = String.format(Locale.US, "%f,%f", currentLoc.getLongitude(), currentLoc.getLatitude());
				 this.filter.setCity(localCity);
//				 filter.setLonLat(location);
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
	
	/**
	 * Received location and request people nearby
	 * @param event
	 */

	@Subscribe
	public void onFilterInitialReceived(FilterResponseEvent event){
		filter = event.getSearchFilter().clone();
	}

	@Subscribe
	public void onFilterReceived(FilterGetResponseEvent event){
		localCity = UserPreferences.getCity(BA.getContext());
		filter = event.getSearchFilter().clone();
		activityActions.showProgressBar(true);
		getLocationAndUpdateListWithoutClear();
	}

	@Subscribe
	public void onSearchTextSubmitted(NearSearchEvent event){
		this.filter.setSearchText(event.getQuery());
		activityActions.showProgressBar(true);
		getLocationAndUpdateListWithoutClear();
	}

	@Subscribe
	public void onNearMapWashersReceived(WashersNearMapResponseEvent event) {
		if(progress!=null) progress.setVisibility(View.GONE);
	    activityActions.showProgressBar(false);
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

		mapCameraAdjust();

		if(!TextUtils.isEmpty(activeBookingId)){
			activityActions.openActiveBookInfo(activeBookingId);
		}
		else if(!TextUtils.isEmpty(activeFareRequestId)){
			activityActions.openFareRequestInfo(activeFareRequestId, fareRequest);
		}

	}

	public void mapCameraAdjust(){
		boolean isCityFoundByGPS = UserPreferences.isCityFoundByGPS(BA.getContext());
		String localCity = UserPreferences.getCity(BA.getContext());

		if(currentLoc == null) {
			if(gps != null && gps.canGetLocation()){
				currentLoc = gps.getLocation();
			}
		}
		if(TextUtils.isEmpty(localCity) || mMap == null)
			return;
		if(currentLoc != null) {
			if(myMarker != null){
				myMarker.remove();
			}
			if(mMap != null) {
				myMarker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude()))
						.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_client_map)))
						.title(null));
			}

			String foundCity = CityChooseActivity.findNearestCity(currentLoc);
			if (!TextUtils.isEmpty(foundCity) && localCity.equals(foundCity) && filter.isMapDefaultValues()) {
				LatLng l = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, ZOOM_LEVEL));
			} else {
				LatLng l = Functions.getCityLatLng(localCity);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 12));
			}
		}
		else{
			LatLng l = Functions.getCityLatLng(localCity);
			try {
				if (l != null)
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 12));
			}
			catch(Exception e){}
		}
	}
	
	private void addMarkers(List<WasherPublic> washers){
		if(mMap != null) {
			// Markers are about to be cleared/recreated; drop any selection state.
			if (markerAnimator != null) markerAnimator.cancel();
			selectedMarker = null;
			selectedItem = null;
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

			if(currentLoc != null) {
				if (myMarker != null) {
					myMarker.remove();
				}
				myMarker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude()))
						.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_client_map)))
						.title(null));
			}
		}
	}


	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);			
		activityActions = (ActivityActions) activity;
		if(fragmentVisible)
			activityActions.hideShowFilterButton(3);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activityActions = null;
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
	    protected void onClusterItemRendered(WasherPublic clusterItem, Marker marker) {
	        // Re-apply the selected (enlarged + darkened) icon if this item is the
	        // currently selected one, so panning/re-rendering doesn't reset it.
	        if (selectedItem != null && clusterItem != null && clusterItem.getId() != null
	                && clusterItem.getId().equals(selectedItem.getId())) {
	            selectedMarker = marker;
	            Bitmap base = baseBitmapForItem(clusterItem);
	            if (base != null) {
	                marker.setIcon(BitmapDescriptorFactory.fromBitmap(
	                        makeSelectedBitmap(base, SELECTED_MARKER_SCALE, SELECTED_MARKER_DARKEN_ALPHA)));
	            }
	            marker.setZIndex(10f);
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
        activityActions.openProfileWasher(item.getId());		
	}
	
	/** Highlight the tapped carwash marker: enlarge it with a pop animation and a dark tint. */
	private void selectMarker(final WasherPublic item) {
		// Restore any previously selected marker first.
		if (selectedMarker != null && selectedItem != null && (selectedItem.getId() == null || !selectedItem.getId().equals(item.getId()))) {
			try {
				selectedMarker.setIcon(normalDescriptorForItem(selectedItem));
				selectedMarker.setZIndex(0f);
			} catch (Exception ignored) {}
		}
		if (markerAnimator != null) {
			markerAnimator.cancel();
		}

		selectedItem = item;
		final Marker marker = (mRenderer != null) ? mRenderer.getMarker(item) : null;
		selectedMarker = marker;
		final Bitmap base = baseBitmapForItem(item);
		if (marker == null || base == null) {
			return;
		}
		marker.setZIndex(10f);

		markerAnimator = ValueAnimator.ofFloat(1.0f, SELECTED_MARKER_SCALE);
		markerAnimator.setDuration(240);
		markerAnimator.setInterpolator(new OvershootInterpolator(2.5f));
		markerAnimator.addUpdateListener(animation -> {
			float scale = (float) animation.getAnimatedValue();
			try {
				marker.setIcon(BitmapDescriptorFactory.fromBitmap(
						makeSelectedBitmap(base, scale, SELECTED_MARKER_DARKEN_ALPHA)));
			} catch (Exception ignored) {}
		});
		markerAnimator.start();
	}

	/** Restore the currently selected marker back to its normal icon. */
	public void resetSelectedMarker() {
		if (markerAnimator != null) {
			markerAnimator.cancel();
		}
		if (selectedMarker != null && selectedItem != null) {
			try {
				selectedMarker.setIcon(normalDescriptorForItem(selectedItem));
				selectedMarker.setZIndex(0f);
			} catch (Exception ignored) {}
		}
		selectedMarker = null;
		selectedItem = null;
	}

	private Bitmap baseBitmapForItem(WasherPublic item) {
		if (item.getStatus() != null && item.getStatus().equals("information"))
			return getBitmapFromMemCache("info");
		if (item.getActiveCampaign() != null)
			return getBitmapFromMemCache("partnerDiscount");
		return getBitmapFromMemCache("partner");
	}

	private BitmapDescriptor normalDescriptorForItem(WasherPublic item) {
		if (item.getStatus() != null && item.getStatus().equals("information"))
			return infoBitmapDescriptor;
		if (item.getActiveCampaign() != null)
			return partnerDiscountBitmapDescriptor;
		return partnerBitmapDescriptor;
	}

	/** Build a scaled-up, darkened copy of the pin to mark it as selected. */
	private Bitmap makeSelectedBitmap(Bitmap base, float scale, int darkenAlpha) {
		int w = Math.max(1, Math.round(base.getWidth() * scale));
		int h = Math.max(1, Math.round(base.getHeight() * scale));
		Bitmap scaled = Bitmap.createScaledBitmap(base, w, h, true);
		Bitmap result = scaled.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// SRC_ATOP tints only the opaque pin pixels, leaving the transparent area clear.
		paint.setColorFilter(new PorterDuffColorFilter(Color.argb(darkenAlpha, 0, 0, 0), PorterDuff.Mode.SRC_ATOP));
		canvas.drawBitmap(scaled, 0, 0, paint);
		return result;
	}

	@Override
	public boolean onClusterItemClick(final WasherPublic item) {
		clickedClusterItem = item;
		selectMarker(item);
		final int dX = getResources().getDimensionPixelSize(R.dimen.map_dx);
		// Calculate required vertical shift for current screen density
		int dY = getResources().getDimensionPixelSize(R.dimen.map_dy_short);
		if(item.isBookable() && !item.getStatus().equals("information") && (item.getSchedule() != null)) {
			dY = getResources().getDimensionPixelSize(R.dimen.map_dy);
		}
		if(item.getUserWanted()) {
			if(item.getWantsCount() > 0)
				dY = getResources().getDimensionPixelSize(R.dimen.map_dy_shorter);
		}
		else if(wantedWashers !=null && wantedWashers.get(item.getId())!=null){
			dY = getResources().getDimensionPixelSize(R.dimen.map_dy_shortest);
		}

		final Projection projection = mMap.getProjection();
		final Point markerPoint = projection.toScreenLocation(
				item.getPosition()
		);
		// Shift the point we will use to center the map
		markerPoint.offset(dX, dY);
		final LatLng newLatLng = projection.fromScreenLocation(markerPoint);
		mMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng),200,null);
		Handler handler = new Handler();

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				activityActions.showClusterItem(item, mMap, currentLoc);
			}}, 200);
		return true;
	}
	
	@Override
	public boolean onClusterClick(Cluster<WasherPublic> cluster) {
		activityActions.showClusterItems((List)cluster.getItems(),mMap,currentLoc);
		return false;
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
    
    class CustomAdapterForClusterItems implements InfoWindowAdapter {
	    @Override
	    public View getInfoContents(Marker marker) {
	          return null;
	    }

		@Override
		public View getInfoWindow(Marker arg0) {
			View layout = null;
			if (clickedClusterItem != null) {
				ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity(), R.style.TransparentBackground);
				LayoutInflater inflater = (LayoutInflater) wrapper.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
				layout = inflater.inflate(R.layout.custom_info_window_map_in_tab,null);
				TextView name = (TextView) layout.findViewById(R.id.infoWindowTitle);
				TextView address = (TextView) layout.findViewById(R.id.infoWindowAddress);
				name.setText(clickedClusterItem.getName().toLowerCase().contains("автомойка")?clickedClusterItem.getName():"Автомойка "+clickedClusterItem.getName());
				address.setText(clickedClusterItem.getAddress());
			}
			return layout;
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		localCity = UserPreferences.getCity(BA.getContext());
		fragmentVisible = isVisibleToUser;
		if(activityActions != null){
			activityActions.hideShowFilterButton(3);
		}
		if(fragmentVisible){
			if((!(washers != null && washers.size() > 0) && gps != null)){
				getLocationAndUpdateListWithoutClear();
			}
//			if(titleView != null)
//			   titleView.setText(Functions.getCityDescription(localCity));

		}
		else
		   if(progress != null) progress.setVisibility(View.GONE);
	}

	@Subscribe
	public void onBooksReceived(BooksResponseEvent event) {
		activeBookingId = null;
		if(event.getData() != null){
			if(BaseAssist.isSuccess(event.getData())) {
				if (event.getData().getResponse().getBookInfoList() != null) { // if
					for(BookInfo item : event.getData().getResponse().getBookInfoList()){
						if(item.getStatus().equals(PENDING) || item.getStatus().equals(APPROVED) ||  item.getStatus().equals(QUEUED_APPROVED) || item.getStatus().equals(QUEUED)) {
//                            activityActions.openActiveBookInfo(item.getId());
							activeBookingId = item.getId();
							break;
						}
					}
				}
			}
		}
	}

    @Subscribe
    public void onFareRequestsReceived(FareRequestsResponseEvent event) {
		activeFareRequestId = null;
		fareRequest = null;
        if (event.getData() != null) {
            if (BaseAssist.isSuccess(event.getData())) {
                if (event.getData().getResponse().getFareRequests() != null) { // if
                    for(FareRequest item : event.getData().getResponse().getFareRequests()){
                        activeFareRequestId = item.getId();
						fareRequest = item;
                        break;
                    }
                }
            }
        }
    }

	@Subscribe
	public void onWantedWashersReceived(WantedWashersResponseEvent event){
		if(event!=null && event.getWantedWashers() != null)
			wantedWashers = event.getWantedWashers();
	}

	String activeBookingId;
    String activeFareRequestId;
	FareRequest fareRequest;
}