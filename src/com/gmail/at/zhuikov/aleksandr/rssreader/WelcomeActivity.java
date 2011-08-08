package com.gmail.at.zhuikov.aleksandr.rssreader;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class WelcomeActivity extends Activity {

	private FeedItemDao feedItemDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RSSReaderServiceLocator serviceLocator = (RSSReaderServiceLocator) getApplication();
		feedItemDao = serviceLocator.getFeedItemDao();
		setContentView(R.layout.welcome);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (feedItemDao.getLatest() != null) {
			startActivity(new Intent(this, RSSFeedItemListActivity.class));
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.welcome_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case R.id.settings_option:
        	startActivity(new Intent(this, SettingsActivity.class));
        	return true;
        default:
        	return super.onOptionsItemSelected(menuItem);
        }
    }
}
