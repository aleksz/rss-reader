package com.gmail.at.zhuikov.aleksandr.rssreader.db;

import java.util.Collection;
import java.util.List;

import android.content.ContentResolver;

/**
 * Interface for hiding interactions with {@link ContentResolver}
 */
public interface FeedItemDao {

	FeedItem getLatest();

	List<FeedItem> loadAll();

	void replaceAll(Collection<FeedItem> newItems);
}
