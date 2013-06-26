package com.serana;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.serana.PopoverView.PopoverViewDelegate;
import com.serena.connection.Network_Available;

@SuppressLint({ "NewApi", "WorldReadableFiles", "WorldWriteableFiles" })
public class TabletCatalog extends FragmentActivity implements
		PopoverViewDelegate {
	RelativeLayout rootView;
	private LayoutInflater layoutInflater;
	private View popupView;
	private PopupWindow popupWindow;
	public boolean isXlarge = false;
	public static boolean isfirstEntry;
	public static int disDialog = 0;
	ProgressBar catalogProgress;
	private SharedPreferences user_details;
	private String prefname = "SERENA";
	private String TAG = "Screen";
	private GridView gridOfServices;
	private String[] headings;
	private Drawable[] bitmap;
	private String[] summary;
	private String[] projectName;
	private String[] discri;
	private String[] servicetabUUID;
	private Button testOnce;
	TextView searchCount, noSearchResults;
	private Button home;
	private String action_colour_codes[] = { "#1f6fbf", "#1bff5f", "#bf1f67",
			"#471fbf", "#1fb5bf", "#cb2f5c", "#e37136", "#dbc41e", "#7cca1d",
			"#48c7e7", "#f68500", "#36f907", "#009ed6", "#00356e", "#e9490b" };

	private int static_images_ids[] = { R.drawable.mock1, R.drawable.mock2,
			R.drawable.mock3, R.drawable.mock4, R.drawable.mock5,
			R.drawable.mock6, R.drawable.mock7, R.drawable.mock8,
			R.drawable.mock9, R.drawable.mock10, R.drawable.mock11,
			R.drawable.mock12, R.drawable.mock13, R.drawable.mock14,
			R.drawable.mock15 };
	boolean search_flag = false;
	private static int image_ids[];

	TextView head, No_Service, catTitleXlarge;
	private Context catContext;
	String catalog_id;
	boolean mSpinner = true;
	EditText search;
	ListView lv;
	Button btn;
	EditText search_bar;
	Button cancel_button;
	RelativeLayout root;
	String req;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.gc();
		Second_Btn.isChildSelected = false;
		Second_Btn.powerButton = false;
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		TabletCatalog.this.finish();
	}

	@Override
	protected void onStart() {
		super.onStart();

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

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		try {
			home.setClickable(true);

		} catch (Exception e) {
		}
		try {
			btn.setEnabled(true);

		} catch (Exception e) {
		}
		if (isfirstEntry) {
			SharedPreferences e1 = getSharedPreferences("SERENA",
					MODE_WORLD_READABLE);
			long lasttime = e1.getLong("backgroundtime", 0);

			long millisecond = (long) System.currentTimeMillis();
			System.out.println("millisecond is" + millisecond);
			System.out.println("last millisecond is:" + lasttime);
			long f = millisecond - lasttime;
			int sec = (int) TimeUnit.MILLISECONDS.toMinutes(f);
			if (!Network_Available.hasConnection(catContext) && sec > 60) {
				Intent m = new Intent(TabletCatalog.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(catContext) && sec > 60) {
				Intent m = new Intent(TabletCatalog.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available.hasConnection(catContext) && sec < 60) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			} else if (Network_Available.hasConnection(catContext) && sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}

		isfirstEntry = true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onStart called" + "Checking configuration");

		setContentView(R.layout.catagory_new);
		onStart();
		initialiseIds();
		catContext = TabletCatalog.this;

		try {
			Compare n = new Compare();
			if (isXlarge) {

				image_ids = n.backgrounds("xlarge");
			} else {

				image_ids = n.backgrounds("medium");
			}
		} catch (Exception e) {
			image_ids = static_images_ids;
		}

		if (isXlarge) {
			search_bar.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					search_bar.setCursorVisible(true);
					cancel_button.setVisibility(View.VISIBLE);
					return false;
				}
			});

			cancel_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					search_flag = false;
					search_bar.setText("");
					cancel_button.setVisibility(View.GONE);
					noSearchResults.setVisibility(View.GONE);
					gridOfServices.setVisibility(View.VISIBLE);
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					try {
						gridOfServices.setAdapter(new MyAdapter_new(
								TabletCatalog.this));
						catTitleXlarge.setText(MyApplication.getCatelog_names()
								.get(0));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			search_bar
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView arg0,
								int actionId, KeyEvent arg2) {
							// TODO Auto-generated method stub
							// call json here......
							search_flag = true;
							String server = MyApplication.getServer();
							Log.v("URL", server);
							MyApplication.clearSearchrResult();
							req = search_bar.getText().toString();

							MyApplication.clearSearchrResult();

							System.out.println("request url is:" + req);
							if (req.equalsIgnoreCase(" ")) {
								req.replace(" ", "%20");
							}
							final String urlstr = server
									+ "/sdf/servicedef/services/"
									+ MyApplication.getAdvance_id()
									+ "?name=%25" + req
									+ "%25&sortby=weight,name";
							System.out.println("url is:" + urlstr);
							InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

							inputManager.hideSoftInputFromWindow(
									getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
							if (actionId == EditorInfo.IME_ACTION_SEARCH
									|| actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION) {
								if (Network_Available.hasConnection(catContext)) {
									try {

										Log.v("url", urlstr);
										// new search_back(urlstr).execute();
										cancel_button.setVisibility(View.GONE);
										catTitleXlarge.setText(req);
										new searchinBackground(urlstr)
												.execute();
									} catch (Exception e) {
									}
								} else {
									if (disDialog == 0) {
										disDialog = 5;
										makediallog("No network connection",
												"You must be connected to the internet to use this app");
									}
								}
							}
							return false;
						}
					});
		}

		try {
			if (isXlarge) {
				gridOfServices
						.setAdapter(new MyAdapter_new(TabletCatalog.this));
				catTitleXlarge.setText(MyApplication.getCatelog_names().get(0));
			} else {
				lv.setAdapter(new MyAdapter_new(TabletCatalog.this));
			}
			int a = MyApplication.getProjectName_new().size();
			int b = MyApplication.getService_images_new().size();
			int c = MyApplication.getService_names_new().size();
			int d = MyApplication.getService_discrpition_new().size();
			int e = MyApplication.gettableUUID_new().size();
			int f = MyApplication.getService_summary_new().size();
			Log.v("seee.............", a + b + c + d + e + f + "");
		} catch (Exception e) {
			e.printStackTrace();
			lv.setVisibility(View.GONE);
		}

		if (isXlarge) {
			layoutInflater = (LayoutInflater) catContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			popupView = layoutInflater.inflate(R.layout.pop_layout, null);
		}

		isfirstEntry = false;
		No_Service.setVisibility(View.GONE);

		home.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Second_Btn.isChildSelected = false;
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				onBackPressed();
				TabletCatalog.this.finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				Second_Btn.powerButton = false;
			}
		});

		testOnce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isXlarge) {
					search_flag = false;
					search_bar.setText("");
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					createPopUpMenu(v);
				} else {
					customlist();

				}
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				onItemmClickMethod(arg0, arg1, arg2, arg3);
			}

		});

		if (isXlarge) {
			gridOfServices.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (search_flag == false) {
						onItemmClickMethod(arg0, arg1, arg2, arg3);
					} else {
						onSearchItemClickMethod(arg0, arg1, arg2, arg3);
					}
				}
			});
		}

		if (MyApplication.catelog_status().equalsIgnoreCase("NULL")) {
			Log.v("NO DATA", "SORRY.........");
		} else {

		}
		ArrayList<String> g = MyApplication.getCatelog_names();

		try {
			head.setText(g.get(0));
			catalog_id = g.get(0);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void initialiseIds() {
		// TODO Auto-generated method stub
		if (isXlarge) {

			gridOfServices = (GridView) findViewById(R.id.gridcontainer);
			root = (RelativeLayout) findViewById(R.id.example);
			cancel_button = (Button) findViewById(R.id.cancelbtn);
			search_bar = (EditText) findViewById(R.id.cat_search);
			noSearchResults = (TextView) findViewById(R.id.noSearchResults);
			noSearchResults.setVisibility(View.GONE);
			gridOfServices.setVisibility(View.VISIBLE);
		}

		home = (Button) findViewById(R.id.home);
		No_Service = (TextView) findViewById(R.id.no_service_text);
		testOnce = (Button) findViewById(R.id.testbtn);
		catalogProgress = (ProgressBar) findViewById(R.id.catalogProgress);
		catalogProgress.setVisibility(View.GONE);

		lv = (ListView) findViewById(R.id.cat_new);
		head = (TextView) findViewById(R.id.headind);
		catTitleXlarge = (TextView) findViewById(R.id.cat_title);
	}

	protected void onItemmClickMethod(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		btn = (Button) arg1.findViewById(R.id.list_btn);
		btn.setEnabled(false);
		final TextView text = (TextView) arg1.findViewById(R.id.service_head);

		final int colorPos = arg2 % image_ids.length;
		isfirstEntry = false;
		if (Network_Available.hasConnection(catContext)) {

			if (MyApplication.get_new_tag() == 0) {
				// for What_new
				ArrayList<String> actionURL = MyApplication.getAction_new_Url();
				ArrayList<String> actionTYPE = MyApplication
						.get_new_ActionType();
				// String aT = null;
				Log.v("Action Type", actionTYPE.get(arg2));
				if (actionTYPE.get(arg2).equalsIgnoreCase("2")) {
					Intent gotoweb = new Intent(TabletCatalog.this,
							Catalog_webview.class);
					gotoweb.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					Bitmap bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[arg2])
							.getBitmap();

					String final_service_url = actionURL.get(arg2);

					gotoweb.putExtra("finurl", final_service_url);
					gotoweb.putExtra("image", bitmap);
					gotoweb.putExtra("heading", text.getText().toString());
					gotoweb.putExtra("actColour", action_colour_codes[colorPos]);

					startActivity(gotoweb);

					if (!isXlarge) {
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);
					}

				} else {
					String proj_name = null, tabid = null, finalProjectId = null, finalTableId = null;

					for (int i = 0; i < projectName.length; i++) {
						proj_name = projectName[arg2];
						tabid = servicetabUUID[arg2];
					}
					Log.v(proj_name, tabid);
					String assotoken = MyApplication.getToken();
					ArrayList<All_Service_webforms_data_key_dto> f = new ArrayList<All_Service_webforms_data_key_dto>();

					f = DBAdpter
							.servicewebformKeys(assotoken, proj_name, tabid);
					Log.v("Size", "" + f.size());

					for (int j = 0; j < f.size(); j++) {
						try {
							Log.v("URL", f.get(j).projectId + "   "
									+ f.get(j).tableId);
							finalProjectId = f.get(j).projectId;
							finalTableId = f.get(j).tableId;
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

					String server = MyApplication.getServer();
					String enccoded = MyApplication.getEncodedUserCred();
					Bitmap bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[arg2])
							.getBitmap();
					Intent gotoweb = new Intent(TabletCatalog.this,
							Catalog_webview.class);
					gotoweb.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

					long millisecond = System.currentTimeMillis();
					String final_service_url = server
							+ "/tmtrack/srcmobile/index.html?form=catalog&tableuuid="
							+ finalTableId + "&projectname=" + finalProjectId
							+ "&usrcredentials=" + enccoded + "&WEBURL="
							+ server + "&millisec=" + millisecond
							+ "&AUTH_TYPE="
							+ MyApplication.getLoggedInUrlType()
							+ "&USER_LOGIN=" + MyApplication.getUserID()
							+ "&USER_PWD=" + MyApplication.getPassWord();
					Log.v("URL WITH TIME", final_service_url);
					gotoweb.putExtra("finurl", final_service_url);
					gotoweb.putExtra("image", bitmap);
					gotoweb.putExtra("heading", text.getText().toString());
					gotoweb.putExtra("actColour", action_colour_codes[colorPos]);

					startActivity(gotoweb);
					overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);

				}

			} else {
				// for all other catalog
				Log.v("All other", "For all other");
				ArrayList<String> actionURL = MyApplication.getAction_Url();
				ArrayList<String> actionTYPE = MyApplication.getAction_Type();
				// String aT = null;
				Log.v("Action Type", actionTYPE.get(arg2));
				if (actionTYPE.get(arg2).equalsIgnoreCase("2")) {
					Intent gotoweb = new Intent(TabletCatalog.this,
							Catalog_webview.class);
					gotoweb.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					Bitmap bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[arg2])
							.getBitmap();

					String final_service_url = actionURL.get(arg2);

					gotoweb.putExtra("finurl", final_service_url);
					gotoweb.putExtra("image", bitmap);
					gotoweb.putExtra("heading", text.getText().toString());
					gotoweb.putExtra("actColour", action_colour_codes[colorPos]);

					startActivity(gotoweb);
					overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);

				} else {

					String proj_name = null, tabid = null, finalProjectId = null, finalTableId = null;

					for (int i = 0; i < projectName.length; i++) {
						proj_name = projectName[arg2];
						tabid = servicetabUUID[arg2];
					}
					Log.v(proj_name, tabid);
					String assotoken = MyApplication.getToken();
					ArrayList<All_Service_webforms_data_key_dto> f = new ArrayList<All_Service_webforms_data_key_dto>();

					f = DBAdpter
							.servicewebformKeys(assotoken, proj_name, tabid);
					Log.v("Size", "" + f.size());

					for (int j = 0; j < f.size(); j++) {
						try {
							Log.v("URL", f.get(j).projectId + "   "
									+ f.get(j).tableId);
							finalProjectId = f.get(j).projectId;
							finalTableId = f.get(j).tableId;
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

					String server = MyApplication.getServer();
					String enccoded = MyApplication.getEncodedUserCred();
					Bitmap bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[arg2])
							.getBitmap();

					long millisecond = System.currentTimeMillis();
					Intent gotoweb = new Intent(TabletCatalog.this,
							Catalog_webview.class);
					gotoweb.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					String final_service_url = server
							+ "/tmtrack/srcmobile/index.html?form=catalog&tableuuid="
							+ finalTableId + "&projectname=" + finalProjectId
							+ "&usrcredentials=" + enccoded + "&WEBURL="
							+ server + "&millisec=" + millisecond
							+ "&AUTH_TYPE="
							+ MyApplication.getLoggedInUrlType()
							+ "&USER_LOGIN=" + MyApplication.getUserID()
							+ "&USER_PWD=" + MyApplication.getPassWord();
					Log.v("URL WITH TIME", final_service_url);
					gotoweb.putExtra("finurl", final_service_url);
					gotoweb.putExtra("image", bitmap);
					gotoweb.putExtra("heading", text.getText().toString());
					gotoweb.putExtra("actColour", action_colour_codes[colorPos]);

					startActivity(gotoweb);
					overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);
				}
			}
		} else {
			if (disDialog == 0) {
				disDialog = 5;
				makediallog("No network connection",
						"You must be connected to the internet to use this app");
			}
		}

	}

	protected void onSearchItemClickMethod(AdapterView<?> arg0, View arg1,
			int arg2, long arg3) {
		// TODO Auto-generated method stub
		final int colorPos = arg2 % image_ids.length;
		if (Network_Available.hasConnection(TabletCatalog.this)) {

			String proj_name = null, tabid = null, finalProjectId = null, finalTableId = null;
			// ArrayList<String> pro_name = MyApplication.getProjectName();
			// ArrayList<String> tab_id = MyApplication.gettableUUID();
			for (int i = 0; i < projectName.length; i++) {
				proj_name = projectName[arg2];
				tabid = servicetabUUID[arg2];
			}
			Log.v(proj_name, tabid);
			String assotoken = MyApplication.getToken();
			ArrayList<All_Service_webforms_data_key_dto> f = new ArrayList<All_Service_webforms_data_key_dto>();
			f = DBAdpter.servicewebformKeys(assotoken, proj_name, tabid);

			for (int j = 0; j < f.size(); j++) {
				try {
					Log.v("URL", f.get(j).projectId + "   " + f.get(j).tableId);
					finalProjectId = f.get(j).projectId;
					finalTableId = f.get(j).tableId;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			final TextView text = (TextView) arg1
					.findViewById(R.id.service_head);

			ArrayList<String> actionURL = MyApplication.getSearch_actionType();
			ArrayList<String> actionType = MyApplication.getSearch_actionUrl();
			Log.v("Action Type", actionType.get(arg2));
			Log.v("Action Url", actionURL.get(arg2));
			if (actionType.get(arg2).equalsIgnoreCase("2")) {
				Log.v("Action Type", actionType.get(arg2));

				Intent gotoweb = new Intent(TabletCatalog.this,
						Catalog_webview.class);

				gotoweb.putExtra("finurl", actionURL.get(arg2));
				Bitmap bitmaps = ((BitmapDrawable) TabletCatalog.this.bitmap[arg2])
						.getBitmap();

				gotoweb.putExtra("image", bitmaps);
				gotoweb.putExtra("heading", text.getText().toString());
				gotoweb.putExtra("actColour", action_colour_codes[colorPos]);
				startActivity(gotoweb);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);

			} else {

				Log.v("Action Type", actionType.get(arg2));

				String server = MyApplication.getServer();
				String enccoded = MyApplication.getEncodedUserCred();

				Intent gotoweb = new Intent(TabletCatalog.this,
						Catalog_webview.class);
				String final_service_url = server
						+ "/tmtrack/srcmobile/index.html?form=catalog&tableuuid="
						+ finalTableId + "&projectname=" + finalProjectId
						+ "&usrcredentials=" + enccoded + "&WEBURL=" + server
						+ "&millisec=" + System.currentTimeMillis()
						+ "&AUTH_TYPE=" + MyApplication.getLoggedInUrlType()
						+ "&USER_LOGIN=" + MyApplication.getUserID()
						+ "&USER_PWD=" + MyApplication.getPassWord();

				Log.v("Search url with time", final_service_url);

				gotoweb.putExtra("finurl", final_service_url);
				Bitmap bitmaps = ((BitmapDrawable) TabletCatalog.this.bitmap[arg2])
						.getBitmap();

				gotoweb.putExtra("image", bitmaps);
				gotoweb.putExtra("heading", text.getText().toString());
				gotoweb.putExtra("actColour", action_colour_codes[colorPos]);
				startActivity(gotoweb);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);

			}

		} else {
			if (disDialog == 0) {
				disDialog = 5;
				makediallog("No network connection",
						"You must be connected to the internet to use this app");
			}
		}
	}

	public class MyAdapter extends BaseAdapter {

		@SuppressWarnings("unused")
		private Context context;
		Point p;
		String h;
		@SuppressWarnings("unused")
		private ArrayList<All_Sla_data_dto> list_Sla = new ArrayList<All_Sla_data_dto>();

		private LayoutInflater layoutInflater;

		MyAdapter(Context c)

		{
			headings = new String[MyApplication.getService_names().size()];
			bitmap = new Drawable[MyApplication.getService_images().size()];
			summary = new String[MyApplication.getService_summary().size()];
			discri = new String[MyApplication.getService_discrpition().size()];
			servicetabUUID = new String[MyApplication.gettableUUID().size()];
			projectName = new String[MyApplication.getProjectName().size()];

			context = c;

			layoutInflater = LayoutInflater.from(c);
			for (int i = 0; i < MyApplication.getService_names().size(); i++) {
				bitmap[i] = MyApplication.getService_images().get(i);
				headings[i] = MyApplication.getService_names().get(i);
				summary[i] = MyApplication.getService_summary().get(i);
				discri[i] = MyApplication.getService_discrpition().get(i);
				projectName[i] = MyApplication.getProjectName().get(i);
				servicetabUUID[i] = MyApplication.gettableUUID().get(i);

			}
		}

		public int getCount() {
			return headings.length;
		}

		public Object getItem(int position) {
			return headings[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View grid, ViewGroup parent) {
			try {
				grid = layoutInflater.inflate(R.layout.catagory_new_list_item,
						null);
View view= grid;
				final int colorPos = position % image_ids.length;

				if (MyApplication.getService_names().get(position) != null) {
					grid.setBackgroundResource(image_ids[colorPos]);
					ImageView imageView = (ImageView) grid
							.findViewById(R.id.service_img);
					final TextView text = (TextView) grid
							.findViewById(R.id.service_head);
					TextView discript = (TextView) grid
							.findViewById(R.id.service_discrip);
					final Button information = (Button) grid
							.findViewById(R.id.list_btn);
					information.setFocusable(false);
					information.setOnClickListener(new OnClickListener() {

						@SuppressWarnings("unused")
						@Override
						public void onClick(View v) {
							// DialogBlock pd = new DialogBlock();
							DialogBlock.showProgressDialog(catContext, "",
									false);
							String tsm = projectName[position];
							ArrayList<All_Sla_data_dto> vx = new ArrayList<All_Sla_data_dto>();

							// home.setClickable(false);
							Bitmap bitmap = null;
							try {
								bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[position])
										.getBitmap();
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

							if (isXlarge) {
								SLA_Data sla1 = new SLA_Data();
								sla1.description = MyApplication
										.getService_discrpition().get(position);
								dis = MyApplication.getService_discrpition()
										.get(position);
								System.out
										.println("sla description is................:"
												+ sla1.description);
								sla1.cat = 1;
								sla1.tsm = tsm;
								if (bitmap != null) {
									sla1.image = bitmap;
								}
								sla1.heading = text.getText().toString();
								sla1.actColour = action_colour_codes[colorPos];
								MyApplication.setSLA("FALSE");
								callPopOverViewClass(v, sla1, 1);
								DialogBlock.removeDialog();
							} else {

								Intent sla = new Intent(TabletCatalog.this,
										Sla_data_Viewer.class);
								sla.putExtra("description", MyApplication
										.getService_discrpition().get(position));
								sla.putExtra("cat", 1);
								sla.putExtra("tsm", tsm);
								if (bitmap != null) {
									sla.putExtra("image", bitmap);
								}
								sla.putExtra("heading", text.getText()
										.toString());
								sla.putExtra("actColour",
										action_colour_codes[colorPos]);
								MyApplication.setSLA("FALSE");
								DialogBlock.removeDialog();

								startActivity(sla);
								overridePendingTransition(R.anim.incoming,
										R.anim.outgoing);
							}
						}
					});
					try {

						text.setText(headings[position]);
						discript.setText(summary[position]);
					} catch (Exception e) {
					}
					if (bitmap[position] == null) {
						imageView.setImageResource(R.drawable.ic_launcher);
					} else {
						imageView.setImageDrawable(bitmap[position]);
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return grid;
		}

	}

	ImageView caro;
	ViewPager mPager;

	private void setPopUpWindow(PopoverView popoverView, SLA_Data sla, int i) {
		// TODO Auto-generated method stub
		RelativeLayout act = (RelativeLayout) popoverView
				.findViewById(R.id.action_sla);
		SLA_Data sla1 = new SLA_Data();
		sla1 = sla;
		ImageView img = (ImageView) popoverView.findViewById(R.id.slaimage);
		caro = (ImageView) popoverView.findViewById(R.id.carosel);
		mPager = (ViewPager) popoverView.findViewById(R.id.pager);
		act.setBackgroundColor(Color.parseColor(sla1.actColour));
		TextView sla_headings = (TextView) popoverView
				.findViewById(R.id.slaheading);
		sla_headings.setText(sla1.heading);
		Button catButton = (Button) popoverView.findViewById(R.id.catalog);
		catButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// System.gc();
				onBackPressed();
				finish();
			}
		});
		int btn = 0;
		btn = sla1.cat;

		if (btn == 1) {
			catButton.setText("Catalog");
		} else if (btn == 2) {
			catButton.setText("Search");
		}

		Bitmap bitmap1 = null;
		bitmap1 = sla1.image;
		if (bitmap1 != null) {
			img.setImageBitmap(bitmap1);
		}

		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					caro.setBackgroundResource(R.drawable.firststpage);
					break;
				case 1:
					caro.setBackgroundResource(R.drawable.secondpage);
					break;

				default:
					caro.setBackgroundResource(R.drawable.firststpage);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		System.out.println("sla description is:" + sla1.description);
		if (i == 1) {
			settingAdapter(sla1);
		} else {
			settingAdapter_new(sla1);
		}

	}

	String discri1, slahead, sla_dis;
	String ssotoken;
	String slaname = null;
	String sladescrip1 = null;
	String dis;

	public void settingAdapter(SLA_Data sla1) {
		// TODO Auto-generated method stub
		ArrayList<All_Sla_data_dto> vx = new ArrayList<All_Sla_data_dto>();

		if (Network_Available.hasConnection(TabletCatalog.this)) {
			vx = DBAdpter.slaUserData(MyApplication.getToken(), sla1.tsm);
		} else {
			if (disDialog == 0) {
				disDialog = 5;
				makediallog("No network connection",
						"You must be connected to the internet to use this app");
			}
		}

		if (vx.size() > 0) {
			for (int i = 0; i < vx.size(); i++) {
				slaname = vx.get(i).name;
				Log.v("NAME", slaname);
				sladescrip1 = vx.get(i).description;
				Log.v("DISCRIPTION", sladescrip1);
			}
		} else {
			slaname = "";
			sladescrip1 = "";
		}

		mAdapter = new MyAdapter11(getSupportFragmentManager(),
				sla1.description, slaname, sladescrip1);

		mPager.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		dis = dis.replaceAll("<(.*?)\\>", " ");// Removes all items in brackets
		dis = dis.replaceAll("<(.*?)\\\n", " ");// Must be undeneath
		dis = dis.replaceFirst("(.*?)\\>", " ");// Removes any connected item to
												// the last bracket
		dis = dis.replaceAll("&nbsp;", " ");
		dis = dis.replaceAll("&amp;", " ");
		discri1 = dis;
		slahead = slaname;
		sla_dis = sladescrip1;
	}

	public void settingAdapter_new(SLA_Data sla1) {
		// TODO Auto-generated method stub

		mAdapter = new MyAdapter11(getSupportFragmentManager(),
				sla1.description, slahead, sla_dis);

		mPager.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		dis = dis.replaceAll("<(.*?)\\>", " ");// Removes all items in brackets
		dis = dis.replaceAll("<(.*?)\\\n", " ");// Must be undeneath
		dis = dis.replaceFirst("(.*?)\\>", " ");// Removes any connected item to
												// the last bracket
		dis = dis.replaceAll("&nbsp;", " ");
		dis = dis.replaceAll("&amp;", " ");
		discri1 = dis;
		slahead = sla1.name;
		sla_dis = sla1.description;
	}

	MyAdapter11 mAdapter;

	public class MyAdapter11 extends FragmentStatePagerAdapter {
		String des, slaHead, slaDes;

		public MyAdapter11(FragmentManager fm, String a, String b, String c) {
			super(fm);
			des = a;
			slaHead = b;
			slaDes = c;
			Log.v(" A .B> C Values", a + "......" + b + "......" + c);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				caro.setBackgroundResource(R.drawable.secondpage);
				return new Sladata(des);
			case 1:
				caro.setBackgroundResource(R.drawable.firststpage);
				return new SlaDatadiscription(slaHead, slaDes);
			case 2:
				// return new ImageFragment(R.drawable.ic_launcher);

			default:
				return null;
			}
		}
	}

	// ...Catalog list items....
	public class CatalogSpinner extends BaseAdapter {
		private String[] na;
		private Drawable[] bitmap;
		private String[] item_id;

		private Context context;
		String h;
		private LayoutInflater layoutInflater;

		CatalogSpinner(Context c)

		{
			na = new String[MyApplication.getCatelog_names().size()];
			bitmap = new Drawable[MyApplication.getCatelog_images().size()];
			item_id = new String[MyApplication.getCatelog_ids().size()];
			context = c;
			layoutInflater = LayoutInflater.from(context);

			for (int i = 0; i < MyApplication.getCatelog_ids().size(); i++) {
				na[i] = MyApplication.getCatelog_names().get(i);
				item_id[i] = MyApplication.getCatelog_ids().get(i);
				bitmap[i] = MyApplication.getCatelog_images().get(i);
			}

		}

		public int getCount() {
			return item_id.length;
			// return names.length;
		}

		public Object getItem(int position)

		{
			return item_id[position];
			// return names[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View grid, ViewGroup parent) {
			try {
				grid = layoutInflater.inflate(R.layout.catalog_list_item, null);
				ImageView imageView = (ImageView) grid
						.findViewById(R.id.cat_img);
				TextView text = (TextView) grid.findViewById(R.id.cat_titl);
				TextView id = (TextView) grid.findViewById(R.id.cat_id);
				text.setText(na[position]);
				id.setText(item_id[position]);
				if (bitmap[position] == null) {

				} else {
					imageView.setImageDrawable(bitmap[position]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return grid;
		}
	}

	class service_background extends AsyncTask<Void, Void, Void> {
		// DialogBlock pd;
		String cat_id;

		public service_background(String id) {
			this.cat_id = id;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			DialogBlock.showProgressDialog(catContext, "", false);
			catalogProgress.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... params) {
			Catalog_Services_Background.alldata_services(cat_id);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			DialogBlock.removeDialog();

			catalogProgress.setVisibility(View.GONE);

			try {
				if (MyApplication.getserviceCount() == 0) {
					MyApplication.clearServiceResult();
					lv.setVisibility(View.GONE);
					No_Service.setVisibility(View.GONE);
					if (isXlarge) {
						gridOfServices.setVisibility(View.GONE);
					}

				} else {
					lv.setVisibility(View.VISIBLE);
					No_Service.setVisibility(View.GONE);

					if (isXlarge) {
						gridOfServices.setVisibility(View.VISIBLE);
						gridOfServices.setAdapter(new MyAdapter(catContext));

					} else {
						lv.setAdapter(new MyAdapter(catContext));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void customlist() {
		final Dialog dialog = new Dialog(catContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.catlist);
		// dialog.setTitle("Catalog");

		dialog.show();

		ListView lvc = (ListView) dialog.findViewById(R.id.listtttttttt);
		lvc.setAdapter(new CatalogSpinner(catContext));
		lvc.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (Network_Available.hasConnection(catContext)) {
					if (arg2 == 0) {
						MyApplication.set_new_tag(arg2);
						LinearLayout lll = (LinearLayout) arg1
								.findViewById(R.id.catagorylist);
						TextView textView = (TextView) arg1
								.findViewById(R.id.cat_id);
						TextView title = (TextView) arg1
								.findViewById(R.id.cat_titl);
						lll.setBackgroundColor(Color.parseColor("#1f6fbf"));
						catalog_id = textView.getText().toString();
						head.setText(title.getText().toString());
						MyApplication.setCatalog_Title(title.getText()
								.toString());
						// new service_background(catalog_id).execute();
						try {
							if (isXlarge) {
								gridOfServices.setAdapter(new MyAdapter_new(
										catContext));
							} else {

								lv.setAdapter(new MyAdapter_new(catContext));
							}
						} catch (Exception e) {
							// if null pointer exception
							lv.setVisibility(View.GONE);

						}
						dialog.cancel();
						MyApplication.setCatalog_Title(head.getText()
								.toString());

					} else {

						MyApplication.set_new_tag(arg2);
						LinearLayout lll = (LinearLayout) arg1
								.findViewById(R.id.catagorylist);
						TextView textView = (TextView) arg1
								.findViewById(R.id.cat_id);
						TextView title = (TextView) arg1
								.findViewById(R.id.cat_titl);
						lll.setBackgroundColor(Color.parseColor("#1f6fbf"));
						catalog_id = textView.getText().toString();
						Catalog_New_Background.firstItemInCatalog = Integer
								.valueOf(catalog_id);

						head.setText(title.getText().toString());
						MyApplication.setCatalog_Title(title.getText()
								.toString());
						new service_background(catalog_id).execute();
						dialog.cancel();
						MyApplication.setCatalog_Title(head.getText()
								.toString());
					}
				}

				else {
					if (disDialog == 0) {
						disDialog = 5;
						makediallog("No network connection",
								"You must be connected to the internet to use this app");
					}
				}
			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();
		backgroundActivity();
	}

	private void backgroundActivity() {
		// TODO Auto-generated method stub
		System.gc();
		long millisecond = (long) System.currentTimeMillis();
		MyApplication.setlastTime(millisecond);
		// the following for when app goes to background and is killed my user
		// then to maintain auto reloggin
		long movedBackground = millisecond;
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.commit();
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(catContext);
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

	public class MyAdapter_new extends BaseAdapter {

		private Context contextn;
		String h;
		private ArrayList<All_Sla_data_dto> list_Sla = new ArrayList<All_Sla_data_dto>();

		private LayoutInflater layoutInflater;

		MyAdapter_new(Context c)

		{
			try {

				headings = new String[MyApplication.getService_names_new()
						.size()];
				bitmap = new Drawable[MyApplication.getService_images_new()
						.size()];
				summary = new String[MyApplication.getService_summary_new()
						.size()];
				discri = new String[MyApplication.getService_discrpition_new()
						.size()];
				servicetabUUID = new String[MyApplication.gettableUUID_new()
						.size()];
				projectName = new String[MyApplication.getProjectName_new()
						.size()];
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			try {
				this.contextn = c;

				layoutInflater = LayoutInflater.from(c);
				for (int i = 0; i < MyApplication.getService_names_new().size(); i++) {
					try {
						bitmap[i] = MyApplication.getService_images_new()
								.get(i);
						headings[i] = MyApplication.getService_names_new().get(
								i);
						summary[i] = MyApplication.getService_summary_new()
								.get(i);
						discri[i] = MyApplication.getService_discrpition_new()
								.get(i);
						projectName[i] = MyApplication.getProjectName_new()
								.get(i);
						servicetabUUID[i] = MyApplication.gettableUUID_new()
								.get(i);
					} catch (Exception e) {
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public int getCount() {
			int y = 0;
			try {
				y = headings.length;
			} catch (Exception e) {
			}
			return y;
		}

		public Object getItem(int position) {
			Object d = null;
			try {
				d = headings[position];
			} catch (Exception e) {
				// TODO: handle exception
			}
			return d;
		}

		public long getItemId(int position) {
			int t = 0;
			try {
				t = position;
			} catch (Exception e) {
			}
			return t;
		}

		public View getView(final int position, View grid, ViewGroup parent) {
			try {
				grid = layoutInflater.inflate(R.layout.catagory_new_list_item,
						null);

				final int colorPos = position % MyApplication.mockColors.size();

				if (MyApplication.getService_names_new().get(position) != null) {
					grid.setBackgroundResource(image_ids[colorPos]);
					ImageView imageView = (ImageView) grid
							.findViewById(R.id.service_img);
					final TextView text = (TextView) grid
							.findViewById(R.id.service_head);
					TextView discript = (TextView) grid
							.findViewById(R.id.service_discrip);
					Button information = (Button) grid
							.findViewById(R.id.list_btn);
					information.setFocusable(false);
					// final TextView sla=(TextView)grid.findViewById(R.id.sla);
					information.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// home.setClickable(false);
							// sla.setText(list_Sla.get(position).name);
							String tsm = projectName[position];
							String assotoken = MyApplication.getToken();
							ArrayList<All_Sla_data_dto> vx = new ArrayList<All_Sla_data_dto>();
							if (Network_Available.hasConnection(catContext)) {
								vx = DBAdpter.slaUserData(assotoken, tsm);
							} else {
								if (disDialog == 0) {
									disDialog = 5;
									makediallog("No network connection",
											"You must be connected to the internet to use this app");
								}
							}
							String name = null;
							Bitmap bmp;
							if (MyApplication.getSLA().equalsIgnoreCase("TRUE")) {
								for (int i = 0; i < vx.size(); i++) {
									Bitmap bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[position])
											.getBitmap();
									name = vx.get(i).name;
									Log.v("NAME", name);
									String discription = vx.get(i).description;
									Log.v("DISCRIPTION", discription);
									MyApplication.setSLA("FALSE");
									if (isXlarge) {
										Log.v("Tablet Stuuff", "call");
										SLA_Data sla1 = new SLA_Data();
										sla1.description = MyApplication
												.getService_discrpition_new()
												.get(i);
										dis = MyApplication
												.getService_discrpition_new()
												.get(i);
										System.out
												.println("sla description is:"
														+ sla1.description);

										sla1.tsm = tsm;
										sla1.name = name;
										if (bitmap != null) {
											sla1.image = bitmap;
										}
										sla1.heading = text.getText()
												.toString();
										sla1.actColour = action_colour_codes[colorPos];
										MyApplication.setSLA("FALSE");
										callPopOverViewClass(v, sla1, 2);
									} else {
										callIntent(
												position,
												name,
												vx.get(i).description,
												bitmap,
												text.getText().toString(),
												colorPos,
												MyApplication
														.getService_discrpition_new()
														.get(position), "");
									}
								}
							} else {
								Bitmap bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[position])
										.getBitmap();
								if (isXlarge) {
									Log.v("Tablet Stuuff", "call");
									SLA_Data sla1 = new SLA_Data();
									sla1.description = MyApplication
											.getService_discrpition_new().get(
													position);
									dis = MyApplication
											.getService_discrpition_new().get(
													position);
									System.out.println("sla description is:"
											+ sla1.description);

									sla1.tsm = tsm;
									sla1.name = "";
									if (bitmap != null) {
										sla1.image = bitmap;
									}
									sla1.heading = text.getText().toString();
									sla1.actColour = action_colour_codes[colorPos];
									// MyApplication.setSLA("FALSE");
									callPopOverViewClass(v, sla1, 2);
								} else {
									callIntent(
											position,
											"",
											"",
											bitmap,
											text.getText().toString(),
											colorPos,
											MyApplication
													.getService_discrpition_new()
													.get(position),
											headings[position]);
								}
							}
						}
					});
					try {
						if (headings[position].length() < 20) {
							text.setText(headings[position]);
						} else {
							text.setText(headings[position].substring(0, 18)
									+ "...");
						}
						discript.setText(summary[position]);
					} catch (Exception e) {
					}
					if (bitmap[position] == null) {
						imageView.setImageResource(R.drawable.ic_launcher);
					} else {
						imageView.setImageDrawable(bitmap[position]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return grid;

		}

	}

	private void callIntent(int position, String name, String description,
			Bitmap bitmap, String string, int colorPos, String string2,
			String head) {
		// TODO Auto-generated method stub
		Intent sla = new Intent(TabletCatalog.this, Sla_data_Viewer.class);
		sla.putExtra("description", string2);
		sla.putExtra("slanam", name);
		sla.putExtra("keys", "catalog");
		sla.putExtra("slades", description);
		sla.putExtra("image", bitmap);
		sla.putExtra("heading", string);
		sla.putExtra("head", "");
		sla.putExtra("actColour", action_colour_codes[colorPos]);
		startActivity(sla);
		overridePendingTransition(R.anim.incoming, R.anim.outgoing);
	}

	private void createPopUpMenu(View v) {

		popupWindow = new PopupWindow(getApplicationContext());

		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		ListView vi = (ListView) popupView.findViewById(R.id.pop_listview);

		vi.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					noSearchResults.setVisibility(View.GONE);
					gridOfServices.setVisibility(View.VISIBLE);
					if (Network_Available.hasConnection(catContext)) {
						if (arg2 == 0) {
							MyApplication.set_new_tag(arg2);
							LinearLayout lll = (LinearLayout) arg1
									.findViewById(R.id.catagorylist);
							TextView textView = (TextView) arg1
									.findViewById(R.id.cat_id);
							TextView title = (TextView) arg1
									.findViewById(R.id.cat_titl);
							lll.setBackgroundColor(Color.parseColor("#1f6fbf"));
							catalog_id = textView.getText().toString();
							Catalog_New_Background.firstItemInCatalog = Integer
									.valueOf(catalog_id);
							catTitleXlarge.setText(title.getText().toString());
							MyApplication.setCatalog_Title(title.getText()
									.toString());
							gridOfServices.setAdapter(new MyAdapter_new(
									catContext));
							MyApplication.setCatalog_Title(head.getText()
									.toString());
							popupWindow.dismiss();

						} else {

							MyApplication.set_new_tag(arg2);
							LinearLayout lll = (LinearLayout) arg1
									.findViewById(R.id.catagorylist);
							TextView textView = (TextView) arg1
									.findViewById(R.id.cat_id);
							TextView title = (TextView) arg1
									.findViewById(R.id.cat_titl);
							lll.setBackgroundColor(Color.parseColor("#1f6fbf"));
							catalog_id = textView.getText().toString();
							Catalog_New_Background.firstItemInCatalog = Integer
									.valueOf(catalog_id);
							catTitleXlarge.setText(title.getText().toString());
							MyApplication.setCatalog_Title(title.getText()
									.toString());
							new service_background(catalog_id).execute();
							MyApplication.setCatalog_Title(head.getText()
									.toString());
							popupWindow.dismiss();
						}
					}

					else {
						if (disDialog == 0) {
							disDialog = 5;
							makediallog("No network connection",
									"You must be connected to the internet to use this app");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		try {
			vi.setAdapter(new CatalogSpinner(catContext));
		} catch (Exception e) {
			e.printStackTrace();
		}
		popupWindow.setWidth(300);
		popupWindow.setHeight(500);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setContentView(popupView);
		popupWindow.showAsDropDown(v, 0, 0);

	}

	private void callPopOverViewClass(View v, SLA_Data sla1, int i) {
		// TODO Auto-generated method stub
		PopoverView popoverView = new PopoverView(catContext, R.layout.sla);
		popoverView.setContentSizeForViewInPopover(new Point(480, 320));
		popoverView.setDelegate(this);
		popoverView.showPopoverFromRectInViewGroup(root,
				PopoverView.getFrameForView(v),
				PopoverView.PopoverArrowDirectionUp, true);
		setPopUpWindow(popoverView, sla1, i);
	}

	@Override
	public void popoverViewWillShow(PopoverView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void popoverViewDidShow(PopoverView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void popoverViewWillDismiss(PopoverView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void popoverViewDidDismiss(PopoverView view) {
		// TODO Auto-generated method stub
	}

	public class searchinBackground extends AsyncTask<Void, Void, Void> {
		private String url;
		int size;

		searchinBackground(String requrl) {
			url = requrl;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				System.out.println("url for searching is:" + url);
				url = url.replaceAll(" ", "%20");
				System.out.println("url for searching after encode is:" + url);
				size = search_background.search(url);
				System.out.println("size of search item list is" + size);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// super.onPostExecute(result);
			DialogBlock.removeDialog();

			if (size > 0) {
				try {
					noSearchResults.setVisibility(View.GONE);
					gridOfServices.setVisibility(View.VISIBLE);
					gridOfServices.setAdapter(new MySearchAdapter(
							TabletCatalog.this));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					noSearchResults.setVisibility(View.VISIBLE);
				}
			} else {

				gridOfServices.setVisibility(View.GONE);
				noSearchResults.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			DialogBlock.showProgressDialog(TabletCatalog.this, "", false);
		}
	}

	public class MySearchAdapter extends BaseAdapter {

		private Context context;
		Point p;
		String h;
		private ArrayList<All_Sla_data_dto> list_Sla = new ArrayList<All_Sla_data_dto>();

		private LayoutInflater layoutInflater;

		MySearchAdapter(Context c) {
			headings = new String[MyApplication.getSearch_names().size()];
			bitmap = new Drawable[MyApplication.getSearch_images().size()];
			summary = new String[MyApplication.getSearch_summary().size()];
			discri = new String[MyApplication.getSearch_discrpition().size()];
			servicetabUUID = new String[MyApplication.getSearchTabUuid().size()];
			projectName = new String[MyApplication.getSearchProjectName()
					.size()];

			context = c;

			layoutInflater = LayoutInflater.from(c);
			for (int i = 0; i < MyApplication.getSearch_names().size(); i++) {
				bitmap[i] = MyApplication.getSearch_images().get(i);
				headings[i] = MyApplication.getSearch_names().get(i);
				summary[i] = MyApplication.getSearch_summary().get(i);
				discri[i] = MyApplication.getSearch_discrpition().get(i);
				projectName[i] = MyApplication.getSearchProjectName().get(i);
				servicetabUUID[i] = MyApplication.getSearchTabUuid().get(i);
			}
		}

		public int getCount() {
			System.out.println("size of heading is:" + headings.length);
			return headings.length;
		}

		public Object getItem(int position) {
			return headings[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View grid, ViewGroup parent) {
			try {
				final int colorPos = position % image_ids.length;

				if (grid == null) {
					grid = layoutInflater.inflate(
							R.layout.catagory_new_list_item, null);

				}

				if (MyApplication.getSearch_names().get(position) != null) {
					grid.setBackgroundResource(image_ids[colorPos]);
					ImageView imageView = (ImageView) grid
							.findViewById(R.id.service_img);
					final TextView text = (TextView) grid
							.findViewById(R.id.service_head);
					TextView discript = (TextView) grid
							.findViewById(R.id.service_discrip);
					Button information = (Button) grid
							.findViewById(R.id.list_btn);
					information.setFocusable(false);

					text.setText("");
					discript.setText("");
					information.setText("");
					imageView.setImageResource(0);

					information.setFocusable(false);
					information.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// DialogBlock pd = new DialogBlock();
							DialogBlock.showProgressDialog(catContext, "",
									false);
							String tsm = projectName[position];
							ArrayList<All_Sla_data_dto> vx = new ArrayList<All_Sla_data_dto>();

							// home.setClickable(false);
							Bitmap bitmap = null;
							try {
								bitmap = ((BitmapDrawable) TabletCatalog.this.bitmap[position])
										.getBitmap();
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

							SLA_Data sla1 = new SLA_Data();
							sla1.description = MyApplication
									.getSearch_discrpition().get(position);
							dis = MyApplication.getSearch_discrpition().get(
									position);
							System.out.println("sla description is:"
									+ sla1.description);
							sla1.cat = 1;
							sla1.tsm = tsm;
							if (bitmap != null) {
								sla1.image = bitmap;
							}
							sla1.heading = text.getText().toString();
							sla1.actColour = action_colour_codes[colorPos];
							MyApplication.setSLA("FALSE");
							callPopOverViewClass(v, sla1, 1);
							DialogBlock.removeDialog();
						}
					});
					try {

						text.setText(headings[position]);
						discript.setText(summary[position]);
					} catch (Exception e) {
					}
					if (bitmap[position] == null) {
						imageView.setImageResource(R.drawable.ic_launcher);
					} else {
						imageView.setImageDrawable(bitmap[position]);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return grid;
		}
	}

}