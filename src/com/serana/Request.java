package com.serana;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.serena.connection.Network_Available;
import com.serena.pull.PullToRefreshView;
import com.serena.pull.PullToRefreshBase.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled", "ResourceAsColor",
		"DefaultLocale", "WorldReadableFiles", "WorldWriteableFiles" })
public class Request extends ListActivity {
	private boolean isXlarge = false;
	public static boolean isfirstEntry;
	private String TAG = "Screen";
	private WebView reqWebView;
	private boolean doubleBackToExitPressedOnce = false;
	private LinearLayout listItem;
	public static boolean isrefreshRelease = false;
	private boolean showoff = true;
	RelativeLayout background, listbackground;
	Context reqctx;
	Button refesh, cancelBtn;
	private TextView Screen_title;
	Spinner sortSpinner;
	boolean status = false;
	EditText search;
	Button attachment;
	public static int disDialog = 0;
	ArrayAdapter<String> sortStrings;
	String[] reqspinner = { "Request Date", "Last Update", "Type", "Owner",
			"State" };
	int textlength = 0;
	String searched;
	PullToRefreshView lvx;
	private SharedPreferences user_details;
	private String prefname = "SERENA";
	public static boolean isRefresh = false;
	static ArrayList<QueueItem> queueItemforRequest;
	boolean click_flag = false;
	int stateToSave = 0;

	@SuppressWarnings("unused")
	private String assosiatetoken;
	@SuppressWarnings("unused")
	private ArrayList<All_Request_data_dto> list = new ArrayList<All_Request_data_dto>();
	ListView lv;

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called");

		Log.i(TAG, "Checking configuration");

		Configuration myConfig = new Configuration();
		myConfig.setToDefaults();
		myConfig = getResources().getConfiguration();

