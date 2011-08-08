package com.gmail.at.zhuikov.aleksandr.rssreader.util;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;
import static android.app.PendingIntent.getService;
import static android.content.Context.ALARM_SERVICE;
import static com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService.SEND_NOTIFICATIONS_EXTRA;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService;

public class RSSPollServiceSchedulerImpl implements RSSPollServiceScheduler {

	private static final String TAG = RSSPollServiceSchedulerImpl.class.getSimpleName();

	private AlarmManager alarmManager;
	private final Context context;
	private final Preferences preferences;

	public RSSPollServiceSchedulerImpl(Preferences preferences, Context context) {
		this.preferences = preferences;
		this.context = context;
		alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
	}

	private Intent getServiceIntent() {
		return new Intent(context, RSSPollService.class);
	}

	private PendingIntent getServicePendingIntent() {
		return getService(context, 0, getServiceIntent(), 0);
	}

	@Override
	public void cancel() {
		Log.i(TAG, "RSS reader service will be cancelled");
		alarmManager.cancel(getServicePendingIntent());
	}

	@Override
	public void runOnce() {
		cancel();
		Log.i(TAG, "Starting RSS poll service");
		Intent intent = getServiceIntent();
		intent.putExtra(SEND_NOTIFICATIONS_EXTRA, false);
		context.startService(intent);
	}

	@Override
	public void schedule() {

		long refreshInterval = preferences.getRefreshInterval();
		long startTime = SystemClock.elapsedRealtime();

		cancel();

		Log.i(TAG, "RSS reader service will start at " + startTime
				+ " with interval " + refreshInterval);

		alarmManager.setRepeating(
				ELAPSED_REALTIME_WAKEUP,
				startTime,
				refreshInterval,
				getServicePendingIntent());
	}
}
