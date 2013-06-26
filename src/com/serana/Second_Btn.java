package com.serana;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.serena.connection.Network_Available;

//Bata Kalkin Sneakers
@SuppressLint({ "NewApi", "WorldReadableFiles" })
public class Second_Btn extends Activity {
	private boolean isXlarge = false;
	@SuppressWarnings("unused")
	private boolean onExit = false;
	static RelativeLayout catlog_btn, approuval_btn, logout_btn, req_btn,
			Search_btn;
	public static int disDialog = 0;
	Button logout, help;
	static ProgressBar menuProgress;
	@SuppressWarnings("unused")
	private ArrayList<All_Approval_Key_dto> alist;
	public static String SERVER_RESPONSE, TMTRACK_RESPONSE, WEBVIEW_RESPONSE;
	MyInterface myInterface;
	static TextView server_txt;
	static TextView apprcalCount;
	static TextView RequestCount;
	String TAG = "SCREEN SIZE";
	private String loginRemembrance = "notloggedout";
	private SharedPreferences user_details;
	private SharedPreferences login_deatilsStore;
	private String prefname = "SERENA";
	private String logindeatilsname = "SERENALOGIN";
	public static boolean sessionMaintance = false;
	private long clickedtime;
	public static boolean isChildSelected;
	private String Uname, Pass, Url;
	private long FirstloggedTime;
	public static boolean powerButton;
	@SuppressWarnings("unused")
	private String blockUnit;
	String alfssoauthntoken;
	Context ctx;
	Calendar cal;
	RelativeLayout switch_img;
	ArrayList<All_sencha_loginData_dto> list = new ArrayList<All_sencha_loginData_dto>();

	@Override
	public void onBackPressed() {
		makediallog();
		try {
			MyApplication.clearServiceResult();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called and Checking configuration");

		Configuration myConfig = new Configuration();
		myConfig.setToDefaults();
		myConfig = getResources().getConfiguration();

		switch (myConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE: {
			Log.v(TAG, "Screen size is extra large");
			isXlarge = true;
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_LARGE: {
			Log.v(TAG, "Screen size is large");
			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_NORMAL: {
			Log.v(TAG, "Screen size is normal");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_SMALL: {
			Log.v(TAG, "Screen size is small");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			break;
		}
		case Configuration.SCREENLAYOUT_SIZE_UNDEFINED: {
			Log.v(TAG, "Screen size is undefined");
			break;
		}
		}

	}

	@SuppressLint("WorldWriteableFiles")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.three_button);

		ctx = Second_Btn.this;
		DialogBlock.showProgressDialog(ctx, "", false);
		powerButton = false;
		menuProgress = (ProgressBar) findViewById(R.id.menuProgress);
		// menuProgress.setVisibility(View.GONE);
		sessionMaintance = false;
		alist = new ArrayList<All_Approval_Key_dto>();
		cal = Calendar.getInstance();
		// get the data from intent
		Uname = getIntent().getStringExtra("uname");
		Pass = getIntent().getStringExtra("pass");
		Url = getIntent().getStringExtra("url");
		blockUnit = getIntent().getStringExtra("block");
		Log.v("Crendetials are ", Uname + Pass + Url);
		SERVER_RESPONSE = "NULL";
		TMTRACK_RESPONSE = "NULL";

		login_deatilsStore = getSharedPreferences(logindeatilsname,
				MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = login_deatilsStore.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		try {
			if (MyApplication.getServer().startsWith("https")) {
				editor.putString("checkbox", "checked");
			} else {
				editor.putString("checkbox", "notchecked");
			}
		} catch (Exception e) {
		}

		editor.commit();
		editor.apply();

		InitialiseIds();

		if (isXlarge) {
			logout.setText(MyApplication.getUserID());
		}

		isChildSelected = false;
		Search_btn.setEnabled(false);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// remove the login detials from shared preferences and jump on
				// login screen
				makediallog();
			}
		});

		catlog_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isXlarge) {
					catlog_btn.setBackgroundResource(R.drawable.menu_bl_sel);
					req_btn.setBackgroundResource(R.drawable.menu_tl_active);
					approuval_btn
							.setBackgroundResource(R.drawable.menu_tr_active);
					switch_img
							.setBackgroundResource(R.drawable.menu_center_circles_bl);
					Search_btn.setBackgroundResource(R.drawable.menu_br_active);
				}
				clickedtime = (long) System.currentTimeMillis();
				isChildSelected = false;
				TabletCatalog.isfirstEntry = false;
				req_btn.setEnabled(false);
				approuval_btn.setEnabled(false);
				Search_btn.setEnabled(false);

