package com.serana;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

public class ExecuteBlobUrlPath {
	@SuppressLint("DefaultLocale")
	@SuppressWarnings("unused")
	public Drawable executeUrlPath(String myurl) {

		{
			try {
				HttpsURLConnection urlHttpsConnection = null;
				HttpURLConnection urlConnection = null;
				Drawable d = null;

				URL url = new URL(myurl);

				if (url.getProtocol().toLowerCase().equals("https")) {

					trustAllHosts();

					urlHttpsConnection = (HttpsURLConnection) url
							.openConnection();

					urlHttpsConnection.setHostnameVerifier(DO_NOT_VERIFY);

					urlConnection = urlHttpsConnection;
					InputStream is = urlHttpsConnection.getInputStream();

					d = Drawable.createFromStream(is, "src name");
					// Bitmap img = BitmapFactory.decodeStream(is);
				} else {

					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();

					InputStream is = connection.getInputStream();

					d = Drawable.createFromStream(is, "src name");

				}

				return d;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	private static void trustAllHosts() {

		X509TrustManager easyTrustManager = new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// Oh, I am easy!
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// Oh, I am easy!
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		};

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { easyTrustManager };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

}