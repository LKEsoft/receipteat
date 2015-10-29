package com.receipteat;

import com.example.receipteat_final.R;
import com.receipteat.cursor.KrMissionCursor;
import com.receipteat.cursor.OrgMissionCursor;
import com.receipteat.cursor.OrgNameCursor;
import com.receipteat.database.KoreanMissionDatabaseOpenHelper;
import com.receipteat.database.OrgMissionDatabaseOpenHelper;
import com.receipteat.database.OrgNameDatabaseOpenHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class saveoption extends Activity {

	SQLiteDatabase krmissiondb;
	KoreanMissionDatabaseOpenHelper krmissionhelper;
	KrMissionCursor krmcursor;
	Cursor krm_cursor;

	SQLiteDatabase orgmissiondb;
	OrgMissionDatabaseOpenHelper orgmissionhelper;
	OrgMissionCursor orgmcursor;
	Cursor orgm_cursor;

	SQLiteDatabase orgnamedb;
	OrgNameDatabaseOpenHelper orgnamehelper;
	OrgNameCursor orgncursor;
	Cursor orgn_cursor;

	String krmissiondbname = "koreanmission";
	String orgmissiondbname = "orgmission";
	String orgnamedbname = "orgname";

	String krmquerySelectAll = String.format("SELECT * FROM %s", krmissiondbname);
	String orgmquerySelectAll = String.format("SELECT * FROM %s", orgmissiondbname);
	String orgnquerySelectAll = String.format("SELECT * FROM %s", orgnamedbname);

	EditText edit_krmission;

	int chooseoption = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saveoption);

		final ListView krmission_list = (ListView) findViewById(R.id.koreanmissionlist);

		krmissionhelper = new KoreanMissionDatabaseOpenHelper(saveoption.this, "koreanmission.db", null, 1);
		krmissiondb = krmissionhelper.getWritableDatabase();

		krm_cursor = krmissiondb.rawQuery(krmquerySelectAll, null);
		krmcursor = new KrMissionCursor(this, krm_cursor);

		orgmissionhelper = new OrgMissionDatabaseOpenHelper(saveoption.this, "orgmission.db", null, 1);
		orgmissiondb = orgmissionhelper.getWritableDatabase();

		orgm_cursor = orgmissiondb.rawQuery(orgmquerySelectAll, null);
		orgmcursor = new OrgMissionCursor(this, orgm_cursor);

		orgnamehelper = new OrgNameDatabaseOpenHelper(saveoption.this, "orgname.db", null, 1);
		orgnamedb = orgnamehelper.getWritableDatabase();

		orgn_cursor = orgnamedb.rawQuery(orgnquerySelectAll, null);
		orgncursor = new OrgNameCursor(this, orgn_cursor);

		krmission_list.setAdapter(krmcursor);

		edit_krmission = (EditText) findViewById(R.id.edit_krmission);
		edit_krmission.setHint("한글 과제명");

		Button btn_kormission = (Button) findViewById(R.id.btn_savekrmission);
		btn_kormission.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText krmname = (EditText) findViewById(R.id.edit_krmission);
				String name = krmname.getText().toString();
				String query;
				switch (chooseoption) {
				case 0:
					query = String.format("INSERT INTO %s VALUES (null,'%s')", krmissiondbname, name);
					krmissiondb.execSQL(query);

					krm_cursor = krmissiondb.rawQuery(krmquerySelectAll, null);
					krmcursor.changeCursor(krm_cursor);
					krmname.setText("");
					break;
				case 1:
					query = String.format("INSERT INTO %s VALUES (null,'%s')", orgmissiondbname, name);
					orgmissiondb.execSQL(query);

					orgm_cursor = orgmissiondb.rawQuery(orgmquerySelectAll, null);
					orgmcursor.changeCursor(orgm_cursor);
					krmname.setText("");
					break;
				case 2:
					query = String.format("INSERT INTO %s VALUES (null,'%s')", orgnamedbname, name);
					orgnamedb.execSQL(query);

					orgn_cursor = orgnamedb.rawQuery(orgnquerySelectAll, null);
					orgncursor.changeCursor(orgn_cursor);
					krmname.setText("");
					break;
				}
			}
		});

		Button btn_choosekrm = (Button) findViewById(R.id.btn_krmission);
		btn_choosekrm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ListView krmission_list = (ListView) findViewById(R.id.koreanmissionlist);
				edit_krmission.setHint("한글 과제명");

				krm_cursor = krmissiondb.rawQuery(krmquerySelectAll, null);
				krmcursor.changeCursor(krm_cursor);

				krmission_list.setAdapter(krmcursor);
				chooseoption = 0;
			}
		});

		Button btn_chooseorgm = (Button) findViewById(R.id.btn_orgmisson);
		btn_chooseorgm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit_krmission.setHint("주관 기관명");
				orgm_cursor = orgmissiondb.rawQuery(orgmquerySelectAll, null);
				orgmcursor.changeCursor(orgm_cursor);

				krmission_list.setAdapter(orgmcursor);
				chooseoption = 1;
			}
		});

		Button btn_chooseorgn = (Button) findViewById(R.id.btn_orgname);
		btn_chooseorgn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				orgn_cursor = orgnamedb.rawQuery(orgnquerySelectAll, null);
				orgncursor.changeCursor(orgn_cursor);

				edit_krmission.setHint("주관 과제명");
				krmission_list.setAdapter(orgncursor);
				chooseoption = 2;
			}
		});

		krmission_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
				alert.setTitle("삭제");
				final Cursor c;
				String message;
				switch (chooseoption) {
				case 0:
					c = (Cursor) krmcursor.getItem(position);
					message = String.format("%s를 삭제하시겠습니까?", c.getString(1));
					alert.setMessage(message);
					alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String query;
							query = String.format("DELETE FROM %s WHERE _id = %s;", krmissiondbname, c.getString(0));
							krmissiondb.execSQL(query);

							krm_cursor = krmissiondb.rawQuery(krmquerySelectAll, null);
							krmcursor.changeCursor(krm_cursor);
						}
					});
					alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					break;
				case 1:
					c = (Cursor) orgmcursor.getItem(position);
					message = String.format("%s를 삭제하시겠습니까?", c.getString(1));
					alert.setMessage(message);
					alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String query;

							query = String.format("DELETE FROM %s WHERE _id = %s;", orgmissiondbname, c.getString(0));
							orgmissiondb.execSQL(query);

							orgm_cursor = orgmissiondb.rawQuery(orgmquerySelectAll, null);
							orgmcursor.changeCursor(orgm_cursor);
						}
					});
					alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					break;
				case 2:
					c = (Cursor) orgncursor.getItem(position);
					message = String.format("%s를 삭제하시겠습니까?", c.getString(1));
					alert.setMessage(message);
					alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String query;

							query = String.format("DELETE FROM %s WHERE _id = %s;", orgnamedbname, c.getString(0));
							orgnamedb.execSQL(query);

							orgn_cursor = orgnamedb.rawQuery(orgnquerySelectAll, null);
							orgncursor.changeCursor(orgn_cursor);
						}
					});
					alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					break;
				}

				alert.show();
				return false;
			}
		});
	}

}