package com.github.gewg34tRGEARGwe3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Purpose: Create a load generating tool for log aggregator
 * 
 * @author amit
 * 
 */

public class Application {

	final static int second = 1000 , minute = 60000;
	
	public static void main(String[] args) {
		
		double maxEvents = 1000 , testLen = 1*minute , ramp = 0.2*minute , tear = 0.2*minute;
		List<String> outputFiles = new ArrayList<>();
		outputFiles.add("output_"+getUnixTime()+".log");
		
		
		printProcessID();
		BriefLog();
		
		if(args.length != 0)
		{	
			// Parsing inputs and retrieving it simpler to inputs list
			List<String> inputs = parseInputs(args);
			if(inputs == null) {
				System.out.println("There's a problam with the Inputs..");
				System.exit(1);
			}
			maxEvents = Double.parseDouble(inputs.get(0)); 
			testLen = Double.parseDouble(inputs.get(1))*minute; 
			ramp = Double.parseDouble(inputs.get(2))*minute; 
			tear = Double.parseDouble(inputs.get(3))*minute;
			
			if(inputs.size() != 4) // output files provided
			{
				outputFiles.clear(); // clearing default file
				for(int i = 4; i < 7; i++){
					outputFiles.add(inputs.get(i));
				}
			}
		}
		
		LogGenerator logger = LogGenerator.getInstance(outputFiles);
		
		//PHASE: Ramp Up
		double incline = (maxEvents*second)/ramp;
		long time = System.currentTimeMillis();
		long toRamp;
		while( (toRamp = System.currentTimeMillis() - time) < ramp)
		{	
			logger.generateLogs(incline);
			incline = (maxEvents*second)/(ramp - toRamp);
		}
		
		
		// PHASE: Max Load
		time = System.currentTimeMillis();
		double maxLoad = testLen-ramp-tear;
		while(System.currentTimeMillis() - time < maxLoad)
		{
			logger.generateLogs(maxEvents);
		}
		
		
		// PHASE: Tear Down 
		incline = (maxEvents*second)/tear;
		long toTear;
		time = System.currentTimeMillis();
		while( (toTear = System.currentTimeMillis() - time) < tear)
		{
			if(minute/(maxEvents-incline) < 0)
				break;
			logger.generateLogs(maxEvents-incline);
			incline = (maxEvents*second)/(tear - toTear);
		}
	}
	
	
	/**  
	 * parsing inputs and checking for correct values:
	 */
	public static List<String> parseInputs(String[] params) {
		
		List<String> inputs = new ArrayList<>();
		String[] fileNames = null; 
		
		int i = 0 , cond = 4 , inc = 1; //short input format
		
		if(params.length > 5){ //long input format
			
			i = 1; cond = 8; inc = 2; 
		}
		
		for(i = 0; i < cond; i+=inc){
			inputs.add(params[i]);
		}
		
		// checking values
		if(Double.parseDouble(inputs.get(1)) > 60 || Double.parseDouble(inputs.get(1)) < 0.5) 
			return null;
		if(Double.parseDouble(inputs.get(2)) > 15 || Double.parseDouble(inputs.get(2)) < 0) 
			return null;
		if(Double.parseDouble(inputs.get(3)) > 15 || Double.parseDouble(inputs.get(3)) < 0) 
			return null;
		
		if(params.length == 10 || params.length == 5) // output provided
		{
			int index = 4; //short input format
			if(params.length == 10) 
				index = 9; //long input format
			
			fileNames = params[index].split(";");
			for(i = 0; i < 3; i++){
				inputs.add(fileNames[i]);
			}
		}
		return inputs;
	}
	
	public static long getUnixTime() {
		
		Date date = new Date();
		return date.getTime()/1000;
	}

	public static void printProcessID() {
		
		String pID = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		System.out.println("Process ID: " +Long.parseLong(pID.split("@")[0]));
	}
	
	public static void BriefLog() {
		final Logger brieflogger = Logger.getLogger(LogGenerator.class.getName());
		brieflogger.info("Executing tool.. Logs are being generated." );
	}
}
