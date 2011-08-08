package com.gmail.at.zhuikov.aleksandr.rssreader.db;

import java.util.Comparator;
import java.util.Date;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

/**
 * Immutable
 */
public class FeedItem implements Comparable<FeedItem> {

	protected Date date;
	protected String description;
	protected long id;
	protected String link;
	protected String title;

	protected FeedItem() {
	}

	FeedItem(long id, String title, String link, String description, Date date) {
		this.id = id;
		this.title = title;
		this.link = link;
		this.description = description;
		this.date = date;
	}

	public FeedItem(String title, String link, String description, Date date) {
		this(0L, title, link, description, date);
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public long getId() {
		return id;
	}

	void setId(long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public String getTitle() {
		return title;
	}

	public boolean isNew() {

		if (getId() == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Handles HTML in description. If description length is longer than
	 * <code>maxLength</code>, returns first 100 characters + "..." attached to
	 * the end
	 */
	public CharSequence getDescriptionWithHtml(int maxLength) {

		Spanned spanned = Html.fromHtml(description);

		if (spanned.length() > maxLength) {
			return new SpannableStringBuilder(spanned).replace(maxLength,
					spanned.length(), "...");
		}

		return spanned;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedItem other = (FeedItem) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FeedItem [date=" + date + "]";
	}

	/**
	 * Provides natural sort order by {@link #date} descending
	 */
	public int compareTo(FeedItem feeditem) {

		if (getDate() == null) {
			return -1;
		} else if (feeditem.getDate() == null) {
			return 1;
		}

		return getDate().compareTo(feeditem.getDate()) * -1;
	}

	public static class ByDateDescending implements Comparator<FeedItem> {

		public int compare(FeedItem feeditem, FeedItem feeditem1) {
			return feeditem.compareTo(feeditem1);
		}
	}
}
