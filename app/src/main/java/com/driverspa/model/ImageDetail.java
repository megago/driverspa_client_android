package com.driverspa.model;

import java.util.ArrayList;

public class ImageDetail {
	private ArrayList<String> size;
	private String url;

	public ImageDetail(){
		
	}
	public ImageDetail(String url){
		this.url = url;
	}
	
	public ArrayList<String> getSize() {
		return size;
	}

	public void setSize(ArrayList<String> size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}