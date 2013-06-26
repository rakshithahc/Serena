package com.serana;

import java.io.ByteArrayOutputStream;

import java.text.SimpleDateFormat;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.webkit.SslErrorHandler;
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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled", "WorldReadableFiles",
		"WorldWriteableFiles", "DefaultLocale", "SimpleDateFormat" })
public class Approval extends ListActivity {
	private boolean isXlarge = false;
	private String TAG = "Screen";
	private WebView appApprovalWeb;
	private static final int PICK_IMAGE = 1;

	private boolean doubleBackToExitPressedOnce = false;
	public static boolean isfirstEntry;
	private Context ApproContext;
	private TextView Screen_title;
	private Button refresh, cancelButton;
	private String req_id1;
	ArrayAdapter<String> sortStrings;
	String[] reqspinner = { "Request Date", "Last Update", "Type", "Owner",
			"State" };
	EditText search;
	private int textlength;
	boolean status = false;
	private String searched;
	private boolean showoff = true;
	public static boolean isRefresh = false;
	private SharedPreferences user_details;
	private String prefname = "SERENA";
	public static boolean isrefreshRelease = false;
	static ArrayList<QueueItem> queueItemforApproval;
	Button attachment;
	public static boolean click_flag = false;
	RelativeLayout background, listbackground;
	LinearLayout listItem;
	int pos = 0;
	public static int disDialog = 0;
	ArrayList<All_Approval_Key_dto> alist = new ArrayList<All_Approval_Key_dto>();

	private String assosiatetoken;
	ArrayList<All_Approval_data_dto> list = new ArrayList<All_Approval_data_dto>();
	PullToRefreshView lvx;
	ListView lv;

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

	@SuppressLint({ "NewApi", "NewApi" })
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

