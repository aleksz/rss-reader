package com.gmail.at.zhuikov.aleksandr.rssreader.parser;

import static java.util.Locale.ENGLISH;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;

/**
 * RSS2 feed parser implementation that uses {@link XmlPullParser}. This allows
 * us parsing only required number of items from feed and skip the rest.
 */
public class RSS2FeedPullParser extends AbstractRSS2FeedParser {

	private static final String TAG = RSS2FeedPullParser.class.getSimpleName();

	//TODO: this format might be wrong
	private static final SimpleDateFormat RFC_822_FORMAT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", ENGLISH);

	@Override
	public List<FeedItem> parse(InputStream input, int maxItems)
			throws RSSFeedParserException {

		List<FeedItem> result = new ArrayList<FeedItem>();
		XmlPullParser parser = setupParser(input);
		FeedItemBuilder feedItemBuilder = null;
		String tagName;

		try {

			int currentEventType = parser.getEventType();

			while (currentEventType != END_DOCUMENT && result.size() != maxItems) {
				tagName = parser.getName();

				switch (currentEventType) {
				case START_TAG:

					if (ITEM_TAG.equalsIgnoreCase(tagName)) {
						feedItemBuilder = new FeedItemBuilder();
					} else if (feedItemBuilder != null) {
						populateMessageProperty(parser.nextText(), feedItemBuilder, tagName);
					}

					break;

				case END_TAG:
					if (ITEM_TAG.equalsIgnoreCase(tagName)) {
						result.add(feedItemBuilder.getFeedItem());
						feedItemBuilder = null;
					}

					break;

				default:
					break;
				}

				currentEventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			throw new RSSFeedParserException("RSS input stream has wrong content", e);
		} catch (IOException e) {
			throw new RSSFeedParserException("Something wrong with input stream", e);
		}

		return result;
	}

	private Date parseDate(String dateString) {
		try {
			return RFC_822_FORMAT.parse(dateString);
		} catch (ParseException e) {
			Log.w(TAG, e);
			return null;
		}
	}

	private void populateMessageProperty(String value,
			FeedItemBuilder feedItemBuilder, String tagName) {

		if (LINK_TAG.equalsIgnoreCase(tagName)) {
			feedItemBuilder.setLink(value);

		} else if (DESCRIPTION_TAG.equalsIgnoreCase(tagName)) {
			feedItemBuilder.setDescription(value);

		} else if (PUB_DATE_TAG.equalsIgnoreCase(tagName)) {
			feedItemBuilder.setDate(parseDate(value));

		} else if (TITLE_TAG.equalsIgnoreCase(tagName)) {
			feedItemBuilder.setTitle(value);
		}
	}

	private XmlPullParser setupParser(InputStream input) {
		try {

			XmlPullParserFactory fatory = XmlPullParserFactory.newInstance();
			fatory.setNamespaceAware(true);
			XmlPullParser parser = fatory.newPullParser();
			parser.setInput(input, null);
			return parser;

		} catch (XmlPullParserException e) {
			// nothing we can do
			throw new RuntimeException(e);
		}
	}
}
