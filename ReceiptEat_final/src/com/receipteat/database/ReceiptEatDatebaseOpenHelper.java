package com.receipteat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ReceiptEatDatebaseOpenHelper extends SQLiteOpenHelper{
	public ReceiptEatDatebaseOpenHelper(Context context,String name, CursorFactory factory,int version){
		super(context,name,factory,version);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		String sql = "create table receipteat ("+
	"_id integer primary key autoincrement, " +
	"kormission text, " +
	"orgmission text, " +
	"orgname text, " +
				"date text not null, " +
	"used text not null, " +
	"storenumber text not null, " +
	"storename text not null, " +
				"price integer not null, " +
	"photourl text not null)";
		db.execSQL(sql);
	}
	
	public void onUpgrade(SQLiteDatabase db,int oldversion,int newversion){
		String sql = "drop table if exists receipt";
		db.execSQL(sql);
		
		onCreate(db);
	}
	
	
}
