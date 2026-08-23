package com.driverspa.client.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.CarsGridAdapter;
import com.driverspa.adapter.GroupServicesGridAdapter;
import com.driverspa.adapter.OfflineBoxesGridAdapter;
import com.driverspa.adapter.ServicesGridAdapter;
import com.driverspa.dialog.MultipleSelectDialog;
import com.driverspa.model.BookInfo;
import com.driverspa.model.CampaignType;
import com.driverspa.model.CarItem;
import com.driverspa.model.CarType;
import com.driverspa.model.ClientInfo;
import com.driverspa.model.ReversePrices;
import com.driverspa.model.ServiceItem;
import com.driverspa.model.User;
import com.driverspa.model.Washer;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.model.api.request.UserUpdateRequest;
import com.driverspa.util.Functions;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.L;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.CarDefinitionItemRequestEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;
import com.driverspa.util.otto.ws.UserUpdateSelfRequestEvent;
import com.driverspa.util.otto.ws.WasherInfoRequestEvent;
import com.driverspa.util.otto.ws.WasherInfoResponseEvent;
import com.driverspa.view.ExpandableGridView;

import static com.driverspa.util.Constants.WASHER_BOOKING_REQUEST_DATA;
import static com.driverspa.util.Constants.WASHER_BOOK_FROM_MAP;
import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;
import static com.driverspa.util.Constants.WASHER_ID;

public class ClientBookingFragment extends ClientBaseFragment {
    String userId = UserPreferences.getUserId(BA.getContext());

    public interface ActivityActions {
        public void showProgressBar(boolean set);
        public void showTimeGrid(Washer washer, BookingRequest request);
    }

