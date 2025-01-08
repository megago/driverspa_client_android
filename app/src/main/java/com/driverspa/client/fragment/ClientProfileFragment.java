package com.driverspa.client.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.Reference;
import com.driverspa.activity.BaseActivity;
import com.driverspa.activity.GalleryPhotosActivity;
import com.driverspa.adapter.CarsGridAdapter;
import com.driverspa.assist.BaseAssist;
import com.driverspa.dialog.SingleSelectDialog;
import com.driverspa.model.CarItem;
import com.driverspa.model.ClientInfo;
import com.driverspa.model.Image;
import com.driverspa.model.PhotoParcelable;
import com.driverspa.model.User;
import com.driverspa.model.api.request.UserUpdateRequest;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.Converters;
import com.driverspa.util.Functions;
import com.driverspa.util.HttpClient;
import com.driverspa.util.RetrofitClient;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.TransformationCircle;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.PhotoPostRequestEvent;
import com.driverspa.util.otto.ws.CarDefinitionItemRequestEvent;
import com.driverspa.util.otto.ws.ClientPhotoPostRequestEvent;
import com.driverspa.util.otto.ws.ClientPhotoPostResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfRequestEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;
import com.driverspa.util.otto.ws.UserUpdateSelfRequestEvent;
import com.driverspa.util.otto.ws.UserUpdateSelfResponseEvent;
import com.driverspa.view.ExpandableGridView;

public class ClientProfileFragment extends ClientBaseFragment {
    String userId = UserPreferences.getUserId(BA.getContext());
    String phone = UserPreferences.getUserPhone(BA.getContext());

	public interface ActivityActions{
	}

	@InjectView(R.id.txtPhoneNo)
	TextView txtPhoneNo;
	@InjectView(R.id.btnSave)
	Button buttonSave;			
	@InjectView(R.id.clientName)
	EditText clientName;		
	@InjectView(R.id.imgClientAvatar)
	ImageView clientImage;
	@InjectView(R.id.txtCity)
	TextView cityText;
	@InjectView(R.id.images_count)
	TextView imagesCount;
	@InjectView(R.id.images_count_rect)
	View imagesCountRect;
	@InjectView(R.id.images_circle)
	View imagesCircle;

	private ActivityActions activityActions;
	ClientInfo client;
	User user;
	private ArrayList<PhotoParcelable> profilePhotos;

	@InjectView(R.id.grid_cars)
	ExpandableGridView gridCars;
	private CarsGridAdapter carAdapter;
	private ClientUserCarDefinitionFragment carDefinitionDialogFragment;

	SingleSelectDialog cityDialog;
	String selectedCity;
	ArrayList<Reference.City> allCities = BA.getReference().getCities();
	boolean cityDialogShown = false;

	@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_client_profile, container,false);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
		}
		
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.inject(this, view);
			Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			TextView titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
            titleView.setText("Профиль");
			titleView.setVisibility(View.VISIBLE);
	        setWaitScreen(true);
