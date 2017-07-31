package com.github.gewg34tRGEARGwe3;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * this formatter is formatted by the following:
 * {ISO8601 Date and time}%{SPACE}%{LOG LEVEL}%{SPACE}
 */

public class MyFormatter extends Formatter {

	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
	
    @Override
    public String format(LogRecord record) {
        return format.format(Calendar.getInstance().getTime())
        		+" "+ record.getLevel() +" "+ record.getMessage();
    }
}
