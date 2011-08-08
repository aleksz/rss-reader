package com.gmail.at.zhuikov.aleksandr.rssreader.db;

import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.AUTHORITY;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.CONTENT_URI;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.TABLE_NAME;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.DATE;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.DESCRIPTION;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.LINK;
import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns.TITLE;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ProviderTestCase2;

public class FeedItemProviderTest extends ProviderTestCase2<FeedItemProvider> {

	private SQLiteDatabase db;

	public FeedItemProviderTest() {
		super(FeedItemProvider.class, AUTHORITY);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		db = ((FeedItemProvider) getProvider()).getDatabaseHelper()
				.getWritableDatabase();
	}

	public void testDeleteAll() {

		createTestItem("title", "link", "description", new Date());
		createTestItem("title2", "link2", "description2", new Date());

		int i = db.query(TABLE_NAME, null, null, null, null, null, null).getCount();
		assertEquals(2, i);

		getMockContentResolver().delete(CONTENT_URI, null, null);

		i = db.query(TABLE_NAME, null, null, null, null, null, null).getCount();
		assertEquals(0, i);
	}

	public void testQueryAllFeedItems() {
		createTestItem("title", "link", "description", new Date(1));
		createTestItem("title2", "link2", "description2", new Date(2));

		Cursor c = getMockContentResolver().query(CONTENT_URI, null, null, null, null);

		assertTrue(c.moveToFirst());
		assertEquals("title", c.getString(1));
		assertEquals("link", c.getString(2));
		assertEquals("description", c.getString(3));
		assertEquals(new Date(1).getTime(), c.getLong(4));

		assertTrue(c.moveToNext());
		assertEquals("title2", c.getString(1));
		assertEquals("link2", c.getString(2));
		assertEquals("description2", c.getString(3));
		assertEquals(new Date(2).getTime(), c.getLong(4));
		assertFalse(c.moveToNext());
	}

	private void createTestItem(String title, String link, String description, Date date) {
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(TITLE, title);
		contentvalues.put(LINK, link);
		contentvalues.put(DESCRIPTION, description);
		contentvalues.put(DATE, Long.valueOf(date.getTime()));
		db.insert(TABLE_NAME, null, contentvalues);
	}
}