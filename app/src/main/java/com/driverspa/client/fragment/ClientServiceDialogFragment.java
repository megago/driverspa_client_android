package com.driverspa.client.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.GroupServicesGridAdapter;
import com.driverspa.adapter.OfflineBoxesGridAdapter;
import com.driverspa.adapter.ServicesGridAdapter;
import com.driverspa.dialog.MultipleSelectDialog;
import com.driverspa.model.BookInfo;
import com.driverspa.model.CampaignType;
import com.driverspa.model.CarType;
import com.driverspa.model.ReversePrices;
import com.driverspa.model.ServiceItem;
import com.driverspa.model.User;
import com.driverspa.model.Washer;
import com.driverspa.model.Washer.Prices;
import com.driverspa.model.WasherPublic;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.util.Functions;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ServiceSelectionEvent;
import com.driverspa.view.ExpandableGridView;

import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;
import static com.driverspa.util.Constants.CAR_TYPE;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


public class ClientServiceDialogFragment extends DialogFragment {
    String userId = UserPreferences.getUserId(BA.getContext());

    public static ClientServiceDialogFragment newInstance(WasherPublic washer, Integer carType) {
        ClientServiceDialogFragment fragment = new ClientServiceDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CAR_TYPE, carType);
        bundle.putString(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer));
        fragment.setArguments(bundle);
        return fragment;
    }

    public interface ActivityActions{
        public void setSelectedServices(WasherPublic washer,List<BookInfo.PriceDetail> priceDetails,List<Integer> services, List<String> groupServices, Double price, int minutes);
    }

    private WasherPublic washer;
    CarType selectedCarType;
    @BindView(R.id.txtPrice)
    TextView txtPrice;
    @BindView(R.id.txtTime)
    TextView txtTime;

    @BindView(R.id.no_service)
    View noService;

    MultipleSelectDialog servicesDialog;
    BookingRequest request;
    BookingRequest initialRequest;
    HashMap<Integer, Double> menuServicesPrices = new HashMap<Integer, Double>();
    private User user;

    @BindView(R.id.grid_services)
    ExpandableGridView gridServices;
    private ServicesGridAdapter serviceAdapter;

    @BindView(R.id.grid_group_services)
    ExpandableGridView gridGroupServices;

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
    @BindView(R.id.cardLayout)
    View cardLayout;
    @BindView(R.id.cardLayoutLine)
    View cardLayoutLine;
    @BindView(R.id.depositLayout)
    View depositLayout;
    @BindView(R.id.bonusLayout)
    View bonusLayout;
    @BindView(R.id.bonusLayoutLine)
    View bonusLayoutLine;
    @BindView(R.id.rootView)
    View rootView;

    double finalPrice;
    double finalPriceDifference;
    List<BookInfo.PriceDetail> priceDetails = new ArrayList<>();
    TextWatcher paymentWatcher;
    TextWatcher cashWatcher;
    TextWatcher cardWatcher;
    TextWatcher bonusWatcher;
    TextWatcher depositWatcher;

    private GroupServicesGridAdapter groupServiceAdapter;
    private OfflineBoxesGridAdapter boxAdapter;

    Animation slideLeftIn;
    Animation slideRightOut;
    ActivityActions activityActions;
    Integer totalTime = 0;

    Locale currentLocale = Locale.getDefault();
    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
    DecimalFormat decimalFormatter;
    DecimalFormat decimalFormatter2;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_service_dialog, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar);
        String washerStr = getArguments().getString(WASHER_DATA_TO_BOOK);
        washer = JsonUtil.deserializeToWasher(washerStr);
        selectedCarType = CarType.valueOf(getArguments().getInt(CAR_TYPE));

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        decimalFormatter = new DecimalFormat("#,###.##", otherSymbols);
        decimalFormatter2 = new DecimalFormat("#.####", otherSymbols);
        request = new BookingRequest();
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

        setData(washer);

        slideLeftIn = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
        slideLeftIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.BlackTransparent));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        slideRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left);
        slideRightOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rootView.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ClientServiceDialogFragment.this.dismiss();
            }
        });

        rootView.startAnimation(slideLeftIn);

        paymentWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculatePaymentTypesTransactionAmount();
            }
        };

        cashAmount.addTextChangedListener(paymentWatcher);
        cardAmount.addTextChangedListener(paymentWatcher);
        bonusAmount.addTextChangedListener(paymentWatcher);
        depositAmount.addTextChangedListener(paymentWatcher);
    }

    private void setData(WasherPublic washer){
             createService(selectedCarType.getValue());

            if(!TextUtils.isEmpty(washer.getCompanyClientId())) {
                if (washer.getCompanyClientDeposit() != null && washer.getCompanyClientDeposit() > 0) {
                    depositLayout.setVisibility(View.VISIBLE);
                    clientDepositAmount.setText(BA.str(R.string.of_sp) + washer.getCompanyClientDeposit());
                }
                if (washer.getCompanyClientBonus() != null && washer.getCompanyClientBonus() > 0) {
                    clientBonusAmount.setText(BA.str(R.string.of_sp) + washer.getCompanyClientBonus());
                    bonusLayout.setVisibility(View.VISIBLE);
                    bonusLayoutLine.setVisibility(View.VISIBLE);
                }

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
                    cardLayout.setVisibility(View.VISIBLE);
                    cardLayoutLine.setVisibility(View.VISIBLE);
                }
                else{
                    cardAmount.setEnabled(false);
                    cardLayout.setVisibility(View.GONE);
                    cardLayoutLine.setVisibility(View.GONE);
                }
            }
    }

    private void createService(final Integer carType) {
        resetPriceAndTime();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ViewGroup.LayoutParams layoutParamsServices = gridServices.getLayoutParams();
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                int width = display.getWidth() - (int) Functions.dipToPixels(getActivity(), 10);
                layoutParamsServices.width = width;
                gridServices.setLayoutParams(layoutParamsServices);
                gridServices.setColumnWidth((display.getWidth() - (int) Functions.dipToPixels(getActivity(), 32)) / 4);
                gridGroupServices.setLayoutParams(layoutParamsServices);
                gridGroupServices.setColumnWidth((display.getWidth() - (int) Functions.dipToPixels(getActivity(), 32)) / 4);

                //Services
                ArrayList<ServiceItem> serviceItems = new ArrayList<ServiceItem>();
                final LinkedHashMap<Integer, String> dictionary = BA.getReference().getServices();
                LinkedHashMap<Integer, ArrayList<Prices>> washerMenu = washer.getMenu();
                if (washerMenu != null && washerMenu.size() > 0) {
                    serviceItems.clear();
                    menuServicesPrices.clear();
                    for (Map.Entry<Integer, ArrayList<Prices>> entry : washerMenu.entrySet()) {
                        Integer key = entry.getKey();
                        if (key == carType) {
                            ArrayList<Prices> pricesMenu = entry.getValue();
                            for (Prices priceMenu : pricesMenu) {
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
                        request.setServices(new ArrayList<Integer>());
                        final ArrayList<Integer> requestServices = new ArrayList<Integer>();
                        if (serviceAdapter.getSelectedPosition().get(position) != null && serviceAdapter.getSelectedPosition().get(position)) {
                            serviceAdapter.setSelectedPosition(position, false);
                            serviceAdapter.notifyDataSetChanged();
                        } else {
                            if (serviceAdapter.getData().get(position).getPrice() > 0) {
                                if (request != null && request.getGroupServices() != null && request.getGroupServices().size() > 0) {
                                    Washer.GroupMenu selectedGroup = null;

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
                        request.setGroupServices(new ArrayList<String>());
                        final ArrayList<String> groupRequestServices = new ArrayList<String>();

                        if (groupServiceAdapter.getSelectedPosition().get(position) != null && groupServiceAdapter.getSelectedPosition().get(position)) {
                            groupServiceAdapter.setSelectedPosition(position, false);
                            groupServiceAdapter.notifyDataSetChanged();
                        } else {
                            Washer.GroupMenu selectedGroup = groupServiceAdapter.getData().get(position);

                            if (selectedGroup != null && selectedGroup.getServices() != null && request != null && request.getServices() != null && request.getServices().size() > 0) {
                                boolean found = false;

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
        cashAmount.setText(decimalFormatter.format((int)finalPrice) + "");
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
        totalTime = 0;
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
            txtPrice.setText(decimalFormatter.format(totalPriceWithDiscount) + " ₸.");
            finalPrice = totalPriceWithDiscount;
            cashAmount.setText(decimalFormatter2.format((int)finalPrice));
            cashAmount.setSelection(cashAmount.getText().toString().length());

            if(washer != null && washer.getActiveCampaign() != null && (washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Both.getValue())
            ||washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Online.getValue()))){
                txtPrice.setText(( decimalFormatter.format((int)totalPrice.longValue()))+" - "+washer.getActiveCampaign().getCampaignDiscount()+"% = "+decimalFormatter.format(totalPriceWithDiscount)+" ₸");
            }

            if (request != null)
                request.setServicePrice(Integer.toString(totalPriceWithDiscount));
        }

        if (totalTime > 0) {
            txtTime.setText(Math.round(totalTime) + BA.str(R.string.space_min));
            if (request != null)
                request.setServiceTotalTime(totalTime);
        }

        calculatePaymentTypesTransactionAmount();
    }


    protected void calculatePaymentTypesTransactionAmount() {
        double cash = 0, card = 0, bonus = 0, deposit = 0, price = 0;

        if(bonusAmount.hasFocus()){
            if(washer != null && washer.getCompanyClientBonus() != null){
                try {
                    bonus = Double.parseDouble(bonusAmount.getText().toString());
                } catch (NumberFormatException e) {
                    bonus = 0;
                }
                if(bonus > washer.getCompanyClientBonus()){
                    bonusAmount.removeTextChangedListener(paymentWatcher);
                    bonusAmount.setText(decimalFormatter2.format(washer.getCompanyClientBonus().doubleValue()));
                    bonusAmount.addTextChangedListener(paymentWatcher);
                }
            }
        }

        if(depositAmount.hasFocus()){
            if(washer != null && washer.getCompanyClientDeposit() != null){
                try {
                    deposit = Double.parseDouble(depositAmount.getText().toString());
                } catch (NumberFormatException e) {
                    deposit = 0;
                }
                if(deposit > washer.getCompanyClientDeposit()){
                    depositAmount.removeTextChangedListener(paymentWatcher);
                    depositAmount.setText(decimalFormatter2.format(washer.getCompanyClientDeposit().doubleValue()));
                    depositAmount.addTextChangedListener(paymentWatcher);
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
            if(!cashAmount.hasFocus() && cash > 0){ cashAmount.removeTextChangedListener(paymentWatcher); cashAmount.setText((cash >= Math.abs(finalPriceDifference)?decimalFormatter2.format(cash-Math.abs(finalPriceDifference)):"0")); cashAmount.addTextChangedListener(paymentWatcher);
                cash = (cash >= Math.abs(finalPriceDifference)?cash-Math.abs(finalPriceDifference):0);
            }
            if(!cardAmount.hasFocus() && card > 0) { cardAmount.removeTextChangedListener(paymentWatcher); cardAmount.setText((card >= Math.abs(finalPriceDifference)?decimalFormatter2.format(card-Math.abs(finalPriceDifference)):"0")); cardAmount.addTextChangedListener(paymentWatcher);
                card = (card >= Math.abs(finalPriceDifference)?card-Math.abs(finalPriceDifference):0);
            }
            if(!bonusAmount.hasFocus() && bonus > 0) {bonusAmount.removeTextChangedListener(paymentWatcher); bonusAmount.setText((bonus >= Math.abs(finalPriceDifference)?decimalFormatter2.format(bonus-Math.abs(finalPriceDifference)):"0")); bonusAmount.addTextChangedListener(paymentWatcher);
                bonus = (bonus >= Math.abs(finalPriceDifference)?bonus-Math.abs(finalPriceDifference):0);
            }
            if(!depositAmount.hasFocus() && deposit > 0) {bonusAmount.removeTextChangedListener(paymentWatcher);  depositAmount.removeTextChangedListener(paymentWatcher); depositAmount.setText((deposit >= Math.abs(finalPriceDifference)?decimalFormatter2.format(deposit-Math.abs(finalPriceDifference)):"0")); depositAmount.addTextChangedListener(paymentWatcher);
                deposit = (deposit >= Math.abs(finalPriceDifference)?deposit-Math.abs(finalPriceDifference):0);
            }
        }

        finalPriceDifference = finalPrice - cash - card - bonus - deposit;

        txtPriceDifference.setText(decimalFormatter.format(finalPriceDifference) + " ₸");

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
            if (TextUtils.isEmpty(bonusAmount.getText().toString())) {
                bonusAmount.removeTextChangedListener(paymentWatcher);
                bonusAmount.setText("0");
                bonusAmount.addTextChangedListener(paymentWatcher);
            }
            calculatePaymentTypesTransactionAmount();
        } else {
            if (!TextUtils.isEmpty(bonusAmount.getText().toString()) && bonusAmount.getText().toString().equals("0")) {
                bonusAmount.removeTextChangedListener(paymentWatcher);
                bonusAmount.setText("");
                bonusAmount.addTextChangedListener(paymentWatcher);
            }
        }
    }

    @OnFocusChange(R.id.depositAmount)
    public void onFocusChangeDeposit(boolean focus) {
        if (!focus) {
            if (TextUtils.isEmpty(depositAmount.getText().toString())) {
                depositAmount.removeTextChangedListener(paymentWatcher);
                depositAmount.setText("0");
                depositAmount.addTextChangedListener(paymentWatcher);
            }
            calculatePaymentTypesTransactionAmount();
        } else {
            if (!TextUtils.isEmpty(depositAmount.getText().toString()) && depositAmount.getText().toString().equals("0")) {
                depositAmount.removeTextChangedListener(paymentWatcher);
                depositAmount.setText("");
                depositAmount.addTextChangedListener(paymentWatcher);
            }
        }
    }

    @OnFocusChange(R.id.cashAmount)
    public void onFocusChangeCash(boolean focus) {
        if (!focus) {
            if (TextUtils.isEmpty(cashAmount.getText().toString())) {
                cashAmount.removeTextChangedListener(paymentWatcher);
                cashAmount.setText("0");
                cashAmount.addTextChangedListener(paymentWatcher);
            }
            calculatePaymentTypesTransactionAmount();
            cashAmount.clearFocus();
        } else {
            if (!TextUtils.isEmpty(cashAmount.getText().toString()) && cashAmount.getText().toString().equals("0")) {
                cashAmount.removeTextChangedListener(paymentWatcher);
                cashAmount.setText("");
                cashAmount.addTextChangedListener(paymentWatcher);
            }
        }
    }

    @OnFocusChange(R.id.cardAmount)
    public void onFocusChangeCard(boolean focus) {
        if (!focus) {
            if (TextUtils.isEmpty(cardAmount.getText().toString())) {
                cardAmount.removeTextChangedListener(paymentWatcher);
                cardAmount.setText("0");
                cardAmount.addTextChangedListener(paymentWatcher);
            }
            calculatePaymentTypesTransactionAmount();
        } else {
            if (!TextUtils.isEmpty(cardAmount.getText().toString()) && cardAmount.getText().toString().equals("0")) {
                cardAmount.removeTextChangedListener(paymentWatcher);
                cardAmount.setText("");
                cardAmount.addTextChangedListener(paymentWatcher);
            }
        }
    }

    @OnClick(R.id.rootView)
    public void onRootViewClicked(){
        rootView.startAnimation(slideRightOut);
//        this.dismiss();
    }

    @OnClick(R.id.back)
    public void onBackClicked(){
        rootView.startAnimation(slideRightOut);
//        this.dismiss();
    }

    @OnClick(R.id.mainLayout)
    public void onMainViewClicked(){
        //must have empty implementation
    }

    @OnClick(R.id.btnApply)
    public void onApplyClicked(){
        if (finalPriceDifference != 0) {
            ToastUtil.displayAtTop(getActivity(), BA.str(R.string.balance_not_zero));
            return;
        }

        if(request != null && ((request.getServices() != null && request.getServices().size() > 0) ||(request.getGroupServices() != null && request.getGroupServices().size() > 0))) {
            BA.getEventBus().post(new ServiceSelectionEvent(washer, request.getPriceDetails(), request.getServices(), request.getGroupServices(), finalPrice, totalTime));
            activityActions.setSelectedServices(washer, request.getPriceDetails(), request.getServices(), request.getGroupServices(), finalPrice, totalTime);
        }
        else{
            ToastUtil.display(getActivity(),BA.str(R.string.select_one_service));
            return;
        }
        onBackClicked();
    }
}
