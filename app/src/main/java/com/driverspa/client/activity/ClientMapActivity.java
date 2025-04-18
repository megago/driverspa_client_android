package com.driverspa.client.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.splunk.mint.Mint;

import java.util.List;
import java.util.Locale;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.adapter.WasherListAdapter;
import com.driverspa.client.fragment.ClientFilterFragment;
import com.driverspa.client.fragment.ClientMapFragment;
import com.driverspa.client.fragment.ClientWasherInfoFragment;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.NearSearchEvent;

public class ClientMapActivity extends BaseActivity implements ClientMapFragment.ActivityActions,
		SearchView.OnQueryTextListener,
		ClientFilterFragment.ActivityActions,
		MenuItemCompat.OnActionExpandListener
{
	public static FragmentManager fragmentManager;
	EditText editsearch;
    Dialog dialog = null;
	boolean isSearchable = false;
	String innerQuery = "";
	MenuItem searchMenuItem;
	private SearchView mSearchView;
	TextView titleView;
	ClientFilterFragment filterDialogFragment;
    View progressBar;

	@Override
	protected void onCreate(Bundle arg0) {		
		super.onCreate(arg0);
		Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
		setContentView(R.layout.activity_client_map);

		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
		progressBar =  mToolbar.findViewById(R.id.progress_bar);
        titleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onActionBarTitleClicked();
			}
		});
		fragmentManager = getSupportFragmentManager();
                
        if( arg0 == null ) {
        	fragmentManager.beginTransaction().add(R.id.fragment_container,new ClientMapFragment()).commitAllowingStateLoss();
        }	
   }


    private TextWatcher textWatcher = new TextWatcher() { 
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String text = editsearch.getText().toString()
                    .toLowerCase(Locale.getDefault());
//            adapter.filter(text);
        }
 
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
            // TODO Auto-generated method stub
 
        }
 
        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
            // TODO Auto-generated method stub
 
        }
 
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_map, menu);
		searchMenuItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchMenuItem.getActionView();
		mSearchView.setOnQueryTextListener(this);
		MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
//		progressBar.setVisibility(View.GONE);
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

	@Override
	public void showProgressBar(boolean set) {
//		setWaitScreen(set);
	    if(set)
		 progressBar.setVisibility(View.VISIBLE);
		else
			progressBar.setVisibility(View.GONE);
	}
	
	@Override
	public void showClusterItems(final List<WasherPublic> washers, final GoogleMap map, Location currentLoc){
		dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
		android.view.Window window = getWindow();
		window.setGravity(Gravity.CENTER);
		dialog.setContentView(R.layout.custom_cluster_info_window_map);
		ListView events = (ListView) dialog.findViewById(R.id.detailClusterList);
	    WasherListAdapter adapter = new WasherListAdapter(this,currentLoc);
	    adapter.set(washers);
		events.setAdapter(adapter);
		events.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				openProfileWasher(washers.get(arg2).getId());
			}
		});				
		   
		Drawable mDrawable = this.getResources().getDrawable(R.drawable.ic_exit);
		mDrawable.setColorFilter(this.getResources().getColor(R.color.background_main),Mode.MULTIPLY);

		dialog.findViewById(R.id.btnClose).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);		
		dialog.setTitle(null);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(true);		
        dialog.show();
	}	    	
	
	@Override
	public void openProfileWasher(String washerId) {
		Intent intent = new Intent(this,ClientWasherInfoActivity.class);
		intent.putExtra(ClientWasherInfoFragment.EXTRA_WASHER_ID, washerId);
		intent.putExtra(OPENING_ANIMATION, false);
		startActivity(intent);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if(!TextUtils.isEmpty(query)){
			innerQuery = query;
			searchQuery(query);
		}
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		if(TextUtils.isEmpty(query) && isSearchable){
			innerQuery = query;
			searchQuery(query);
		}
		return false;
	}

	private void searchQuery(String query){
		BA.getEventBus().post(new NearSearchEvent(query));
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		if (!TextUtils.isEmpty(innerQuery))
			searchQuery("");
		isSearchable = false;
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {
		searchQuery("");
		invalidateOptionsMenu();
		isSearchable = true;
		return true;
	}

	@OnClick(R.id.action_bar_title)
	public void onActionBarTitleClicked() {
		filterDialogFragment = new ClientFilterFragment();
		filterDialogFragment.setCancelable(false);
		filterDialogFragment.show(getSupportFragmentManager().beginTransaction(),"DialogFragment");
	}

	@Override
	public void closeFilter() {

	}

	@Override
	public void showChooseCity() {

	}
}
