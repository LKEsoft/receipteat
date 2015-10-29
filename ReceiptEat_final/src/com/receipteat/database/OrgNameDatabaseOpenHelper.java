package com.receipteat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class OrgNameDatabaseOpenHelper extends SQLiteOpenHelper{
	public OrgNameDatabaseOpenHelper(Context context,String name, CursorFactory factory,int version){
		super(context,name,factory,version);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		String sql = "create table orgname ("+
	"_id integer primary key autoincrement, " +
				"name text not null)";
		db.execSQL(sql);
	}
	
	public void onUpgrade(SQLiteDatabase db,int oldversion,int newversion){
		String sql = "drop table if exists orgname";
		db.execSQL(sql);
		
		onCreate(db);
	}
}