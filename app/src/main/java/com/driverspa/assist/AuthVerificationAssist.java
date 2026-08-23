package com.driverspa.assist;
import com.driverspa.R;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import com.driverspa.BA;
import com.driverspa.Reference;
import com.driverspa.model.BookingType;
import com.driverspa.model.User;
import com.driverspa.model.Washer;
import com.driverspa.model.WasherPublic;
import com.driverspa.model.api.request.InitialWasherCreateRequest;
import com.driverspa.model.api.response.AuthAdminVerificationResponseHolder;
import com.driverspa.model.api.response.AuthClientVerificationResponseHolder;
import com.driverspa.model.api.response.BaseResponseHolder;
import com.driverspa.model.api.response.WasherResponseHolder;
import com.driverspa.model.api.response.WashersResponseHolder;
import com.driverspa.util.L;
import com.driverspa.util.RetrofitClient.WashmeApi;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.DummyWashersRequestEvent;
import com.driverspa.util.otto.ws.AuthAdminVerificationRequestEvent;
import com.driverspa.util.otto.ws.AuthAdminVerificationResponseEvent;
import com.driverspa.util.otto.ws.AuthClientVerificationRequestEvent;
import com.driverspa.util.otto.ws.AuthClientVerificationResponseEvent;
import com.driverspa.util.otto.ws.LoginPasswordRequestEvent;
import com.driverspa.util.otto.ws.ResetPasswordRequestEvent;
import com.driverspa.util.otto.ws.WasherGetResponseEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AuthVerificationAssist extends BaseAssist {
	
	public AuthVerificationAssist(WashmeApi api, Bus eventBus) {
		super(api, eventBus);
	}

	@Subscribe
	public void onAuthRegisterRequested(AuthClientVerificationRequestEvent event) {
		
		logD("onAuthRegisterRequested");		
		getApi().authClientVerify(event.getAuthVerifyRequest(), new Callback<AuthClientVerificationResponseHolder>() {
			
			@Override
			public void success(AuthClientVerificationResponseHolder data, Response response) {
				if( isSuccess(data) ) {
					logD("onAuthRegisterRequested: success");							
					String userId = data.getResponse().getId();
					String userPhone = data.getResponse().getPhone();	
					String userToken = data.getResponse().getToken();
					boolean isAdmin = data.getResponse().isCompany();
					
//					if(isAdmin){
					if(1!=1){
					  displayToast(BA.str(R.string.err_number_used_wash));
					  getEventsBus().post(new AuthClientVerificationResponseEvent(null));
					}
					else{
						writeSelfToDb(data.getResponse());
						UserPreferences.onUserLogin(BA.getContext(), userId, userPhone, userToken);
						getEventsBus().post(new AuthClientVerificationResponseEvent(data));
					}
				} else {
					getEventsBus().post(new AuthClientVerificationResponseEvent(data));
					logD("onAuthRegisterRequested: error " + data.getStatus());
					onDataError(data);
				}
			}
			
			@Override
			public void failure(RetrofitError error) {
				getEventsBus().post(new AuthClientVerificationResponseEvent(null));
				onRetrofitError(error);
			}
		});
	}

	/**
	 * Password login for a returning client (check_phone -> has_password == true).
	 * Signs the user in and reuses the client verification response so the UI
	 * routes to the client home exactly like the OTP path.
	 */
	@Subscribe
	public void onLoginPasswordRequested(final LoginPasswordRequestEvent event) {
		logD("onLoginPasswordRequested");
		getApi().loginPassword(event.getRequest(), new Callback<AuthClientVerificationResponseHolder>() {
			@Override
			public void success(AuthClientVerificationResponseHolder data, Response response) {
				if (isSuccess(data)) {
					User user = data.getResponse();
					UserPreferences.onUserLogin(BA.getContext(), user.getId(), user.getPhone(), user.getToken());
					writeSelfToDb(user);
					getEventsBus().post(new AuthClientVerificationResponseEvent(data));
				} else {
					getEventsBus().post(new AuthClientVerificationResponseEvent(data));
					onDataError(data);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				getEventsBus().post(new AuthClientVerificationResponseEvent(null));
				onRetrofitError(error);
			}
		});
	}

	/**
	 * Forgot-password completion for a client (OTP code + new password). Signs
	 * the user in with the new password, then routes like the OTP path.
	 */
	@Subscribe
	public void onResetPasswordRequested(final ResetPasswordRequestEvent event) {
		logD("onResetPasswordRequested");
		getApi().resetPassword(event.getRequest(), new Callback<AuthClientVerificationResponseHolder>() {
			@Override
			public void success(AuthClientVerificationResponseHolder data, Response response) {
				if (isSuccess(data)) {
					User user = data.getResponse();
					UserPreferences.onUserLogin(BA.getContext(), user.getId(), user.getPhone(), user.getToken());
					writeSelfToDb(user);
					getEventsBus().post(new AuthClientVerificationResponseEvent(data));
				} else {
					getEventsBus().post(new AuthClientVerificationResponseEvent(data));
					onDataError(data);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				getEventsBus().post(new AuthClientVerificationResponseEvent(null));
				onRetrofitError(error);
			}
		});
	}


	@Subscribe
	public void onAuthVerificationRequested(final AuthAdminVerificationRequestEvent event) {
		
		logD("onAuthRegisterRequested");
		
		getApi().authAdminVerify(event.getAuthVerifyRequest(), new Callback<AuthAdminVerificationResponseHolder>() {
			
			@Override
			public void success(AuthAdminVerificationResponseHolder verifyData, Response response) {
				if( isSuccess(verifyData) ) {
					logD("onAuthRegisterRequested: success");
					final User user = verifyData.getResponse();
					String userId = user.getId();
					String userPhone = user.getPhone();
					String userToken = user.getToken();
					boolean isAdmin = user.isCompany();
					
//					if(!isAdmin){
					if(1!=1){
						displayToast("This user is registered as client before");
//						ToastUtil.displayAtTop(BA.getContext(), "Вы клиент!!! Не можете зайти как мойка!!!");
						getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
					}
					else{
//						UserPreferences.onAdminLogin(BA.getContext(), userId, userPhone, userToken);
						// write down user id and token
						writeSelfToDb(verifyData.getResponse());
						//First request washer list
						getApi().getOwnWashers(
								null,
								new Callback<WashersResponseHolder>() {
									@Override
									public void success(WashersResponseHolder data, Response response) {
									 if(isSuccess(data)){
										if (data.getResponse().getResult() != null && data.getResponse().getResult().size() > 0) {
											//if this user has washer, just post this list
											WasherPublic washerPublic = data.getResponse().getResult().get(0);
											//Request all data of the washer
											getApi().adminWasherInfo(washerPublic.getId(),
													new retrofit.Callback<WasherResponseHolder>() {

														@Override
														public void success(WasherResponseHolder data, Response response) {
														  if(isSuccess(data)) {
															  Washer washer = data.getResponse().getWasher();
															  //Create washer data preference
															  UserPreferences.putWasherData(BA.getContext(), washer.serialize());
															  UserPreferences.putCity(BA.getContext(),washer.getCity());
															  //Set singleton washer
															  BA.getEventBus().post(new WasherGetResponseEvent(washer));
															  //Response to the UI
															  getEventsBus().post(new AuthAdminVerificationResponseEvent((BaseResponseHolder) data));
														  }
															else{
															  getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
															  displayToast(data.getMessage());
															  UserPreferences.onAdminLogout(BA.getContext());
															  removeAllDbData();
														  }
															return;
														}

														@Override
														public void failure(RetrofitError error) {
															UserPreferences.onAdminLogout(BA.getContext());
															removeAllDbData();
															onRetrofitError(error);
															getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
														}
													});

										} else {
											InitialWasherCreateRequest request = new InitialWasherCreateRequest();
											double LAT = 43.240008;
											double LON = 76.912231;
											ArrayList<Double> lonLat = new ArrayList<Double>();
											try {
												request.setCity(UserPreferences.getCity(BA.getContext()));
												for (Reference.City city : BA.getReference().getCities()) {
													if (city.getCode().equals(request.getCity())) {
														LAT = Double.parseDouble(city.getLonLat().get(1));
														LON = Double.parseDouble(city.getLonLat().get(0));
														break;
													}
												}
											} catch (Exception e) {
												request.setCity("almaty");
											}

											lonLat.add(LON);
											lonLat.add(LAT);
											request.setLonLat(lonLat);
											request.setName(user.getCompanyName());
											ArrayList<Washer.BoxSettings> boxSettings = new ArrayList<Washer.BoxSettings>();
											for (int i = 0; i < 1; i++) {
												Washer.BoxSettings boxSetting = new Washer.BoxSettings();
												boxSetting.setBoxName(BA.str(R.string.box_1));
												boxSetting.setBookingType(BookingType.Online);
												boxSettings.add(boxSetting);
											}
											request.setBoxSettings(boxSettings);

											//if there is no washer for this user, create it
											getApi().createInitialWasher(
													request,
													new Callback<WasherResponseHolder>() {

														@Override
														public void success(WasherResponseHolder data, Response response) {
														 if(isSuccess(data)) {
															 Washer washer = data.getResponse().getWasher();
															 UserPreferences.putWasherData(BA.getContext(), washer.serialize());
															 BA.getEventBus().post(new WasherGetResponseEvent(washer));
															 //Response to the UI
															 getEventsBus().post(new AuthAdminVerificationResponseEvent((BaseResponseHolder) data));
														 }else{
															getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
															UserPreferences.onAdminLogout(BA.getContext());
															removeAllDbData();
														    displayToast(data.getMessage());
														 }
															return;
														}

														@Override
														public void failure(RetrofitError error) {
															UserPreferences.onAdminLogout(BA.getContext());
															removeAllDbData();
															onRetrofitError(error);
															getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
															return;
														}
													});
										}
									 }
										else{
										 UserPreferences.onAdminLogout(BA.getContext());
										 removeAllDbData();
										 displayToast(data.getMessage());
										 getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
									 }
									}

									@Override
									public void failure(RetrofitError error) {
										UserPreferences.onAdminLogout(BA.getContext());
										removeAllDbData();
										onRetrofitError(error);
										getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
										return;
									}
								});
//						getEventsBus().post(new AuthAdminVerificationResponseEvent(data));
					}					

				} else {
					getEventsBus().post(new AuthAdminVerificationResponseEvent(verifyData));
					logD("onAuthRegisterRequested: error " + verifyData.getStatus());
					onDataError(verifyData);
				}
			}
			
			@Override
			public void failure(RetrofitError error) {
				getEventsBus().post(new AuthAdminVerificationResponseEvent(null));
				onRetrofitError(error);
			}
		});
	}

	@Subscribe
	public void createDummyWashersRequested(DummyWashersRequestEvent event){
		for(final Washer request : event.getRequestList()) {


			//if there is no washer for this user, create it
			getApi().createTmpWasher(
					request,
					new Callback<WasherResponseHolder>() {

						@Override
						public void success(WasherResponseHolder data, Response response) {
							L.d("Success for " + request.getName());
							return;
						}

						@Override
						public void failure(RetrofitError error) {
							return;
						}
					});

			try {
				Thread.sleep(500);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

		}
	}
}
