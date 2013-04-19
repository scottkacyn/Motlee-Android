package com.motlee.android.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.Comment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.Friend;
import com.motlee.android.object.Like;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.SharePref;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DatabaseWrapper {
	
	private DatabaseHelper helper;

	private Context mContext;
	
	public DatabaseWrapper(Context context) {
		
		helper = DatabaseHelper.getInstance(context.getApplicationContext());
		
		mContext = context;
	}
	
	public void createOrUpdateUser(UserInfo user)
	{
		try
		{
			helper.getUserDao().createOrUpdate(user);
		}
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to createOrUpdateUser for user", e);
		}
	}
	
	public void createIfNotExistsUser(UserInfo user)
	{
		try
		{
			helper.getUserDao().createIfNotExists(user);
		}
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to createOrUpdateUser for user", e);
		}
	}
	
	public Cursor getPhotoCursor(Integer eventId)
	{
		CloseableIterator<PhotoItem> iterator = null;
		try 
		{
			QueryBuilder<PhotoItem, Integer> qb = helper.getPhotoDao().queryBuilder();
			qb.where().eq("event_id", eventId);
			// when you are done, prepare your query and build an iterator

			iterator = helper.getPhotoDao().iterator(qb.prepare());

		   AndroidDatabaseResults results =
		       (AndroidDatabaseResults)iterator.getRawResults();
		   Cursor cursor = results.getRawCursor();
		   return cursor;
		} 
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to get cursor from db");
			return null;
		}
		finally 
		{
			if (iterator != null)
			{
				iterator.closeQuietly();
			}
		}
	}
	
	public void deleteEvent(EventDetail eDetail)
	{
		try
		{
			helper.getEventDao().deleteById(eDetail.getEventID());
		}
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to deleteEvent eventDetail", e);
		}
	}
	
	public Collection<PhotoItem> getAllUploadingPhotos()
	{
		try
		{
			QueryBuilder<PhotoItem, Integer> qb = helper.getPhotoDao().queryBuilder();
			qb.where().lt("id", 0);
			return helper.getPhotoDao().query(qb.prepare());
		}
		catch (Exception ex)
		{
			Log.e("DatabaseWrapper", "Failed to getAllUploadingPhotos", ex);
			return new ArrayList<PhotoItem>();
		}
	}
	
	public ArrayList<Integer> getFriends()
	{
		try
		{
			Collection<Friend> friends = helper.getFriendsDao().queryForAll();

			ArrayList<Integer> user_ids = new ArrayList<Integer>();
			for (Friend friend : friends)
			{
				if (friend.user_id != null)
				{
					user_ids.add(friend.user_id);
				}
			}
			return user_ids;
		}
		catch (Exception ex)
		{
			Log.e("DatabaseWrapper", "Failed to getAllUploadingPhotos", ex);
			return new ArrayList<Integer>();
		}
	}
	
	public Collection<PhotoItem> getPhotosDescending(Integer eventId)
	{
		try 
		{
			QueryBuilder <PhotoItem, Integer> queryBuilder = helper.getPhotoDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId); // Here is the problem
			queryBuilder.orderBy("id", false);
			return helper.getPhotoDao().query(queryBuilder.prepare());
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getPhotos for event", e);
			return new ArrayList<PhotoItem>();
		}
	}
	
	public void updatePhoto(PhotoItem photo)
	{
		try
		{
			helper.getPhotoDao().createOrUpdate(photo);
		}
		catch (Exception ex)
		{
			Log.e("DatabaseWrapper", "Failed to updatePhoto", ex);
		}
	}
	
	public long getStreamCount()
	{
		try
		{
			return helper.getEventDao().countOf();
		}
		catch (Exception ex)
		{
			Log.e("DatabaseWrapper", "Failed to getStreamCount", ex);
			return -1;
		}
	}
	
	public Date getOldestUpdatedTime()
	{
		try
		{
			QueryBuilder<EventDetail, Integer> qb = helper.getEventDao().queryBuilder();
			qb.orderBy("updated", true);
			
			ArrayList<EventDetail> events = new ArrayList<EventDetail>(helper.getEventDao().query(qb.prepare()));
			
			if (events.size() > 0)
			{
				return events.get(0).updated_at;
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex)
		{
			Log.e("DatabaseWrapper", "Failed to getOldestUpdatedTime", ex);
			return null;
		}
	}
	
	public void updateComment(Comment comment)
	{
		try
		{
			helper.getCommentDao().update(comment);
		}
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to deleteEvent eventDetail", e);
		}
	}
	
	public void createOrUpdateEvent(EventDetail eDetail)
	{
		try 
		{
			helper.getEventDao().createOrUpdate(eDetail);
		} 
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to createOrUpdate eventDetail", e);
		}
	}
	
	public PhotoItem getMostRecentPhoto(Integer eventId)
	{
		try
		{
			QueryBuilder<PhotoItem, Integer> qb = helper.getPhotoDao().queryBuilder();
			qb.where().eq("event_detail", eventId);
			qb.orderBy("created_at", true);
			
			ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>(helper.getPhotoDao().query(qb.prepare()));
			
			if (photos.size() > 0)
			{
				return photos.get(0);
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex)
		{
			Log.e("DatabaseWrapper", "Failed to getMostRecentPhoto for eventId " + eventId, ex);
			return null;
		}
	}
	
	public Collection<EventDetail> getEvents(Set<Integer> eventIds)
	{
		try
		{
			if (eventIds.size() > 0)
			{
				QueryBuilder<EventDetail, Integer> qb = helper.getEventDao().queryBuilder();
				Where<EventDetail, Integer> where = qb.where();
				Iterator<Integer> iterator = eventIds.iterator();
				
				while (iterator.hasNext())
				{
					where.eq("id", iterator.next());
				}
				where.or(eventIds.size());
				return helper.getEventDao().query(qb.prepare());
			}
			else
			{
				return new ArrayList<EventDetail>();
			}
		}
		catch (SQLException e)
		{
			Log.e("DatabaseWrapper", "Failed to getEvents eventDetail", e);
			return null;
		}
	}
	
	public Cursor getAllEventsInCursor()
	{
		CloseableIterator<EventDetail> iterator = null;
		try 
		{
			QueryBuilder<EventDetail, Integer> qb = helper.getEventDao().queryBuilder();
			qb.where();
			// when you are done, prepare your query and build an iterator

			iterator = helper.getEventDao().iterator(qb.prepare());

		   AndroidDatabaseResults results =
		       (AndroidDatabaseResults)iterator.getRawResults();
		   Cursor cursor = results.getRawCursor();
		   return cursor;

		} 
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to get cursor from db");
			return null;
		}
		finally 
		{
			if (iterator != null)
			{
				iterator.closeQuietly();
			}
		}
	}
	
	public Collection<Integer> getAllEventIds()
	{
		try 
		{
			Collection<Integer> eventIds = new ArrayList<Integer>();
			QueryBuilder<EventDetail, Integer> queryBuilder = helper.getEventDao().queryBuilder();
			queryBuilder.selectColumns("id");
			Collection<EventDetail> eDetails = helper.getEventDao().query(queryBuilder.prepare());
			for (EventDetail detail : eDetails)
			{
				eventIds.add(detail.getEventID());
			}
			return eventIds;
		}
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getAllEventIds");
			return null;
		}
	}
	
	public long getPhotoCount(Integer eventId)
	{
		try
		{
			QueryBuilder<PhotoItem, Integer> queryBuilder = helper.getPhotoDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId);
			queryBuilder.setCountOf(true);
			return helper.getPhotoDao().countOf(queryBuilder.prepare());
		}
		catch (SQLException e)
		{
			Log.e("DatabaseWrapper", "Failed to getPhotoCount");
			return -1;
		}
	}
	
	public void createIfNotExistsEvent(EventDetail eDetail)
	{
		try {
			helper.getEventDao().createIfNotExists(eDetail);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to createIfNotExists eventDetail", e);
		}
	}
	
	public void createLocation(LocationInfo location)
	{
		try {
			helper.getLocationDao().createIfNotExists(location);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to createLocation", e);
		}
	}
	
	public LocationInfo getLocation(Integer locationId)
	{
		if (locationId == null || locationId < 1)
		{
			return new LocationInfo();
		}
		else
		{
			try {
				return helper.getLocationDao().queryForId(locationId);
			} catch (SQLException e) {
				Log.e("DatabaseWrapper", "Failed to getLocation location", e);
				return new LocationInfo();
			}
		}
	}
	
	public int getAttendeeCount(Integer eventId)
	{
		try
		{
			QueryBuilder<Attendee, Integer> queryBuilder = helper.getAttendeeDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId);
			queryBuilder.setCountOf(true);
			long count = helper.getAttendeeDao().countOf(queryBuilder.prepare());
			
			EventDetail eDetail = helper.getEventDao().queryForId(eventId);
			if (eDetail.getAttendeeCount() > (int) count)
			{
				return eDetail.getAttendeeCount();
			}
			else
			{
				return (int) count;
			}
		}
		catch (Exception e) {
			Log.e("DatabaseWrapper", "Failed to getAttendeeCount", e);
			return -1;
		}
	}
	
	public void updatePhotos(Integer eventId, Collection<PhotoItem> newPhotos)
	{
		try 
		{
			QueryBuilder<PhotoItem, Integer> queryBuilder = helper.getPhotoDao().queryBuilder();
			Where<PhotoItem, Integer> where = queryBuilder.where();
			where.eq("event_detail", eventId);
			where.and();
			where.gt("id", 0);
			Collection<PhotoItem> photos = helper.getPhotoDao().query(queryBuilder.prepare());
			Iterator<PhotoItem> iterator = photos.iterator();
			while (iterator.hasNext())
			{
				PhotoItem photo = iterator.next();
				if (newPhotos.contains(photo))
				{
					iterator.remove();
					newPhotos.remove(photo);
				}
			}
			
			if (photos.size() > 0)
			{
				DeleteBuilder<PhotoItem, Integer> deleteBuilder = helper.getPhotoDao().deleteBuilder();
				where = deleteBuilder.where();
				for (PhotoItem photo : photos)
				{
					where.eq("id", photo.id);
				}
				where.or(photos.size());
				helper.getPhotoDao().delete(deleteBuilder.prepare());
			}
			
			for (PhotoItem photo : newPhotos)
			{
				helper.getPhotoDao().createOrUpdate(photo);
			}
		} 
		catch (SQLException e)
		{
			Log.e("DatabaseWrapper", "Failed to updatePhoto for eventDetail", e);
		}
	}
	
	public void updateAttendees(Integer eventId, Collection<Attendee> newAttendees)
	{
		try 
		{
			QueryBuilder<Attendee, Integer> queryBuilder = helper.getAttendeeDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId);
			Collection<Attendee> attendees = helper.getAttendeeDao().query(queryBuilder.prepare());
			Iterator<Attendee> iterator = attendees.iterator();
			while (iterator.hasNext())
			{
				Attendee attendee = iterator.next();
				if (newAttendees.contains(attendee))
				{
					iterator.remove();
					newAttendees.remove(attendee);
				}
			}
			
			if (attendees.size() > 0)
			{
				DeleteBuilder<Attendee, Integer> deleteBuilder = helper.getAttendeeDao().deleteBuilder();
				Where<Attendee, Integer> where = deleteBuilder.where();
				for (Attendee attendee : attendees)
				{
					where.eq("id", attendee.id);
				}
				where.or(attendees.size());
				helper.getAttendeeDao().delete(deleteBuilder.prepare());
			}
			
			for (Attendee attendee : newAttendees)
			{
				helper.getAttendeeDao().createOrUpdate(attendee);
			}
		} 
		catch (SQLException e)
		{
			Log.e("DatabaseWrapper", "Failed to updateAttendee for eventDetail", e);
		}
	}
	
	/*public void updateFriendsList(ArrayList<Long> uids)
	{
		try {
			ArrayList<Friend> friends = new ArrayList<Friend>(helper.getFriendsDao().queryForAll());
			
			for (Friend friend : friends)
			{
				if (uids.contains(friend.uid))
				{
					uids.remove(friend.uid);
				}
			}
			
			for (Long uid : uids)
			{
				Friend friend = new Friend(uid);
				
				helper.getFriendsDao().createOrUpdate(friend);
			}
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to updateFriendsList for user", e);
		}
	}*/
	
	public boolean isFriend(Integer userId)
	{
		try {
			QueryBuilder<Friend, Integer> queryBuilder = helper.getFriendsDao().queryBuilder();
			queryBuilder.where().eq("user_id", userId);
			ArrayList<Friend> friends = new ArrayList<Friend>(helper.getFriendsDao().query(queryBuilder.prepare()));
			if (friends.size() == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getFriend for user", e);
			return false;
		}
	}
	
	public boolean isFriend(Long uid)
	{
		try {
			QueryBuilder<Friend, Integer> queryBuilder = helper.getFriendsDao().queryBuilder();
			queryBuilder.where().eq("uid", uid);
			ArrayList<Friend> friends = new ArrayList<Friend>(helper.getFriendsDao().query(queryBuilder.prepare()));
			if (friends.size() > 0)
			{
				return true;
			}
			else
			{
				return false;
			}

		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getFriend for user", e);
			return false;
		}
	}
	
	public PhotoItem getPhoto(Integer photoId)
	{
		try {
			return helper.getPhotoDao().queryForId(photoId);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getPhoto for user", e);
			return null;
		}
	}
	
	public void updateMessages(Integer eventId, Collection<StoryItem> stories)
	{
		try 
		{
			UpdateBuilder<EventDetail, Integer> updateBuilder = helper.getEventDao().updateBuilder();
			updateBuilder.updateColumnValue("stories", stories);
			updateBuilder.where().idEq(eventId);
			updateBuilder.update();
		} 
		catch (SQLException e)
		{
			Log.e("DatabaseWrapper", "Failed to updateMessages for eventDetail", e);
		}
	}
	
	@SuppressWarnings("finally")
	public EventDetail getEvent(Integer eventId)
	{
		EventDetail event = null;
		try 
		{
			event = helper.getEventDao().queryForId(eventId);
		} 
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to getEvent for eventDetail", e);
		}
		finally
		{
			return event;
		}
	}
	
	public Collection<EventDetail> getAllEvents()
	{
		try {	
			Collection<EventDetail> events = helper.getEventDao().queryForAll();
			if (events != null)
			{
				return events;
			}
			else
			{
				return new ArrayList<EventDetail>();
			}
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getAllEvents", e);
			return new ArrayList<EventDetail>();
		}
	}
	
	public Collection<EventDetail> getEventsForUser(Integer userId)
	{
		try
		{
			QueryBuilder<Attendee, Integer> queryBuilder = helper.getAttendeeDao().queryBuilder();
			queryBuilder.where().eq("user_id", userId);
			Collection<Attendee> attendees = helper.getAttendeeDao().query(queryBuilder.prepare());
			
			Collection<Attendee> allAttendees = helper.getAttendeeDao().queryForAll();
			
			Collection<EventDetail> eDetails = new ArrayList<EventDetail>();
			
			for (Attendee attendee : attendees)
			{
				EventDetail eDetail = getEvent(attendee.event_detail.getEventID());
				if (eDetail != null)
				{
					eDetails.add(getEvent(attendee.event_detail.getEventID()));
				}
			}
			return eDetails;
		}
	    catch (SQLException e) 
	    {
			Log.e("DatabaseWrapper", "Failed to getPhotosForUser", e);
			return new ArrayList<EventDetail>();
		}
	}
	
	public Collection<PhotoItem> getPhotosForUser(Integer userId)
	{
		try
		{
			QueryBuilder<PhotoItem, Integer> queryBuilder = helper.getPhotoDao().queryBuilder();
			queryBuilder.where().eq("user_id", userId);
			return helper.getPhotoDao().query(queryBuilder.prepare());
		}
	    catch (SQLException e) 
	    {
			Log.e("DatabaseWrapper", "Failed to getPhotosForUser", e);
			return new ArrayList<PhotoItem>();
		}
	}
	
	public UserInfo getUser(Integer userId)
	{
		UserInfo user = null;
		try 
		{
			user = helper.getUserDao().queryForId(userId);
		} 
		catch (SQLException e) 
		{
			Log.e("DatabaseWrapper", "Failed to getUser for userInfo", e);
		}
		finally
		{
			return user;
		}
	}
	
	public ArrayList<EventDetail> getMyEvents()
	{
		try 
		{
			Integer userId = SharePref.getIntPref(mContext.getApplicationContext(), SharePref.USER_ID);
			
			QueryBuilder <Attendee, Integer> queryBuilder = helper.getAttendeeDao().queryBuilder();
			queryBuilder.where().eq("user_id", userId); // Here is the problem
			Collection<Attendee> attendees = helper.getAttendeeDao().query(queryBuilder.prepare());
			
			Collection<EventDetail> eDetails = new ArrayList<EventDetail>();
			
			for (Attendee attendee : attendees)
			{
				EventDetail eDetail = getEvent(attendee.event_detail.getEventID());
				if (eDetail != null)
				{
					eDetails.add(eDetail);
				}
			}
			return new ArrayList<EventDetail>(eDetails);
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getAttendees for event", e);
			return null;
		}
	}
	
	public boolean isAttending(Integer eventId)
	{
		try 
		{
			QueryBuilder <Attendee, Integer> queryBuilder = helper.getAttendeeDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId); // Here is the problem
			Collection<Attendee> attendees = helper.getAttendeeDao().query(queryBuilder.prepare());
			
			Integer userId = SharePref.getIntPref(mContext.getApplicationContext(), SharePref.USER_ID);			
			
			for (Attendee attendee : attendees)
			{
				if (attendee.user_id == userId)
				{
					return true;
				}
			}
			return false;
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getAttendees for event", e);
			return false;
		}
	}
	
	public Collection<Attendee> getAttendees(Integer eventId)
	{
		try 
		{
			QueryBuilder <Attendee, Integer> queryBuilder = helper.getAttendeeDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId); // Here is the problem
			return helper.getAttendeeDao().query(queryBuilder.prepare());
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getAttendees for event", e);
			return new ArrayList<Attendee>();
		}
	}
	
	public void clearAttendees(Integer eventId)
	{
		try {
			DeleteBuilder<Attendee, Integer> deleteBuilder = helper.getAttendeeDao().deleteBuilder();
			deleteBuilder.where().eq("event_detail", eventId);
			deleteBuilder.delete();
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to clearAttendees for event", e);
		}
		
	}
	
	public void clearFriends()
	{
		try {
			DeleteBuilder<Friend, Integer> deleteBuilder = helper.getFriendsDao().deleteBuilder();
			deleteBuilder.delete();
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to clearAttendees for event", e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void createAttendee(Attendee attendee)
	{
		try {
			QueryBuilder <Attendee, Integer> queryBuilder = helper.getAttendeeDao().queryBuilder();
			Where<Attendee, Integer> where = queryBuilder.where();
			where.and(where.eq("user_id", attendee.user_id), where.eq("event_detail", attendee.event_detail.getEventID()));
			Collection<Attendee> attendees = helper.getAttendeeDao().query(queryBuilder.prepare());
			if (attendees.size() == 0)
			{
				helper.getAttendeeDao().create(attendee);
			}
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to createAttendee for event", e);
		}
	}
	
	public Collection<PhotoItem> getPhotos(Integer eventId)
	{
		try 
		{
			QueryBuilder <PhotoItem, Integer> queryBuilder = helper.getPhotoDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId); // Here is the problem
			return helper.getPhotoDao().query(queryBuilder.prepare());
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getPhotos for event", e);
			return new ArrayList<PhotoItem>();
		}
	}
	
	public void clearPhotos(Integer eventId)
	{
		try {
			DeleteBuilder<PhotoItem, Integer> deleteBuilder = helper.getPhotoDao().deleteBuilder();
			Where<PhotoItem, Integer> where = deleteBuilder.where();
			where.eq("event_detail", eventId);
			where.and();
			where.gt("id", 0);
			deleteBuilder.delete();
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to clearPhotos for event", e);
		}
		
	}
	
	public Collection<PhotoItem> getUploadingPhotos(Integer eventId)
	{
		try 
		{
			QueryBuilder <PhotoItem, Integer> queryBuilder = helper.getPhotoDao().queryBuilder();
			Where<PhotoItem, Integer> where = queryBuilder.where();
			where.eq("event_detail", eventId); // Here is the problem
			where.and();
			where.lt("id", 0);
			return helper.getPhotoDao().query(queryBuilder.prepare());
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getStories for event", e);
			return null;
		}
	}
	
	public void createPhoto(PhotoItem photo)
	{
		try {
			
			if (photo.id < 0)
			{
				int i = -1;
				while (getPhoto(i) != null)
				{
					i--;
				}
				
				photo.id = i;
			}
			helper.getPhotoDao().createOrUpdate(photo);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to createPhoto for event", e);
		}
	}
	
	public void deletePhoto(PhotoItem photo)
	{
		try {
			helper.getPhotoDao().delete(photo);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to deletePhoto for event", e);
		}
	}
	
	public Collection<StoryItem> getStories(Integer eventId)
	{
		try 
		{
			QueryBuilder <StoryItem, Integer> queryBuilder = helper.getStoryDao().queryBuilder();
			queryBuilder.where().eq("event_detail", eventId); // Here is the problem
			return helper.getStoryDao().query(queryBuilder.prepare());
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getStories for event", e);
			return null;
		}
	}
	
	public void clearStories(Integer eventId)
	{
		try {
			DeleteBuilder<StoryItem, Integer> deleteBuilder = helper.getStoryDao().deleteBuilder();
			deleteBuilder.where().eq("event_detail", eventId);
			deleteBuilder.delete();
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to clearStories for event", e);
		}
		
	}
	
	public void createStory(StoryItem story)
	{
		try {
			helper.getStoryDao().createOrUpdate(story);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to createAttendee for event", e);
		}
	}
	
	public void deleteStory(StoryItem story)
	{
		try {
			helper.getStoryDao().delete(story);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to deleteStory for event", e);
		}
	}
	
	public Comment getComment(Integer commentId)
	{
		try {
			return helper.getCommentDao().queryForId(commentId);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getCommment", e);
			return null;
		}
	}
	
	public Collection<Comment> getComments(Integer photoId)
	{
		try 
		{
			QueryBuilder <Comment, Integer> queryBuilder = helper.getCommentDao().queryBuilder();
			queryBuilder.where().eq("photo", photoId); // Here is the problem
			return helper.getCommentDao().query(queryBuilder.prepare());
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getComments for photo", e);
			return null;
		}
	}
	
	public void clearComments(Integer photoId)
	{
		try {
			DeleteBuilder<Comment, Integer> deleteBuilder = helper.getCommentDao().deleteBuilder();
			deleteBuilder.where().eq("photo", photoId);
			deleteBuilder.delete();
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to clearComments for photo", e);
		}
		
	}
	
	public void createComment(Comment comment)
	{
		try {
			helper.getCommentDao().createOrUpdate(comment);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to createComment for photo", e);
		}
	}
	
	public void deleteComment(Comment comment)
	{
		try {
			helper.getCommentDao().delete(comment);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to deleteComment for photo", e);
		}
	}
	
	public void deleteLike(Like like)
	{
		try {
			helper.getLikeDao().delete(like);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to deleteLike for photo", e);
		}
	}
	
	public Collection<Like> getLikes(Integer photoId)
	{
		try 
		{
			QueryBuilder <Like, Integer> queryBuilder = helper.getLikeDao().queryBuilder();
			queryBuilder.where().eq("photo", photoId); // Here is the problem
			return helper.getLikeDao().query(queryBuilder.prepare());
		} 
		catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to getLikes for photo", e);
			return null;
		}
	}
	
	public void clearLikes(Integer photoId)
	{
		try {
			DeleteBuilder<Like, Integer> deleteBuilder = helper.getLikeDao().deleteBuilder();
			deleteBuilder.where().eq("photo", photoId);
			deleteBuilder.delete();
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to clearLikes for photo", e);
		}
	}
	
	public void createLike(Like like)
	{
		try {
			helper.getLikeDao().createOrUpdate(like);
		} catch (SQLException e) {
			Log.e("DatabaseWrapper", "Failed to createLike for photo", e);
		}
	}
}
