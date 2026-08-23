package com.driverspa.client.activity;

import android.annotation.SuppressLint;
import butterknife.BindView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.splunk.mint.Mint;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.dialog.LanguageDialog;
import com.driverspa.activity.CityChooseActivity;
import com.driverspa.activity.LoginActivity;
import com.driverspa.adapter.ClientTabPagerAdapter;
import com.driverspa.adapter.WasherListAdapter;
import com.driverspa.assist.BaseAssist;
import com.driverspa.client.fragment.ClientAvailableWashersFragment;
import com.driverspa.client.fragment.ClientBaseHomeFragment;
import com.driverspa.client.fragment.ClientBooksFragment;
import com.driverspa.client.fragment.ClientFavouriteWashersFragment;
import com.driverspa.client.fragment.ClientFilterFragment;
import com.driverspa.client.fragment.ClientMapInTabFragment;
import com.driverspa.client.fragment.ClientNearByWashersFragment;
import com.driverspa.client.fragment.ClientNotificationFragment;
import com.driverspa.client.fragment.ClientPromoWashersFragment;
import com.driverspa.client.fragment.ClientServiceDialogFragment;
import com.driverspa.client.fragment.ClientSettingsFragment;
import com.driverspa.client.fragment.ClientTimetableDialogFragment;
import com.driverspa.client.fragment.ClientWasherCampaignsFragment;
import com.driverspa.client.fragment.ClientWasherInfoFragment;
import com.driverspa.model.BookInfo;
import com.driverspa.model.CarItem;
import com.driverspa.model.CurrentGeoPosition;
import com.driverspa.model.FareRequest;
import com.driverspa.model.ImageDetail;
import com.driverspa.model.NotificationType;
import com.driverspa.model.PushData;
import com.driverspa.model.ReversePrices;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.ServiceItem;
import com.driverspa.model.User;
import com.driverspa.model.WantedWashers;
import com.driverspa.model.Washer;
import com.driverspa.model.WasherPublic;
import com.driverspa.model.api.request.AddReviewRequest;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.navigation.client.NavigationDrawerCallbacks;
import com.driverspa.navigation.client.NavigationDrawerFragment;
import com.driverspa.util.Constants;
import com.driverspa.util.Functions;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.HttpClient;
import com.driverspa.util.RetrofitClient;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.AvailableSearchEvent;
import com.driverspa.util.otto.CollapseRequestFields;
import com.driverspa.util.otto.ExpandRequestFields;
import com.driverspa.util.otto.FavouriteSearchEvent;
import com.driverspa.util.otto.NearSearchEvent;
import com.driverspa.util.otto.NewBookingPushResponseEvent;
import com.driverspa.util.otto.WantedWashersResponseEvent;
import com.driverspa.util.otto.ws.AddReviewRequestEvent;
import com.driverspa.util.otto.ws.BookRequestEvent;
import com.driverspa.util.otto.ws.BookResponseEvent;
import com.driverspa.util.otto.ws.FareRequestEvent;
import com.driverspa.util.otto.ws.FareRequestResponseEvent;
import com.driverspa.util.otto.ws.FilterGetResponseEvent;
import com.driverspa.util.otto.ws.FilterResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;
import com.driverspa.view.CustomViewPager;
import com.driverspa.view.NDSpinner;

import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;
import static com.driverspa.util.Constants.EXTRA_FARE_REQUEST_ID;
import static com.driverspa.util.Constants.EXTRA_WASHER_ID;
import static com.driverspa.util.Constants.PUSH_DATA;
import static com.driverspa.util.Constants.PUSH_TYPE;
import static com.driverspa.util.Constants.URL_PREFIX;
import static com.driverspa.util.Constants.WASHER_BOOK_FROM_MAP;
import static com.driverspa.util.Constants.WASHER_ID;

/**
 * Client home activity.
 *
 * @author Yerzhan
 */

