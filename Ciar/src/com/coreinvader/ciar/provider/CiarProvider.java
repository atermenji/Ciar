package com.coreinvader.ciar.provider;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.coreinvader.ciar.provider.CiarContract.Categories;
import com.coreinvader.ciar.provider.CiarContract.MapObjects;
import com.coreinvader.ciar.provider.CiarDatabase.Tables;
import com.coreinvader.ciar.service.SyncService;
import com.coreinvader.ciar.util.SelectionBuilder;

/**
 * Provider that stores {@link CiarContract} data. Data is usually inserted by {@link SyncService}, and queried by
 * various {@link Activity} instances.
 */
public class CiarProvider extends ContentProvider {

    private static final String TAG = "CiarProvider";

    private CiarDatabase mDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int CATEGORIES = 100;
    private static final int CATEGORIES_ID = 101;
    private static final int CATEGORIES_ID_MAP_OBJECTS = 102;
    private static final int CATEGORIES_IS_ACTIVE_MAP_OBJECTS = 103;

    private static final int MAP_OBJECTS = 200;
    private static final int MAP_OBJECTS_ID = 201;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CiarContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, "categories", CATEGORIES);
        uriMatcher.addURI(authority, "categories/*", CATEGORIES_ID);
        uriMatcher.addURI(authority, "categories/*/map_objects", CATEGORIES_ID_MAP_OBJECTS);
        uriMatcher.addURI(authority, "categories/is_active/*/map_objects", CATEGORIES_IS_ACTIVE_MAP_OBJECTS);

        uriMatcher.addURI(authority, "map_objects", MAP_OBJECTS);
        uriMatcher.addURI(authority, "map_objects/*", MAP_OBJECTS_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new CiarDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CATEGORIES:
                return Categories.CONTENT_TYPE;
            case CATEGORIES_ID:
                return Categories.CONTENT_ITEM_TYPE;
            case CATEGORIES_ID_MAP_OBJECTS:
                return MapObjects.CONTENT_TYPE;
            case CATEGORIES_IS_ACTIVE_MAP_OBJECTS:
                return MapObjects.CONTENT_TYPE;
            case MAP_OBJECTS:
                return MapObjects.CONTENT_TYPE;
            case MAP_OBJECTS_ID:
                return MapObjects.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase database = mDbHelper.getReadableDatabase();
        return buildSimpleSelection(uri).where(selection, selectionArgs).query(database, projection, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CATEGORIES: {
                Log.v(TAG, "inserting category uri is : " + uri.toString());
                database.insertOrThrow(Tables.CATEGORIES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                Log.v(TAG, "category is inserted");
                return Categories.buildCategoryUri(values.getAsString(Categories.CATEGORY_ID));
            }
            case MAP_OBJECTS: {
                Log.v(TAG, "inserting map object : " + values.toString());
                database.insertOrThrow(Tables.MAP_OBJECTS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                Log.v(TAG, "map objects inserted");
                return MapObjects.buildMapObjectUri(values.getAsString(MapObjects.MAP_OBJECT_ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(database, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(database);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside a {@link SQLiteDatabase} transaction.
     * All changes will be rolled back if any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        database.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];

            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }

            database.setTransactionSuccessful();
            return results;
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested {@link Uri}. This is usually enough to support
     * {@link #insert}, {@link #update}, and {@link #delete} operations. At this project it also used at {@link #query}
     * since no join needed.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CATEGORIES: {
                return builder.table(Tables.CATEGORIES);
            }
            case CATEGORIES_ID: {
                final String categoryId = Categories.getCategoryId(uri);
                return builder.table(Tables.CATEGORIES).where(Categories.CATEGORY_ID + "=?", categoryId);
            }
            case CATEGORIES_ID_MAP_OBJECTS: {
                final String categoryId = Categories.getCategoryId(uri);
                return builder.table(Tables.MAP_OBJECTS).where(MapObjects.CATEGORY_ID + "=?", categoryId);
            }
            case CATEGORIES_IS_ACTIVE_MAP_OBJECTS: {
                final String isActive = Categories.getCategoryIsActive(uri);
                Log.v(TAG, "selecting is active : " + isActive);
                return builder.table(Tables.MAP_OBJECTS_JOIN_CATEGORIES)
                        .mapToTable(MapObjects._ID, Tables.MAP_OBJECTS)
                        .mapToTable(MapObjects.CATEGORY_ID, Tables.MAP_OBJECTS)
                        .where(Qualified.CATEGORIES_IS_ACTIVE + "=?", isActive);
            }
            case MAP_OBJECTS: {
                return builder.table(Tables.MAP_OBJECTS);
            }
            case MAP_OBJECTS_ID: {
                final String mapObjectId = MapObjects.getMapObjectId(uri);
                return builder.table(Tables.MAP_OBJECTS).where(MapObjects.MAP_OBJECT_ID + "=?", mapObjectId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
    
    /**
     * {@link ScheduleContract} fields that are fully qualified with a specific
     * parent {@link Tables}. Used when needed to work around SQL ambiguity.
     */
    private interface Qualified {
        public static final String MAP_OBJECTS_CATEGORY_ID = Tables.MAP_OBJECTS + "." + MapObjects.CATEGORY_ID;
        public static final String CATEGORIES_IS_ACTIVE = Tables.CATEGORIES + "." + Categories.CATEGORY_IS_ACTIVE;
    }
}
