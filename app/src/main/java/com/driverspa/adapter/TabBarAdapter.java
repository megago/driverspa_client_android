package com.driverspa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.driverspa.R;
import com.driverspa.model.Washer;
import com.driverspa.model.api.response.WasherTimeTableResponse;
import static com.driverspa.util.Constants.*;

/**
 * Created by Yerzhan Tanatov on 22/12/15.
 */
public class TabBarAdapter extends BaseAdapter {

    Context context;
    protected List<Washer.BoxSettings> tabBars;
    int selectedTab = 0;

    public TabBarAdapter(Context context, List<Washer.BoxSettings> tabBars){
        this.context = context;
        this.tabBars = tabBars;
    }

    @Override
    public int getCount() {
        return tabBars.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.tabbar_item, parent,false);
        TextView tabItemTxt = (TextView)rowView.findViewById(R.id.txtTabBarItem);
        TextView tabItemTxtDetail = (TextView)rowView.findViewById(R.id.txtTabBarItemDetail);

        tabItemTxt.setText(tabBars.get(position).getBoxName());
        tabItemTxtDetail.setVisibility(View.VISIBLE);
        tabItemTxtDetail.setText(boxTypeMap.get(tabBars.get(position).getBookingType().getValue()));

        if(position == selectedTab){
            tabItemTxt.setTextColor(context.getResources().getColor(R.color.White));
            tabItemTxtDetail.setTextColor(context.getResources().getColor(R.color.White));
        }
        else{
            tabItemTxt.setTextColor(context.getResources().getColor(R.color.tab_inactive));
            tabItemTxtDetail.setTextColor(context.getResources().getColor(R.color.tab_inactive));
        }
        return rowView;
    }

    public void setSelectedTab(int selectedTab){
        this.selectedTab = selectedTab;
    }
}
