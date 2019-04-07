package com.easyapper.eventsbatchms.utilities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.easyapper.eventsbatchms.model.postevent.CategoriesResponseDto;
import com.easyapper.eventsbatchms.model.postevent.CategoryDto;

public class EACategoryValidator {

	@Autowired
	RestTemplate restTemplate;
	
	public String getValidCategory() {
		return null;
	}
	
	public List<CategoryDto> getCatrgories() {
		
		List<CategoryDto> categories = null;
		
		
		return categories;
	}
}
