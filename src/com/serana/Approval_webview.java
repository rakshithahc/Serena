package com.serana;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.serena.connection.Network_Available;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
public class Approval_webview extends Activity {

	Calendar cal;
	static ArrayList<QueueItem> queueItemAdapterforApproval;
	Button attachment;
	public static int disDialog = 0;

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	@SuppressLint("NewApi")
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

			if (!Network_Available.hasConnection(appWebCtx) && sec > 60) {
				Intent m = new Intent(Approval_webview.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(appWebCtx) && sec > 60) {
				Intent m = new Intent(Approval_webview.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();

			} else if (!Network_Available.hasConnection(appWebCtx) && sec < 60) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			} else if (Network_Available.hasConnection(appWebCtx) && sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}
		isfirstEntry = true;
	}

	@SuppressLint("WorldWriteableFiles")
	@Override
	protected void onStop() {
		super.onStop();
		backgroundActivity();
	}

	private void backgroundActivity() {
		// TODO Auto-generated method stub
		MyApplication.setlastTime((long) System.currentTimeMillis());
		// the following for when app goes to background and is killed my user
		// then to maintain auto reloggin
		long movedBackground = (long) System.currentTimeMillis();
		user_details = getSharedPreferences(prefname, MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = user_details.edit();
		editor.putString("name", MyApplication.getUserID());
		editor.putString("password", MyApplication.getPassWord());
		editor.putString("url", MyApplication.getServer());
		editor.putLong("backgroundtime", movedBackground);
		editor.commit();

	}

	private Button goback, next, pre;
	public static boolean isfirstEntry;
	WebView wb;
	private Context appWebCtx;
	private TextView titl, statusText;
	private SharedPreferences user_details;
	private String prefname = "SERENA";
	private static final int PICK_IMAGE = 1;
	int position;
	ArrayList<All_Approval_data_dto> list;
	String final_req_url;

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.approval_webview);
		goback = (Button) findViewById(R.id.backApp);
		statusText = (TextView) findViewById(R.id.statusTextApp);
		queueItemAdapterforApproval = new ArrayList<QueueItem>();
		encode = new ArrayList<QueueString>();
		statusText.setVisibility(View.GONE);
		cal = Calendar.getInstance();
		list = MyApplication.get_Filled_ApprovalList();
		attachment = (Button) findViewById(R.id.attachment);
		goback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// onBackPressed();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				Approval.isRefresh = true;
				finish();
			}
		});

		attachment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callintentforattachment();
			}
		});

		isfirstEntry = false;

		titl = (TextView) findViewById(R.id.approTitle);
		appWebCtx = Approval_webview.this;
		wb = (WebView) findViewById(R.id.plan_web);
		pre = (Button) findViewById(R.id.pre_selection);
		next = (Button) findViewById(R.id.next_selection);
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			// mWebView is some WebView
			wb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		wb.getSettings().setJavaScriptEnabled(true);
		// final String final_req_url = getIntent().getStringExtra("finurl");

		String title = getIntent().getStringExtra("title");
		position = getIntent().getIntExtra("position", 0);
		position = position - 1;
		final_req_url = MyApplication.getServer()
				+ "/tmtrack/srcmobile/index.html?form=request&tableid="
				+ list.get(position).tableId + "&recordid="
				+ list.get(position).requestId + "&projectid="
				+ list.get(position).projectId + "&usrcredentials="
				+ MyApplication.getEncodedUserCred() + "&WEBURL="
				+ MyApplication.getServer() + "&millisec="
				+ System.currentTimeMillis() + "&AUTH_TYPE="
				+ MyApplication.getLoggedInUrlType() + "&USER_LOGIN="
				+ MyApplication.getUserID() + "&USER_PWD="
				+ MyApplication.getPassWord();
		goback.setText("Approvals ("
				+ MyApplication.get_Filled_ApprovalList().size() + ")");
		if (title.length() < 20) {
			titl.setText(title);
		} else {
			titl.setText(title.substring(0, 18));
		}

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("next selection");
				position = position + 1;
				System.out.println("position after next selection is"
						+ position);
				if (position <= list.size()) {
					final_req_url = MyApplication.getServer()
							+ "/tmtrack/srcmobile/index.html?form=request&tableid="
							+ list.get(position).tableId + "&recordid="
							+ list.get(position).requestId + "&projectid="
							+ list.get(position).projectId + "&usrcredentials="
							+ MyApplication.getEncodedUserCred() + "&WEBURL="
							+ MyApplication.getServer() + "&millisec="
							+ System.currentTimeMillis() + "&AUTH_TYPE="
							+ MyApplication.getLoggedInUrlType()
							+ "&USER_LOGIN=" + MyApplication.getUserID()
							+ "&USER_PWD=" + MyApplication.getPassWord();
					wb.clearView();
					wb.loadUrl(final_req_url);
				} else {
					position = position - 1;
				}
			}
		});

		pre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("Pre selection");
				position = position - 1;
				System.out.println("position after pre selection is:"
						+ position);
				if (position >= 0) {
					final_req_url = MyApplication.getServer()
							+ "/tmtrack/srcmobile/index.html?form=request&tableid="
							+ list.get(position).tableId + "&recordid="
							+ list.get(position).requestId + "&projectid="
							+ list.get(position).projectId + "&usrcredentials="
							+ MyApplication.getEncodedUserCred() + "&WEBURL="
							+ MyApplication.getServer() + "&millisec="
							+ System.currentTimeMillis() + "&AUTH_TYPE="
							+ MyApplication.getLoggedInUrlType()
							+ "&USER_LOGIN=" + MyApplication.getUserID()
							+ "&USER_PWD=" + MyApplication.getPassWord();
					wb.clearView();
					wb.loadUrl(final_req_url);
				} else {
					position = position + 1;
				}
			}
		});

		wb.loadUrl(final_req_url);
		// wb.setWebChromeClient(new WebChromeClient());
		wb.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}

			private int webViewPreviousState;
			private final int PAGE_STARTED = 0x1;
			private final int PAGE_REDIRECTED = 0x2;

			@Override
			public boolean shouldOverrideUrlLoading(WebView view,
					String urlNewString) {
				webViewPreviousState = PAGE_REDIRECTED;
				// wb.loadUrl(urlNewString);
				Approval.isRefresh = true;
				Log.v("final url", final_req_url);
				Log.v("webview control", urlNewString);

				if (urlNewString.equalsIgnoreCase("OKWEBVIEW://form=request")) {
					Approval.isRefresh = true;
					Approval.isrefreshRelease = true;
					attachment.setVisibility(View.INVISIBLE);
					next.setVisibility(View.INVISIBLE);
					pre.setVisibility(View.INVISIBLE);
					Approval_webview.this.finish();
				}

				if (urlNewString.equalsIgnoreCase("MASK://param=value")) {
					statusText.setVisibility(View.VISIBLE);
					Approval.isrefreshRelease = true;
					Approval.isRefresh = true;
					goback.setVisibility(View.GONE);
					attachment.setVisibility(View.VISIBLE);
					next.setVisibility(View.GONE);
					pre.setVisibility(View.GONE);
				}
				if (urlNewString.startsWith("http://")
						|| urlNewString.startsWith("https://")) {
					Approval.isRefresh = true;
					String[] urlforattachment = urlNewString
							.split("&FORMSUBMITOK=");
					System.out
							.println("split string is:" + urlforattachment[1]);
					if (urlforattachment[1].equalsIgnoreCase("false")) {
						attachment.setVisibility(View.INVISIBLE);
						next.setVisibility(View.INVISIBLE);
						pre.setVisibility(View.INVISIBLE);
						queueItemAdapterforApproval.clear();
						index = 0;
					} else if (urlforattachment[1].contains("recordid")) {
						attachdataonserver(urlforattachment[1]);
						attachment.setVisibility(View.INVISIBLE);
						next.setVisibility(View.INVISIBLE);
						pre.setVisibility(View.INVISIBLE);
					}

					view.clearView();
					view.loadUrl(urlforattachment[0]);
				}

				if (urlNewString.contains("DELETEWEBVIEW")) {
					Approval.isrefreshRelease = true;
					wb.clearCache(true);
					attachment.setVisibility(View.INVISIBLE);
					next.setVisibility(View.INVISIBLE);
					pre.setVisibility(View.INVISIBLE);
					Approval.isRefresh = true;
					Approval_webview.this.finish();
				}

				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webViewPreviousState = PAGE_STARTED;
				if (CustomDialog.notShowing()) {
					CustomDialog.showProgressDialog(appWebCtx, "", false);
					Log.v("error", "simple demo");
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
					statusText.setVisibility(View.GONE);
					goback.setVisibility(View.VISIBLE);

				}

			}
		});

	}

	public void callintentforattachment() {
		Intent intent = new Intent(Approval_webview.this, SelectOption.class);
		intent.putExtra("value", 3);
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
							+ queueItemAdapterforApproval.size());
					index = queueItemAdapterforApproval.size();
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
			for (int i = 0; i < queueItemAdapterforApproval.size(); i++) {
				/**/
				QueueString en = new QueueString();
				if (queueItemAdapterforApproval.get(i).bitmap != null) {
					System.out.println("bitmap is:"
							+ queueItemAdapterforApproval.get(i).bitmap);
					en.path = convertintobase64(queueItemAdapterforApproval
							.get(i).bitmap);
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

	@SuppressLint("NewApi")
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
		Approval_webview.queueItemAdapterforApproval.clear();
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(appWebCtx);
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
}
