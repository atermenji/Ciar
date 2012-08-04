package com.coreinvader.ciar.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateUtils;

/**
 * A general class with methods to work with HttpUrlConnection.
 */
public class HttpHelper {

    private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;
    private static final int CONNECTION_TIMEOUT = 20;
    private static final int SOCKET_TIMEOUT = 20;
    private static final int SOCKET_BUFFER_SIZE = 8192;
    
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    private static final String GET = "GET";

    private HttpHelper() {}

    public static InputStream getResponse(URL url) throws IOException {
	HttpURLConnection con = null;

	try {
	    con = (HttpURLConnection) url.openConnection();
	    con.setReadTimeout(READ_TIMEOUT);
	    con.setConnectTimeout(CONNECT_TIMEOUT);
	    con.setRequestMethod(GET);
	    con.setDoInput(true);
	    con.connect();

	    return StreamHelper.cloneStream(con.getInputStream());
	} finally {
	    con.disconnect();
	}
    }

    /**
     * Generate and return a {@link HttpClient} configured for general use, including setting an
     * application-specific user-agent string.
     */
    public static HttpClient getHttpClient(Context context) {
	final HttpParams params = new BasicHttpParams();

	// Use generous timeouts for slow mobile networks
	HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT * SECOND_IN_MILLIS);
	HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT * SECOND_IN_MILLIS);

	HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);
	HttpProtocolParams.setUserAgent(params, buildUserAgent(context));

	final DefaultHttpClient client = new DefaultHttpClient(params);

	return client;
    }

    /**
     * Build and return a user-agent string that can identify this application to remote servers.
     * Contains the package name and version code.
     */
    private static String buildUserAgent(Context context) {
	try {
	    final PackageManager manager = context.getPackageManager();
	    final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

	    // Some APIs require "(gzip)" in the user-agent string.
	    return info.packageName + "/" + info.versionName + " (" + info.versionCode + ") (gzip)";
	} catch (NameNotFoundException e) {
	    return null;
	}
    }
}