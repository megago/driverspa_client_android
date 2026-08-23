package com.driverspa.client.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.PricesExpandableListAdapter;
import com.driverspa.model.ServiceMenuModel;
import com.driverspa.model.Washer;
import com.driverspa.model.Washer.Prices;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.WASHER_DATA;

public class ClientPriceDetailActivity extends ClientBaseActivity {
		
	@BindView(R.id.txtTitle)
	TextView textTitle;
	Washer washer;
	@BindView(R.id.expandable_list_view)
	ExpandableListView listView;
	PricesExpandableListAdapter adapter;
		
    @Override
    protected void onCreate(Bundle arg0) {
    	super.onCreate(arg0);
        Mint.initAndStartSession(this.getApplication(), "b054ddc0");
        Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
        setContentView(R.layout.activity_client_prices);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    	ButterKnife.bind(this);
    	
        washer = JsonUtil.deserializeToWasher(getIntent().getStringExtra(WASHER_DATA));
        TextView titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
        titleView.setText(BA.str(R.string.prices));
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(washer.getName());
        textTitle.setVisibility(View.GONE);

        ArrayList<ServiceMenuModel> carListMenu = new ArrayList<ServiceMenuModel>();
        LinkedHashMap<Integer,ArrayList<Prices>> washerMenu = washer.getMenu();
        final HashMap<Integer,String> services = BA.getReference().getServices();
        final HashMap<Integer,String> cars = BA.getReference().getCarType();
        
        if(washerMenu != null && washerMenu.size() > 0){
            LinkedHashMap<Integer, ArrayList<Prices>> carTypeMenu = washerMenu;

            List<Integer> keys = new ArrayList<Integer>(carTypeMenu.keySet());
            Collections.sort(keys, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    if(lhs > rhs)
                    return 1;
                    else return 0;
                }
            });

            for(Integer carTypeKey : keys){
                ArrayList<Prices> prices = carTypeMenu.get(carTypeKey);
                ArrayList<Prices> definedPrices = new ArrayList<Prices>();
                for(Prices p: prices){
                    if(p.getPrice() > 0)
                        definedPrices.add(p);
                }

                ServiceMenuModel model = new ServiceMenuModel();
                model.setCarType(carTypeKey);
                model.setPrices(definedPrices);
                if(definedPrices.size() > 0)
                    carListMenu.add(model);
            }

//	        for (Map.Entry<Integer, ArrayList<Prices>> entry : carTypeMenu.entrySet()) {
//		        Integer key = entry.getKey();
//                ArrayList<Prices> prices = carTypeMenu.get(key);
////                Iterator<Prices> iter = prices.iterator();
////                while (iter.hasNext()) {
////                    Prices p = iter.next();
//////                    if(p.getType() > 4)
//////                        iter.remove();
////                }
//
//		       ServiceMenuModel model = new ServiceMenuModel();
//		       model.setCarType(key);
//		       model.setPrices(prices);
//               if(prices.size() > 0)
//		        carListMenu.add(model);
//		  	}



        }
        
		adapter = new PricesExpandableListAdapter(this, carListMenu, cars, services);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

    } 
    	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	finish();
            	overridePendingTransitionWithCommonCloseTransition();
                break;
        }
        return true;
    }
}
