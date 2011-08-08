package com.gmail.at.zhuikov.aleksandr.rssreader.parser;

public class RSSFeedParserFactory {

	public static RSSFeedParser getParserThatAllowsPartialParsing() {
		return new RSS2FeedPullParser();
	}
}