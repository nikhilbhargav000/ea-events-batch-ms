package com.easyapper.eventsbatchms.reader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easyapper.eventsbatchms.utilities.EALogger;
import com.opencsv.CSVReader;

public class CsvFileReader {
	
	String filePath;
	
	EALogger logger = EALogger.getLogger();
	
	public CsvFileReader(String filePath) {
		this.filePath = filePath;
	}
	
	public List<String> getList() {
		List<String> valueList = new ArrayList<>();
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(filePath));
			String[] rowList = null;
			while((rowList=csvReader.readNext()) != null) {
				valueList.addAll(Arrays.asList(rowList));
			}
			
		} catch (FileNotFoundException e) {
			logger.warning("Invalid file path (Fle not found) : " + this.filePath, e);
		} catch (IOException e) {
			logger.warning("Invalid file path : " + this.filePath, e);
		} catch (Exception e) {
			logger.warning("Error while reading from file : " + this.filePath, e);
		} finally {
			if(csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException e) {
					logger.warning("Error while closing file : " + this.filePath, e);
				}
			}
		}
		return valueList;
	}

}
