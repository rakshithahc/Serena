package com.serana;

import java.util.concurrent.TimeUnit;

import com.serena.connection.Network_Available;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class HelpScreen extends FragmentActivity {

	HorizontalListViewAdapter list;
	// HelpScreenAdapter mAdapter;
	private SharedPreferences user_details;
	public static boolean isfirstEntry;
	private String prefname = "SERENA";
	String TAG = "Serena";
	boolean isXlarge = false;
	private int static_images_ids[] = { R.drawable.splashh, R.drawable.login,
			R.drawable.home, R.drawable.homelogout, R.drawable.catalog,
			R.drawable.catalog1, R.drawable.catalogwebview, R.drawable.request,
			R.drawable.requestwebview, R.drawable.approval,
			R.drawable.approvalwebview, R.drawable.search, R.drawable.search1,
			R.drawable.update, R.drawable.galleryy, R.drawable.select_images };

	Button home;
	private MyAdapter mAdapter;
	private ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		mAdapter = new MyAdapter(getSupportFragmentManager(), static_images_ids);
		home = (Button) findViewById(R.id.home);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HelpScreen.this, Second_Btn.class);
				startActivity(intent);
				onBackPressed();
				Second_Btn.powerButton = false;
				Second_Btn.isChildSelected = false;
				HelpScreen.this.finish();
			}
		});
	}

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

	@Override
	protected void onStop() {
		super.onStop();
		System.gc();
		backgroundActivity();
	}

	private void backgroundActivity() {
		// TODO Auto-generated method stub
		MyApplication.setlastTime(System.currentTimeMillis());
		// the following for when app goes to background and is killed my user
		// then to maintain auto reloggin
		long movedBackground = (long) System.currentTimeMillis();
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.putBoolean("backgroundSave", true);
		editor.commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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

			if (!Network_Available.hasConnection(HelpScreen.this) && sec > 60) {
				Intent m = new Intent(HelpScreen.this, MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(HelpScreen.this)
					&& sec > 60) {
				Intent m = new Intent(HelpScreen.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available.hasConnection(HelpScreen.this)
					&& sec < 60) {
				makediallog("No network connection",
						"You must be connected to the internet to use this app");

			} else if (Network_Available.hasConnection(HelpScreen.this)
					&& sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}
		isfirstEntry = true;
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(HelpScreen.this);
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

	public static class MyAdapter extends FragmentPagerAdapter {
		int[] static_images_ids;

		public MyAdapter(FragmentManager fm, int[] static_images_ids) {
			super(fm);
			this.static_images_ids = static_images_ids;
		}

		@Override
		public int getCount() {
			return static_images_ids.length;
		}

		@Override
		public Fragment getItem(int position) {

			return new ImageFragment(static_images_ids[position]);

		}
	}

}
