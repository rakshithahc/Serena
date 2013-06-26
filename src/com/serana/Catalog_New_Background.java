package com.serana;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.serena.parser.JSONParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

@SuppressLint("SimpleDateFormat")
class Catalog_New_Background extends AsyncTask<Void, Void, Void> {
	public static int firstItemInCatalog;
	public static final String images_replace_sizes[] = { "48x48", "32x32",
			"72x72", "64x64" };
	static String whatsnew_png;
	static String featured_png;
	static boolean isNew = false, isfeatured = false, isfav = false;
	static String fav_png;
	String dcc = null;
	// .................catalog....................
	private static final String RESULTS = "results";
	public static final String IMAGE = "image";
	private static final String ORDERWEIGHT = "orderWeight";
	private static final String CATALOGID = "catalogId";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String DESCRIPTION = "description";
	private static MyInterface listener;
	private static ArrayList<Drawable> imagelist;
	private static ArrayList<String> dis_names;
	private static ArrayList<String> ids;
	String assosiatetoken;
	private String req_id;
	private ArrayList<All_Approval_Key_dto> alist;
	private ArrayList<All_Approval_data_dto> list;
	static String catalog_url;
	static String catalog_base_image_url;
	static JSONArray catalog_result = null;
	public static HashMap<String, String> catalog_map;
	public static ArrayList<HashMap<String, String>> contactList;
	private String FLAG;
	private ArrayList<All_Approval_data_dto> apprList1;
	Catalog_Services_Background Sb;

	Context localContext;

