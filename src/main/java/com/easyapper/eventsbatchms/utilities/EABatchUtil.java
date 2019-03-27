package com.easyapper.eventsbatchms.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.exception.DateFormatNotSupportedException;
import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighPriceDto;

@Component
public class EABatchUtil {
	
	@Autowired
	EALogger logger;
	
	public String getPrice(OrglEventsHighPriceDto priceDto) {
		return priceDto.getValue() + " " + priceDto.getCurrency();
	}
	
	private String getDateUATStr(Date dateObj) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(EABatchConstants.DATE_UAT_FORMAT_PATTERN);
		return dateFormat.format(dateObj);
	}


	public String getDateUATStr(String strDate) throws DateFormatNotSupportedException {
		String strUatDate = null;
		
		List<String> dateFormatPatternList = new ArrayList<>();
		dateFormatPatternList.add(EABatchConstants.DATE_FORMAT_PATTERN_SPPORTED_1);
		dateFormatPatternList.add(EABatchConstants.DATE_FORMAT_PATTERN_SPPORTED_2);
		Date dateObj = null;
		for (String dateFormatPattern : dateFormatPatternList) {
			dateObj = this.getDateFormatObj(strDate, dateFormatPattern);
			if (dateObj != null) {
				break;
			}
		}
		if(dateObj == null) {
			throw new DateFormatNotSupportedException();
		}
		return this.getDateUATStr(dateObj);
	}
	
	private Date getDateFormatObj(String strDate, String formatPatter) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatPatter);
		try {
			return dateFormat.parse(strDate);
		} catch (ParseException e) {
			logger.warning(e.getMessage(), e);
			return null;
		}
	}
	
}
