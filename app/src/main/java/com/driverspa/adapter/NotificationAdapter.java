package com.driverspa.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.HashMap;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.driverspa.R;
import com.driverspa.model.Notification;
import com.driverspa.model.Washer.Review;
import com.driverspa.util.Functions;
import com.driverspa.util.L;

public class NotificationAdapter extends BaseDataAdapter<Notification> {

	public NotificationAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		view = LayoutInflater.from(context).inflate(R.layout.list_notification_item, parent, false);
		ViewHolder holder = new ViewHolder(view);
		view.setTag(holder);

		Notification item = getItem(position);
		HashMap<String,String> timeTSMap = Functions.formatUTCDate(item.getTS());

		PrettyTime p = new PrettyTime(new Locale("ru"));
		if(DateUtils.isToday(Functions.getUTCDate(item.getTS()).getTime()))
		   holder.date.setText(p.format(Functions.getUTCDate(item.getTS())));
		else
		   holder.date.setText(p.format(Functions.getUTCDate(item.getTS()))+" ("+timeTSMap.get(Functions.DATE)+" в "+timeTSMap.get(Functions.TIME)+")");

		holder.text.setText(item.getText());

		return view;
	}

	static class ViewHolder {

		@InjectView(R.id.text)
		TextView text;
		@InjectView(R.id.date)
		TextView date;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
