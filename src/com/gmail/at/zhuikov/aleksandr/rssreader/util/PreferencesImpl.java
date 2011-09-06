package com.gmail.at.zhuikov.aleksandr.rssreader.util;

import static android.content.Context.MODE_PRIVATE;
import static android.preference.PreferenceManager.setDefaultValues;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.gmail.at.zhuikov.aleksandr.rssreader.R;

public class PreferencesImpl implements Preferences {

	private static String DEFAULT_PREFERENCES_FILE_NAME = "RSSReaderPreferences";
	private final String fileName;
	private SharedPreferences sharedPreferences;

	public PreferencesImpl(Context context) {
		this(context, DEFAULT_PREFERENCES_FILE_NAME);
	}

	public PreferencesImpl(Context context, String fileName) {
		this.fileName = fileName;
		setDefaultValues(context, fileName, MODE_PRIVATE, R.xml.settings, false);
		sharedPreferences = context.getSharedPreferences(fileName, MODE_PRIVATE);
	}

	@Override
	public String getPreferencesFileName() {
		return fileName;
	}

	@Override
	public long getRefreshInterval() {
		return new Long(sharedPreferences.getString(
				REFRESH_INTERVAL_PREFERENCE_KEY, null));
	}

	@Override
	public URL getRssFeedUrl() {
		String rssUrl = sharedPreferences.getString(RSS_URL_PREFERENCE_KEY,	null);

		if (TextUtils.isEmpty(rssUrl)) {
			return null;
		} else {
			try {
				return new URL(rssUrl);
			} catch (MalformedURLException e) {
				// Should not happen, as url is validated
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setRssFeedUrl(URL url) {
		sharedPreferences.edit().putString(RSS_URL_PREFERENCE_KEY, url.toString()).commit();
	}
}
