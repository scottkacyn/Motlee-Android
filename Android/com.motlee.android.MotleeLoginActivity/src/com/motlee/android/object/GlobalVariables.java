package com.motlee.android.object;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import android.graphics.Typeface;
import android.view.Display;

public class GlobalVariables {
	
	
	private Typeface gothamLightFont;
    
    private Display display;
    
    private Typeface helveticaNeueRegularFont;
    
    private Typeface helveticaNeueBoldFont;
    
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm aa");
    
    private String authoToken;
   
    private int userId;
    
    public static String FOMOS = "fomos";
    public static String ATTENDEES = "attendees";
    public static String DATE = "date";
    public static String LOCATION = "location";
    public static String FB_APP_ID = "283790891721595";
    
	private static GlobalVariables instance;
	
	public static synchronized GlobalVariables getInstance()
	{
		if (instance == null)
		{
			instance = new GlobalVariables();
		}
		return instance;
	}
	
	private GlobalVariables() {

	}
	
	public SimpleDateFormat getDateFormatter()
	{
		return simpleDateFormat;
	}
	
	public void setGothamLigtFont(Typeface gothamLightFont)
	{
		this.gothamLightFont = gothamLightFont;
	}
	
	public Typeface getGothamLightFont()
	{
		return this.gothamLightFont;
	}
	
	public void setDisplay(Display display)
	{
		this.display = display;
	}
	
	public Display getDisplay()
	{
		return this.display;
	}

	public Typeface getHelveticaNeueRegularFont() 
	{
		return helveticaNeueRegularFont;
	}

	public void setHelveticaNeueRegularFont(Typeface helveticaNeueRegularFont) 
	{
		this.helveticaNeueRegularFont = helveticaNeueRegularFont;
	}

	public Typeface getHelveticaNeueBoldFont() 
	{
		return helveticaNeueBoldFont;
	}

	public void setHelveticaNeueBoldFont(Typeface helveticaNeueBoldFont) 
	{
		this.helveticaNeueBoldFont = helveticaNeueBoldFont;
	}

	public String getAuthoToken() {
		return authoToken;
	}

	public void setAuthoToken(String authoToken) {
		this.authoToken = authoToken;
	}

	/**
	 * @return the userID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