	public Catalog_New_Background(MyInterface listener, Context c, String x) {
		this.listener = listener;

		String server = MyApplication.getServer();
		// all other catalog url

		whatsnew_png = MyApplication.getServer()
				+ "/tmtrack/images/shell/srp/mobilelibrary/high/applications/whatsnew.png";
		featured_png = MyApplication.getServer()
				+ "/tmtrack/images/shell/srp/mobilelibrary/high/applications/featured.png";
		fav_png = MyApplication.getServer()
				+ "/tmtrack/images/shell/srp/mobilelibrary/high/applications/favorite.png";
		// catalog image url
		catalog_base_image_url = MyApplication.getServer() + "/tmtrack/";
		localContext = c;
		apprList1 = new ArrayList<All_Approval_data_dto>();
		alist = new ArrayList<All_Approval_Key_dto>();
		list = new ArrayList<All_Approval_data_dto>();
		FLAG = x;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		try {
			Second_Btn.menuProgress.setVisibility(View.VISIBLE);
		} catch (Exception e) {
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			ArrayList<String> ccc = DBAdpter.bgmapData();
			MyApplication.mockColors = ccc;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String assosiatetoken = MyApplication.getToken();

		try {
			// to get the list of approval
			alist = DBAdpter.recursUserData(assosiatetoken);
		} catch (Exception e) {
		}

		Second_Btn.server_txt.post(new Runnable() {

			@Override
			public void run() {

				for (int x = 0; x < alist.size(); x++) {
					dcc = alist.get(x).title;
				}
				if (dcc == null) {
					Second_Btn.server_txt.setText("Serena Request Center");
				} else if (dcc == "") {
					Second_Btn.server_txt.setText("Serena Request Center");
				} else if (dcc.equalsIgnoreCase("")) {
					Second_Btn.server_txt.setText("Serena Request Center");
				} else if (dcc.equalsIgnoreCase("null")) {
					Second_Btn.server_txt.setText("Serena Request Center");
				} else {
					if (dcc.length() < 23) {
						Second_Btn.server_txt.setText(dcc);
					} else {
						Second_Btn.server_txt.setText(dcc.substring(0, 21)
								+ "..");
					}
				}
			}
		});

		if (FLAG.equalsIgnoreCase("CALL1")) {
			Log.v("Catalog_New_Background", "First Time");
			assosiatetoken = MyApplication.getToken();

			alist = DBAdpter.recursUserData(assosiatetoken);
			ArrayList<All_Request_data_dto> sorted_requests = new ArrayList<All_Request_data_dto>();
			ArrayList<All_Approval_data_dto> sorted_approvals = new ArrayList<All_Approval_data_dto>();
			Log.v("Catalog_New_Background", "Request");
			try {
				sorted_requests = DBAdpter.requestUserData(assosiatetoken,
						MyApplication.getReqURL());
			} catch (Exception e) {
			}
			try {
				Collections.sort(sorted_requests, byDate);
				MyApplication.setRequstList(sorted_requests);
			} catch (Exception e) {
			}
			Second_Btn.RequestCount.post(new Runnable() {

				@Override
				public void run() {
					Second_Btn.RequestCount.setText(MyApplication
							.getRequestList().size() + "");
				}
			});

			Log.v("Catalog_New_Background", "Approval data");
			// String tok = MyApplication.getToken();
			for (int i = 0; i < alist.size(); i++) {
				req_id = alist.get(i).requestId;
				try {
					sorted_approvals = DBAdpter.approvalUserData(
							assosiatetoken, req_id);
				} catch (Exception e) {
				}
			}

			try {
				Collections.sort(sorted_approvals, byDate_app);
				MyApplication.setApprovalList(sorted_approvals);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Second_Btn.apprcalCount.post(new Runnable() {
				@Override
				public void run() {
					Second_Btn.apprcalCount.setText(MyApplication
							.getApprovalList().size() + "");
				}
			});

			try {
				alldata_catalog();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try {
				Catalog_Services_Background.alldata_services(MyApplication
						.getCatelog_ids().get(0));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		else {
			Log.v("Catalog_New_Background", "Second Time");
			assosiatetoken = MyApplication.getToken();
			// alldata_catalog();
			alist = DBAdpter.recursUserData(assosiatetoken);
			ArrayList<All_Request_data_dto> sorted_requests = new ArrayList<All_Request_data_dto>();
			ArrayList<All_Approval_data_dto> sorted_approvals = new ArrayList<All_Approval_data_dto>();

			try {

				sorted_requests = DBAdpter.requestUserData(assosiatetoken,
						MyApplication.getReqURL());
			} catch (Exception e) {
			}
			Collections.sort(sorted_requests, byDate);
			MyApplication.setRequstList(sorted_requests);
			Second_Btn.RequestCount.post(new Runnable() {

				@Override
				public void run() {
					Second_Btn.RequestCount.setText(MyApplication
							.getRequestList().size() + "");
				}
			});

			String tok = MyApplication.getToken();
			for (int i = 0; i < alist.size(); i++) {
				req_id = alist.get(i).requestId;
				sorted_approvals = DBAdpter.approvalUserData(assosiatetoken,
						req_id);
			}
			try {
				Collections.sort(sorted_approvals, byDate_app);
				MyApplication.setApprovalList(sorted_approvals);
				Second_Btn.apprcalCount.post(new Runnable() {

					@Override
					public void run() {
						Second_Btn.apprcalCount.setText(MyApplication
								.getApprovalList().size() + "");
					}
				});
			} catch (Exception e) {
			}
			try {
				Catalog_Services_Background.alldata_services(MyApplication
						.getCatelog_ids().get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// pd.removeDialog();
		Second_Btn.menuProgress.setVisibility(View.GONE);
		DialogBlock.removeDialog();

	}

	public static void alldata_catalog() {
		catalog_url = MyApplication.getServer() + "/sdf/servicedef/categories/"
				+ MyApplication.getAdvance_id() + "?sortby=weight,name";
		boolean isNewtag = false;
		boolean isFeaturedtag = false;
		boolean isFavtag = false;
		ids = new ArrayList<String>();
		imagelist = new ArrayList<Drawable>();
		dis_names = new ArrayList<String>();
		ArrayList<NewFeaFav> newfeafav = new ArrayList<NewFeaFav>();
		// Checks for "results" filter in json array
		JSONParser jParsera = new JSONParser();
		System.out.println("url for getJSON....." + MyApplication.getServer()
				+ Catalog_Services_Background.service_cat_new_url);
		JSONObject jsonz = jParsera.getJSONFromUrl(MyApplication.getServer()
				+ Catalog_Services_Background.service_cat_new_url);

		JSONArray jarray = null;
		NewFeaFav vvv = null;

		try {
			jarray = jsonz.getJSONArray("results");

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		if (jarray.length() != 0) {

			for (int i = 0; i < jarray.length(); i++) {
				vvv = new NewFeaFav();
				JSONObject p;
				try {
					p = jarray.getJSONObject(i);
					JSONObject properties = p.getJSONObject("properties");
					vvv.whats_new = properties.getString("newtag");

					if (properties.getString("newtag").equalsIgnoreCase("true")) {
						isNewtag = true;
					}
					vvv.featured = properties.getString("featuredtag");

					if (properties.getString("featuredtag").equalsIgnoreCase(
							"true")) {
						isFeaturedtag = true;
					}

					vvv.favour = properties.getString("quicktag");

					if (properties.getString("quicktag").equalsIgnoreCase(
							"true")) {
						isFavtag = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			newfeafav.add(vvv);

			for (int y = 0; y < newfeafav.size(); y++) {
				if (isNewtag) {
					dis_names.add("What's New");
					ids.add("-2");
					imagelist.add(new ExecuteBlobUrlPath()
							.executeUrlPath(whatsnew_png));
				}
				if (isFeaturedtag) {
					dis_names.add("Featured");
					ids.add("-3");
					imagelist.add(new ExecuteBlobUrlPath()
							.executeUrlPath(fav_png));
				}
				if (isFavtag) {
					dis_names.add("Favourites");
					ids.add("-1");
					imagelist.add(new ExecuteBlobUrlPath()
							.executeUrlPath(featured_png));
				}
			}

		} else {
			// not adding any thing
		}

		// Hashmap for ListView
		contactList = new ArrayList<HashMap<String, String>>();

		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();
		System.out.println("url is:" + catalog_url);
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(catalog_url);
		try {
			// Getting Array of Contacts
			catalog_result = json.getJSONArray(RESULTS);

			// JSONArray propert=json.getJSONArray(PROPERTIES);
			// looping through All Contacts
			for (int i = 0; i < catalog_result.length(); i++) {
				JSONObject c = catalog_result.getJSONObject(i);
				String img = c.getString(IMAGE);
				if (img.contains("iconlibrary")) {
					img = img.replace("iconlibrary", "mobilelibrary");
					for (int y = 0; y < images_replace_sizes.length; y++) {
						if (img.contains(images_replace_sizes[y])) {
							img = img.replace(images_replace_sizes[y], "high");

							if (img.contains(" ")) {
								img = img.replace(" ", "%20");
							}
						}
					}
				} else {
					Log.v("Catalog_New_Background", img);

				}
				String orderweight = c.getString(ORDERWEIGHT);
				String catid = c.getString(CATALOGID);
				String nam = c.getString(NAME);
				String id = c.getString(ID);
				String descrip = c.getString(DESCRIPTION);

				if (nam.equalsIgnoreCase("home")) {

				} else {
					catalog_map = new HashMap<String, String>();
					String final_image_url = catalog_base_image_url + img;
					if (img.startsWith("htt")) {
						final_image_url = img;

					} 

					Drawable dd = new ExecuteBlobUrlPath()
							.executeUrlPath(final_image_url);
					imagelist.add(dd);
					dis_names.add(nam);
					// map.put(IMAGE,final_image_url);
					catalog_map.put(ORDERWEIGHT, orderweight);
					catalog_map.put(CATALOGID, catid);
					catalog_map.put(NAME, nam);
					catalog_map.put(ID, id);
					ids.add(id);
					catalog_map.put(DESCRIPTION, descrip);
					contactList.add(catalog_map);
					MyApplication.setCat_images(imagelist);
					MyApplication.setCat_ids(ids);
					MyApplication.setCat_names(dis_names);
					firstItemInCatalog = Integer.valueOf(MyApplication
							.getCatelog_ids().get(0));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
				e.printStackTrace();
			}

			return (d1.getTime() > d2.getTime() ? -1 : 1); // descending
			// return (d1.getTime() > d2.getTime() ? 1 : -1); //ascending
		}
	};

	static final Comparator<All_Approval_data_dto> byDate_app = new Comparator<All_Approval_data_dto>() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

		@SuppressWarnings("unused")
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

}
