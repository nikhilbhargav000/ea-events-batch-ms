package com.easyapper.eventsbatchms.model;

public class PostEventDto {

	private String id;
	private String city;
	private String title;
	private String description;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "PostEventDto [id=" + id + ", city=" + city + ", title=" + title + ", description=" + description + "]";
	}
	
	
	
}
