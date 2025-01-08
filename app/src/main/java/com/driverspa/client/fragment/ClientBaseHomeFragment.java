package com.driverspa.client.fragment;

import android.os.Bundle;
import com.driverspa.fragment.BaseFragment;
import com.driverspa.listener.OnActionbarTitleChange;

public abstract class ClientBaseHomeFragment extends BaseFragment implements OnActionbarTitleChange {

//	private static User user;
	private static String userId;	
	private boolean _hasLoadedOnce = false; // your boolean field
//	/**
//	 * Use user info. Fragments might leave this method empty
//	 * @param user - user to be displayed
//	 */
//	protected abstract void useUser(User user);

	/**
	 * Every fragment has title. This method returns resource id to that title.
	 * @return resource id of fragment title
	 */
	public abstract int getTitleResourceId();
	
	public abstract void load();

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (this.isVisible()) {
	        if (!isVisibleToUser && !_hasLoadedOnce) {
	            _hasLoadedOnce = true;
//	            load();
	        }
	    }
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTitle( getResources().getString(getTitleResourceId()) );
	}
		
}
