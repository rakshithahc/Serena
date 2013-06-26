package com.serana;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.serena.connection.Network_Available;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

@SuppressLint({ "WorldReadableFiles", "NewApi" })
public class SplashScreen extends Activity {
	private String TAG = " Splash Screen";

	ArrayList<All_sencha_loginData_dto> list;
	public static String SERVER_RESPONSE, TMTRACK_RESPONSE, WEBVIEW_RESPONSE;
	ImageView img;
	Context splashCtx;
	int f;
	private String username, password, server;
	long lasttime, presentTime, backTime;
	String logStates;

	@SuppressLint("NewApi")
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called and Checking configuration");
		Configuration myConfig = new Configuration();
		myConfig.setToDefaults();
		myConfig = getResources().getConfiguration();
		switch (myConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {

		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			Log.v(TAG, "Screen size is extra large");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;

		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			Log.v(TAG, "Screen size is large");
			break;

		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			Log.v(TAG, "Screen size is normal");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;

		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			Log.v(TAG, "Screen size is small");
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;

		case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
			Log.v(TAG, "Screen size is undefined");
			break;

		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		list = new ArrayList<All_sencha_loginData_dto>();
		// make a context of splash screen
		splashCtx = SplashScreen.this;

		// checking for thr sdk version in device. If its greater than 9
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		SERVER_RESPONSE = "NULL";
		TMTRACK_RESPONSE = "NULL";

		if (Network_Available.hasConnection(splashCtx)) {
			// it is available
			Thread n = new Thread() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						Log.v(TAG, "Splash Class enrty");

						// fetching the login details from shared preference
						SharedPreferences e = getSharedPreferences("SERENA",
								MODE_WORLD_READABLE);
						username = e.getString("name", "");
						password = e.getString("password", "");
						server = e.getString("url", "");
						lasttime = e.getLong("lasttime", 0);
						backTime = e.getLong("backgroundtime", 0);

						logStates = e.getString("logout", "");

						MyApplication.setUserID(username);
						MyApplication.setPassWord(password);
						MyApplication.setServer(server);
						Log.v("back n login time:", Long.toString(backTime)
								+ "\n" + Long.toString(lasttime));

						Log.v(TAG, "name:" + username + "\nPassword" + password
								+ "\nLasttime" + lasttime + "\nserver" + server);
						long oneHour = backTime - lasttime;
						Log.v(TAG, "One Hour" + oneHour + "");
						if (username.equalsIgnoreCase("")) {
							Log.v(TAG, "ALL CREDENTIALS ARE EMPTY");
							callMainSerenaIntent();
						} else {
							int f = (int) TimeUnit.MILLISECONDS
									.toMinutes(oneHour);

							if (f > 2) {
								callUrlforLogin();
							} else {
								callUrlforLogin();
							}
						}
					}
				}
			};
			n.start();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(splashCtx);
			builder.setTitle("No network connection");
			builder.setMessage("You must be connected to the internet to use this app");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent gotologin = new Intent(SplashScreen.this,
									MainSerenaActivity.class);
							gotologin
									.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
											| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
							startActivity(gotologin);
							SplashScreen.this.finish();
						}
					});
			// AlertDialog dialog = builder.create();
			builder.setCancelable(false);
			builder.show();
		}
	}

	protected void callMainSerenaIntent() {
		// TODO Auto-generated method stub
		Intent v = new Intent(SplashScreen.this, MainSerenaActivity.class);
		v.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		startActivity(v);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		SplashScreen.this.finish();
	}

	protected void callUrlforLogin() {
		// TODO Auto-generated method stub
		String firstLogin = MyApplication.getServer()
				+ "/tmtrack/tmtrack.dll?shell=srp&JSONPage&command=getssotoken";
		String ssoToken = DBAdpter.FirstBasicUrlCheck(firstLogin);
		if (ssoToken == null) {
			Log.v(TAG, "SSO TOKEN null $$ user fails to login...");
			// fireing second url
			String ssotokensec = MyApplication.getServer()
					+ "/tmtrack/tmtrack.dll?shell=srp&JSONPage&command=getssotoken&ttauthuid="
					+ username + "&ttauthpwd=" + password;
			Log.v(TAG, ssotokensec);
			String ssotoken2 = DBAdpter.SecondBasicUrlCheck(ssotokensec);
			if (ssotoken2 == null) {
				// firing final url
				list = DBAdpter.loginInUser(username, password, server);
				allOtherLogin1();
			} else {
				Log.v(TAG, "from login type 2");
				encodeCredentials();
				callIntent("type2", ssotoken2);
			}
		} else {
			Log.v(TAG, "type 1 login");
			callIntent("type1", ssoToken);
		}
	}

	protected void callIntent(String string, String ssoToken) {
		// TODO Auto-generated method stub
		if (ssoToken != null) {
			Log.v(TAG, "sso token is not null");
			MyApplication.setLoggedInUrlType(string);
			MyApplication.setToken(ssoToken);
		} else {
			MyApplication.setLoggedInUrlType(string);
		}

		Intent gotoHome = new Intent(SplashScreen.this, Second_Btn.class);
		gotoHome.putExtra("uname", MyApplication.getUserID());
		gotoHome.putExtra("pass", MyApplication.getPassWord());
		gotoHome.putExtra("url", MyApplication.getServer());
		gotoHome.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(gotoHome);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		SplashScreen.this.finish();
	}

	public void encodeCredentials() {
		String addencPass = MyApplication.getUserID() + ":"
				+ MyApplication.getPassWord();
		String encPass = Base64.encodeToString(addencPass.getBytes(),
				Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP);
		MyApplication.setEncodedUserCred(encPass);
	}

	@SuppressWarnings("unused")
	public void allOtherLogin1() {
		String alfssoauthntoken = null;
		if (!SERVER_RESPONSE.equalsIgnoreCase("200")) {
			runonThread("Error",
					"A server with the specified hostname could not be found.");
		} else {
			SERVER_RESPONSE = "NULL";
			for (int i = 0; i < list.size(); i++) {
				alfssoauthntoken = list.get(i).serena_token;
			}
			String url = MyApplication.getServer()
					+ "/tmtrack/images/shell/srp/mobilelibrary/bgmap.txt";
			Log.v("My server Check...", url);
			String c = DBAdpter.tmtrackcheck(url, "tmtr");
			Log.v(c, c);
			if (c.equalsIgnoreCase("200")) {
				Log.v("TMTRACK RESPONSE", c);
				String webviewurl = MyApplication.getServer()
						+ "/tmtrack/srcmobile";
				String h = DBAdpter.tmtrackcheck(webviewurl, "web");
				Log.v("Finally", h);
				if (h.equalsIgnoreCase("200")) {
					MyApplication.setEntryToWebForms(true);
					try {
						if (MyApplication.getToken().equalsIgnoreCase("NULL")) {
							runonThread("Authentication Failed",
									"Username or Password is wrong.");
						} else {
							callIntent("type3", "");
						}
					} catch (Exception e) {
						runonThread("Authentication Failed",
								"Username or Password is wrong.");
					}
				} else {
					try {
						if (!MyApplication.getToken().equalsIgnoreCase("NULL")) {
							runonThread("Authentication Failed",
									"Username or Password is wrong.");
						} else {
							MyApplication.setEntryToWebForms(false);
							callIntent("type3", "");
						}
					} catch (Exception e) {
						runonThread("Authentication Failed",
								"Username or Password is wrong.");
					}
				}
			} else {
				c = "NULL";
				runonThread(
						"info",
						"One or more files were not found on the SRC Mobile server.  Please contact your administrator.");
			}
		}
	}

	private void runonThread(final String string, final String string2) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				makediallog(string, string2);
			}
		});
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(splashCtx);
		builder.setTitle(titlte);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Intent gotoHome = new Intent(SplashScreen.this,
						MainSerenaActivity.class);
				gotoHome.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(gotoHome);
				SplashScreen.this.finish();
			}
		});
		builder.show();
	}

	public static int getHoursFromMillis(long milliseconds) {
		return (int) ((milliseconds / (1000 * 60 * 60)) % 24);
	}

}
