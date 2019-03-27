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
		
		
		try {
			String url = EABatchConstants.GET_CATEGORIES_URL;
			ResponseEntity<CategoriesResponseDto> response = restTemplate.getForEntity(url, CategoriesResponseDto.class);
			if(response != null && response.getBody() != null) {
				categories = response.getBody().getCategories();
			}
		} catch (Exception e) {
			
		}
		
		return categories;
	}
}
