package com.gmail.at.zhuikov.aleksandr.rssreader.parser;

import java.io.InputStream;
import java.util.List;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.test.InstrumentationTestCase;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;
import com.gmail.at.zhuikov.aleksandr.rssreader.test.R;

public class RSSFeedPullParserTest extends InstrumentationTestCase {

	private RSSFeedParser parser;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		parser = new RSS2FeedPullParser();
	}

	public void testParseDelfiFeed() throws NotFoundException, RSSFeedParserException {
		Resources resources = getInstrumentation().getContext().getResources();
		assertEquals(30, parser.parse(resources.openRawResource(R.raw.delfi_feed), 100).size());
	}

	public void testParseDelfiFeedWithLimit() throws NotFoundException,
			RSSFeedParserException {
		Resources resources = getInstrumentation().getContext().getResources();
		InputStream input = resources.openRawResource(R.raw.delfi_feed);
		assertEquals(25, parser.parse(input, 25).size());
	}

	public void testWithMultipleItems() throws NotFoundException,
			RSSFeedParserException {
		Resources resources = getInstrumentation().getContext().getResources();
		InputStream input = resources.openRawResource(R.raw.test_feed_multiple_items);
		assertEquals(2, parser.parse(input, 2).size());
	}

	public void testWithMultipleItemsLimited()
			throws android.content.res.Resources.NotFoundException,
			RSSFeedParserException {
		Resources resources = getInstrumentation().getContext().getResources();
		RSSFeedParser rssfeedparser = parser;
		java.io.InputStream inputstream = resources.openRawResource(2130968577);
		int i = rssfeedparser.parse(inputstream, 1).size();
		assertEquals(1, i);
	}

	public void testWithSingleItem() throws NotFoundException,
			RSSFeedParserException {

		Resources resources = getInstrumentation().getContext().getResources();
		List<FeedItem> list = parser.parse(resources.openRawResource(R.raw.test_feed_single_item), 2);
		assertEquals(1, list.size());
		assertEquals("testTitle", list.get(0).getTitle());
		assertEquals("testDescription", list.get(0).getDescription());
		assertEquals("testLink", list.get(0).getLink());
		assertEquals("16 Apr 2009 06:18:51 GMT", list.get(0).getDate().toGMTString());
	}
}
