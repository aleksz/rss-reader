package com.gmail.at.zhuikov.aleksandr.rssreader.db;

import static com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.TABLE_NAME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmail.at.zhuikov.aleksandr.rssreader.db.FeedItemProvider.Columns;

public class DatabaseHelper extends SQLiteOpenHelper {

	static final String DATABASE_NAME = "rssReader.db";

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
				+ Columns._ID + " INTEGER PRIMARY KEY,"
				+ Columns.TITLE + " TEXT,"
				+ Columns.LINK + " TEXT,"
				+ Columns.DESCRIPTION + " TEXT,"
				+ Columns.DATE + " INTEGER);");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
