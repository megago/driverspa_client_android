package com.driverspa.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.Washer.Prices;

@SuppressLint({ "SimpleDateFormat", "ViewHolder" })
public class PriceDefinitionDetailAdapter extends BaseDataAdapter<Prices> {

	public PriceDefinitionDetailAdapter(Context context) {
		super(context);		
	}	
		
	@Override
	public View getView(final int position, View view, ViewGroup parent) {		
		
		view = LayoutInflater.from(context).inflate(R.layout.list_price_definition_detail_item, parent, false);
		ViewHolder holder = new ViewHolder(view);			 			
		view.setTag(holder);			
		Prices price = getItem(position);
		holder.priceItem.setText((price.getPrice()!=null?price.getPrice():0)+"0");
		holder.timeItem.setText(price.getTime()!=null?""+price.getTime():"0");
		HashMap<Integer,String> allServices = BA.getReference().getServices();
		holder.txtService.setText(allServices.get(price.getType()));
		
		//we need to update adapter once we finish with editing
        holder.priceItem.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final EditText priceEditText = (EditText) v;
                    getList().get(position).setPrice(Double.parseDouble(priceEditText.getText().toString()));
                }
            }
        });

        holder.timeItem.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final EditText timeEditText = (EditText) v;
                    getList().get(position).setTime(Integer.parseInt(timeEditText.getText().toString()));
                }
            }
        });

		return view;
	}
	
	static class ViewHolder {
		 		 						
		@BindView(R.id.txtService)
		TextView txtService;
		@BindView(R.id.priceItem)
		EditText priceItem;
		@BindView(R.id.timeItem)
		EditText timeItem;
						
		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
