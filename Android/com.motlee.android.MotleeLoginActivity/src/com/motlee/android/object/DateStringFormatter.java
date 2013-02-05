package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.PeriodType;

public class DateStringFormatter {

	public static String UPCOMING = "Upcoming";
	
	private static DateTime currentTime;
	private static final long msInMin = 60000;
	private static final long msInHour = msInMin * 60;
	private static final long msInDay = msInHour * 24;
	private static final long msInWeek = msInDay * 7;
	// chose 30 as multiplier because we need to get close enough
	private static final long msInMonth = msInWeek * 30;
	private static final long msInYear = msInMonth * 12;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, M/d");
	
	public static String getEventDateString(Date startDate, Date endDate)
	{
		DateTime startDateParam = new DateTime(startDate);
		DateTime endDateParam = new DateTime(endDate);
		currentTime = new DateTime();
		
		String dateString = "";
		
		if (startDateParam.compareTo(currentTime) > 0)
		{
			Interval timeInterval = new Interval(currentTime, startDateParam);
			long timeInMs = timeInterval.toDurationMillis();
			
			if (timeInMs > msInDay)
			{
				return dateFormat.format(startDate);
			}
			if (timeInMs > 2 * msInHour)
			{
				return "Starting Today";
			}
			else
			{
				return "Starting Soon";
			}
		}
		else if (endDateParam.compareTo(currentTime) > 0)
		{
			Interval timeInterval = new Interval(currentTime, endDateParam);
			long timeInMs = timeInterval.toDurationMillis();
			
			if (timeInMs > 2 * msInHour)
			{
				return "Now";
			}
			else
			{
				return "Ending Soon";
			}
		}
		
		Interval timeInterval = new Interval(endDateParam, currentTime);
		long timeInMs = timeInterval.toDurationMillis();
		
		if (timeInMs > msInDay)
		{
			dateString = dateFormat.format(endDate);
		}
		else if (timeInMs > 5 * msInHour)
		{
			dateString = "Ended Today";
		}
		else
		{
			dateString = "Just Ended";
		}
		
		return dateString;
	}
	
	public static String getPastDateString(Date date)
	{
		DateTime dateParam = new DateTime(date);
		currentTime = new DateTime();
		
		String dateString = "";
		
		if (dateParam.compareTo(currentTime) >= 0)
		{
			return "Just Now";
		}
		
		Interval timeInterval = new Interval(dateParam, currentTime);
		long timeInMs = timeInterval.toDurationMillis();
		
		if (timeInMs > msInYear)
		{
			int numOfYears = timeInterval.toPeriod(PeriodType.years()).getYears();
			dateString = setUpString(numOfYears, "yr");
		}
		else if (timeInMs > msInMonth)
		{
			int numOfMonths = timeInterval.toPeriod(PeriodType.months()).getMonths();
			dateString = setUpString(numOfMonths, "mon");
		}
		else if (timeInMs > msInWeek)
		{
			int numOfWeeks = timeInterval.toPeriod(PeriodType.weeks()).getWeeks();
			dateString = setUpString(numOfWeeks, "wk");
		}
		else if (timeInMs > msInDay)
		{
			int numOfDays = timeInterval.toPeriod(PeriodType.days()).getDays();
			dateString = setUpString(numOfDays, "d");
		}
		else if (timeInMs > msInHour)
		{
			int numOfHours = timeInterval.toPeriod(PeriodType.hours()).getHours();
			dateString = setUpString(numOfHours, "hr");
		}
		else if (timeInMs > msInMin)
		{
			int numOfMins = timeInterval.toPeriod(PeriodType.minutes()).getMinutes();
			dateString = setUpString(numOfMins, "min");
		}
		else
		{
			//int numOfSecs = timeInterval.toPeriod(PeriodType.seconds()).getSeconds();
			dateString = "Just Now";
		}
		
		return dateString;
	}
	
	public static String getDescriptivePastDateString(Date date)
	{
		DateTime dateParam = new DateTime(date);
		currentTime = new DateTime();
		
		String dateString = "";
		
		if (dateParam.compareTo(currentTime) >= 0)
		{
			return "Just Now";
		}
		
		Interval timeInterval = new Interval(dateParam, currentTime);
		long timeInMs = timeInterval.toDurationMillis();
		
		if (timeInMs > msInYear)
		{
			int numOfYears = timeInterval.toPeriod(PeriodType.years()).getYears();
			dateString = setUpDescriptiveString(numOfYears, "year");
		}
		else if (timeInMs > msInMonth)
		{
			int numOfMonths = timeInterval.toPeriod(PeriodType.months()).getMonths();
			dateString = setUpDescriptiveString(numOfMonths, "month");
		}
		else if (timeInMs > msInWeek)
		{
			int numOfWeeks = timeInterval.toPeriod(PeriodType.weeks()).getWeeks();
			dateString = setUpDescriptiveString(numOfWeeks, "week");
		}
		else if (timeInMs > msInDay)
		{
			int numOfDays = timeInterval.toPeriod(PeriodType.days()).getDays();
			dateString = setUpDescriptiveString(numOfDays, "day");
		}
		else if (timeInMs > msInHour)
		{
			int numOfHours = timeInterval.toPeriod(PeriodType.hours()).getHours();
			dateString = setUpDescriptiveString(numOfHours, "hour");
		}
		else if (timeInMs > msInMin)
		{
			int numOfMins = timeInterval.toPeriod(PeriodType.minutes()).getMinutes();
			dateString = setUpDescriptiveString(numOfMins, "min");
		}
		else
		{
			int numOfSecs = timeInterval.toPeriod(PeriodType.seconds()).getSeconds();
			dateString = setUpDescriptiveString(numOfSecs, "sec");
		}
		
		return dateString;
	}
	
	private static String setUpDescriptiveString(int time, String singularLabel) {

		String dateString = Integer.toString(time) + " " + singularLabel;

		if (time > 1)
		{
			dateString = Integer.toString(time) + " " + singularLabel + "s ago";
		}
		else
		{
			dateString = Integer.toString(time) + " " + singularLabel + " ago";
		}
		
		return dateString;
	}

	private static String setUpString(int time, String singularLabel)
	{
		String dateString = Integer.toString(time) + " " + singularLabel;

		return dateString;
	}
	
}
