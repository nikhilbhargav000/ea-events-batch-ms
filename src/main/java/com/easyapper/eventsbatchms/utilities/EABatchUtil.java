package com.easyapper.eventsbatchms.utilities;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.exception.DateFormatNotSupportedException;
import com.easyapper.eventsbatchms.model.eventshigh.OrglEventsHighPriceDto;

@Component
public class EABatchUtil {
	
	@Autowired
	EALogger logger;
	
	@Value("${category.regex.dir}")
	String categoryRegexDir;
	
	public String getCategoryRegexFilePath(String fileName) {
		if(StringUtils.isNoneBlank(categoryRegexDir)) {
			return categoryRegexDir + fileName;
		}
		return null;
	}
	
	public String getPrice(OrglEventsHighPriceDto priceDto) {
		return priceDto.getValue() + " " + priceDto.getCurrency();
	}
	
	public  String geEATimeFormatStr(String timeStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(EABatchConstants.DATE_UAT_FORMAT_PATTERN);
		
		List<String> timeFormatPatternList = new ArrayList<>();
		timeFormatPatternList.add(EABatchConstants.TIME_FORMAT_PATTERN_SPPORTED_1);
		Date dateObj = null;
		for(String timeFormatPattern : timeFormatPatternList) {
			dateObj = this.getDateFormatObj(timeStr, timeFormatPattern);
			if(dateObj != null) {
				break;
			}
		}
		return this.geEATimeFormatStr(dateObj);
	}
	
	public String getDateUATStr(String strDate) throws DateFormatNotSupportedException {
		Date dateObj = this.getDateIfInputSupported(strDate);
		return this.getDateUATStr(dateObj);
	}
	
	public Date getDateIfInputSupported(String strDate) throws DateFormatNotSupportedException {
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
		return dateObj; 
	}
	
	public String getDateUATStr(Date dateObj) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(EABatchConstants.DATE_UAT_FORMAT_PATTERN);
		return dateFormat.format(dateObj);
	}
	
	public String geEATimeFormatStr(Date dateObj) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(EABatchConstants.TIME_EA_FORMAT_PATTERN);
		return dateFormat.format(dateObj);
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
