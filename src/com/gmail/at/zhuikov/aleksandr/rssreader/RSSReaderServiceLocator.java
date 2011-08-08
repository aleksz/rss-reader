package com.gmail.at.zhuikov.aleksandr.rssreader;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParser;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.ConnectionOpener;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.StatusBarNotificationSender;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceScheduler;

/**
 * Service locator patters. Provides centralized place for keeping all
 * application components.
 */
public interface RSSReaderServiceLocator {

	ConnectionOpener getConnectionOpener();

	FeedItemDao getFeedItemDao();

	StatusBarNotificationSender getNotificationSender();

	Preferences getPreferences();

	RSSFeedParser getRssFeedParser();

	RSSPollServiceScheduler getRssPollServiceScheduler();
}