@SuppressLint("NewApi")
public class ClientHomeActivity extends ClientBaseActivity implements ClientNearByWashersFragment.ActivityActions,
        ClientAvailableWashersFragment.ActivityActions,
        ClientFavouriteWashersFragment.ActivityActions,
        ClientPromoWashersFragment.ActivityActions,
        NavigationDrawerCallbacks,
        SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener,
        ClientBooksFragment.ActivityActions,
        ClientSettingsFragment.ActivityActions,
        ClientNotificationFragment.ActivityActions,
        ClientFilterFragment.ActivityActions,
        ClientMapInTabFragment.ActivityActions,
        ClientWasherCampaignsFragment.ActivityActions,
        ClientServiceDialogFragment.ActivityActions,
        ClientTimetableDialogFragment.ActivityActions{

    private ClientTabPagerAdapter TabAdapter;
    @BindView(R.id.pager)
    CustomViewPager pagerTab;
    int selectedTab = 0;

    @BindView(R.id.toolbar_actionbar)
    View toolbarActionBar;
    @BindView(R.id.container)
    View homeContainer;
    @BindView(R.id.fragment_container)
    View fragmentContainer;
    TextView titleView;

    HashMap<String,PushData> bookingPushData;
    Dialog dialog;

    //Bottom layout views
    @BindView(R.id.imgAvailability)
    View imgAvailability;
    @BindView(R.id.txtWasherTime)
    TextView washerTime;
    @BindView(R.id.imgWasherAvatar)
    ImageView washerImage;
    @BindView(R.id.txtWasherNameTitle)
    TextView name;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.txtWasherCity)
    TextView address;
    @BindView(R.id.reviewCount)
    TextView reviewCount;
    @BindView(R.id.txtWasherPhone)
    TextView washerPhone;
    @BindView(R.id.txtWasherComment)
    TextView comment;
    @BindView(R.id.review)
    RatingBar review;
    @BindView(R.id.btnMain)
    Button mainButton;
    @BindView(R.id.washerWindow)
    View washerWindow;
    @BindView(R.id.requestWindow)
    View requestWindow;
    @BindView(R.id.mainContainer)
    View mainContainer;
    @BindView(R.id.mainLayout)
    View mainLayout;
    @BindView(R.id.fareLine)
    View fareLine;
    @BindView(R.id.mainFieldsLayout)
    View mainFieldsLayout;
    @BindView(R.id.informationLayout)
    View informationLayout;
    @BindView(R.id.commentLayout)
    View commentLayout;
    @BindView(R.id.btnRequest)
    Button requestButton;
    @BindView(R.id.fare)
    EditText fare;
    @BindView(R.id.comment)
    EditText fareComment;
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
    @BindView(R.id.fareLayout)
    View fareLayout;
    @BindView(R.id.bookTime)
    TextView bookTime;
    @BindView(R.id.bookTimeLayout)
    View bookTimeLayout;
    @BindView(R.id.bookService)
    View bookService;
    @BindView(R.id.washerDetail)
    View washerDetail;
    @BindView(R.id.bookServiceClose)
    View bookServiceClose;
    @BindView(R.id.bookTimeClose)
    View bookTimeClose;
    @BindView(R.id.bookServiceRightArrow)
    View bookServiceRightArrow;
    @BindView(R.id.bookTimeRightArrow)
    View bookTimeRightArrow;
    @BindView(R.id.btnRequireWashme)
    Button requireWashmeButton;
    @BindView(R.id.reqireWashmeText)
    TextView reqireWashmeText;
    HashMap<String, String> wantedWashers;

    private boolean isSpinnerTouched = false;
    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    //Search menu
    Menu menu;
    User user;
    boolean isSearchable = false;
    String innerQuery = "";
    MenuItem searchMenuItem;
    MenuItem filterMenuItem;
    MenuItem filteredMenuItem;
    private SearchView mSearchView;
    String localCity = UserPreferences.getCity(BA.getContext());
    private int selectedMenu;
    ClientFilterFragment filterDialogFragment;
    ClientServiceDialogFragment serviceDialogFragment;
    ClientTimetableDialogFragment timetableDialogFragment;
    long lastSearchTime = (Calendar.getInstance()).getTimeInMillis();
    DecimalFormat formatter = new DecimalFormat("###.##");
    GPSTracker gps;
    public static final int UP = 0;
    public static final int DOWN = 1;
    int expandButtonPosition = UP;
    boolean loggedIn = false;
    boolean filtered = false;
    BookingRequest request;
    FareRequest fareRequest;
    SearchFilter filter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Mint.initAndStartSession(this.getApplication(), "b054ddc0");
        Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
        setContentView(R.layout.activity_client_home);
        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        gps = new GPSTracker(ClientHomeActivity.this);

        setSupportActionBar(mToolbar);
        boolean isCityFoundByGPS = UserPreferences.isCityFoundByGPS(BA.getContext());
        localCity = UserPreferences.getCity(BA.getContext());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, findViewById(R.id.drawer), mToolbar);
        titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
        titleView.setText(Functions.getCityDescription(localCity));

        carSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });

        serviceSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });

        TabAdapter = new ClientTabPagerAdapter(getSupportFragmentManager());
        pagerTab.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectedTab = position;
                setTabButtons(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        pagerTab.setOffscreenPageLimit(5);
        pagerTab.setAdapter(TabAdapter);
        pagerTab.setCurrentItem(selectedTab);
        setTabButtons(selectedTab);

        String pushType = getIntent().getStringExtra(PUSH_TYPE);
        if(!TextUtils.isEmpty(pushType)){
            if(pushType.equals("carwash_info")){
               selectedTab = 0;
               setTabButtons(selectedTab);
               pagerTab.setCurrentItem(selectedTab);
               String washerId = getIntent().getStringExtra(EXTRA_WASHER_ID);
               String pushData = getIntent().getStringExtra(PUSH_DATA);
               if(!TextUtils.isEmpty(washerId))
                   openProfileWasher(washerId,pushData);
            }
             //TODO come here and redirect it to notification page
            else if(pushType.contains("info")){
                selectedTab = 3;
                setTabButtons(selectedTab);
                pagerTab.setCurrentItem(selectedTab);
            }
            else if(pushType.contains("book") || pushType.contains("serving") || pushType.contains("express")){
                selectedTab = 3;
                setTabButtons(selectedTab);
                pagerTab.setCurrentItem(selectedTab);
                String bookId = getIntent().getStringExtra(EXTRA_BOOKING_ID);
                if(!TextUtils.isEmpty(bookId))
                    openBookInfo(bookId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;

        switch (selectedTab){
            case 0: //Washer list
                getMenuInflater().inflate(R.menu.menu_main, menu);
                searchMenuItem = menu.findItem(R.id.action_search);
                filterMenuItem = menu.findItem(R.id.action_filter);
                filteredMenuItem = menu.findItem(R.id.action_filtered);
                if(filtered) {
                    filteredMenuItem.setVisible(true);
                    filterMenuItem.setVisible(false);
                }
                else{
                    filteredMenuItem.setVisible(false);
                    filterMenuItem.setVisible(true);
                }
                menu.findItem(R.id.action_map).setVisible(false);
                menu.findItem(R.id.action_list).setVisible(true);
                mSearchView = (SearchView) searchMenuItem.getActionView();
                mSearchView.setOnQueryTextListener(this);
                styleSearchViewWhite(mSearchView);
                MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
                break;
            case 1: //Map
                getMenuInflater().inflate(R.menu.menu_main, menu);
                searchMenuItem = menu.findItem(R.id.action_search);
                filterMenuItem = menu.findItem(R.id.action_filter);
                filteredMenuItem = menu.findItem(R.id.action_filtered);
                if(filtered) {
                    filteredMenuItem.setVisible(true);
                    filterMenuItem.setVisible(false);
                }
                else{
                    filteredMenuItem.setVisible(false);
                    filterMenuItem.setVisible(true);
                }
                menu.findItem(R.id.action_map).setVisible(true);
                menu.findItem(R.id.action_list).setVisible(false);
                mSearchView = (SearchView) searchMenuItem.getActionView();
                mSearchView.setOnQueryTextListener(this);
                styleSearchViewWhite(mSearchView);
                MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
                break;
            case 2: //Books
                getMenuInflater().inflate(R.menu.menu_main_empty, menu);
                break;
            case 3: //Notifications
                getMenuInflater().inflate(R.menu.menu_main_empty, menu);
                break;
            case 4: //Favourite
                getMenuInflater().inflate(R.menu.menu_main_empty, menu);
                break;
        }

        if(filter != null && filteredMenuItem != null) {
            localCity = UserPreferences.getCity(BA.getContext());
            if (titleView != null) titleView.setText(Functions.getCityDescription(localCity));
            if (!filter.isMapDefaultValues()) {
                filtered = true;
                filteredMenuItem.setVisible(true);
                filterMenuItem.setVisible(false);
            } else {
                filtered = false;
                filteredMenuItem.setVisible(false);
                filterMenuItem.setVisible(true);
            }
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                onFilterClicked();
                break;
            case R.id.action_filtered:
                onFilterClicked();
                break;
            case R.id.action_map:
                pagerTab.setCurrentItem(0);
                showRequestItem();
                break;
            case R.id.action_list:
                pagerTab.setCurrentItem(1);
                hideClusterItem();
                hideRequestItem();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        setWaitScreen(false);
        BA.setNotificationType(NotificationType.HOME);
        BA.getEventBus().register(this);
        super.onResume();
        localCity = UserPreferences.getCity(BA.getContext());
        if(TextUtils.isEmpty(localCity)){
            showChooseCity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        BA.setNotificationType(NotificationType.NOTHING);
        BA.getEventBus().unregister(this);
    }

    @Subscribe
    public void onUserReceived(UserGetSelfResponseEvent event) {
        loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
        fareRequest = new FareRequest();
        createDefaultServicesForRequest();
        if (event != null && loggedIn && event.getUser() != null) {
            user = event.getUser();
            createCarsSpinner(user);
        }
        else{
            myCarText.setVisibility(View.VISIBLE);
            carSpinner.setVisibility(View.GONE);
        }
    }

    private void setTabButtons(int position){
        this.invalidateOptionsMenu();
    }

    private void createCarsSpinner(final User user){

        final List<CarItem> carItemList = new ArrayList<CarItem>();
        if(user!= null && user.getCars() != null && user.getCars().size() > 0) {
            for (int i = 0; i < user.getCars().size(); i++) {
                if (user.getCars().get(i).getStatusDisabled() == null || (user.getCars().get(i).getStatusDisabled() != null && !user.getCars().get(i).getStatusDisabled())) {
                    carItemList.add(user.getCars().get(i));
                }
            }
        }

        if(carSpinner != null && carItemList.size() > 0) {
            carSpinner.setVisibility(View.VISIBLE);
            int size = carItemList.size();
            if(carItemList.size() > 3)
                size = 3;
            String[] valuesArray = new String[size];
            final String[] models = new String[size];
            final String[] carNumbers = new String[size];
            final Integer[] keysArray = new Integer[size];

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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesArray);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (!isSpinnerTouched) return;
                    if(fareRequest != null) {
                        fareRequest.setCarType(keysArray[pos]);
                        fareRequest.setCarMark(models[pos]);
                        fareRequest.setCarNo(carNumbers[pos]);
                        fareRequest.setClientName(user.getFirstName());
                        fareRequest.setClientKey(carNumbers[pos] + " (" + user.getFirstName() + ")");
                    }
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            fareRequest.setCarType(keysArray[0]);
            fareRequest.setCarMark(models[0]);
            fareRequest.setCarNo(carNumbers[0]);
            fareRequest.setClientName(user.getFirstName());
            fareRequest.setClientKey(carNumbers[0] + " (" + user.getFirstName() + ")");
            carSpinner.setAdapter(adapter);
        }
        else{
            myCarText.setVisibility(View.VISIBLE);
            carSpinner.setVisibility(View.GONE);
        }
    }

    private void createCarwashCarsSpinner(final User user,final WasherPublic washer){

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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesArray);
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
                        serviceDialogFragment.show(getSupportFragmentManager().beginTransaction(), "DialogFragment");
                    }
                }
            });

            bookTimeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bookTimeClose.getVisibility() == View.GONE) {
                        timetableDialogFragment = ClientTimetableDialogFragment.newInstance(washer);
                        timetableDialogFragment.setCancelable(false);
                        timetableDialogFragment.show(getSupportFragmentManager().beginTransaction(), "DialogFragment");
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
//            mainButton.setEnabled(false);
            myCarText.setVisibility(View.VISIBLE);
            carSpinner.setVisibility(View.GONE);
        }
    }

    private void createDefaultServicesForRequest(){
        if(serviceSpinner != null) {
            int defaultServices [] = {3,2,1,5};
            final ArrayList<Integer> serviceItems = new ArrayList<Integer>();
            String[] valuesArray = new String[defaultServices.length];
            final Integer[] keysArray = new Integer[defaultServices.length];
            for(int i = 0 ; i < defaultServices.length; i++){
                valuesArray[i] = BA.getReference().getServices().get(defaultServices[i]);
                keysArray[i] = defaultServices[i];
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesArray);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (!isSpinnerTouched) return;
                    if(fareRequest != null) {
                        serviceItems.clear();
                        serviceItems.add(keysArray[pos]);
                        fareRequest.setServices(new ArrayList<Integer>(serviceItems));
                    }
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            serviceItems.add(keysArray[0]);
            if(fareRequest != null)
               fareRequest.setServices(new ArrayList<Integer>(serviceItems));
            serviceSpinner.setAdapter(adapter);
        }
    }

    private void createCarwashServices(WasherPublic washer, int carType){
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesArray);
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

    @Override
    public void showProgressBar(boolean set) {
    }

    @OnClick(R.id.mainLayout)
    public void onRequestWindowClick(){
       //Empty implementation
    }

    @Subscribe
    public void onCollapseRequestFields(CollapseRequestFields event){
           collapse(mainLayout);
    }

    @Subscribe
    public void expandRequestFields(ExpandRequestFields event){
           expand(mainLayout);
    }

    @Override
    public void showClusterItems(final List<WasherPublic> washers, GoogleMap map, Location currentLoc) {
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
        android.view.Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.custom_cluster_info_window_map);
        ListView events = (ListView) dialog.findViewById(R.id.detailClusterList);
        WasherListAdapter adapter = new WasherListAdapter(this,currentLoc);
        adapter.set(washers);
        events.setAdapter(adapter);
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                openProfileWasher(washers.get(arg2).getId());
            }
        });

        Drawable mDrawable = this.getResources().getDrawable(R.drawable.ic_exit);
        mDrawable.setColorFilter(this.getResources().getColor(R.color.background_main), PorterDuff.Mode.MULTIPLY);

         dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        dialog.setTitle(null);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void hideClusterItem(){
        fareRequest = new FareRequest();
        if(washerWindow.getVisibility() == View.VISIBLE) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                }
            },400);

            createDefaultServicesForRequest();
            resetSelectedServices();
            resetSelectedTime();

            fareLine.setVisibility(View.VISIBLE);
            requestButton.setVisibility(View.VISIBLE);
            fareLayout.setVisibility(View.VISIBLE);
            serviceSpinner.setVisibility(View.VISIBLE);
            commentLayout.setVisibility(View.VISIBLE);
            mainFieldsLayout.setVisibility(View.VISIBLE);

            mainButton.setEnabled(true);
            washerWindow.setVisibility(View.GONE);
            mainButton.setVisibility(View.GONE);
            bookTimeLayout.setVisibility(View.GONE);
            bookService.setVisibility(View.GONE);
            informationLayout.setVisibility(View.GONE);

            if(selectedTab == 0 && expandButtonPosition == DOWN)
                upExpandButton();

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

    public void hideRequestItem(){
        if(expandButtonPosition == DOWN) {
            upExpandButton();
        }
        mainFieldsLayout.setVisibility(View.GONE);
        requestButton.setVisibility(View.GONE);

    }

    public void showRequestItem(){
        fareRequest = new FareRequest();
        createCarsSpinner(user);
        createDefaultServicesForRequest();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable(){
            @Override
            public void run() {
            }
        },400);

        if(selectedTab != 0) {
            downExpandButton();
        }
        else if(selectedTab == 0 && expandButtonPosition == DOWN){
            upExpandButton();
        }

        mainFieldsLayout.setVisibility(View.VISIBLE);
        requestButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void showClusterItem(final WasherPublic item, GoogleMap map, Location currentLoc) {
        request = new BookingRequest();
        request.setCarwash(URL_PREFIX+item.getId());

        if(washerWindow.getVisibility() == View.GONE) {
            downExpandButton();
        }

        washerWindow.setVisibility(View.VISIBLE);
        mainButton.setVisibility(View.VISIBLE);
        bookTimeLayout.setVisibility(View.VISIBLE);
        bookService.setVisibility(View.VISIBLE);

        washerDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileWasher(item.getId());
            }
        });
