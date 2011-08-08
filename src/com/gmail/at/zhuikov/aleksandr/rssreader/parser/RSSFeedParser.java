package com.gmail.at.zhuikov.aleksandr.rssreader.parser;

import java.io.InputStream;
import java.util.List;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;

public interface RSSFeedParser {

	/**
	 * @param input RSS stream
	 * @param maxItems maximum number of items to parse
	 * @return parsed RSS items
	 * @throws RSSFeedParserException if parsing fails
	 */
	List<FeedItem> parse(InputStream input, int maxItems)
			throws RSSFeedParserException;
}