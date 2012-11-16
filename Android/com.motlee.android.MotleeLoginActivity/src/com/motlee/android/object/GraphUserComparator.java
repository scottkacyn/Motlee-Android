package com.motlee.android.object;

import java.util.Comparator;

import com.facebook.GraphUser;

public class GraphUserComparator implements Comparator<GraphUser> {

	public int compare(GraphUser user1, GraphUser user2) {
		
		return user1.getName().compareTo(user2.getName());
	}

}
