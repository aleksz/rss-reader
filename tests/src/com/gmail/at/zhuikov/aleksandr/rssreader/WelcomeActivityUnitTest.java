package com.gmail.at.zhuikov.aleksandr.rssreader;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.mock.MockApplication;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;
import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParser;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.ConnectionOpener;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceScheduler;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.StatusBarNotificationSender;

public class WelcomeActivityUnitTest extends
		ActivityUnitTestCase<WelcomeActivity> {

	private MockFeedItemDao feedItemDao;

	public WelcomeActivityUnitTest() {
		super(WelcomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		feedItemDao = new MockFeedItemDao();
		setApplication(new WelcomeActivityMockApplication());
		super.setUp();
	}

	public void testShowsWelcomeMessageIfLatestItemIsNotAvailable() {
		startActivity(new Intent(), null, null);
		getInstrumentation().callActivityOnResume(getActivity());
		assertNull(getStartedActivityIntent());
		assertFalse(isFinishCalled());
	}

	public void testStartsFeedItemListActivityIfLatestItemExists() {
		feedItemDao.latestItem = new FeedItem("t", "l", "d", new Date());
		startActivity(new Intent(), null, null);
		getInstrumentation().callActivityOnResume(getActivity());
		assertEquals(RSSFeedItemListActivity.class.getName(),
				getStartedActivityIntent().getComponent().getClassName());
		assertTrue(isFinishCalled());
	}

	public void testClickOnSettingsMenu() {
		startActivity(new Intent(), null, null);
		getInstrumentation().invokeMenuActionSync(getActivity(), R.id.settings_option, 0);
		assertEquals(SettingsActivity.class.getName(),
				getStartedActivityIntent().getComponent().getClassName());
	}

	private class MockFeedItemDao implements FeedItemDao {

		FeedItem latestItem;

		@Override
		public FeedItem getLatest() {
			return latestItem;
		}

		@Override
		public List<FeedItem> loadAll() {
			return null;
		}

		@Override
		public void replaceAll(Collection<FeedItem> newItems) {
		}

	}

	private class WelcomeActivityMockApplication extends MockApplication
			implements RSSReaderServiceLocator {

		@Override
		public ConnectionOpener getConnectionOpener() {
			return null;
		}

		@Override
		public FeedItemDao getFeedItemDao() {
			return feedItemDao;
		}

		@Override
		public StatusBarNotificationSender getNotificationSender() {
			return null;
		}

		@Override
		public Preferences getPreferences() {
			return null;
		}

		@Override
		public RSSFeedParser getRssFeedParser() {
			return null;
		}

		@Override
		public RSSPollServiceScheduler getRssPollServiceScheduler() {
			return null;
		}

	}
}
