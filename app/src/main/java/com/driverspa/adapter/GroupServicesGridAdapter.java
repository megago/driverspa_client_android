package com.driverspa.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.dialog.MultipleSelectDialog;
import com.driverspa.model.Washer;
import com.driverspa.util.Functions;

public class GroupServicesGridAdapter extends BaseAdapter {
    private Context context;
    List<Washer.GroupMenu> gridList;
    private View selectedView;
    private HashMap<Integer,Boolean> selectedPosition = new HashMap<Integer,Boolean>();;
    final List<HashMap<String,String>> servicesMapList = new ArrayList<HashMap<String,String>>();
    final HashMap<Integer,String> allServices = BA.getReference().getServices();

    public GroupServicesGridAdapter(Context c, List<Washer.GroupMenu> gridList) {
    	context = c;
        this.gridList = gridList;
        for (Map.Entry<Integer,String> entry : allServices.entrySet()) {
            HashMap<String,String> serviceMap = new HashMap<String,String>();
            Integer key = entry.getKey();
            String value = entry.getValue();
            serviceMap.put(MultipleSelectDialog.ID, Integer.toString(key));
            serviceMap.put(MultipleSelectDialog.NAME, value);
            servicesMapList.add(serviceMap);
        }
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
//        selectedPosition.clear();
        selectedPosition.put(position, selected);
    }

    public List<Washer.GroupMenu> getData(){
        return gridList;
    }
    
    public HashMap<Integer,Boolean> getSelectedPosition(){
    	return selectedPosition;
    }
    
    public View getSelectedView(){
    	return selectedView;
    }
    
    public View getView(int position, View view, ViewGroup parent) {
    	view = LayoutInflater.from(context).inflate(R.layout.grid_group_service_item, parent, false);
		ViewHolder holder = new ViewHolder(view);			 			
		view.setTag(holder);

        final View gridLayoutView = view.findViewById(R.id.layoutGridItem);
        if(selectedPosition.get(position) != null && selectedPosition.get(position)) {
            holder.gridItem.setBackgroundResource(R.drawable.button_grid_item_drawable_cars_pressed);
        }
        else{
            holder.gridItem.setBackgroundResource(R.drawable.background_button_grid_item_cars);
        }

        final Washer.GroupMenu item = gridList.get(position);
        holder.serviceName.setText(item.getName());
        holder.serviceName.setTypeface(null, Typeface.NORMAL);
        String serviceNames = "";
        if(item.getServices() != null) {
            for (int i = 0; i < item.getServices().size(); i++) {
                serviceNames = ((i == 0) ? "" : (serviceNames + " + ")) + ((BA.getReference().getServices().get(item.getServices().get(i))));
            }
        }

        holder.serviceDesc.setText(serviceNames);

        final String dialogServiceNames = serviceNames;
        holder.dotDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
                dialog.setTitle(item.getName());
                dialog.setMessage(dialogServiceNames);
                dialog.setPositiveButton("Ок", null);
                dialog.show();
            }
        });
        if(TextUtils.isEmpty(serviceNames)){
            holder.dotDetail.setVisibility(View.GONE);
        }
        else{
            holder.dotDetail.setVisibility(View.VISIBLE);
        }


//        if(item.ge() == 0){
//            holder.serviceName.setTextColor(context.getResources().getColor(R.color.BlueLight));
//            holder.serviceName.setTypeface(null, Typeface.ITALIC);
//            holder.serviceName.setText(item.getServiceName()+" - 0тг");
//        }

		ViewGroup.LayoutParams layoutParams = holder.gridItem.getLayoutParams();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width = ((display.getWidth()-(int) Functions.dipToPixels(context, 32)))/4;
		layoutParams.width = width; //this is in pixels
		layoutParams.height = width*2/3; //this is in pixels
		holder.gridItem.setLayoutParams(layoutParams);

        return view;
    }

    static class ViewHolder {
			
		@InjectView(R.id.service_name)
		TextView serviceName;
		@InjectView(R.id.service_desc)
		TextView serviceDesc;

		@InjectView(R.id.layoutGridItem)
		View gridItem;
		@InjectView(R.id.dot_detail)
		View dotDetail;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}