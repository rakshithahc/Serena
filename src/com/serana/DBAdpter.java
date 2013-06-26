package com.serana;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.serena.ntlmsupport.NTLMSchemeFactory;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class DBAdpter {
	public static ArrayList<All_Approval_data_dto> fetch_approvalUserData = new ArrayList<All_Approval_data_dto>();

	private static String url_fn_ls;
	private static String project_name;
	private static String tableUUID;

	// it will create the drawable from web url and return the drawable
	public static Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	public static ArrayList<All_sencha_loginData_dto> loginInUser(
			String username, String password, String url) {

		ArrayList<All_sencha_loginData_dto> fetchlogin_Type = new ArrayList<All_sencha_loginData_dto>();
		String result = "";
		// String msg = "";
		InputStream is = null;
		String encPass;
		String addencPass;
		// url_fn_ls = MyApplication.getServer().toString();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		addencPass = username + ":" + password;
		encPass = Base64.encodeToString(addencPass.getBytes(), Base64.DEFAULT
				| Base64.URL_SAFE | Base64.NO_WRAP);
		String Url1 = url
				+ "/tmtrack/tmtrack.dll?JSONPage&command=getssotoken&userinfo=1";
		try {
			HttpClient client = getNewHttpClient();
			HttpGet get = new HttpGet(Url1);
			Log.v("url", Url1);
			get.setHeader("Authorization", "Basic " + encPass);
			MyApplication.setEncodedUserCred(encPass);
			get.setHeader("Content-Type", "application/json");
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();
			int i = responseGet.getStatusLine().getStatusCode();
			MainSerenaActivity.SERVER_RESPONSE = String.valueOf(i);
			SplashScreen.SERVER_RESPONSE = String.valueOf(i);
			if (i == 200) {
				Log.v(":-)", String.valueOf(i));
			} else {
				Log.v(":-(", String.valueOf(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// convert response to string
		try {
			result = convertStreamToString(is);
			Log.v("LOGIN INFORMATION", result);
		} catch (Exception e) {
			// Log.e("log_tag", "Error converting result " + e.toString());
		}

		try {
			JSONObject jObj = new JSONObject(result);
			JSONObject j_objresult = jObj.getJSONObject("result");
			// String j_Array2 = j_objresult.getString("token");
			JSONObject j_Array1 = j_objresult.getJSONObject("user");

			String token_name = j_objresult.getString("token");

			All_sencha_loginData_dto login_dto = new All_sencha_loginData_dto();
			login_dto.serena_login_id = j_Array1.getString("id");
			login_dto.serena_login = j_Array1.getString("login");
			login_dto.serena_avatar = j_Array1.getString("avatar");
			login_dto.serena_token = j_objresult.getString("token");
			login_dto.serena_user_name = j_Array1.getString("name");
			login_dto.setToken(token_name);
			MyApplication.setToken(token_name);

			fetchlogin_Type.add(login_dto);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetchlogin_Type;

	}

	public static ArrayList<All_sencha_token_dto> assotokenUser(String assotoken) {

		ArrayList<All_sencha_token_dto> fetch_assotokenUser = new ArrayList<All_sencha_token_dto>();
		String result = "";

		url_fn_ls = MyApplication.getServer().toString();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));
		String requrl = url_fn_ls + "/sdf/servicedef/services/"
				+ MyApplication.getAdvance_id()
				+ "?tagged=true&sortby=weight,name";
		try {
			result = getDatafromHttpResponsewithHeader(requrl, assotoken);
		} catch (Exception e) {
			// Log.e("log_tag", "Error converting result " + e.toString());
		}

		try {
			JSONObject jObj = new JSONObject(result);
			JSONArray j_Arr_rs = jObj.getJSONArray("results");
			for (int i = 0; i < j_Arr_rs.length(); i++) {
				All_sencha_token_dto assotokenUser = new All_sencha_token_dto();
				JSONObject json_objs = j_Arr_rs.getJSONObject(i);
				assotokenUser.token_service_id = json_objs.getString("id");
				assotokenUser.token_image = json_objs.getString("image");
				assotokenUser.token_summary = json_objs.getString("summary");
				assotokenUser.token_catagory_id = json_objs
						.getString("categoryId");
				assotokenUser.token_order_weight = json_objs
						.getString("orderWeight");
				assotokenUser.token_description = json_objs
						.getString("description");
				assotokenUser.token_hidden = json_objs.getString("hidden");
				assotokenUser.token_enable = json_objs.getString("enabled");
				assotokenUser.token_image_name = json_objs.getString("name");
				JSONObject json_properties = json_objs
						.getJSONObject("properties");

				assotokenUser.token_servicelevel = json_properties
						.getString("servicelevel");
				assotokenUser.token_newtag = json_properties
						.getString("newtag");
				assotokenUser.token_featuretag = json_properties
						.getString("featuredtag");
				assotokenUser.token_quicktag = json_properties
						.getString("quicktag");

				JSONArray j_Arr_serviceActions = json_objs
						.getJSONArray("serviceActions");
				for (int j = 0; j < j_Arr_serviceActions.length(); j++) {
					JSONObject json_objs_serviceActions = j_Arr_serviceActions
							.getJSONObject(j);
					assotokenUser.token_seract_project_name = json_objs_serviceActions
							.getString("projectName");
					assotokenUser.token_seract_action_url = json_objs_serviceActions
							.getString("actionURL");
					assotokenUser.token_seract_table_uuid = json_objs_serviceActions
							.getString("tableUUID");
					assotokenUser.token_seract_solution_uuid = json_objs_serviceActions
							.getString("solutionUUID");
					assotokenUser.token_seract_action_type = json_objs_serviceActions
							.getString("actionType");
					assotokenUser.token_seract_name = json_objs_serviceActions
							.getString("name");

				}
				fetch_assotokenUser.add(assotokenUser);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetch_assotokenUser;

	}

	public static ArrayList<All_sencha_tokenCatagory_dto> assotokenCatagory(
			String assotoken) {

		ArrayList<All_sencha_tokenCatagory_dto> fetch_assotokenCatagory = new ArrayList<All_sencha_tokenCatagory_dto>();
		String result = "";
		url_fn_ls = MyApplication.getServer().toString();
		All_sencha_tokenCatagory_dto dt = new All_sencha_tokenCatagory_dto();
		dt.token_catagory_catName = "What's New";
		dt.token_catagory_catagoryid = "-1";
		fetch_assotokenCatagory.add(dt);
		dt = new All_sencha_tokenCatagory_dto();
		dt.token_catagory_catName = "Features";
		dt.token_catagory_catagoryid = "-2";
		fetch_assotokenCatagory.add(dt);

		dt = new All_sencha_tokenCatagory_dto();
		dt.token_catagory_catName = "Favourite";
		dt.token_catagory_catagoryid = "-3";
		fetch_assotokenCatagory.add(dt);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));
		String requrl = url_fn_ls + "/sdf/servicedef/categories/"
				+ MyApplication.getAdvance_id() + "?sortby=weight,name";
		try {
			result = getDatafromHttpResponsewithHeader(requrl, assotoken);
		} catch (Exception e) {
			// Log.e("log_tag", "Error converting result " + e.toString());
		}

		try {
			JSONObject jObj = new JSONObject(result);
			JSONArray j_Arr_rs = jObj.getJSONArray("results");
			for (int i = 0; i < j_Arr_rs.length(); i++) {
				All_sencha_tokenCatagory_dto assotokencatagory = new All_sencha_tokenCatagory_dto();
				JSONObject json_objs = j_Arr_rs.getJSONObject(i);
				assotokencatagory.token_catagory_image = json_objs
						.getString("image");
				assotokencatagory.token_catagory_catalogid = json_objs
						.getString("catalogId");
				assotokencatagory.token_catagory_catName = json_objs
						.getString("name");
				assotokencatagory.token_catagory_catagoryid = json_objs
						.getString("id");

				assotokencatagory.token_catagory_description = json_objs
						.getString("description");

				fetch_assotokenCatagory.add(assotokencatagory);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetch_assotokenCatagory;

	}

	public static ArrayList<All_sencha_catagoryid_token_dto> catagoryidassotokenUser(
			String assotoken, Integer cataagoryid) {

		ArrayList<All_sencha_catagoryid_token_dto> fetch_catagoryidassotokenUser = new ArrayList<All_sencha_catagoryid_token_dto>();
		String result = "";
		url_fn_ls = MyApplication.getServer().toString();
		String url = url_fn_ls + "/sdf/servicedef/services/"
				+ MyApplication.getAdvance_id() + "?";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));
		String requrl = url + "categoryid=" + cataagoryid
				+ "&sortby=weight,name";
		try {
			result = getDatafromHttpResponsewithHeader(requrl, assotoken);
		} catch (Exception e) {
			// /Log.e("log_tag", "Error converting result " + e.toString());
		}

		try {
			JSONObject jObj = new JSONObject(result);
			JSONArray j_Arr_rs = jObj.getJSONArray("results");
			for (int i = 0; i < j_Arr_rs.length(); i++) {
				All_sencha_catagoryid_token_dto catagoryidassotokenUser = new All_sencha_catagoryid_token_dto();
				JSONObject json_objs = j_Arr_rs.getJSONObject(i);
				catagoryidassotokenUser.catagoryid_token_service_id = json_objs
						.getString("id");
				catagoryidassotokenUser.catagoryid_token_image = json_objs
						.getString("image");
				catagoryidassotokenUser.catagoryid_token_summary = json_objs
						.getString("summary");
				catagoryidassotokenUser.catagoryid_token_catagory_id = json_objs
						.getString("categoryId");

				catagoryidassotokenUser.catagoryid_token_order_weight = json_objs
						.getString("orderWeight");
				catagoryidassotokenUser.catagoryid_token_description = json_objs
						.getString("description");
				catagoryidassotokenUser.catagoryid_token_hidden = json_objs
						.getString("hidden");
				catagoryidassotokenUser.catagoryid_token_enable = json_objs
						.getString("enabled");
				catagoryidassotokenUser.catagoryid_token_image_name = json_objs
						.getString("name");
				JSONObject json_properties = json_objs
						.getJSONObject("properties");

				catagoryidassotokenUser.catagoryid_token_newtag = json_properties
						.getString("newtag");
				catagoryidassotokenUser.catagoryid_token_featuretag = json_properties
						.getString("featuredtag");
				catagoryidassotokenUser.catagoryid_token_quicktag = json_properties
						.getString("quicktag");

				JSONArray j_Arr_serviceActions = json_objs
						.getJSONArray("serviceActions");
				for (int j = 0; j < j_Arr_serviceActions.length(); j++) {
					JSONObject json_objs_serviceActions = j_Arr_serviceActions
							.getJSONObject(j);
					catagoryidassotokenUser.catagoryid_token_seract_project_name = json_objs_serviceActions
							.getString("projectName");
					catagoryidassotokenUser.catagoryid_token_seract_action_url = json_objs_serviceActions
							.getString("actionURL");
					catagoryidassotokenUser.catagoryid_token_seract_table_uuid = json_objs_serviceActions
							.getString("tableUUID");
					catagoryidassotokenUser.catagoryid_token_seract_solution_uuid = json_objs_serviceActions
							.getString("solutionUUID");
					catagoryidassotokenUser.catagoryid_token_seract_action_type = json_objs_serviceActions
							.getString("actionType");
					catagoryidassotokenUser.catagoryid_token_seract_name = json_objs_serviceActions
							.getString("name");
				}
				fetch_catagoryidassotokenUser.add(catagoryidassotokenUser);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetch_catagoryidassotokenUser;
	}

	public static ArrayList<All_Request_data_dto> requestUserData(
			String assotoken, String requrl) {
		ArrayList<All_Request_data_dto> fetch_requestUserData = new ArrayList<All_Request_data_dto>();
		All_Request_data_dto requestdto_data = null;
		String result = "";
		url_fn_ls = MyApplication.getServer().toString();
		// String key = MyApplication.getReqId_or_string();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));
		try {
			Log.v("url", requrl);
			result = getDatafromHttpResponsewithHeader(requrl, assotoken);
			Log.v("DBAdpter", "we are not using NTLM");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			JSONObject jObj = null;
			jObj = new JSONObject(result);
			JSONObject request = jObj.getJSONObject("request");
			JSONObject count = request.getJSONObject("pageinfo");
			String cnt = count.getString("totalrec");

			Log.v("REQUEST COUNT", cnt);
			JSONArray j_Arr_rs = jObj.getJSONArray("results");
			for (int i = 0; i < j_Arr_rs.length(); i++) {
				JSONObject json_objs = j_Arr_rs.getJSONObject(i);
				requestdto_data = new All_Request_data_dto();
				JSONArray j_Arr_row = json_objs.getJSONArray("rows");
				for (int j = 0; j < j_Arr_row.length(); j++) {
					requestdto_data.totalRequests = cnt;

					JSONObject json_objs_row = j_Arr_row.getJSONObject(j);
					requestdto_data = new All_Request_data_dto();

					requestdto_data.tableId = json_objs.getString("tableid");
					requestdto_data.projectName = json_objs
							.getString("projectname");

					requestdto_data.projectId = json_objs
							.getString("projectid");
					requestdto_data.requestId = json_objs_row.getString("id");

					Log.v("",
							json_objs.getString("tableid") + "\n"
									+ json_objs.getString("projectname") + "\n"
									+ json_objs.getString("projectid") + "\n"
									+ json_objs_row.getString("id"));

					requestdto_data.displayId = json_objs_row
							.getString("displayid");
					requestdto_data.issueId = json_objs_row
							.getString("ISSUEID");
					requestdto_data.title = json_objs_row.getString("TITLE");
					Log.v("Request projectTITLE" + j,
							json_objs_row.getString("TITLE"));
					Log.v("Request list size" + j, fetch_requestUserData.size()
							+ "");

					requestdto_data.state = json_objs_row.getString("STATE");

					JSONObject json_lastmodifierdate = json_objs_row
							.getJSONObject("LASTMODIFIEDDATE");

					requestdto_data.lastModifiedDate = json_lastmodifierdate
							.getString("text");
					String ff = json_lastmodifierdate.getString("text");
					String ha = ff.substring(11, 22);
					try {
						String h = time_to_days_ago(ff, ha);
						requestdto_data.daysago = h;
						System.out.println("value of h is:" + h + "----------"
								+ requestdto_data.daysago);
					} catch (Exception e) {
						// TODO: handle exception
					}

					try {
						if (json_objs_row.getJSONObject("LASTMODIFIER") != null) {
							JSONObject json_LASTMODIFIER = json_objs_row
									.getJSONObject("LASTMODIFIER");
							requestdto_data.lastModifier = json_LASTMODIFIER
									.getString("text");
						} else {
							requestdto_data.lastModifier = "";
						}

					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						JSONObject json_LASTSTATECHANGER = json_objs_row
								.getJSONObject("LASTSTATECHANGER");
						requestdto_data.lastStateChanger = json_LASTSTATECHANGER
								.getString("text");
					} catch (Exception e) {
					}
					try {
						JSONObject json_OWNER = json_objs_row
								.getJSONObject("OWNER");
						requestdto_data.owner = json_OWNER.getString("text");
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						JSONObject json_SUBMITDATE = json_objs_row
								.getJSONObject("SUBMITDATE");
						requestdto_data.submitDate = json_SUBMITDATE
								.getString("text");
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						JSONObject json_SUBMITTER = json_objs_row
								.getJSONObject("SUBMITTER");
						requestdto_data.submitter = json_SUBMITTER
								.getString("text");
					} catch (Exception e) {
					}
					fetch_requestUserData.add(requestdto_data);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetch_requestUserData;
	}

	public static ArrayList<All_Approval_data_dto> approvalUserData(
			String assotoken, String req_id) {
		All_Approval_data_dto approvaldto_data;
		// int finalCount = 0;
		String result = "";
		url_fn_ls = MyApplication.getServer().toString();

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));
		String url = url_fn_ls
				+ "/tmtrack/tmtrack.dll?shell=srp&ReportPage&template=reports%2Fjsonlist&ReportId="
				+ Integer.parseInt(req_id) + "&advanced=1&recno=-1";
		try {
			Log.v("url", url);

			result = getDatafromHttpResponsewithHeader(url, assotoken);
			Log.v("DBAdpter", "we are not using NTLM");

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			JSONObject jObj = new JSONObject(result);
			JSONObject request = jObj.getJSONObject("request");
			JSONObject count = request.getJSONObject("pageinfo");
			String cnt = count.getString("totalrec");
			Log.v("APPROVAL COUNT", cnt);
			JSONArray j_Arr_rs = jObj.getJSONArray("results");

			for (int i = 0; i < j_Arr_rs.length(); i++) {
				JSONObject json_objs = j_Arr_rs.getJSONObject(i);
				approvaldto_data = new All_Approval_data_dto();
				approvaldto_data.projectId = json_objs.getString("projectid");
				try {
					if (json_objs.getString("tableid") != null) {
						approvaldto_data.tableId = json_objs
								.getString("tableid");
					} else {
						approvaldto_data.tableId = "";
					}
				} catch (Exception e) {
				}
				try {
					if (json_objs.getString("projectname") != null) {
						approvaldto_data.projectName = json_objs
								.getString("projectname");
					} else {
						approvaldto_data.projectName = "";
					}
				} catch (Exception e) {
				}
				JSONArray j_Arr_row = json_objs.getJSONArray("rows");
				for (int j = 0; j < j_Arr_row.length(); j++) {
					JSONObject json_objs_row = j_Arr_row.getJSONObject(j);
					approvaldto_data = new All_Approval_data_dto();
					approvaldto_data.totalApprovalCnt = cnt;

					approvaldto_data.projectId = json_objs
							.getString("projectid");
					try {
						if (json_objs.getString("tableid") != null) {
							approvaldto_data.tableId = json_objs
									.getString("tableid");
						} else {
							approvaldto_data.tableId = "";
						}
					} catch (Exception e) {
					}
					try {
						if (json_objs.getString("projectname") != null) {
							approvaldto_data.projectName = json_objs
									.getString("projectname");
						} else {
							approvaldto_data.projectName = "";
						}
					} catch (Exception e) {
					}

					try {
						if (json_objs_row.getString("id") != null) {
							approvaldto_data.requestId = json_objs_row
									.getString("id");
						} else {
							approvaldto_data.requestId = "";
						}
					} catch (Exception e) {
					}
					try {
						if (json_objs_row.getString("displayid") != null) {
							approvaldto_data.displayId = json_objs_row
									.getString("displayid");
						} else {
							approvaldto_data.displayId = "";
						}
					} catch (Exception e) {
					}
					try {
						if (json_objs_row.getString("ISSUEID") != null) {
							approvaldto_data.issueId = json_objs_row
									.getString("ISSUEID");
						} else {
							approvaldto_data.issueId = "";
						}
					} catch (Exception e) {
					}
					try {
						if (json_objs_row.getString("TITLE") != null) {
							approvaldto_data.title = json_objs_row
									.getString("TITLE");
						} else {
							approvaldto_data.title = "";
						}
						Log.v("Approval projectTITLE" + j,
								json_objs_row.getString("TITLE"));
					} catch (Exception e) {
					}
					try {
						if (json_objs_row.getString("STATE") != null) {
							approvaldto_data.state = json_objs_row
									.getString("STATE");
						} else {
							approvaldto_data.state = "";
						}
					} catch (Exception e) {
					}
					try {
						if (json_objs_row.getString("LASTMODIFIEDDATE") != null) {
							JSONObject json_lastmodifierdate = json_objs_row
									.getJSONObject("LASTMODIFIEDDATE");
							String s = json_lastmodifierdate.getString("text");
							String data = json_lastmodifierdate
									.getString("text");
							String ti = data.substring(11, 22);
							try {
								String h = time_to_days_ago(data, ti);

								approvaldto_data.daysago = h;
								System.out.println("value of h is:" + h
										+ "-----" + approvaldto_data.daysago);
							} catch (Exception e) {
							}

							if (!s.equalsIgnoreCase("")) {
								approvaldto_data.lastModifiedDate = json_lastmodifierdate
										.getString("text");

								Log.v("not null",
										approvaldto_data.lastModifiedDate);
							} else {
								approvaldto_data.lastModifiedDate = "12/20/1232 04:27:26 AM";
								Log.v("null", approvaldto_data.lastModifiedDate);
							}
						} else {
							approvaldto_data.lastModifiedDate = "12/20/1232 04:27:26 AM";
							Log.v("null", approvaldto_data.lastModifiedDate);
						}
					} catch (Exception e) {
						approvaldto_data.lastModifiedDate = "12/20/1232 04:27:26 AM";
						Log.v("null", approvaldto_data.lastModifiedDate);
					}
					try {
						if (json_objs_row.getJSONObject("OWNER") != null) {
							JSONObject json_OWNER = json_objs_row
									.getJSONObject("OWNER");
							approvaldto_data.owner = json_OWNER
									.getString("text");
						} else {
							approvaldto_data.owner = "";
						}
					} catch (Exception e) {
					}
					try {
						if (json_objs_row.getJSONObject("SUBMITDATE") != null) {
							JSONObject json_SUBMITDATE = json_objs_row
									.getJSONObject("SUBMITDATE");
							approvaldto_data.submitDate = json_SUBMITDATE
									.getString("text");
						} else {
							approvaldto_data.submitDate = "";
						}

					} catch (Exception e) {
					}
					try {
						if (json_objs_row.getJSONObject("SUBMITTER") != null) {
							JSONObject json_SUBMITTER = json_objs_row
									.getJSONObject("SUBMITTER");
							approvaldto_data.submitter = json_SUBMITTER
									.getString("text");
						} else {
							approvaldto_data.submitter = "";
						}
					} catch (Exception e) {
					}
					fetch_approvalUserData.add(approvaldto_data);
				}
			}
		} catch (JSONException e) {
			// Log.e("log_tag", e.getMessage());
		}
		return fetch_approvalUserData;
	}

	@SuppressWarnings("rawtypes")
	public static ArrayList<All_Approval_Key_dto> recursUserData(
			String assotoken) {
		String id = null, reqkey = null, name = null, title = null, request_report = null;

		All_Approval_Key_dto approvaldto_Key;
		ArrayList<All_Approval_Key_dto> fetchrecursUserData = new ArrayList<All_Approval_Key_dto>();
		ArrayList<String> reqUrl;
		String result = "";
		try {
			url_fn_ls = MyApplication.getServer().toString();
		} catch (Exception e) {
		}
		project_name = MyApplication.get_project_name();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));

		try {
			String url = url_fn_ls + "/sdf/servicedef/catalog/srp";
			result = getDatafromHttpResponsewithHeader(url, assotoken);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			JSONObject jObj = new JSONObject(result);
			JSONObject jsonResult = jObj.getJSONObject("results");
			// Log.v("log_tag", "json result Array :   " + jsonResult);
			JSONObject pro_object = jsonResult.getJSONObject("properties");
			Log.v("ProPerties", pro_object + "");
			String id_object = jsonResult.getString("id");
			Log.v("id......................", id_object + "");
			MyApplication.setAdvance_id(id_object);
			approvaldto_Key = new All_Approval_Key_dto();

			try {
				request_report = pro_object.getString("requests_report");
				Log.v("request_report", request_report);
			} catch (Exception e) {
			}
			try {
				reqkey = pro_object.getString("requests_report_id");
				Log.v("req key is", reqkey);
			} catch (Exception e) {

			}
			try {
				name = pro_object.getString("requests_report_name");
				Log.v("name is:", name);
			} catch (Exception e) {
			}
			try {
				title = pro_object.getString("title");
				Log.v("title is:", title);
				MyApplication.setServerName(pro_object.getString("title"));
			} catch (Exception e) {
			}

		} catch (Exception e) {
		}

		if (request_report == null) {
			MyApplication.setreqURL("");
			if (reqkey == null) {
				MyApplication.setreqURL("");
				if (name == null) {
					// default json;
					MyApplication.setreqURL("");
				} else {
					String c2 = MyApplication.getServer()
							+ "/tmtrack/tmtrack.dll?shell=srp&ReportPage&template=reports%2Fjsonlist&ReportRef="
							+ name + "&advanced=1&recno=-1";
					Log.v("value of c2 is :", c2);
					MyApplication.setreqURL(c2);
				}
			} else {
				String id_url1 = MyApplication.getServer()
						+ "/tmtrack/tmtrack.dll?shell=srp&ReportPage&template=reports%2Fjsonlist&ReportId="
						+ reqkey + "&advanced=1&recno=-1";

				Log.v("value of id_url1 is:", id_url1);
				MyApplication.setreqURL(id_url1);

			}
		} else {
			String id_url2 = MyApplication.getServer()
					+ "/tmtrack/tmtrack.dll?shell=srp&ReportPage&template=reports%2Fjsonlist&ReportRef="
					+ request_report + "&advanced=1&recno=-1";
			Log.v("value of id_url2 is:", id_url2);
			MyApplication.setreqURL(id_url2);
		}

		reqUrl = new ArrayList<String>();

		String id_url1 = MyApplication.getServer()
				+ "/tmtrack/tmtrack.dll?shell=srp&ReportPage&template=reports%2Fjsonlist&ReportId="
				+ reqkey + "&advanced=1&recno=-1";
		reqUrl.add(id_url1);
		String c2 = MyApplication.getServer()
				+ "/tmtrack/tmtrack.dll?shell=srp&ReportPage&template=reports%2Fjsonlist&ReportRef="
				+ name + "&advanced=1&recno=-1";
		reqUrl.add(c2);
		String id_url2 = MyApplication.getServer()
				+ "/tmtrack/tmtrack.dll?shell=srp&ReportPage&template=reports%2Fjsonlist&ReportRef="
				+ request_report + "&advanced=1&recno=-1";
		reqUrl.add(id_url2);
		MyApplication.setrequrl(reqUrl);

		Log.v("ALL DATA ", id + "\n request id " + reqkey + "\n"
				+ "\n request report name" + name + "\n SERVERNAME   " + title
				+ "\n reuests_reports  " + request_report);
		MyApplication.setServerName(title);

		try {

			Log.v("TAG", "second json");
			if (result != null) {
				JSONObject jObj = new JSONObject(result);
				JSONObject jsonResult = jObj.getJSONObject("results");
				JSONObject pro_object = jsonResult.getJSONObject("properties");
				Iterator keys = pro_object.keys();

				while (keys.hasNext()) {
					approvaldto_Key = new All_Approval_Key_dto();

					// loop to get the dynamic key
					String currentDynamicKey = (String) keys.next();
					// /String value = pro_object.getString(currentDynamicKey);

					String upToEightCharacters = currentDynamicKey.substring(0,
							Math.min(currentDynamicKey.length(), 8));

					if (upToEightCharacters.startsWith("reportId")) {
						approvaldto_Key.title = title;
						approvaldto_Key.requestId = pro_object
								.getString(currentDynamicKey);
						Log.v("Approval id",
								pro_object.getString(currentDynamicKey));

						fetchrecursUserData.add(approvaldto_Key);
					}
				}
			} else {
				fetchrecursUserData.clear();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetchrecursUserData;
	}

	public static ArrayList<All_Service_webforms_data_key_dto> servicewebformKeys(
			String assotoken, String projectname, String tableUUid) {
		ArrayList<All_Service_webforms_data_key_dto> web_UserData = new ArrayList<All_Service_webforms_data_key_dto>();

		All_Service_webforms_data_key_dto service_dto = null;
		String result = "";
		url_fn_ls = MyApplication.getServer().toString();
		project_name = projectname;
		tableUUID = tableUUid;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));

		try {
			String finUrl = url_fn_ls
					+ "/tmtrack/tmtrack.dll?JSONPage&Command=metadatasubmit&projectName="
					+ project_name + "&tableUUID=" + tableUUID;
			result = getDatafromHttpResponsewithHeader(finUrl, assotoken);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			JSONObject jObj = new JSONObject(result);
			JSONObject j = jObj.getJSONObject("request");
			String projId = j.getString("projectId");
			String tabid = j.getString("tableId");
			Log.v(projId, tabid);
			service_dto = new All_Service_webforms_data_key_dto();
			service_dto.projectId = projId;
			service_dto.tableId = tabid;
		} catch (Exception e) {
		}
		web_UserData.add(service_dto);
		return web_UserData;
	}

	public static ArrayList<All_Sla_data_dto> slaUserData(String assotoken,
			String TSM) {
		ArrayList<All_Sla_data_dto> sla_UserData = new ArrayList<All_Sla_data_dto>();

		All_Sla_data_dto sla_data = null;
		String result = "";
		url_fn_ls = MyApplication.getServer().toString();
		project_name = TSM;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("assotoken", assotoken));
		String url = url_fn_ls
				+ "/sla/slaServices/reportService/getSLADefinitionsByProject/"
				+ project_name + "?sdfServerURL=" + url_fn_ls;
		try {
			result = getDatafromHttpResponsewithHeader(url, assotoken);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// convert response to string

		try {
			JSONObject jObj = new JSONObject(result);
			JSONArray j_Arr_rs = jObj.getJSONArray("results");
			if (jObj.getJSONArray("results") != null) {

				for (int i = 0; i < j_Arr_rs.length(); i++) {
					JSONObject json_objs = j_Arr_rs.getJSONObject(i);
					sla_data = new All_Sla_data_dto();
					// try {
					if (json_objs.getString("name") != null) {
						MyApplication.setSLA("TRUE");
						sla_data.name = json_objs.getString("name");
						sla_data.description = json_objs
								.getString("description");
						sla_UserData.add(sla_data);
						Log.v("DATA SIZE", sla_UserData.size() + "");
					} else {
						sla_data.name = "";
						sla_data.description = "";
						sla_UserData.add(sla_data);
					}
				}
			} else {
				sla_data.name = "";
				sla_data.description = "";
				sla_UserData.add(sla_data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sla_UserData;
	}

	public static String tmtrackcheck(String url, String check) {
		String status = null;
		try {
			if (check.equalsIgnoreCase("tmtr")) {

				int i = getIntValuefromHttpResponseWithHeader(url);
				String tmr = String.valueOf(i);
				Log.v("tmtrack present address", tmr);
				status = tmr;
				// MainSerenaActivity.TMTRACK_RESPONSE=tmr;
			} else if (check.equalsIgnoreCase("web")) {

				int i = getIntValuefromHttpResponseWithHeader(url);
				String tmr = String.valueOf(i);
				status = tmr;
				Log.v("webURL", tmr);
				// MainSerenaActivity.WEBVIEW_RESPONSE=tmr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	@SuppressLint({ "NewApi" })
	public static String time_to_days_ago(String date, String time) {

		String res = "";

		long millis1 = System.currentTimeMillis();
		long millis2 = Date.parse(date);

		Calendar calInitial = Calendar.getInstance();
		int offsetInitial = calInitial.get(Calendar.ZONE_OFFSET)
				+ calInitial.get(Calendar.DST_OFFSET);

		long current = millis1 - offsetInitial;

		System.out.println("SYS TIME date is" + date);
		Log.v("CURRENT SYS TIME", Long.toString(current));
		Calendar javaCalendar = null;
		String currentDate = "";
		String dat = "";
		String mon = "";
		javaCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		if (javaCalendar.get(Calendar.DATE) < 10) {
			dat = "0" + javaCalendar.get(Calendar.DATE);
		} else {
			dat = "" + javaCalendar.get(Calendar.DATE);
		}

		if ((javaCalendar.get(Calendar.MONTH) + 1) < 10) {
			mon = "0" + (javaCalendar.get(Calendar.MONTH) + 1);
		} else {
			mon = "" + (javaCalendar.get(Calendar.MONTH) + 1);
		}

		currentDate = mon + "/" + dat + "/" + javaCalendar.get(Calendar.YEAR);
		System.out.println("ddd value is" + currentDate);
		long difference = current - millis2;
		long days = TimeUnit.MILLISECONDS.toDays(difference);

		String[] servertime = null;
		try {
			servertime = date.split(" ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int dd = 0;

		if (currentDate.equalsIgnoreCase(servertime[0])) {
			dd = 1;
		} else {
			dd = 2;
		}

		System.out.println("value of dd is......" + currentDate
				+ "________________" + servertime[0] + "________________"
				+ days);

		if (days == 0 && dd == 1) {
			res = "Today" + " ," + time;
			Log.v("TOday", "Came today");
		} else if ((days == 0 && dd == 2) || days == 1) {
			res = "Yesterday";
			Log.v("1 WEEK", "yester day");
		} else if (days == 2) {
			res = "2 days ago";
		} else if (days < 7 && days > 2) {
			res = "This week";
		} else if (days < 14 && days >= 7) {
			res = "Last week";
		} else if (days < 30 && days >= 14) {
			res = "Within a month";
		} else if (days < 60 && days >= 30) {
			res = "1 month ago";
			Log.v("1 month", " 1 month ago");
		} else if (days < 90 && days >= 60) {
			res = "3 months ago";
			Log.v("2 month", " 2 month ago");
			// do whatever when it's been a month or more
		} else if (days < 180 && days >= 90) {
			res = "6 months ago";
			Log.v("3 month", " 3 month ago");
			// do whatever when it's been a month or more
		} else if (days < 360 && days >= 180) {
			res = "1 year ago";
		} else if (days >= 360) {
			res = "1 year ago";
			Log.v("1 year", "1 year ago");
		}

		return res;
	}

	public static int FirstBasicUrlStatuCheck(String url) {
		int i = 0;
		try {
			i = getIntValuefromHttpResponse(url);
			String statuscode = String.valueOf(i);
			Log.v("Status of server existance", statuscode);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return i;
	}

	public static String FirstBasicUrlCheck(String url) {
		String status = null;
		String result = null;
		String sso = null;
		try {
			result = getDatafromHttpResponse(url);

			try {
				JSONObject jObj = new JSONObject(result);
				JSONObject j = jObj.getJSONObject("result");
				sso = j.getString("token");
				Log.v("new token", sso);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return sso;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public static String SecondBasicUrlCheck(String url) {
		InputStream is = null;
		String result = null;
		String ssotoken = null;
		try {
			HttpClient client = getNewHttpClient();
			Log.v("uRl", url);
			HttpGet get = new HttpGet(url);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();

			int i = responseGet.getStatusLine().getStatusCode();
			String statuscode = String.valueOf(i);
			MainSerenaActivity.SEC_URL_CHECK = statuscode;
			Log.v("Status", statuscode);
			result = convertStreamToString(is);

			try {
				JSONObject jObj = new JSONObject(result);
				JSONObject j_objresult = jObj.getJSONObject("result");
				ssotoken = j_objresult.getString("token");

				MyApplication.setToken(ssotoken);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ssotoken;
	}

	public static int BasicUrlCheck(String url) {
		int i = 0;
		i = getIntValuefromHttpResponse(url);
		return i;
	}

	public static HttpClient getNewHttpClient() {
		try {

			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			// System.out.println("trust score is :"+ trustStore);
			// SSLSocketFactory can be used to validate the identity of the
			// HTTPS server against a list of trusted
			// certificates and to authenticate to the HTTPS server using a
			// private key.
			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			// Represents a collection of HTTP protocol and framework
			// parameters.
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			// A set of supported protocol schemes. Schemes are identified by
			// lowercase names.
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			HttpClient client = new DefaultHttpClient(ccm, params);

			((AbstractHttpClient) client).getAuthSchemes().register("NTLM",
					new NTLMSchemeFactory());

			NTCredentials creds = new NTCredentials(MyApplication.getUserID(),
					MyApplication.getPassWord(), "", "");

			((AbstractHttpClient) client).getCredentialsProvider()
					.setCredentials(AuthScope.ANY, creds);
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
			return client;
		} catch (Exception e) {
			return getNewHttpClient();
		}
	}

	public static ArrayList<String> bgmapData() throws Exception {
		System.out.println("bgmap testing");
		String result = null;
		ArrayList<String> mocks = null;
		String url = MyApplication.getServer()
				+ "/tmtrack/images/shell/srp/mobilelibrary/bgmap.txt";

		result = getDatafromHttpResponse(url);
		try {
			JSONObject jObj = new JSONObject(result);
			JSONArray json_Arry = jObj.getJSONArray("imgbgmap");
			mocks = new ArrayList<String>();
			for (int y = 0; y < json_Arry.length(); y++) {
				// mocks=new ArrayList<String>();
				JSONObject jobj = json_Arry.getJSONObject(y);
				Log.v("mock" + y, jobj.getString("bg"));
				String data = jobj.getString("bg");
				String g = data.substring(0, data.indexOf("."));
				Log.v("data needed", g);
				mocks.add(g);
			}
		} catch (Exception e) {
		}

		return mocks;
	}

	public static String getDatafromHttpResponse(String url) {
		String result = null;
		InputStream is = null;
		try {
			HttpClient client = getNewHttpClient();
			HttpGet get = new HttpGet(url);
			Log.v("URL", url);
			get.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();
			result = convertStreamToString(is);
			Log.v("Response", result);
			int i = responseGet.getStatusLine().getStatusCode();
			String statuscode = String.valueOf(i);
			Log.v("Status", statuscode);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	public static String getDatafromHttpResponsewithHeader(String url,
			String sso) {
		String result = null;
		InputStream is = null;
		try {
			HttpClient client = getNewHttpClient();
			Log.v("URL", url);
			HttpGet get = new HttpGet(url);
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-Type", "application/json");
			get.setHeader("Alfssoauthntoken", sso);
			get.setHeader("User-Agent", "Mozilla/5.0");
			get.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();
			result = convertStreamToString(is);
			Log.v("Response", result);
			int i = responseGet.getStatusLine().getStatusCode();
			String statuscode = String.valueOf(i);
			Log.v("Status", statuscode);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	public static String getDatafromHttpwithHeader(String url, String sso) {
		String result = null;
		InputStream is = null;
		try {
			HttpClient client = getNewHttpClient();
			Log.v("URL", url);
			HttpGet get = new HttpGet(url);
			get.setHeader("Accept", "application/json");
			get.setHeader("Content-Type", "application/json");
			get.setHeader("Alfssoauthntoken", sso);
			get.setHeader("User-Agent", "Mozilla/5.0");
			get.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();
			result = convertStreamToString(is);
			Log.v("Response", result);
			int i = responseGet.getStatusLine().getStatusCode();
			String statuscode = String.valueOf(i);
			Log.v("Status", statuscode);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	public static String getVersionfromHttp() {
		String result = null;
		InputStream is = null;
		String url = MyApplication.getServer()
				+ "/tmtrack/srcmobile/version.html";
		try {
			HttpClient client = getNewHttpClient();
			Log.v("URL", url);
			HttpGet get = new HttpGet(url);
			get.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();
			result = convertStreamToString(is);
			Log.v("Response", result);
			int i = responseGet.getStatusLine().getStatusCode();
			String statuscode = String.valueOf(i);
			Log.v("Status", statuscode);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	@SuppressWarnings("unused")
	public static int getIntValuefromHttpResponse(String url) {
		int result = 0;
		InputStream is = null;
		try {
			HttpClient client = getNewHttpClient();
			Log.v("URL", url);
			HttpGet get = new HttpGet(url);
			get.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();
			result = responseGet.getStatusLine().getStatusCode();
			String statuscode = Integer.toString(result);
			Log.v("Status", statuscode);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	@SuppressWarnings("unused")
	public static int getIntValuefromHttpResponseWithHeader(String url) {
		int result = 0;
		InputStream is = null;
		try {
			Log.v("URL", url);
			HttpClient client = getNewHttpClient();
			HttpGet get = new HttpGet(url);
			get.setHeader("Content-Type", "application/json");
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			is = resEntityGet.getContent();
			result = responseGet.getStatusLine().getStatusCode();
			String statuscode = Integer.toString(result);
			Log.v("Status", statuscode);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return result;
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static int sendRequestforAttachment(JSONObject object,
			String urlNewString, String ssotoken) {
		// TODO Auto-generated method stub
		int i = 0;
		String server = MyApplication.getServer().toString();
		String req_url = server
				+ "/tmtrack/tmtrack.dll?JSONPage&Command=addfile&"
				+ urlNewString;
		String result = postdataHttpwithHeader(req_url, ssotoken, object);
		System.out.println("result is:" + result);
		try {
			JSONObject jObj = new JSONObject(result);
			JSONObject j_objresult = null;
			try {
				j_objresult = jObj.getJSONObject("results");
				if (j_objresult.toString().equalsIgnoreCase("results")) {
					i = 5;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			JSONObject j_objError = null;
			try {
				j_objError = jObj.getJSONObject("error");
				if (j_objError.toString().equalsIgnoreCase("error")) {
					i = 1;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		return i;
	}

	public static String postdataHttpwithHeader(String url, String sso,
			JSONObject object) {
		String result = null;
		System.out.println("request url is:" + url);
		// HttpClient httpclient = new DefaultHttpClient();
		HttpClient httpclient = getNewHttpClient();
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		HttpConnectionParams.setSoTimeout(myParams, 10000);

		String json = object.toString();
		System.out.println("json in string is:" + json);

		try {

			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Alfssoauthntoken", sso);
			StringEntity se = new StringEntity(object.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			httppost.setEntity(se);
			httppost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			HttpResponse response = httpclient.execute(httppost);
			result = EntityUtils.toString(response.getEntity());
			Log.i("tag", result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}