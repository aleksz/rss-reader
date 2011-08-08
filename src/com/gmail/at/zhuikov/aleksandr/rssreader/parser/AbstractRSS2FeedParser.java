package com.gmail.at.zhuikov.aleksandr.rssreader.parser;

/**
 * Abstract parent for all RSS2 feed parsers
 */
public abstract class AbstractRSS2FeedParser implements RSSFeedParser {

	protected static final String DESCRIPTION_TAG = "description";
	protected static final String ITEM_TAG = "item";
	protected static final String LINK_TAG = "link";
	protected static final String PUB_DATE_TAG = "pubDate";
	protected static final String TITLE_TAG = "title";
}
