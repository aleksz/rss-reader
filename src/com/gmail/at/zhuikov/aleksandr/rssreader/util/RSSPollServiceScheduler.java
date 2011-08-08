package com.gmail.at.zhuikov.aleksandr.rssreader.util;

import com.gmail.at.zhuikov.aleksandr.rssreader.RSSPollService;

/**
 * Contains functionality for starting, cancelling and scheduling
 * {@link RSSPollService}
 */
public interface RSSPollServiceScheduler {

	void cancel();

	void runOnce();

	void schedule();
}