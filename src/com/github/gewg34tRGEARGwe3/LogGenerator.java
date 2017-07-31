package com.github.gewg34tRGEARGwe3;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is implemented by the singleton pattern.
 * This class Generates log strings according to the following format:
 * {ISO8601 Date and time}%{SPACE}%{LOG LEVEL}%{SPACE}%{Line1: 128 random characters}%
 * {CR + LF + 5 TAB}%{Line2: 100 random characters}%
 * {CR + LF + 5 TAB}%{Line3: 80 random characters}
 * 
 * Where {LOG LEVEL} can be: INFO, FINE, SEVERE
 * Line2 will appear with probability 15%
 * Line3 will appear with probability 5%
 * 
 * @author amit
 *
 */

public class LogGenerator {

	private static LogGenerator singleInstance = null;
	private static final Logger logger = Logger.getLogger(LogGenerator.class.getName());
	private static FileHandler handler;
	private static StringBuilder[] lines;
	final static int second = 1000 , minute = 60000;
	static int count = 0;
	
	/**
	 * private constructor, 
	 * invoked when an object of this class is instantiated for the first time
	 * setting the handler with the appropriate file
	 * Initializing 3 random-characters lines to be used as log messages 
	 */
	private LogGenerator(List<String> filesnames) {
		
		try {
			
			if(filesnames.size() > 1) {				// randomly selected file
				handler = new FileHandler( filesnames.get( (int)(Math.random()*3) ) );
			}
			else {
				handler = new FileHandler(filesnames.get(0)); //default file
			}	
			handler.setFormatter(new MyFormatter());
			logger.addHandler(handler);
			logger.setUseParentHandlers(false); // disable logs to console
			logger.setLevel(Level.FINE);
			
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		
		lines = new StringBuilder[]{new StringBuilder(128), 
									new StringBuilder(100), 
									new StringBuilder(80)
									};
		
		for (int i = 0; i < 3; i++) {
			for(int j = 0; j < lines[i].capacity(); j++) {
				lines[i].insert(j , ((char) (Math.random()*25+97)) );
			}
		}
	}
	
	/**
	 * Retrieving the singleton instance
	 * (lazy initialization)
	 */
	public static LogGenerator getInstance(List<String> filesnames) {
		
		if(singleInstance == null)	
		{
			singleInstance = new LogGenerator(filesnames);
		}
		return singleInstance;
	}
	
	/**
	 * Generating the log strings that shall be written to the files
	 */
	
	public void generateLogs(double maxEvents) {
		
		StringBuilder sb = new StringBuilder(); // will be written to the file
		sb.append(lines[0].toString()+"\n");

		// Using random to decide whether lines two and three will appear

		int num = (int) (Math.random()*100);
		if(num < 15)
		{
			sb.append("\t\t\t\t\t" + lines[1].toString() + "\n");
		}
		if(num < 5)
		{
			sb.append("\t\t\t\t\t" + lines[2].toString() + "\n");
		}

		// Using random to decide the log's level

		int setLevel = (int) (Math.random()*3);
		// ++count + " " +  add this to each case output to follow the number of logs
		// 4example: logger.info(++count + " " + sb.toString() + "\n");
		switch(setLevel) {
		case 0: 
			logger.info(++count + " " + sb.toString() + "\n");
			break;
		case 1: 
			logger.fine(++count + " " + sb.toString() + "\n");
			break;
		case 2: 
			logger.severe(++count + " " + sb.toString() + "\n");
		}
		
		try {
			Thread.sleep((long) (minute/maxEvents));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}