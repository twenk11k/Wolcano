
package com.wolcano.musicplayer.music.mvp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class TaskQuery {

	private final String[] projection;
	private final String[] selectionArgs;
	private final String selection;
	public Uri uri;

	private String sort_order;
	public int type;


	public TaskQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		this.uri = uri;
		this.projection = projection;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.sort_order = sortOrder;
	}
	private static Cursor queryResolver(ContentResolver resolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Cursor cursor = null;
		try {
			cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch(SecurityException e) {
			e.printStackTrace();
		}
		return cursor;
	}
	public Cursor runQuery(ContentResolver resolver)
	{
			return queryResolver(resolver, uri, projection, selection, selectionArgs, sort_order);
	}
}
