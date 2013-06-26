package com.serana;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.serena.parser.JSONParserLogin;

public class search_background {
	public static final String images_replace_sizes[] = { "48x48", "32x32",
			"72x72", "64x64" };

	private static String service_base_image_url;

	static String final_url;
	ProgressDialog pd;
	static JSONArray service_result;
	Context serviceCtx;
	static String catalog_id;
	public static ArrayList<HashMap<String, String>> list;
	static ArrayList<String> title;
	public static ArrayList<String> summary_list;
	public static ArrayList<String> discrip;
	public static ArrayList<Drawable> list_images;
	public static ArrayList<String> project_name;
	public static ArrayList<String> tab_uuid_list;
	public static ArrayList<String> actTypeArr;
	public static ArrayList<String> actUrlArr;
	static HashMap<String, String> service_map;
	static String id;
	private static final String SERVICE_RESULTS = "results";
	private static final String SERVICE_IMAGE = "image";
	private static final String SERVICE_SUMMARY = "summary";
	private static final String SERVICE_DESCRIPTION = "description";
	private static final String SERVICE_NAME = "name";
	private static final String SERVICE_NEW_TAG = "newtag";
	private static final String SERVICE_FEATURED_TAG = "featuredtag";
	private static final String SERVICE_QUICK_TAG = "quicktag";
	private static final String SERVICE_PROPERTIES = "properties";
	private static final String SERVICE_HIDDEN = "hidden";
	private static final String SERVICE_ACTION = "serviceActions";
	private static final String PROJECT_NAME = "projectName";
	private static final String TABLE_UUID = "tableUUID";
	private static final String ACTION_URL = "actionURL";
	private static final String ACTION_TYPE = "actionType";

	search_background() {
	}

	public static int search(String url) {
		Log.v("DATA", "grabbed");
		list = new ArrayList<HashMap<String, String>>();

		final_url = url;

		JSONParserLogin jParser = new JSONParserLogin();
		String sso_tok = MyApplication.getToken();
		Log.v("TOKE", sso_tok);
		JSONObject json = jParser.getJSONFromUrl(final_url, "", "", sso_tok);
		Log.v("CAlled Search", json + "");

	int i=	parser_method(json);
	return i;

	}

	private static int parser_method(JSONObject json) {
		try {
			title = new ArrayList<String>();
			discrip = new ArrayList<String>();
			list_images = new ArrayList<Drawable>();
			project_name = new ArrayList<String>();
			summary_list = new ArrayList<String>();
			tab_uuid_list = new ArrayList<String>();
			actUrlArr = new ArrayList<String>();
			actTypeArr = new ArrayList<String>();
			// tableUUID=new ArrayList<String>();

			service_result = json.getJSONArray(SERVICE_RESULTS);
			if (service_result.length() != 0) {
				MyApplication.setserviceCount(service_result.length());
				for (int i = 0; i < service_result.length(); i++) {

					JSONObject c = service_result.getJSONObject(i);
					JSONObject property = c.getJSONObject(SERVICE_PROPERTIES);
					String img = c.getString(SERVICE_IMAGE);

					if (img.contains("iconlibrary")) {
						img = img.replace("iconlibrary", "mobilelibrary");
						for (int y = 0; y < images_replace_sizes.length; y++) {
							if (img.contains(images_replace_sizes[y])) {
								img = img.replace(images_replace_sizes[y],
										"high");
								Log.v("DATA", img);
								if (img.contains(" ")) {
									img = img.replace(" ", "%20");

								}
							}
						}

					} else {
						Log.v("NOn url", img);
					//	img = img;
					}
					String new_tag = null, quick_tag = null, fetaured_tag = null, hidden = null, name = null, dis = null, summary = null, actionType = null, actionUrl = null;
					try {
						summary = c.getString(SERVICE_SUMMARY);
						dis = c.getString(SERVICE_DESCRIPTION);
						name = c.getString(SERVICE_NAME);
						hidden = c.getString(SERVICE_HIDDEN);
						new_tag = property.getString(SERVICE_NEW_TAG);
						quick_tag = property.getString(SERVICE_QUICK_TAG);
						fetaured_tag = property.getString(SERVICE_FEATURED_TAG);

					} catch (Exception r) {

					}
					JSONArray service_action = c.getJSONArray(SERVICE_ACTION);
					String pro_nam = null, table_UUID = null;
					for (int j = 0; j < service_action.length(); j++) {
						JSONObject json_objs_serviceActions = service_action
								.getJSONObject(j);
						pro_nam = json_objs_serviceActions
								.getString(PROJECT_NAME);
						table_UUID = json_objs_serviceActions
								.getString(TABLE_UUID);
						actionType = json_objs_serviceActions
								.getString(ACTION_TYPE);
						actionUrl = json_objs_serviceActions
								.getString(ACTION_URL);

						Log.v("test..........................", pro_nam);
						Log.v("table uuid..........................",
								table_UUID);

					}

					service_map = new HashMap<String, String>();
					service_map.put(SERVICE_IMAGE, img);

					// String final_image_url = null;
					String final_image_url;
					// try {
					if (img.startsWith("htt")) {
						final_image_url = img;
					} else {
						service_base_image_url = MyApplication.getServer();
						final_image_url = service_base_image_url + "/tmtrack/"
								+ img;
					}
					Log.v("IMAGE URL FORMED", final_image_url);
					Drawable dd = new ExecuteBlobUrlPath()
							.executeUrlPath(final_image_url);
					list_images.add(dd);
					service_map.put(SERVICE_SUMMARY, summary);
					summary_list.add(summary);
					service_map.put(SERVICE_NAME, name);
					title.add(name);
					service_map.put(PROJECT_NAME, pro_nam);
					project_name.add(pro_nam);
					service_map.put(TABLE_UUID, table_UUID);
					tab_uuid_list.add(table_UUID);
					service_map.put(SERVICE_DESCRIPTION, dis);
					discrip.add(dis);
					service_map.put(ACTION_TYPE, actionType);
					actTypeArr.add(actionType);
					service_map.put(actionUrl, actionUrl);
					actUrlArr.add(actionUrl);

					Log.v("summary", summary);
					Log.v("name", name);
					Log.v("projname", pro_nam);
					Log.v("tabid", table_UUID);
					Log.v("description", dis);
					list.add(service_map);
					MyApplication.setSearch_discription(discrip);
					MyApplication.setSearch_images(list_images);
					MyApplication.setSearch_names(title);
					MyApplication.setSearchProjName(project_name);
					MyApplication.setSearch_summary(summary_list);
					MyApplication.setSearch_tabUUID(tab_uuid_list);
					MyApplication.setSearch_actionType(actTypeArr);
					MyApplication.setSearch_actionUrl(actUrlArr);

					// put_into_map(img, summary, name,pro_nam,dis,table_UUID);
					try {
						Log.v("NEWTAG", new_tag);
						Log.v("QUICKTAG", quick_tag);
						Log.v("Featured", fetaured_tag);
						Log.v("HIDDEN STATUS", hidden);
					} catch (Exception e) {
						// TODO: handle exception
					}

					Log.v("SEARCH DATAS", "Summary......" + summary
							+ "\nprojectname....." + pro_nam + "\ndiscription"
							+ dis + "\ntableuuid" + table_UUID);

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return title.size();
	}
}

