package com.serana;

import java.util.ArrayList;

import com.serena.connection.Network_Available;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainSerenaActivity extends Activity {

	int ntlmResponseValue = 0;
	public static boolean fromOtherScreens = false;
	int WrongUnamePass = 0;
	private String TAG = "Screen";
	EditText uname;
	EditText pass;
	Button login;
	String strUname;
	String unamePass;
	String strPass;
	String unamepass;
	String uPass;
	String encPass;
	TextView reg_txt;
	MyApplication app;
	String passwde;
	ProgressDialog pd;
	Context loginContext;
	String serverUrl;
	EditText server;
	ToggleButton chkBox;
	private long FirstloggedTime;
	ProgressBar loginProgess;
	ArrayList<All_sencha_loginData_dto> list = new ArrayList<All_sencha_loginData_dto>();

	private SharedPreferences user_details;
	private String prefname = "SERENA";
	
	public static String SERVER_RESPONSE, TMTRACK_RESPONSE, WEBVIEW_RESPONSE,
			SEC_URL_CHECK = "NULL";

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called");

		Log.i(TAG, "Checking configuration");

		Configuration myConfig = new Configuration();
		myConfig.setToDefaults();
		myConfig = getResources().getConfiguration();

		// set the orientation according to phone screen size or phone layout
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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		System.exit(0);

	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		InitialiseIds();
		uname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (chkBox.isChecked()) {
					if (!server.getText().toString().contains(":")) {
						server.append(":443");
					}
				}
			}
		});

		pass.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (chkBox.isChecked()) {
					if (!server.getText().toString().contains(":")) {
						server.append(":443");
					}
				}
			}
		});

		server.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (chkBox.isChecked()) {
						if (!server.getText().toString().contains(":")) {
							server.append(":443");
						}
					}
					strUname = uname.getText().toString().trim();
					strPass = pass.getText().toString().trim();
					serverUrl = server.getText().toString().trim();

					// check the connectivity
					if (Network_Available.hasConnection(loginContext)) {
						if ((uname.getText().toString()).equals("")
								|| (pass.getText().toString()).equals("")
								|| server.getText().toString()
										.equalsIgnoreCase("")) {
							makediallog("info", "Please fill all the fields");
						} else {
							if (chkBox.isChecked()) {
								String strServer = server.getText().toString()
										.trim();
								serverUrl = "https://" + strServer;
								Log.v("data", serverUrl);
							} else {
								String strServer = server.getText().toString()
										.trim();
								serverUrl = "http://" + strServer;
								Log.v("data", serverUrl);
							}
							try {
								new loginBackground().execute();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						loginProgess.setVisibility(View.GONE);
						makediallog("No network connection",
								"You must be connected to the internet to use this app");
					}
				}
				return false;
			}
		});

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (android.os.Build.VERSION.SDK_INT > 8) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		SERVER_RESPONSE = "NULL";
		TMTRACK_RESPONSE = "NULL";

		chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					if (server.getText().toString().contains(":")) {
						Log.v("contains ", ":");
						// chkBox.setEnabled(false);
					} else {
						chkBox.setBackgroundResource(R.drawable.checkbox_selected);
						server.append(":443");
					}
				} else if (!isChecked) {
					chkBox.setBackgroundResource(R.drawable.checkbox);
					String h = server.getText().toString().replace(":443", "");
					server.setText(h);
				}

			}
		});

		loginContext = MainSerenaActivity.this;

		// Following code is used for remember login functionality
		SharedPreferences e = getSharedPreferences("SERENALOGIN", MODE_PRIVATE);
		String username = e.getString("name", "");
		String password = e.getString("password", "");
		String serverx = e.getString("url", "");
		String checkboxState = e.getString("checkbox", "");
		Log.v("CheckBox", checkboxState);
		if (checkboxState.equalsIgnoreCase("checked")) {
			chkBox.setChecked(true);
		} else if (checkboxState.equalsIgnoreCase("notchecked")) {
			chkBox.setChecked(false);
		}

		if (username.equalsIgnoreCase("")) {
			uname.setText("bill");
			pass.setText("bill");
			server.setText("srcmobile.serena.com");
		} else {
			uname.setText(username);
			pass.setText(password);
			if (serverx.contains("http://")) {
				serverx = serverx.replace("http://", "");
			} else if (serverx.contains("https://")) {
				serverx = serverx.replace("https://", "");
			}
			server.setText(serverx);

		}

		login.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("ShowToast")
			public void onClick(View v) {

				strUname = uname.getText().toString().trim();
				strPass = pass.getText().toString().trim();
				serverUrl = server.getText().toString().trim();

				if (Network_Available.hasConnection(loginContext)) {
					if ((uname.getText().toString()).equals("")
							|| (pass.getText().toString()).equals("")
							|| server.getText().toString().equalsIgnoreCase("")) {
						makediallog("info", "Please fill all the fields");
					} else {
						// Check box Checked--HTTPS server
						if (chkBox.isChecked()) {
							String strServer = server.getText().toString()
									.trim();
							serverUrl = "https://" + strServer;
							Log.v("data", serverUrl);
						} else {
							// Check box not Checked--HTTP server
							String strServer = server.getText().toString()
									.trim();
							serverUrl = "http://" + strServer;
							Log.v("data", serverUrl);
						}
						MyApplication.setUserID(uname.getText().toString());
						MyApplication.setPassWord(pass.getText().toString());
						MyApplication.setServer(server.getText().toString());
						new loginBackground().execute();
					}
				} else {
					loginProgess.setVisibility(View.GONE);
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			}
		});

	}

	private void InitialiseIds() {
		// TODO Auto-generated method stub
		chkBox = (ToggleButton) findViewById(R.id.checkBox1);
		loginProgess = (ProgressBar) findViewById(R.id.loginProgress);
		loginProgess.setVisibility(View.GONE);
		uname = (EditText) findViewById(R.id.user_edt_lg);
		pass = (EditText) findViewById(R.id.pass_edt_lg);
		login = (Button) findViewById(R.id.login);
		server = (EditText) findViewById(R.id.server);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (fromOtherScreens) {
			loginProgess.setVisibility(View.GONE);
			// when user returns from any other screen and Internet is failed
			// then this will make a dialog...
			makediallog("No network connection",
					"You must be connected to the internet to use this app");
		}

	}

	class loginBackground extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DialogBlock.showProgressDialog(loginContext, "", false);
			loginProgess.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... params) {

			int i = DBAdpter.BasicUrlCheck(serverUrl);
			if (i == 200) {
				Log.v("basic url", i + "");
				int bgmap = DBAdpter.BasicUrlCheck(serverUrl
						+ "/tmtrack/images/shell/srp/mobilelibrary/bgmap.txt");
				if (bgmap == 200) {
					int index = DBAdpter.BasicUrlCheck(serverUrl
							+ "/tmtrack/srcmobile/index.html");
					Log.v("index.html", index + "");
					if (index == 200) {
						// bgmap and index.txt conditions satisfied so do type 1
						// ,2, 3 logings
						type123logins();
					} else {
						// message for not finding index.html
						removeDialog_invisible(
								"info",
								"SRC Mobile is not installed on the specified server.  Please contact your administrator.");
					}
				} else {
					// message for not finding bgmap.txt
					Log.v("Bgmap", bgmap + "");
					if (MyApplication.isCheckforAuthScheme()) {
						Log.v("NTLM Message", "using NTLM");
						removeDialog_invisible(
								"NTLM Error",
								"Authentication failed."+"\n"+"Please contact your administrator.");
					} else {
						removeDialog_invisible(
								"info",
								"One or more files were not found on the SRC Mobile server."+"\n"+"Please contact your administrator.");
					}
				}

			} else {
				// if server IP is wrong then this message....
				removeDialog_invisible("Error",
						"A server with the specified hostname could not be found.");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(loginContext);
		builder.setTitle(titlte);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		try {
			dialog.show();
		} catch (WindowManager.BadTokenException e) {
			// TODO: handle exception
		}
	}

	public void type123logins() {

		String firstLogin = serverUrl
				+ "/tmtrack/tmtrack.dll?shell=srp&JSONPage&command=getssotoken";
		int serverStatus = DBAdpter.FirstBasicUrlStatuCheck(firstLogin);
		if (serverStatus != 200) {
			// when server in not available then this message,no need of showing
			// this but even though.let keep it
			removeDialog_invisible("Error",
					"A server with the specified hostname could not be found.");

		} else {
			// making ssoToken Variable to null so that if any older token is
			// there flush it.
			String ssoToken = null;
			try {
				// Try to log in through 1st url...
				ssoToken = DBAdpter.FirstBasicUrlCheck(firstLogin);
				Log.v(TAG, ssoToken);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ssoToken == null) {
				// if 1st url fails to do log in,then use type 2nd type login
				Log.v("SSO TOKEN", "null $$ user fails to login...");
				String ssotokensec = serverUrl
						+ "/tmtrack/tmtrack.dll?shell=srp&JSONPage&command=getssotoken&ttauthuid="
						+ strUname + "&ttauthpwd=" + strPass;
				String ssotoken2 = null;
				try {
					ssotoken2 = DBAdpter.SecondBasicUrlCheck(ssotokensec);
					Log.v("Token from 2nd URL", ssotoken2);
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (ssotoken2 == null) {
					// Even if 2nd type is failed to do the log in,the final
					// login
					// firing final url
					Log.v("FIRING FINAL URL", "final url check");
					allOtherLogin1();
				} else {
					Log.v(TAG, "from login type 2");
					// on success full log in of type 2 url...
					// base 64 encoding for username and password
					encodeCredentials();
					setcredentials("type2", strUname, strPass, serverUrl,
							ssotoken2);
					callIntent();
				}
			} else {

				Log.v(TAG, "type 1 login");
				// on success full log in of type 2 url...
				// base 64 encoding for username and password
				encodeCredentials();
				setcredentials("type1", strUname, strPass, serverUrl, ssoToken);
				callIntent();
			}
		}
	}

	private void setcredentials(String string, String strUname2,
			String strPass2, String serverUrl2, String ssotoken2) {
		// TODO Auto-generated method stub
		Log.v(TAG, "set the credentials");
		MyApplication.setLoggedInUrlType(string);
		MyApplication.setUserID(strUname2);
		MyApplication.setPassWord(strPass2);
		MyApplication.setServer(serverUrl2);
		MyApplication.setToken(ssotoken2);
	}

	public void allOtherLogin1() {
		Log.v("type3", "type3 login");
		String alfssoauthntoken = null;
		try {
			// Making this call we are storing SSO token in connection class.
			list = DBAdpter.loginInUser(strUname, strPass, serverUrl);
		} catch (Exception e) {
		}

		String sso = MyApplication.getToken();

		try {
			if (sso.equalsIgnoreCase("NULL")) {
				// if status is ok but SSO token is null, we say a follows
				removeDialog_invisible("Authentication Failed",
						"Username or Password is wrong.");

			} else {
				// for status 200 ,if server is not responding we say a follows
				if (!SERVER_RESPONSE.equalsIgnoreCase("200")) {
					// Toast.makeText(getApplicationContext(),
					// "SERVER NOT FOUND",Toast.LENGTH_SHORT).show();
					removeDialog_invisible("Error",
							"A server with the specified hostname could not be found.");
				} else {
					SERVER_RESPONSE = "NULL";
					// Server found and SSO token as well,
					// by setting SERVER RESPONCE=NULL Such that static
					// resetting static reference...

					for (int i = 0; i < list.size(); i++) {
						alfssoauthntoken = list.get(i).serena_token;

					}
					MyApplication.setToken(alfssoauthntoken);
					MyApplication.setServer(serverUrl);
					// Log.v("token", MyApplication.getToken());
					String url = MyApplication.getServer()
							+ "/tmtrack/images/shell/srp/mobilelibrary/bgmap.txt";
					Log.v("My server Check...", url);
					// Checking tmtrack validation against server...
					String c = DBAdpter.tmtrackcheck(url, "tmtr");

					if (c.equalsIgnoreCase("200")) {
						Log.v("TMTRACK RESPONSE", c);
						// Checking status of webURl validation against server
						String webviewurl = MyApplication.getServer()
								+ "/tmtrack/srcmobile/index.html";

						String h = DBAdpter.tmtrackcheck(webviewurl, "web");

						if (h.equalsIgnoreCase("200")) {
							MyApplication.setEntryToWebForms(true);
							try {
								if (MyApplication.getToken().equalsIgnoreCase(
										"NULL")) {
									// If SERVER_IP is correct(server passes all
									// above conditions)and entered username and
									// password are wrong
									makediallog("Authentication Failed",
											"Username or Password is wrong.");
								} else {
									// Every thing is correct.Move to next
									// activity
									MyApplication.setLoggedInUrlType("type3");
									callIntent();
								}
							} catch (Exception e) {
								// even on getting Null pointer Exception, If
								// SERVER_IP is correct(server passes all above
								// conditions)and entered username and password
								// are wrong
								removeDialog_invisible("Authentication Failed",
										"Username or Password is wrong.");
							}
						} else {
							// else loop for webURl validation against server
							try {
								if (!MyApplication.getToken().equalsIgnoreCase(
										"NULL")) {
									removeDialog_invisible(
											"Authentication Failed",
											"Username or Password is wrong.");
								} else {
									MyApplication.setEntryToWebForms(false);
									callIntent();
									MyApplication.setLoggedInUrlType("type3");
								}
							} catch (Exception e) {
								removeDialog_invisible("Authentication Failed",
										"Username or Password is wrong.");
							}
						}
					} else {
						c = "NULL";
						removeDialog_invisible(
								"info",
								"One or more files were not found on the SRC Mobile server.  Please contact your administrator.");
					}
				}
			}
		} catch (Exception e) {
			removeDialog_invisible("Authentication Failed",
					"Username or Password is wrong.");
		}
	}

	private void removeDialog_invisible(final String string,
			final String string2) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				DialogBlock.removeDialog();
				loginProgess.setVisibility(View.GONE);
				makediallog(string, string2);
			}
		});
	}

	private void callIntent() {
		// TODO Auto-generated method stub
		Log.v(TAG, "call intent");
		FirstloggedTime = System.currentTimeMillis();
		Log.v("first time login is:", Long.toString(FirstloggedTime));
		// save the login details into shared preferences
		login_storage(strUname, strPass, FirstloggedTime, serverUrl);
		Intent in = new Intent(MainSerenaActivity.this, Second_Btn.class);
		in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		in.putExtra("uname", strUname);
		in.putExtra("pass", strPass);
		in.putExtra("url", serverUrl);
		in.putExtra("dialog", "block");
		MyApplication.noSplash = true;
		startActivity(in);
		this.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	public void encodeCredentials() {
		String addencPass = strUname + ":" + strPass;
		encPass = Base64.encodeToString(addencPass.getBytes(), Base64.DEFAULT
				| Base64.URL_SAFE | Base64.NO_WRAP);
		MyApplication.setEncodedUserCred(encPass);
	}

	public void serverChecks() {

	}


	@SuppressLint("WorldWriteableFiles")
	public void login_storage(String name, String pass, long time, String url) {
		Log.v("first time login is:" , Long.toString(time));
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		Log.v(name + "\n" + pass + "\n" + time + "\n" + url, "are creds");
		editor.putString("name", name);
		editor.putString("password", pass);
		editor.putLong("lasttime", FirstloggedTime);
		editor.putString("url", url);
		editor.commit();
		MyApplication.setUserID(name);
		MyApplication.setPassWord(pass);
		MyApplication.setServer(url);
		// MyApplication.setToken();
	}

}
