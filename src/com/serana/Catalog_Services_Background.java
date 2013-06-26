package com.serana;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.serena.parser.JSONParser;
import com.serena.parser.JSONParserLogin;

class Catalog_Services_Background {
	private static int OnclickValue;
	public static final String images_replace_sizes[] = { "48x48", "32x32",
			"72x72", "64x64" };

	static String server = MyApplication.getServer();
	static String service_cat_new_url = "/sdf/servicedef/services/"+MyApplication.getAdvance_id()+"?tagged=true&sortby=weight,name";
	static String service_base_image_url = "/tmtrack/";
	static String init_url = "/sdf/servicedef/services/"+MyApplication.getAdvance_id()+"?categoryid=";

	static String final_url;
	CustomDialog pd;
	static JSONArray service_result;
	Context serviceCtx;
	static String catalog_id;
	public static ArrayList<HashMap<String, String>> list;
	static ArrayList<String> title;
	public static ArrayList<String> summary;
	public static ArrayList<String> discrip;
	public static ArrayList<Drawable> list_images;
	public static ArrayList<String> project_name;
	public static ArrayList<String> tableUUID;
	public static ArrayList<String> actionType;
	public static ArrayList<String> actionUrl;

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

	Catalog_Services_Background() {

	}

	public static void search(String url) {

		final_url = url;
		list = new ArrayList<HashMap<String, String>>();
		if (catalog_id.equalsIgnoreCase("-1")) {
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl(MyApplication.getServer()
					+ service_cat_new_url);
			parser_method(json, catalog_id);
		}

		else if (catalog_id.equalsIgnoreCase("-2")) {
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl(MyApplication.getServer()
					+ service_cat_new_url);
			parser_method(json, catalog_id);

		} else if (catalog_id.equalsIgnoreCase("-3")) {
			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl(MyApplication.getServer()
					+ service_cat_new_url);
			Log.v("the id is", catalog_id);
			parser_method(json, catalog_id);
		} else {
			JSONParserLogin jParser = new JSONParserLogin();
			String sso_tok = MyApplication.getToken();
			Log.v("TOKE", sso_tok);
			JSONObject json = jParser
					.getJSONFromUrl(final_url, "", "", sso_tok);
			parser_method(json, catalog_id);

		}
	}

	public static void alldata_services(String id) {
		OnclickValue = Integer.parseInt(id);
		int intValue = 0;
		intValue = Integer.parseInt(id);

		final_url = MyApplication.getServer() + init_url + id
				+ "&sortby=weight,name";
		Log.v("url is...............:", final_url);
		list = new ArrayList<HashMap<String, String>>();

		if (intValue == -1) {
			try {
				JSONParser jParser = new JSONParser();
				JSONObject json = jParser.getJSONFromUrl(MyApplication
						.getServer() + service_cat_new_url);
				parser_method(json, id);
			} catch (Exception e) {
			}
		} else if (intValue == -2) {
			try {
				JSONParser jParser = new JSONParser();
				JSONObject json = jParser.getJSONFromUrl(MyApplication
						.getServer() + service_cat_new_url);
				parser_method(json, id);
			} catch (Exception e) {
			}
		} else if (intValue == -3) {
			try {
				JSONParser jParser = new JSONParser();
				JSONObject json = jParser.getJSONFromUrl(MyApplication
						.getServer() + service_cat_new_url);
				parser_method(json, id);
			} catch (Exception e) {
			}
		} else if (intValue > 0) {
			try {
				JSONParserLogin jParser = new JSONParserLogin();
				String sso_tok = MyApplication.getToken();
				JSONObject json = jParser.getJSONFromUrl(final_url, "", "",
						sso_tok);
				parser_method(json, id);
			} catch (Exception e) {
			}

		}
	}