			if (!Network_Available.hasConnection(ApproContext) && sec > 60) {

				Intent m = new Intent(Approval.this, MainSerenaActivity.class);
				m.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				this.finish();

			} else if (Network_Available.hasConnection(ApproContext)
					&& sec > 60) {

				Intent m = new Intent(Approval.this, SplashScreen.class);
				m.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				this.finish();

			} else if (!Network_Available.hasConnection(ApproContext)
					&& sec < 60) {
				System.out.println("id is:" + disDialog);
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}

			} else if (Network_Available.hasConnection(ApproContext)
					&& sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}

		isfirstEntry = true;
		isRefresh = true;
		System.out.println("first entry is true" + isfirstEntry);
		this.doubleBackToExitPressedOnce = true;

		if (isRefresh && !isXlarge && isfirstEntry) {
			if (Network_Available.hasConnection(ApproContext)) {
				System.out.println("call doin background");
				new doinbackground(ApproContext, "CUSDIALOG").execute();
			} else {
				System.out.println("list adapet");
				lv.setAdapter(new MyListAdapter(ApproContext, MyApplication
						.get_Filled_ApprovalList()));
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// TODO Auto-generated method stub
		// for notifing when user goes to backgroud
		MyApplication.setlastTime(System.currentTimeMillis());
		MyApplication.setMenuScreenTimeout(System.currentTimeMillis());
		// the following for when app goes to background and is killed my user
		// then to maintain auto reloggin
		System.out.println("millisecond is in approval class:"
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
		if (MyApplication.app_status()) {
			lv.setAdapter(new MyListAdapter(getApplicationContext(),
					MyApplication.get_Filled_ApprovalList()));
			refresh.setVisibility(View.VISIBLE);
			search.setVisibility(View.GONE);
			Screen_title.setVisibility(View.VISIBLE);
		} else {

			refresh.setVisibility(View.VISIBLE);
			search.setVisibility(View.GONE);
			Screen_title.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onStart();
		setContentView(R.layout.approval);
		queueItemforApproval = new ArrayList<QueueItem>();
		encode = new ArrayList<QueueString>();

		if (isXlarge) {
			appApprovalWeb = (WebView) findViewById(R.id.web_app);
			attachment = (Button) findViewById(R.id.attachment);
			background = (RelativeLayout) findViewById(R.id.relativeLayout1);
			listbackground = (RelativeLayout) findViewById(R.id.relativelayout3);
		}

		ApproContext = Approval.this;

		if (showoff) {
			if (!Network_Available.hasConnection(ApproContext)) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
				showoff = false;
			}
		}

		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setVisibility(View.GONE);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search.setText("");
				lv.setAdapter(new MyListAdapter(ApproContext, MyApplication
						.getApprovalList()));
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
				try {
					Screen_title.setText("Approvals ("
							+ MyApplication.get_Filled_ApprovalList().size()
							+ ")");
				} catch (Exception e) {
					// TODO: handle exception
				}
				cancelButton.setVisibility(View.GONE);
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		Screen_title = (TextView) findViewById(R.id.iratemapprovallogo);
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

		search = (EditText) findViewById(R.id.app_search);

		search.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (click_flag == false) {
					cancelButton.setVisibility(View.VISIBLE);
				}
				return false;
			}
		});

		search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (click_flag == false) {
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

					inputManager.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						int count = search_implement();
						if (count == 0) {
							makediallog("info", "Your search had no results.");
							lv.setAdapter(new MyListAdapter(ApproContext,
									MyApplication.getApprovalList()));
						} else {
							Screen_title.setText("Approvals(" + count + ")");

						}
					}
				}
				return false;
			}
		});

		lvx = (PullToRefreshView) findViewById(R.id.listapprovaldata);

		if (click_flag == false) {
			lv = lvx.getRefreshableView();
			lvx.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					if (click_flag == false) {
						assosiatetoken = MyApplication.getToken();
						if (Network_Available.hasConnection(ApproContext)) {
							new doinbackground(ApproContext, "block").execute();
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
			lv.setAdapter(new MyListAdapter(getApplicationContext(),
					MyApplication.get_Filled_ApprovalList()));
			lv.setVisibility(View.VISIBLE);
			callAdapterMethod();
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (!isXlarge) {
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View convertView,
						int position, long arg3) {
					if (click_flag == false) {
						getListItem(convertView, position, arg3);
					}

				}
			});
		}

		// refresh has been changed to home button
		refresh = (Button) findViewById(R.id.btn_refresh);
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (click_flag == false) {
					Second_Btn.isChildSelected = false;
					isfirstEntry = false;
					onBackPressed();
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
					Second_Btn.powerButton = false;
					Approval.this.finish();
				}
			}
		});
	}

	private void callAdapterMethod() {
		// TODO Auto-generated method stub
		if (isXlarge && lv.getAdapter().getCount() > 0)
			getListItem(lv.getAdapter().getView(1, null, null), 1,
					lv.getItemIdAtPosition(1));
	}

	private void callAdapterMethod(int i, int i2) {
		// TODO Auto-generated method stub
		pos = 0;
		if (i == 5) {
			if (isRefresh) {
				if (Network_Available.hasConnection(ApproContext)) {
					new doinbackground(ApproContext, "CUSDIALOG").execute();
				} else {
					lv.setAdapter(new MyListAdapter(ApproContext, MyApplication
							.get_Filled_ApprovalList()));
					callAdapterMethod();
				}
			}
		}
	}

	private void getListItem(View convertView, int i, long itemIdAtPosition) {
		// TODO Auto-generated method stub
		try {
			TextView ApproTile = (TextView) convertView
					.findViewById(R.id.apprItemTitle);
			TextView req_id = (TextView) convertView.findViewById(R.id.req_txt);
			TextView tableId = (TextView) convertView.findViewById(R.id.tabid);
			TextView projeId = (TextView) convertView
					.findViewById(R.id.projtId);

			String recordId = req_id.getText().toString();
			String proj = projeId.getText().toString();
			String tabId = tableId.getText().toString();
			String Approtitle = ApproTile.getText().toString();
			callWebUrlApproval(recordId, tabId, proj, Approtitle, i);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void callWebUrlApproval(String recordId, String tabId, String proj,
			String app_title, int i) {
		// TODO Auto-generated method stub
		try {
			String server = MyApplication.getServer();
			String encoded = MyApplication.getEncodedUserCred();
			Log.v("DATA", tabId + "\n" + recordId + "\n" + proj + "\n" + server
					+ "\n" + encoded);

			String req_web_url = server
					+ "/tmtrack/srcmobile/index.html?form=request&tableid="
					+ tabId + "&recordid=" + recordId + "&projectid=" + proj
					+ "&usrcredentials=" + encoded + "&WEBURL=" + server
					+ "&millisec=" + System.currentTimeMillis() + "&AUTH_TYPE="
					+ MyApplication.getLoggedInUrlType() + "&USER_LOGIN="
					+ MyApplication.getUserID() + "&USER_PWD="
					+ MyApplication.getPassWord();

			if (Network_Available.hasConnection(ApproContext)) {

				if (MyApplication.getLoggedInUrlType()
						.equalsIgnoreCase("type1")) {
					Log.v("type 1", "type 1 url........");

				}

				else if (MyApplication.getLoggedInUrlType().equalsIgnoreCase(
						"type2")) {
					Log.v("type 2", "type 2 url........");
					if (!isXlarge) {
						Intent req_go = new Intent(Approval.this,
								Approval_webview.class);
						req_go.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
								| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
						req_go.putExtra("finurl", req_web_url);
						req_go.putExtra("title", app_title);
						req_go.putExtra("position", i);
						MyApplication.setApp_url(req_web_url);
						MyApplication.setApp_title(app_title);
						startActivity(req_go);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);
					} else {
						appApprovalWeb.clearView();
						LoadWebViewUrl(req_web_url, i);
					}
				} else if (MyApplication.getLoggedInUrlType().equalsIgnoreCase(
						"type3")) {

					if (MyApplication.getEntryToWebForms()) {

						if (!isXlarge) {
							Intent req_go = new Intent(Approval.this,
									Approval_webview.class);
							req_go.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
									| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
							req_go.putExtra("finurl", req_web_url);

							int y = 0;
							try {
								y = MyApplication.getApprovalList().size();
							} catch (Exception e) {
							}
							req_go.putExtra("title", "");
							MyApplication.setApp_url(req_web_url);
							MyApplication.setApp_title("");

							req_go.putExtra("btnTextA", "Approvals(" + y + ")");
							startActivity(req_go);
							overridePendingTransition(R.anim.push_right_in,
									R.anim.push_right_out);
						} else {
							appApprovalWeb.clearView();
							LoadWebViewUrl(req_web_url, i);
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

	private void LoadWebViewUrl(String req_web_url, final int i) {
		// TODO Auto-generated method stub
		// final WebView wb = (WebView) findViewById(R.id.plan_web);
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			// mWebView is some WebView
			appApprovalWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		appApprovalWeb.getSettings().setJavaScriptEnabled(true);
		appApprovalWeb.loadUrl(req_web_url);
		appApprovalWeb.setWebViewClient(new WebViewClient() {

			private int webViewPreviousState;
			private final int PAGE_STARTED = 0x1;
			private final int PAGE_REDIRECTED = 0x2;

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				if (webViewPreviousState == PAGE_STARTED) {
					CustomDialog.removeDialog();
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				webViewPreviousState = PAGE_STARTED;
				if (CustomDialog.notShowing()) {
					CustomDialog.showProgressDialog(Approval.this, "", false);
				}
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
					}
				};
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					String urlNewString) {
				webViewPreviousState = PAGE_REDIRECTED;
				// appApprovalWeb.loadUrl(url);
				isRefresh = true;
				Log.v("webview control", urlNewString);

				if (urlNewString.contains("DELETEWEBVIEW")) {
					isrefreshRelease = true;
					callmethod(i);
				}

				if (urlNewString.equalsIgnoreCase("MASK://param=value")) {
					isrefreshRelease = true;
					attachment.setVisibility(View.VISIBLE);
					click_flag = true;
					addopqueueonBackground();
				}

				if (urlNewString.startsWith("http://")
						|| urlNewString.startsWith("https://")) {
					isrefreshRelease = true;
					attachment.setVisibility(View.GONE);
					click_flag = false;
					String[] urlforattachment = urlNewString
							.split("&FORMSUBMITOK=");
					System.out
							.println("split string is:" + urlforattachment[1]);
					if (urlforattachment[1].equalsIgnoreCase("false")) {
						queueItemforApproval.clear();
						index = 0;
					}
					view.clearView();
					removealphaOpeque();
					view.loadUrl(urlforattachment[0]);

				}

				if (urlNewString.contains("OKWEBVIEW://")) {
					isrefreshRelease = true;
					String[] urlreq = urlNewString.split("://");
					if (urlreq[1].contains("form=request")) {
						
					} else if (urlreq[1].contains("recordid")) {
						attachdataonserver(urlreq[1]);
					}
					attachment.setVisibility(View.GONE);
					view.clearView();
					callmethod(i);
				}
				return true;
			}

		});

	}

	protected void removealphaOpeque() {
		// TODO Auto-generated method stub
		listbackground.setAlpha(1f);
		background.setAlpha(1f);
		lv.setClickable(true);
		lvx.setClickable(true);
		refresh.setClickable(true);
		search.setClickable(true);
		search.setEnabled(true);
		lv.setEnabled(true);
		lvx.setEnabled(true);
		cancelButton.setClickable(true);
		cancelButton.setEnabled(true);
		refresh.setEnabled(true);
	}

	protected void addopqueueonBackground() {
		// TODO Auto-generated method stub
		lv.setClickable(false);
		lvx.setClickable(false);
		refresh.setClickable(false);
		search.setClickable(false);
		search.setEnabled(false);
		lv.setEnabled(false);
		lvx.setEnabled(false);
		refresh.setEnabled(false);
		cancelButton.setClickable(false);
		cancelButton.setEnabled(false);
		listbackground.setAlpha(0.5f);
		background.setAlpha(0.5f);
	}

	protected void callmethod(int i) {
		// TODO Auto-generated method stub
		removealphaOpeque();
		click_flag = false;
		appApprovalWeb.clearCache(true);
		appApprovalWeb.clearView();
		attachment.setVisibility(View.INVISIBLE);
		callAdapterMethod(5, i);
	}

	@SuppressLint("DefaultLocale")
	public int search_implement() {
		Log.v("Count", "");

		ArrayList<All_Approval_data_dto> sort_arrarList = MyApplication
				.getApprovalList();
		All_Approval_data_dto[] array = (All_Approval_data_dto[]) sort_arrarList
				.toArray(new All_Approval_data_dto[sort_arrarList.size()]);
		ArrayList<All_Approval_data_dto> d = new ArrayList<All_Approval_data_dto>();
		All_Approval_data_dto j;
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
						j = new All_Approval_data_dto();
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
		lv.setAdapter(new MyListAdapter(ApproContext, d));
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

	TextView req_id;
	TextView date;
	TextView owner;
	TextView state;
	TextView tableId;
	TextView displayId;
	TextView projeId;
	TextView App_item_title;

	public class MyListAdapter extends BaseAdapter {

		private ArrayList<All_Approval_data_dto> list;
		Context context;

		public MyListAdapter(Context mContext,
				ArrayList<All_Approval_data_dto> list) {
			// String list_size=MyApplication.getApprovalList().size()+"";
			Screen_title.setText("Approvals ("
					+ MyApplication.get_Filled_ApprovalList().size() + ")");
			this.context = mContext;
			this.list = list;
		}

		public int getCount() {
			int size = 0;
			try {
				size = list.size();
			} catch (Exception e) {
			}
			return size;
		}

		public All_Approval_data_dto getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			try {
				notifyDataSetChanged();

				LayoutInflater inflator = (LayoutInflater) context
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.custom_approval_data,
						null);
				listItem = (LinearLayout) convertView
						.findViewById(R.id.listitem_app);
				App_item_title = (TextView) convertView
						.findViewById(R.id.apprItemTitle);
				req_id = (TextView) convertView.findViewById(R.id.req_txt);
				date = (TextView) convertView.findViewById(R.id.date_txt);
				owner = (TextView) convertView.findViewById(R.id.owner_txt);
				state = (TextView) convertView.findViewById(R.id.state_txt);
				tableId = (TextView) convertView.findViewById(R.id.tabid);
				displayId = (TextView) convertView.findViewById(R.id.dispId);
				projeId = (TextView) convertView.findViewById(R.id.projtId);
				try {
					displayId.setText(list.get(position).displayId);
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					App_item_title.setText(list.get(position).title);
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					tableId.setText(list.get(position).tableId);
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					projeId.setText(list.get(position).projectId);
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					req_id.setText(list.get(position).requestId);
				} catch (Exception e) {
					// TODO: handle exception
				}

				try {

					date.setText(list.get(position).daysago);
				} catch (Exception e) {
					// TODO: handle exception
				}
				// if(list.get(position).owner.length()<10){
				try {
					if (list.get(position).owner.equalsIgnoreCase("(None)")) {
						owner.setText("--/");
					} else {
						owner.setText(list.get(position).owner + " / ");
					}
				} catch (Exception e) {
					owner.setText("--/");
				}

				try {
					state.setText(list.get(position).state);
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (isXlarge) {
					if (position == pos) {
						convertView
								.setBackgroundResource(R.drawable.listitemgreen);
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
									listItem.setBackgroundResource(R.drawable.listitemgreen);
								} else {
									listItem.setBackgroundResource(R.drawable.listitem_default);
								}
								System.out.println("pos is:" + pos + "_"
										+ position);
								String recordId = list.get(position).requestId;
								String proj = list.get(position).projectId;
								String tabId = list.get(position).tableId;
								String app_title = list.get(position).title;
								listItem.setBackgroundResource(R.drawable.listitemyellow);
								callWebUrlApproval(recordId, tabId, proj,
										app_title, position);
							}
						}
					});
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return convertView;
		}
	}

	class doinbackground extends AsyncTask<Void, Void, Void> {
		private String fi;
		private Context ctx;
		private ArrayList<All_Approval_data_dto> apprList;

		public doinbackground(Context c, String g) {
			ctx = c;
			new ArrayList<All_Approval_data_dto>();
			fi = g;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DialogBlock.showProgressDialog(ApproContext, "", false);
		//	refresh.setClickable(false);

		}

		@Override
		protected Void doInBackground(Void... params) {
			assosiatetoken = MyApplication.getToken();

			try {
				alist = DBAdpter.recursUserData(assosiatetoken);
				System.out.println("list of size is" + alist.size());
				for (int i = 0; i < alist.size(); i++) {
					MyApplication.clear_approvals();
				}

				for (int i = 0; i < alist.size(); i++) {
					req_id1 = alist.get(i).requestId;
					System.out.println("req id is" + i + "_" + req_id1);
					list = DBAdpter.approvalUserData(assosiatetoken, req_id1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Collections.sort(list, byDate_app);

			try {
				MyApplication.setApprovalList(list);
			} catch (Exception e) {
			}

			// onCreate(null);
			lvx.onRefreshComplete();
			DialogBlock.removeDialog();
			pos=0;
			lv.setAdapter(new MyListAdapter(getApplicationContext(),
					MyApplication.get_Filled_ApprovalList()));
			callAdapterMethod();
		//	refresh.setClickable(true);

		}

	}

	static final Comparator<All_Approval_data_dto> byDate_app = new Comparator<All_Approval_data_dto>() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

		public int compare(All_Approval_data_dto ord1,
				All_Approval_data_dto ord2) {
			java.util.Date d1 = null;
			java.util.Date d2 = null;

			try {
				d1 = sdf.parse(ord1.lastModifiedDate);
				d2 = sdf.parse(ord2.lastModifiedDate);

			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

			return (d1.getTime() > d2.getTime() ? -1 : 1); // descending
			// return (d1.getTime() > d2.getTime() ? 1 : -1); //ascending
		}
	};

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ApproContext);
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
		Intent intent = new Intent(Approval.this, SelectOption.class);
		intent.putExtra("value", 5);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		startActivityForResult(intent, PICK_IMAGE);
	}

	int index = 0;

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
							+ queueItemforApproval.size());
					index = queueItemforApproval.size();
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
			for (int i = 0; i < queueItemforApproval.size(); i++) {
				/**/
				QueueString en = new QueueString();
				if (queueItemforApproval.get(i).bitmap != null) {
					System.out.println("bitmap is:"
							+ queueItemforApproval.get(i).bitmap);
					en.path = convertintobase64(queueItemforApproval.get(i).bitmap);
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
		queueItemforApproval.clear();
	}

}
