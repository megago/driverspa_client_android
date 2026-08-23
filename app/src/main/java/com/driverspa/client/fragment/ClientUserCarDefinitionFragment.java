package com.driverspa.client.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.model.CarItem;
import com.driverspa.model.CarType;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.otto.ws.CarDefinitionItemRequestEvent;

public class ClientUserCarDefinitionFragment extends DialogFragment {

	CarType selectedCarType;

	@BindView(R.id.btnSedan)
	View buttonSedan;
	@BindView(R.id.btnCrossover)
	View buttonCrossover;
	@BindView(R.id.btnBigJeep)
	View buttonBigJeep;
	@BindView(R.id.btnMinbus)
	View buttonMinbus;
	@BindView(R.id.btnMini)
	View buttonMini;
	@BindView(R.id.btnUniversal)
	View buttonUniversal;
	@BindView(R.id.btnMiniJeep)
	View buttonMiniJeep;
	@BindView(R.id.btnMinivan)
	View buttonMiniVan;
	@BindView(R.id.btnMoto)
	View buttonMoto;
	@BindView(R.id.carMark)
	EditText carMark;
	@BindView(R.id.carNo)
	EditText carNo;

	private void selectOnly(View selected, CarType type) {
		buttonMini.setSelected(selected == buttonMini);
		buttonSedan.setSelected(selected == buttonSedan);
		buttonUniversal.setSelected(selected == buttonUniversal);
		buttonCrossover.setSelected(selected == buttonCrossover);
		buttonMiniJeep.setSelected(selected == buttonMiniJeep);
		buttonBigJeep.setSelected(selected == buttonBigJeep);
		buttonMoto.setSelected(selected == buttonMoto);
		buttonMiniVan.setSelected(selected == buttonMiniVan);
		buttonMinbus.setSelected(selected == buttonMinbus);
		selectedCarType = type;
	}

	private View.OnClickListener buttonSelector = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if ( view.getId() == buttonSedan.getId() && !buttonSedan.isSelected()){
				selectOnly(buttonSedan, CarType.Sedan);
			}
			else if ( view.getId() == buttonMini.getId()  && !buttonMini.isSelected()){
				selectOnly(buttonMini, CarType.Mini);
			}
			else if ( view.getId() == buttonUniversal.getId()  && !buttonUniversal.isSelected()){
				selectOnly(buttonUniversal, CarType.Universal);
			}
			else if ( view.getId() == buttonMiniJeep.getId()  && !buttonMiniJeep.isSelected()){
				selectOnly(buttonMiniJeep, CarType.MiniJeep);
			}
			else if ( view.getId() == buttonMiniVan.getId()  && !buttonMiniVan.isSelected()){
				selectOnly(buttonMiniVan, CarType.Minivan);
			}
			else if ( view.getId() == buttonCrossover.getId()  && !buttonCrossover.isSelected()){
				selectOnly(buttonCrossover, CarType.Crossover);
			}
			else if ( view.getId() == buttonBigJeep.getId()  && !buttonBigJeep.isSelected()){
				selectOnly(buttonBigJeep, CarType.Jeep);
			}
			else if ( view.getId() == buttonMoto.getId()  && !buttonMoto.isSelected()){
				selectOnly(buttonMoto, CarType.Moto);
			}
			else if ( view.getId() == buttonMinbus.getId()  && !buttonMinbus.isSelected()){
				selectOnly(buttonMinbus, CarType.Minibus);
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
			setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_DriverSpa_TranslucentDialog);
		}
		
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.bind(this, view);
	         
	        buttonSedan.setOnClickListener(buttonSelector);
	        buttonCrossover.setOnClickListener(buttonSelector);
	        buttonBigJeep.setOnClickListener(buttonSelector);
	        buttonMinbus.setOnClickListener(buttonSelector);
	        buttonMoto.setOnClickListener(buttonSelector);
	        buttonMini.setOnClickListener(buttonSelector);
	        buttonUniversal.setOnClickListener(buttonSelector);
	        buttonMiniJeep.setOnClickListener(buttonSelector);
	        buttonMiniVan.setOnClickListener(buttonSelector);

			selectOnly(buttonSedan, CarType.Sedan);
	    }
	    
	    @OnClick(R.id.button_registration)
	    public void onButtonRegistrationClicked(){
			String сarNoStr = carNo.getText().toString();
			String сarMarkStr = carMark.getText().toString();

			if (TextUtils.isEmpty(сarNoStr)){
				ToastUtil.displayAtTop(getActivity(), BA.str(R.string.enter_vehicle_plate));
//				carNo.setError("Обязательное поле");
//				carNo.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(сarMarkStr)){
				ToastUtil.displayAtTop(getActivity(), BA.str(R.string.enter_vehicle_brand));
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
