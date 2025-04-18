package com.driverspa.client.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.assist.BaseAssist;
import com.driverspa.model.AdminSearchFilter;
import com.driverspa.model.ClientInfo;
import com.driverspa.model.PushData;
import com.driverspa.model.User;
import com.driverspa.model.Washer;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.model.BookInfo;
import com.driverspa.util.Constants;
import com.driverspa.util.Functions;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.NewBookingPushRequestEvent;
import com.driverspa.util.otto.NewBookingPushResponseEvent;
import com.driverspa.util.otto.RemoveBookingPushRequestEvent;
import com.driverspa.util.otto.ws.BookCancelRequestEvent;
import com.driverspa.util.otto.ws.BookCancelResponseEvent;
import com.driverspa.util.otto.ws.BookInfoRequestEvent;
import com.driverspa.util.otto.ws.BookInfoResponseEvent;
import com.driverspa.util.otto.ws.ClientRequestEvent;
import com.driverspa.util.otto.ws.ClientResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;

import static com.driverspa.util.Constants.APPROVED;
import static com.driverspa.util.Constants.BOOKING_INFO_TYPE;
import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;
import static com.driverspa.util.Constants.FIRST_BOOK;
import static com.driverspa.util.Constants.PENDING;
import static com.driverspa.util.Constants.QUEUED;
import static com.driverspa.util.Constants.bookStatus;

public class ClientBookingInfoFragment extends ClientBaseFragment {
	DecimalFormat decimalFormatter = new DecimalFormat("#,###.##");
	public static final int TIMER_MINUTE = 14;
    String userId = UserPreferences.getUserId(BA.getContext());
    String phone = UserPreferences.getUserPhone(BA.getContext());
    
	public interface ActivityActions{
		public void showProgressBar(boolean set);
		public void editBooking(Washer washer, BookingRequest request);
		public void goToHome();
		public void openWasher(Washer washer);
		public void addReview(Washer washer);
		public void writeToWashme(String email, String title);
	}		
    List<HashMap<String,String>> gridList;
    List<String> joinSlots;            
    
