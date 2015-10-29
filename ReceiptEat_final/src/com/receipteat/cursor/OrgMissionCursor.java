package com.receipteat.cursor;

import com.example.receipteat_final.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class OrgMissionCursor extends CursorAdapter {
	@SuppressWarnings("deprecation")

	public OrgMissionCursor(Context context, Cursor c) {
		super(context, c);
	}

	@Override

	public void bindView(View view, Context context, Cursor cursor) {
		TextView krmission_text = (TextView) view.findViewById(R.id.krmission_item);

		String krmission_name = cursor.getString(cursor.getColumnIndex("name"));
		
		krmission_text.setText(krmission_name);
	}

	@Override

	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.krmission_list, parent, false);
		return v;
	}

}