//        serviceSpinner.setVisibility(View.GONE);

        //TODO temproary use comment column for additional info for information carwash
        if(item.getSchedule() != null && !TextUtils.isEmpty(item.getSchedule().getComment())){
            comment.setText(item.getSchedule().getComment());
        }

        if(item.isBookable() && !item.getStatus().equals("information") && (item.getSchedule() != null)) {
            resetSelectedServices();
            resetSelectedTime();
            mainFieldsLayout.setVisibility(View.VISIBLE);
            imgAvailability.setVisibility(View.VISIBLE);
            informationLayout.setVisibility(View.GONE);
            serviceSpinner.setVisibility(View.GONE);
            mainButton.setText(BA.str(R.string.book_verb));
            createCarwashCarsSpinner(user, item);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run() {

                }
            },200);

            mainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
                    if(loggedIn) {
                        if(request != null && request.getCarType()==-1) {
                            ToastUtil.display(ClientHomeActivity.this, BA.str(R.string.select_car));
                            return;
                        }
                        if (!(request.getServices() != null && request.getServices().size() > 0) && !(request.getGroupServices() != null && request.getGroupServices().size() > 0)) {
                            ToastUtil.displayAtTop(ClientHomeActivity.this, BA.str(R.string.select_services));
                            return;
                        }
                        if ((request.getTime() == null)) {
                            ToastUtil.displayAtTop(ClientHomeActivity.this, BA.str(R.string.choose_time));
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
                    boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
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
            imgAvailability.setVisibility(View.GONE);
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


        if(item.getSchedule() != null && item.getSchedule().getToday() != null && item.getSchedule().getToday().size() > 0) {
            int startTime = item.getSchedule().getToday().get(0).getFrom();
            int endTime = item.getSchedule().getToday().get(0).getTo();
            String time = String.format("%02d", startTime/60)+":"+String.format("%02d", startTime%60)+" - "+(endTime/60==0?"24":String.format("%02d", endTime/60))+":"+String.format("%02d", endTime%60);
            washerTime.setText(time);
        }
        else
            washerTime.setText(BA.str(R.string.not_specified));

        reviewCount.setText(item.getReviewCount()+BA.str(R.string.reviews_suffix));
        if(item.getImages() != null &&  item.getImages().size() > 0){

            ImageDetail firstImage = item.getImages().get(0).getThumb1();
            HttpClient.getPicasso()
                    .load(RetrofitClient.API_URL_IMAGES+firstImage.getUrl())
                    .fit()
                    .centerCrop()
                    .into(washerImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
        else{
            washerImage.setVisibility(View.GONE);
        }

        if(item.getContacts() != null && item.getContacts().size() > 0){
            for(final Washer.Contact contact : item.getContacts())
                if(contact.getType().contains("phone")&&!TextUtils.isEmpty(contact.getValue())) {
                    washerPhone.setText(contact.getValue());
                    washerPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + contact.getValue()));
                            startActivity(intent);
                        }
                    });
                }
        }
        else
            washerPhone.setText(BA.str(R.string.not_specified));

        name.setText(item.getName().toLowerCase().contains("автомойка")?item.getName():BA.str(R.string.car_wash_label_sp)+item.getName());
        price.setText(BA.str(R.string.body_interior_from_pre) + (item.getPrice()!=null?formatter.format(item.getPrice()).replaceAll(",", " ")+" ₸.":""));
        review.setRating((float)item.getRating().doubleValue());

