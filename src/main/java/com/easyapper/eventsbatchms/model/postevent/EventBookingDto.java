package com.easyapper.eventsbatchms.model.postevent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
public class EventBookingDto implements Cloneable{
	private String url;
	private String inquiry_url;
	
	public EventBookingDto() {
		
	}
	
	public EventBookingDto(String url, String inquiry_url) {
		super();
		this.url = url;
		this.inquiry_url = inquiry_url;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
