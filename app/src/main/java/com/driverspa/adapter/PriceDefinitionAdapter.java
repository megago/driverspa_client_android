package com.driverspa.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.BA;
import com.driverspa.R;

@SuppressLint({ "SimpleDateFormat", "ViewHolder" })
public class PriceDefinitionAdapter extends BaseDataAdapter<Integer> {

	public PriceDefinitionAdapter(Context context) {
		super(context);		
	}	
		
	@Override
	public View getView(int position, View view, ViewGroup parent) {		
	 	
		view = LayoutInflater.from(context).inflate(R.layout.list_price_definition_item, parent, false);
		ViewHolder holder = new ViewHolder(view);			 			
		view.setTag(holder);			
		
		Integer carTypeItem = getItem(position);
		final HashMap<Integer,String> allCarTypes = BA.getReference().getCarType();
		String carTypeDesc = allCarTypes.get(carTypeItem);
		holder.carTypeTxt.setText(carTypeDesc);
		
		return view;
	}
	
	static class ViewHolder {
		 		 						
		@BindView(R.id.txtCarType)
		TextView carTypeTxt;
				
		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
