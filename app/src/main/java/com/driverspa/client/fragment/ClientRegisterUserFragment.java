package com.driverspa.client.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.Reference;
import com.driverspa.adapter.CarsGridAdapter;
import com.driverspa.dialog.SingleSelectDialog;
import com.driverspa.model.CarItem;
import com.driverspa.model.User;
import com.driverspa.model.Washer;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.model.api.request.UserUpdateRequest;
import com.driverspa.util.Converters;
import com.driverspa.util.Functions;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.CarDefinitionItemRequestEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;
import com.driverspa.util.otto.ws.UserUpdateSelfRequestEvent;
import com.driverspa.util.otto.ws.UserUpdateSelfResponseEvent;
import com.driverspa.view.ExpandableGridView;

public class ClientRegisterUserFragment extends ClientBaseFragment {
    String phone = UserPreferences.getUserPhone(BA.getContext());
    String userId = UserPreferences.getUserId(BA.getContext());

	public interface ActivityActions {
		public void openClientHomeActivity();
	}

	public static ClientRegisterUserFragment newInstance(Washer washer, BookingRequest request) {
		ClientRegisterUserFragment fragment = new ClientRegisterUserFragment();
		return fragment;
	}
	
	@BindView(R.id.txtPhoneNo)
	TextView txtPhoneNo;
	@BindView(R.id.button_registration)
	Button buttonRegistration;
	@BindView(R.id.clientName)
	EditText clientName;
	@BindView(R.id.grid_cars)
	ExpandableGridView gridCars;
	@BindView(R.id.txtCity)
	TextView cityText;
	private CarsGridAdapter carAdapter;
	private ActivityActions activityActions;
	private ClientUserCarDefinitionFragment carDefinitionDialogFragment;
	SingleSelectDialog cityDialog;
	String selectedCity;
	ArrayList<Reference.City> allCities = BA.getReference().getCities();
	boolean cityDialogShown = false;

	private User user;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_client_registration_user, container,false);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
		}
		
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.bind(this, view);
		}

	@Subscribe
	public void onUserSelfReceived(UserGetSelfResponseEvent event) {
		setWaitScreen(false);
		user = event.getUser();
		clientName.setText(TextUtils.isEmpty(user.getFirstName())?"":user.getFirstName());
		txtPhoneNo.setText(Functions.maskPhoneNumber(phone, Converters.PHONE_PATTERN));

		//Car adapter
		ViewGroup.LayoutParams layoutParamsCar = gridCars.getLayoutParams();
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width = display.getWidth()-(int) Functions.dipToPixels(getActivity(), 20);
		layoutParamsCar.width = width; //this is in pixels
		gridCars.setLayoutParams(layoutParamsCar);
		gridCars.setColumnWidth((display.getWidth() - (int) Functions.dipToPixels(getActivity(), 45)) / 4);

		List<CarItem> carItemList = new ArrayList<CarItem>();
		carItemList.add(new CarItem());
		user.setCars(new ArrayList<CarItem>());
//		carItemList.addAll(user.getCars());
		carAdapter = new CarsGridAdapter(getActivity(),carItemList);
		gridCars.setAdapter(carAdapter);
		gridCars.setExpanded(true);
		gridCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (position == 0) {
					openCarDefinition();
				} else {
					carAdapter.setSelectedPosition(position);
					if (carAdapter.getSelectedView() != null)
						carAdapter.getSelectedView().setSelected(false);
					carAdapter.setSelectedView(v);
					v.setSelected(true);
				}
			}
		});

	}

	    @OnClick(R.id.button_registration)
	    public void onButtonRegistrationClicked(){
			String clientNameStr = clientName.getText().toString();
			if (TextUtils.isEmpty(clientNameStr)){
				clientName.requestFocus();
				clientName.setError("Введите имя");
				return;
			}

			if(!(user.getCars() != null && user.getCars().size() > 0)){
				ToastUtil.displayAtTop(getActivity(), "Добавьте хотя бы одну машину");
				return;
			}

			UserUpdateRequest userRequest =  new UserUpdateRequest();
			userRequest.setFirstName(clientNameStr);
			userRequest.setLastName("    ");
			userRequest.setCars(user.getCars());
			setWaitScreen(true);
			BA.getEventBus().post(new UserUpdateSelfRequestEvent(userId, userRequest));
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
	public void onUserSelfResponseReceived(UserUpdateSelfResponseEvent event){
		if(event != null && event.getData() != null && event.getData().getResponse() != null){
			setWaitScreen(false);
			activityActions.openClientHomeActivity();
		}
		else{
			ToastUtil.displayAtTop(getActivity(),"Повторите еще раз");
		}
 	}

	public void openCarDefinition() {
		carDefinitionDialogFragment = new ClientUserCarDefinitionFragment();
		carDefinitionDialogFragment.setCancelable(false);
		carDefinitionDialogFragment.show(getActivity().getSupportFragmentManager().beginTransaction(), "DialogFragment");
	}

	@Subscribe
	public void onCarDefinitionReceived(CarDefinitionItemRequestEvent event){
		if(user.getCars() != null && user.getCars().size() > 0)
			for(CarItem car : user.getCars()) {
				if (car.getCarNumber().toLowerCase().equals(event.getCar().getCarNumber().toLowerCase())) {
					ToastUtil.displayAtTop(getActivity(), "С таким номером у вас уже есть автомобиль");
					return;
				}
			}

		user.getCars().add(event.getCar());
		List<CarItem> carItemList = new ArrayList<CarItem>();
		carItemList.add(new CarItem());
		carItemList.addAll(user.getCars());
		carAdapter = new CarsGridAdapter(getActivity(),carItemList);
		gridCars.setAdapter(carAdapter);
		gridCars.setExpanded(true);
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
						cityText.setText(Functions.getCityDescription(selectedCity));
						user.setCity(selectedCity);
					}
				}
			};

			cityDialog.show();
			cityDialogShown = true;
		}
	}
}