//			BA.getEventBus().post(new UserGetSelfRequestEvent(userId));
			selectedCity = UserPreferences.getCity(getActivity());
	    }

		@Override
		public void onDestroy() {
			super.onDestroy();		
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

	@Subscribe
	public void onUserSelfReceived(UserGetSelfResponseEvent event){
		setWaitScreen(false);
		user = event.getUser();

		profilePhotos = new ArrayList<PhotoParcelable>();
		if(user != null){			
	    for(Image photo: user.getImages()){
	    	PhotoParcelable parcelablePhoto = PhotoParcelable.newPhoto(photo.getPhotoOrm(user.getId()));
	    	profilePhotos.add(parcelablePhoto);
	    }
		txtPhoneNo.setText(Functions.maskPhoneNumber(TextUtils.isEmpty(user.getPhone()) ? phone : user.getPhone(), Converters.PHONE_PATTERN));
		clientName.setText(TextUtils.isEmpty(user.getFirstName()) ? "" : user.getFirstName());

		if(!TextUtils.isEmpty(selectedCity))
		   cityText.setText(Functions.getCityDescription(selectedCity));

			imagesCircle.setVisibility(View.GONE);
			imagesCount.setVisibility(View.GONE);
			imagesCountRect.setVisibility(View.GONE);

			if(!TextUtils.isEmpty(user.getAvatar())){
				HttpClient.getPicasso()
				.load(RetrofitClient.API_URL_IMAGES+user.getAvatar())
				.fit()	
				.centerCrop()
				.transform(new TransformationCircle(getActivity()))
				.placeholder(R.drawable.ic_no_image)
				.into(clientImage, new Callback() {
					@Override
					public void onSuccess() {
						int cnt = user.getImages().size();
						if(cnt > 1){
							imagesCircle.setVisibility(View.VISIBLE);
							imagesCount.setVisibility(View.VISIBLE);
							imagesCount.setText(cnt+"");
							imagesCountRect.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onError() {
						imagesCircle.setVisibility(View.GONE);
						imagesCount.setVisibility(View.GONE);
						imagesCountRect.setVisibility(View.GONE);
					}
				});
		  }
		  else{
			clientImage.setVisibility(View.VISIBLE);
			clientImage.setImageResource(R.drawable.ic_no_image);
		  }

			//Car adapter
			ViewGroup.LayoutParams layoutParamsCar = gridCars.getLayoutParams();
			WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			int width = display.getWidth()-(int) Functions.dipToPixels(getActivity(), 20);
			layoutParamsCar.width = width; //this is in pixels
			gridCars.setLayoutParams(layoutParamsCar);
			gridCars.setColumnWidth((display.getWidth() - (int) Functions.dipToPixels(getActivity(), 45)) / 4);

			final List<CarItem> carItemList = new ArrayList<CarItem>();
			carItemList.add(new CarItem());
			if(user.getCars() == null) user.setCars(new ArrayList<CarItem>());

			final Map<Integer,Integer> positions = new HashMap<>();
			int j = 0;
			for(int i = 0;i < user.getCars().size(); i++){

				if(user.getCars().get(i).getStatusDisabled() == null || (user.getCars().get(i).getStatusDisabled() != null && !user.getCars().get(i).getStatusDisabled())){
					carItemList.add(user.getCars().get(i));
					positions.put(j,i);
					j++;
				}
			}

			carAdapter = new CarsGridAdapter(getActivity(),carItemList);
			gridCars.setAdapter(carAdapter);
			gridCars.setExpanded(true);

			gridCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
					if (position == 0) {
						openCarDefinition();
					} else {
//						carAdapter.setSelectedPosition(position);
//						if (carAdapter.getSelectedView() != null)
//							carAdapter.getSelectedView().setSelected(false);
//						     carAdapter.setSelectedView(v);
//						v.setSelected(true);

						AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
						dialog.setTitle("Удалить машину?");
						dialog.setNegativeButton("Нет", null);
						dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								for(CarItem item : user.getCars()){
									if(item.getCarNumber().equals(carItemList.get(position).getCarNumber())
											&& item.getCarType().equals(carItemList.get(position).getCarType())
											&& item.getCarModel().equals(carItemList.get(position).getCarModel())){
										item.setStatusDisabled(true);
									}
								}

								carItemList.remove(position);
								carAdapter = new CarsGridAdapter(getActivity(), carItemList);
								gridCars.setAdapter(carAdapter);
								gridCars.setExpanded(true);
							}
						});
						dialog.show();
					}
				}
			});

	  }
	}

	@OnClick(R.id.btnSave)
	public void saveProfileInfo(){
		String clientNameStr = clientName.getText().toString();

		if(!(user.getCars() != null && user.getCars().size() > 0)){
			ToastUtil.display(getActivity(), "Добавьте хотя бы одну машину");
			return;
		}
		UserUpdateRequest userRequest =  new UserUpdateRequest();
		userRequest.setFirstName(clientNameStr);
		userRequest.setLastName("    ");
		userRequest.setCars(user.getCars());
		setWaitScreen(true);
		BA.getEventBus().post(new UserUpdateSelfRequestEvent(userId, userRequest));
	  }

	@Subscribe
	public void onUserSelfResponseReceived(UserUpdateSelfResponseEvent event){
		setWaitScreen(false);
		if(event != null && event.getData() != null && event.getData().getResponse() != null){
			ToastUtil.display(getActivity(),"Данные успешно сохранены");
		}
		else{
			ToastUtil.display(getActivity(),"Повторите еще раз");
		}
	}
	  private boolean validateProfileInfo(String str, View view, String warningMessage){
		   if(TextUtils.isEmpty(str) || str.length() <= 2){
			   ToastUtil.display(getActivity(), warningMessage);
			   view.requestFocus();
			   return false;
		   }
		  return true;
	  }
	  
	  @Subscribe
	  public void onPhotoPostRequested(PhotoPostRequestEvent event){
		  setWaitScreen(true);		  
		  BA.getEventBus().post(new ClientPhotoPostRequestEvent(event.getFile()));
	  }
	  	  	  
		public void profileViewAddMedia() {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			intent.putExtra(BaseActivity.OPENING_ANIMATION, false);
			startActivityForResult(intent, ActivityForResult.CLIENT_PHOTO_UPLOAD);
		}
		
		@Override
		public void onActivityResult (int requestCode, int resultCode, Intent data) {					
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == ActivityForResult.CLIENT_PHOTO_UPLOAD) {
				if (resultCode == Activity.RESULT_OK) {		
					String selectedImagePath = null;
					Uri selectedImageUri = data.getData();
					Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, null, null, null, null);
					if (cursor == null) { 
						selectedImagePath = selectedImageUri.getPath();
					} else {
						cursor.moveToFirst();
						int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
						selectedImagePath = cursor.getString(idx);
					}
					setWaitScreen(true);
					BA.getEventBus().post(new ClientPhotoPostRequestEvent(new File(selectedImagePath)));
				}
		    }
			else if ( requestCode == ActivityForResult.ACTIVITY_PHOTO_CHANGE ) {
				// check if user has changed photo or not
				if( resultCode == ActivityForResult.PHOTO_CHANGED_YES) {
					// update view as user info has changed
//			        setWaitScreen(true);
			        BA.getEventBus().post(new UserGetSelfRequestEvent(userId));
				}
			}else if ( requestCode == ActivityForResult.ACTIVITY_GALLERY_PHOTOS ) {
				// check if user has changed photo or not
				if( resultCode == ActivityForResult.GALLERY_PHOTO_CHANGED_YES) {
					// update view as user info has changed
			        setWaitScreen(true);
			        BA.getEventBus().post(new UserGetSelfRequestEvent(userId));
				}
			}


		}
				
		@Subscribe
		public void onPhotoPostReceived(ClientPhotoPostResponseEvent event){
			  setWaitScreen(false);
			  if(event.getResponse() != null){
				  if(BaseAssist.isSuccess(event.getResponse())){
					  ToastUtil.display(getActivity(), "Успешно загрузили фото");
				       setWaitScreen(true);
				       BA.getEventBus().post(new UserGetSelfRequestEvent(userId));
				  }
				  else{
					  ToastUtil.display(getActivity(), event.getResponse().getMessage());
				  }
			  }
			  else{				  
			  }
		  }
		
		@OnClick(R.id.imgClientAvatar)
		public void onPhotoChangeClick(){			
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
            b.setItems(R.array.photo_options, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    switch (i)
                    {
                        case 0:
                        	profileViewAddMedia();	     
                            break;
//                        case 1:
//	                  		  if(profilePhotos.size() > 1){
//	                  			startActivityForResult(UserPhotoChangeActivity.newIntent(getActivity(), userId, profilePhotos), ActivityForResult.ACTIVITY_PHOTO_CHANGE);
//	                  		  }
//	                  		  else{
//	                  			  ToastUtil.display(getActivity(), "Загрузите 1 и более фото для выбора в качестве аватара!");
//	                  		  }
//                            break;
                        case 1:
                        	if(profilePhotos.size() > 0){	
                        		startActivityForResult(GalleryPhotosActivity.newIntent(getActivity(), profilePhotos, 0, true), ActivityForResult.ACTIVITY_GALLERY_PHOTOS);
	                  		  }
	                  		  else{
	                  			  ToastUtil.display(getActivity(), "Загрузите 1 и более фото для просмотра!");
	                  		  }                        	
                            break;
                    }
                }
            });
