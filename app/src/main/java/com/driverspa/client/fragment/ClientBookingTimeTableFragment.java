package com.driverspa.client.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.WasherTimeTableAdapter;
import com.driverspa.assist.BaseAssist;
import com.driverspa.model.CampaignType;
import com.driverspa.model.Washer;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.model.api.response.WasherTimeTableResponse;
import com.driverspa.model.api.response.WasherTimeTableResponse.BoxItem;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.Functions;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.L;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.BookRequestEvent;
import com.driverspa.util.otto.ws.BookResponseEvent;
import com.driverspa.util.otto.ws.WasherTimeTableRequestEvent;
import com.driverspa.util.otto.ws.WasherTimeTableResponseEvent;
import com.driverspa.view.ExpandableGridView;

import static com.driverspa.util.Constants.DAY_MINUTES;
import static com.driverspa.util.Constants.GRID_ID;
import static com.driverspa.util.Constants.GRID_STATUS;
import static com.driverspa.util.Constants.GRID_TIME_INTERVAL;
import static com.driverspa.util.Constants.GRID_TITLE;
import static com.driverspa.util.Constants.REQUEST_CODE;
import static com.driverspa.util.Constants.SLOT_AVAILABLE;
import static com.driverspa.util.Constants.SLOT_BUSY;
import static com.driverspa.util.Constants.SLOT_NOT_AVAILABLE;
import static com.driverspa.util.Constants.SLOT_TIME_INTERVAL;
import static com.driverspa.util.Constants.TODAY;
import static com.driverspa.util.Constants.TOMORROW;
import static com.driverspa.util.Constants.URL_PREFIX;
import static com.driverspa.util.Constants.WASHER_BOOKING_REQUEST_DATA;
import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;

public class ClientBookingTimeTableFragment extends ClientBaseFragment {
        
    String phone = UserPreferences.getUserPhone(BA.getContext());
    
	public interface ActivityActions{
		public void showProgressBar(boolean set);
		public void showBookingInfo(String bookingId, Washer washer, BookingRequest request);
	}		
    
	public static ClientBookingTimeTableFragment newInstance(Washer washer, BookingRequest request, int requestCode) {
		ClientBookingTimeTableFragment fragment = new ClientBookingTimeTableFragment();
		Bundle bundle = new Bundle();
		bundle.putString(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer) );
		bundle.putString(WASHER_BOOKING_REQUEST_DATA, JsonUtil.serialize(request) );
		bundle.putInt(REQUEST_CODE, requestCode );
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	@InjectView(R.id.txtWasherNameTitle)
	TextView washerTitleName;
	@InjectView(R.id.txtNoTime)
	TextView washerNoTime;		
	@InjectView(R.id.btnBookNext)
	Button buttonNext;
	@InjectView(R.id.btnRepeat)
	Button buttonRepeat;				
	@InjectView(R.id.gridView)
	ExpandableGridView gridview;
	@InjectView(R.id.btnToday)
	TextView buttonToday;		
	@InjectView(R.id.btnTomorrow)
	TextView buttonTomorrow;
	@InjectView(R.id.txtTime)
	TextView txtTime;
	@InjectView(R.id.txtPrice)
	TextView txtPrice;

	Date serverTime;

