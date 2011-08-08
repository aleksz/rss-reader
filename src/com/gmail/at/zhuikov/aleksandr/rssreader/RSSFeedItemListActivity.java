package com.gmail.at.zhuikov.aleksandr.rssreader;

import static android.content.Intent.ACTION_VIEW;
import static android.text.TextUtils.isEmpty;
import static android.text.format.DateFormat.getDateFormat;
import static android.text.format.DateFormat.getTimeFormat;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService.FINISHED_ACTION;
import static com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService.NOT_CONFIGURED_ACTION;

import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;
import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemDao;
import com.gmail.at.zhuikov.aleksandr.rssreader.util.RSSPollServiceScheduler;

/**
 * Displays list of available RSS feed items
 */
public class RSSFeedItemListActivity extends ListActivity {

	private static final String TAG = RSSFeedItemListActivity.class.getSimpleName();

	private static final int MAX_DESCRIPTION_CHARS = 100;

	private static final int REFRESHING_DIALOG = 0;

	private static final String REFRESH_IN_PROGRESS_STATE = "refresh_in_progress";

	private FeedItemDao feedItemDao;
	private RSSPollServiceScheduler rssPollServiceScheduler;

	private NotConfiguredBroadcastReceiver notConfiguredReceiver;
	private RSSPollServiceBroadcastReceiver rssPollServiceBroadcastReceiver;

