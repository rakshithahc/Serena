package com.serena.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.serana.MyApplication;
import com.serana.MySSLSocketFactory;
import com.serena.ntlmsupport.NTLMSchemeFactory;

import android.util.Log;

public class JSONParserLogin {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	HttpResponse httpResponse;

	// constructor
	public JSONParserLogin() {

	}

	public JSONObject getJSONFromUrl(String url, String username, String pass,
			String token) {
		try {
			Log.i("caller", "called with headers");

			HttpClient httpClient = getNewHttpClient();
			Log.v("tok", token);
			if (token.equalsIgnoreCase("NULL")) {
				HttpPost httpPost = new HttpPost(url);

				Log.i("TOKEN", "Not present");
				httpPost.addHeader("Accept", "application/json");
				httpPost.addHeader("Content-Type", "application/json");
				httpPost.addHeader("Authorization", MyApplication.getToken());
				httpPost.addHeader("User-Agent", "Mozilla/5.0");
				httpResponse = httpClient.execute(httpPost);
			} else {
				Log.i("TOKEN", "present");
				HttpGet httpget = null;
				try {
					httpget = new HttpGet(url);
				

				httpget.addHeader("Accept", "application/json");
				httpget.addHeader("Content-Type", "application/json");
				httpget.addHeader("Alfssoauthntoken", token);
				httpget.addHeader("User-Agent", "Mozilla/5.0");
				httpResponse = httpClient.execute(httpget);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			Log.v("String", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
			Log.v("JSON OBJ", "" + jObj);

		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

/*	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return getNewHttpClient();
		}
	}*/
	
	
	public static HttpClient getNewHttpClient() {
		try {

			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

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

}
