package com.gmail.at.zhuikov.aleksandr.rssreader;

import static com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService.FINISHED_ACTION;
import static java.lang.Thread.sleep;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class RSSFeedItemListActivityIntegrationTest extends
		ActivityInstrumentationTestCase2<RSSFeedItemListActivity> {

	public RSSFeedItemListActivityIntegrationTest() {
		super(RSSFeedItemListActivity.class.getPackage().getName(),
				RSSFeedItemListActivity.class);
	}

	public void testPreconditions() {
		assertNotNull(getActivity());
	}

	public void testClickOnRefreshMenu() throws InterruptedException {
		getInstrumentation().invokeMenuActionSync(getActivity(), R.id.refresh_option, 0);
		assertTrue(getActivity().getRefreshProgressDialog().isShowing());
		getInstrumentation().getContext().sendBroadcast(new Intent(FINISHED_ACTION));
		sleep(500L);
		assertFalse(getActivity().getRefreshProgressDialog().isShowing());
	}

	public void testClickOnSettingsMenu() {
		ActivityMonitor monitor = getInstrumentation().addMonitor(
				SettingsActivity.class.getName(), null, true);
		getInstrumentation().invokeMenuActionSync(getActivity(), R.id.settings_option, 0);
		assertTrue(getInstrumentation().checkMonitorHit(monitor, 1));
	}
}