	public static ClientBookingInfoFragment newInstance(String bookId, String bookInfoType) {
		ClientBookingInfoFragment fragment = new ClientBookingInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_BOOKING_ID, bookId );		
		bundle.putString(BOOKING_INFO_TYPE, bookInfoType );	
		fragment.setArguments(bundle);
		return fragment;
	}
	private Handler mHandler = new Handler();

	@BindView(R.id.txtWasherNameTitle)
	TextView washerTitleName;
	@BindView(R.id.txtWasherAddress)
	TextView washerAddress;
	@BindView(R.id.txtWasherPhone)
	TextView washerPhone;
	@BindView(R.id.txtClientName)
	TextView clientName;
	@BindView(R.id.txtClientPhone)
	TextView clientPhone;
	@BindView(R.id.txtClientCarNo)
	TextView clientCarNo;
	@BindView(R.id.txtClientCarMark)
	TextView clientCarMark;
	@BindView(R.id.txtClientCarType)
	TextView clientCarType;
	@BindView(R.id.txtClientServiceType)
	TextView clientServiceType;
	@BindView(R.id.txtClientServiceDate)
	TextView clientServiceDate;
	@BindView(R.id.txtClientServiceTime)
	TextView clientServiceTime;
	@BindView(R.id.txtClientServicePrice)
	TextView clientServicePrice;
	@BindView(R.id.txtBookingStatus)
	TextView txtBookingStatus;
	@BindView(R.id.txtDuration)
	TextView txtDuration;
	@BindView(R.id.discount)
	TextView txtDiscount;
	@BindView(R.id.comment)
	TextView txtComment;
	@BindView(R.id.subTitle)
	TextView subTitle;
	@BindView(R.id.txtClientGroupName)
	TextView txtClientGroupName;
	@BindView(R.id.remainingTimeLayout)
	View remainingTimeLayout;
	@BindView(R.id.remainingTime)
	TextView remainingTime;
	@BindView(R.id.order)
	TextView order;
	@BindView(R.id.commentLayout)
	View commentLayout;
	@BindView(R.id.layoutQueue)
	View queueLayout;
	@BindView(R.id.durationLayout)
	View durationLayout;
	@BindView(R.id.priceLayout)
	View priceLayout;
	@BindView(R.id.discountLayout)
	View discountLayout;
	@BindView(R.id.countsLayout)
	View countsLayout;
	@BindView(R.id.countsDateLayout)
	View countsDateLayout;
	@BindView(R.id.txtFinishedBooksDate)
	TextView finishedBooksDate;
	@BindView(R.id.txtFinishedBooksCount)
	TextView finishedBooksCount;
	ClientInfo client;
	@BindView(R.id.txtPriceDetail)
	TextView txtPriceDetail;

	private ActivityActions activityActions;	
	private Washer washer;
	private String bookId;
	private String bookInfoType;
	private BookInfo book;
	private User user;
	GPSTracker gps;
	TextView titleView;
	String date;
	String time;
	HashMap<String,PushData> bookingPushData;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_client_booking_info, container,false);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			gps = new GPSTracker(getActivity());
			bookId = getArguments().getString(EXTRA_BOOKING_ID);
			bookInfoType = getArguments().getString(BOOKING_INFO_TYPE);
			bookInfoType = !TextUtils.isEmpty(bookInfoType)?bookInfoType:"";									
			activityActions.showProgressBar(true);
			setWaitScreen(true);
		}

	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.bind(this, view);
			remainingTimeLayout.setVisibility(View.GONE);
			Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
			if(bookInfoType.equals(FIRST_BOOK)){
				remainingTimeLayout.setVisibility(View.VISIBLE);
			}
			loadData();
	    }

	@Subscribe
	    public void onBookingInfoReceived(BookInfoResponseEvent event){
	    	activityActions.showProgressBar(false);
	    	setWaitScreen(false);
			book = event.getResponse();
	    	if(event.getResponse() != null){
				if(bookInfoType.equals(FIRST_BOOK) && book.getStatus().equals(Constants.PENDING) || book.getStatus().equals(APPROVED)) {
						PushData data = new PushData(book.getId());
						data.setType("booking_"+book.getStatus());
						data.setText("");
						data.setObjectId(book.getId());
						BA.getEventBus().post(new NewBookingPushRequestEvent(data));
				}

				if (!book.isQueued())
			    	subTitle.setText("Бронь на имя");
				else {
					subTitle.setText("Мойка на имя");

					if(book.getStatus().equals(QUEUED)){
						subTitle.setVisibility(View.GONE);
						txtClientGroupName.setVisibility(View.GONE);
						clientCarType.setVisibility(View.GONE);
						durationLayout.setVisibility(View.GONE);
						priceLayout.setVisibility(View.GONE);
						discountLayout.setVisibility(View.GONE);
						queueLayout.setVisibility(View.VISIBLE);
						order.setText(book.getOrder()+"");
					}
				}

				titleView.setText(bookStatus.get(book.getStatus()));

				 if(book.getStatus().equals(PENDING)) {
					titleView.setText("Бронь отправлена");
					startUpdateTimer();
				}
				else{
					 remainingTimeLayout.setVisibility(View.GONE);
				 }


				washer = book.getWasher();
				washerTitleName.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						activityActions.openWasher(washer);
					}
				});
				washerTitleName.setPaintFlags(washerTitleName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				washerTitleName.setText(washer.getName().toLowerCase().contains("автомойка")?washer.getName():"Автомойка "+washer.getName());
	    		washerAddress.setText(washer.getAddress());
				if(!TextUtils.isEmpty(book.getMobile())) {
					washerPhone.setText(book.getMobile());
//					washerPhone.setText(Functions.maskPhoneNumber(book.getMobile(), Converters.PHONE_PATTERN));
//					washerPhone.setText(Functions.formatPhoneNumber("+7 ### ### ## ##",book.getMobile()));
				}
				else{
			       washerPhone.setText("Не указано");
			    }
				txtBookingStatus.setText(bookStatus.get(book.getStatus()));
				txtDuration.setText(book.getDuration()+" минут");
				txtDiscount.setText((book.getDiscount()!=null?book.getDiscount():"0")+" %");
				if(TextUtils.isEmpty(book.getComment())){
					commentLayout.setVisibility(View.GONE);
				}
				else{
					commentLayout.setVisibility(View.VISIBLE);
					txtComment.setText(book.getComment());
				}
				clientName.setText("");
				if(user != null) {
					clientName.setText(TextUtils.isEmpty(user.getFirstName()) ? "" : user.getFirstName());
				}

//				clientCarMark.setText(TextUtils.isEmpty(user.getCarModel())?"":user.getCarModel());
				clientCarNo.setText(event.getResponse().getClientKey());
	    		clientPhone.setText(phone);
	    		clientCarType.setText(BA.getReference().getCarType().get(Integer.parseInt(event.getResponse().getCartype())));

				String groupServices = "";
				if(book.getGroupServices() != null && book.getGroupServices().size() > 0) {
					for (String service : book.getGroupServices()) {
						groupServices = groupServices + getGroupServiceName(washer,service).getName() + "+";
					}
					groupServices = groupServices.substring(0, groupServices.length() - 1);
				}

				String services = "";
				if(book.getServices() != null && book.getServices().size() > 0) {
					for (Integer service : book.getServices()) {
						services = services + BA.getReference().getServices().get(service) + "+";
					}
					services = services.substring(0, services.length() - 1);
				}

				clientServiceType.setText("");
				if(!TextUtils.isEmpty(groupServices)) {
					if(!TextUtils.isEmpty(services))
						clientServiceType.append(groupServices + "+" + services);
					else
						clientServiceType.append(groupServices);
				}
				else
					clientServiceType.append(services);

	    		HashMap<String,String> timeMap = Functions.formatTZDate(book.getTime(),book.getTimeZone());
	    		clientServiceDate.setText(timeMap.get(Functions.DATE));
	    		clientServiceTime.setText(timeMap.get(Functions.TIME));

				Date bookTimeUTC = Functions.getTZDate(book.getTime(),book.getTimeZone());
				if(DateUtils.isToday(bookTimeUTC.getTime()))
					clientServiceDate.setText("Сегодня ("+timeMap.get(Functions.DATE)+")");
				else clientServiceDate.setText(timeMap.get(Functions.DATE));

				clientServiceTime.setText(timeMap.get(Functions.TIME));
//				clientServicePrice.setText(book.getPrice()+" ₸");
				clientServicePrice.setText(decimalFormatter.format(Double.parseDouble(book.getPrice()))+" ₸");

				txtPriceDetail.setVisibility(View.VISIBLE);
				txtPriceDetail.setText(Functions.formatPriceDetail(book.getPriceDetails(),Double.parseDouble(book.getPrice())));


				AdminSearchFilter filter = new AdminSearchFilter();
				if(washer != null) {
					filter.setCarwash(washer.getId());
					filter.setBookId(bookId);
					BA.getEventBus().post(new ClientRequestEvent(filter));
				}
			}
	    }
	    
	    public void loadData(){
	    	BA.getEventBus().post(new BookInfoRequestEvent(bookId));
		}

	    public void onButtonOkClicked(){
	        activityActions.goToHome();
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

		private Washer.GroupMenu getGroupServiceName(Washer washer, String groupServiceId){
			for(Washer.GroupMenu groupMenu : washer.getGroupMenu()){
				if(groupMenu.getId().equals(groupServiceId)){
					return groupMenu;
				}
			}
			return null;
		}

		public void onCancelBooking(){			
			Builder dialog = new Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
			dialog.setTitle("Отменить бронь?");
			dialog.setNeutralButton("Нет", null);
			dialog.setPositiveButton("Да", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setWaitScreen(true);
					BA.getEventBus().post(new BookCancelRequestEvent(book.getId()));
				}
			});
			dialog.show();
		}

