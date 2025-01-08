/**
 * 
 */
package com.driverspa.util;

import android.graphics.Typeface;

import java.util.HashMap;

/**
 * @author Yerzhan Tanatov
 * date: 09.10.2014
 * time: 17:36:10
 */
public class Constants {

    public final static int VIEW_HEADER = 2;
    public final static int VIEW_FOOTER = 1;
    public final static int VIEW_NORMAL = 0;

    public final static String PUSH_TYPE = "PUSH_TYPE";
    public final static String PUSH_DATA = "PUSH_DATA";

    public final static String WASHER_BASE = "WASHER_BASE";
	public final static String CAR_TYPE = "CAR_TYPE";	
	public final static String EXTRA_WASHER_ID = "EXTRA_WASHER_ID";
    public final static String EXTRA_CAMPAIGN_ID = "EXTRA_CAMPAIGN_ID";
    public final static String EXTRA_BOOK = "EXTRA_BOOK";
	public final static String EXTRA_BOX_ID = "EXTRA_BOX_ID";
	public final static String EXTRA_BOX_NAME = "EXTRA_BOX_NAME";
    public final static String EXTRA_BOX_WASHER_PERSON = "EXTRA_BOX_WASHER_PERSON";
    public static final String EXTRA_BOOKING_ID = "book_id";
    public static final String EXTRA_FARE_REQUEST_ID = "fare_request_id";
    public static final String EXTRA_PUSH_MESSAGE = "EXTRA_PUSH_MESSAGE";
    public static final String EXTRA_BOOK_DAY = "EXTRA_BOOK_DAY";
    public static final String BOOKING_INFO_TYPE = "book_info_type";
    public static final String FIRST_BOOK = "first_book";    
    public static final String PENDING = "pending";
    public static final String FINISHED = "finished";
    public static final String APPROVED = "approved";
    public static final String TEST = "test";
    public static final String REJECTED = "rejected";
    public static final String STARTED = "started";
    public static final String NOCOME = "nocome";    
    public static final String CANCELED = "canceled";
    public static final String STATUS = "STATUS";
    public static final String QUEUED = "queued";
    public static final String QUEUED_APPROVED = "queued_approved";
    public static final String QUEUED_REJECTED = "queued_rejected";
    public static final String QUEUED_FINISHED = "queued_finished";

    public static final String ONLINE = "online";    
    public static final String OFFLINE = "offline";    
    public static final String HYBRID = "hybrid";        

    public static final int ZOOM_LEVEL = 14;    
    public static final String URL_PREFIX = "/api/v1/public_carwash/";
    public static final int GRID_TIME_INTERVAL = 30;
    public static final int SLOT_TIME_INTERVAL = 15;
    public static final int DAY_MINUTES = 1440;
    public static final String SLOT_AVAILABLE = "+";
    public static final String SLOT_NOT_AVAILABLE = "-";
    public static final String SLOT_BUSY = "*";
    public static final String TODAY = "today";
    public static final String TOMORROW = "tomorrow";
    public final static String GRID_ID = "grid_id";
    public final static String GRID_TIME = "grid_time";
    public final static String GRID_BOX_ID = "grid_box_id";
    public final static String GRID_EXPIRED = "grid_box_id";
    public final static String GRID_TITLE = "grid_title";
    public final static String GRID_STATUS = "grid_status";
    public final static String GRID_BOOK_ID = "grid_book_id";

    public final static String ITEM_NAME = "item_name";
    public final static String ITEM_VALUE = "item_value";

    public static final String WASHER_DATA_TO_BOOK = "WASHER_DATA";
    public static final String WASHER_BOOK_FROM_MAP = "WASHER_BOOK_FROM_MAP";
    public static final String WASHER_BOOKING_REQUEST_DATA = "WASHER_BOOKING_DATA";
    
    public static final String WASHER_ID = "WASHER_ID";
    public static final String WASHER_NAME = "WASHER_NAME";
    public static final String REQUEST_CODE = "REQUEST_CODE";    
	public static final String WASHER_DATA = "WASHER_DATA";

	public static final String BOOKING_CREATED = "booking_created";
	public static final String REVIEW_CREATED = "review_created";
	public static final String BOOKING_FINISH_INFORM = "booking_finish_inform";


		
	public static final String LOG_TAG = "project_debug";
	
	public static Typeface PFSquareSansPro_Bold;
	public static Typeface PFSquareSansPro_Light;
	public static Typeface PFSquareSansPro_Regular;
	
	public static String host = "HOST";
	public class request_url{		
		public static final String add = "http://";		
	}
	
	public static final HashMap<String, String> bookStatus;
    public static HashMap<String,String> boxTypeMap = new HashMap<String,String>();

	static{

        //Book status map
		bookStatus = new HashMap<String,String>();
		bookStatus.put(QUEUED, "В очереди");
		bookStatus.put(QUEUED_APPROVED, "В боксе");
		bookStatus.put(QUEUED_REJECTED, "Удалено с очереди");
		bookStatus.put(QUEUED_FINISHED, "Услуга оказана");
		bookStatus.put(PENDING, "Ожидает");
		bookStatus.put(FINISHED, "Услуга оказана");
		bookStatus.put(APPROVED, "Одобрено");
		bookStatus.put(REJECTED, "Не одобрено");
		bookStatus.put(STARTED, "Начато");
		bookStatus.put(NOCOME, "Клиент не пришел");
		bookStatus.put(CANCELED, "Отменено клиентом");

        //Box type map
        boxTypeMap.put(HYBRID,"Гибрид");
        boxTypeMap.put(ONLINE,"Онлайн");
        boxTypeMap.put(OFFLINE,"Оффлайн");
    }

}
