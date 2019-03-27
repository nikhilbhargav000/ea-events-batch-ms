package com.easyapper.eventsbatchms.model.postevent;

import java.util.List;

public class CategoriesResponseDto {
	List<CategoryDto> categories;
	//Constructor
	public CategoriesResponseDto(List<CategoryDto> categories) {
		super();
		this.categories = categories;
	}
	//Getter and Setter
	public List<CategoryDto> getCategories() {
		return categories;
	}
	public void setCategories(List<CategoryDto> categories) {
		this.categories = categories;
	}
	@Override
	public String toString() {
		return "CategoriesResponseDTO [categories=" + categories + "]";
	}
}
