package com.serana;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.serena.connection.Network_Available;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint({ "InlinedApi", "NewApi", "WorldWriteableFiles",
		"WorldReadableFiles" })
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Search_View extends Activity {
	@SuppressLint({ "NewApi", "NewApi" })
	Calendar cal;
	public static int disDialog = 0;

	@Override
	protected void onResume() {
		super.onResume();
		try {
			btn.setEnabled(true);
		} catch (Exception e) {
		}
		try {
			goBack.setClickable(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		lv.setVisibility(View.VISIBLE);
		if (isfirstEntry) {
			SharedPreferences e1 = getSharedPreferences("SERENA",
					MODE_WORLD_READABLE);
			long lasttime = e1.getLong("backgroundtime", 0);

			long millisecond = (long) System.currentTimeMillis();
			System.out.println("millisecond is" + millisecond);
			System.out.println("last millisecond is:" + lasttime);
			long f = millisecond - lasttime;
			int sec = (int) TimeUnit.MILLISECONDS.toMinutes(f);
			if (!Network_Available.hasConnection(search_Ctx) && sec > 60) {
				Intent m = new Intent(Search_View.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(search_Ctx) && sec > 60) {
				Intent m = new Intent(Search_View.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available.hasConnection(search_Ctx) && sec < 60) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			} else if (Network_Available.hasConnection(search_Ctx) && sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}
		isfirstEntry = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		backgroundActivity();
	}

	private void backgroundActivity() {
		// TODO Auto-generated method stub
		System.gc();
		MyApplication.setlastTime(System.currentTimeMillis());
		long movedBackground = System.currentTimeMillis();
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.commit();
	}

	private SharedPreferences user_details;
	private String prefname = "SERENA";
	static ProgressBar searchProgrss;
	private String[] headings;
	private Drawable[] bitmap;
	private String[] summary;
	private String[] projectName;
	private String[] discri;
	private String[] servicetabUUID;

	Button btn;
	private String action_colour_codes[] = { "#1f6fbf", "#1bff5f", "#bf1f67",
			"#471fbf", "#1fb5bf", "#cb2f5c", "#e37136", "#dbc41e", "#7cca1d",
			"#48c7e7", "#f68500", "#36f907", "#009ed6", "#00356e", "#e9490b" };
	private int static_image_ids[] = { R.drawable.mock1, R.drawable.mock2,
			R.drawable.mock3, R.drawable.mock4, R.drawable.mock5,
			R.drawable.mock6, R.drawable.mock7, R.drawable.mock8,
			R.drawable.mock9, R.drawable.mock10, R.drawable.mock11,
			R.drawable.mock12, R.drawable.mock13, R.drawable.mock14,
			R.drawable.mock15 };
	private EditText search_txt;
	private ListView lv;
	private Context search_Ctx;
	@SuppressWarnings("unused")
	private String fin_url;
	private String req;
	private Button goBack, Cancel;
	TextView searchCount, noSearchResults;
	public static boolean isfirstEntry;
	public boolean onExit = false;
	private static int image_ids[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view);

		cal = Calendar.getInstance();
		onExit = false;
		Second_Btn.sessionMaintance = false;
		try {
			Compare n = new Compare();
			image_ids = n.backgrounds("medium");
		} catch (Exception e) {
			image_ids = static_image_ids;
		}

		searchProgrss = (ProgressBar) findViewById(R.id.searchProgress);
		searchProgrss.setVisibility(View.GONE);

		Cancel = (Button) findViewById(R.id.cancelSearch);
		Cancel.setVisibility(View.GONE);
		Cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_txt.setCursorVisible(false);

				Cancel.setVisibility(View.GONE);
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		goBack = (Button) findViewById(R.id.backtohome);
		goBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Search_View.this, Second_Btn.class);
				startActivity(intent);
				onBackPressed();
				Second_Btn.powerButton = false;
				Second_Btn.isChildSelected = false;
				Search_View.this.finish();
			}
		});
		isfirstEntry = false;

		searchCount = (TextView) findViewById(R.id.search_count);
		search_Ctx = Search_View.this;
		search_txt = (EditText) findViewById(R.id.search);
		search_txt.setCursorVisible(false);
		noSearchResults = (TextView) findViewById(R.id.noSearchResults);
		noSearchResults.setVisibility(View.GONE);
		lv = (ListView) findViewById(R.id.searched_list);

		search_txt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				search_txt.setCursorVisible(true);
				Cancel.setVisibility(View.VISIBLE);
				return false;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final int colorPos = arg2 % image_ids.length;

				btn = (Button) arg1.findViewById(R.id.list_btn);
				btn.setEnabled(false);
				isfirstEntry = false;
				if (Network_Available.hasConnection(search_Ctx)) {

					String proj_name = null, tabid = null, finalProjectId = null, finalTableId = null;
					// ArrayList<String> pro_name =
					// MyApplication.getProjectName();
					// ArrayList<String> tab_id = MyApplication.gettableUUID();
					for (int i = 0; i < projectName.length; i++) {
						proj_name = projectName[arg2];
						tabid = servicetabUUID[arg2];
					}
					Log.v(proj_name, tabid);
					String assotoken = MyApplication.getToken();
					ArrayList<All_Service_webforms_data_key_dto> f = new ArrayList<All_Service_webforms_data_key_dto>();
					f = DBAdpter
							.servicewebformKeys(assotoken, proj_name, tabid);

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
					final TextView text = (TextView) arg1
							.findViewById(R.id.service_head);

					ArrayList<String> actionURL = MyApplication
							.getSearch_actionType();
					ArrayList<String> actionType = MyApplication
							.getSearch_actionUrl();
					Log.v("Action Type", actionType.get(arg2));
					Log.v("Action Url", actionURL.get(arg2));
					if (actionType.get(arg2).equalsIgnoreCase("2")) {
						Log.v("Action Type", actionType.get(arg2));

						Intent gotoweb = new Intent(Search_View.this,
								search_webview.class);

						gotoweb.putExtra("finurl", actionURL.get(arg2));
						Bitmap bitmaps = ((BitmapDrawable) Search_View.this.bitmap[arg2])
								.getBitmap();

						gotoweb.putExtra("image", bitmaps);
						gotoweb.putExtra("heading", text.getText().toString());
						gotoweb.putExtra("actColour",
								action_colour_codes[colorPos]);
						startActivity(gotoweb);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_right_out);

					} else {

						Log.v("Action Type", actionType.get(arg2));

						String server = MyApplication.getServer();
						String enccoded = MyApplication.getEncodedUserCred();

						Intent gotoweb = new Intent(Search_View.this,
								search_webview.class);
						String final_service_url = server
								+ "/tmtrack/srcmobile/index.html?form=catalog&tableuuid="
								+ finalTableId + "&projectname="
								+ finalProjectId + "&usrcredentials="
								+ enccoded + "&WEBURL=" + server + "&millisec="
								+ System.currentTimeMillis() + "&AUTH_TYPE="
								+ MyApplication.getLoggedInUrlType()
								+ "&USER_LOGIN=" + MyApplication.getUserID()
								+ "&USER_PWD=" + MyApplication.getPassWord();

						Log.v("Search url with time", final_service_url);

						gotoweb.putExtra("finurl", final_service_url);
						Bitmap bitmaps = ((BitmapDrawable) Search_View.this.bitmap[arg2])
								.getBitmap();

						gotoweb.putExtra("image", bitmaps);
						gotoweb.putExtra("heading", text.getText().toString());
						gotoweb.putExtra("actColour",
								action_colour_codes[colorPos]);
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
		});
		search_txt
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// call json here......
						String server = MyApplication.getServer();
						Log.v("URL", server);
						req = search_txt.getText().toString();

						MyApplication.clearSearchrResult();
						final String urlstr = server
								+ "/sdf/servicedef/services/"+MyApplication.getAdvance_id()+"?name=%25" + req
								+ "%25&sortby=weight,name";
						InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

						inputManager.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION) {
							if (Network_Available.hasConnection(search_Ctx)) {
								try {
									// lv.setVisibility(View.GONE);
									Log.v("url", urlstr);
									new search_back(urlstr).execute();
								} catch (Exception e) {
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
						return false;
					}
				});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Second_Btn.isChildSelected = false;
		Second_Btn.powerButton = false;
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		// this.finish();
	}

	public class MyAdapter extends BaseAdapter {

		@SuppressWarnings("unused")
		private Context context;
		String h;
		@SuppressWarnings("unused")
		private ArrayList<All_Sla_data_dto> list_Sla = new ArrayList<All_Sla_data_dto>();

		private LayoutInflater layoutInflater;

		MyAdapter(Context c)

		{

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
				// Log.v("NAME",names[i]);

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

		@SuppressWarnings("unused")
		public View getView(final int position, View grid, ViewGroup parent) {

			final int colorPos = position % image_ids.length;

			ViewHolder holder = null;
			if (grid == null) {
				grid = layoutInflater.inflate(R.layout.catagory_new_list_item,
						null);
				holder = new ViewHolder();

			}
			try {
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

					// final TextView sla=(TextView)grid.findViewById(R.id.sla);
					information.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							goBack.setClickable(false);

							// sla.setText(list_Sla.get(position).name);
							// DialogBlock p = new DialogBlock();
							DialogBlock.showProgressDialog(search_Ctx, "",
									false);
							String tsm = projectName[position];
							String assotoken = MyApplication.getToken();
							ArrayList<All_Sla_data_dto> vx = new ArrayList<All_Sla_data_dto>();
							vx = DBAdpter.slaUserData(assotoken, tsm);
							String name = null;
							Bitmap bmp;
							if (MyApplication.getSLA().equalsIgnoreCase("TRUE")) {
								for (int i = 0; i < vx.size(); i++) {
									Bitmap bitmap = ((BitmapDrawable) Search_View.this.bitmap[position])
											.getBitmap();
									name = vx.get(i).name;
									Log.v("NAME", name);
									String discription = vx.get(i).description;
									Log.v("DISCRIPTION", discription);
									Intent sla = new Intent(Search_View.this,
											Sla_data_Viewer.class);
									sla.putExtra("description",
											MyApplication
													.getSearch_discrpition()
													.get(position));
									sla.putExtra("slanam", name);
									sla.putExtra("keys", "search");

									sla.putExtra("head", headings[position]);
									sla.putExtra("slades",
											vx.get(i).description);
									sla.putExtra("image", bitmap);
									sla.putExtra("actName",
											action_colour_codes[colorPos]);
									sla.putExtra("heading", text.getText()
											.toString());
									sla.putExtra("actColour",
											action_colour_codes[colorPos]);
									sla.putExtra("cat", 2);

									MyApplication.setSLA("FALSE");
									DialogBlock.removeDialog();
									searchProgrss.setVisibility(View.GONE);

									startActivity(sla);
									overridePendingTransition(R.anim.incoming,
											R.anim.outgoing);
								}
							} else {
								Bitmap bitmap = ((BitmapDrawable) Search_View.this.bitmap[position])
										.getBitmap();
								Intent sla = new Intent(Search_View.this,
										Sla_data_Viewer.class);
								sla.putExtra("description", MyApplication
										.getSearch_discrpition().get(position));
								sla.putExtra("slanam", "");
								sla.putExtra("slades", "");
								sla.putExtra("keys", "search");

								sla.putExtra("head", headings[position]);
								sla.putExtra("image", bitmap);
								sla.putExtra("actName",
										action_colour_codes[colorPos]);
								sla.putExtra("heading", text.getText()
										.toString());
								sla.putExtra("actColour",
										action_colour_codes[colorPos]);
								sla.putExtra("cat", 2);

								startActivity(sla);
								DialogBlock.removeDialog();
								searchProgrss.setVisibility(View.GONE);

								overridePendingTransition(R.anim.incoming,
										R.anim.outgoing);

							}
						}
					});
					text.setText(headings[position]);
					discript.setText(summary[position]);

					if (bitmap[position] == null) {

						imageView.setImageResource(R.drawable.ic_launcher);
					} else {

						imageView.setImageDrawable(bitmap[position]);

					}
				}
				// grid.setTag(holder);

			} catch (Exception e) {
			}
			// }
			// else {
			// holder=(ViewHolder)grid.getTag();
			// }
			// }
			return grid;

		}

	}

	class ViewHolder {
		ImageView imageView;
		TextView text;
		TextView discript;
		Button information;
	}

	class search_back extends AsyncTask<Void, Void, Void> {
		private String url;

		public search_back(String u) {
			url = u;
			System.out.println("url is:" + url);
		}

		//
		// DialogBlock pdg;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// / pdg = new DialogBlock();
			DialogBlock.showProgressDialog(search_Ctx, "", false);
			searchProgrss.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				search_background.search(url);
			} catch (Exception e) {
				// TODO: handle exception
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			noSearchResults.setVisibility(View.VISIBLE);
			DialogBlock.removeDialog();
			searchProgrss.setVisibility(View.GONE);

			try {
				Log.v("DATA>>>>>>SIZE>>>>>>>>>>>>>", MyApplication
						.getSearch_discrpition().size() + "");
				Log.v("DATA>>>>>>SIZE>>>>>>>>>>>>>", MyApplication
						.getSearch_names().size() + "");
				Log.v("DATA>>>>>>SIZE>>>>>>>>>>>>>", MyApplication
						.getSearch_summary().size() + "");
				Log.v("DATA>>>>>>SIZE>>>>>>>>>>>>>", MyApplication
						.getSearchProjectName().size() + "");
				Log.v("DATA>>>>>>SIZE>>>>>>>>>>>>>", MyApplication
						.getSearchTabUuid().size() + "");

				try {
					lv.setAdapter(new MyAdapter(search_Ctx));
					lv.setVisibility(View.VISIBLE);
					String size = MyApplication.getSearch_names().size() + "";
					searchCount.setText(size);
					int y = 0;
					try {
						y = MyApplication.getSearch_names().size();
					} catch (Exception e) {
					}
					if (y == 0) {
						lv.setVisibility(View.GONE);
						noSearchResults.setVisibility(View.VISIBLE);
						DialogBlock.removeDialog();
						searchProgrss.setVisibility(View.GONE);

					} else {
						DialogBlock.removeDialog();
						searchProgrss.setVisibility(View.GONE);

						lv.setVisibility(View.VISIBLE);
						noSearchResults.setVisibility(View.GONE);
					}

				} catch (Exception e) {
					noSearchResults.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
			}

		}

	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(search_Ctx);
		builder.setTitle(titlte);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(disDialog==5)
				{
					disDialog=0;
				}
				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}