package com.gmail.at.zhuikov.aleksandr.rssreader.util;

import java.net.URL;

import android.content.SharedPreferences;

/**
 * Wraps interactions with {@link SharedPreferences} and provides simpler
 * interface.
 */
public interface Preferences {

	String REFRESH_INTERVAL_PREFERENCE_KEY = "refresh_interval";
	String RSS_URL_PREFERENCE_KEY = "rss_url";

	String getPreferencesFileName();

	long getRefreshInterval();

	URL getRssFeedUrl();

	void setRssFeedUrl(URL url);
}
