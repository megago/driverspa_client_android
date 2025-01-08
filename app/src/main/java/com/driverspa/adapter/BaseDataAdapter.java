package com.driverspa.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDataAdapter<T> extends BaseAdapter {
	
	protected final Context context;
	protected ArrayList<T> list;
	private boolean hasData;
	boolean isLoad = false;    	
	
	public BaseDataAdapter(Context context) {
		super();
		
		this.context = context;
		this.list = new ArrayList<T>();
		hasData = false;
	}

	public void add(ArrayList<T> list){
		if( list == null ){
			list = new ArrayList<T>();
		}
		this.list.addAll(list);
		
		setHasData(true);
	}
	
	public void addItem(T item) {
		if( list == null ) {
			list = new ArrayList<T>();
		}
		list.add(item);
		setHasData(true);
	}
	
	public void clear() {
		list.clear();
		setHasData(false);
	}
	
	public void set(ArrayList<T> list) {
		this.list = list;
		setHasData(true);
	}
	
	public void set(List<? extends T> list) {
		this.list = new ArrayList<T>(list);
		setHasData(true);
	}
	
	public ArrayList<T> getList() {
		return list;
	}
	
	@Override
	public abstract View getView(int position, View view, ViewGroup parent); /*{
		//Log.d(this.getClass().getName(), "getView");
		return view;
	}*/

	@Override
	public int getCount() {
		if(list == null)
			return 0;
		else
		   return list.size();
	}
	
	@Override
	public T getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public boolean hasData() {
		return hasData;
	}
	
	protected void setHasData(boolean hasData) {
		this.hasData = hasData;
	}
	
	public boolean getPreloadStatus() {
		return isLoad;
	}

	public void setPreloadStatus(boolean status) {
		this.isLoad = status;
	}
	
}
