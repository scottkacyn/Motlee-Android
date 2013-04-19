package com.motlee.android.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.motlee.android.object.Attendee;
import com.motlee.android.object.Comment;
import com.motlee.android.object.EventDetail;
import com.motlee.android.object.Friend;
import com.motlee.android.object.Like;
import com.motlee.android.object.LocationInfo;
import com.motlee.android.object.PhotoItem;
import com.motlee.android.object.StoryItem;
import com.motlee.android.object.UserInfo;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "motlee.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 20;

	// the DAO object we use to access the EventDetail table
	private Dao<EventDetail, Integer> eventDao = null;
	private RuntimeExceptionDao<EventDetail, Integer> eventRuntimeDao = null;

	// the DAO object we use to access the EventDetail table
	private Dao<UserInfo, Integer> userDao = null;
	private RuntimeExceptionDao<UserInfo, Integer> userRuntimeDao = null;
	
	private Dao<Friend, Integer> friendDao = null;
	private RuntimeExceptionDao<Friend, Integer> friendRuntimeDao = null;
	
	private Dao<Attendee, Integer> attendeeDao = null;
	private RuntimeExceptionDao<Attendee, Integer> attendeeRuntimeDao = null;
	
	private Dao<PhotoItem, Integer> photoDao = null;
	private RuntimeExceptionDao<PhotoItem, Integer> photoRuntimeDao = null;
	
	private Dao<StoryItem, Integer> storyDao = null;
	private RuntimeExceptionDao<StoryItem, Integer> storyRuntimeDao = null;
	
	private Dao<Comment, Integer> commentDao = null;
	private RuntimeExceptionDao<Comment, Integer> commentRuntimeDao = null;
	
	private Dao<Like, Integer> likeDao = null;
	private RuntimeExceptionDao<Like, Integer> likeRuntimeDao = null;
	
	private Dao<LocationInfo, Integer> locationDao = null;
	private RuntimeExceptionDao<LocationInfo, Integer> locationRuntimeDao = null;
	
	private static DatabaseHelper instance;
	
	public static DatabaseHelper getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new DatabaseHelper(context);
		}
		return instance;
	}
	
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
	{
		
		try 
		{
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			db.setLockingEnabled(false);
			TableUtils.createTable(connectionSource, EventDetail.class);TableUtils.dropTable(connectionSource, Like.class, true);
			TableUtils.createTable(connectionSource, UserInfo.class);
			TableUtils.createTable(connectionSource, Friend.class);
			TableUtils.createTable(connectionSource, Attendee.class);
			TableUtils.createTable(connectionSource, PhotoItem.class);
			TableUtils.createTable(connectionSource, StoryItem.class);
			TableUtils.createTable(connectionSource, Comment.class);
			TableUtils.createTable(connectionSource, Like.class);
			TableUtils.createTable(connectionSource, LocationInfo.class);
		} 
		catch (SQLException e) 
		{
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
	{
		try 
		{
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, EventDetail.class, true);
			TableUtils.dropTable(connectionSource, UserInfo.class, true);
			TableUtils.dropTable(connectionSource, Friend.class, true);
			TableUtils.dropTable(connectionSource, Attendee.class, true);
			TableUtils.dropTable(connectionSource, PhotoItem.class, true);
			TableUtils.dropTable(connectionSource, StoryItem.class, true);
			TableUtils.dropTable(connectionSource, Comment.class, true);
			TableUtils.dropTable(connectionSource, Like.class, true);
			TableUtils.dropTable(connectionSource, LocationInfo.class, true);

			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} 
		catch (SQLException e) 
		{
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<EventDetail, Integer> getEventDao() throws SQLException {
		if (eventDao == null) {
			eventDao = getDao(EventDetail.class);
		}
		return eventDao;
	}

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Attendee, Integer> getAttendeeDao() throws SQLException {
		if (attendeeDao == null) {
			attendeeDao = getDao(Attendee.class);
		}
		return attendeeDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<PhotoItem, Integer> getPhotoDao() throws SQLException {
		if (photoDao == null) {
			photoDao = getDao(PhotoItem.class);
		}
		return photoDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<StoryItem, Integer> getStoryDao() throws SQLException {
		if (storyDao == null) {
			storyDao = getDao(StoryItem.class);
		}
		return storyDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Comment, Integer> getCommentDao() throws SQLException {
		if (commentDao == null) {
			commentDao = getDao(Comment.class);
		}
		return commentDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Like, Integer> getLikeDao() throws SQLException {
		if (likeDao == null) {
			likeDao = getDao(Like.class);
		}
		return likeDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<UserInfo, Integer> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(UserInfo.class);
		}
		return userDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Friend, Integer> getFriendsDao() throws SQLException {
		if (friendDao == null) {
			friendDao = getDao(Friend.class);
		}
		return friendDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<LocationInfo, Integer> getLocationDao() throws SQLException {
		if (locationDao == null) {
			locationDao = getDao(LocationInfo.class);
		}
		return locationDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<LocationInfo, Integer> getLocationDataDao() {
		if (locationRuntimeDao == null) {
			locationRuntimeDao = getRuntimeExceptionDao(LocationInfo.class);
		}
		return locationRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Friend, Integer> getFriendsDataDao() {
		if (friendRuntimeDao == null) {
			friendRuntimeDao = getRuntimeExceptionDao(Friend.class);
		}
		return friendRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<PhotoItem, Integer> getPhotosDataDao() {
		if (photoRuntimeDao == null) {
			photoRuntimeDao = getRuntimeExceptionDao(PhotoItem.class);
		}
		return photoRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Comment, Integer> getCommentDataDao() {
		if (commentRuntimeDao == null) {
			commentRuntimeDao = getRuntimeExceptionDao(Comment.class);
		}
		return commentRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Like, Integer> getLikeDataDao() {
		if (likeRuntimeDao == null) {
			likeRuntimeDao = getRuntimeExceptionDao(Like.class);
		}
		return likeRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<StoryItem, Integer> getStoryDataDao() {
		if (storyRuntimeDao == null) {
			storyRuntimeDao = getRuntimeExceptionDao(StoryItem.class);
		}
		return storyRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Attendee, Integer> getAttendeeDataDao() {
		if (attendeeRuntimeDao == null) {
			attendeeRuntimeDao = getRuntimeExceptionDao(Attendee.class);
		}
		return attendeeRuntimeDao;
	}
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<EventDetail, Integer> getEventDataDao() {
		if (eventRuntimeDao == null) {
			eventRuntimeDao = getRuntimeExceptionDao(EventDetail.class);
		}
		return eventRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<UserInfo, Integer> getUserDataDao() {
		if (userRuntimeDao == null) {
			userRuntimeDao = getRuntimeExceptionDao(UserInfo.class);
		}
		return userRuntimeDao;
	}
	
	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		eventRuntimeDao = null;
		userRuntimeDao = null;
		this.attendeeRuntimeDao = null;
		this.commentRuntimeDao = null;
		this.friendRuntimeDao = null;
		this.likeRuntimeDao = null;
		this.photoRuntimeDao = null;
		this.storyRuntimeDao = null;
	}
}
