package com.driverspa.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.driverspa.R;

/**
 * Created by Yerzhan Tanatov on 04.05.15.
 */
public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public ProgressViewHolder(View v) {
        super(v);
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
    }
}