	private ProgressDialog refreshProgressDialog;
	private FeedItemListAdapter listAdapter;
	private boolean receiversRegistered;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);

		RSSReaderServiceLocator serviceLocator = (RSSReaderServiceLocator) getApplication();
		feedItemDao = serviceLocator.getFeedItemDao();
		rssPollServiceScheduler = serviceLocator.getRssPollServiceScheduler();
		rssPollServiceBroadcastReceiver = new RSSPollServiceBroadcastReceiver();
		notConfiguredReceiver = new NotConfiguredBroadcastReceiver();

		if (savedState != null && savedState.getBoolean(REFRESH_IN_PROGRESS_STATE)) {
			registerAllReceivers();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		if (id == REFRESHING_DIALOG) {

			refreshProgressDialog = new ProgressDialog(this);
			refreshProgressDialog.setMessage(getText(R.string.refreshing));
			refreshProgressDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialoginterface) {
					unregisterAllBroadcastReceivers();
				}

			});

			return refreshProgressDialog;
		}

		return super.onCreateDialog(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	protected void onResume() {
		rssPollServiceScheduler.cancel();
		listAdapter = new FeedItemListAdapter(this, feedItemDao.loadAll());
		setListAdapter(listAdapter);
		super.onResume();
	}

	@Override
	protected void onPause() {
		rssPollServiceScheduler.schedule();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterAllBroadcastReceivers();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		if (receiversRegistered) {
			savedState.putBoolean(REFRESH_IN_PROGRESS_STATE, true);
		}
	}

	private void registerAllReceivers() {
		registerReceiver(notConfiguredReceiver, new IntentFilter(NOT_CONFIGURED_ACTION));
		registerReceiver(rssPollServiceBroadcastReceiver, new IntentFilter(FINISHED_ACTION));
		receiversRegistered = true;
	}

	private void unregisterAllBroadcastReceivers() {
		if (receiversRegistered) {
			unregisterReceiver(notConfiguredReceiver);
			unregisterReceiver(rssPollServiceBroadcastReceiver);
		}
		receiversRegistered = false;
	}

	@Override
	protected void onListItemClick(ListView listview, View view, int position, long id) {
		super.onListItemClick(listview, view, position, id);

		FeedItem item = (FeedItem) listAdapter.getItem(position);

		if (isEmpty(item.getLink())) {
			makeText(this, R.string.no_url_in_item, LENGTH_SHORT).show();
			return;
		}

		startActivity(new Intent(ACTION_VIEW, Uri.parse(item.getLink())));
	}

	private void startSettingsActivity() {
		startActivity(new Intent(this, SettingsActivity.class));
	}

	private void onRefreshItems() {
		showDialog(REFRESHING_DIALOG);
		rssPollServiceScheduler.runOnce();
		registerAllReceivers();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case R.id.refresh_option:
        	onRefreshItems();
        	return true;
        case R.id.settings_option:
        	startSettingsActivity();
        	return true;
        default:
        	return super.onOptionsItemSelected(menuItem);
        }
    }

	private void reloadListData() {
		Log.d(TAG, "Reloading list data");
		listAdapter.clear();
		for (FeedItem item : feedItemDao.loadAll()) {
			listAdapter.add(item);
		}
	}

	protected ProgressDialog getRefreshProgressDialog() {
		return refreshProgressDialog;
	}

	/**
	 * This implementation is not as efficient as {@link CursorAdapter}.
	 * However, it provides cleaner code and in given case performance should
	 * not be an issue.
	 *
	 * To work efficiently the adapter implemented here uses two techniques: -
	 * It reuses the convertView passed to getView() to avoid inflating View
	 * when it is not necessary - It uses the ViewHolder pattern to avoid
	 * calling findViewById() when it is not necessary
	 *
	 * The ViewHolder pattern consists in storing a data structure in the tag of
	 * the view returned by getView(). This data structures contains references
	 * to the views we want to bind data to, thus avoiding calls to
	 * findViewById() every time getView() is invoked.
	 */
	private class FeedItemListAdapter extends ArrayAdapter<FeedItem> {

		private LayoutInflater mInflater;

		public FeedItemListAdapter(Context context, List<FeedItem> data) {
			super(context, 0, 0, data);
			mInflater = LayoutInflater.from(context);
			sort(new FeedItem.ByDateDescending());
		}

		@Override
		public long getItemId(int i) {
			return getItem(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {

			ViewHolder viewholder;

			if (view == null) {

				view = mInflater.inflate(R.layout.list_item, null);
				viewholder = new ViewHolder();
				viewholder.text = (TextView) view.findViewById(R.id.list_item_text);
				viewholder.date = (TextView) view.findViewById(R.id.list_item_date);
				view.setTag(viewholder);

			} else {
				viewholder = (ViewHolder) view.getTag();
			}

			populateViewData(i, viewholder);

			return view;
		}

		private String formatDate(FeedItem item) {
			java.text.DateFormat dateFormat = getDateFormat(getContext());
			java.text.DateFormat timeFormat = getTimeFormat(getContext());
			return dateFormat.format(item.getDate()) + " " + timeFormat.format(item.getDate());
		}

		private void populateViewData(int i, ViewHolder viewHolder) {

			FeedItem item = (FeedItem) getItem(i);

			if (TextUtils.isEmpty(item.getTitle())) {
				viewHolder.text.setText(item.getDescriptionWithHtml(MAX_DESCRIPTION_CHARS));
			} else {
				viewHolder.text.setText(item.getTitle());
			}

			viewHolder.date.setText(formatDate(item));
		}

		private class ViewHolder {
			TextView date;
			TextView text;
		}
	}

	private class NotConfiguredBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Not configured");
			makeText(RSSFeedItemListActivity.this, R.string.not_configured, LENGTH_SHORT).show();
			dismissDialog(REFRESHING_DIALOG);
			unregisterAllBroadcastReceivers();
		}
	}

	private class RSSPollServiceBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			FeedItem item = null;

			if (listAdapter.getCount() != 0) {
				item = listAdapter.getItem(0);
			}

			if (item != null && !item.equals(feedItemDao.getLatest())) {
				Log.i(TAG, "New items available, refreshing list");
				reloadListData();
			} else {
				informThatThereAreNoNewItemsAvailable();
			}

			dismissDialog(REFRESHING_DIALOG);
			unregisterAllBroadcastReceivers();
		}

		private void informThatThereAreNoNewItemsAvailable() {
			Log.i(TAG, "No new feed items available");
			makeText(RSSFeedItemListActivity.this,
					R.string.no_new_feed_items_available, LENGTH_SHORT).show();
		}
	}
}