//		holder.address.setText(item.getAddress());
        if(!TextUtils.isEmpty(item.getAddress()) && !TextUtils.isEmpty(item.getCity())){
            address.setText(Functions.getCityDescription(item.getCity())+", "+item.getAddress());
        }
        if(TextUtils.isEmpty(item.getAddress()))
            address.setText(Functions.getCityDescription(item.getCity())+", "+BA.str(R.string.address_not_specified));

        requestButton.setVisibility(View.GONE);
        fareLayout.setVisibility(View.GONE);
        fareLine.setVisibility(View.GONE);
        commentLayout.setVisibility(View.GONE);
    }

    private void showLoginWarning(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        dialog.setTitle(BA.str(R.string.login_required));
        dialog.setPositiveButton(BA.str(R.string.login_word), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                login();
            }
        });
        dialog.setNegativeButton(BA.str(R.string.cancel_word),null);
        dialog.show();
    }


    public void startBooking(WasherPublic washer) {
        Intent intent = new Intent(this,ClientBookingActivity.class);
        intent.putExtra(WASHER_ID, washer.getId() );
        intent.putExtra(WASHER_BOOK_FROM_MAP, true);
        startActivity(intent);
    }

    @Override
    public void openProfileWasher(String washerId) {
        Intent intent = new Intent(this,ClientWasherInfoActivity.class);
        intent.putExtra(ClientWasherInfoFragment.EXTRA_WASHER_ID, washerId);
        startActivity(intent);
    }

    @Override
    public void hideShowFilterButton(int tab) {

    }

    public void openProfileWasher(String washerId, String pushData) {
        Intent intent = new Intent(this,ClientWasherInfoActivity.class);
        intent.putExtra(ClientWasherInfoFragment.EXTRA_WASHER_ID, washerId);
        intent.putExtra(Constants.PUSH_DATA, pushData);
        startActivity(intent);
    }

    @Override
    public void openCampaignInfo(String washerId, String washer) {
        Intent intent = new Intent(this,ClientCampaignInfoActivity.class);
        intent.putExtra(Constants.EXTRA_WASHER_ID, washerId);
        intent.putExtra(Constants.WASHER_DATA, washer);
        startActivity(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectedMenu = position;
        switch (position){
            case 0: //Home

                if(homeContainer != null)
                    homeContainer.setVisibility(View.VISIBLE);
                if(fragmentContainer != null)
                   fragmentContainer.setVisibility(View.GONE);

                localCity = UserPreferences.getCity(BA.getContext());
                if(titleView != null) {
                    if(!TextUtils.isEmpty(localCity)) {
                        titleView.setText(Functions.getCityDescription(localCity));
                    }
//                    titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_white_24dp, 0);
                    titleView.setVisibility(View.VISIBLE);
                }
                if(pagerTab!= null) {
                    pagerTab.setCurrentItem(0,false);
                    showRequestItem();
                }
                break;
            case 1: //books
                if(titleView != null) {
                   titleView.setText(BA.str(R.string.my_bookings));
                }
                pagerTab.setCurrentItem(2,false);
                hideClusterItem();
                hideRequestItem();
                break;
            case 2: //notification
                if(titleView != null) {
                    titleView.setText(BA.str(R.string.notifications));
                }
                pagerTab.setCurrentItem(3,false);
                hideClusterItem();
                hideRequestItem();
                break;
            case 3: //favourite
                if(titleView != null) {
                    titleView.setText(BA.str(R.string.favorites));
                }
                pagerTab.setCurrentItem(4,false);
                hideClusterItem();
                hideRequestItem();
                break;
            case 4: //about us
                startActivity(new Intent(this,ClientAboutUSActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .putExtra(OPENING_ANIMATION,false)
                );
                break;
            case 5: //language
                if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen())
                    mNavigationDrawerFragment.closeDrawer();
                LanguageDialog.show(this);
                break;


        }
    }

    @OnClick(R.id.btnRequest)
    public void onButtonFareRequestClicked() {
        loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
        if(loggedIn) {
            fareRequest.setCity(UserPreferences.getCity(BA.getContext()));
            if (!TextUtils.isEmpty(UserPreferences.getGPSData(BA.getContext()))) {
                CurrentGeoPosition gpsData = CurrentGeoPosition.deserialize(UserPreferences.getGPSData(BA.getContext()));
                ArrayList<Double> lonLat = new ArrayList<Double>();
                lonLat.add(gpsData.getLng());
                lonLat.add(gpsData.getLat());
                fareRequest.setLonLat(lonLat);
            }else{
                gps.showSettingsAlert();
                return;
            }


            if(TextUtils.isEmpty(fare.getText().toString())){
                fare.requestFocus();
                ToastUtil.display(this,BA.str(R.string.enter_price));
                return;
            }
            fareRequest.setFare(Double.parseDouble(fare.getText().toString()));

            if(!TextUtils.isEmpty(comment.getText().toString()) && comment.getText().length() < 3){
                comment.requestFocus();
                ToastUtil.display(this,BA.str(R.string.enter_detailed_comment));
                return;
            }

            fareRequest.setComment(fareComment.getText().toString());

            setWaitScreen(true);
            BA.getEventBus().post(new FareRequestEvent(fareRequest));
        }
        else
            showLoginWarning();
    }
    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        long actualSearchTime = (Calendar.getInstance()).getTimeInMillis();
        // Only one search every second to avoid key-down & key-up
        if (actualSearchTime > lastSearchTime + 1000)
        {
            if(!TextUtils.isEmpty(query)){
                innerQuery = query;
                searchQuery(query);
            }
            lastSearchTime=actualSearchTime;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(TextUtils.isEmpty(query) && isSearchable){
        }
        return false;
    }

    private void searchQuery(String query){
        BA.getEventBus().post(new NearSearchEvent(query));
		ClientBaseHomeFragment fragment = (ClientBaseHomeFragment) TabAdapter.getItem(selectedTab);
        if(fragment instanceof ClientAvailableWashersFragment)
            BA.getEventBus().post(new AvailableSearchEvent(query));
          else if(fragment instanceof ClientNearByWashersFragment)
            BA.getEventBus().post(new NearSearchEvent(query));
          else if(fragment instanceof ClientFavouriteWashersFragment)
         	 BA.getEventBus().post(new FavouriteSearchEvent(query));
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if (!TextUtils.isEmpty(innerQuery)) {
            searchQuery("");
        }
            isSearchable = false;
        // The toolbar lazily creates its collapse (back) arrow when the search
        // action view expands, and it defaults to a dark tint. Post so it exists,
        // then white it to match the toolbar.
        if (mToolbar != null) {
            mToolbar.post(this::whiteCollapseIcon);
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        isSearchable = true;
        if (!TextUtils.isEmpty(innerQuery))
            searchQuery("");
        innerQuery = "";
        return true;
    }

    /**
     * The appcompat SearchView's magnifier / close icons and its input text
     * default to dark colors, which are barely visible on the blue toolbar.
     * Tint them white to match the rest of the action bar.
     */
    private void styleSearchViewWhite(SearchView searchView) {
        if (searchView == null) {
            return;
        }
        int white = getResources().getColor(R.color.White);

        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchText != null) {
            searchText.setTextColor(white);
            searchText.setHintTextColor(getResources().getColor(R.color.BlueLight));
        }

        int[] iconIds = {
                androidx.appcompat.R.id.search_mag_icon,
                androidx.appcompat.R.id.search_button,
                androidx.appcompat.R.id.search_close_btn,
                androidx.appcompat.R.id.search_go_btn,
                androidx.appcompat.R.id.search_voice_btn
        };
        for (int id : iconIds) {
            ImageView icon = searchView.findViewById(id);
            if (icon != null) {
                icon.setColorFilter(white, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    /** White-tints the toolbar's collapse (back) arrow shown while search is expanded. */
    private void whiteCollapseIcon() {
        if (mToolbar == null) {
            return;
        }
        Drawable collapseIcon = mToolbar.getCollapseIcon();
        if (collapseIcon != null) {
            collapseIcon = collapseIcon.mutate();
            collapseIcon.setColorFilter(getResources().getColor(R.color.White), PorterDuff.Mode.SRC_IN);
            mToolbar.setCollapseIcon(collapseIcon);
        }
    }

    @Override
    public void openBookInfo(String bookId) {
        Intent intent = new Intent(this,ClientBookingInfoActivity.class);
        intent.putExtra(EXTRA_BOOKING_ID, bookId );
        startActivity(intent);
    }

    @Override
    public void openFareRequestInfo(String fareRequestId, FareRequest fareRequest) {
        Intent intent = new Intent(this,ClientWaitingRequestActivity.class);
        intent.putExtra(OPENING_ANIMATION,false);
        intent.putExtra(EXTRA_FARE_REQUEST_ID, fareRequestId );
        intent.putExtra(ClientWaitingRequestActivity.REQUEST_DATA, fareRequest.serialize());
        startActivity(intent);
    }

    @Override
    public void openActiveBookInfo(String bookId) {
        Intent intent = new Intent(this,ClientActiveBookingInfoActivity.class);
        intent.putExtra(EXTRA_BOOKING_ID, bookId );
        startActivity(intent);
    }

    public void onFilterClicked() {
         filterDialogFragment = new ClientFilterFragment();
         filterDialogFragment.setCancelable(false);
         filterDialogFragment.show(getSupportFragmentManager().beginTransaction(),"DialogFragment");
    }

    public void onMapFilterClicked() {
        onFilterClicked();
    }

    @Override
    public void closeFilter() {
        if(filterDialogFragment != null){
            filterDialogFragment.dismiss();
        }
    }

    @Subscribe
    public void onFilterReceived(FilterResponseEvent event){
        filter = event.getSearchFilter().clone();
    }

    @Subscribe
    public void onFilterReceived(FilterGetResponseEvent event){
        SearchFilter filter = event.getSearchFilter().clone();
        localCity = UserPreferences.getCity(BA.getContext());
        if(titleView != null) titleView.setText(Functions.getCityDescription(localCity));
        if(!filter.isMapDefaultValues()){
            filtered = true;
            filteredMenuItem.setVisible(true);
            filterMenuItem.setVisible(false);
        }
        else{
            filtered = false;
            filteredMenuItem.setVisible(false);
            filterMenuItem.setVisible(true);
        }
    }

    @Override
    public void login() {
        startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra(OPENING_ANIMATION,false));
    }

    @Override
    public void showChooseCity() {
        startActivity(new Intent(ClientHomeActivity.this, CityChooseActivity.class));
    }

    @Subscribe
    public void onBookingPushReceived(NewBookingPushResponseEvent event){
        if(event != null && event.getPush() != null){
            bookingPushData = event.getPush().getData();
            if(bookingPushData!=null && bookingPushData.size() > 0){
            }
        }
    }

    public static void expand(final View v) {
        v.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? FrameLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @OnClick(R.id.btnExpand)
    public void expandButtonClicked(){
        hideClusterItem();
        if(selectedTab != 0){
            if(mainFieldsLayout.getVisibility() == View.GONE){
               showRequestItem();
            }
            else{
                hideRequestItem();
            }
        }
    }

    private Washer.GroupMenu getGroupServiceName(WasherPublic washer, String groupServiceId){
        for(Washer.GroupMenu groupMenu : washer.getGroupMenu()){
            if(groupMenu.getId().equals(groupServiceId)){
                return groupMenu;
            }
        }
        return null;
    }

    @Override
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

    @Override
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
                openActiveBookInfo(event.getResult().getResponse().getId());
            }
            else
                ToastUtil.display(this, event.getResult().getMessage());
        }
        else{
            ToastUtil.display(this, BA.str(R.string.err_booking));
        }
    }

    @Subscribe
    public void onFareRequestResponseReceived(FareRequestResponseEvent event){
        setWaitScreen(false);
        if(event.getData() != null){
            if(BaseAssist.isSuccess(event.getData())) {
                UserPreferences.putActiveFareRequestBooking(BA.getContext(), event.getData().getData().serialize());
                openFareRequestInfo(event.getData().getData().getId(), event.getData().getData());
            }
            else
                ToastUtil.display(this, event.getData().getMessage());
        }
        else{
            ToastUtil.display(this, BA.str(R.string.err_offer_price));
        }
    }

    @Subscribe
    public void onWantedWashersReceived(WantedWashersResponseEvent event){
        if(event!=null && event.getWantedWashers() != null)
            wantedWashers = event.getWantedWashers();
    }
}
