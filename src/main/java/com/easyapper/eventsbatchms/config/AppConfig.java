package com.easyapper.eventsbatchms.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AppConfig {
	
	@Bean
	RestTemplate eaRestTemplate() {
		
		RestTemplate restTemplate = new RestTemplate();
		//For Octet Stream support
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList( new MediaType[] {
				MediaType.APPLICATION_JSON, 
				MediaType.APPLICATION_OCTET_STREAM 
			}));
		List<HttpMessageConverter<?>> converterList = new ArrayList<>();
		converterList.add(converter);
		restTemplate.setMessageConverters(converterList);
		return restTemplate;
	}
	
}
