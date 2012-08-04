package com.coreinvader.ciar.io;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;

/**
 * Abstract class that handles reading and parsing a {@link JSONArray} into
 * a set of {@link ContentProviderOperation}. It catches recoverable network
 * exceptions and rethrows them as {@link HandlerException}. Any local
 * {@link ContentProvider} exceptions are considered unrecoverable.
 * <p>
 * This class is only designed to handle simple one-way synchronization.
 */
public abstract class JsonHandler {
    
    private final String mAuthority;

    public JsonHandler(String authority) {
	mAuthority = authority;
    }
    
    /**
     * Parse the given {@link JSONArray}, turning into a series of
     * {@link ContentProviderOperation} that are immediately applied using the
     * given {@link ContentResolver}.
     */
    public void parseAndApply(JSONArray jsonArray, ContentResolver resolver) throws JsonHandlerException {
	try {
	    final ArrayList<ContentProviderOperation> batch = parse(jsonArray, resolver);
	    resolver.applyBatch(mAuthority, batch);
	} catch (JsonHandlerException ex) {
	    throw ex;
	} catch (JSONException ex) {
	    throw new JsonHandlerException("Problem parsing JSON response", ex);
	} catch (IOException ex) {
	    throw new JsonHandlerException("Problem reading response", ex);
	} catch (RemoteException ex) {
	    throw new RuntimeException("Problem applying batch operation", ex);
	} catch (OperationApplicationException e) {
            throw new RuntimeException("Problem applying batch operation", e);
        }
    }
    
    /**
     * Parse the given {@link JSONArray}, returning a set of
     * {@link ContentProviderOperation} that will bring the
     * {@link ContentProvider} into sync with the parsed data.
     */
    public abstract ArrayList<ContentProviderOperation> parse(JSONArray jsonArray, ContentResolver resolver)
	    throws JSONException, IOException;
}
