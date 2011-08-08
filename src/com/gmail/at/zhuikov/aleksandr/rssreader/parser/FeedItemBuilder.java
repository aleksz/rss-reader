package com.gmail.at.zhuikov.aleksandr.rssreader.parser;

import java.util.Date;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItem;

public class FeedItemBuilder {

	private Date date;
	private String description;
	private String link;
	private String title;

	public FeedItem getFeedItem() {
		return new FeedItem(title, link, description, date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}