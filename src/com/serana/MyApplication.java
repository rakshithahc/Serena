package com.serana;

import java.util.ArrayList;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class MyApplication extends Application {
	public static boolean noSplash = false;
	private static long lattime;
	private static String server_Name;
	private static boolean entry_to_webview = false;
	private static String has_slaData = "flase";
	private static String encoded_user_data;
	private static ArrayList<All_Sla_data_dto> sla;
	private static String server;
	private static String project_name;
	private static int services_count = 0;
	private static String userID;
	private static String Req_id_or_string;
	public static String currentVersion="06-18-2013";
	private static String passWord;
	private static String token = "NULL";
	private static ArrayList<All_Request_data_dto> list;
	private static ArrayList<All_Approval_data_dto> appvalList;
	private static ArrayList<All_Approval_data_dto> filled_appvalList;
	private static ArrayList<All_Request_data_dto> filled_requestList;
	private static boolean REQ_FLAG = false;
	private static boolean APPROVAL_FLAG = false;
	private static String CATALOG_FLAG = "NULL";
	private static boolean FILLED = false;
	private static boolean SERVICE_FLAG = false;
	private static String loggedFromSecUrl;
	private static String ReqURL;
	private static int new_tag = 0;
	private static long timeLogin;
	private static String catalog_title;
	private static ArrayList<String> catelog_names;
	private static ArrayList<String> catelog_ids;
	private static ArrayList<Drawable> catelog_images;
	private static ArrayList<String> service_names;
	private static ArrayList<String> service_dicription;
	private static ArrayList<String> service_summary;
	private static ArrayList<Drawable> service_images;
	private static ArrayList<String> service_project_name;
	private static ArrayList<String> service_table_uuid;
	private static ArrayList<String> request_server_urls;
	private static ArrayList<String> service_action_type;
	private static ArrayList<String> service_action_url;

	private static ArrayList<String> search_names;
	private static ArrayList<String> search_dicription;
	private static ArrayList<String> search_summary;
	private static ArrayList<Drawable> search_images;
	private static ArrayList<String> search_project_name;
	private static ArrayList<String> search_tab_uuid;
	private static ArrayList<String> search_service_action_type;
	private static ArrayList<String> search_service_action_url;
	// whats new
	private static ArrayList<String> service_names_new;
	private static ArrayList<String> service_dicription_new;
	private static ArrayList<String> service_summary_new;
	private static ArrayList<Drawable> service_images_new;
	private static ArrayList<String> service_project_name_new;
	private static ArrayList<String> service_table_uuid_new;
	private static ArrayList<String> new_service_action_type;
	private static ArrayList<String> new_service_action_url;
	
	


	public static ArrayList<String> mockColors;
	public static boolean checkforAuthScheme = false;
	public static ArrayList<QueueItem> queueItems;
	public static boolean firstflag= false;
	public static String req_url;
	public static String app_url;
	public static String advance_id;
	
	
	public static String getAdvance_id() {
		return advance_id;
	}

	public static void setAdvance_id(String advance_id) {
		System.out.println("id is:"+ advance_id);
		MyApplication.advance_id = advance_id;
		System.out.println("id is:"+ MyApplication.advance_id);
	}
	
	public static String getReq_url() {
		return req_url;
	}

	public static void setReq_url(String req_url) {
		MyApplication.req_url = req_url;
	}

	public static String getApp_url() {
		return app_url;
	}

	public static void setApp_url(String app_url) {
		MyApplication.app_url = app_url;
	}

	public static String getReq_title() {
		return req_title;
	}

	public static void setReq_title(String req_title) {
		MyApplication.req_title = req_title;
	}

	public static String getApp_title() {
		return app_title;
	}

	public static void setApp_title(String app_title) {
		MyApplication.app_title = app_title;
	}

	public static String req_title;
	public static String app_title;


	public static boolean isFirstflag() {
		return firstflag;
	}

	public static void setFirstflag(boolean firstflag) {
		MyApplication.firstflag = firstflag;
	}

	public static boolean isCheckforAuthScheme() {
		return checkforAuthScheme;
	}

	public static void setCheckforAuthScheme(boolean checkforAuthScheme) {
		MyApplication.checkforAuthScheme = checkforAuthScheme;
	}

	public static void setEntryToWebForms(boolean status) {
		entry_to_webview = status;
	}

	public static boolean getEntryToWebForms() {
		return entry_to_webview;
	}

	public static void setEncodedUserCred(String encPass) {
		encoded_user_data = encPass;
	}

	public static String getEncodedUserCred() {
		return encoded_user_data;
	}

	public static String getUserID() {
		return userID;
	}

	public static void setUserID(String userID) {
		MyApplication.userID = userID;
	}

	public static String getPassWord() {
		return passWord;
	}

	public static void setPassWord(String pass_word) {
		MyApplication.passWord = pass_word;
	}

	public static String getToken() {
		return token;
	}

	public static void setToken(String token_name) {
		MyApplication.token = token_name;
	}

	public static void setRequstList(ArrayList<All_Request_data_dto> list) {
		MyApplication.list = list;
		MyApplication.REQ_FLAG = true;
		filled_requestList = new ArrayList<All_Request_data_dto>();
		filled_requestList.addAll(list);

	}

	public static ArrayList<All_Request_data_dto> getRequestList() {
		return list;
	}
	
	public static ArrayList<All_Request_data_dto> get_Filled_RequestList() {
		return filled_requestList;
	}
	
	
	public static ArrayList<QueueItem> getQueueItems() {
		return queueItems;
	}

	public static void setQueueItems(ArrayList<QueueItem> queueItem) {
		MyApplication.queueItems = queueItem;
		queueItems= new ArrayList<QueueItem>();
		queueItems.addAll(queueItems);
		
	}

	public static ArrayList<QueueItem> get_Filled_List() {
		return queueItems;
	}

	public static void setApprovalList(
			ArrayList<All_Approval_data_dto> approvalList) {

		MyApplication.appvalList = approvalList;
		MyApplication.APPROVAL_FLAG = true;
		filled_appvalList = new ArrayList<All_Approval_data_dto>();
		filled_appvalList.addAll(approvalList);
	}

	public static ArrayList<All_Approval_data_dto> getApprovalList() {
		return appvalList;
	}

	public static ArrayList<All_Approval_data_dto> get_Filled_ApprovalList() {
		return filled_appvalList;
	}

	public static boolean req_status() {
		return REQ_FLAG;
	}

	public static boolean app_status() {
		return APPROVAL_FLAG;
	}

	public static void set_app_status(boolean s) {
		APPROVAL_FLAG = s;
	}

	// catalog
	public static void setCat_ids(ArrayList<String> cat_id) {

		MyApplication.catelog_ids = cat_id;
		CATALOG_FLAG = "NOT NULL";
	}

	public static void setCat_names(ArrayList<String> cat_names) {
		MyApplication.catelog_names = cat_names;
	}

	// .......................catalog list.....................
	public static void setCat_images(ArrayList<Drawable> cat_images) {
		MyApplication.catelog_images = cat_images;
	}

	public static ArrayList<String> getCatelog_names() {
		return catelog_names;
	}

	public static ArrayList<String> getCatelog_ids() {
		return catelog_ids;
	}

	public static ArrayList<Drawable> getCatelog_images() {
		return catelog_images;
	}

	public static String catelog_status() {
		return CATALOG_FLAG;
	}

	public static void setCatalog_Title(String titl) {
		catalog_title = titl;
	}

	public static String getCatalog_Title() {
		return catalog_title;
	}

	// ...............................services...........................

	public static void setService_names(ArrayList<String> service_names) {
		MyApplication.service_names = service_names;
		SERVICE_FLAG = true;
	}

	public static void setService_discription(ArrayList<String> service_discript) {
		MyApplication.service_dicription = service_discript;
	}

	public static void setService_images(ArrayList<Drawable> service_images) {
		MyApplication.service_images = service_images;
	}

	public static void setService_summary(ArrayList<String> service_summary) {
		MyApplication.service_summary = service_summary;
	}

	// Service_getters
	public static ArrayList<String> getService_names() {
		return service_names;
	}

	public static ArrayList<String> getService_discrpition() {
		return service_dicription;
	}

	public static ArrayList<Drawable> getService_images() {
		return service_images;
	}

	public static ArrayList<String> getService_summary() {
		return service_summary;
	}

	public static void setProjectName(ArrayList<String> project_name) {
		service_project_name = project_name;
	}

	public static ArrayList<String> getProjectName() {
		return service_project_name;
	}

	// action type
	public static void setAction_Type(ArrayList<String> actType) {
		service_action_type = actType;
	}

	public static ArrayList<String> getAction_Type() {
		return service_action_type;
	}

	// action url
	public static void setAction_Url(ArrayList<String> actUrl) {
		service_action_url = actUrl;
	}

	public static ArrayList<String> getAction_Url() {
		return service_action_url;
	}

	public static void settableUUID(ArrayList<String> project_name) {
		service_table_uuid = project_name;
	}

	public static ArrayList<String> gettableUUID() {
		return service_table_uuid;
	}

	public static boolean service_status() {
		return SERVICE_FLAG;

	}

	public static void setserviceCount(int count) {
		services_count = count;
	}

	public static int getserviceCount() {
		return services_count;
	}

	public static String getServer() {
		return server;
	}

	public static void setServer(String server_name) {
		MyApplication.server = server_name;
	}

	public static void clear_approvals() {
		try
		{
		Log.v("Count before clear" ,Integer.toString(filled_appvalList.size()));
		appvalList.clear();
		Log.v("Count After clear" , Integer.toString(filled_appvalList.size()));
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	public static void clear_Request() {
		try
		{
		Log.v("Count before clear" ,Integer.toString(filled_requestList.size()));
		list.clear();
		Log.v("Count After clear" , Integer.toString(filled_requestList.size()));
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void set_project_name(String pro_name) {
		project_name = pro_name;
	}

	public static String get_project_name() {
		return project_name;
	}

	public static void setSlaData(ArrayList<All_Sla_data_dto> sl) {
		sla = sl;
	}

	public static ArrayList<All_Sla_data_dto> getSlaData() {
		return sla;
	}

	public static void set_fill_flag_check(boolean v) {
		FILLED = v;
	}

	public static boolean get_fill_flag_check() {
		return FILLED;
	}

	// search setter and getters

	public static void setSearch_names(ArrayList<String> service_names) {
		MyApplication.search_names = service_names;
	}

	public static void setSearch_discription(ArrayList<String> service_discript) {
		MyApplication.search_dicription = service_discript;
	}

	public static void setSearch_images(ArrayList<Drawable> service_images) {
		MyApplication.search_images = service_images;
	}

	public static void setSearch_summary(ArrayList<String> service_summary) {
		MyApplication.search_summary = service_summary;
	}

	public static void setSearch_tabUUID(ArrayList<String> search_tabuuid) {
		MyApplication.search_tab_uuid = search_tabuuid;
	}

	public static void setSearch_actionType(ArrayList<String> action_type) {
		search_service_action_type = action_type;
	}

	public static void setSearch_actionUrl(ArrayList<String> action_url) {
		search_service_action_url = action_url;
	}

	// Service_getters
	public static ArrayList<String> getSearch_names() {
		return search_names;
	}

	public static ArrayList<String> getSearch_discrpition() {
		return search_dicription;
	}

	public static ArrayList<Drawable> getSearch_images() {
		return search_images;
	}

	public static ArrayList<String> getSearch_summary() {
		return search_summary;
	}

	public static ArrayList<String> getSearch_actionType() {
		return search_service_action_type;
	}

	public static ArrayList<String> getSearch_actionUrl() {
		return search_service_action_url;
	}

	public static void setSearchProjName(ArrayList<String> project_name) {
		search_project_name = project_name;
	}

	public static ArrayList<String> getSearchProjectName() {
		return search_project_name;
	}

	public static ArrayList<String> getSearchTabUuid() {
		return search_tab_uuid;
	}

	public static void setReqId_or_string(String data) {
		Req_id_or_string = data;
	}

	public static String getReqId_or_string() {
		return Req_id_or_string;
	}

	public static void setSLA(String d) {
		has_slaData = d;
	}

	public static String getSLA() {
		return has_slaData;
	}

	public static void setServerName(String da) {
		server_Name = da;
	}

	public static String getServerName() {
		return server_Name;
	}

	public static void clearSearchrResult() {
		try {
			search_names.clear();
			search_dicription.clear();
			search_images.clear();
			search_project_name.clear();
			search_summary.clear();
			search_tab_uuid.clear();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void clearServiceResult() {
		try {
			service_names.clear();
			service_dicription.clear();
			service_images.clear();
			service_project_name.clear();
			service_summary.clear();
			service_table_uuid.clear();
		} catch (Exception e) {
		}
	}

	public static void setlastTime(long d) {
		lattime = d;
	}

	public static long getLastTime() {
		return lattime;
	}

	// whatnew list setters
	public static void new_setService_names(ArrayList<String> service_names) {
		service_names_new = service_names;
		// SERVICE_FLAG=true;
	}

	public static void new_setService_discription(
			ArrayList<String> service_discript) {
		service_dicription_new = service_discript;
	}

	public static void new_setService_images(ArrayList<Drawable> service_images) {
		service_images_new = service_images;
	}

	public static void new_setService_summary(ArrayList<String> service_summary) {
		service_summary_new = service_summary;
	}

	public static void new_settableUUID(ArrayList<String> project_name) {
		service_table_uuid_new = project_name;
	}

	public static void new_setProjectName(ArrayList<String> project_name) {
		service_project_name_new = project_name;
	}

	public static void new_setActionType(ArrayList<String> action_type) {
		new_service_action_type = action_type;
	}

	public static void new_setActionUrl(ArrayList<String> action_url) {
		new_service_action_url = action_url;
	}

	// whats newService_getters
	public static ArrayList<String> getService_names_new() {
		return service_names_new;
	}

	public static ArrayList<String> getService_discrpition_new() {
		return service_dicription_new;
	}

	public static ArrayList<Drawable> getService_images_new() {
		return service_images_new;
	}

	public static ArrayList<String> getService_summary_new() {
		return service_summary_new;
	}

	public static ArrayList<String> getProjectName_new() {
		return service_project_name_new;
	}

	public static ArrayList<String> gettableUUID_new() {
		return service_table_uuid_new;
	}

	public static ArrayList<String> get_new_ActionType() {
		return new_service_action_type;
	}

	public static ArrayList<String> getAction_new_Url() {
		return new_service_action_url;
	}

	public static void clear_new_ServiceResult() {
		try {
			service_names_new.clear();
			service_dicription_new.clear();
			service_images_new.clear();
			service_project_name_new.clear();
			service_summary_new.clear();
			service_table_uuid_new.clear();
		} catch (Exception e) {
		}

	}

	public static void setLoggedInUrlType(String b) {
		loggedFromSecUrl = b;
	}

	public static String getLoggedInUrlType() {
		return loggedFromSecUrl;
	}

	public static void setrequrl(ArrayList<String> url) {
		request_server_urls = url;
	}

	public static ArrayList<String> getRequrls() {
		return request_server_urls;
	}

	public static void setreqURL(String url) {
		ReqURL = url;
	}

	public static String getReqURL() {
		return ReqURL;
	}

	public static void set_new_tag(int f) {
		new_tag = f;
	}

	public static int get_new_tag() {
		return new_tag;
	}

	public static void setMenuScreenTimeout(long f) {
		timeLogin = f;
	}

	public static long getMenuScreenTimeout() {
		return timeLogin;
	}
}