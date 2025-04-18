package com.driverspa.adapter;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.R;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.LogUtil;

public class WasherListNearByAdapter extends BaseDataAdapter<WasherPublic> {

	DecimalFormat formatter = new DecimalFormat("#,###.##");
	boolean isCompany = false;
	boolean useNoGPSOption = false;
	Location currentLoc;
	public Location getCurrentLoc() {
		return currentLoc;
	}

	public void setCurrentLoc(Location currentLoc) {
		this.currentLoc = currentLoc;
	}

	public WasherListNearByAdapter(Context context, Location currentLoc, boolean showDistance) {
		super(context);
		this.currentLoc = currentLoc;
		this.useNoGPSOption = showDistance;
	}

	public WasherListNearByAdapter(Context context, boolean isCompany) {
		super(context);
		this.isCompany = isCompany;
	}

	public WasherListNearByAdapter(Context context) {
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
		  if(item.isBookable() && !item.getStatus().equals("information"))
			 view = LayoutInflater.from(context).inflate(R.layout.list_nearby_washers_online, parent, false);
		  else if(!item.isBookable() && !item.getStatus().equals("information"))
			 view = LayoutInflater.from(context).inflate(R.layout.list_nearby_washers_online_not_working, parent, false);
		  else
			 view = LayoutInflater.from(context).inflate(R.layout.list_nearby_washers_offline, parent, false);

			holder = new ViewHolder(view);		
		    view.setTag(holder);
//		}

		holder.discount.setVisibility(View.GONE);
		if(item.getActiveCampaign() != null){
			holder.discount.setVisibility(View.VISIBLE);
			holder.discountText.setText("-"+item.getActiveCampaign().getCampaignDiscount()+"%");
		}

		holder.name.setText(item.getName().toLowerCase().contains("автомойка")?item.getName():"Автомойка "+item.getName());
		
	   if(!isCompany){
		holder.price.setVisibility(View.VISIBLE);
		holder.price.setVisibility(View.VISIBLE);
		holder.distance.setVisibility(View.VISIBLE);
		holder.priceLayout.setVisibility(View.VISIBLE);
		holder.address.setVisibility(View.VISIBLE);

		if(item.getPrice()!=null) {
			holder.price.setText("Кузов-салон от " + (item.getPrice() != null ? formatter.format(item.getPrice()).replaceAll(",", " ") + "" : ""));
			holder.tenge.setVisibility(View.VISIBLE);
		}
		else {
			holder.price.setText("");
			holder.tenge.setVisibility(View.GONE);
		}

		holder.review.setRating((float)item.getRating().doubleValue());
		if(item.getReviewCount() != null && item.getReviewCount() > 0) {
			holder.reviewCount.setVisibility(View.VISIBLE);
			holder.reviewCount.setText(item.getReviewCount() + " отзывов");
		}
		else{
			holder.reviewCount.setVisibility(View.GONE);
			holder.reviewCount.setText("");
		}

		if(!TextUtils.isEmpty(item.getAddress()) && !TextUtils.isEmpty(item.getCity())){
			holder.address.setText(Functions.getCityDescription(item.getCity())+", "+item.getAddress());
		}
		if(TextUtils.isEmpty(item.getAddress()))
			holder.address.setText(Functions.getCityDescription(item.getCity())+", "+"адрес не указан");

	   holder.distance.setVisibility(View.GONE);

		if(!TextUtils.isEmpty(item.getCompanyClientId())){
			holder.clientLayout.setVisibility(View.VISIBLE);
			if(item.getCompanyClientDeposit() != null)
				holder.discountClientText.setText(item.getCompanyClientDiscount()+"%");
			if(item.getCompanyClientDeposit() != null)
				holder.depositText.setText(formatter.format(item.getCompanyClientDeposit())+"");
			if(item.getCompanyClientBonus() != null)
				holder.bonusText.setText(formatter.format(item.getCompanyClientBonus())+"");
		}
		   else{
			holder.clientLayout.setVisibility(View.GONE);
		}


	  if(currentLoc != null && item.getLonLat() != null && item.getLonLat().size() > 0){
		 try{
		  Location washerLocation = new Location("A");
		  washerLocation.setLatitude(Double.parseDouble(item.getLonLat().get(1).toString()));
		  washerLocation.setLongitude(Double.parseDouble(item.getLonLat().get(0).toString()));
	      float dist = currentLoc.distanceTo(washerLocation);
	      holder.distance.setText(String.format("%s km", formatter.format(dist/1000).replaceAll(",", " ")));
	      if(useNoGPSOption){
	    	  holder.distance.setVisibility(View.GONE);
	      }
	      else{
	    	  holder.distance.setVisibility(View.VISIBLE);
	       }
		  }
		  catch(Exception e){
		  }
		 }	
		}
		else{
			holder.imageAvailability.setVisibility(View.GONE);
			holder.price.setVisibility(View.GONE);
			holder.reviewCount.setVisibility(View.GONE);
			holder.price.setVisibility(View.GONE);
			holder.reviewCount.setVisibility(View.GONE);
			holder.distance.setVisibility(View.GONE);
			holder.priceLayout.setVisibility(View.GONE);
			holder.address.setVisibility(View.GONE);
		}
					
		return view;
	}
	
	static class ViewHolder {
		 		 				
		@BindView(R.id.imgAvailability)
		ImageView imageAvailability;
		@BindView(R.id.txtWasherAddress)
		TextView address;
		@BindView(R.id.txtWasherName)
		TextView name;
		@BindView(R.id.txtPrice)
		TextView price;
		@BindView(R.id.txtRevCount)
		TextView reviewCount;
		@BindView(R.id.txtDistance)
		TextView distance;
		@BindView(R.id.priceLayout)
		View priceLayout;
		@BindView(R.id.bottomLayout)
		View bottomLayout;
		@BindView(R.id.discountLayout)
		View discount;
		@BindView(R.id.discountText)
		TextView discountText;
		@BindView(R.id.review)
		RatingBar review;
		@BindView(R.id.tenge)
		View tenge;

		@BindView(R.id.txtDeposit)
		TextView depositText;
		@BindView(R.id.txtBonus)
		TextView bonusText;
		@BindView(R.id.txtDiscount)
		TextView discountClientText;
		@BindView(R.id.clientLayout)
		View clientLayout;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
