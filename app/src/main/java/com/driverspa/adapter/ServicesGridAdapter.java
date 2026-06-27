package com.driverspa.adapter;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.driverspa.R;
import com.driverspa.model.ServiceItem;
import com.driverspa.util.Functions;

public class ServicesGridAdapter extends BaseAdapter {
    private Context context;
    List<ServiceItem> gridList;
    private View selectedView;
    private HashMap<Integer,Boolean> selectedPosition = new HashMap<Integer,Boolean>();;

    public ServicesGridAdapter(Context c, List<ServiceItem> gridList) {
    	context = c;
        this.gridList = gridList;
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
    
    public void setSelectedPosition(Integer position, boolean selected){
        selectedPosition.put(position, selected);
    }

    public List<ServiceItem> getData(){
        return gridList;
    }
    
    public HashMap<Integer,Boolean> getSelectedPosition(){
    	return selectedPosition;
    }
    
    public View getSelectedView(){
    	return selectedView;
    }
    
    public View getView(int position, View view, ViewGroup parent) {
    	view = LayoutInflater.from(context).inflate(R.layout.grid_service_item, parent, false);
		ViewHolder holder = new ViewHolder(view);			 			
		view.setTag(holder);

        final View gridLayoutView = view.findViewById(R.id.layoutGridItem);
        if(selectedPosition.get(position) != null && selectedPosition.get(position)) {
            holder.gridItem.setBackgroundResource(R.drawable.bg_service_tile_selected);
        }
        else{
            holder.gridItem.setBackgroundResource(R.drawable.bg_service_tile_normal);
        }

        ServiceItem item = gridList.get(position);
        holder.serviceName.setText(item.getServiceName());

		ViewGroup.LayoutParams layoutParams = holder.gridItem.getLayoutParams();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width = ((display.getWidth()-(int)Functions.dipToPixels(context, 32)))/4;
		layoutParams.width = width; //this is in pixels
		layoutParams.height = width*2/3; //this is in pixels
		holder.gridItem.setLayoutParams(layoutParams);

        return view;
    }

    static class ViewHolder {
			
		@BindView(R.id.service_name)
		TextView serviceName;
		
		@BindView(R.id.layoutGridItem)
		View gridItem;			

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}