package com.driverspa.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;

public class SplashInfoFragment extends BaseFragment {

	public static final String INDEX = "INDEX";

	public interface ActivityActions {
		public void nextPage();
		public void start();
	}

	@BindView(R.id.title)
	TextView title;
	@BindView(R.id.image)
	ImageView image;
	@BindView(R.id.main_text)
	TextView mainText;
	@BindView(R.id.parentView)
	View parentView;
	int index = 0;

	public static SplashInfoFragment init(int index) {
		SplashInfoFragment fragment = new SplashInfoFragment();
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

	ActivityActions activityActions;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    index = getArguments().getInt(INDEX);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_info_item, container, false);
		ButterKnife.bind(this,view);
		switch (index){
			case 0:
				parentView.setVisibility(View.VISIBLE);
				image.setImageResource(R.drawable.ic_splash_info_1);
				mainText.setText(BA.str(R.string.onboarding_find_wash));
				break;
			case 1:
				parentView.setVisibility(View.VISIBLE);
				image.setImageResource(R.drawable.ic_splash_info_2);
				mainText.setText(BA.str(R.string.onboarding_compare));
				break;
			case 2:
				parentView.setVisibility(View.VISIBLE);
				title.setText(BA.str(R.string.for_wash_owners));
				image.setImageResource(R.drawable.ic_splash_info_3);
				mainText.setText(BA.str(R.string.onboarding_attract));
				break;
		}
        return view;
    }

	@Override
	public void onResume() {
		super.onResume();
		BA.getEventBus().register(this);
	};
	
	@Override
	public void onPause() {
		super.onPause();
		BA.getEventBus().unregister(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activityActions = (ActivityActions) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activityActions = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
