package com.driverspa.client.fragment;

/**
 * @author Yerzhan
 *
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.squareup.otto.Subscribe;
import java.text.ParseException;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.api.request.AuthClientRegistrationRequest;
import com.driverspa.util.MaskedWatcher;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.otto.ws.AuthClientRegistrationRequestEvent;
import com.driverspa.util.otto.ws.AuthClientRegistrationResponseEvent;

public class ClientRegistrationFragment extends ClientBaseFragment {

	public interface ActivityActions {
		public void openClientVerification();
	}	
	private final String TAG = "RegistrationFragment";	
	private ActivityActions activityActions;
	
	@BindView(R.id.login_phone)
	EditText phone;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_client_registration, container, false);
		ButterKnife.bind(this, view);
//		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        try {
            MaskedWatcher maskedWatcher = new MaskedWatcher("### ###-##-##");
            phone.addTextChangedListener(maskedWatcher);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        phone.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_NEXT) {
		        	processRegistrationRequest();
		            return true;
		        }
		        return false;
		    }
		});        

		return view;
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
	
	@OnClick(R.id.btnOk)
	protected void processRegistrationRequest() {
		String phoneStr = phone.getText().toString();
		phoneStr = phoneStr.replaceAll("\\+", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("-", "").replaceAll(" ", "");
		
		if(phoneStr.length() > 0) {
			
			if(isPhoneValid(phoneStr) ) {
				processRegistration("+7"+phoneStr);
			} else {
				phone.setError("Неправильный номер телефона");
			}
		} else {
			phone.setError("Введите номер телефона");
		}
	}
	
	private void processRegistration(String phone) {
		setWaitScreen(true);
		BA.getEventBus().post(new AuthClientRegistrationRequestEvent(new AuthClientRegistrationRequest(phone)));
	}
	
	
	/**
	 * Runs when user's just registration (Open Verification activity)
	 * @param event
	 */
	@Subscribe
	public void onAuthRegistrationResponseReceived(AuthClientRegistrationResponseEvent event) {
		setWaitScreen(false);
		 if(event.getAuthLoginResponse().getStatus() != null && !event.getAuthLoginResponse().getStatus().equals("error")){			
		   activityActions.openClientVerification();
		 }else{
		   ToastUtil.display(getActivity(), "Ошибка, попробуйте еще раз");
	   }
	}
	
	/**
	 * Check if provided fields are valid
	 * @param phone - phone of user
	 * @return
	 */
	private boolean isPhoneValid(String phone) {
		if(phone.length() == 10)
		 return true;
		else
	     return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		BA.getEventBus().register(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		BA.getEventBus().unregister(this);
	}
}
