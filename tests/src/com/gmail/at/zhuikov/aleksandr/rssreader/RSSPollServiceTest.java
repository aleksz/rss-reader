package com.gmail.at.zhuikov.aleksandr.rssreader;

import static com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService.FINISHED_ACTION;
import static com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService.NOT_CONFIGURED_ACTION;
import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.mock.MockApplication;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;
import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParser;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParserException;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.ConnectionOpener;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceScheduler;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.StatusBarNotificationSender;

class RSSPollServiceTest extends ServiceTestCase<RSSPollService> {

	private MockBroadcastReceiver broadcastReceiver;
	private MockConnectionOpener connectionOpener;
	private MockFeedItemDao feedItemDao;
	private MockNotificationSender notificationSender;
	private MockRSSFeedParser parser;
	private MockPreferences preferences;


	public RSSPollServiceTest() {
		super(RSSPollService.class);
	}

	@Override
	protected void setUp() throws Exception {
		parser = new MockRSSFeedParser();
		preferences = new MockPreferences();
		preferences.rssFeedUrl = new URL("http://engadget.com/rss.xml");
		feedItemDao = new MockFeedItemDao();
		connectionOpener = new MockConnectionOpener();
		broadcastReceiver = new MockBroadcastReceiver();
		notificationSender = new MockNotificationSender();
		setApplication(new RSSReaderMockApplication());
		super.setUp();
	}

	public void testWhenConnectionBreaks() throws InterruptedException {
		parser.throwException = true;
		connectionOpener.data = "testData";
		getContext().registerReceiver(broadcastReceiver, new IntentFilter(FINISHED_ACTION));
		startService(new Intent());
		sleep(500L);
		getContext().unregisterReceiver(broadcastReceiver);
		assertTrue(parser.called);
		assertNull(feedItemDao.replaceAllParams);
		assertNotNull(broadcastReceiver.intent);
		assertEquals("finished", broadcastReceiver.intent.getAction());
	}

	public void testWhenFeedDoesNotReturnAnything() throws IOException,
			InterruptedException {
		connectionOpener.data = "";
		parser.returnValue = Collections.emptyList();
		startService(new Intent());
		assertNull(feedItemDao.replaceAllParams);
	}

	public void testWhenLatestItemInFeedMatchesLatestInDatabase()
			throws IOException, InterruptedException {
		FeedItem item1 = new FeedItem("t", "l", "d", new Date(1L));
		FeedItem item2 = new FeedItem("t", "l", "d", new Date(2L));
		connectionOpener.data = "testData";
		parser.returnValue = asList(item1, item2);
		feedItemDao.latestItem = item2;
		startService(new Intent());
		assertNull(feedItemDao.replaceAllParams);
	}

	public void testWhenNewItemsAvailable() throws IOException,
			InterruptedException {

		connectionOpener.data = "testData";
		parser.returnValue = asList(new FeedItem("t", "l", "d", new Date()));
		getContext().registerReceiver(broadcastReceiver, new IntentFilter(FINISHED_ACTION));
		startService(new Intent());
		sleep(500L);
		getContext().unregisterReceiver(broadcastReceiver);
		assertTrue(connectionOpener.connectionOpened);
		InputStreamReader reader = new InputStreamReader(parser.input);
		assertEquals("testData", new BufferedReader(reader).readLine());
		assertNotNull(parser.input);
		assertTrue(parser.called);
		assertEquals(25, parser.maxItems);
		assertEquals(asList(new FeedItem("t", "l", "d", new Date())), feedItemDao.replaceAllParams);
		assertNotNull(broadcastReceiver.intent);
		assertEquals("finished", broadcastReceiver.intent.getAction());
		assertTrue(notificationSender.newItemsAvailableNotificationSent);
	}

	public void testWhenNewItemsAvailableButNotificationsDisabled()
			throws IOException, InterruptedException {

		connectionOpener.data = "testData";
		parser.returnValue = asList(new FeedItem("t", "l", "d", new Date()));
		Intent intent = new Intent();
		intent.putExtra("send_notifications", false);
		startService(intent);
		sleep(500L);
		assertFalse(notificationSender.newItemsAvailableNotificationSent);
	}

	public void testWhenNoFeedUrlConfigured() throws InterruptedException {
		preferences.rssFeedUrl = null;
		getContext().registerReceiver(broadcastReceiver, new IntentFilter(NOT_CONFIGURED_ACTION));
		startService(new Intent());
		sleep(500L);
		getContext().unregisterReceiver(broadcastReceiver);
		assertFalse(parser.called);
		assertNotNull(broadcastReceiver.intent);
		assertEquals(NOT_CONFIGURED_ACTION, broadcastReceiver.intent.getAction());
	}

	private final class MockBroadcastReceiver extends BroadcastReceiver {

		Intent intent;

		@Override
		public void onReceive(Context context, Intent intent) {
			this.intent = intent;
		}
	}

	private class MockConnectionOpener extends ConnectionOpener {

		boolean connectionOpened;
		String data;

		@Override
		public InputStream getInputStream(URL url) throws IOException {
			connectionOpened = true;
			return new ByteArrayInputStream(data.getBytes());
		}
	}

	private class MockFeedItemDao implements FeedItemDao {

		FeedItem latestItem;
		Collection<FeedItem> replaceAllParams;

		@Override
		public FeedItem getLatest() {
			return latestItem;
		}

		@Override
		public List<FeedItem> loadAll() {
			return null;
		}

		@Override
		public void replaceAll(Collection<FeedItem> collection) {
			replaceAllParams = collection;
		}
	}

	private class MockNotificationSender extends StatusBarNotificationSender {

		boolean newItemsAvailableNotificationSent;

		public MockNotificationSender() {
			super(getContext());
		}

		@Override
		public void onNewItemsAvailable() {
			newItemsAvailableNotificationSent = true;
		}
	}

	private class MockPreferences implements Preferences {

		URL rssFeedUrl;

		@Override
		public URL getRssFeedUrl() {
			return rssFeedUrl;
		}

		@Override
		public String getPreferencesFileName() {
			return null;
		}

		@Override
		public long getRefreshInterval() {
			return 0;
		}
	}

	private final class MockRSSFeedParser implements RSSFeedParser {

		boolean called;
		InputStream input;
		int maxItems;
		List<FeedItem> returnValue;
		boolean throwException;

		@Override
		public List<FeedItem> parse(InputStream inputstream, int maxItems)
				throws RSSFeedParserException {

			input = inputstream;
			this.maxItems = maxItems;
			called = true;
			if (throwException) {
				throw new RSSFeedParserException();
			}
			return returnValue;
		}
	}

	private class RSSReaderMockApplication extends MockApplication implements
			RSSReaderServiceLocator {

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
			return notificationSender;
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
			return null;
		}
	}
}
