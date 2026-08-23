package com.driverspa.client.fragment;
import com.driverspa.R;
import com.driverspa.BA;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.driverspa.listener.OnActionbarTitleChange;

public abstract class ClientBaseFragment extends Fragment {
	
	private OnActionbarTitleChange actionbarTitleChange;
	private ProgressBar progressBar;
	private ProgressDialog pd = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	protected void addProgressBar(ListView listView) {		
//		getProgressBar().setVisibility(View.VISIBLE);
		listView.setEmptyView(getProgressBar());
	}

	protected void addEmptyTextView(ListView listView,String text) {
		View view = listView.getEmptyView();
		if(view != null)
		   view.setVisibility(View.GONE);
		listView.setEmptyView(getEmtpyTextView(text));
	}
	
	protected void removeProgressBar() {
		ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
		root.removeView(progressBar);
	}
	
	protected void addProgressBar(GridView gridView) {
		gridView.setEmptyView(getProgressBar());
	}
	
	private ProgressBar getProgressBar() {
		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		flp.gravity = Gravity.CENTER;

		// Create a progress bar to display while the list loads
		progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyle);
		progressBar.setIndeterminate(true);

		// Must add the progress bar to the root of the layout
		ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
		root.addView(progressBar, flp );
		
		return progressBar;
	}

	private TextView getEmtpyTextView(String text){
		TextView textView;
		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		flp.gravity = Gravity.CENTER;

		// Create a progress bar to display while the list loads
		textView = new TextView(getActivity());
		textView.setText(text);

		// Must add the progress bar to the root of the layout
		ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
		root.addView(textView, flp );
		
		return textView;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		actionbarTitleChange = (OnActionbarTitleChange) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		actionbarTitleChange = null;
	}

	/**
	 * Sets action bar title
	 */
	public void setTitle(String title) {
		if ( actionbarTitleChange != null )
			actionbarTitleChange.setTitle(title);
	}
	
    void setWaitScreen(boolean set) {
    	if(pd == null) {
			pd = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_LIGHT);
			pd.setTitle("");
			pd.setIndeterminate(true);
			pd.setMessage(BA.str(R.string.loading_dots));
		}

	  try {
		  if (set) pd.show();
		  else pd.dismiss();
	  }
	  catch(Exception e){}
     }

	boolean isWaitingScreenActive(){
		return pd!=null&&pd.isShowing()?true:false;
	}
}
