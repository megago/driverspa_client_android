package com.driverspa.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import com.splunk.mint.Mint;
import butterknife.ButterKnife;
import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientWaitingRequestMapFragment;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;

public class ClientWaitingRequestActivity extends BaseActivity implements ClientWaitingRequestMapFragment.ActivityActions
{
    public static String REQUEST_DATA = "REQUEST_DATA";
    public static FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Mint.initAndStartSession(this.getApplication(), "b054ddc0");
        Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
        setContentView(R.layout.activity_client_waiting_request);
        ButterKnife.inject(this);

        fragmentManager = getFragmentManager();
        ClientWaitingRequestMapFragment fragment = ClientWaitingRequestMapFragment.init(getIntent().getStringExtra(REQUEST_DATA));

        if( arg0 == null ) {
            fragmentManager.beginTransaction().add(R.id.fragment_container,fragment).commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
    public void openActiveBookInfo(String bookId) {
        Intent intent = new Intent(this,ClientActiveBookingInfoActivity.class);
        intent.putExtra(EXTRA_BOOKING_ID, bookId );
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}