	private ActivityActions activityActions;	
	private Washer washer;
	private BookingRequest request;
    private List<HashMap<String,String>> gridList;
    private List<String> joinSlots;
    private WasherTimeTableAdapter adapter;
    private String selectedDay;
    private int requestCode;


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_client_booking_timetable, container,false);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			String washerStr = getArguments().getString(WASHER_DATA_TO_BOOK);
			String requestStr = getArguments().getString(WASHER_BOOKING_REQUEST_DATA);
			requestCode = getArguments().getInt(REQUEST_CODE);
			washer = JsonUtil.deserializeToWasher(washerStr);
			request = JsonUtil.deserializeToBookingRequest(requestStr);
		}
		
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.inject(this, view);
	        selectedDay = TODAY;
	        buttonToday.setSelected(true);

			Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			TextView titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
			titleView.setVisibility(View.VISIBLE);
			washerTitleName.setText("Выбрать время");
			if(washer != null && washer.getActiveCampaign() != null && (washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Both.getValue())
					||washer.getActiveCampaign().getCampaignType().getValue().equals(CampaignType.Online.getValue()))){
				titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_discount_list, 0);
				titleView.setCompoundDrawablePadding((int) Functions.dipToPixels(getActivity(), 5f));
			}

			titleView.setText(washer.getName().toLowerCase().contains("автомойка")?washer.getName():"Автомойка "+washer.getName());

			ViewGroup.LayoutParams layoutParams = gridview.getLayoutParams();			
			WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			
			int width = display.getWidth()-(int) Functions.dipToPixels(getActivity(), 37);
			layoutParams.width = width; //this is in pixels
			
			gridview.setLayoutParams(layoutParams);			
			gridview.setColumnWidth((display.getWidth()-(int) Functions.dipToPixels(getActivity(), 40))/4);
			gridview.setVisibility(View.GONE);
			buttonNext.setVisibility(View.GONE);
			loadData();
			if(requestCode == ActivityForResult.ACTIVITY_TIMETABLE_INFO){
				washerNoTime.setText("Нет доступных расписаний");
				txtTime.setVisibility(View.GONE);
			}
			else{
				txtPrice.setText(request.getServicePrice()+" ₸");
				txtTime.setText(request.getServiceTotalTime()+" мин.");
//				try {
//					if (washer != null && washer.getActiveCampaign() != null) {
//						int discnt = washer.getActiveCampaign().getCampaignDiscount();
//						txtPrice.setText((Integer.parseInt(request.getServicePrice()) + Integer.parseInt(request.getServicePrice()) * washer.getActiveCampaign().getCampaignDiscount()) + " - " + washer.getActiveCampaign().getCampaignDiscount() + "% = " + request.getServicePrice() + " ₸");
//					}
//				}
//				catch (Exception e){}
			}
	    }
	
	    @Subscribe
	    public void onWasherTimeTableReceived(WasherTimeTableResponseEvent event){
	    	setWaitScreen(false);
	    	if(event.getWasherTimeTable() != null){
	    		joinSlots = new ArrayList<String>();	    		
	    		WasherTimeTableResponse timeTable = event.getWasherTimeTable();
	    		HashMap<String, BoxItem> boxMap = timeTable.getResult().getBoxes();
				if(timeTable.getServerTime()!=null) {
					serverTime = Functions.getTZDate(timeTable.getServerTime(),washer.getCarwashTimeZone());
					L.d("server time "+serverTime);
				}

	    		if(boxMap != null && boxMap.size() > 0){
		    		for (Map.Entry<String,BoxItem> entry : boxMap.entrySet()) {
		    			  String key = entry.getKey();
		    			  BoxItem value = entry.getValue();
		    			  int i = 0;
		    			  for(String item : value.getSlots()){
		    				  if(joinSlots.size() >= i+1){		    					 
		    					 if(item.equals(SLOT_AVAILABLE)){ 		    						 
		    					    joinSlots.set(i,item);		
		    					   }
		    					 else if(!item.equals(SLOT_NOT_AVAILABLE)){
		    						 joinSlots.set(i,item);  
		    					  }
		    					 }
		    				  else		    					  
		    					  joinSlots.add(item);		    				  
		    				i++;
		    			  }
		            }		    		
		    		createGridList(washer,joinSlots);
	    		}
	    		else{
			    	washerNoTime.setVisibility(View.VISIBLE);
			    	buttonRepeat.setVisibility(View.VISIBLE);
	    		}
	    	}
	    	else{
		    	washerNoTime.setVisibility(View.VISIBLE);
		    	buttonRepeat.setVisibility(View.VISIBLE);
	    	}
	    }
	    
	    public void loadData(){
	    	washerNoTime.setVisibility(View.GONE);
	    	buttonRepeat.setVisibility(View.GONE);
	    	setWaitScreen(true);
	    	BA.getEventBus().post(new WasherTimeTableRequestEvent(washer.getId(), selectedDay));
	    }

	    @OnClick(R.id.btnToday)
	    public void onTodayButtonClick(){
	    	selectedDay = TODAY;
	     	buttonToday.setSelected(true);
	     	buttonTomorrow.setSelected(false);
	     	loadData();	    	
	    }
	    
	    @OnClick(R.id.btnTomorrow)
	    public void onTomorrowButtonClick(){
	    	selectedDay = TOMORROW;
	     	buttonToday.setSelected(false);
	     	buttonTomorrow.setSelected(true);
	     	loadData();
	    }
	    
	    @OnClick(R.id.btnBookNext)
	    public void onButtonNextClicked(){
	      if(adapter != null && adapter.getSelectedView() != null){
	    	  HashMap<String,String> selectedTimeMap = gridList.get(adapter.getSelectedPosition());
	    	  int hour = Integer.parseInt(selectedTimeMap.get(GRID_ID))/60;
	    	  int minut = Integer.parseInt(selectedTimeMap.get(GRID_ID))%60;
	    	  Calendar cal = Calendar.getInstance();
			  cal.setTimeZone(TimeZone.getTimeZone(washer.getCarwashTimeZone()));
	    	  if(selectedDay.equals(TOMORROW)) cal.add(Calendar.DATE, 1);
	    	  cal.set(Calendar.HOUR_OF_DAY,hour);
	    	  cal.set(Calendar.MINUTE,minut);
	    	  cal.set(Calendar.SECOND,0);
	    	  cal.set(Calendar.MILLISECOND,0);	    	  
	    	  Date bookDate = cal.getTime();
	    	  request.setTime(bookDate);	    	  	    	  
	    	  request.setCarwash(URL_PREFIX+washer.getId());	    	  
	    	  BA.getEventBus().post(new BookRequestEvent(request));
	    	  setWaitScreen(true);
	      }
			else{
			  ToastUtil.display(getActivity(),"Выберите время начала мойки");
		  }
	    }

	    @Subscribe
	    public void onBookResponseReceived(BookResponseEvent event){
	    	setWaitScreen(false);
	    	if(event.getResult() != null){
	    		if(BaseAssist.isSuccess(event.getResult())) {
					activityActions.showBookingInfo(event.getResult().getResponse().getId(), washer, request);
				}
	    		else
	    			ToastUtil.display(getActivity(), event.getResult().getMessage());
	    	}
	    	else{
	    		ToastUtil.display(getActivity(), "Ошибка при брони");
	    	}
	    }
	    
	    @OnClick(R.id.btnRepeat)
	    public void onButtonRepeatClicked(){
	      loadData();
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
		
	public void createGridList(Washer washer, List<String> slots){
		gridList = new ArrayList<HashMap<String,String>>();			
		Calendar calendar = Calendar.getInstance();

		if(serverTime != null) calendar.setTime(serverTime);

		int currHour = calendar.get(Calendar.HOUR_OF_DAY);
		int currMin = calendar.get(Calendar.MINUTE);

		int todayStartTime = currHour*60+currMin;
		int startTime = 0;
		int endTime = 0; 		
		if(washer.getSchedule().getToday() != null && washer.getSchedule().getToday().size() > 0){
			startTime = washer.getSchedule().getToday().get(0).getFrom();
			endTime = washer.getSchedule().getToday().get(0).getTo();
						
		   if(selectedDay.equals(TODAY) && startTime < todayStartTime && endTime > todayStartTime){
				startTime = todayStartTime;
		   }
		   else if(selectedDay.equals(TODAY) && endTime < todayStartTime){
			   startTime = todayStartTime;
			   gridview.setVisibility(View.GONE);
			   washerNoTime.setVisibility(View.VISIBLE);
			   buttonRepeat.setVisibility(View.GONE);
			   washerNoTime.setText("Нет доступных расписаний!");
		   }
		   
		   int time = 0;		
		   for(int i = 0; i < (DAY_MINUTES)/GRID_TIME_INTERVAL; i++){
		   if(time >= startTime && time <= endTime-GRID_TIME_INTERVAL){	   
			HashMap<String,String> gridMap = new HashMap<String,String>();
			HashMap<String,String> gridMapAdditional = new HashMap<String,String>();
			gridMap.put(GRID_ID, Integer.toString(time));
			gridMap.put(GRID_TITLE, String.format("%02d", time/60)+":"+String.format("%02d", time%60));
			gridMap.put(GRID_STATUS, SLOT_NOT_AVAILABLE);
            if(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)).equals(SLOT_AVAILABLE) &&
					(
					 (slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) != null && slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1).equals(SLOT_AVAILABLE)) //next 15 grid may be null
					   || slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) == null
					  )
					) {
				//First slot or both slots are available
				gridMap.put(GRID_STATUS, SLOT_AVAILABLE);
			}
            else if(!slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)).equals(SLOT_AVAILABLE) &&
					(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) != null &&
							(!slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1).equals(SLOT_AVAILABLE))
					    ) || slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) == null
					) {
				//both slots are not available
				gridMap.put(GRID_STATUS, SLOT_BUSY);
			}
			else if(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)).equals(SLOT_AVAILABLE) &&
					(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) != null &&
							(!slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1).equals(SLOT_AVAILABLE))
					) || slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) == null
					) {
				//First slot available
				gridMap.put(GRID_STATUS, SLOT_AVAILABLE);
				//Second slot not available
				if(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) != null) {
					int addGridTime = time+15;
					gridMapAdditional.put(GRID_ID, Integer.toString(addGridTime));
					gridMapAdditional.put(GRID_TITLE, String.format("%02d", (addGridTime)/60)+":"+String.format("%02d", (addGridTime)%60));
					gridMapAdditional.put(GRID_STATUS, SLOT_BUSY);
				}
			}
			else if(!slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)).equals(SLOT_AVAILABLE) &&
					(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) != null &&
							(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1).equals(SLOT_AVAILABLE))
					) || slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) == null
					) {
				//First slot not available
				gridMap.put(GRID_STATUS, SLOT_BUSY);
				//Second slot available
				if(slots.get(i*(GRID_TIME_INTERVAL/SLOT_TIME_INTERVAL)+1) != null) {
					int addGridTime = time+15;
					gridMapAdditional.put(GRID_ID, Integer.toString(addGridTime));
					gridMapAdditional.put(GRID_TITLE, String.format("%02d", (addGridTime)/60)+":"+String.format("%02d", (addGridTime)%60));
					gridMapAdditional.put(GRID_STATUS, SLOT_AVAILABLE);
				}
			}
			gridList.add(gridMap);
			if(gridMapAdditional.size() > 0){
				gridList.add(gridMapAdditional);
			}
		   }
		   time += GRID_TIME_INTERVAL;
		  }
		}			
					
		if(gridList.size() > 0){	
			gridview.setVisibility(View.VISIBLE);			
			adapter = new WasherTimeTableAdapter(getActivity(),gridList);
		    gridview.setAdapter(adapter);
		    gridview.setExpanded(true);
		    if(requestCode == ActivityForResult.ACTIVITY_TIMETABLE_BOOK){
			  buttonNext.setVisibility(View.VISIBLE);
			  gridview.setOnItemClickListener(new OnItemClickListener() {
		        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {	        	
		    	 if(gridList.get(position).get(GRID_STATUS).equals(SLOT_AVAILABLE)){
			            adapter.setSelectedPosition(position);	
			            if(adapter.getSelectedView() != null) adapter.getSelectedView().setSelected(false);
			            adapter.setSelectedView(v);
			            v.setSelected(true);	            		
		         	}	
		        }
		      });
		   }
		}
		else{
	    	washerNoTime.setVisibility(View.VISIBLE);
	    	buttonRepeat.setVisibility(View.VISIBLE);
		}
	}
}