				// if(Network_Available.hasConnection(ctx)){
				Intent in = new Intent(Second_Btn.this, TabletCatalog.class);
				in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				in.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				startActivity(in);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);

			}
		});

		req_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isXlarge) {
					catlog_btn.setBackgroundResource(R.drawable.menu_bl_active);
					req_btn.setBackgroundResource(R.drawable.menu_tl_sel);
					approuval_btn
							.setBackgroundResource(R.drawable.menu_tr_active);
					switch_img
							.setBackgroundResource(R.drawable.menu_center_circles_tl);
					Search_btn.setBackgroundResource(R.drawable.menu_br_active);
				}

				isChildSelected = false;
				Request.isfirstEntry = false;
				Request.isrefreshRelease = false;
				clickedtime = (long) System.currentTimeMillis();
				catlog_btn.setEnabled(false);
				approuval_btn.setEnabled(false);
				Search_btn.setEnabled(false);
				if (RequestCount.getText().toString().equalsIgnoreCase("0")) {
					catlog_btn.setEnabled(true);
					approuval_btn.setEnabled(true);
					Search_btn.setEnabled(true);

				} else {
					Intent in = new Intent(Second_Btn.this, Request.class);
					in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
							| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
					startActivity(in);
					overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);
				}
			}
		});

		approuval_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isXlarge) {
					catlog_btn.setBackgroundResource(R.drawable.menu_bl_active);
					req_btn.setBackgroundResource(R.drawable.menu_tl_active);
					approuval_btn.setBackgroundResource(R.drawable.menu_tr_sel);
					switch_img
							.setBackgroundResource(R.drawable.menu_center_circles_tr);
					Search_btn.setBackgroundResource(R.drawable.menu_br_active);
				}
				clickedtime = (long) System.currentTimeMillis();
				isChildSelected = false;
				Approval.isfirstEntry = false;
				Approval.isrefreshRelease = false;
				req_btn.setEnabled(false);
				catlog_btn.setEnabled(false);
				Search_btn.setEnabled(false);
				if (apprcalCount.getText().toString().equalsIgnoreCase("0")) {
					req_btn.setEnabled(true);
					catlog_btn.setEnabled(true);
					Search_btn.setEnabled(true);
				} else {

					Intent in = new Intent(Second_Btn.this, Approval.class);
					in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
							| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
					startActivity(in);
					overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);
				}
			}
		});

		Search_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isXlarge) {
					catlog_btn.setBackgroundResource(R.drawable.menu_bl_active);
					req_btn.setBackgroundResource(R.drawable.menu_tl_active);
					approuval_btn
							.setBackgroundResource(R.drawable.menu_tr_active);
					Search_btn.setBackgroundResource(R.drawable.menu_br_sel);
					switch_img
							.setBackgroundResource(R.drawable.menu_center_circles_br);
				}
				clickedtime = (long) System.currentTimeMillis();
				isChildSelected = false;
				req_btn.setEnabled(false);
				approuval_btn.setEnabled(false);
				catlog_btn.setEnabled(false);
				Search_View.isfirstEntry = false;

				Intent f = new Intent(Second_Btn.this, Search_View.class);
				f.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				startActivity(f);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		
		help.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent f = new Intent(Second_Btn.this, HelpScreen.class);
				f.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				startActivity(f);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		
	}

	private void InitialiseIds() {
		// TODO Auto-generated method stub
		Log.v(TAG, "Initialise the ids for the layout");
		logout = (Button) findViewById(R.id.logout);
		help = (Button) findViewById(R.id.help);
		server_txt = (TextView) findViewById(R.id.serverTitle);
		apprcalCount = (TextView) findViewById(R.id.approvalCount);
		RequestCount = (TextView) findViewById(R.id.RequestCount);
		try {
			Search_btn = (RelativeLayout) findViewById(R.id.serch_btn);
		} catch (Exception e) {
			// TODO: handle exception
		}
		req_btn = (RelativeLayout) findViewById(R.id.req_btn);
		catlog_btn = (RelativeLayout) findViewById(R.id.catlog_btn);
		approuval_btn = (RelativeLayout) findViewById(R.id.approval_btn);
		switch_img = (RelativeLayout) findViewById(R.id.logo_img);
	}

	@SuppressWarnings("unused")
	@Override
	protected void onResume() {
		super.onResume();
		onExit = true;
		Log.v("HOME SCREEN", "INVOKED");
		Log.v("Session", sessionMaintance + "");
		req_btn.setEnabled(true);
		approuval_btn.setEnabled(true);
		Search_btn.setEnabled(true);
		catlog_btn.setEnabled(true);

		if (isXlarge) {
			TextView userTxt = (TextView) findViewById(R.id.user_txtname);
			userTxt.setText(MyApplication.getUserID());
			ImageView userImage = (ImageView) findViewById(R.id.user_img);
		}

		SharedPreferences e1 = getSharedPreferences("SERENA",
				MODE_WORLD_READABLE);
		long lasttime = e1.getLong("backgroundtime", 0);

		long millisecond = (long) System.currentTimeMillis();
		long f = millisecond - lasttime;
		Log.v("value of f is" , Long.toString(f));
		int sec = (int) TimeUnit.MILLISECONDS.toMinutes(f);

		Log.v("value of sec is :" , Long.toString(sec));// 3608// 0

		if (sec > 60 && !MyApplication.noSplash) {
			MyApplication.noSplash = false;
			Log.v("session expired", "relogging in");
			Intent relogin = new Intent(Second_Btn.this, SplashScreen.class);
			relogin.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
			startActivity(relogin);
			finish();
		} else {

			if (Network_Available.hasConnection(ctx)) {
				MyApplication.noSplash = false;
				Log.v("Entered This loop", "entered");
				// long presentTime = (long)cal.get(Calendar.MILLISECOND);
				long timeDiff = millisecond - clickedtime;
				System.out.println("checked millisecond is:" + millisecond
						+ "_" + clickedtime + "_" + timeDiff);
				int secon = (int) TimeUnit.MILLISECONDS.toSeconds(timeDiff);
				Log.v("Time diff", secon + "");
				if (!powerButton) {
					powerButton = true;
					if (secon > 3) {
						int i = DBAdpter.BasicUrlCheck(MyApplication
								.getServer());
						if (i == 200) {
						} else {
							makediallog("Error",
									"A server with the specified hostname could not be found.");
						}
					}
					try {
						ArrayList<All_Approval_data_dto> ddd = new ArrayList<All_Approval_data_dto>();
						ddd = MyApplication.get_Filled_ApprovalList();

						ArrayList<All_Request_data_dto> dd = new ArrayList<All_Request_data_dto>();
						dd = MyApplication.get_Filled_RequestList();
						MyApplication.clear_approvals();
						MyApplication.clear_Request();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						new Catalog_New_Background(myInterface, ctx, "CALL1")
								.execute();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				} else {
					// ...do nothing...
				}
			} else {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			}
		}
	}

	@SuppressLint({ "WorldWriteableFiles", "WorldWriteableFiles" })
	@Override
	protected void onStop() {
		super.onStop();
		powerButton = false;
		onExit = false;
		MyApplication.setMenuScreenTimeout((long) System.currentTimeMillis());
		MyApplication.setlastTime((long) System.currentTimeMillis());
		isChildSelected = true;
		sessionMaintance = true;
		long movedBackground = (long) System.currentTimeMillis();
		if (loginRemembrance.equalsIgnoreCase("loggedtout")) {
			user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
			SharedPreferences.Editor editor = user_details.edit();
			editor.putLong("backgroundtime", 0);
			editor.putString("logout", "yes");
			editor.putString("name", "");
			editor.putString("password", "");
			editor.putString("lastActivity", "");
			editor.putLong("lasttime", 0);
			editor.commit();
		} else if (loginRemembrance.equalsIgnoreCase("notloggedout")) {
			user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
			SharedPreferences.Editor editor = user_details.edit();
			Log.v("the details", Uname + Pass + FirstloggedTime + Url);
			Log.v(MyApplication.getUserID() + MyApplication.getPassWord(), "");
			editor.putString("name", Uname);
			editor.putString("password", Pass);
			editor.putString("url", Url);
			editor.putLong("backgroundtime", movedBackground);
			editor.commit();
		}
		System.gc();
	}

	public void toast_maker() {
		Toast.makeText(getApplicationContext(), "No internet connection ...",
				Toast.LENGTH_SHORT).show();
	}

	public void makediallog() {

		final Dialog dialog = new Dialog(Second_Btn.this,
				R.style.FullHeightDialog);
		dialog.setContentView(R.layout.dialog);
		dialog.setCancelable(true);
		Button yes = (Button) dialog.findViewById(R.id.bYes);
		Button no = (Button) dialog.findViewById(R.id.bNo);
		Button info = (Button) dialog.findViewById(R.id.binfo);

		// if button is clicked, close the custom dialog
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginRemembrance = "loggedtout";
				System.gc();
				user_details = getSharedPreferences(prefname,
						MODE_WORLD_WRITEABLE);
				SharedPreferences.Editor editor = user_details.edit();
				editor.putString("logout", "yes");
				editor.putString("name", "");
				editor.putString("password", "");
				editor.putString("lastActivity", "");
				editor.putLong("backgroundtime", 0);
				editor.putLong("lasttime", 0);

				MyApplication.set_app_status(false);
				MyApplication.clearServiceResult();
				MyApplication.clearSearchrResult();
				MyApplication.clear_new_ServiceResult();
				MyApplication.clear_approvals();
				editor.commit();
				editor.apply();

				dialog.cancel();
				Intent x = new Intent(Second_Btn.this, MainSerenaActivity.class);
				x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				x.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

				startActivity(x);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				Second_Btn.this.finish();
			}
		});

		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				System.gc();
			}
		});

		info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					new foundVersion().execute();

				} catch (Exception e) {
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(titlte);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (disDialog == 5) {
					disDialog = 0;
				}
				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void thisloop() {
		Log.v("session not expired", "stay in same screen");

		Log.v("method called", "method called");
		long presentTime = (long) cal.get(Calendar.MILLISECOND);
		long timeDiff = presentTime - clickedtime;
		int refresh_seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeDiff);
		Log.v("Time diff", refresh_seconds + "");
		Log.v("entered", "true loop" + isChildSelected);

		if (!isChildSelected) {
			Log.v("entered", "true loop" + isChildSelected);
			if (Network_Available.hasConnection(ctx)) {

				if (refresh_seconds > 15) {
					Log.v("Present time", presentTime + "");
					Log.v("clicked time", clickedtime + "");
					Log.v("difference", presentTime - clickedtime + "");
					Log.v("Time diff", refresh_seconds + "");
					System.out.println("app status is:"
							+ MyApplication.app_status());
					if (MyApplication.app_status()) {
						MyApplication.clear_approvals();
						try {
							new Catalog_New_Background(myInterface, ctx,
									"CALL2").execute();
							// server_txt.setText(MyApplication.getServerName());
						} catch (Exception e) {
							// TODO: handle exception
						}
					} else {
						try {
							new Catalog_New_Background(myInterface, ctx,
									"CALL1").execute();
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				} else {
					Log.v(TAG, "Do Nothing");
				}
			} else {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			}
		} else {
			Log.v("from home or power button", "Launch");
		}
	}

	class foundVersion extends AsyncTask<Void, Void, String> {
		String result = null;

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				result = DBAdpter.getVersionfromHttp();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(final String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							if (result.contains("Webfiles")) {
								Toast.makeText(
										Second_Btn.this,
										"App : " + MyApplication.currentVersion
												+ "\n" + result,
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(
										Second_Btn.this,
										"App : " + MyApplication.currentVersion,
										Toast.LENGTH_LONG).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	}

}
