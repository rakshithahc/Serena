package com.serana;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.serena.connection.Network_Available;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
public class Sla_data_Viewer extends FragmentActivity {

	Calendar cal;

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	protected void onResume() {
		super.onResume();

		if (isfirstEntry) {
			SharedPreferences e1 = getSharedPreferences("SERENA",
					MODE_WORLD_READABLE);
			long lasttime = e1.getLong("backgroundtime", 0);

			long millisecond = (long) System.currentTimeMillis();
			System.out.println("millisecond is" + millisecond);
			System.out.println("last millisecond is:" + lasttime);
			long f = millisecond - lasttime;
			int sec = (int) TimeUnit.MILLISECONDS.toMinutes(f);
			if (!Network_Available.hasConnection(slaCtx) && sec > 60) {
				Intent m = new Intent(Sla_data_Viewer.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(slaCtx) && sec > 60) {
				Intent m = new Intent(Sla_data_Viewer.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available.hasConnection(slaCtx) && sec < 60) {
				makediallog("No network connection",
						"You must be connected to the internet to use this app");

			} else if (Network_Available.hasConnection(slaCtx) && sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}

		isfirstEntry = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		MyApplication.setlastTime((long) System.currentTimeMillis());
		long movedBackground = (long) System.currentTimeMillis();
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		// this.finish();
		// overridePendingTransition(R.anim.incoming, R.anim.outgoing);
	}

	public static boolean isfirstEntry;
	private Context slaCtx;
	private MyAdapter mAdapter;
	private ViewPager mPager;
	String discri, slahead, sla_dis;
	private Button catButton;
	private TextView sla_headings;
	private ImageView img;
	RelativeLayout act;
	String gobacto;
	static ImageView caro, carosIndicator;
	private SharedPreferences user_details;
	private String prefname = "SERENA";
	String ssotoken;
	String slaname = null;
	String sladescrip1 = null;
	String dis;
	String tsm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isfirstEntry = false;
		slaCtx = Sla_data_Viewer.this;
		setContentView(R.layout.sla);
		cal = Calendar.getInstance();
		catButton = (Button) findViewById(R.id.catalog);
		catButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// onBackPressed();
				finish();
			}
		});
		act = (RelativeLayout) findViewById(R.id.action_sla);
		img = (ImageView) findViewById(R.id.slaimage);
		caro = (ImageView) findViewById(R.id.carosel);
		mPager = (ViewPager) findViewById(R.id.pager);
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

		sla_headings = (TextView) findViewById(R.id.slaheading);
		sla_headings.setText(getIntent().getStringExtra("heading"));
		dis = getIntent().getStringExtra("description");
		int btn = getIntent().getIntExtra("cat", 0);

		if (btn == 1) {
			catButton.setText("Catalog");
		} else if (btn == 2) {
			catButton.setText("Search");
		}

		Bitmap bitmap = null;
		if ((Bitmap) this.getIntent().getParcelableExtra("image") != null) {
			bitmap = (Bitmap) this.getIntent().getParcelableExtra("image");
		}
		String action_colour = getIntent().getStringExtra("actColour");
		act.setBackgroundColor(Color.parseColor(action_colour));
		//String head = getIntent().getStringExtra("heading");
		tsm = getIntent().getStringExtra("tsm");
		gobacto = getIntent().getStringExtra("keys");
		ssotoken = MyApplication.getToken();
		img.setImageBitmap(bitmap);

		settingAdapter();

	}

	public void settingAdapter() {
		// TODO Auto-generated method stub
		ArrayList<All_Sla_data_dto> vx = new ArrayList<All_Sla_data_dto>();

		if (Network_Available.hasConnection(slaCtx)) {
			vx = DBAdpter.slaUserData(ssotoken, tsm);
		} else {
			makediallog("No network connection",
					"You must be connected to the internet to use this app");

		}

		for (int i = 0; i < vx.size(); i++) {
			slaname = vx.get(i).name;
			Log.v("NAME", slaname);
			sladescrip1 = vx.get(i).description;
			Log.v("DISCRIPTION", sladescrip1);
		}

		mAdapter = new MyAdapter(getSupportFragmentManager(), getIntent()
				.getStringExtra("description"), slaname, sladescrip1);

		mPager.setAdapter(mAdapter);

		dis = dis.replaceAll("<(.*?)\\>", " ");// Removes all items in brackets
		dis = dis.replaceAll("<(.*?)\\\n", " ");// Must be undeneath
		dis = dis.replaceFirst("(.*?)\\>", " ");// Removes any connected item to
												// the last bracket
		dis = dis.replaceAll("&nbsp;", " ");
		dis = dis.replaceAll("&amp;", " ");
		discri = dis;
		slahead = slaname;
		sla_dis = sladescrip1;
	}

	public static class MyAdapter extends FragmentPagerAdapter {
		String des, slaHead, slaDes;

		public MyAdapter(FragmentManager fm, String a, String b, String c) {
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

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(slaCtx);
		builder.setTitle(titlte);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}