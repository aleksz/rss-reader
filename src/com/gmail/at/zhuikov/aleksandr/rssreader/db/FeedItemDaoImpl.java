package com.gmail.at.zhuikov.aleksandr.rssreader.db;

import static android.content.ContentUris.parseId;
import static android.content.ContentUris.withAppendedId;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.CONTENT_URI;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.DATE;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.DESCRIPTION;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.LINK;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.TITLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns;

/**
 * All public methods are synchronized to solve concurent modification issue.
 */
public class FeedItemDaoImpl implements FeedItemDao {

	private static final String[] FEED_ITEM_PROJECTION = new String[] {
			Columns._ID,
			TITLE,
			LINK,
			DESCRIPTION,
			DATE };

	private final ContentResolver resolver;

	public FeedItemDaoImpl(ContentResolver contentresolver) {
		resolver = contentresolver;
	}

	@Override
	public synchronized FeedItem getLatest() {

		Cursor c = resolver.query(
				CONTENT_URI,
				FEED_ITEM_PROJECTION,
				null,
				null,
				DATE + " DESC");

		if (!c.moveToFirst()) {
			c.close();
			return null;
		}

		FeedItem result = constructFeedItem(c);
		c.close();
		return result;
	}

	@Override
	public synchronized List<FeedItem> loadAll() {

		List<FeedItem> result = new ArrayList<FeedItem>();

		Cursor c = resolver.query(
				CONTENT_URI,
				FEED_ITEM_PROJECTION,
				null,
				null,
				null);

		while (c.moveToNext()) {
			result.add(constructFeedItem(c));
		}

		c.close();

		return result;
	}

	@Override
	public synchronized void replaceAll(Collection<FeedItem> newItems) {
	    deleteAll();
	    saveAll(newItems);
	}

	private FeedItem constructFeedItem(Cursor c) {
		return new FeedItem(
				c.getLong(0),
				c.getString(1),
				c.getString(2),
				c.getString(3),
				new Date(c.getLong(4)));
	}

	private void deleteAll() {
		resolver.delete(CONTENT_URI, null, null);
	}

	private void save(FeedItem feeditem) {

		ContentValues values = new ContentValues();
		values.put(TITLE, feeditem.getTitle());
		values.put(LINK, feeditem.getLink());
		values.put(DESCRIPTION, feeditem.getDescription());
		values.put(DATE, Long.valueOf(feeditem.getDate().getTime()));// TODO:
																		// possible
																		// NPE

		if (feeditem.isNew()) {
			feeditem.setId(parseId(resolver.insert(CONTENT_URI, values)));
		} else {
			resolver.update(withAppendedId(CONTENT_URI, feeditem.getId()),
					values, null, null);
		}
	}

	private void saveAll(Collection<FeedItem> items) {
		for (FeedItem item : items) {
			save(item);
		}
	}
}
