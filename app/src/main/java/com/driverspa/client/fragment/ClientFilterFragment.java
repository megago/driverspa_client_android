package com.driverspa.client.fragment;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.DialogFragment;

import com.squareup.otto.Subscribe;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.SearchFilter;
import com.driverspa.util.Functions;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.FilterResponseEvent;
import com.driverspa.util.otto.ws.FilterUpdateRequestEvent;
import com.driverspa.view.NDSpinner;

/**
 * Created by Yerzhan Tanatov on 06/11/15.
 */
public class ClientFilterFragment extends DialogFragment {

    private static final int REP_DELAY = 500;
    private static final int REP_DELAY_START_ACCELERATE = 2;
    private static final int REP_DELAY_ACCELERATED = 100;
    private static final int PRICE_STEP = 200;

    public interface ActivityActions{
        public void closeFilter();
        public void showChooseCity();
    }

    @BindView(R.id.cityLayout)
    View cityLayout;
    @BindView(R.id.txtCity)
    TextView txtCity;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.seek_distance)
    SeekBar seekDistance;
    @BindView(R.id.toggle_cafe)
    ToggleButton toggleCafe;
    @BindView(R.id.toggle_atm)
    ToggleButton toggleAtm;
    @BindView(R.id.toggle_coffee)
    ToggleButton toggleCoffee;
    @BindView(R.id.toggle_nocash)
    ToggleButton toggleNocach;
    @BindView(R.id.toggle_room)
    ToggleButton toggleRoom;
    @BindView(R.id.toggle_games)
    ToggleButton toggleGames;
    @BindView(R.id.toggle_wifi)
    ToggleButton toggleWifi;
    @BindView(R.id.toggle_campaign)
    ToggleButton toggleCampaign;

    @BindView(R.id.star1)
    ImageView star1;
    @BindView(R.id.star2)
    ImageView star2;
    @BindView(R.id.star3)
    ImageView star3;
    @BindView(R.id.star4)
    ImageView star4;
    @BindView(R.id.star5)
    ImageView star5;

    @BindView(R.id.minus_price)
    View minusPrice;
    @BindView(R.id.plus_price)
    View plusPrice;

    @BindView(R.id.root_view)
    View rootView;
    @BindView(R.id.spinner)
    NDSpinner spinner;
    private boolean isSpinnerTouched = false;
    int selectedSortingType = 0;

    int mark = 0;
    private ActivityActions activityActions;
    boolean closed = false;
    SearchFilter searchFilter;

    private int incDecCount = 0;
    private int currRepDelay = REP_DELAY;
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    Animation slideUpIn;
    Animation slideDownOut;
    private Handler repeatUpdateHandler = new Handler();
    private Handler customHandler = new Handler();
    private SearchFilter initialEnteringFilter;
    String localCity;
    String initialCity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_filter, container, false);
        // This is a DialogFragment with its own window, so it doesn't get the host
        // activity's system-bar inset padding. On edge-to-edge (Android 15+) that
        // draws the top of the filter under the status bar ("cut off"). Pad the
        // root by the system-bar insets so the content clears the status bar.
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            androidx.core.graphics.Insets bars =
                    insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return androidx.core.view.WindowInsetsCompat.CONSUMED;
        });
        androidx.core.view.ViewCompat.requestApplyInsets(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar);
    }

    private void createSpinner(){
        if(spinner != null) {
            final String[] valuesArray =  {BA.str(R.string.nearest),BA.str(R.string.by_rating),BA.str(R.string.cheapest), BA.str(R.string.most_expensive)};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_filter, valuesArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (!isSpinnerTouched) return;
                    selectedSortingType = pos;

                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spinner.setAdapter(adapter);
            isSpinnerTouched = true;
            spinner.setSelection(selectedSortingType);
            isSpinnerTouched = false;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initialCity = UserPreferences.getCity(BA.getContext());
        localCity = UserPreferences.getCity(BA.getContext());
        txtCity.setText(Functions.getCityDescription(localCity));

        createSpinner();
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });

        seekDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance.setText(progress + BA.str(R.string.km_suffix));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        slideUpIn = AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_bottom);
            slideUpIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        slideDownOut = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_to_top);
        slideDownOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ClientFilterFragment.this.dismiss();
            }
        });

            rootView.startAnimation(slideUpIn);
    }

    @Subscribe
    public void onFilterReceived(FilterResponseEvent event){
          if(event.getSearchFilter() != null){
               searchFilter = event.getSearchFilter();
               initialEnteringFilter = searchFilter.clone();
               if(localCity != null)
                  searchFilter.setCity(localCity);

              if(searchFilter.getOrderBy().equals("nearest"))
                  selectedSortingType = 0;
              else if(searchFilter.getOrderBy().equals("rating"))
                  selectedSortingType = 1;
              else if(searchFilter.getOrderBy().equals("cheapest"))
                  selectedSortingType = 2;
              else if(searchFilter.getOrderBy().equals("expensive"))
                  selectedSortingType = 3;

               spinner.setSelection(selectedSortingType);

               distance.setText(((Integer.parseInt(searchFilter.getMaxDistance())) / 1000) + BA.str(R.string.km_suffix));
               seekDistance.setProgress((Integer.parseInt(searchFilter.getMaxDistance()) / 1000));

               if(!TextUtils.isEmpty(searchFilter.getRating())) {
                  int rating = Integer.parseInt(searchFilter.getRating());
                   switch (rating){
                       case 1:
                           onClickStar1();
                           break;
                       case 2:
                           onClickStar2();
                           break;
                       case 3:
                           onClickStar3();
                           break;
                       case 4:
                           onClickStar4();
                           break;
                       case 5:
                           onClickStar5();
                           break;
                   }
               }

               if(!TextUtils.isEmpty(searchFilter.getMaxPrice())){
                  price.setText(searchFilter.getMaxPrice());
               }

              if(!TextUtils.isEmpty(searchFilter.getCafe())){
                 toggleCafe.setChecked(true);
              }
              if(!TextUtils.isEmpty(searchFilter.getCoffee())){
                 toggleCoffee.setChecked(true);
              }
              if(!TextUtils.isEmpty(searchFilter.getWifi())){
                 toggleWifi.setChecked(true);
              }
              if(!TextUtils.isEmpty(searchFilter.getRestroom())){
                 toggleRoom.setChecked(true);
              }
              if(!TextUtils.isEmpty(searchFilter.getGames())){
                 toggleGames.setChecked(true);
              }
              if(!TextUtils.isEmpty(searchFilter.getPayments())){
                 toggleNocach.setChecked(true);
              }
              toggleCampaign.setChecked(searchFilter.isHasCampaign());
          }
    }

    @OnClick(R.id.campaignLayout)
    public void campaignLayoutClicked(){
        toggleCampaign.setChecked(!toggleCampaign.isChecked());
    }

    @OnClick(R.id.cafeLayout)
    public void cafeLayoutClicked(){
        toggleCafe.setChecked(!toggleCafe.isChecked());
    }

    @OnClick(R.id.coffeeLayout)
    public void coffeeLayoutClicked(){
        toggleCoffee.setChecked(!toggleCoffee.isChecked());
    }

    @OnClick(R.id.wifiLayout)
    public void wifiLayoutClicked(){
        toggleWifi.setChecked(!toggleWifi.isChecked());
    }

    @OnClick(R.id.roomLayout)
    public void roomLayoutClicked(){
        toggleRoom.setChecked(!toggleRoom.isChecked());
    }

    @OnClick(R.id.gameLayout)
    public void gameLayoutClicked(){
        toggleGames.setChecked(!toggleGames.isChecked());
    }

    @OnClick(R.id.nocashLayout)
    public void nocashLayoutClicked(){
        toggleNocach.setChecked(!toggleNocach.isChecked());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(txtCity != null){
            localCity = UserPreferences.getCity(BA.getContext());
            txtCity.setText(Functions.getCityDescription(localCity));
            if(searchFilter != null)
               searchFilter.setCity(localCity);
        }
        BA.getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BA.getEventBus().unregister(this);
    }

    @OnClick(R.id.star1)
    public void onClickStar1(){
        if(mark == 1) {
            mark = 0;
            star1.setSelected(false);
        }
        else {
            star1.setSelected(true);
            mark = 1;
        }
        star2.setSelected(false);
        star3.setSelected(false);
        star4.setSelected(false);
        star5.setSelected(false);

    }

    @OnClick(R.id.star2)
    public void onClickStar2(){
        star1.setSelected(true);
        star2.setSelected(true);
        star3.setSelected(false);
        star4.setSelected(false);
        star5.setSelected(false);
        mark = 2;
    }

    @OnClick(R.id.star3)
    public void onClickStar3(){
        star1.setSelected(true);
        star2.setSelected(true);
        star3.setSelected(true);
        star4.setSelected(false);
        star5.setSelected(false);
        mark = 3;
    }

    @OnClick(R.id.star4)
    public void onClickStar4(){
        star1.setSelected(true);
        star2.setSelected(true);
        star3.setSelected(true);
        star4.setSelected(true);
        star5.setSelected(false);
        mark = 4;
    }

    @OnClick(R.id.star5)
    public void onClickStar5(){
        star1.setSelected(true);
        star2.setSelected(true);
        star3.setSelected(true);
        star4.setSelected(true);
        star5.setSelected(true);
        mark = 5;
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

    public void applyFilter(){
        if(!closed) {
            closed = true;
            searchFilter.setMaxDistance(seekDistance.getProgress() * 1000 + "");
            searchFilter.setMaxPrice(price.getText().toString());
            switch (selectedSortingType){
                case 0:
                    searchFilter.setOrderBy("nearest");
                    break;
                case 1:
                    searchFilter.setOrderBy("rating");
                    break;
                case 2:
                    searchFilter.setOrderBy("cheapest");
                    break;
                case 3:
                    searchFilter.setOrderBy("expensive");
                    break;
            }


            String comforts = "";
            comforts = (toggleCafe.isChecked() ? ",cafe" : "")+(toggleCoffee.isChecked() ? ",coffee" : "")+(toggleWifi.isChecked() ? ",wifi" : "")
                    +(toggleGames.isChecked() ? ",games" : "")
                    +(toggleRoom.isChecked() ? ",restroom" : "");
            searchFilter.setComforts(comforts);
            searchFilter.setCafe(toggleCafe.isChecked() ? "cafe" : "");
            searchFilter.setCoffee(toggleCoffee.isChecked() ? "coffee" : "");
            searchFilter.setWifi(toggleWifi.isChecked() ? "wifi" : "");
            searchFilter.setRestroom(toggleRoom.isChecked() ? "restroom" : "");
            searchFilter.setGames(toggleGames.isChecked() ? "games" : "");
            searchFilter.setHasCampaign(toggleCampaign.isChecked());

            searchFilter.setPayments(toggleNocach.isChecked() ? "noncash" : "");
            searchFilter.setRating(mark + "");
            BA.getEventBus().post(new FilterUpdateRequestEvent(searchFilter));
            rootView.startAnimation(slideDownOut);
        }
    }

    @OnClick(R.id.close)
    public void closeFilter(){
        if(!closed) {
            closed = true;
            searchFilter = initialEnteringFilter;
            if(initialCity != null && !initialCity.equals(initialEnteringFilter.getCity())) {
                searchFilter.setCity(localCity);
                BA.getEventBus().post(new FilterUpdateRequestEvent(searchFilter));
            }

            rootView.startAnimation(slideDownOut);
        }
    }

    @OnClick(R.id.btnApply)
    public void onApplyButtonClicked(){
        applyFilter();
    }

    @OnClick(R.id.minus_price)
    public void decrementPrice(){
        if(incDecCount > REP_DELAY_START_ACCELERATE) currRepDelay = REP_DELAY_ACCELERATED;
        if((Long.parseLong(price.getText().toString()) - PRICE_STEP) >= 0) {
            minusPrice.setEnabled(true);
            price.setText(Html.fromHtml((Long.parseLong(price.getText().toString()) - PRICE_STEP) + ""));
            incDecCount++;
        }
        else {
            price.setText(Html.fromHtml(""+ PRICE_STEP));
            minusPrice.setEnabled(false);
            mAutoDecrement = false;
            currRepDelay = REP_DELAY;
            incDecCount = 0;
        }
    }

    @OnClick(R.id.plus_price)
    public void incrementPrice(){
        if(incDecCount > REP_DELAY_START_ACCELERATE) currRepDelay = REP_DELAY_ACCELERATED;
        price.setText(Html.fromHtml((Long.parseLong(price.getText().toString()) + PRICE_STEP) + ""));
        minusPrice.setEnabled(true);
        incDecCount++;
    }

    @OnLongClick(R.id.minus_price)
    public boolean onLongPriceMinusButtonClicked(){
        mAutoDecrement = true;
        repeatUpdateHandler.post( new RptPriceUpdater());
        return false;
    }

    @OnTouch(R.id.minus_price)
    public boolean onTouchPriceMinusButton(View v,MotionEvent event){
        if( (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                && mAutoDecrement ){
            mAutoDecrement = false;
            currRepDelay = REP_DELAY;
            incDecCount = 0;
        }
        return false;
    }

    @OnLongClick(R.id.plus_price)
    public boolean onLongPricePlusButtonClicked(){
        mAutoIncrement = true;
        repeatUpdateHandler.post( new RptPriceUpdater());
        return false;
    }

    @OnTouch(R.id.plus_price)
    public boolean onTouchPricePlusButton(View v,MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) && mAutoIncrement) {
            mAutoIncrement = false;
            currRepDelay = REP_DELAY;
            incDecCount = 0;
        }
        return false;
    }

    class RptPriceUpdater implements Runnable {
        public void run() {
            if( mAutoIncrement ){
                incrementPrice();
                repeatUpdateHandler.postDelayed( new RptPriceUpdater(), currRepDelay );
            } else if( mAutoDecrement ){
                decrementPrice();
                repeatUpdateHandler.postDelayed( new RptPriceUpdater(), currRepDelay );
            }
        }
    }

    @OnClick(R.id.cityLayout)
    public void onCityClick(){
        activityActions.showChooseCity();
    }
}
