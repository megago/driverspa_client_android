package com.driverspa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.CarType;
import com.driverspa.model.ServiceMenuModel;
import com.driverspa.model.Washer.Prices;

public class PricesExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<ServiceMenuModel> carItems;
    private HashMap<Integer,String> cars;
    private HashMap<Integer,String> services;
    
    public PricesExpandableListAdapter(Context context, List<ServiceMenuModel> carItems, HashMap<Integer,String> cars, HashMap<Integer,String> services) {
        this._context = context;        
        this.carItems = carItems;
        this.cars = cars;
        this.services = services;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
    	return carItems.get(groupPosition).getPrices().get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_cars_item, null);
        }
          
        TextView childTextTitle = (TextView) convertView.findViewById(R.id.text_service);
        TextView childTextPrice = (TextView) convertView.findViewById(R.id.text_price);
                           
        Prices item = carItems.get(groupPosition).getPrices().get(childPosition);
        childTextTitle.setText(services.get(item.getType()));
        String value = Math.round(item.getPrice()) + " ₸";
        if(item.getTime() != null && item.getTime() > 0)
            value += " · " + item.getTime() + BA.str(R.string.space_min);
        childTextPrice.setText(value);
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
    	return this.carItems.get(groupPosition).getPrices().size();        
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.carItems.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.carItems.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_cars_group, null);
        }
        
//        ExpandableListView eLV = (ExpandableListView) parent;
//        eLV.expandGroup(groupPosition);
                
        TextView parentTextTitle = (TextView) convertView.findViewById(R.id.text_car_type);
        ImageView carTypeIcon = (ImageView) convertView.findViewById(R.id.car_type_icon);
        int carType = carItems.get(groupPosition).getCarType();
        parentTextTitle.setText(cars.get(carType));
        carTypeIcon.setImageResource(iconForCarType(carType));
         
        return convertView;
    }
 
    private int iconForCarType(int carTypeId) {
        CarType type = CarType.toEnum(carTypeId);
        if (type == null)
            return R.drawable.background_button_sedan;
        switch (type) {
            case Mini:
            case Universal:
            case Crossover: return R.drawable.background_button_crossover;
            case MiniJeep:
            case Jeep:      return R.drawable.background_button_jeep;
            case Moto:      return R.drawable.background_button_moto;
            case Minivan:
            case Minibus:   return R.drawable.background_button_minbus;
            default:        return R.drawable.background_button_sedan;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}