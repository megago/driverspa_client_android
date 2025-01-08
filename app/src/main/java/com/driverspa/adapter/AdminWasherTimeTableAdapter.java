package com.driverspa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.driverspa.R;
import com.driverspa.model.BookInfo;
import com.driverspa.util.Functions;
import com.driverspa.util.L;

import static com.driverspa.util.Constants.*;

public class AdminWasherTimeTableAdapter extends BaseAdapter {
    private Context context;
    List<HashMap<String,String>> gridList;
    private View selectedView;
    private int selectedPosition;
	HashMap<String,BookInfo> bookingObjects;
    public AdminWasherTimeTableAdapter(Context c, List<HashMap<String, String>> gridList,HashMap<String,BookInfo> bookingObjects) {
    	context = c;
        this.gridList = gridList;
		this.bookingObjects = bookingObjects;
    }

    public int getCount() {
        return gridList.size();
    }

    public Object getItem(int position) {
        return gridList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }
    
    public void setSelectedView(View selectedView){
    	this.selectedView = selectedView; 
    }
    
    public void setSelectedPosition(int position){
    	this.selectedPosition = position; 
    }
    
    public int getSelectedPosition(){
    	return selectedPosition;
    }
    
    public View getSelectedView(){
    	return selectedView;
    }
    
    public View getView(int position, View view, ViewGroup parent) {
    	view = LayoutInflater.from(context).inflate(R.layout.grid_admin_time_item, parent, false);
		ViewHolder holder = new ViewHolder(view);			 			
		view.setTag(holder);			
		
		if(position == selectedPosition) view.setSelected(true);
		holder.txtKey.setVisibility(View.GONE);
		holder.txtFree.setVisibility(View.GONE);
		holder.txtStatus.setVisibility(View.GONE);

		if(gridList.get(position).get(GRID_STATUS).equals(SLOT_AVAILABLE)){
			holder.imageCar.setVisibility(View.GONE); 
			holder.txtTime.setVisibility(View.VISIBLE);
			holder.txtTime.setText(gridList.get(position).get(GRID_TITLE));
			if(Boolean.parseBoolean(gridList.get(position).get(GRID_EXPIRED))){
				view.setEnabled(false);
			}
		}
		else if(gridList.get(position).get(GRID_STATUS).equals(SLOT_NOT_AVAILABLE)){
			view.setEnabled(false);
			holder.gridItem.setEnabled(false);
			holder.imageCar.setVisibility(View.GONE);
			holder.txtFree.setVisibility(View.GONE);
			if(Boolean.parseBoolean(gridList.get(position).get(GRID_EXPIRED))){
				view.setEnabled(false);
			}
		}
		else{
			holder.txtTime.setText(gridList.get(position).get(GRID_TITLE));
			if(bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID)) != null){
				holder.txtKey.setVisibility(View.VISIBLE);
				String clientKey = bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID)).getClientKey();

				holder.txtKey.setText(clientKey.replace("(", "\n("));
			}
			else
				holder.txtKey.setVisibility(View.GONE);

			view.setEnabled(false);
			holder.gridItem.setEnabled(false);
			holder.imageCar.setVisibility(View.GONE);

			BookInfo item = bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID));
			if(!item.getPaid()){
				holder.notPaid.setVisibility(View.VISIBLE);
			}
			else{
				holder.notPaid.setVisibility(View.GONE);
			}

			if(gridList.get(position).get(GRID_BOOK_ID) != null && bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID)) != null) {
				BookInfo bookItem = bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID));
				if(!bookItem.isQueued()) holder.online.setVisibility(View.VISIBLE);
				else holder.online.setVisibility(View.GONE);
				holder.txtFree.setText(bookStatus.get(bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID)).getStatus()));
				holder.txtStatus.setText(bookStatus.get(bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID)).getStatus()));
				if (bookingObjects.get(gridList.get(position).get(GRID_BOOK_ID)).getStatus().equals(PENDING))
					holder.txtStatus.setTextColor(Color.RED);
				else
					holder.txtStatus.setTextColor(context.getResources().getColor(R.color.BlueLight));
			}

			holder.innerLayout.setBackgroundColor(context.getResources().getColor(R.color.admin_grid_item_busy));

			holder.txtStatus.setVisibility(View.VISIBLE);
		}
		
		ViewGroup.LayoutParams layoutParams = holder.gridItem.getLayoutParams();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);
		int width = ((display.getWidth()-(int)Functions.dipToPixels(context, 40)))/4;
		if(tabletSize)
			width = ((display.getWidth()-(int)Functions.dipToPixels(context, 40)))/6;
		layoutParams.width = width; //this is in pixels
		layoutParams.height = width; //this is in pixels
		holder.gridItem.setLayoutParams(layoutParams);


        return view;
    }

    static class ViewHolder {
		@InjectView(R.id.imgCar)
		ImageView imageCar;
		@InjectView(R.id.txtTime)
		TextView txtTime;	
		@InjectView(R.id.txtFree)
		TextView txtFree;
		@InjectView(R.id.txtKey)
		TextView txtKey;
		@InjectView(R.id.txtStatus)
		TextView txtStatus;
		@InjectView(R.id.layoutGridItem)
		View gridItem;
		@InjectView(R.id.innerLayout)
		View innerLayout;
		@InjectView(R.id.online)
		View online;
		@InjectView(R.id.notPaid)
		View notPaid;


		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}