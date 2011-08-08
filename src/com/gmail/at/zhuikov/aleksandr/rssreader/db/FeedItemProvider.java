package com.gmail.at.zhuikov.aleksandr.rssreader.db;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static android.text.TextUtils.isEmpty;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class FeedItemProvider extends ContentProvider {

	public static final String AUTHORITY = FeedItemProvider.class.getName();
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aleksz.feed_item";
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aleksz.feed_item";
	public static final Uri CONTENT_URI =  Uri.parse("content://" + AUTHORITY + "/feed_items");

	private static final int FEED_ITEMS_URI_CODE = 1;
	private static final int FEED_ITEM_ID_URI_CODE = 2;

	static final String TABLE_NAME = "feed_item";

	private static final UriMatcher sUriMatcher;
	private DatabaseHelper mOpenHelper;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "feed_items", FEED_ITEMS_URI_CODE);
		sUriMatcher.addURI(AUTHORITY, "feed_items/#", FEED_ITEM_ID_URI_CODE);
	}

	public static final class Columns implements BaseColumns {
		public static final String DATE = "date";
		public static final String DESCRIPTION = "description";
		public static final String LINK = "link";
		public static final String TITLE = "title";
	}

	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper databasehelper = new DatabaseHelper(context);
		mOpenHelper = databasehelper;
		return true;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case FEED_ITEMS_URI_CODE:
            count = db.delete(TABLE_NAME, where, whereArgs);
            break;

        case FEED_ITEM_ID_URI_CODE:
            count = db.delete(TABLE_NAME, Columns._ID+ "=" + parseId(uri)
                    + (!isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case FEED_ITEMS_URI_CODE:
			return CONTENT_TYPE;

		case FEED_ITEM_ID_URI_CODE:
			return CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(TABLE_NAME, null, values);

		if (rowId > 0) {
			Uri pairUri = withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(pairUri, null);
			return pairUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TABLE_NAME);

		switch (sUriMatcher.match(uri)) {
		case FEED_ITEMS_URI_CODE:
			break;
		case FEED_ITEM_ID_URI_CODE:
			qb.appendWhere(Columns._ID + "=" + parseId(uri));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		count = db.update(TABLE_NAME, values, Columns._ID
				+ "=" + parseId(uri)
				+ (!isEmpty(where) ? " AND (" + where + ')' : ""),
				whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	protected DatabaseHelper getDatabaseHelper() {
		return mOpenHelper;
	}
}
