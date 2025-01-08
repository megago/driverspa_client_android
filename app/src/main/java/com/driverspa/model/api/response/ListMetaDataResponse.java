package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class ListMetaDataResponse {	
	private int limit;
    private String next;
    private int offset;
    private String previous;
    
    @SerializedName("total_count")
    private long totalCount;
    
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}    
}
