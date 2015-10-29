package com.receipteat;

import com.example.receipteat_final.R;
import com.receipteat.database.KoreanMissionDatabaseOpenHelper;
import com.receipteat.database.OrgMissionDatabaseOpenHelper;
import com.receipteat.database.OrgNameDatabaseOpenHelper;
import com.receipteat.database.ReceiptEatDatebaseOpenHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	SQLiteDatabase krmissiondb;
	KoreanMissionDatabaseOpenHelper krmissionhelper;
	SQLiteDatabase orgmissiondb;
	OrgMissionDatabaseOpenHelper orgmissionhelper;
	SQLiteDatabase orgnamedb;
	OrgNameDatabaseOpenHelper orgnamehelper;
	SQLiteDatabase receipteatdb;
	ReceiptEatDatebaseOpenHelper rehelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		krmissionhelper = new KoreanMissionDatabaseOpenHelper(MainActivity.this,"koreanmission.db",null,1);
		orgmissionhelper = new OrgMissionDatabaseOpenHelper(MainActivity.this,"orgmission.db",null,1);
		orgnamehelper = new OrgNameDatabaseOpenHelper(MainActivity.this, "orgname.db", null, 1);
		rehelper = new ReceiptEatDatebaseOpenHelper(MainActivity.this, "receipteat.db", null, 1);
		View.OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch (v.getId()) {
				case R.id.btn_savepage:
					intent = new Intent(getApplicationContext(), savepage.class);
					break;
				case R.id.btn_viewpage:
					intent = new Intent(getApplicationContext(), viewpage.class);
					break;
				case R.id.btn_saveoption:
					intent = new Intent(getApplicationContext(), saveoption.class);
					break;
				}
				startActivity(intent);
			}
		};
		
		Button btn1 = (Button)findViewById(R.id.btn_savepage);
		Button btn2 = (Button)findViewById(R.id.btn_viewpage);
		Button btn3 = (Button)findViewById(R.id.btn_saveoption);
		
		btn1.setOnClickListener(listener);
		btn2.setOnClickListener(listener);
		btn3.setOnClickListener(listener);
	}

}
