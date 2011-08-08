package com.gmail.at.zhuikov.aleksandr.rssreader.util;

import static android.content.Context.MODE_PRIVATE;
import static com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences.REFRESH_INTERVAL_PREFERENCE_KEY;
import static com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences.RSS_URL_PREFERENCE_KEY;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

public class PreferencesTest extends AndroidTestCase {

	private static final String PREFS_FILE_NAME = PreferencesTest.class.getName();

	private Preferences preferences;
	private SharedPreferences sharedPreferences;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sharedPreferences = getContext().getSharedPreferences(PREFS_FILE_NAME,
				MODE_PRIVATE);
		preferences = new PreferencesImpl(getContext(), PREFS_FILE_NAME);
		sharedPreferences.edit().clear().commit();
	}

	@Override
	protected void tearDown() throws Exception {
		sharedPreferences.edit().clear().commit();
		super.tearDown();
	}

	public void testPreconditions() {
		assertTrue(sharedPreferences.getAll().isEmpty());
	}

	public void testGetRefreshInterval() {
		sharedPreferences.edit().putString(REFRESH_INTERVAL_PREFERENCE_KEY, "100").commit();
		assertEquals(100L, preferences.getRefreshInterval());
	}

	public void testGetRssFeedUrl() {
		sharedPreferences.edit().putString(RSS_URL_PREFERENCE_KEY, "http://www.google.com").commit();
		assertEquals("http://www.google.com", preferences.getRssFeedUrl().toString());
	}

	public void testGetRssFeedUrlWhenSettingIsEmpty() {
		assertNull(preferences.getRssFeedUrl());
	}

	public void testGetRssFeedUrlWhenUrlIsWrong() {
		sharedPreferences.edit().putString(RSS_URL_PREFERENCE_KEY, "wrong").commit();
		try {
			preferences.getRssFeedUrl();
		} catch (RuntimeException runtimeexception) {
			return;
		}
		fail("Should throw exception");
	}
}