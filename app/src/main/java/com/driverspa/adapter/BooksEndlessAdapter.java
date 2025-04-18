package com.driverspa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import com.driverspa.R;
import com.driverspa.model.BookInfo;

/**
 * User: Yerzhan
 * Date: 11.11.14
 */
public class BooksEndlessAdapter extends EndlessAdapter {

    private Context context;    
    private BooksAdapter adapter;
    private List<BookInfo> currentBatch;
    private View pendingView;

    public BooksEndlessAdapter(Context context) {
        super(new BooksAdapter(context));
        this.adapter = (BooksAdapter) getWrappedAdapter();
        this.context = context;
        this.currentBatch = new ArrayList<BookInfo>();
    }

    public void setItems(List<BookInfo> items) {        
        adapter.setItems(items);
        restartAppending();
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
    	
//        currentBatch = personManager.getHistory(s, e);
        return currentBatch.size() > 0;
    }

    @Override
    protected void appendInBackground() {
        if (currentBatch != null) {
            adapter.addItems(currentBatch);
            currentBatch.clear();
        }
    }

    @Override
    protected View getPendingView(ViewGroup paramViewGroup) {
        if (this.pendingView == null)
            this.pendingView = View.inflate(context, R.layout.list_progress, null);
        return this.pendingView;
    }


    public static class BooksAdapter extends BaseAdapter {

        private List<BookInfo> items;
        private LayoutInflater inflater;


        public BooksAdapter(Context context) {
            items = new ArrayList<BookInfo>();
            inflater = LayoutInflater.from(context);
        }

        public void setItems(List<BookInfo> newItems) {            
            items = newItems;
            notifyDataSetChanged();
        }

        public void addItems(List<BookInfo> newItems) {            
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        public void clear() {
            items.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.list_books_item, null);

            return view;
        }
    }
}