//            b.setTitle("Фото");
//            b.setNeutralButton("Отменить", null);
            b.show();	
	   }

	public void openCarDefinition() {
		carDefinitionDialogFragment = new ClientUserCarDefinitionFragment();
		carDefinitionDialogFragment.setCancelable(false);
		carDefinitionDialogFragment.show(getActivity().getFragmentManager().beginTransaction(), "DialogFragment");
	}

	@Subscribe
	public void onCarDefinitionReceived(CarDefinitionItemRequestEvent event){
		user.getCars().add(event.getCar());
		List<CarItem> carItemList = new ArrayList<CarItem>();
		carItemList.add(new CarItem());
		if(user.getCars() == null) user.setCars(new ArrayList<CarItem>());
		for(int i = 0;i < user.getCars().size(); i++){
			if(user.getCars().get(i).getStatusDisabled() == null || (user.getCars().get(i).getStatusDisabled() != null && !user.getCars().get(i).getStatusDisabled())){
				carItemList.add(user.getCars().get(i));
			}
		}
		carAdapter = new CarsGridAdapter(getActivity(),carItemList);
		gridCars.setAdapter(carAdapter);
		gridCars.setExpanded(true);
		saveProfileInfo();
	}

//	@OnClick(R.id.cityLayout)
	public void showCityDialog(){
		if(!cityDialogShown){
			List<HashMap<String,String>> cityMapList = new ArrayList<HashMap<String,String>>();
			List<HashMap<String,String>> selectedCityMapList = new ArrayList<HashMap<String,String>>();
			HashMap<String,String> selectedCityMap = new HashMap<String,String>();
			selectedCityMap.put(SingleSelectDialog.ID, selectedCity);
			selectedCityMap.put(SingleSelectDialog.NAME, Functions.getCityDescription(selectedCity));
			selectedCityMapList.add(selectedCityMap);

			for(Reference.City city : allCities){
				HashMap<String,String> cityMap = new HashMap<String,String>();
				cityMap.put(SingleSelectDialog.ID, city.getCode());
				cityMap.put(SingleSelectDialog.NAME, city.getTitle());
				cityMapList.add(cityMap);
			}

			cityDialog = new SingleSelectDialog(getActivity(), cityMapList, "Выберите город", selectedCityMapList) {
				@Override
				public void onDismiss(HashMap<Integer, String> namesSelectedItm) {
					cityDialogShown = false;
					if(namesSelectedItm.get(0) != null){
						selectedCity = namesSelectedItm.get(0);
						user.setCity(selectedCity);
						cityText.setText(Functions.getCityDescription(selectedCity));
					}
				}
			};

			cityDialog.show();
			cityDialogShown = true;
		}
	}


}