    public static ClientBookingFragment newInstance(Washer washer, BookingRequest request) {
        ClientBookingFragment fragment = new ClientBookingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer));
        bundle.putString(WASHER_BOOKING_REQUEST_DATA, JsonUtil.serialize(request));
        bundle.putBoolean(WASHER_BOOK_FROM_MAP,false);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ClientBookingFragment newInstance(String washerId) {
        ClientBookingFragment fragment = new ClientBookingFragment();
        Bundle bundle = new Bundle();
        L.d("ID "+washerId);
        bundle.putString(WASHER_ID,washerId);
        bundle.putBoolean(WASHER_BOOK_FROM_MAP,true);
        fragment.setArguments(bundle);
        return fragment;
    }

    String washerId;
    private Washer washer;
    CarType selectedCarType;
    CarItem selectedCarItem;
    @BindView(R.id.txtWasherNameTitle)
    TextView washerTitleName;
    @BindView(R.id.txtPrice)
    TextView txtPrice;
    @BindView(R.id.txtTime)
    TextView txtTime;
    @BindView(R.id.btnBookNext)
    Button buttonNext;
    TextView titleView;

    @BindView(R.id.no_service)
    View noService;

    private ActivityActions activityActions;
    MultipleSelectDialog servicesDialog;
    BookingRequest request;
    BookingRequest initialRequest;
    HashMap<Integer, Double> menuServicesPrices = new HashMap<Integer, Double>();
    private User user;

    @BindView(R.id.grid_cars)
    ExpandableGridView gridCars;
    @BindView(R.id.grid_services)
    ExpandableGridView gridServices;
    private CarsGridAdapter carAdapter;
    private ServicesGridAdapter serviceAdapter;

    @BindView(R.id.grid_group_services)
    ExpandableGridView gridGroupServices;
    @BindView(R.id.serviceBtn)
    View serviceBtn;

    @BindView(R.id.cashAmount)
    EditText cashAmount;
    @BindView(R.id.cardAmount)
    EditText cardAmount;
    @BindView(R.id.bonusAmount)
    EditText bonusAmount;
    @BindView(R.id.depositAmount)
    EditText depositAmount;
    @BindView(R.id.clientBonusAmount)
    TextView clientBonusAmount;
    @BindView(R.id.clientDepositAmount)
    TextView clientDepositAmount;
    @BindView(R.id.txtPriceDifference)
    TextView txtPriceDifference;

    ClientInfo selectedClient;
    double finalPrice;
    double finalPriceDifference;
    List<BookInfo.PriceDetail> priceDetails = new ArrayList<>();
    TextWatcher cashWatcher;
    TextWatcher cardWatcher;
    TextWatcher bonusWatcher;
    TextWatcher depositWatcher;
    boolean fromMap;
    private GroupServicesGridAdapter groupServiceAdapter;
    private OfflineBoxesGridAdapter boxAdapter;

    Toolbar mToolbar;

    private ClientUserCarDefinitionFragment carDefinitionDialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_booking, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fromMap = getArguments().getBoolean(WASHER_BOOK_FROM_MAP);
        if(!fromMap) {
            String washerStr = getArguments().getString(WASHER_DATA_TO_BOOK);
            String requestStr = getArguments().getString(WASHER_BOOKING_REQUEST_DATA);
            washer = JsonUtil.deserializeToWasher(washerStr);
            initialRequest = JsonUtil.deserializeToBookingRequest(requestStr);
            request = new BookingRequest();
        }
        else{
             washerId = getArguments().getString(WASHER_ID);
            request = new BookingRequest();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
        selectedCarType = CarType.Sedan;
        serviceBtn.setSelected(true);

        titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);

        //Deposit and Bonus amounts
        depositAmount.setEnabled(false);
        bonusAmount.setEnabled(false);
        cardAmount.setEnabled(false);

        clientDepositAmount.setText(BA.str(R.string.of_0));
        clientBonusAmount.setText(BA.str(R.string.of_0));
        depositAmount.setText("0");
        bonusAmount.setText("0");
        bonusAmount.setSelection(bonusAmount.getText().length());
        depositAmount.setSelection(depositAmount.getText().length());
    }

    @Subscribe
    public void onUserReceived(UserGetSelfResponseEvent event) {
        this.user = event.getUser();
        if(washer != null){
            setData();
        }
        else{
            setWaitScreen(true);
            BA.getEventBus().post(new WasherInfoRequestEvent(washerId));
        }
        cashWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculatePaymentTypesTransactionAmount(cashAmount,this);
            }
        };
        cardWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculatePaymentTypesTransactionAmount(cardAmount,this);
            }
        };
        depositWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculatePaymentTypesTransactionAmount(depositAmount,this);
            }
        };
        bonusWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculatePaymentTypesTransactionAmount(bonusAmount,this);
            }
        };

        cashAmount.addTextChangedListener(cashWatcher);
        cardAmount.addTextChangedListener(cardWatcher);
        bonusAmount.addTextChangedListener(bonusWatcher);
        depositAmount.addTextChangedListener(depositWatcher);
    }

    @Subscribe
    public void onWasherInfoReceived(WasherInfoResponseEvent event) {
        setWaitScreen(false);
        if(event.getWasher() != null){
            washer = event.getWasher();
            setData();
        }
        else{
            ToastUtil.display(getActivity(),BA.str(R.string.err_get_wash_data));
            getActivity().finish();
        }
    }


    private void setData(){
        if (user != null) {
            if(washer != null && washer.getActiveCampaign() != null && (washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Both.getValue())
                    ||washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Online.getValue()))){
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_discount_list, 0);
                titleView.setCompoundDrawablePadding((int) Functions.dipToPixels(getActivity(), 5f));
            }
            washerTitleName.setText(BA.str(R.string.book));
            titleView.setText(washer.getName().toLowerCase().contains("автомойка") ? washer.getName() : BA.str(R.string.car_wash_label_sp) + washer.getName());

            ViewGroup.LayoutParams layoutParamsCar = gridCars.getLayoutParams();
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int width = display.getWidth() - (int) Functions.dipToPixels(getActivity(), 10);
            layoutParamsCar.width = width; //this is in pixels
            gridCars.setLayoutParams(layoutParamsCar);
            gridCars.setColumnWidth((display.getWidth() - (int) Functions.dipToPixels(getActivity(), 32)) / 4);

            ViewGroup.LayoutParams layoutParamsServices = gridServices.getLayoutParams();
            layoutParamsServices.width = width;
            gridServices.setLayoutParams(layoutParamsServices);
            gridServices.setColumnWidth((display.getWidth() - (int) Functions.dipToPixels(getActivity(), 32)) / 4);
            gridGroupServices.setLayoutParams(layoutParamsServices);
            gridGroupServices.setColumnWidth((display.getWidth() - (int) Functions.dipToPixels(getActivity(), 32)) / 4);

            //Car adapter
            List<CarItem> carItemList = new ArrayList<CarItem>();
            carItemList.add(new CarItem());
            if (user.getCars() == null) user.setCars(new ArrayList<CarItem>());
            carItemList.addAll(user.getCars());
            carAdapter = new CarsGridAdapter(getActivity(), carItemList);
            gridCars.setAdapter(carAdapter);
            gridCars.setExpanded(true);

            //set selected car
            if (user.getCars() != null && user.getCars().size() > 0) {
                carAdapter.setSelectedPosition(user.getCars().size());
                CarItem item = carAdapter.getCarList().get(user.getCars().size());
                createService(item.getCarType());
                selectedCarItem = item;
                carAdapter.notifyDataSetChanged();
            }

            gridCars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    if (position == 0) {
                        openCarDefinition();
                    } else {
                        carAdapter.setSelectedPosition(position);
                        if (carAdapter.getSelectedView() != null)
                            carAdapter.getSelectedView().setSelected(false);
                        carAdapter.setSelectedView(v);
                        carAdapter.getSelectedView().setSelected(false);
                        v.setSelected(true);
                        CarItem item = carAdapter.getCarList().get(position);
                        createService(item.getCarType());
                        selectedCarItem = item;
                        carAdapter.notifyDataSetChanged();
                    }
                }
            });

            if(!TextUtils.isEmpty(washer.getCompanyClientId())) {
                if (washer.getCompanyClientDeposit() != null)
                    clientDepositAmount.setText(BA.str(R.string.of_sp) + washer.getCompanyClientDeposit());
                if (washer.getCompanyClientBonus() != null)
                    clientBonusAmount.setText(BA.str(R.string.of_sp) + washer.getCompanyClientBonus());

                depositAmount.setText("0");
                bonusAmount.setText("0");
                bonusAmount.setSelection(bonusAmount.getText().length());
                depositAmount.setSelection(depositAmount.getText().length());
                depositAmount.setEnabled(true);
                bonusAmount.setEnabled(true);
            }

            if(washer.getPayOptions() != null && washer.getPayOptions().size() > 0){
                boolean noCash = false;
                for(String option : washer.getPayOptions()){
                    if(!option.equals("cash")) {
                        noCash = true;
                        break;
                    }
                }
                if(noCash){
                    cardAmount.setEnabled(true);
                }
                else{
                    cardAmount.setEnabled(false);
                }
            }
        }
    }

    private void createService(final Integer carType) {
        resetPriceAndTime();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Services
                ArrayList<ServiceItem> serviceItems = new ArrayList<ServiceItem>();
                final LinkedHashMap<Integer, String> dictionary = BA.getReference().getServices();
                LinkedHashMap<Integer, ArrayList<Washer.Prices>> washerMenu = washer.getMenu();
                if (washerMenu != null && washerMenu.size() > 0) {
                    serviceItems.clear();
                    menuServicesPrices.clear();
                    for (Map.Entry<Integer, ArrayList<Washer.Prices>> entry : washerMenu.entrySet()) {
                        Integer key = entry.getKey();
                        if (key == carType) {
                            ArrayList<Washer.Prices> pricesMenu = entry.getValue();
                            for (Washer.Prices priceMenu : pricesMenu) {
                                if (priceMenu.getPrice() != null && priceMenu.getPrice() > 0) {
                                    ServiceItem newItem = new ServiceItem();
                                    newItem.setServiceId(priceMenu.getType());
                                    newItem.setServiceName(dictionary.get(priceMenu.getType()));
                                    newItem.setPrice(priceMenu.getPrice());
                                    newItem.setTime(priceMenu.getTime());
                                    serviceItems.add(newItem);
                                }
                            }
                            break;
                        }
                    }
                }

                //Group services
                List<Washer.GroupMenu> groupServiceItems = new ArrayList<Washer.GroupMenu>();
                List<Washer.GroupMenu> washerGroup = washer.getGroupMenu();
                if (washerGroup != null && washerGroup.size() > 0) {
                    groupServiceItems.clear();
                    for (Washer.GroupMenu g : washerGroup) {
                        for (ReversePrices p : g.getPrices()) {
                            if (p.getCarType() == carType && p.getPrice() != null && p.getPrice() > 0) {
                                groupServiceItems.add(g);
                            }
                        }
                    }
                }

                //Service adapter
                serviceAdapter = new ServicesGridAdapter(getActivity(), serviceItems);
                gridServices.setAdapter(serviceAdapter);
                gridServices.setExpanded(true);
                gridServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        closeKeyboard();
                        request.setServices(new ArrayList<Integer>());
                        final ArrayList<Integer> requestServices = new ArrayList<Integer>();
                        if (serviceAdapter.getSelectedPosition().get(position) != null && serviceAdapter.getSelectedPosition().get(position)) {
                            serviceAdapter.setSelectedPosition(position, false);
                            serviceAdapter.notifyDataSetChanged();
                        } else {
                            if (serviceAdapter.getData().get(position).getPrice() > 0) {
                                if (request != null && request.getGroupServices() != null && request.getGroupServices().size() > 0) {
                                    Washer.GroupMenu selectedGroup = null;
//                                    if (groupServiceAdapter != null && groupServiceAdapter.getData() != null && groupServiceAdapter.getData().size() > 0) {
//                                        for (Washer.GroupMenu g : groupServiceAdapter.getData()) {
//                                            for (String gId : request.getGroupServices()) {
//                                                if (gId.equals(g.getId())) {
//                                                    selectedGroup = g;
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
                                    if (selectedGroup != null && selectedGroup.getServices() != null && selectedGroup.getServices().contains(serviceAdapter.getData().get(position).getServiceId())) {
                                        ToastUtil.display(BA.getContext(), BA.str(R.string.service_already_in_complex));
                                    } else {
                                        serviceAdapter.setSelectedPosition(position, true);
                                        serviceAdapter.notifyDataSetChanged();
                                    }

                                } else {
                                    serviceAdapter.setSelectedPosition(position, true);
                                    serviceAdapter.notifyDataSetChanged();
                                }
                            } else {
                                ToastUtil.display(BA.getContext(), BA.str(R.string.err_no_price_for_type));
                            }
                        }

                        for (int i = 0; i < serviceAdapter.getData().size(); i++) {
                            if (serviceAdapter.getSelectedPosition().get(i) != null && serviceAdapter.getSelectedPosition().get(i)) {
                                requestServices.add(serviceAdapter.getData().get(i).getServiceId());
                            }
                        }
                        request.setServices(new ArrayList<Integer>(requestServices));
                        requestServices.clear();
                        calculatePriceAndTime((ArrayList) serviceAdapter.getData(), (ArrayList) groupServiceAdapter.getData());
                    }
                });

                //Group Service adapter
                groupServiceAdapter = new GroupServicesGridAdapter(getActivity(), groupServiceItems);
                gridGroupServices.setAdapter(groupServiceAdapter);
                gridGroupServices.setExpanded(true);
                gridGroupServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        closeKeyboard();
                        request.setGroupServices(new ArrayList<String>());
                        final ArrayList<String> groupRequestServices = new ArrayList<String>();

                        if (groupServiceAdapter.getSelectedPosition().get(position) != null && groupServiceAdapter.getSelectedPosition().get(position)) {
                            groupServiceAdapter.setSelectedPosition(position, false);
                            groupServiceAdapter.notifyDataSetChanged();
                        } else {
                            Washer.GroupMenu selectedGroup = groupServiceAdapter.getData().get(position);

                            if (selectedGroup != null && selectedGroup.getServices() != null && request != null && request.getServices() != null && request.getServices().size() > 0) {
                                boolean found = false;
//                                for (Integer s : request.getServices()) {
//                                    if (selectedGroup.getServices().contains(s)) {
//                                        found = true;
//                                    }
//                                }
                                if (found) {
                                    ToastUtil.display(BA.getContext(), BA.str(R.string.service_already_selected_complex));
                                } else {
                                    groupServiceAdapter.setSelectedPosition(position, true);
                                    groupServiceAdapter.notifyDataSetChanged();
                                }
                            } else {
                                groupServiceAdapter.setSelectedPosition(position, true);
                                groupServiceAdapter.notifyDataSetChanged();
                            }

                        }

                        for (int i = 0; i < groupServiceAdapter.getData().size(); i++) {
                            if (groupServiceAdapter.getSelectedPosition().get(i) != null && groupServiceAdapter.getSelectedPosition().get(i)) {
                                groupRequestServices.add(groupServiceAdapter.getData().get(i).getId());
                            }
                        }
                        request.setGroupServices(new ArrayList<String>(groupRequestServices));
                        groupRequestServices.clear();
                        calculatePriceAndTime((ArrayList) serviceAdapter.getData(), (ArrayList) groupServiceAdapter.getData());
                    }
                });

                if (serviceItems.size() == 0) {
                    gridServices.setVisibility(View.GONE);
                } else {
                    gridServices.setVisibility(View.VISIBLE);
                }
                if (groupServiceItems.size() == 0) {
                    gridGroupServices.setVisibility(View.GONE);
                } else {
                    gridGroupServices.setVisibility(View.VISIBLE);
                }

                if (serviceItems.size() == 0 && groupServiceItems.size() == 0) {
                   noService.setVisibility(View.VISIBLE);
                }
                else{
                   noService.setVisibility(View.GONE);
                }

            }
        }, 50);
    }


    @OnClick(R.id.btnBookNext)
    public void onButtonNextClicked() {
        createBookingRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        BA.getEventBus().register(this);
        if(washer != null && user != null)
         resetPriceAndTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        cashAmount.removeTextChangedListener(cashWatcher);
        cardAmount.removeTextChangedListener(cardWatcher);
        bonusAmount.removeTextChangedListener(bonusWatcher);
        depositAmount.removeTextChangedListener(depositWatcher);
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

    private void resetPriceAndTime() {
        txtPrice.setText("0 ₸");
        if(washer != null && washer.getActiveCampaign() != null && (washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Both.getValue())
                ||washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Online.getValue()))){
            txtPrice.setText("0 - "+washer.getActiveCampaign().getCampaignDiscount()+"% = 0 ₸");
        }
