package com.easyapper.eventsbatchms.model.eventbrite;

import com.easyapper.eventsbatchms.model.common.AbstractOriginalEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class EventBriteDto extends AbstractOriginalEvent {
//	private String id;
	private BriteTextHtml name;
	private BriteTextHtml description;
	private String url;
	private BriteDateTime start;
	private BriteDateTime end;
	private String organization_id;
	private String created;
	private String changed;
	private String published;
	private String capacity;
	private String capacity_is_custom;
	private String status;
	private String currency;
	private boolean listed;
	private boolean shareable;
	private boolean online_event;
	private int tx_time_limit;
	private boolean hide_start_date;
	private boolean hide_end_date;
	private String locale;
	private boolean is_locked;
	private String privacy_setting;
	private boolean is_series;
	private boolean is_series_parent;
	private String inventory_type;
	private boolean is_reserved_seating;
	private boolean show_pick_a_seat;
	private boolean show_seatmap_thumbnail;
	private boolean show_colors_in_seatmap_thumbnail;
	private String source;
	private boolean is_free;
	private String version;
	private String summary;
	private String logo_id;
	private String organizer_id;
	private String venue_id;
	private String category_id;
	private String subcategory_id;
	private String format_id;
	private String resource_uri;
	private boolean is_externally_ticketed;
	private EventBriteVenueDto venue;
//	private String logo;
	
	//Custom fields
	private EventBriteCategoryDto category;

	
	/** 
	 * Internal Classes 
	 * */	
	@Getter @Setter
	@ToString
	public class BriteTextHtml {
		private String text;
		private String html;
	}
	
	@Getter @Setter
	@ToString
	public static class BriteDateTime{
		private String timezone;
		private String local;
		private String utc;
	}
}
