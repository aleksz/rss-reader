package com.gmail.at.zhuikov.aleksandr.rssreader;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gmail.at.zhuikov.aleksandr.rssreader.util.Preferences;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceScheduler;

public class OpenRSSUrlActivity extends Activity {

	private static final String TAG = OpenRSSUrlActivity.class.getSimpleName();

	private Preferences preferences;
	private RSSPollServiceScheduler scheduler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RSSReaderServiceLocator serviceLocator = (RSSReaderServiceLocator) getApplication();
		preferences = serviceLocator.getPreferences();
		scheduler = serviceLocator.getRssPollServiceScheduler();

		setContentView(R.layout.configured_by_intent);
		TextView text = (TextView) findViewById(R.id.configure_by_intent);
		text.setText(getConfirmationText(preferences.getRssFeedUrl()));
	}

	private CharSequence getConfirmationText(URL oldUrl) {
		String text;
		if (oldUrl == null) {
			text = getString(R.string.set_url_confirmation, getIntent().getDataString());
		} else {
			text = getString(R.string.change_url_confirmation, oldUrl, getIntent().getDataString());
		}
		return Html.fromHtml(text);
	}

	public void cancel(View view) {
		finish();
	}

	public void confirm(View view) {
		try {
			preferences.setRssFeedUrl(new URL(getIntent().getDataString()));
		} catch (MalformedURLException e) {
			Log.w(TAG, "Tried to set feed url to " + getIntent().getDataString());
			makeText(this, R.string.wrong_url, LENGTH_SHORT).show();
			return;
		}
		scheduler.schedule();
		finish();
		startActivity(new Intent(this, RSSFeedItemListActivity.class));
	}
}