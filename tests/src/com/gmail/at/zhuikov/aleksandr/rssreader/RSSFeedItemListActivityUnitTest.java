package com.gmail.at.zhuikov.aleksandr.rssreader;

import static android.content.Intent.ACTION_VIEW;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

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

public class RSSFeedItemListActivityUnitTest extends
		ActivityUnitTestCase<RSSFeedItemListActivity> {

	private MockFeedItemDao feedItemDao;
	private MockRSSPollServiceScheduler scheduler;

	public RSSFeedItemListActivityUnitTest() {
		super(RSSFeedItemListActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scheduler = new MockRSSPollServiceScheduler();
		feedItemDao = new MockFeedItemDao();
		setApplication(new MockApplicationForTest());
	}

	public void testClickOnItemWithLink() {
		startActivity(new Intent(), null, null);
		feedItemDao.loadAllResult = asList(new FeedItem(null, "link", null, null));
		getInstrumentation().callActivityOnResume(getActivity());
		getActivity().onListItemClick(null, null, 0, 123L);
		assertNotNull(getStartedActivityIntent());
		assertEquals(ACTION_VIEW, getStartedActivityIntent().getAction());
		assertEquals("link", getStartedActivityIntent().getData().toString());
	}

	public void testClickOnItemWithoutLink() {
		startActivity(new Intent(), null, null);
		feedItemDao.loadAllResult = asList(new FeedItem(null, null, null, null));
		getInstrumentation().callActivityOnResume(getActivity());
		getActivity().onListItemClick(null, null, 0, 123L);
		assertNull(getStartedActivityIntent());
	}

	public void testClickOnSettingsMenu() {
		startActivity(new Intent(), null, null);
		getInstrumentation().invokeMenuActionSync(getActivity(), R.id.settings_option, 0);
		assertNotNull(getStartedActivityIntent());
		assertEquals(SettingsActivity.class.getName(),
				getStartedActivityIntent().getComponent().getClassName());
	}

	private class MockApplicationForTest extends MockApplication implements
			RSSReaderServiceLocator {

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
			return scheduler;
		}
	}

	private class MockFeedItemDao implements FeedItemDao {

		List<FeedItem> loadAllResult;

		@Override
		public FeedItem getLatest() {
			return null;
		}

		@Override
		public List<FeedItem> loadAll() {
			return loadAllResult;
		}

		@Override
		public void replaceAll(Collection<FeedItem> collection) {
		}
	}

	private class MockRSSPollServiceScheduler implements RSSPollServiceScheduler {

		@Override
		public void cancel() {
		}

		@Override
		public void runOnce() {
		}

		@Override
		public void schedule() {
		}
	}
}