//	   @OnClick(R.id.btnMapRoute)
	   public void onButtonMapRoute(){
		   if(gps.canGetLocation()){
			   Location currentLoc = gps.getLocation();
			   if(currentLoc != null && washer != null && washer.getLonLat() != null && washer.getLonLat().size() > 0){
				   String lat = Double.toString(washer.getLonLat().get(1));//"43.255058";
				   String lng = Double.toString(washer.getLonLat().get(0));
				   Intent intent = new Intent(Intent.ACTION_VIEW,
						   Uri.parse("http://maps.google.com/maps?saddr=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude() + "&daddr=" + lat + "," + lng + ""));
				   startActivity(intent);
			   }
			   else{
				   gps.showSettingsAlert();
			   }
		   }
		   else{
			   gps.showSettingsAlert();
		   }
	   }

//		@OnClick(R.id.btnEditBooking)
		public void onBookingEdit(){
			Builder dialog = new Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
			dialog.setTitle("Изменить бронь?");
			dialog.setNeutralButton("Нет", null);
			dialog.setPositiveButton("Да", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
		            BookingRequest request = new BookingRequest();
		            request.setRequestType(BookingRequest.MODIFY);
		            request.setBookId(book.getId());
		            request.setCarType(Integer.parseInt(book.getCartype()));
		            request.setServices(book.getServices());
					activityActions.editBooking(washer, request);
				}
			});
			dialog.show();
		}

		@Subscribe
		public void onBookCancelReceived(BookCancelResponseEvent event){
			setWaitScreen(false);
			if(event.getData() != null){
				if(BaseAssist.isSuccess(event.getData())) {
					Builder dialog = new Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
					BA.getEventBus().post(new RemoveBookingPushRequestEvent(new PushData(book.getId())));
					dialog.setTitle("Ваша бронь отменена");
					dialog.setPositiveButton("Ок", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activityActions.goToHome();
						}
					});
					dialog.show();
				}
				else{
					ToastUtil.display(getActivity(),event.getData().getMessage());
					activityActions.goToHome();
				}
			}
			else{
				ToastUtil.display(getActivity(), "Ошибка при отмене брони");
			}				
	}

	@Subscribe
	public void onUserSelfReceived(UserGetSelfResponseEvent event) {
		user = event.getUser();
	}

	@Subscribe
	public void onBookingPushReceived(NewBookingPushResponseEvent event){
		if(event != null && event.getPush() != null){
			bookingPushData = event.getPush().getData();
			loadData();
//			for (Map.Entry<String,PushData> entry : bookingPushData.entrySet()) {
//				String key = entry.getKey();
//				PushData data = entry.getValue();
////				if (data.getType().toLowerCase().contains("book") && data.getObjectId().equals(book.getId()) && (data.getType().contains("finish") || data.getType().contains("rejected")|| data.getType().contains("canceled"))){
////					activityActions.goToHome();
////				}
////				else if (data.getType().toLowerCase().contains("book") && data.getObjectId().equals(book.getId()) && (data.getType().contains("approved"))){
//
////				}
//			}
		}
	}

	public void updateTimeRemaining(long currentTime) {
		if(book != null && book.getTs() != null) {
			long timeDiff = currentTime - book.getTs().getTime();
			if (timeDiff > 0) {
				int seconds = (int) (timeDiff / 1000) % 60;
				int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
				int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);

				if ((TIMER_MINUTE - minutes) <= 0){
//				  remainingTime.setVisibility(View.GONE);
					remainingTime.setText("00:00");
				}
				else if(book.getStatus().equals(PENDING)){
					remainingTimeLayout.setVisibility(View.VISIBLE);
					remainingTime.setText(String.format("%02d", (TIMER_MINUTE - minutes)) + ":" + String.format("%02d", (59 - seconds)));
//				  remainingTime.setText(Html.fromHtml("Необходимо подтвердить бронь в течении " + "<font color=\"#DC143C\">" + String.format("%02d", (TIMER_MINUTE - minutes)) + ":" + String.format("%02d", (59 - seconds)) + "</font>"));
				}
			}
		}
	}

	@OnClick(R.id.btnCall)
	public void onCallButtonClicked(){
		if(book != null) {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + book.getMobile().toString()));
			startActivity(intent);
		}
	}

	private Runnable updateRemainingTimeRunnable = new Runnable() {
		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			updateTimeRemaining(currentTime);
		}
	};

	private void startUpdateTimer() {
		Timer tmr = new Timer();
		tmr.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.post(updateRemainingTimeRunnable);
			}
		}, 1000, 1000);
	}

	public void showOptions(){
		if(washer != null && book != null) {
			if (!book.isQueued()) {
				if (book.getStatus().equals(Constants.PENDING) || book.getStatus().equals(Constants.APPROVED)) {
					Builder b = new Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
					b.setItems(R.array.cancel_washer_book_options, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							switch (i) {
								case 0:
									onCancelBooking();
									break;
								case 1:
									onCallButtonClicked();
									break;
								case 2:
									onButtonMapRoute();
									break;
								case 3:
									activityActions.addReview(washer);
									break;
								case 4:
									String email = "info@washme.kz";
									String title = "Пожаловаться на автомойку " + washer.getName().toLowerCase().replace("автомойка", "");
									activityActions.writeToWashme(email, title);
									break;
							}
						}
					});
//			  b.setTitle(washer.getName().toLowerCase().contains("автомойка")?washer.getName():"Автомойка "+washer.getName());
//					b.setTitle("Опции");
					b.show();
				} else {
					Builder b = new Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
					b.setItems(R.array.information_washer_info_options, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							switch (i) {
								case 0:
									onCallButtonClicked();
									break;
								case 1:
									onButtonMapRoute();
									break;
								case 2:
									activityActions.addReview(washer);
									break;
								case 3:
									String email = "info@washme.kz";
									String title = "Пожаловаться на автомойку " + washer.getName().toLowerCase().replace("автомойка", "");
									activityActions.writeToWashme(email, title);
									break;
							}
						}
					});
