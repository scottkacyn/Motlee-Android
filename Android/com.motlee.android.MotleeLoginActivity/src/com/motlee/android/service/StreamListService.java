package com.motlee.android.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.motlee.android.database.DatabaseWrapper;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.EventServiceBuffer;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StreamListHandler;
import com.motlee.android.object.TempAttendee;
import com.motlee.android.object.UserInfo;
import com.motlee.android.object.event.UpdatedEventDetailEvent;
import com.motlee.android.object.event.UpdatedEventDetailListener;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class StreamListService extends IntentService implements UpdatedEventDetailListener {

	private final static String TAG = StreamListService.class.toString();
	
	public static final String NOTIFY_LIST_CHANGE = "com.motlee.android.service.StreamListHandler.NOTIFY_LIST_CHANGE";
	
	public final static String PULL_FROM_SERVER = "pullfromserver";
	public final static String NEW_PHOTO = "newphoto";
	public final static String STREAM_FILTER = "streamFilter";
	public final static String INITIAL_PULL = "initialPull";
	public final static String FORCE_RESET = "forcereset";
	public final static String DELETE_PHOTO = "deletePhoto";
	public final static String DELETE_STREAM = "deleteStream";
	public final static String CREATE_STREAM = "createStream";
	public final static String CREATE_PHOTO = "createPhoto";
	public final static String PHOTO_STATUS_CHANGE = "photoStatusChange";
	
	private String mStreamFilter = "";
	private boolean forceListReset = false;
	
	private DatabaseWrapper dbWrapper;
	
	public StreamListService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.d(TAG, intent.toString());
		
		EventServiceBuffer.getInstance(getApplicationContext());
		dbWrapper = new DatabaseWrapper(getApplicationContext());
		
		forceListReset = intent.getBooleanExtra(FORCE_RESET, false);
		
		if (intent.getBooleanExtra(INITIAL_PULL, false))
		{
			/*if (dbWrapper.getStreamCount() > 0)
			{
				updateStreamList(EventServiceBuffer.NO_EVENT_FILTER);
				notifyListChangeBroadcast(EventServiceBuffer.NO_EVENT_FILTER);
			}*/
			
			mStreamFilter = EventServiceBuffer.NO_EVENT_FILTER;
			
			EventServiceBuffer.setEventDetailListener(this);
			EventServiceBuffer.getEventsFromService();
		}
		else
		{
			PhotoItem deletePhoto = intent.getParcelableExtra(DELETE_PHOTO);
			PhotoItem createPhoto = intent.getParcelableExtra(CREATE_PHOTO);
			Integer createEventId = intent.getIntExtra(CREATE_STREAM, -1);
			Integer deleteEventId = intent.getIntExtra(DELETE_STREAM, -1);
			PhotoItem finishedPhoto = intent.getParcelableExtra(PHOTO_STATUS_CHANGE);
			
			if (deletePhoto != null)
			{
				deletePhoto(deletePhoto);
			}
			else if (createEventId > 0)
			{
				createStream(createEventId);
			}
			else if (createPhoto != null)
			{
				createPhoto(createPhoto);
			}
			else if (deleteEventId > 0)
			{
				deleteStream(deleteEventId);
			}
			else if (finishedPhoto != null)
			{
				photoFinished(finishedPhoto);
			}
			else if (intent.getBooleanExtra(PULL_FROM_SERVER, false))
			{
				mStreamFilter = intent.getStringExtra(STREAM_FILTER);
				if (mStreamFilter == null || mStreamFilter.equals(""))
				{
					mStreamFilter = EventServiceBuffer.NO_EVENT_FILTER;
				}
				
				EventServiceBuffer.setEventDetailListener(this);
				EventServiceBuffer.getEventsFromService();
			}
			else
			{
				if (intent.getBooleanExtra(NEW_PHOTO, false))
				{
					
				}
				else
				{
					mStreamFilter = intent.getStringExtra(STREAM_FILTER);
					if (mStreamFilter == null || mStreamFilter.equals(""))
					{
						mStreamFilter = EventServiceBuffer.NO_EVENT_FILTER;
						if (updateStreamList(EventServiceBuffer.MY_EVENTS))
						{
							notifyListChangeBroadcast(EventServiceBuffer.MY_EVENTS);
						}
					}
					if (updateStreamList(mStreamFilter))
					{
						notifyListChangeBroadcast(mStreamFilter);
					}
				}
			}
		}
	}
	
	private void photoFinished(PhotoItem finishedPhoto) {
		
		ArrayList<EventDetail> allStreams = StreamListHandler.getCurrentAllStreamList();
		ArrayList<EventDetail> myStreams = StreamListHandler.getCurrentMyStreamList();
		
		EventDetail eDetail = dbWrapper.getEvent(finishedPhoto.event_id);
		
		if (allStreams.contains(eDetail))
		{
			eDetail = allStreams.get(allStreams.indexOf(eDetail));
			eDetail.setPhotos(dbWrapper.getPhotos(eDetail.getEventID()));
			StreamListHandler.updateAllStreamList(allStreams);
		}
		
		if (myStreams.contains(eDetail))
		{
			eDetail = myStreams.get(myStreams.indexOf(eDetail));
			eDetail.setPhotos(dbWrapper.getPhotos(eDetail.getEventID()));
			StreamListHandler.updateMyStreamList(myStreams);
		}
		
		notifyListChangeBroadcast(EventServiceBuffer.NO_EVENT_FILTER);
	}

	private void deleteStream(Integer deleteEventId) 
	{
		ArrayList<EventDetail> allStreams = StreamListHandler.getCurrentAllStreamList();
		ArrayList<EventDetail> myStreams = StreamListHandler.getCurrentMyStreamList();
		
		EventDetail eDetail = dbWrapper.getEvent(deleteEventId);

		if (allStreams.contains(eDetail))
		{
			allStreams.remove(eDetail);
			StreamListHandler.updateAllStreamList(allStreams);
		}
		if (myStreams.contains(eDetail))
		{
			myStreams.remove(eDetail);
			StreamListHandler.updateMyStreamList(myStreams);
		}
		
		notifyListChangeBroadcast(EventServiceBuffer.NO_EVENT_FILTER);
	}

	private void createPhoto(PhotoItem createPhoto) 
	{
		ArrayList<EventDetail> allStreams = StreamListHandler.getCurrentAllStreamList();
		ArrayList<EventDetail> myStreams = StreamListHandler.getCurrentMyStreamList();
		
		EventDetail eDetail = dbWrapper.getEvent(createPhoto.event_id);

		if (allStreams.contains(eDetail))
		{
			Log.d(TAG, "Creating photo");
			eDetail = allStreams.get(allStreams.indexOf(eDetail));
			eDetail.getPhotos().add(0, createPhoto);
			allStreams.remove(eDetail);
			allStreams.add(0, eDetail);
			StreamListHandler.updateAllStreamList(allStreams);
		}
		if (myStreams.contains(eDetail))
		{
			Log.d(TAG, "Creating photo");
			eDetail = myStreams.get(myStreams.indexOf(eDetail));
			eDetail.getPhotos().add(0, createPhoto);
			myStreams.remove(eDetail);
			myStreams.add(0, eDetail);
			StreamListHandler.updateMyStreamList(myStreams);
		}
		notifyListChangeBroadcast(EventServiceBuffer.NO_EVENT_FILTER);
		
		notifyListChangeBroadcast(EventServiceBuffer.NO_EVENT_FILTER);
		
	}

	private void createStream(Integer eventId) 
	{
		ArrayList<EventDetail> allStreams = StreamListHandler.getCurrentAllStreamList();
		ArrayList<EventDetail> myStreams = StreamListHandler.getCurrentMyStreamList();
		
		EventDetail eDetail = dbWrapper.getEvent(eventId);

		allStreams.add(0, eDetail);
		StreamListHandler.updateAllStreamList(allStreams);
		
		myStreams.add(0, eDetail);
		StreamListHandler.updateMyStreamList(myStreams);
		
		notifyListChangeBroadcast(EventServiceBuffer.NO_EVENT_FILTER);
	}

	private void deletePhoto(PhotoItem photo) {
		
		ArrayList<EventDetail> allStreams = StreamListHandler.getCurrentAllStreamList();
		ArrayList<EventDetail> myStreams = StreamListHandler.getCurrentMyStreamList();
		
		EventDetail eDetail = dbWrapper.getEvent(photo.event_id);
		
		if (allStreams.contains(eDetail))
		{
			Log.d(TAG, "Removing photo");
			allStreams.get(allStreams.indexOf(eDetail)).getPhotos().remove(photo);
			StreamListHandler.updateAllStreamList(allStreams);
		}
		if (myStreams.contains(eDetail))
		{
			Log.d(TAG, "Removing photo");
			myStreams.get(myStreams.indexOf(eDetail)).getPhotos().remove(photo);
			StreamListHandler.updateMyStreamList(myStreams);
		}
		notifyListChangeBroadcast(EventServiceBuffer.NO_EVENT_FILTER);
	}

	public boolean updateStreamList(String streamFilter)
	{
		return updateStreamList(streamFilter, false);
	}
	
	public boolean updateStreamList(String streamFilter, boolean forceNotify)
	{
		ArrayList<EventDetail> eventsToShow = new ArrayList<EventDetail>();
		ArrayList<EventDetail> currentEvents = new ArrayList<EventDetail>();
		
		if (streamFilter.equals(EventServiceBuffer.NO_EVENT_FILTER))
		{
			eventsToShow = new ArrayList<EventDetail>(dbWrapper.getAllEvents());
			currentEvents = StreamListHandler.getCurrentAllStreamList();
		}
		else if (streamFilter.equals(EventServiceBuffer.MY_EVENTS))
		{
			eventsToShow = dbWrapper.getMyEvents();
			currentEvents = StreamListHandler.getCurrentMyStreamList();
		}
		
		ArrayList<EventDetail> eventsToDisplay = new ArrayList<EventDetail>();
		
		boolean notifyChange = forceNotify;
		
		for (EventDetail eDetail : eventsToShow)
		{
			UserInfo owner = dbWrapper.getUser(eDetail.getOwnerID());
			if (owner != null)
			{

				ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>(dbWrapper.getPhotos(eDetail.getEventID()));
				Integer attendeeCount = dbWrapper.getAttendeeCount(eDetail.getEventID()) + TempAttendee.getTempAttendees(eDetail.getEventID()).size();
				
				if (!notifyChange)
				{
					if (currentEvents.contains(eDetail))
					{
						EventDetail currentEDetail = currentEvents.get(currentEvents.indexOf(eDetail));
						if (currentEDetail.getPhotos().size() != photos.size()
								|| currentEDetail.getAttendeeCount() != attendeeCount
								|| currentEDetail.getLocationID() != eDetail.getLocationID() || !currentEDetail.updated_at.equals(eDetail.updated_at) || currentEDetail.getIsPrivate() != eDetail.getIsPrivate())
						{
							notifyChange = true;
						}
					}
					else
					{
						notifyChange = true;
					}
				}
				
				/*
				 * Initializes some event variables to improve scrolling
				 * Takes away db calls from EventListAdapter
				 */
				
				eDetail.setPhotos(photos);
				eDetail.setOwnerInfo(owner);
				eDetail.setAttendeeCount(attendeeCount);
				
				if (eDetail.getLocationID() != null)
				{
					eDetail.setLocationInfo(dbWrapper.getLocation(eDetail.getLocationID()));
				}

				if (eDetail.getStartTime().compareTo(new Date()) < 0)
				{
					eventsToDisplay.add(eDetail);
				}
			}
			else
			{
				dbWrapper.deleteEvent(eDetail);
			}
		}
		
		Collections.sort(eventsToDisplay);
		
		
		
		if (notifyChange)
		{
			if (streamFilter.equals(EventServiceBuffer.NO_EVENT_FILTER))
			{
				StreamListHandler.updateAllStreamList(eventsToDisplay);
			}
			else if (streamFilter.equals(EventServiceBuffer.MY_EVENTS))
			{
				StreamListHandler.updateMyStreamList(eventsToDisplay);
			}
			//notifyListChangeBroadcast(streamFilter);
		}
		
		return notifyChange;
	}

	public void myEventOccurred(UpdatedEventDetailEvent evt) 
	{	
		EventServiceBuffer.removeEventDetailListener(this);
		
		if (mStreamFilter.equals(EventServiceBuffer.NO_EVENT_FILTER))
		{
			if (updateStreamList(mStreamFilter, forceListReset))
			{
				notifyListChangeBroadcast(mStreamFilter);
			}
			
			updateStreamList(EventServiceBuffer.MY_EVENTS);
		}
		else if (mStreamFilter.equals(EventServiceBuffer.MY_EVENTS))
		{
			if (updateStreamList(mStreamFilter, forceListReset))
			{
				notifyListChangeBroadcast(mStreamFilter);
			}
			
			updateStreamList(EventServiceBuffer.NO_EVENT_FILTER);
		}
	}

	public void updatedEventOccurred(Integer eventId) 
	{
		// Nothing to do here!	
	}

    private void notifyListChangeBroadcast(String streamFilter)
    {
    	Log.d(TAG, "About to notify change in list broadcast, streamFilter: " + streamFilter);
    	
    	Intent broadcast = new Intent();
        broadcast.setAction(NOTIFY_LIST_CHANGE);
        broadcast.putExtra(STREAM_FILTER, streamFilter);
        broadcast.putExtra(FORCE_RESET, forceListReset);
        sendBroadcast(broadcast);
    }
	
}
