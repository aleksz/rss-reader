package com.gmail.at.zhuikov.aleksandr.rssreader;

import static java.util.Collections.sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;
import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParser;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParserException;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.ConnectionOpener;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.StatusBarNotificationSender;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences;

/**
 * This service runs on background and polls RSS feed for new items.
 *
 * Service extends from {@link IntentService}, which starts incoming requests in
 * separate {@link Thread} and processes only one intent at a time.
 *
 * Service informs other consumers that it has finished processing intent with a
 * broadcast. In case if service call failed due to missing configuration,
 * broadcast intent will have {@link #NOT_CONFIGURED_ACTION}. Otherwise,
 * {@link #FINISHED_ACTION}
 *
 * Service informs user about new available items with status bar notification.
 * This functionality can be turned off by providing
 * {@link #SEND_NOTIFICATIONS_EXTRA} in starting intent.
 */
public class RSSPollService extends IntentService {

	private static final String TAG = RSSPollService.class.getSimpleName();

	public static final String FINISHED_ACTION = "finished";
	public static final String NOT_CONFIGURED_ACTION = "not_configured";

	private static final int MAX_ITEMS_IN_FEED = 25;

	public static final String SEND_NOTIFICATIONS_EXTRA = "send_notifications";

	private ConnectionOpener connectionOpener;
	private FeedItemDao feedItemDao;
	private StatusBarNotificationSender statusBarNotificationSender;
	private RSSFeedParser parser;
	private Preferences preferences;

	public RSSPollService() {
		super("RSSPollServiceThread");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		RSSReaderServiceLocator serviceLocator = (RSSReaderServiceLocator) getApplication();
		statusBarNotificationSender = serviceLocator.getNotificationSender();
		parser = serviceLocator.getRssFeedParser();
		feedItemDao = serviceLocator.getFeedItemDao();
		preferences = serviceLocator.getPreferences();
		connectionOpener = serviceLocator.getConnectionOpener();
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (preferences.getRssFeedUrl() == null) {
			Log.i(TAG, "No feed url configured, will get back next time");
			sendBroadcast(new Intent(NOT_CONFIGURED_ACTION));
			return;
		}

		Log.i(TAG, "Polling feed " + preferences.getRssFeedUrl());

		List<FeedItem> result = new ArrayList<FeedItem>();

		try {
			InputStream input = connectionOpener.getInputStream(preferences
					.getRssFeedUrl());
			result = parser.parse(input, MAX_ITEMS_IN_FEED);
			Log.d(TAG, "Retrieved following items " + result);

		} catch (RSSFeedParserException e) {
			Log.w(TAG, "Could not parse feed, will get back next time", e);
		} catch (IOException e) {
			Log.w(TAG, "Could not poll feed, will get back next time", e);
		}

		if (newItemsAvailable(result)) {
			onNewItemsAvailable(result, intent);
		}

		sendBroadcast(new Intent(FINISHED_ACTION));
	}

	private void onNewItemsAvailable(List<FeedItem> result,
			Intent startingIntent) {

		feedItemDao.replaceAll(result);

		if (startingIntent.getBooleanExtra(SEND_NOTIFICATIONS_EXTRA, true)) {
			statusBarNotificationSender.onNewItemsAvailable();
		}
	}

	private boolean newItemsAvailable(List<FeedItem> result) {

		if (result.isEmpty()) {
			return false;
		}

		FeedItem latestItem = feedItemDao.getLatest();

		sort(result, new FeedItem.ByDateDescending());

		if (latestItem == null || !latestItem.equals(result.get(0))) {
			return true;
		}

		return false;
	}
}
