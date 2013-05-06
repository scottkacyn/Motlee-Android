package com.motlee.android.object;

public class Relationship 
{
	public Integer followed_id;
	public Boolean is_active;
	public Boolean is_pending;
	
	public Relationship()
	{
		
	}

	public Relationship(Integer followId, Boolean isActive, Boolean isPending)
	{
		followed_id = followId;
		is_active = isActive;
		is_pending = isPending;
	}
}
