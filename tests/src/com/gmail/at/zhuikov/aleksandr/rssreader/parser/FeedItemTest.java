package com.gmail.at.zhuikov.aleksandr.rssreader.parser;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;

import java.util.Date;
import java.util.List;

import android.test.AndroidTestCase;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;

public class FeedItemTest extends AndroidTestCase {

	public void testByDateDescendingComparator() {

		FeedItem item1 = new FeedItem(null, null, null, new Date(1));
		FeedItem item2 = new FeedItem(null, null, null, null);
		FeedItem item3 = new FeedItem(null, null, null, new Date(2));

		List<FeedItem> list = asList(item1, item2, item3);

		sort(list, new FeedItem.ByDateDescending());

		assertEquals(item2, list.get(0));
		assertEquals(item3, list.get(1));
		assertEquals(item1, list.get(2));
	}

	public void testFormatDescriptionWithoutHtml() {
		assertEquals("abc", new FeedItem(null, null, "abc", null)
				.getDescriptionWithHtml(10).toString());
	}

	public void testFormatLinkInDescription() {
		assertEquals("abc", new FeedItem(null, null, "<a>abc</a>", null)
				.getDescriptionWithHtml(10).toString());
	}

	public void testFormatTooLongDescription() {
		assertEquals("abc...", new FeedItem(null, null, "ab<b>c</b>de", null)
				.getDescriptionWithHtml(3).toString());
	}

	public void testOrder() {
		FeedItem item1 = new FeedItem(null, null, null, new Date(1));
		FeedItem item2 = new FeedItem(null, null, null, null);
		FeedItem item3 = new FeedItem(null, null, null, new Date(2));

		List<FeedItem> list = asList(item1, item2, item3);

		sort(list);

		assertEquals(item2, list.get(0));
		assertEquals(item3, list.get(1));
		assertEquals(item1, list.get(2));
	}
}