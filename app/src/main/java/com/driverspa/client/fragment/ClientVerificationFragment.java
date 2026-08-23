package com.driverspa.client.fragment;

/**
 * @author Yerzhan
 *
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.squareup.otto.Subscribe;
import java.text.ParseException;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.assist.BaseAssist;
import com.driverspa.model.api.request.AuthClientRegistrationRequest;
import com.driverspa.model.api.request.AuthVerificationRequest;
import com.driverspa.util.Converters;
import com.driverspa.util.Functions;
import com.driverspa.util.MaskedWatcher;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.SmsRecoveryReceivedEvent;
import com.driverspa.util.otto.ws.AuthClientRegistrationRequestEvent;
import com.driverspa.util.otto.ws.AuthClientRegistrationResponseEvent;
import com.driverspa.util.otto.ws.AuthClientVerificationRequestEvent;
import com.driverspa.util.otto.ws.AuthClientVerificationResponseEvent;

public class ClientVerificationFragment extends ClientBaseFragment {
	public interface ActivityActions {
		public void openClientHomeActivity();
	}

	private final String TAG = "VerificationFragment";	
	private ActivityActions activityActions;
	String phone = UserPreferences.getUserPhone(BA.getContext());

	public static final int TIMER_SECONDS = 30;
	private long activationTime;

	@BindView(R.id.verificationCode)
	EditText verificationCode;
	@BindView(R.id.btnNext)
	Button nextButton;
	@BindView(R.id.btnRetry)
	Button retryButton;
	@BindView(R.id.txtPhoneNo)
	TextView txtPhoneNo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_client_verification, container, false);
		ButterKnife.bind(this, view);
		txtPhoneNo.setText(Functions.maskPhoneNumber(phone, Converters.PHONE_PATTERN));
		activationTime = System.currentTimeMillis();
		UserPreferences.putRecoverySmsSentTime(getActivity(), (new Date()).getTime());

        try {
            MaskedWatcher maskedWatcher = new MaskedWatcher(Converters.PIN_PATTERN);
            verificationCode.addTextChangedListener(maskedWatcher);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        verificationCode.setOnEditorActionListener(new OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_NEXT) {
		        	processVerificationRequest();
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
		
	@OnClick(R.id.btnNext)
	protected void processVerificationRequest() {
		String verificationCodeStr = verificationCode.getText().toString();
		verificationCodeStr = verificationCodeStr.replaceAll("-", "").replaceAll(" ", "");
		if( verificationCode.length() > 0) {					
			processVerify(verificationCodeStr);			
		} else {
			verificationCode.setError(BA.str(R.string.required_field));
		}
	}
	
	@OnClick(R.id.btnRetry)
	protected void retryVerificationRequest() {
		long currentTime = System.currentTimeMillis();
		long timeDiff = currentTime - activationTime;

		int seconds = (int) (timeDiff / 1000) % 60;

		if (seconds < TIMER_SECONDS){
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
			dialog.setTitle(BA.str(R.string.wait_a_bit));
            dialog.setMessage(BA.str(R.string.sms_every_30s));
			dialog.setPositiveButton(BA.str(R.string.ok_word),null);
			dialog.show();
		}
		else {
			activationTime = System.currentTimeMillis();
			setWaitScreen(true);
			// Resend the OTP over the channel/email the user chose on the
			// phone-entry screen (email by default).
			String channel = UserPreferences.getOtpChannel(BA.getContext());
			String email = UserPreferences.getOtpEmail(BA.getContext());
			String emailToSend = (email == null || email.isEmpty()) ? null : email;
			BA.getEventBus().post(new AuthClientRegistrationRequestEvent(
					new AuthClientRegistrationRequest(phone, channel, emailToSend)));
		}
	}
	
	private void processVerify(String code) {
		setWaitScreen(true);		
		BA.getEventBus().post(new AuthClientVerificationRequestEvent(new AuthVerificationRequest(phone,code)));
	}
	
	/**
	 * Runs when user's just logged in (Open home activity)
	 * @param event
	 */
	@Subscribe
	public void onAuthVerificationResponseReceived(AuthClientVerificationResponseEvent event) {
      setWaitScreen(false);
     if(event.getAuthLoginResponse() != null){
      if(BaseAssist.isSuccess(event.getAuthLoginResponse())){
	    UserPreferences.putRecoverySmsSentTime(getActivity(), 0);
	    // New users must set a password after activation. The flag is read by
	    // LoginActivity to route through the Create Password screen.
	    boolean passwordRequired = event.getAuthLoginResponse().getResponse() != null
	        && event.getAuthLoginResponse().getResponse().isPasswordRequired();
	    UserPreferences.putPasswordRequired(getActivity(), passwordRequired);
		activityActions.openClientHomeActivity();

	  }else{
		  ToastUtil.display(getActivity(), BA.str(R.string.err_try_again));
	   }
      }
	}	
	
	@Subscribe
	public void onAuthRetryRegistrationResponseReceived(AuthClientRegistrationResponseEvent event) {
		setWaitScreen(false);
		 if(event.getAuthLoginResponse().getStatus() != null && !event.getAuthLoginResponse().getStatus().equals("error")){
			 UserPreferences.putRecoverySmsSentTime(getActivity(), (new Date()).getTime());
		 }else{
		   ToastUtil.display(getActivity(), BA.str(R.string.err_try_again));
	   }
	}

	@Subscribe
	public void onSmsParsed(SmsRecoveryReceivedEvent event){
		verificationCode.setText(event.getCode());
		processVerify(event.getCode());
		UserPreferences.putRecoverySmsSentTime(getActivity(), 0);
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
