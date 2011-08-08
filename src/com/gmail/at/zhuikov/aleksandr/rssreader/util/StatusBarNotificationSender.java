package com.gmail.at.zhuikov.aleksandr.rssreader.util;

import static android.app.Notification.DEFAULT_LIGHTS;
import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_SHOW_LIGHTS;
import static android.app.PendingIntent.getActivity;
import static android.content.Context.NOTIFICATION_SERVICE;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.at.zhuikov.aleksandr.rssreader.R;
import com.gmail.at.zhuikov.aleksandr.rssreader.RSSFeedItemListActivity;

public class StatusBarNotificationSender {

	private static final String TAG = StatusBarNotificationSender.class.getSimpleName();

	private final Context context;

	public StatusBarNotificationSender(Context context) {
		this.context = context;
	}

	/**
	 * Sends notification and sets flashing lights
	 */
	public void onNewItemsAvailable() {

		Log.i(TAG, "New feed items available, will notify");

		Notification notification = new Notification(
				R.drawable.stat_notify_rss,
				context.getText(R.string.new_feed_items_available),
				System.currentTimeMillis());

		PendingIntent pendingintent = getActivity(
				context,
				0,
				new Intent(context, RSSFeedItemListActivity.class),
				0);

		notification.setLatestEventInfo(
				context,
				context.getText(R.string.app_name),
				context.getText(R.string.new_feed_items_available),
				pendingintent);

		notification.defaults |= DEFAULT_LIGHTS;
		notification.flags |= FLAG_AUTO_CANCEL;
		notification.flags |= FLAG_SHOW_LIGHTS;

		((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE))
				.notify(R.string.new_feed_items_available, notification);
	}
}
