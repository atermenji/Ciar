package com.coreinvader.ciar.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;

import com.coreinvader.ciar.util.StreamHelper;

import android.content.ContentResolver;

/**
 * Executes an {@link HttpUriRequest} and passes the result as an
 * {@link JSONArray} to the given {@link JsonHandler}.
 */
public class RemoteExecutor {
    
    private final HttpClient mHttpClient;
    private final ContentResolver mResolver;

    public RemoteExecutor(HttpClient httpClient, ContentResolver resolver) {
        mHttpClient = httpClient;
        mResolver = resolver;
    }
    
    /**
     * Execute a {@link HttpGet} request, passing a valid response through
     * {@link JsonHandler#parseAndApply(JSONArray, ContentResolver)}.
     */
    public void executeGet(String url, JsonHandler handler) throws JsonHandlerException {
        final HttpUriRequest request = new HttpGet(url);
        execute(request, handler);
    }
    
    /**
     * Execute this {@link HttpUriRequest}, passing a valid response through
     * {@link JsonHandler#parseAndApply(JSONArray, ContentResolver)}.
     */
    public void execute(HttpUriRequest request, JsonHandler handler) throws JsonHandlerException {
        try {
            final HttpResponse response = mHttpClient.execute(request);
            final int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new JsonHandlerException("Unexpected server response " + response.getStatusLine()
                        + " for " + request.getRequestLine());
            }

            final InputStream input = response.getEntity().getContent();
            try {
        	JSONArray entries = new JSONArray(StreamHelper.makeString(input));
                handler.parseAndApply(entries, mResolver);
            } catch (JSONException ex) {
                throw new JsonHandlerException("Malformed response for " + request.getRequestLine(), ex);
            } finally {
                if (input != null) input.close();
            }
        } catch (JsonHandlerException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new JsonHandlerException("Problem reading remote response for "
                    + request.getRequestLine(), ex);
        }
    }
}
