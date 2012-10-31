package com.motlee.android.object;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.PeriodType;

public class DateStringFomatter {

	private static DateTime currentTime;
	private static final long msInMin = 60000;
	private static final long msInHour = msInMin * 60;
	private static final long msInDay = msInHour * 24;
	private static final long msInWeek = msInDay * 7;
	// chose 30 as multiplier because we need to get close enough
	private static final long msInMonth = msInWeek * 30;
	private static final long msInYear = msInMonth * 12;
	
	public static String getPastDateString(Date date)
	{
		DateTime dateParam = new DateTime(date);
		currentTime = new DateTime();
		
		String dateString = "";
		
		Interval timeInterval = new Interval(dateParam, currentTime);
		long timeInMs = timeInterval.toDurationMillis();
		
		if (timeInMs > msInYear)
		{
			int numOfYears = timeInterval.toPeriod(PeriodType.years()).getYears();
			dateString = setUpString(numOfYears, "year");
		}
		else if (timeInMs > msInMonth)
		{
			int numOfMonths = timeInterval.toPeriod(PeriodType.months()).getMonths();
			dateString = setUpString(numOfMonths, "month");
		}
		else if (timeInMs > msInWeek)
		{
			int numOfWeeks = timeInterval.toPeriod(PeriodType.weeks()).getWeeks();
			dateString = setUpString(numOfWeeks, "week");
		}
		else if (timeInMs > msInDay)
		{
			int numOfDays = timeInterval.toPeriod(PeriodType.days()).getDays();
			dateString = setUpString(numOfDays, "day");
		}
		else if (timeInMs > msInHour)
		{
			int numOfHours = timeInterval.toPeriod(PeriodType.hours()).getHours();
			dateString = setUpString(numOfHours, "hour");
		}
		else if (timeInMs > msInMin)
		{
			int numOfMins = timeInterval.toPeriod(PeriodType.minutes()).getMinutes();
			dateString = setUpString(numOfMins, "min");
		}
		else
		{
			int numOfSecs = timeInterval.toPeriod(PeriodType.seconds()).getSeconds();
			dateString = setUpString(numOfSecs, "sec");
		}
		
		return dateString;
	}
	
	private static String setUpString(int time, String singularLabel)
	{
		String dateString = Integer.toString(time) + " " + singularLabel;
		if (time > 1)
		{
			dateString = dateString + "s ";
		}
		dateString = dateString + "ago";
		return dateString;
	}
	
}