//			  b.setTitle(washer.getName().toLowerCase().contains("автомойка")?washer.getName():"Автомойка "+washer.getName());
//					b.setTitle("Опции");
					b.show();
				}
			}
		}
	}

	@Subscribe
	public void onClientResponseReceived(ClientResponseEvent event){
		setWaitScreen(false);
		if(event.getResult() != null){
			if(BaseAssist.isSuccess(event.getResult())) {
				client = event.getResult().getClientInfo();
				if(client!=null){
					if(client.getFinishedBooksCount() != null){
						countsLayout.setVisibility(View.VISIBLE);
						finishedBooksCount.setText(client.getFinishedBooksCount()+" раз(а)");
						if(client.getRegistered())
							finishedBooksCount.setPaintFlags(finishedBooksCount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
						if(client.getLastFinishedBookDate() != null){
							countsDateLayout.setVisibility(View.VISIBLE);
//							finishedBooksDate.setPaintFlags(finishedBooksDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
							HashMap<String,String> timeMap = Functions.formatUTCDate(client.getLastFinishedBookDate());
							finishedBooksDate.setText(timeMap.get(Functions.DATE));
						}
					}
				}
			}
			else{
				countsLayout.setVisibility(View.GONE);
				countsDateLayout.setVisibility(View.GONE);
			}
		}
		else{
			countsLayout.setVisibility(View.GONE);
			countsDateLayout.setVisibility(View.GONE);
		}
	}

	public boolean canBackPressed(){
		if(book!=null && (book.getStatus().equals(PENDING) || book.getStatus().equals(APPROVED)))
		   return false;
		return  true;
	}
}
