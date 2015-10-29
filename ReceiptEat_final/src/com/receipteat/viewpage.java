package com.receipteat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.receipteat_final.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.receipteat.cursor.ReceiptCursorAdapter;
import com.receipteat.database.ReceiptEatDatebaseOpenHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class viewpage extends Activity {

	ReceiptEatDatebaseOpenHelper help;
	SQLiteDatabase db;
	Cursor cursor;
	ReceiptCursorAdapter radapter;

	String queryselectall = "SELECT * FROM receipteat WHERE DATE LIKE ";
	String queryselectresult;
	String choosemonth;

	ListView list;
	String path = Environment.getExternalStorageDirectory().getAbsolutePath();
	String foldername = "receipteat";

	public static final String PACKAGE_NAME = "com.datumdroid.android.ocr.simple";
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/receipteat/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "font/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return;
				}
			}
		}

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "font/" + "NanumGothic.ttf")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("font/" + "NanumGothic.ttf");
				// GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "font/" + "NanumGothic.ttf");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				// while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				// gin.close();
				out.close();

			} catch (IOException e) {
			}
		}
		if (!(new File(DATA_PATH + "font/" + "line.png")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("font/" + "line.png");
				// GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "font/" + "line.png");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				// while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				// gin.close();
				out.close();

			} catch (IOException e) {
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String strsdf = sdf.format(new Date());
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpage);

		TextView textYear = (TextView) findViewById(R.id.textYear);
		textYear.setText(strsdf.substring(0, strsdf.indexOf("-")));
		TextView textMonth = (TextView) findViewById(R.id.textMonth);
		textMonth.setText(strsdf.substring(strsdf.indexOf("-")+1,strsdf.lastIndexOf("-"))+"월");

		choosemonth = "'" + textYear.getText().toString() + "-" + textMonth.getText().toString().replaceAll("월", "-")
				+ "%'";
		queryselectresult = queryselectall + choosemonth;
		list = (ListView) findViewById(R.id.receiptlist);

		help = new ReceiptEatDatebaseOpenHelper(viewpage.this, "receipteat.db", null, 1);

		db = help.getWritableDatabase();

		cursor = db.rawQuery(queryselectresult, null);
		radapter = new ReceiptCursorAdapter(this, cursor);

		list.setAdapter((ListAdapter) radapter);
		list.setOnItemClickListener(listener);

		ImageButton yearleft = (ImageButton) findViewById(R.id.imageButton1);
		yearleft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textYear = (TextView) findViewById(R.id.textYear);
				TextView textMonth = (TextView) findViewById(R.id.textMonth);
				String yearstr = (String) textYear.getText();
				int year = Integer.parseInt(yearstr);
				year -= 1;
				if (year == 0) {
					year = 2015;
				}
				textYear.setText("" + year);
				choosemonth = "'" + year + "-" + textMonth.getText().toString().replaceAll("월", "-") + "%'";
				queryselectresult = queryselectall + choosemonth;
				cursor = db.rawQuery(queryselectresult, null);

				radapter.changeCursor(cursor);
				list.setAdapter(radapter);
			}
		});

		ImageButton yearright = (ImageButton) findViewById(R.id.imageButton2);
		yearright.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textYear = (TextView) findViewById(R.id.textYear);
				TextView textMonth = (TextView) findViewById(R.id.textMonth);
				String yearstr = (String) textYear.getText();
				int year = Integer.parseInt(yearstr);
				year += 1;
				if (year == 0) {
					year = 2015;
				}

				textYear.setText("" + year);
				choosemonth = "'" + year + "-" + textMonth.getText().toString().replaceAll("월", "-") + "%'";
				queryselectresult = queryselectall + choosemonth;
				cursor = db.rawQuery(queryselectresult, null);

				radapter.changeCursor(cursor);
				list.setAdapter(radapter);
			}
		});

		ImageButton monthleft = (ImageButton) findViewById(R.id.imageButton3);
		monthleft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textYear = (TextView) findViewById(R.id.textYear);
				TextView textMonth = (TextView) findViewById(R.id.textMonth);
				String monthstr = (String) textMonth.getText();
				int month = Integer.parseInt(monthstr.replaceAll("월", ""));
				month -= 1;
				if (month == 0) {
					month = 12;
				}
				textMonth.setText("" + month + "월");
				choosemonth = "'" + textYear.getText().toString() + "-"
						+ textMonth.getText().toString().replaceAll("월", "-") + "%'";
				queryselectresult = queryselectall + choosemonth;
				cursor = db.rawQuery(queryselectresult, null);

				radapter.changeCursor(cursor);
				list.setAdapter(radapter);
			}
		});

		ImageButton monthright = (ImageButton) findViewById(R.id.imageButton4);
		monthright.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textYear = (TextView) findViewById(R.id.textYear);
				TextView textMonth = (TextView) findViewById(R.id.textMonth);
				String monthstr = (String) textMonth.getText();
				int month = Integer.parseInt(monthstr.replaceAll("월", ""));
				month += 1;
				if (month == 13) {
					month = 1;
				}
				textMonth.setText("" + month + "월");
				choosemonth = "'" + textYear.getText().toString() + "-"
						+ textMonth.getText().toString().replaceAll("월", "-") + "%'";
				queryselectresult = queryselectall + choosemonth;
				cursor = db.rawQuery(queryselectresult, null);

				radapter.changeCursor(cursor);
				list.setAdapter(radapter);
			}
		});
	}
	OnItemClickListener listener = new OnItemClickListener() {
		@SuppressLint("InflateParams")
		@Override

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final Cursor c = (Cursor) radapter.getItem(position);

			final String strdetail_korm = String.format("한글 사업명 : %s", c.getString(1));
			final String strdetail_orgm = String.format("주관 기관명 : %s", c.getString(3));
			final String strdetail_orgn = String.format("주관 과제명 : %s", c.getString(2));
			final String strdetail_date = String.format("날짜 : %s", c.getString(4));
			final String strdetail_used = String.format("사용 용도 : %s", c.getString(5));
			final String strdetail_storename = String.format("가게 명 : %s", c.getString(7));
			final String strdetail_storenumber = String.format("사업자 번호 : %s", c.getString(6));
			final String strdetail_price = String.format("가격 : %s 원", c.getString(8));

			
			LayoutInflater inflater = getLayoutInflater();

			final View dialogView = inflater.inflate(R.layout.view_detail, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(viewpage.this); // AlertDialog.Builder
			// 객체 생성
			AlertDialog alertDialog;

			builder.setTitle("상세 정보");
			builder.setView(dialogView);
			builder.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					AlertDialog.Builder alert = new AlertDialog.Builder(viewpage.this);
					alert.setTitle("삭제");
					String message;
					message = String.format("삭제하시겠습니까?");
					alert.setMessage(message);
					alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							File img = new File(c.getString(9));
							if(img.exists()){
								img.delete();
							}
							String query;
							query = String.format("DELETE FROM receipteat WHERE _id = %s;", c.getString(0));
							db.execSQL(query);

							cursor = db.rawQuery(queryselectresult, null);
							radapter.changeCursor(cursor);
						}
					});
					alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					alert.show();
				}
			});
			builder.setPositiveButton("전송", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					LayoutInflater inflater = getLayoutInflater();
					SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
					final String filename = date.format(new Date()) + ".pdf";
					
					AlertDialog.Builder builder = new AlertDialog.Builder(viewpage.this); // AlertDialog.Builder
					builder.setTitle("이메일로 전송");
					
					builder.setMessage("전송하시겠습니까?");
					builder.setPositiveButton("전송", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								try {
									
									Document document = new Document(PageSize.A4,50,50,50,50);
									PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(DATA_PATH+"pdfs/"+filename));
									Image jpg = Image.getInstance(c.getString(9));
									
									document.open();
									
									BaseFont odjBaseFont = BaseFont.createFont(DATA_PATH + "font/" + "NanumGothic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
									Font odjFont = new Font(odjBaseFont,20);
									Font titleFont = new Font(odjBaseFont,32);
									
									Paragraph title = new Paragraph("카드 영수증 증빙 자료", titleFont);

					                title.setAlignment(Paragraph.ALIGN_CENTER);
					                
									document.add(title);
									
									Image line = Image.getInstance(DATA_PATH+"font/line.png");
									line.setAlignment(Image.ALIGN_CENTER);
									document.add(line);
									document.add(new Paragraph(strdetail_korm,odjFont));
									document.add(new Paragraph(strdetail_orgm,odjFont));
									document.add(new Paragraph(strdetail_orgn,odjFont));
									document.add(new Paragraph(strdetail_date,odjFont));
									document.add(new Paragraph(strdetail_used,odjFont));
									document.add(new Paragraph(strdetail_storename,odjFont));
									document.add(new Paragraph(strdetail_storenumber,odjFont));
									document.add(new Paragraph(strdetail_price,odjFont));
									jpg.scaleAbsolute(300, 400);
									document.add(jpg);
									
									document.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (DocumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							File f = new File(DATA_PATH+"pdfs/" + filename);
							if(f.exists()){
								String[] email = {""};
								Intent intent = new Intent(Intent.ACTION_SEND);
								intent.setType("plain/text");
								Uri fileuri = Uri.fromFile(f);
								
								intent.putExtra(Intent.EXTRA_EMAIL, email);
								intent.putExtra(Intent.EXTRA_SUBJECT,"ReceiptEat");
								intent.putExtra(Intent.EXTRA_TEXT,"전송하신 내용입니다.");
								intent.putExtra(Intent.EXTRA_STREAM, fileuri);
								startActivity(intent);
							}
						}
					});
					builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					builder.show();
					dialog.cancel();
				}
			});
			builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
				}

			});

			TextView detail_korm = (TextView) dialogView.findViewById(R.id.detail_korm);
			TextView detail_orgm = (TextView) dialogView.findViewById(R.id.detail_orgm);
			TextView detail_orgn = (TextView) dialogView.findViewById(R.id.detail_orgn);
			TextView detail_used = (TextView) dialogView.findViewById(R.id.detail_used);
			TextView detail_date = (TextView) dialogView.findViewById(R.id.detail_date);
			TextView detail_storenumber = (TextView) dialogView.findViewById(R.id.detail_storenumber);
			TextView detail_storename = (TextView) dialogView.findViewById(R.id.detail_storename);
			TextView detail_price = (TextView) dialogView.findViewById(R.id.detail_price);

			detail_korm.setText(strdetail_korm);
			detail_orgm.setText(strdetail_orgm);
			detail_orgn.setText(strdetail_orgn);
			detail_date.setText(strdetail_date);
			detail_used.setText(strdetail_used);
			detail_storenumber.setText(strdetail_storenumber);
			detail_storename.setText(strdetail_storename);
			detail_price.setText(strdetail_price);

			alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(false);// 없어지지 않도록 설정

			alertDialog.show();
		}

	};
}