package com.driverspa.adapter;
import com.driverspa.BA;

import android.content.Context;
import android.location.Location;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.R;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.L;
import com.driverspa.util.LogUtil;

public class WasherCampaignsListAdapter extends BaseDataAdapter<WasherPublic> {

	DecimalFormat formatter = new DecimalFormat("###.##");
	boolean isCompany = false;
	boolean useNoGPSOption = false;
	Location currentLoc;
	public Location getCurrentLoc() {
		return currentLoc;
	}

	public void setCurrentLoc(Location currentLoc) {
		this.currentLoc = currentLoc;
	}

	public WasherCampaignsListAdapter(Context context, Location currentLoc, boolean showDistance) {
		super(context);
		this.currentLoc = currentLoc;
		this.useNoGPSOption = showDistance;
	}

	public WasherCampaignsListAdapter(Context context, boolean isCompany) {
		super(context);
		this.isCompany = isCompany;
	}

	public WasherCampaignsListAdapter(Context context) {
		super(context);		
	}	
		
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		if (isLoad && position == getCount() - 1) {
			LinearLayout ll = new LinearLayout(context);
			ll.setGravity(Gravity.CENTER_HORIZONTAL);
			ProgressBar bar = new ProgressBar(context);
			ll.addView(bar);
			return ll;
		}
				
		ViewHolder holder;
		WasherPublic item = getItem(position);
//		if (view != null) {
//			holder = (ViewHolder) view.getTag();
//		} else {
			view = LayoutInflater.from(context).inflate(R.layout.list_washer_campaigns, parent, false);

			holder = new ViewHolder(view);		
		    view.setTag(holder);
//		}

		holder.discount.setVisibility(View.GONE);
		if(item.getActiveCampaign() != null){
			holder.discount.setVisibility(View.VISIBLE);
			holder.discountText.setText("-"+item.getActiveCampaign().getCampaignDiscount()+"%");
		}

		Long tsTimestamp = item.getActiveCampaign().getTs()*1000;

		PrettyTime p = new PrettyTime(new Locale("ru"));
		holder.campaignTS.setText(p.format(new Date(tsTimestamp)));
		holder.name.setText(item.getName().toLowerCase().contains("автомойка")?item.getName():BA.str(R.string.car_wash_label_sp)+item.getName());
		holder.campaignDescription.setText(item.getActiveCampaign().getDescription());

		return view;
	}
	
	static class ViewHolder {

		@BindView(R.id.txtWasherName)
		TextView name;
		@BindView(R.id.txtCampaignDescription)
		TextView campaignDescription;
		@BindView(R.id.discountLayout)
		View discount;
		@BindView(R.id.discountText)
		TextView discountText;
		@BindView(R.id.txtCampaignTS)
		TextView campaignTS;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
