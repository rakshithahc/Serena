package com.serena.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network_Available {
	public static boolean hasConnection(Context context) {
		 ConnectivityManager cm = (ConnectivityManager)context.getSystemService(
			        Context.CONNECTIVITY_SERVICE);


			    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			    if (activeNetwork != null && activeNetwork.isConnected()) {
			      return true;
			    }

			    return false;
			  }
			}