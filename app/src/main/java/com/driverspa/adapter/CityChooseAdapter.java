package com.driverspa.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.Reference;
import com.driverspa.model.Washer.Prices;
import com.driverspa.util.L;

public class CityChooseAdapter extends BaseDataAdapter<Reference.City> {

	private String selectedCityCode;


	public CityChooseAdapter(Context context) {
		super(context);		
	}	
		
	@Override
	public View getView(final int position, View view, ViewGroup parent) {		
		
		view = LayoutInflater.from(context).inflate(R.layout.list_city_item, parent, false);
		ViewHolder holder = new ViewHolder(view);			 			
		view.setTag(holder);

		Reference.City item = getItem(position);
		holder.description.setText(item.getTitle());
		if(!TextUtils.isEmpty(selectedCityCode) && selectedCityCode.equals(item.getCode())){
		   holder.checkBox.setVisibility(View.VISIBLE);
		}
		else
			holder.checkBox.setVisibility(View.GONE);

		return view;
	}
	
	static class ViewHolder {
		 		 						
		@InjectView(R.id.desc)
		TextView description;
		@InjectView(R.id.checkbox)
		View checkBox;
						
		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	public String getSelectedCityCode() {
		return selectedCityCode;
	}

	public void setSelectedCityCode(String selectedCityCode) {
		this.selectedCityCode = selectedCityCode;
	}
}
