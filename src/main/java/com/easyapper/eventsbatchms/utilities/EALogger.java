package com.easyapper.eventsbatchms.utilities;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.stereotype.Component;

@Component
public class EALogger {
	
	private static EALogger easyApperLogger;
	private Logger logger;
	private Handler handler;
	
	public EALogger(){
		logger = Logger.getLogger("EasyApper : Logger");
		handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
	}

	public static EALogger getLogger() {
		if(EALogger.easyApperLogger == null) {
			EALogger.easyApperLogger = new EALogger();
		}
		return easyApperLogger;
	}
	
	public void info(String msg){
		logger.info("EASY_APPER : INFO : " + msg);
	}

	public void warning(String msg){
		logger.warning("EASY_APPER : WARNING : " + msg);
	}
	
	public void warning(String msg, Exception e){
		String logMsg = "EASY_APPER : WARNING : " + msg;
		logger.log(Level.WARNING, logMsg, e);
	}
	
}
