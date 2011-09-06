package com.gmail.at.zhuikov.aleksandr.rssreader;

import java.net.URL;

import android.content.Intent;
import android.content.res.Resources;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.test.ActivityUnitTestCase;
import android.test.mock.MockApplication;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;
import com.gmail.at.zhuikov.aleksandr.rssreader.parser.RSSFeedParser;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.ConnectionOpener;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceScheduler;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.StatusBarNotificationSender;

public class SettingsActivityUnitTest extends ActivityUnitTestCase<SettingsActivity> {

	private MockPreferences preferences;
	private Resources resources;
	private MockRssPollServiceScheduler scheduler;

	public SettingsActivityUnitTest() {
		super(SettingsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		preferences = new MockPreferences();
		scheduler = new MockRssPollServiceScheduler();
		setApplication(new MockSettingsActivityApplication());
		startActivity(new Intent(), null, null);
		resources = getActivity().getResources();
	}

	public void testPreconditions() {
		assertNotNull(getActivity());
	}

	public void testChangeUrlPreference() {
		assertTrue(getActivity().getRssUrlPreference()
				.getOnPreferenceChangeListener()
				.onPreferenceChange(null, "http://google.com"));
	}

	public void testChangeUrlPreferenceToWrongValue() {
		assertFalse(getActivity().getRssUrlPreference()
				.getOnPreferenceChangeListener()
				.onPreferenceChange(null, "wrong"));
	}

	public void testRefreshIntervalSummaryUpdatedWhenPreferenceValueChanges() {
		ListPreference pref = getActivity().getRefreshIntervalPreference();
		pref.setSummary("old");
		pref.setValue(resources.getStringArray(R.array.refresh_interval_list_preference_values)[0]);
		getActivity().onSharedPreferenceChanged(null, null);
		assertEquals(resources.getStringArray(R.array.refresh_interval_list_preference)[0], pref.getSummary());
	}

	public void testRssUrlSummaryNotUpdatedToEmptyWhenPreferenceValueChanges() {
		EditTextPreference pref = getActivity().getRssUrlPreference();
		pref.setSummary("old");
		pref.setText("");
		getActivity().onSharedPreferenceChanged(null, null);
		assertEquals("old", pref.getSummary());
	}

	public void testRssUrlSummaryUpdatedWhenPreferenceValueChanges() {
		EditTextPreference pref = getActivity().getRssUrlPreference();
		pref.setSummary("old");
		pref.setText("changed");
		getActivity().onSharedPreferenceChanged(null, null);
		assertEquals("changed", pref.getSummary());
	}

	public void testSchedulesRssPollServiceOnSettingsChange() {
		getActivity().onSharedPreferenceChanged(null, null);
		assertTrue(scheduler.scheduleCalled);
	}

	private class MockPreferences implements Preferences {

		@Override
		public String getPreferencesFileName() {
			return null;
		}

		@Override
		public long getRefreshInterval() {
			return 0L;
		}

		@Override
		public URL getRssFeedUrl() {
			return null;
		}

		@Override
		public void setRssFeedUrl(URL url) {
		}
	}

	private class MockRssPollServiceScheduler implements
			RSSPollServiceScheduler {

		boolean scheduleCalled;

		@Override
		public void cancel() {
		}

		@Override
		public void runOnce() {
		}

		@Override
		public void schedule() {
			scheduleCalled = true;
		}
	}

	private class MockSettingsActivityApplication extends MockApplication
			implements RSSReaderServiceLocator {

		@Override
		public ConnectionOpener getConnectionOpener() {
			return null;
		}

		@Override
		public FeedItemDao getFeedItemDao() {
			return null;
		}

		@Override
		public StatusBarNotificationSender getNotificationSender() {
			return null;
		}

		@Override
		public Preferences getPreferences() {
			return preferences;
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
}
