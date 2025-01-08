package com.driverspa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import com.driverspa.R;
import com.driverspa.model.PhotoParcelable;
import com.driverspa.util.HttpClient;
import com.driverspa.util.RetrofitClient;

public class UserPhotosAdapter extends BaseDataAdapter<PhotoParcelable> {

	public UserPhotosAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		PhotoParcelable item = getItem(position);

		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.profile_view_media_grid_item_photo, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.image);
			holder.progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
   		
		HttpClient.getPicasso()
		.load(RetrofitClient.API_URL_IMAGES+item.getThumb1Url())
		.fit()
		.centerCrop()
		.into(holder.image, new Callback(){
			@Override
			public void onSuccess() {
				if( holder.progressBar != null )
					holder.progressBar.setVisibility(View.GONE);
			}
			@Override
			public void onError() {
				if( holder.progressBar != null )
					holder.progressBar.setVisibility(View.GONE);
			}
		});

		return view;
	}

	static class ViewHolder {
		ImageView image;
		ProgressBar progressBar;
	}

}
