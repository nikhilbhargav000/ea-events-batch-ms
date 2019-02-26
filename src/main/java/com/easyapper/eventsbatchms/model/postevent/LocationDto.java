package com.easyapper.eventsbatchms.model.postevent;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class LocationDto implements Cloneable {
	private String longitude;
	private String latitude;
	private AddressDto address;
	//Constructor
	public LocationDto(String longitude, String latitude, AddressDto address) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
	}
	public LocationDto() {
		this.address = new AddressDto();
	}
	
	//Deep Copy
	public LocationDto clone() throws CloneNotSupportedException {
		LocationDto clonedObj = (LocationDto) super.clone();
		clonedObj.address = (AddressDto) this.address.clone();
		return clonedObj;
	}
	
}
