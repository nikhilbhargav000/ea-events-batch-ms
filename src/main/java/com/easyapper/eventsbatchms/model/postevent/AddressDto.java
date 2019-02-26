package com.easyapper.eventsbatchms.model.postevent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class AddressDto implements Cloneable {
	
	private String id;
	private String city;
	private String street;
	private String pin;
	//Constructor
	public AddressDto(String id, String city, String street, String pin) {
		super();
		this.id = id;
		this.city = city;
		this.street = street;
		this.pin = pin;
	}
	public AddressDto() {
		
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
