package com.serana;

import java.util.ArrayList;

public class GridViewConfig {
	private static ArrayList<String> resim_list=new ArrayList<String>();

	public static ArrayList<String> getResim_list() {
		return resim_list;
	}

	public static void setResim_list(ArrayList<String> resim_list) {
		GridViewConfig.resim_list = resim_list;
	}

	public static void addImageUrls(){
		resim_list.add("http://www.ethemsulan.com/wp-content/uploads/ara.png");
		resim_list.add("http://www.ethemsulan.com/wp-content/uploads/cikti5.png");
		resim_list.add("http://www.minare.net/wp-content/uploads/2011/01/android_ipod_touch_4.jpg");
		resim_list.add("http://blog.fommy.com/wp-content/uploads/2008/10/android-wallpaper1_1024x768.png");
		resim_list.add("http://www.buynetbookcomputer.com/android-netbook-images/android-netbook-big.jpg");
		resim_list.add("http://www.sharepointhoster.com/wp-content/uploads/2011/03/android_apps.jpeg");
		resim_list.add("http://www.ethemsulan.com/wp-content/uploads/customList.png");
		resim_list.add("http://www.ethemsulan.com/wp-content/uploads/handler.png");
		resim_list.add("http://www.ethemsulan.com/wp-content/uploads/xml.png");
		resim_list.add("http://www.ethemsulan.com/wp-content/uploads/progessbar.png");

	}
}