		switch (myConfig.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
		case Configuration.SCREENLAYOUT_SIZE_XLARGE: {
			isXlarge = true;
			Log.v(TAG, "Screen size is extra large");
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

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("on resume call for request screen");
		if (isfirstEntry) {
			System.out.println("first entry is true");
			SharedPreferences e1 = getSharedPreferences("SERENA",
					MODE_WORLD_READABLE);
			long lasttime = e1.getLong("backgroundtime", 0);

			long millisecond = (long) System.currentTimeMillis();
			System.out.println("millisecond is" + millisecond);
			System.out.println("last millisecond is:" + lasttime);
			long f = millisecond - lasttime;
			int sec = (int) TimeUnit.MILLISECONDS.toMinutes(f);
			if (!Network_Available.hasConnection(reqctx) && sec > 60) {
				Intent m = new Intent(Request.this, MainSerenaActivity.class);
				m.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				this.finish();
			} else if (Network_Available.hasConnection(reqctx) && sec > 60) {
				Intent m = new Intent(Request.this, SplashScreen.class);
				m.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				this.finish();

			} else if (!Network_Available.hasConnection(reqctx) && sec < 60) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			} else if (Network_Available.hasConnection(reqctx) && sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}

		isfirstEntry = true;
		isRefresh = true;
		System.out.println("first entry is true" + isfirstEntry);
		this.doubleBackToExitPressedOnce = true;

		if (isRefresh && !isXlarge && isfirstEntry) {
			System.out.println("call doin background");
			if (Network_Available.hasConnection(reqctx)) {
				System.out.println("do in background");
				new doinbackground(reqctx, "CUSDIALOG").execute();
			} else {
				System.out.println("list adapet");
				lv.setAdapter(new MyListAdapter(Request.this, MyApplication
						.get_Filled_RequestList()));
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		MyApplication.setlastTime(System.currentTimeMillis());
		MyApplication.setMenuScreenTimeout(System.currentTimeMillis());
		// the following for when app goes to background and is killed my user
		// then to maintain auto reloggin
		System.out.println("millisecond is in request class:"
				+ MyApplication.getLastTime());
		long movedBackground = System.currentTimeMillis();
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.commit();
		System.out.println("first entry ");
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			Second_Btn.isChildSelected = false;
			Second_Btn.powerButton = false;
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			this.finish();

			return;
		}
		this.doubleBackToExitPressedOnce = true;
		if (MyApplication.req_status()) {
			lv.setAdapter(new MyListAdapter(Request.this, MyApplication
					.getRequestList()));
			search.setVisibility(View.GONE);
			refesh.setVisibility(View.VISIBLE);
		} else {
			search.setVisibility(View.GONE);
			refesh.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onStart();
		setContentView(R.layout.request);
		queueItemforRequest = new ArrayList<QueueItem>();
		System.out.println("on create call");
		encode = new ArrayList<QueueString>();
		if (isXlarge) {
			reqWebView = (WebView) findViewById(R.id.web_req);
			attachment = (Button) findViewById(R.id.attachment);
			background = (RelativeLayout) findViewById(R.id.relativeLayout1);
			listbackground = (RelativeLayout) findViewById(R.id.relativelayout3);
		}
		reqctx = Request.this;

		if (showoff) {
			if (!Network_Available.hasConnection(reqctx)) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
				showoff = false;
			}
		}

		cancelBtn = (Button) findViewById(R.id.cancelbtn);
		cancelBtn.setVisibility(View.GONE);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search.setText("");
				lv.setAdapter(new MyListAdapter(Request.this, MyApplication
						.getRequestList()));
				try {
					pos = 0;
					if (isXlarge && lv.getAdapter().getCount() > 0)
						getListItem(lv.getAdapter().getView(1, null, null), 1,
								lv.getItemIdAtPosition(1));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				try {
					Screen_title.setText("Requests ("
							+ MyApplication.getRequestList().size() + ")");
				} catch (Exception e) {
				}
				cancelBtn.setVisibility(View.GONE);
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		Screen_title = (TextView) findViewById(R.id.iratemlogo);
		// this has put because on click on sencha button it calls on create so
		// to maintain refresh while coming from
		// webview and avoid refresh while coming from home screen
		if (isrefreshRelease) {
			isRefresh = true;

		} else {
			isRefresh = false;
		}

		// isfirstEntry = false;

		if (isXlarge) {
			attachment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					callintentforattachment();
				}
			});
		}

		search = (EditText) findViewById(R.id.req_search);

		search.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (click_flag == false) {
					cancelBtn.setVisibility(View.VISIBLE);
				}
				return false;
			}
		});

		search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (click_flag == false) {
					InputMethodManager inputManager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

					inputManager1.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						int count = search_implement();
						if (count == 0) {
							makediallog("info", "Your search had no results.");
							lv.setAdapter(new MyListAdapter(Request.this,
									MyApplication.getRequestList()));
						} else {
							Screen_title.setText("Requests(" + count + ")");
						}
					}
				}
				return false;
			}
		});

		
		lvx = (PullToRefreshView) findViewById(R.id.listrequestdata);
		if (click_flag == false) {
			lv = lvx.getRefreshableView();

			lvx.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					if (click_flag == false) {
						lv.setVisibility(View.VISIBLE);

						assosiatetoken = MyApplication.getToken();
						if (Network_Available.hasConnection(reqctx)) {
							try {
								new doinbackground(reqctx, "block").execute();
							} catch (Exception e) {
							}
						} else {
							if (disDialog == 0) {
								disDialog = 5;
								makediallog("No network connection",
										"You must be connected to the internet to use this app");
							}
							lvx.onRefreshComplete();
						}
					}
				}
			});
		}

		try {
			lv.setAdapter(new MyListAdapter(Request.this, MyApplication
					.get_Filled_RequestList()));
			lv.setVisibility(View.VISIBLE);
			callAdpaterMethod();
			// no_data.setVisibility(View.GONE);
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (!isXlarge) {
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					if (click_flag == false) {
						getListItem(view, position, arg3);
					}

				}
			});
		}

		sortSpinner = (Spinner) findViewById(R.id.spin_request);
		refesh = (Button) findViewById(R.id.but_refresh);
		sortStrings = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, reqspinner);
		sortSpinner.setAdapter(sortStrings);

		refesh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (click_flag == false) {
					Second_Btn.isChildSelected = false;
					isfirstEntry = false;
					onBackPressed();
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					Second_Btn.powerButton = false;
					Request.this.finish();
				}
			}
		});
	}

	private void callAdpaterMethod() {
		// TODO Auto-generated method stub
		if (isXlarge && lv.getAdapter().getCount() > 0)
			getListItem(lv.getAdapter().getView(1, null, null), 1,
					lv.getItemIdAtPosition(1));
	}

	private void callAdpaterMethod(int i, int i2) {
		// TODO Auto-generated method stub
		pos = 0;
		if (i == 5) {

			if (isRefresh) {
				if (Network_Available.hasConnection(reqctx)) {
					new doinbackground(reqctx, "CUSDIALOG").execute();
				} else {
					lv.setAdapter(new MyListAdapter(Request.this, MyApplication
							.get_Filled_RequestList()));
					callAdpaterMethod();
				}
			}
		}
	}

	private void getListItem(View view, int i, long itemIdAtPosition) {
		// TODO Auto-generated method stub
		try {
			TextView reqTitle = (TextView) view.findViewById(R.id.titleReq);
			TextView recId = (TextView) view.findViewById(R.id.req_txt);
			TextView projId = (TextView) view.findViewById(R.id.dispId);
			TextView tableId = (TextView) view.findViewById(R.id.tableid);
			TextView projectIdUrl = (TextView) view
					.findViewById(R.id.projectId);

			String recordId = recId.getText().toString();
			String proj = projectIdUrl.getText().toString();
			String tabId = tableId.getText().toString();
			String req_title = reqTitle.getText().toString();

			callWebUrlRequest(recordId, tabId, proj, req_title, i);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void callWebUrlRequest(String recordId, String tabId, String proj,
			String req_title2, int i) {
		// TODO Auto-generated method stub
		try {
			String server = MyApplication.getServer();
			String encoded = MyApplication.getEncodedUserCred();
			Log.v("ids", "\n table id" + tabId + "record id\n" + recordId
					+ "\nproject id" + proj + "\n" + server);
			Log.v("recordId.............", recordId);
			String req_web_url = server
					+ "/tmtrack/srcmobile/index.html?form=request&tableid="
					+ tabId + "&recordid=" + recordId + "&projectid=" + proj
					+ "&usrcredentials=" + encoded + "&WEBURL=" + server
					+ "&millisec=" + System.currentTimeMillis() + "&AUTH_TYPE="
					+ MyApplication.getLoggedInUrlType() + "&USER_LOGIN="
					+ MyApplication.getUserID() + "&USER_PWD="
					+ MyApplication.getPassWord();

			Log.v("url", req_web_url);
			if (Network_Available.hasConnection(reqctx)) {
				if (MyApplication.getLoggedInUrlType()
						.equalsIgnoreCase("type1")) {
					Log.v("type 1", "type 1 url........");
				}

				else if (MyApplication.getLoggedInUrlType().equalsIgnoreCase(
						"type2")) {
					Log.v("type 2", "type 2 url........");
					if (!isXlarge) {
						Intent req_go = new Intent(Request.this,
								Request_webview.class);
						req_go.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
								| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
						MyApplication.setReq_url(req_web_url);
						MyApplication.setReq_title(req_title2);
						req_go.putExtra("finurl", req_web_url);
						req_go.putExtra("title", req_title2);
						req_go.putExtra("position", i);
						startActivity(req_go);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);
					} else {
						reqWebView.clearView();
						loadWebView(req_web_url, i);
					}

				} else if (MyApplication.getLoggedInUrlType().equalsIgnoreCase(
						"type3")) {

					if (MyApplication.getEntryToWebForms()) {
						Log.v("type 3", "type 3 url........");
						if (!isXlarge) {
							Intent req_go = new Intent(Request.this,
									Request_webview.class);
							req_go.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
									| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
							req_go.putExtra("finurl", req_web_url);
							int y = 0;
							try {
								y = MyApplication.getRequestList().size();
							} catch (Exception e) {
							}
							MyApplication.setReq_url(req_web_url);
							MyApplication.setReq_title("");
							req_go.putExtra("title", "");
							req_go.putExtra("btnTextR", "Requests(" + y + ")");
							startActivity(req_go);
							overridePendingTransition(R.anim.push_right_in,
									R.anim.push_right_out);
						} else {
							reqWebView.clearView();
							loadWebView(req_web_url, i);
						}

					} else {
						makediallog(
								"info",
								"Your Serena Request Center service is not up to date. Please contact your administrator.");

					}
				}
			} else {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected void loadWebView(String req_web_url, final int i) {
		// TODO Auto-generated method stub
		status = true;

		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			// mWebView is some WebView
			reqWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		reqWebView.getSettings().setJavaScriptEnabled(true);
		//Toast.makeText(Request.this, req_web_url, Toast.LENGTH_LONG).show();
//		reqWebView.clearCache(true);
		reqWebView.loadUrl(req_web_url);
		// wb.loadUrl("http://192.168.1.21:8080/test.html");

		reqWebView.setWebViewClient(new WebViewClient() {
			private int webViewPreviousState;
			private final int PAGE_STARTED = 0x1;
			private final int PAGE_REDIRECTED = 0x2;

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					String urlNewString) {
				webViewPreviousState = PAGE_REDIRECTED;
				// wb.loadUrl(urlNewString);
				isRefresh = true;
				Log.v("webview control", urlNewString);
			//	Toast.makeText(Request.this, urlNewString, Toast.LENGTH_LONG).show();
				if (urlNewString.contains("DELETEWEBVIEW")) {
					isrefreshRelease = true;
					callmethod(i);
				}else	if (urlNewString.equalsIgnoreCase("MASK://param=value")) {
				//	Toast.makeText(Request.this, "getting mask ", Toast.LENGTH_LONG).show();
					isrefreshRelease = true;
					attachment.setVisibility(View.VISIBLE);
					click_flag = true;
					addopqueueonBackground();
				}else if (urlNewString.contains("OKWEBVIEW://")) {
					isrefreshRelease = true;
			//	Toast.makeText(Request.this, "getting okwebview", Toast.LENGTH_LONG).show();
					String[] urlreq = urlNewString.split("://");
					if (urlreq[1].contains("form=request")) {
					} else if (urlreq[1].contains("recordid")) {
						attachdataonserver(urlreq[1]);
					}
					attachment.setVisibility(View.GONE);
					view.clearView();
					callmethod(i);
				} else{
				//	Toast.makeText(Request.this, "getting http or https", Toast.LENGTH_LONG).show();
				//if (urlNewString.startsWith("http://")
					//	|| urlNewString.startsWith("https://")) {
					isrefreshRelease = true;
					attachment.setVisibility(View.GONE);
					click_flag = false;
					String[] urlforattachment = urlNewString
							.split("&FORMSUBMITOK=");
					System.out
							.println("split string is:" + urlforattachment[1]);
					if (urlforattachment[1].equalsIgnoreCase("false")) {
						queueItemforRequest.clear();
						index = 0;
					}
					view.clearView();
					view.clearCache(true);
					removealphaOpeque();
					view.loadUrl(urlforattachment[0]);
				}

				
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webViewPreviousState = PAGE_STARTED;
				if (CustomDialog.notShowing()) {
					CustomDialog.showProgressDialog(reqctx, "", false);
				}
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {

					}
				};
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (webViewPreviousState == PAGE_STARTED) {
					CustomDialog.removeDialog();
				}
			}
		});
		
		
		reqWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Request.this)
						.setTitle("Alert")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				new AlertDialog.Builder(Request.this)
						.setTitle("javaScript dialog")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								}).create().show();
				return true;
			};

			@Override
			public void onReceivedTitle(WebView view, String title) {
				setTitle(title);
				super.onReceivedTitle(view, title);
			}

		});

	}

	protected void removealphaOpeque() {
		// TODO Auto-generated method stub		
		listbackground.setAlpha(1f);
		background.setAlpha(1f);
		lv.setClickable(true);
		lvx.setClickable(true);
		refesh.setClickable(true);
		search.setClickable(true);
		search.setEnabled(true);
		lv.setEnabled(true);
		lvx.setEnabled(true);
		cancelBtn.setClickable(true);
		cancelBtn.setEnabled(true);
		refesh.setEnabled(true);
	}

	protected void addopqueueonBackground() {
		// TODO Auto-generated method stub
		lv.setClickable(false);
		lvx.setClickable(false);
		refesh.setClickable(false);
		search.setClickable(false);
		search.setEnabled(false);
		lv.setEnabled(false);
		lvx.setEnabled(false);
		refesh.setEnabled(false);
		cancelBtn.setClickable(false);
		cancelBtn.setEnabled(false);
		listbackground.setAlpha(0.5f);
		background.setAlpha(0.5f);
	}

	protected void callmethod(int i) {
		// TODO Auto-generated method stub
		reqWebView.clearCache(true);
		reqWebView.clearView();
		System.out.println("call method");
		removealphaOpeque();
		attachment.setVisibility(View.INVISIBLE);
		click_flag = false;
		callAdpaterMethod(5, i);
	}

	public int search_implement() {
		ArrayList<All_Request_data_dto> sort_arrarList = MyApplication
				.getRequestList();
		All_Request_data_dto[] array = (All_Request_data_dto[]) sort_arrarList
				.toArray(new All_Request_data_dto[sort_arrarList.size()]);
		ArrayList<All_Request_data_dto> d = new ArrayList<All_Request_data_dto>();
		All_Request_data_dto j;
		for (int y = 0; y < array.length; y++) {
			Log.v("The data at " + y, array[y].title + array[y].requestId);
		}

		searched = search.getText().toString();
		textlength = search.getText().length();
		for (int i = 0; i < array.length; i++) {
			if (textlength <= array[i].title.length()) {
				try {
					if (searched.equalsIgnoreCase(array[i].title.toString()
							.substring(0, textlength))
							|| array[i].title.toString().toLowerCase()
									.contains(searched.toLowerCase())
							|| array[i].state.toString().toLowerCase()
									.contains(searched.toLowerCase())
							|| searched.equalsIgnoreCase(array[i].state
									.toString().substring(0, textlength))
							|| array[i].displayId.toString().toLowerCase()
									.contains(searched.toLowerCase())
							|| searched.equalsIgnoreCase(array[i].displayId
									.toString().substring(0, textlength))
							|| array[i].owner.toString().toLowerCase()
									.contains(searched.toLowerCase())
							|| searched.equalsIgnoreCase(array[i].owner
									.toString().substring(0, textlength))) {
						j = new All_Request_data_dto();
						j.title = array[i].title;
						j.requestId = array[i].requestId;
						j.owner = array[i].owner;
						j.state = array[i].state;
						j.submitDate = array[i].submitDate;
						j.lastModifiedDate = array[i].lastModifiedDate;
						j.tableId = array[i].tableId;
						j.projectId = array[i].projectId;
						j.daysago = array[i].daysago;
						j.displayId = array[i].displayId;
						d.add(j);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		lv.setAdapter(new MyListAdapter(Request.this, d));
		try {
			pos = 0;
			if (isXlarge && lv.getAdapter().getCount() > 0) {
				getListItem(lv.getAdapter().getView(1, null, null), 1,
						lv.getItemIdAtPosition(1));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return d.size();
	}

	TextView req_Title;
	TextView req_id;
	TextView date;
	TextView owner;
	TextView state;
	TextView tableID;
	TextView displayId;
	TextView projectId;
	int pos = 0;

	public class MyListAdapter extends BaseAdapter {
		@SuppressWarnings("unused")
		private int selectedPos;
		private ArrayList<All_Request_data_dto> list_sea;
		Activity context;

		public MyListAdapter(Activity mContext,
				ArrayList<All_Request_data_dto> listre) {
			this.list_sea = listre;
			this.context = mContext;
			try {
				Screen_title.setText("Requests ("
						+ MyApplication.getRequestList().size() + ")");
			} catch (Exception e) {
			}
		}

		public MyListAdapter() {

		}

		public int getCount() {
			int size = 0;
			try {
				size = list_sea.size();
			} catch (Exception e) {
				// TODO: handle exception
			}
			return size;
		}

		public All_Request_data_dto getItem(int position) {
			return list_sea.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// if (convertView == null) {
			try {

				notifyDataSetChanged();

				LayoutInflater inflator = (LayoutInflater) context
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.custom_request_data,
						null);

				// int colorPos = position % image_ids.length;
				// if(list.get(position)!=null){
				listItem = (LinearLayout) convertView
						.findViewById(R.id.listitem_req);

				req_Title = (TextView) convertView.findViewById(R.id.titleReq);
				req_id = (TextView) convertView.findViewById(R.id.req_txt);
				date = (TextView) convertView.findViewById(R.id.date_txt);
				owner = (TextView) convertView.findViewById(R.id.owner_txt);
				state = (TextView) convertView.findViewById(R.id.state_txt);
				tableID = (TextView) convertView.findViewById(R.id.tableid);
				displayId = (TextView) convertView.findViewById(R.id.dispId);
				projectId = (TextView) convertView.findViewById(R.id.projectId);

				try {
					projectId.setText(list_sea.get(position).projectId);

					req_Title.setText(list_sea.get(position).title);

					tableID.setText(list_sea.get(position).tableId);

					displayId.setText(list_sea.get(position).displayId);

					req_id.setText(list_sea.get(position).requestId);

					date.setText(list_sea.get(position).daysago);

					if (list_sea.get(position).owner.equalsIgnoreCase("(None)")) {
						owner.setText("--/");
					} else {
						owner.setText(list_sea.get(position).owner + "  /  ");
					}
					state.setText(list_sea.get(position).state);
					if (isXlarge) {
						if (position == pos) {
							convertView
									.setBackgroundResource(R.drawable.listitemyellow);
						} else {
							convertView
									.setBackgroundResource(R.drawable.listitem_default);
						}
						convertView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if (click_flag == false) {
									pos = position;
									if (position == pos) {
										listItem.setBackgroundResource(R.drawable.listitemyellow);
									} else {
										listItem.setBackgroundResource(R.drawable.listitem_default);
									}
									System.out.println("pos is:" + pos);
									String recordId = list_sea.get(position).requestId;
									String proj = list_sea.get(position).projectId;
									String tabId = list_sea.get(position).tableId;
									String req_title = list_sea.get(position).title;
									listItem.setBackgroundResource(R.drawable.listitemyellow);
									callWebUrlRequest(recordId, tabId, proj,
											req_title, position);
								}
							}
						});
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return convertView;
		}
	}

	class doinbackground extends AsyncTask<Void, Void, Void> {

		private Context ctx;
		private String fl;
		private ArrayList<All_Request_data_dto> listvvv;

		public doinbackground(Context c, String di) {
			ctx = c;
			listvvv = new ArrayList<All_Request_data_dto>();
			fl = di;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (fl.equalsIgnoreCase("CUSDIALOG")) {
				CustomDialog.showProgressDialog(reqctx, "", false);
			} else {
				DialogBlock.showProgressDialog(reqctx, "", false);
			}
		//	refesh.setClickable(false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String assotoken = MyApplication.getToken();

			try {
				Log.v("LOADING URL for request class",
						MyApplication.getReqURL());

				listvvv = DBAdpter.requestUserData(assotoken,
						MyApplication.getReqURL());
			} catch (Exception e) {
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Collections.sort(listvvv, byDate);
			try {
				MyApplication.setRequstList(listvvv);
			} catch (Exception e) {
				e.printStackTrace();
			}
			lvx.onRefreshComplete();

			CustomDialog.removeDialog();
			DialogBlock.removeDialog();
			pos=0;
			lv.setAdapter(new MyListAdapter(Request.this, MyApplication
					.getRequestList()));
			callAdpaterMethod();
			//refesh.setClickable(true);
		}

	}

	static final Comparator<All_Request_data_dto> byDate = new Comparator<All_Request_data_dto>() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

		public int compare(All_Request_data_dto ord1, All_Request_data_dto ord2) {
			java.util.Date d1 = null;
			java.util.Date d2 = null;
			try {
				d1 = sdf.parse(ord1.lastModifiedDate);
				d2 = sdf.parse(ord2.lastModifiedDate);

			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return (d1.getTime() > d2.getTime() ? -1 : 1); // descending
			// return (d1.getTime() > d2.getTime() ? 1 : -1); //ascending
		}
	};

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(reqctx);
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

	public void callintentforattachment() {
		Intent intent = new Intent(Request.this, SelectOption.class);
		intent.putExtra("value", 4);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		startActivityForResult(intent, PICK_IMAGE);
	}

	int index = 0;
	private static final int PICK_IMAGE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					System.out
							.println("activity result called for catalog webview class");
					// new accesstheimagesfromUri().execute();
					System.out.println("Catalog list size is"
							+ queueItemforRequest.size());
					index = queueItemforRequest.size();
					callbitmapmethod();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			break;
		}
	}

	ArrayList<QueueString> encode;

	private void callbitmapmethod() {
		// TODO Auto-generated method stub
		try {
			encode.clear();
			for (int i = 0; i < queueItemforRequest.size(); i++) {
				/**/
				QueueString en = new QueueString();
				if (queueItemforRequest.get(i).bitmap != null) {
					System.out.println("bitmap is:"
							+ queueItemforRequest.get(i).bitmap);
					en.path = convertintobase64(queueItemforRequest.get(i).bitmap);
					System.out.println("path is :" + en.path);
					// Bitmap bitmap = decodeBase64(en.path, i);

				} else {
					System.out.println("bitmap is null");
				}
				encode.add(en);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private String convertintobase64(Bitmap bitmap2) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap2.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT | Base64.NO_WRAP);
		System.out.println("encoded string is:" + temp);
		if (temp.contains(" ")) {
			System.out.println("space is there");
			temp.replaceAll(" ", "");
		}

		if (temp.contains("\n")) {
			System.out.println("space is there");
			temp.replaceAll("\n", "");
		}

		if (temp.contains("<br>")) {
			System.out.println("space is there");
			temp.replaceAll("<br>", "");
		}
		Log.e("LOOK", temp);
		return temp;
	}

	protected void attachdataonserver(String urlNewString) {
		// TODO Auto-generated method stub
		switch (index) {
		case 1:
			dotheattachmentpart(1, urlNewString);
			break;
		case 2:
			dotheattachmentpart(2, urlNewString);
			break;
		case 3:
			dotheattachmentpart(3, urlNewString);
			break;

		default:
			// dotheattachmentpart(0, urlNewString);
			break;
		}
	}

	private void dotheattachmentpart(int value, String urlNewString) {
		// TODO Auto-generated method stub

		for (int i = 0; i < value; i++) {
			System.out.println("attachment" + i);
			JSONObject object = new JSONObject();
			try {
				object.put("filename", "Attachment");
				object.put("displayname", "Attachment");
				object.put("showAsImage", true);
				object.put("isUnrestricted", false);
				// content value contains base64 value of bitmap
				System.out.println("encoded value is:" + encode.get(i).path);
				object.put("content", encode.get(i).path);
				// doin background thread for request
				int j = DBAdpter.sendRequestforAttachment(object, urlNewString,
						MyApplication.getToken());
				if (j == 5) {
					Log.v("Results", "Successfully Uploaded");
				} else if (j == 1) {
					Log.v("Results", "Error");
					makediallog("Error", "CannotUpload the file");
				} else {
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		encode.clear();
		index = 0;
		queueItemforRequest.clear();
	}
}