package com.easyapper.eventsbatchms.model.postevent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class EventDto implements Cloneable{
	@Null(message="_id should be null")
	private String _id;
	@Null(message="user_id should be null")
	private String user_id;
	@NotBlank(message="event_type should not be blank or null")
	private String event_type;
	private String event_category;
	private String event_subcategory;
	private LocationDto event_location;
	private String organizer_email;
	private String event_name;
	@ToString.Exclude 
	private String event_description;
	private String event_image_url;
	private String event_start_date;
	private String event_last_date;
	private String event_min_age;
	private String event_max_age;
	private String event_price;
	private EventBookingDto event_booking;
	@ToString.Exclude 
	@JsonIgnore
	private OrglEventsHighDto original_event;
	private String event_approved;
	//For subscribed
	private String posted_event_id;
	private String event_start_time;
	private String event_end_time;
	//Constructor for posted events
	public EventDto(String _id, String user_id, String event_type, String event_category, String event_subcategory,
			LocationDto event_location, String organizer_email, String event_name, String event_description,
			String event_image_url, String event_start_date, String event_last_date, String event_min_age,
			String event_max_age, String event_price, EventBookingDto event_booking,
			OrglEventsHighDto original_event, String approved) {
		super();
		this._id = _id;
		this.user_id = user_id;
		this.event_type = event_type;
		this.event_category = event_category;
		this.event_subcategory = event_subcategory;
		this.event_location = event_location;
		this.organizer_email = organizer_email;
		this.event_name = event_name;
		this.event_description = event_description;
		this.event_image_url = event_image_url;
		this.event_start_date = event_start_date;
		this.event_last_date = event_last_date;
		this.event_min_age = event_min_age;
		this.event_max_age = event_max_age;
		this.event_price = event_price;
		this.event_booking = event_booking;
		this.original_event = original_event;
		this.event_approved = approved;
	}
	//Constructor for subscribed events
	public EventDto(@Null(message = "_id should be null") String _id,
			@Null(message = "user_id should be null") String user_id,
			@NotBlank(message = "event_type should not be blank or null") String event_type, String event_start_date,
			String event_last_date, String posted_event_id, String event_start_time, String event_end_time) {
		super();
		this._id = _id;
		this.user_id = user_id;
		this.event_type = event_type;
		this.event_start_date = event_start_date;
		this.event_last_date = event_last_date;
		this.posted_event_id = posted_event_id;
		this.event_start_time = event_start_time;
		this.event_end_time = event_end_time;
	}
	public EventDto() {
		super();
	}
	//Deep Copy
	public EventDto clone() throws CloneNotSupportedException {
		EventDto cloneObj = (EventDto) super.clone();
		cloneObj.event_location = (LocationDto)  this.event_location.clone();
		cloneObj.event_booking = (EventBookingDto) this.event_booking.clone();
		return cloneObj;
	}
}
