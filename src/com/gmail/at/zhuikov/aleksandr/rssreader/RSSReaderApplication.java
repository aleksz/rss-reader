package com.gmail.at.zhuikov.aleksandr.rssreader;

import android.app.Application;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;
import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDaoImpl;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParser;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParserFactory;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.ConnectionOpener;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.StatusBarNotificationSender;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.PreferencesImpl;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceScheduler;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceSchedulerImpl;

/**
 * Implements {@link RSSReaderServiceLocator} as there is only one instance of
 * this class.
 */
public class RSSReaderApplication extends Application implements
		RSSReaderServiceLocator {

	private ConnectionOpener connectionOpener;
	private FeedItemDaoImpl feedItemDao;
	private StatusBarNotificationSender statusBarNotificationSender;
	private RSSFeedParser parser;
	private Preferences preferences;
	private RSSPollServiceScheduler rssPollServiceScheduler;

	@Override
	public void onCreate() {
		super.onCreate();
		preferences = new PreferencesImpl(this);
		parser = RSSFeedParserFactory.getParserThatAllowsPartialParsing();
		feedItemDao = new FeedItemDaoImpl(getContentResolver());
		rssPollServiceScheduler = new RSSPollServiceSchedulerImpl(preferences, this);
		connectionOpener = new ConnectionOpener();
		statusBarNotificationSender = new StatusBarNotificationSender(this);
	}

	@Override
	public ConnectionOpener getConnectionOpener() {
		return connectionOpener;
	}

	@Override
	public FeedItemDao getFeedItemDao() {
		return feedItemDao;
	}

	@Override
	public StatusBarNotificationSender getNotificationSender() {
		return statusBarNotificationSender;
	}

	@Override
	public Preferences getPreferences() {
		return preferences;
	}

	@Override
	public RSSFeedParser getRssFeedParser() {
		return parser;
	}

	@Override
	public RSSPollServiceScheduler getRssPollServiceScheduler() {
		return rssPollServiceScheduler;
	}
}
