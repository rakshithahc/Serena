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
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class Request_webview extends Activity {

	static ArrayList<QueueItem> queueItemAdapterforRequest;
	Calendar cal;
	private static final int PICK_IMAGE = 1;
	Button attachment, next, pre;
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
			if (!Network_Available.hasConnection(ReqWebCtx) && sec > 60) {
				Intent m = new Intent(Request_webview.this,
						MainSerenaActivity.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (Network_Available.hasConnection(ReqWebCtx) && sec > 60) {
				Intent m = new Intent(Request_webview.this, SplashScreen.class);
				m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(m);
				finish();
			} else if (!Network_Available.hasConnection(ReqWebCtx) && sec < 60) {
				if (disDialog == 0) {
					disDialog = 5;
					makediallog("No network connection",
							"You must be connected to the internet to use this app");
				}
			} else if (Network_Available.hasConnection(ReqWebCtx) && sec < 60) {
				// don't do any thing let user stays in same screen with the
				// data loaded previously
			}
		}

		isfirstEntry = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
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
		editor.commit();

	}

	public static boolean isfirstEntry;

	WebView wb;
	private Context ReqWebCtx;
	Button backToreq;
	TextView titl, statusText;
	private SharedPreferences user_details;
	private String prefname = "SERENA";
	int position;
	private ArrayList<All_Request_data_dto> list;

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.request_webview);
		statusText = (TextView) findViewById(R.id.statusTextReq);
		statusText.setVisibility(View.GONE);
		cal = Calendar.getInstance();
		queueItemAdapterforRequest = new ArrayList<QueueItem>();
		encode = new ArrayList<QueueString>();
		attachment = (Button) findViewById(R.id.attachment);
		backToreq = (Button) findViewById(R.id.backReq);
		pre = (Button) findViewById(R.id.pre_selection);
		next = (Button) findViewById(R.id.next_selection);
		list = MyApplication.get_Filled_RequestList();
		backToreq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// onBackPressed();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				Request.isRefresh = true;
				finish();
			}
		});
		isfirstEntry = false;

		titl = (TextView) findViewById(R.id.reqTile);
		ReqWebCtx = Request_webview.this;
		wb = (WebView) findViewById(R.id.plan_web);
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			// mWebView is some WebView
			wb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		wb.getSettings().setJavaScriptEnabled(true);
		// String final_req_url = getIntent().getStringExtra("finurl");
		String title = getIntent().getStringExtra("title");
		String btn = getIntent().getStringExtra("btnTextR");
		position = getIntent().getIntExtra("position", 0);
		if (position != 0) {
			position = position - 1;
		}
		System.out.println("position is" + position);

		String final_req_url = MyApplication.getServer()
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
		backToreq.setText("Requests (" + MyApplication.getRequestList().size()
				+ ")");

		if (title.length() < 23) {
			titl.setText(title);
		} else {
			titl.setText(title.substring(0, 18) + "...");
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
					String final_req_url = MyApplication.getServer()
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
					String final_req_url = MyApplication.getServer()
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
		

		attachment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				callintentforattachment();
			}
		});
		// wb.setWebChromeClient(new WebChromeClient());
		wb.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
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

				Log.v("webview control", urlNewString);
			//	Toast.makeText(Request_webview.this, urlNewString, Toast.LENGTH_LONG).show();
				if (urlNewString.contains("DELETEWEBVIEW")) {
					Request.isRefresh = true;
					attachment.setVisibility(View.INVISIBLE);
					Request.isrefreshRelease = true;
					wb.clearCache(true);
					Request_webview.this.finish();
				}

				if (urlNewString.equalsIgnoreCase("OKWEBVIEW://form=request")) {
					Request.isRefresh = true;
					Request.isrefreshRelease = true;
					attachment.setVisibility(View.INVISIBLE);
					next.setVisibility(View.INVISIBLE);
					pre.setVisibility(View.INVISIBLE);
					Request_webview.this.finish();
				}

				if (urlNewString.equalsIgnoreCase("MASK://param=value")) {
					statusText.setVisibility(View.VISIBLE);
					backToreq.setVisibility(View.GONE);
					attachment.setVisibility(View.VISIBLE);
				//	Toast.makeText(Request_webview.this, "getting mask", Toast.LENGTH_LONG).show();
					next.setVisibility(View.GONE);
					pre.setVisibility(View.GONE);
					Request.isrefreshRelease = true;
					Request.isRefresh = true;

				}
				if (urlNewString.startsWith("http://")||urlNewString.startsWith("https://")) {
					Request.isRefresh = true;
					String[] urlforattachment = urlNewString
							.split("&FORMSUBMITOK=");
					System.out
							.println("split string is:" + urlforattachment[1]);
				//	Toast.makeText(Request_webview.this, "getting http or https", Toast.LENGTH_LONG).show();
					if (urlforattachment[1].equalsIgnoreCase("false")) {
						attachment.setVisibility(View.GONE);
						next.setVisibility(View.INVISIBLE);
						pre.setVisibility(View.INVISIBLE);
						queueItemAdapterforRequest.clear();
						index = 0;
					} else if (urlforattachment[1].contains("recordid")) {
						attachdataonserver(urlforattachment[1]);
						attachment.setVisibility(View.GONE);
						next.setVisibility(View.INVISIBLE);
						pre.setVisibility(View.INVISIBLE);
					}
					view.clearView();
					view.loadUrl(urlforattachment[0]);
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				webViewPreviousState = PAGE_STARTED;
				if (CustomDialog.notShowing()) {
					CustomDialog.showProgressDialog(ReqWebCtx, "", false);
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
					backToreq.setVisibility(View.VISIBLE);
				}
			}
		});
		
		wb.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Request_webview.this)
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
				new AlertDialog.Builder(Request_webview.this)
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

	public void callintentforattachment() {
		Intent intent = new Intent(Request_webview.this, SelectOption.class);
		intent.putExtra("value", 2);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		startActivityForResult(intent, PICK_IMAGE);
	}

	int index = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("request webview activity on  result");
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					System.out
							.println("activity result called for catalog webview class");
					// new accesstheimagesfromUri().execute();
					System.out.println("Catalog list size is"
							+ queueItemAdapterforRequest.size());
					index = queueItemAdapterforRequest.size();
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
			for (int i = 0; i < queueItemAdapterforRequest.size(); i++) {
				/**/
				QueueString en = new QueueString();
				if (queueItemAdapterforRequest.get(i).bitmap != null) {
					System.out.println("bitmap is:"
							+ queueItemAdapterforRequest.get(i).bitmap);
					en.path = convertintobase64(queueItemAdapterforRequest
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
		index = 0;
		encode.clear();
		queueItemAdapterforRequest.clear();
	}

	public void makediallog(String titlte, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReqWebCtx);
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
