package com.receipteat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.receipteat_final.R;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.receipteat.cursor.KrMissionCursor;
import com.receipteat.cursor.OrgMissionCursor;
import com.receipteat.cursor.OrgNameCursor;
import com.receipteat.database.KoreanMissionDatabaseOpenHelper;
import com.receipteat.database.OrgMissionDatabaseOpenHelper;
import com.receipteat.database.OrgNameDatabaseOpenHelper;
import com.receipteat.database.ReceiptEatDatebaseOpenHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class savepage extends Activity {
	private int TAKE_CAMERA = 1; // Ä«¸Þ¶ó ¸®ÅÏ ÄÚµå°ª ¼³Á¤
	private int TAKE_GALLERY = 2; // ¾Ù¹ü¼±ÅÃ¿¡ ´ëÇÑ ¸®ÅÏ ÄÚµå°ª ¼³Á¤
	private int DIALOG_DATE = 3;
	private ProgressDialog proDial;

	public static final String PACKAGE_NAME = "com.datumdroid.android.ocr.simple";
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/receipteat/";

	// You should have the trained data file in assets folder
	// You can get them at:
	// http://code.google.com/p/tesseract-ocr/downloads/list
	public static final String lang = "kor";

	TextView datetext;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String strsdf = sdf.format(new Date());

	int year = Integer.parseInt(strsdf.substring(0, strsdf.indexOf("-")));
	int month = Integer.parseInt(strsdf.substring(strsdf.indexOf("-") + 1, strsdf.lastIndexOf("-")));
	int day = Integer.parseInt(strsdf.substring(strsdf.lastIndexOf("-") + 1, strsdf.length()));

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			datetext.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
		}
	};

	String path = Environment.getExternalStorageDirectory().getAbsolutePath();
	String foldername = "receipteat";
	Bitmap img = null;
	String dbdate;

	Uri img_uri = null;

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

	SQLiteDatabase receipteatdb;
	ReceiptEatDatebaseOpenHelper rehelper;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" , DATA_PATH + "images/", DATA_PATH + "pdfs/"};

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return;
				}
			}

		}

		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				// GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata");

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

		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosepage);

		krmissionhelper = new KoreanMissionDatabaseOpenHelper(savepage.this, "koreanmission.db", null, 1);
		orgmissionhelper = new OrgMissionDatabaseOpenHelper(savepage.this, "orgmission.db", null, 1);
		orgnamehelper = new OrgNameDatabaseOpenHelper(savepage.this, "orgname.db", null, 1);
		rehelper = new ReceiptEatDatebaseOpenHelper(savepage.this, "receipteat.db", null, 1);

		krmissiondb = krmissionhelper.getWritableDatabase();

		krm_cursor = krmissiondb.rawQuery(krmquerySelectAll, null);
		krmcursor = new KrMissionCursor(this, krm_cursor);

		orgmissiondb = orgmissionhelper.getWritableDatabase();

		orgm_cursor = orgmissiondb.rawQuery(orgmquerySelectAll, null);
		orgmcursor = new OrgMissionCursor(this, orgm_cursor);

		orgnamedb = orgnamehelper.getWritableDatabase();

		orgn_cursor = orgnamedb.rawQuery(orgnquerySelectAll, null);
		orgncursor = new OrgNameCursor(this, orgn_cursor);

		Button btncamera = (Button) findViewById(R.id.btn_takepicture);
		Button btnlist = (Button) findViewById(R.id.btn_Gallery);

		btncamera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(DATA_PATH, "temp.jpg");

				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, TAKE_CAMERA);
			}
		});
		btnlist.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, TAKE_GALLERY);
			}
		});

		datetext = (TextView) findViewById(R.id.textView10);
		dbdate = sdf.format(new Date());
		datetext.setText(dbdate);

		Button btnchoosedate = (Button) findViewById(R.id.btn_choosedate);
		btnchoosedate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_DATE);
			}
		});

		Button btnuse = (Button) findViewById(R.id.Btn_usealert);
		btnuse.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(savepage.this);
				alertBuilder.setTitle("»ç¿ë¿ëµµ");
				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(savepage.this,
						android.R.layout.select_dialog_item);
				adapter.add("È¸ÀÇºñ");
				adapter.add("´Ù°úºñ");
				adapter.add("¿©ºñ");
				adapter.add("¹®Çå±¸ÀÔºñ");
				adapter.add("±âÅ¸");

				alertBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String struse = adapter.getItem(which);
						TextView textuse = (TextView) findViewById(R.id.detail_price);
						textuse.setText(struse);
					}
				});
				alertBuilder.show();
			}
		});

		Button btnkormission = (Button) findViewById(R.id.Btn_kormissionalert);
		btnkormission.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(savepage.this);
				alertBuilder.setTitle("ÇÑ±Û°úÁ¦¸í");

				alertBuilder.setAdapter(krmcursor, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Cursor c = (Cursor) krmcursor.getItem(which);
						String textkormission = c.getString(1);
						TextView textuse = (TextView) findViewById(R.id.detail_storename);
						textuse.setText(textkormission);
					}
				});
				alertBuilder.show();
			}
		});

		Button btnorgname = (Button) findViewById(R.id.Btn_orgnamealert);
		btnorgname.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(savepage.this);
				alertBuilder.setTitle("ÁÖ°ü±â°ü¸í");

				alertBuilder.setAdapter(orgmcursor, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Cursor c = (Cursor) orgmcursor.getItem(which);
						String textkormission = c.getString(1);
						TextView textuse = (TextView) findViewById(R.id.detail_storenumber);
						textuse.setText(textkormission);
					}
				});
				alertBuilder.show();
			}
		});

		Button btnorgmission = (Button) findViewById(R.id.Btn_orgmissionalert);
		btnorgmission.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(savepage.this);
				alertBuilder.setTitle("ÁÖ°ü°úÁ¦¸í");

				alertBuilder.setAdapter(orgncursor, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Cursor c = (Cursor) orgncursor.getItem(which);
						String textkormission = c.getString(1);
						TextView textuse = (TextView) findViewById(R.id.detail_used1);
						textuse.setText(textkormission);
					}
				});
				alertBuilder.show();
			}
		});

		Button btnsave = (Button) findViewById(R.id.btn_savedata);
		btnsave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textfilename = (TextView) findViewById(R.id.textVew10);
				saveBitmapToFileCache(img, path + File.separator + foldername + "/images", textfilename.getText().toString());
				TextView textdate = (TextView) findViewById(R.id.textView10);
				EditText editstorename = (EditText) findViewById(R.id.editText3);
				EditText editstorenumber = (EditText) findViewById(R.id.editText4);
				EditText editprice = (EditText) findViewById(R.id.editText5);
				TextView textkormission = (TextView) findViewById(R.id.detail_storename);
				TextView textorgname = (TextView) findViewById(R.id.detail_storenumber);
				TextView textorgmission = (TextView) findViewById(R.id.detail_used1);
				TextView textused = (TextView) findViewById(R.id.detail_price);
				insert(textkormission.getText().toString(), textorgmission.getText().toString(),
						textorgname.getText().toString(), textdate.getText().toString(), textused.getText().toString(),
						editstorenumber.getText().toString(), editstorename.getText().toString(),
						Integer.parseInt(editprice.getText().toString()),
						path + File.separator + foldername + "/images" + File.separator + textfilename.getText().toString());
				Intent intent = new Intent(getApplicationContext(), viewpage.class);
				startActivity(intent);
			}
		});
	}

	Handler handler = new Handler();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == TAKE_CAMERA) {
				options = new BitmapFactory.Options();
				options.inSampleSize = 1;

				img = BitmapFactory.decodeFile(DATA_PATH + "temp.jpg", options);

				try {
					ExifInterface exif = new ExifInterface(DATA_PATH + "temp.jpg");
					int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);

					int rotate = 0;

					switch (exifOrientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
					}

					if (rotate != 0) {
						int w = img.getWidth();
						int h = img.getHeight();

						Matrix mtx = new Matrix();
						mtx.preRotate(rotate);

						img = Bitmap.createBitmap(img, 0, 0, w, h, mtx, false);
					}
					img = img.copy(Bitmap.Config.ARGB_8888, true);

				} catch (IOException e) {
				}

			} else if (requestCode == TAKE_GALLERY) {
				if (data != null) {
					String imgpath = getImageNameToUri(data.getData());

					options = new BitmapFactory.Options();
					options.inSampleSize = 4;

					img = BitmapFactory.decodeFile(imgpath, options);

					if (img != null) {
						try {
							ExifInterface exif = new ExifInterface(imgpath);
							int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
									ExifInterface.ORIENTATION_NORMAL);

							int rotate = 0;

							switch (exifOrientation) {
							case ExifInterface.ORIENTATION_ROTATE_90:
								rotate = 90;
								break;
							case ExifInterface.ORIENTATION_ROTATE_180:
								rotate = 180;
								break;
							case ExifInterface.ORIENTATION_ROTATE_270:
								rotate = 270;
								break;
							}

							if (rotate != 0) {
								int w = img.getWidth();
								int h = img.getHeight();

								Matrix mtx = new Matrix();
								mtx.preRotate(rotate);

								img = Bitmap.createBitmap(img, 0, 0, w, h, mtx, false);
							}
							img = img.copy(Bitmap.Config.ARGB_8888, true);

							saveBitmapToFileCache(img, path + File.separator + foldername, "temp.jpg");
						} catch (IOException e) {
						}
					}
				}
			}
		}

		if (img != null) {
			TextView textfilename = (TextView) findViewById(R.id.textVew10);
			SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
			String filename = date.format(new Date()) + ".jpg";
			textfilename.setText(filename);

			runOnUiThread(new Runnable() {
				public void run() {
					proDial = ProgressDialog.show(savepage.this, "", "¹®ÀÚÀÎ½Ä ÁßÀÔ´Ï´Ù.", true);
					handler.postDelayed(new Runnable() {
						public void run() {
							TessBaseAPI baseApi = new TessBaseAPI();
							baseApi.setDebug(true);
							baseApi.init(DATA_PATH, lang);
							baseApi.setImage(img);

							String recognizedText = baseApi.getUTF8Text();
							String strstorenumber = findStoreNumber(recognizedText);
							String strprice = findPrice(recognizedText);

							EditText editnum = (EditText) findViewById(R.id.editText4);
							EditText editprice = (EditText) findViewById(R.id.editText5);
							
							editnum.setText(strstorenumber);
							editprice.setText(strprice);
							baseApi.end();
							if (proDial != null && proDial.isShowing()) {
								proDial.dismiss();
							}
						}
					}, 1000);
				}
			});
		}
	}

	public void saveBitmapToFileCache(Bitmap img, String strFilePath, String filename) {
		File file = new File(strFilePath);

		if (!file.exists()) {
			file.mkdirs();
		}

		File fileCacheItem = new File(strFilePath + File.separator + filename);
		OutputStream out = null;
		try {
			fileCacheItem.createNewFile();
			out = new FileOutputStream(fileCacheItem);
			img.compress(CompressFormat.JPEG, 100, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String getImageNameToUri(Uri data) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(data, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		String imgPath = cursor.getString(column_index);

		return imgPath;
	}

	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_DATE) {
			return new DatePickerDialog(this, dateListener, year, month - 1, day);
		} else {
			return null;
		}
	}

	public void insert(String kormission, String orgmission, String orgname, String date, String used,
			String storenumber, String storename, int price, String url) {
		receipteatdb = rehelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("kormission", kormission);
		values.put("orgmission", orgmission);
		values.put("orgname", orgname);
		values.put("date", date);
		values.put("used", used);
		values.put("storenumber", storenumber);
		values.put("storename", storename);
		values.put("price", price);
		values.put("photourl", url);

		receipteatdb.insert("receipteat", null, values);
	}

	public String removeRex(String rex, String inp) {
		Pattern numP = Pattern.compile(rex);
		Matcher mat = numP.matcher("");

		mat.reset(inp);
		inp = mat.replaceAll(" ");

		return inp;
	}

	public String findStoreNumber(String recog_text) {
		String temptext = removeRex("[^0-9]", recog_text);
		temptext = temptext.trim();
		String[] total_text = temptext.split(" ");
		for (int i = 0; i < total_text.length - 2; i++) {
			if (total_text[i].length() == 3 && total_text[i + 1].length() == 2 && total_text[i + 2].length() == 5) {
				return total_text[i] + "-" + total_text[i + 1] + "-" + total_text[i + 2];
			}
		}
		return "Ã£Áö ¸øÇÔ";
	}

	public String findPrice(String recog_text) {
		String tempstring = removeRex("[°¡-ÆR]", recog_text);
		int maxint = 0;
		String[] total_text = tempstring.split("   ");
		for (int i = 0; i < total_text.length; i++) {
			if ((total_text[i].indexOf(",") > 0) || (total_text[i].indexOf(".") > 0)
					|| (total_text[i].indexOf("'") > 0)) {
				total_text[i] = total_text[i].replaceAll("[^0-9]","");
				Log.e("SangYoon", total_text[i]);
				if((total_text[i].length()>3)&&(total_text[i].lastIndexOf("0")==total_text[i].length()-1)){
					Log.e("SangYoon", total_text[i]);
					
					int temp = Integer.parseInt(total_text[i]);
					if (maxint < temp) { 
						maxint = temp; 
					}
				}
			}
		}
		return String.valueOf(maxint);
	}
}
