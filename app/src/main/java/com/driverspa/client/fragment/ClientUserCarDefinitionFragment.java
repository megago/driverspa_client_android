package com.driverspa.client.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.CarItem;
import com.driverspa.model.CarType;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.otto.ws.CarDefinitionItemRequestEvent;

public class ClientUserCarDefinitionFragment extends DialogFragment {

	CarType selectedCarType;

	@InjectView(R.id.btnSedan)
	View buttonSedan;
	@InjectView(R.id.btnMoto)
	View buttonMoto;
	@InjectView(R.id.btnJeep)
	View buttonJeep;
	@InjectView(R.id.btnBigJeep)
	View buttonBigJeep;
	@InjectView(R.id.btnMinbus)
	View buttonMinbus;		
	@InjectView(R.id.carMark)
	EditText carMark;	
	@InjectView(R.id.carNo)
	EditText carNo;
	
	private View.OnClickListener buttonSelector = new View.OnClickListener() {
		@Override
		public void onClick(View view) {					
			if ( view.getId() == buttonSedan.getId() && !buttonSedan.isSelected()){
				buttonSedan.setSelected(true);
				buttonJeep.setSelected(false);
				buttonBigJeep.setSelected(false);
				buttonMinbus.setSelected(false);
				buttonMoto.setSelected(false);
				selectedCarType = CarType.Sedan;
			}
			else if ( view.getId() == buttonJeep.getId()  && !buttonJeep.isSelected()){
				buttonSedan.setSelected(false);
				buttonJeep.setSelected(true);
				buttonBigJeep.setSelected(false);
				buttonMinbus.setSelected(false);
				buttonMoto.setSelected(false);
				selectedCarType = CarType.Crossover;
			}
			else if ( view.getId() == buttonBigJeep.getId()  && !buttonBigJeep.isSelected()){
				buttonSedan.setSelected(false);
				buttonJeep.setSelected(false);
				buttonBigJeep.setSelected(true);
				buttonMinbus.setSelected(false);
				buttonMoto.setSelected(false);
				selectedCarType = CarType.Jeep;
			}
			else if ( view.getId() == buttonMoto.getId()  && !buttonMoto.isSelected()){
				buttonSedan.setSelected(false);
				buttonJeep.setSelected(false);
				buttonBigJeep.setSelected(false);
				buttonMinbus.setSelected(false);
				buttonMoto.setSelected(true);
				selectedCarType = CarType.Moto;
			}
			else if ( view.getId() == buttonMinbus.getId()  && !buttonMinbus.isSelected()){
				buttonSedan.setSelected(false);
				buttonJeep.setSelected(false);
				buttonBigJeep.setSelected(false);
				buttonMinbus.setSelected(true);
				buttonMoto.setSelected(false);
				selectedCarType = CarType.Minibus;
			}
	    	}
	};

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_client_car_definition, container,false);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar);
		}
		
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.inject(this, view);
	         
	        buttonSedan.setOnClickListener(buttonSelector);
	        buttonJeep.setOnClickListener(buttonSelector);
	        buttonBigJeep.setOnClickListener(buttonSelector);
	        buttonMinbus.setOnClickListener(buttonSelector);	        
	        buttonMoto.setOnClickListener(buttonSelector);

			buttonSedan.setSelected(true);
			buttonJeep.setSelected(false);
			buttonBigJeep.setSelected(false);
			buttonMinbus.setSelected(false);
			buttonMoto.setSelected(false);
			selectedCarType = CarType.Sedan;
	    }
	    
	    @OnClick(R.id.button_registration)
	    public void onButtonRegistrationClicked(){
			String сarNoStr = carNo.getText().toString();
			String сarMarkStr = carMark.getText().toString();

			if (TextUtils.isEmpty(сarNoStr)){
				ToastUtil.displayAtTop(getActivity(), "Введите гос.номер техники");
//				carNo.setError("Обязательное поле");
//				carNo.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(сarMarkStr)){
				ToastUtil.displayAtTop(getActivity(), "Введите марку техники");
//				carMark.setError("Обязательное поле");
//				carMark.requestFocus();
				return;
			}

			CarItem item = new CarItem();
			item.setCarModel(сarMarkStr);
			item.setCarNumber(сarNoStr);
			item.setCarType(selectedCarType.getValue());
			BA.getEventBus().post(new CarDefinitionItemRequestEvent(item));
			this.dismiss();
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

	@OnClick(R.id.close)
	public void onCloseButtonClicked(){
		this.dismiss();
	}
}
