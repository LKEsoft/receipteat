package com.receipteat.cursor;

import com.example.receipteat_final.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ReceiptCursorAdapter extends CursorAdapter {
	@SuppressWarnings("deprecation")

	public ReceiptCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@SuppressLint("DefaultLocale")
	@Override

	public void bindView(View view, Context context, Cursor cursor) {

		TextView text_storename = (TextView) view.findViewById(R.id.textStorename);
		TextView text_storenumber = (TextView) view.findViewById(R.id.textStorenumber);
		TextView text_date = (TextView) view.findViewById(R.id.textDate);
		TextView text_price = (TextView) view.findViewById(R.id.textPrice);

		String storename = cursor.getString(cursor.getColumnIndex("storename"));
		String storenumber = cursor.getString(cursor.getColumnIndex("storenumber"));
		String date = cursor.getString(cursor.getColumnIndex("date"));
		int price = cursor.getInt(cursor.getColumnIndex("price"));
		String pricestr = String.format("%d", price);

		date = date.substring(date.lastIndexOf("-")+1,date.length());
		date = date+"¿œ";
		text_storename.setText(storename);
		text_storenumber.setText(storenumber);
		text_date.setText(date);
		text_price.setText(pricestr);
	}

	@Override

	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(context);

		View v = inflater.inflate(R.layout.list_view, parent, false);

		return v;

	}

}
