package com.driverspa.client.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.Reference;
import com.driverspa.fragment.BaseFragment;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.Washer;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.HttpClient;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.LocationRequestEvent;
import com.driverspa.util.otto.LocationResponseEvent;
import com.driverspa.util.otto.NearSearchEvent;
import com.driverspa.util.otto.ws.FilterGetResponseEvent;
import com.driverspa.util.otto.ws.FilterResponseEvent;
import com.driverspa.util.otto.ws.WashersNearResponseEvent;
import com.driverspa.view.MySupportMapFragment;

public class ClientMapFragment extends BaseFragment implements GoogleMap.OnMyLocationChangeListener,
												ClusterManager.OnClusterClickListener<WasherPublic>,
												ClusterManager.OnClusterItemClickListener<WasherPublic>,
												ClusterManager.OnClusterItemInfoWindowClickListener<WasherPublic>{
	
    public interface ActivityActions {
    	public void showProgressBar(boolean set);
    	public void showClusterItems(final List<WasherPublic> washers, final GoogleMap map, Location currentLoc);
    	public void openProfileWasher(String washerId);
    }        

    private ActivityActions activityActions;    
	private static View view;
	private static GoogleMap mMap;
	MarkerOptions myMarker = new MarkerOptions();
	private static ClusterManager<WasherPublic> mClusterManager;
	private Bitmap bMap;
	private Bitmap bMMap;
	Bitmap [] baseBitmaps = new Bitmap[1];		
	float density;
	DisplayMetrics metrics;
	private static final int ZOOM_LEVEL = 10;
	Location currentLoc;
	private SearchFilter filter = new SearchFilter();
	LatLngBounds bounds = null;
	WasherPublic clickedClusterItem;
	GPSTracker gps;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	
	    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker);
	    bMMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_my_marker);
	    baseBitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clustermarker);
	    metrics = getResources().getDisplayMetrics();
	    density = metrics.density;		   
	    gps = new GPSTracker(getActivity());
	    
	    if (container == null) {
	        return null;
	    }
	    view = (RelativeLayout) inflater.inflate(R.layout.fragment_map, container, false);
	
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
		ui.setZoomControlsEnabled(true);
		ui.setMyLocationButtonEnabled(false);
		ui.setAllGesturesEnabled(true);
	    mMap.setOnCameraChangeListener(mClusterManager);        
	    mMap.setOnMarkerClickListener(mClusterManager);
	    mMap.setOnInfoWindowClickListener(mClusterManager);
	    mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());        
	    mClusterManager.setOnClusterClickListener(this);
	    mClusterManager.setOnClusterItemClickListener(this);
	    mClusterManager.setOnClusterItemInfoWindowClickListener(this);
	    mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomAdapterForClusterItems());
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
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
	public void onMyLocationChange(Location location) {	
	    LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
	    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, ZOOM_LEVEL));
	    mMap.setOnMyLocationChangeListener(null);
	}    
	
	
	@Override
	public void onPause() {
		super.onPause();
//	    if (mMap != null) {
//        ClientMapActivity.fragmentManager.beginTransaction()
//            .remove(ClientMapActivity.fragmentManager.findFragmentById(R.id.mapThis)).commitAllowingStateLoss();
        mMap = null;
//      }
	    if(gps!=null)
	    	gps.stopUsingGPS();        
		BA.getEventBus().unregister(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();			
		BA.getEventBus().register(this);
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
        if(gpsSettingsShown){
			BA.getEventBus().post(new LocationRequestEvent());
			gpsSettingsShown = false;
		}
	}

	@Subscribe
	public void onLocationResponseEvent(LocationResponseEvent event){
		gps = new GPSTracker(getActivity());
		getLocationAndUpdateList();
	}
	/**
	 * Received location and request people nearby
	 * @param
	 */
//	@Subscribe
//	public void onLocationReceived(LocationResponseEvent event) {
//		currentLoc = event.getLocation();
//		myMarker.position(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude()))
//        .title("Это я!");
//		myMarker.icon(BitmapDescriptorFactory.fromBitmap(bMMap));
//		mMap.addMarker(myMarker);
//		activityActions.showProgressBar(true);
//		getLocationAndUpdateList();
//	}
	
	   public void getLocationAndUpdateList(){
		   if(gps.canGetLocation()){
			    activityActions.showProgressBar(true);
			   if(mMap != null)
   			      mMap.clear();
//			    mClusterManager.clearItems();
				currentLoc = gps.getLocation();
  			    loadData();
				if(currentLoc != null){
				  myMarker.position(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude())).title("Это я!");
					myMarker.icon(BitmapDescriptorFactory.fromBitmap(bMMap));
					mMap.addMarker(myMarker);

				}
				else{
					ToastUtil.display(getActivity(), "Не могу определить местоположение");
					activityActions.showProgressBar(false);		
				}
		   }
		   else{
			   activityActions.showProgressBar(false);
			   showSettingsAlert();
		   }	  
	   }

	  private void loadData() {
			if(currentLoc != null) {
				this.filter.setOffset("0");
				this.filter.setLimit("100000");
				if(!TextUtils.isEmpty(UserPreferences.getCity(getActivity())))
					this.filter.setCity(UserPreferences.getCity(getActivity()));
//				BA.getEventBus().post(new WashersNearRequestEvent(currentLoc.getLatitude(), currentLoc.getLongitude(), filter));
			}
			else
			  ToastUtil.display(getActivity(), "Не могу определить местоположение");
		}
	
	/**
	 * Received location and request people nearby
	 * @param event
	 */

	@Subscribe
	public void onFilterInitialReceived(FilterResponseEvent event){
		filter = event.getSearchFilter().clone();
		if(mMap != null) {
			activityActions.showProgressBar(true);
			getLocationAndUpdateList();
		}
	}

	@Subscribe
	public void onFilterReceived(FilterGetResponseEvent event){
		filter = event.getSearchFilter().clone();
		activityActions.showProgressBar(true);
		getLocationAndUpdateList();
	}

	@Subscribe
	public void onSearchTextSubmitted(NearSearchEvent event){
		this.filter.setSearchText(event.getQuery());
		activityActions.showProgressBar(true);
		getLocationAndUpdateList();
	}

	@Subscribe
	public void onNearWashersReceived(WashersNearResponseEvent event) {
	    activityActions.showProgressBar(false);
		if(event.getData().getResponse().getResult() != null){
			addMarkers(event.getData().getResponse().getResult());
		}
	}
	
	private void addMarkers(List<WasherPublic> washers){
		mMap.clear();
		mClusterManager.clearItems();
		if(currentLoc != null) {
			myMarker.position(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude())).title("Это я!");
			myMarker.icon(BitmapDescriptorFactory.fromBitmap(bMMap));
			mMap.addMarker(myMarker);
		}

		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		if (washers != null && washers.size() > 0) {			
		  for(WasherPublic model : washers){
			    builder.include(new LatLng(Double.parseDouble(model.getLonLat().get(1).toString()),Double.parseDouble(model.getLonLat().get(0).toString())));
				mClusterManager.addItem(model);				
			}
			bounds = builder.build();
			showBoundsOfMarkers(bounds);
			mClusterManager.cluster();
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
	
	private class WasherModelRenderer extends DefaultClusterRenderer<WasherPublic> {
		public WasherModelRenderer() {
			super(getActivity(), mMap, mClusterManager);
		}
	
		@Override
		protected void onBeforeClusterItemRendered(WasherPublic model,
				MarkerOptions markerOptions) {
				  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bMap));
	    }
	
	    @Override
	    protected void onBeforeClusterRendered(Cluster<WasherPublic> cluster, MarkerOptions markerOptions) {
	             markerOptions.icon(getClusterIcon(cluster.getSize()));
	    }
	
	    @Override
	    protected boolean shouldRenderAsCluster(Cluster cluster) {
	        // Always render clusters.
	        return cluster.getSize() > 1;
	    }
	}
	
	@Override
	public void onClusterItemInfoWindowClick(WasherPublic item) {
        activityActions.openProfileWasher(item.getId());		
	}
	
	@Override
	public boolean onClusterItemClick(WasherPublic item) {
		clickedClusterItem = item;
		return false;
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
		Bitmap base = baseBitmaps[0];
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
				layout = inflater.inflate(R.layout.custom_info_window_map,null);
				TextView name = (TextView) layout.findViewById(R.id.txtWasherNameTitle);
				TextView address = (TextView) layout.findViewById(R.id.txtWasherAddress);
				TextView price = (TextView) layout.findViewById(R.id.price);
				TextView city = (TextView) layout.findViewById(R.id.txtWasherCity);
				TextView phone = (TextView) layout.findViewById(R.id.txtWasherPhone);
				TextView time = (TextView) layout.findViewById(R.id.txtWasherTime);
				ImageView avatar = (ImageView) layout.findViewById(R.id.imgWasherAvatar);
				name.setText(clickedClusterItem.getName());
				address.setText(clickedClusterItem.getAddress());
				price.setText("от " + clickedClusterItem.getPrice() + " ₸");
				ArrayList<Reference.City> allCities = BA.getReference().getCities();
				city.setVisibility(View.GONE);
				for(Reference.City c:allCities){
					if(c.getCode().equals(clickedClusterItem.getCity())){
						city.setText(c.getTitle());
						break;
					}
				}
				if(clickedClusterItem.getContacts() != null && clickedClusterItem.getContacts().size() > 0){
					for(Washer.Contact contact : clickedClusterItem.getContacts())
						if(contact.getType().contains("phone"))
							phone.setText(contact.getValue());
				}

				HttpClient.getPicasso()
						.load(R.drawable.background_car)
						.fit()
						.centerCrop()
						.placeholder(R.drawable.background_car)
				        .into(avatar);

				if(clickedClusterItem.getSchedule() != null && clickedClusterItem.getSchedule().getComment() !=null && !clickedClusterItem.getSchedule().getComment().equals(""))
				time.setText(clickedClusterItem.getSchedule().getComment());

//				WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//				Display display = wm.getDefaultDisplay();
//
//				int height = display.getHeight(); //(I put here a "*2/3" too to not fill the whole screen, but this is your choice);
//				LinearLayout infoWindowSubLayout = (LinearLayout) layout.findViewById(R.id.infoWindowLayout);
//				android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(height,LinearLayout.LayoutParams.MATCH_PARENT);
//				infoWindowSubLayout.setLayoutParams(lp);
			}
			return layout;
		}
	}    

}