package com.easyapper.eventsbatchms.model.eventbrite;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
public class EventBriteAddressDto {
	
	private String address_1;
	private String address_2;
	private String city;
	private String region;
	private String postal_code;
	private String country;
	private String latitude;
	private String longitude;
	private String localized_address_display;
	private String localized_area_display;
	private List<String> localized_multi_line_address_display;
}
