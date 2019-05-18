package com.easyapper.eventsbatchms.utilities;

import org.springframework.stereotype.Component;

@Component
public class EABatchConstants {
	
	
	public static final String BATCH_REQUEST_USER_NAME = "Batch_Request_User";
	public static final String EA_POST_REQUEST_EVENTS_URL = "http://localhost:8080/users/" + BATCH_REQUEST_USER_NAME + "/events/";
	public static final String EA_GET_CATEGORIES_URL = "http://localhost:8080/categories";
	
	public static final String DELHI_EVENTS_URL = "https://developer.eventshigh.com/events/delhi?key=ev3nt5h1ghte5tK3y";
	public static final String BANGALORE_EVENTS_URL = "https://developer.eventshigh.com/events/bangalore?key=ev3nt5h1ghte5tK3y&cf=kids";
	public static final String MUMBAI_EVENTS_URL = "https://developer.eventshigh.com/events/mumbai?key=ev3nt5h1ghte5tK3y";
	
	public static final String EVENT_TYPE_POSTED = "posted";
	
	public static final String BATCH_USER_ID = "batch_user";
	
	public static final String FREE_PRICE_VALUE = "Free";

	public static final String DATE_UAT_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String TIME_EA_FORMAT_PATTERN = "HH:mm";
	public static final String DATE_FORMAT_PATTERN_SPPORTED_1 = "yyyy-MM-dd:HH-mm";
	public static final String DATE_FORMAT_PATTERN_SPPORTED_2 = "yyyy-MM-dd";
	
	public static final String TIME_FORMAT_PATTERN_SPPORTED_1 = "HH:mm:ss";
	
	public static final String EVENT_APPROVED_VAL_1 = "1";
	
	public static final int RETRYING_REQUEST_COUNT = 5;
	
	
}
