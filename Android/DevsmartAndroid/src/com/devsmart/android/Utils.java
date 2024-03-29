package com.devsmart.android;

import android.database.Cursor;
import android.util.Log;

public class Utils {
	
	public static String printCursor(Cursor cursor) {
		StringBuilder retval = new StringBuilder();
		
		retval.append("|");
		final int numcolumns = cursor.getColumnCount();
		for(int column=0;column<numcolumns;column++){
			String columnName = cursor.getColumnName(column);
			retval.append(String.format("%-20s |", columnName.substring(0, Math.min(20, columnName.length()))));
		}
		retval.append("\n|");
		for(int column=0;column<numcolumns;column++){
			for(int i=0;i<21;i++){
				retval.append("-");
			}
			retval.append("+");
		}
		retval.append("\n|");
		
		while(cursor.moveToNext()){
			for(int column=0;column<numcolumns;column++){
				String columnValue = cursor.getString(column);
				if(columnValue != null){
					columnValue = columnValue.substring(0, Math.min(20, columnValue.length()));
				}
				retval.append(String.format("%-20s |", columnValue));
			}
			retval.append("\n");
		}
		
		
		
		return retval.toString();
	}

	
	public static void log(String msg) {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		StackTraceElement element = Thread.currentThread().getStackTrace()[3];
		
		String logMsg = String.format("[%s::%s:%d] %s", 
				element.getClassName(),
				element.getMethodName(),
				element.getLineNumber(),
				msg
				);
		
		Log.i("", logMsg);
	}
	
}