//        txtPrice.setText("0 ₸");

        finalPrice = 0d;
        cashAmount.setText((int)finalPrice + "");
        cashAmount.setSelection(cashAmount.getText().toString().length());
        txtTime.setText(BA.str(R.string.zero_min));
        request.setServices(new ArrayList<Integer>());
        request.setGroupServices(new ArrayList<String>());
        cashAmount.setText("0");
        cardAmount.setText("0");
        bonusAmount.setText("0");
        depositAmount.setText("0");
        calculatePaymentTypesTransactionAmount();
    }

    public void calculatePriceAndTime(ArrayList<ServiceItem> data, ArrayList<Washer.GroupMenu> groupData) {
        cashAmount.setText("0");
        cashAmount.setSelection(cashAmount.getText().toString().length());
        cardAmount.setText("0");
        bonusAmount.setText("0");
        depositAmount.setText("0");
        txtPrice.setText("0 ₸");
        finalPrice = 0d;
        txtTime.setText(BA.str(R.string.zero_min));

        txtPrice.setText("0 ₸");
        if(washer != null && washer.getActiveCampaign() != null && (washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Both.getValue())
                ||washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Online.getValue()))){
            txtPrice.setText("0 - "+washer.getActiveCampaign().getCampaignDiscount()+"% = 0 ₸");
        }
        txtTime.setText(BA.str(R.string.zero_min));
        Double totalPrice = 0d;
        Integer totalTime = 0;
        for (int i = 0; i < data.size(); i++) {
            for (Integer service : request.getServices()) {
                if (data.get(i).getServiceId() == service) {
                    totalPrice += (data.get(i).getPrice() != null ? data.get(i).getPrice() : 0d);
                    totalTime += (data.get(i).getTime() != null ? data.get(i).getTime() : 0);
                }
            }
        }

        if (groupData != null && groupData.size() > 0) {
            for (int i = 0; i < groupData.size(); i++) {
                if (request.getGroupServices() != null && request.getGroupServices().size() > 0) {
                    for (String groupId : request.getGroupServices()) {
                        if (groupData.get(i).getId().equals(groupId)) {
                            for (ReversePrices p : groupData.get(i).getPrices()) {
                                if (p.getCarType() == selectedCarType.getValue()) {
                                    totalPrice += (p.getPrice() != null ? p.getPrice() : 0d);
                                    totalTime += (p.getTime() != null ? p.getTime() : 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (totalPrice > 0) {
            int discnt = 0;
            if(washer != null && washer.getActiveCampaign() != null && (washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Both.getValue())
                    ||washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Online.getValue()))){
                discnt = washer.getActiveCampaign().getCampaignDiscount();
                txtPrice.setText(totalPrice+" - "+washer.getActiveCampaign().getCampaignDiscount()+"% =  ₸");
            }
            int totalPriceWithDiscount = (int) (Math.round(totalPrice) - Math.round(totalPrice) * discnt / 100);
            txtPrice.setText(totalPriceWithDiscount + " ₸.");
            finalPrice = totalPriceWithDiscount;
            cashAmount.setText((int)finalPrice + "");
            cashAmount.setSelection(cashAmount.getText().toString().length());


            if(washer != null && washer.getActiveCampaign() != null && (washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Both.getValue())
            ||washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Online.getValue()))){
                txtPrice.setText(((int)totalPrice.longValue())+" - "+washer.getActiveCampaign().getCampaignDiscount()+"% = "+totalPriceWithDiscount+" ₸");
            }

            if (request != null)
                request.setServicePrice(Integer.toString(totalPriceWithDiscount)+"");
        }

        if (totalTime > 0) {
            txtTime.setText(Math.round(totalTime) + BA.str(R.string.space_min));
            if (request != null)
                request.setServiceTotalTime(totalTime);
        }

        calculatePaymentTypesTransactionAmount();
    }

    public void createBookingRequest() {

        if (selectedCarItem == null) {
            ToastUtil.displayAtTop(getActivity(), BA.str(R.string.select_car));
            return;
        }

        if (!(request.getServices() != null && request.getServices().size() > 0) && !(request.getGroupServices() != null && request.getGroupServices().size() > 0)) {
            ToastUtil.displayAtTop(getActivity(), BA.str(R.string.select_services));
            return;
        }

        if (finalPriceDifference != 0) {
            ToastUtil.displayAtTop(getActivity(), BA.str(R.string.balance_not_zero));
            return;
        }

        request.setCarMark(selectedCarItem.getCarModel());
        request.setCarNo(selectedCarItem.getCarNumber());
        request.setClientName(user.getFirstName());
        request.setClientKey(selectedCarItem.getCarNumber() + " (" + user.getFirstName() + ")");
        request.setCarType(selectedCarItem.getCarType());
        if (initialRequest != null && initialRequest.isEditRequest()) {
            request.setRequestType(initialRequest.getRequestType());
            request.setBookId(initialRequest.getBookId());
        }
        activityActions.showTimeGrid(washer, request);
    }

    public void openCarDefinition() {
        carDefinitionDialogFragment = new ClientUserCarDefinitionFragment();
        carDefinitionDialogFragment.setCancelable(false);
        carDefinitionDialogFragment.show(getActivity().getSupportFragmentManager().beginTransaction(), "DialogFragment");
    }

    @Subscribe
    public void onCarDefinitionReceived(CarDefinitionItemRequestEvent event) {
        if (user.getCars() != null && user.getCars().size() > 0)
            for (CarItem car : user.getCars()) {
                if (car.getCarNumber().toLowerCase().equals(event.getCar().getCarNumber().toLowerCase())) {
                    ToastUtil.display(getActivity(), BA.str(R.string.car_number_exists_you));
                    return;
                }
            }

        user.getCars().add(event.getCar());
        List<CarItem> carItemList = new ArrayList<CarItem>();
        carItemList.add(new CarItem());
        carItemList.addAll(user.getCars());
        carAdapter = new CarsGridAdapter(getActivity(), carItemList);
        gridCars.setAdapter(carAdapter);
        gridCars.setExpanded(true);

        //Set last selected car
        if (user.getCars() != null && user.getCars().size() > 0) {
            carAdapter.setSelectedPosition(user.getCars().size());
            CarItem item = carAdapter.getCarList().get(user.getCars().size());
            createService(item.getCarType());
            selectedCarItem = item;
            carAdapter.notifyDataSetChanged();
        }

        //Update user cars
        UserUpdateRequest userRequest = new UserUpdateRequest();
        userRequest.setFirstName(user.getFirstName());
        userRequest.setLastName("    ");
        userRequest.setCars(user.getCars());
        BA.getEventBus().post(new UserUpdateSelfRequestEvent(userId, userRequest));
    }


    protected void calculatePaymentTypesTransactionAmount() {
        double cash = 0, card = 0, bonus = 0, deposit = 0, price = 0;

        try {
            cash = Double.parseDouble(cashAmount.getText().toString());
        } catch (NumberFormatException e) {
            cash = 0;
        }
        try {
            card = Double.parseDouble(cardAmount.getText().toString());
        } catch (NumberFormatException e) {
            card = 0;
        }
        try {
            bonus = Double.parseDouble(bonusAmount.getText().toString());
        } catch (NumberFormatException e) {
            bonus = 0;
        }
        try {
            deposit = Double.parseDouble(depositAmount.getText().toString());
        } catch (NumberFormatException e) {
            deposit = 0;
        }

        finalPriceDifference = finalPrice - cash - card - bonus - deposit;
        txtPriceDifference.setText(finalPriceDifference + " ₸");

        priceDetails.clear();
        if (cash > 0) {
            BookInfo.PriceDetail pdCash = new BookInfo.PriceDetail();
            pdCash.setAmount(cash);
            pdCash.setPaymentType("C");
            priceDetails.add(pdCash);
        }
        if (card > 0) {
            BookInfo.PriceDetail pdCard = new BookInfo.PriceDetail();
            pdCard.setAmount(card);
            pdCard.setPaymentType("CD");
            priceDetails.add(pdCard);
        }
        if (bonus > 0) {
            BookInfo.PriceDetail pdBonus = new BookInfo.PriceDetail();
            pdBonus.setAmount(bonus);
            pdBonus.setPaymentType("B");
            priceDetails.add(pdBonus);
        }
        if (deposit > 0) {
            BookInfo.PriceDetail pdDeposit = new BookInfo.PriceDetail();
            pdDeposit.setAmount(deposit);
            pdDeposit.setPaymentType("DA");
            priceDetails.add(pdDeposit);
        }
        request.setPriceDetails(priceDetails);
    }


    protected void calculatePaymentTypesTransactionAmount(EditText enteringEditText, TextWatcher watcher) {
        double cash = 0, card = 0, bonus = 0, deposit = 0, price = 0;


        if(enteringEditText == bonusAmount){
           if(washer != null && washer.getCompanyClientBonus() != null){
               try {
                   bonus = Double.parseDouble(bonusAmount.getText().toString());
               } catch (NumberFormatException e) {
                   bonus = 0;
               }
              if(bonus > washer.getCompanyClientBonus()){
                  bonusAmount.setText(washer.getCompanyClientBonus().intValue()+"");
              }
           }
        }

        if(enteringEditText == depositAmount){
            if(washer != null && washer.getCompanyClientDeposit() != null){
                try {
                    deposit = Double.parseDouble(depositAmount.getText().toString());
                } catch (NumberFormatException e) {
                    deposit = 0;
                }
                if(deposit > washer.getCompanyClientDeposit()){
                    depositAmount.setText(washer.getCompanyClientDeposit().intValue()+"");
                }
            }
        }

        try {
            cash = Double.parseDouble(cashAmount.getText().toString());
        } catch (NumberFormatException e) {
            cash = 0;
        }
        try {
            card = Double.parseDouble(cardAmount.getText().toString());
        } catch (NumberFormatException e) {
            card = 0;
        }
        try {
            bonus = Double.parseDouble(bonusAmount.getText().toString());
        } catch (NumberFormatException e) {
            bonus = 0;
        }
        try {
            deposit = Double.parseDouble(depositAmount.getText().toString());
        } catch (NumberFormatException e) {
            deposit = 0;
        }

        finalPriceDifference = finalPrice - cash - card - bonus - deposit;

        if(finalPriceDifference < 0){
            if(enteringEditText != cashAmount) cashAmount.setText("0");
            if(enteringEditText != cardAmount) cardAmount.setText("0");
            if(enteringEditText != bonusAmount) bonusAmount.setText("0");
            if(enteringEditText != depositAmount) depositAmount.setText("0");
        }

        txtPriceDifference.setText(finalPriceDifference + " ₸");

        priceDetails.clear();
        if (cash > 0) {
            BookInfo.PriceDetail pdCash = new BookInfo.PriceDetail();
            pdCash.setAmount(cash);
            pdCash.setPaymentType("C");
            priceDetails.add(pdCash);
        }
        if (card > 0) {
            BookInfo.PriceDetail pdCard = new BookInfo.PriceDetail();
            pdCard.setAmount(card);
            pdCard.setPaymentType("CD");
            priceDetails.add(pdCard);
        }
        if (bonus > 0) {
            BookInfo.PriceDetail pdBonus = new BookInfo.PriceDetail();
            pdBonus.setAmount(bonus);
            pdBonus.setPaymentType("B");
            priceDetails.add(pdBonus);
        }
        if (deposit > 0) {
            BookInfo.PriceDetail pdDeposit = new BookInfo.PriceDetail();
            pdDeposit.setAmount(deposit);
            pdDeposit.setPaymentType("DA");
            priceDetails.add(pdDeposit);
        }
        request.setPriceDetails(priceDetails);
    }

    @OnFocusChange(R.id.bonusAmount)
    public void onFocusChangeBonus(boolean focus) {
        if (!focus) {
            if (TextUtils.isEmpty(bonusAmount.getText().toString()))
                bonusAmount.setText("0");
            calculatePaymentTypesTransactionAmount();
        } else {
            if (!TextUtils.isEmpty(bonusAmount.getText().toString()) && bonusAmount.getText().toString().equals("0")) {
                bonusAmount.setText("");
            }
        }
    }

    @OnFocusChange(R.id.depositAmount)
    public void onFocusChangeDeposit(boolean focus) {
        if (!focus) {
            if (TextUtils.isEmpty(depositAmount.getText().toString()))
                depositAmount.setText("0");
            calculatePaymentTypesTransactionAmount();
        } else {
            if (!TextUtils.isEmpty(depositAmount.getText().toString()) && depositAmount.getText().toString().equals("0")) {
                depositAmount.setText("");
            }
        }
    }

    @OnFocusChange(R.id.cashAmount)
    public void onFocusChangeCash(boolean focus) {
        if (!focus) {
            if (TextUtils.isEmpty(cashAmount.getText().toString()))
                cashAmount.setText("0");
            calculatePaymentTypesTransactionAmount();
            cashAmount.clearFocus();
        } else {
            if (!TextUtils.isEmpty(cashAmount.getText().toString()) && cashAmount.getText().toString().equals("0")) {
                cashAmount.setText("");
            }
        }

    }

    @OnFocusChange(R.id.cardAmount)
    public void onFocusChangeCard(boolean focus) {
        if (!focus) {
            if (TextUtils.isEmpty(cardAmount.getText().toString()))
                cardAmount.setText("0");
            calculatePaymentTypesTransactionAmount();
        } else {
            if (!TextUtils.isEmpty(cardAmount.getText().toString()) && cardAmount.getText().toString().equals("0")) {
                cardAmount.setText("");
            }
        }
    }
}
