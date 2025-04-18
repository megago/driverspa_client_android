package com.driverspa.client.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.driverspa.R;

public class ClientPromoWashersFragment extends ClientBaseHomeFragment implements AdapterView.OnItemClickListener {

	public interface ActivityActions {
		public void openProfileWasher(String washerId);
	}

	@InjectView(R.id.pull_to_refresh_listview)
	PullToRefreshListView pullToRefreshView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_promo_washers, container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        ListView listView = pullToRefreshView.getRefreshableView();	       	        
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);	        	        
        TextView emptyView = new TextView(this.getActivity());
        emptyView.setText(R.string.list_empty);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setEmptyView(emptyView);        
	}

	public void setFilterQuery(String query) {
		// adapter.setFilterQuery(query);
	}

	public void resetFilterQuery() {
		// adapter.resetFilterQuery();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Object o = adapterView.getItemAtPosition(position);
	}

	@Override
	public int getTitleResourceId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
	}
	
}