	@SuppressWarnings("unused")
	private static void parser_method(JSONObject json, String id) {
	//	int intValue = 0;

		try {
			title = new ArrayList<String>();
			discrip = new ArrayList<String>();
			list_images = new ArrayList<Drawable>();
			project_name = new ArrayList<String>();
			summary = new ArrayList<String>();
			tableUUID = new ArrayList<String>();
			actionType = new ArrayList<String>();
			actionUrl = new ArrayList<String>();
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
					}

					String new_tag = null;
					String quick_tag = null;
					String fetaured_tag = null;
					String hidden = null;
					String name = null;
					String dis = null;
					String summary = null;

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
					String pro_nam = null, table_UUID = null, action_type = null, action_url = null;
					for (int j = 0; j < service_action.length(); j++) {
						JSONObject json_objs_serviceActions = service_action
								.getJSONObject(j);
						pro_nam = json_objs_serviceActions
								.getString(PROJECT_NAME);
						table_UUID = json_objs_serviceActions
								.getString(TABLE_UUID);
						action_type = json_objs_serviceActions
								.getString(ACTION_TYPE);
						action_url = json_objs_serviceActions
								.getString(ACTION_URL);
					}

					int temp = Integer.valueOf(MyApplication.getCatelog_ids()
							.get(0));
					if (temp == Catalog_New_Background.firstItemInCatalog) {
						if (Catalog_New_Background.firstItemInCatalog == -2) {
							if (new_tag.equalsIgnoreCase("true")) {
								put_into_map_whats_new(img, summary, name,
										pro_nam, dis, table_UUID, action_type,
										action_url);
							}
						} else if (Catalog_New_Background.firstItemInCatalog == -1) {
							if (quick_tag.equalsIgnoreCase("true")) {
								put_into_map_whats_new(img, summary, name,
										pro_nam, dis, table_UUID, action_type,
										action_url);
							}
						} else if (Catalog_New_Background.firstItemInCatalog == -3) {
							if (fetaured_tag.equalsIgnoreCase("true")) {
								put_into_map_whats_new(img, summary, name,
										pro_nam, dis, table_UUID, action_type,
										action_url);
							}
						}

						else {
							put_into_map_whats_new(img, summary, name, pro_nam,
									dis, table_UUID, action_type, action_url);
						}
					}
					if (temp != Catalog_New_Background.firstItemInCatalog) {
						if (OnclickValue == -2) {
							if (new_tag.equalsIgnoreCase("true")) {
								put_into_map(img, summary, name, pro_nam, dis,
										table_UUID, action_type, action_url);
							}
						} else if (OnclickValue == -1) {
							if (quick_tag.equalsIgnoreCase("true")) {
								put_into_map(img, summary, name, pro_nam, dis,
										table_UUID, action_type, action_url);
							}
						} else if (OnclickValue == -3) {
							if (fetaured_tag.equalsIgnoreCase("true")) {
								put_into_map(img, summary, name, pro_nam, dis,
										table_UUID, action_type, action_url);
							}
						} else {
							put_into_map(img, summary, name, pro_nam, dis,
									table_UUID, action_type, action_url);
						}
					}

				}

			} else {
				MyApplication.setserviceCount(0);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void put_into_map(String img, String summar, String name,
			String proj_name, String description, String tabuuid,
			String actType, String actUrl) {

		service_map = new HashMap<String, String>();
		service_map.put(SERVICE_IMAGE, img);

		// String final_image_url = null;
		String final_image_url;
		// try {
		if (img.startsWith("htt")) {
			final_image_url = img;
		} else {
			final_image_url = MyApplication.getServer()
					+ service_base_image_url + img;
		}
		Log.v("Image url", final_image_url);
		Drawable dd = new ExecuteBlobUrlPath().executeUrlPath(final_image_url);
		list_images.add(dd);
		service_map.put(SERVICE_SUMMARY, summar);
		summary.add(summar);
		service_map.put(SERVICE_NAME, name);
		title.add(name);
		service_map.put(PROJECT_NAME, proj_name);
		project_name.add(proj_name);
		service_map.put(TABLE_UUID, tabuuid);
		tableUUID.add(tabuuid);
		service_map.put(SERVICE_DESCRIPTION, description);
		discrip.add(description);
		service_map.put(ACTION_TYPE, actType);
		actionType.add(actType);
		service_map.put(ACTION_URL, actUrl);
		actionUrl.add(actUrl);
		Log.v("summary", summar);
		Log.v("name", name);
		Log.v("projname", proj_name);
		Log.v("tabid", tabuuid);
		Log.v("description", description);

		list.add(service_map);
		MyApplication.setService_discription(discrip);
		MyApplication.setService_images(list_images);
		MyApplication.setService_names(title);
		MyApplication.setProjectName(project_name);
		MyApplication.setService_summary(summary);
		MyApplication.settableUUID(tableUUID);
		MyApplication.setAction_Type(actionType);
		MyApplication.setAction_Url(actionUrl);

	}

	private static void put_into_map_whats_new(String img, String summar,
			String name, String proj_name, String description, String tabuuid,
			String actiType, String actiUrl) {

		service_map = new HashMap<String, String>();
		service_map.put(SERVICE_IMAGE, img);

		// String final_image_url = null;
		String final_image_url;
		// try {
		if (img.startsWith("htt")) {
			final_image_url = img;
		} else {
			final_image_url = MyApplication.getServer()
					+ service_base_image_url + img;
		}

		Log.v("Image url", final_image_url);
		Drawable dd = new ExecuteBlobUrlPath().executeUrlPath(final_image_url);
		list_images.add(dd);
		service_map.put(SERVICE_SUMMARY, summar);
		summary.add(summar);
		service_map.put(SERVICE_NAME, name);
		title.add(name);
		service_map.put(PROJECT_NAME, proj_name);
		project_name.add(proj_name);
		service_map.put(TABLE_UUID, tabuuid);
		tableUUID.add(tabuuid);
		service_map.put(SERVICE_DESCRIPTION, description);
		discrip.add(description);
		service_map.put(ACTION_TYPE, actiType);
		actionType.add(actiType);
		service_map.put(ACTION_URL, actiUrl);
		actionUrl.add(actiUrl);
		Log.v("summary", summar);
		Log.v("name", name);
		Log.v("projname", proj_name);
		Log.v("tabid", tabuuid);
		Log.v("description", description);
		list.add(service_map);
		MyApplication.new_setService_discription(discrip);
		MyApplication.new_setService_images(list_images);
		MyApplication.new_setService_names(title);
		MyApplication.new_setProjectName(project_name);
		MyApplication.new_setService_summary(summary);
		MyApplication.new_settableUUID(tableUUID);
		MyApplication.new_setActionType(actionType);
		MyApplication.new_setActionUrl(actionUrl);

	}

}