package com.driverspa.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.driverspa.R;
import com.driverspa.model.Washer.Review;

@SuppressLint({ "SimpleDateFormat", "ViewHolder" })
public class WasherReviewAdapter extends BaseDataAdapter<Review> {

	public WasherReviewAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		view = LayoutInflater.from(context).inflate(R.layout.list_washer_review_item, parent, false);
		ViewHolder holder = new ViewHolder(view);
		view.setTag(holder);

		Review item = getItem(position);
		holder.washerReviewText.setText(item.getText());
		if(item.getMark() > 0 && item.getMark() < 2){
		  holder.star1.setSelected(true);
		}
		else if(item.getMark() > 1 && item.getMark() < 3){
			holder.star1.setSelected(true);
			holder.star2.setSelected(true);
		}
		else if(item.getMark() > 2 && item.getMark() < 4){
			holder.star1.setSelected(true);
			holder.star2.setSelected(true);
			holder.star3.setSelected(true);
		}
		else if(item.getMark() > 3 && item.getMark() < 5){
			holder.star1.setSelected(true);
			holder.star2.setSelected(true);
			holder.star3.setSelected(true);
			holder.star4.setSelected(true);
		}
		else if(item.getMark() == 5){
			holder.star1.setSelected(true);
			holder.star2.setSelected(true);
			holder.star3.setSelected(true);
			holder.star4.setSelected(true);
			holder.star5.setSelected(true);
		}
		return view;
	}

	static class ViewHolder {

		@InjectView(R.id.txtReview)
		TextView washerReviewText;
		@InjectView(R.id.star1)
		ImageView star1;
		@InjectView(R.id.star2)
		ImageView star2;
		@InjectView(R.id.star3)
		ImageView star3;
		@InjectView(R.id.star4)
		ImageView star4;
		@InjectView(R.id.star5)
		ImageView star5;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
