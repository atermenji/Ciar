package com.coreinvader.ciar.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.coreinvader.ciar.provider.CiarContract.Categories;
import com.coreinvader.ciar.provider.CiarContract.CategoriesColumns;
import com.coreinvader.ciar.provider.CiarContract.MapObjects;
import com.coreinvader.ciar.provider.CiarContract.MapObjectsColumns;

public class CiarDatabase extends SQLiteOpenHelper {

    private static final String TAG = "CiarDatabase";

    private static final String DATABASE_NAME = "ciarobjects.db";
    private static final int DATABASE_VERSION = 1;

    public interface Tables {
        public static final String CATEGORIES = "categories";
        public static final String MAP_OBJECTS = "map_objects";
        
        public static final String MAP_OBJECTS_JOIN_CATEGORIES = 
                Tables.MAP_OBJECTS + " INNER JOIN " + Tables.CATEGORIES
                + "ON " + Tables.MAP_OBJECTS + "." + MapObjects.CATEGORY_ID
                + " = " + Tables.CATEGORIES + "." + Categories.CATEGORY_ID;
    }

    public interface References {
        public static final String CATEGORY_ID = "REFERENCES " + Tables.CATEGORIES + "("
                + CategoriesColumns.CATEGORY_ID + ")";
    }

    public CiarDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "database on create");

        db.execSQL("CREATE TABLE " + Tables.CATEGORIES 
                + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
                + CategoriesColumns.CATEGORY_ID + " TEXT NOT NULL, "
                + CategoriesColumns.CATEGORY_NAME + " TEXT NOT NULL, " 
                + CategoriesColumns.CATEGORY_IMAGE_URL + " TEXT, " 
                + CategoriesColumns.CATEGORY_IS_ACTIVE + " INTEGER NOT NULL, "
                + "UNIQUE (" + CategoriesColumns.CATEGORY_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.MAP_OBJECTS + " (" 
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
                + MapObjectsColumns.MAP_OBJECT_ID + " TEXT NOT NULL, "
                + MapObjects.CATEGORY_ID + " TEXT " + References.CATEGORY_ID + ", " 
                + MapObjectsColumns.MAP_OBJECT_NAME + " TEXT NOT NULL, " 
                + MapObjectsColumns.MAP_OBJECT_DESCRIPTION + " TEXT, "
                + MapObjectsColumns.MAP_OBJECT_IMAGE_URL + " TEXT, " 
                + MapObjectsColumns.MAP_OBJECT_ADDRESS + " TEXT, "
                + MapObjectsColumns.MAP_OBJECT_DISCTANCE + " TEXT, " 
                + MapObjectsColumns.MAP_OBJECT_CATEGORY_NAME + " TEXT, " 
                + MapObjectsColumns.MAP_OBJECT_LATITUDE + " DOUBLE, "
                + MapObjectsColumns.MAP_OBJECT_LONGITUDE + " DOUBLE, " 
                + "UNIQUE ( " + MapObjectsColumns.MAP_OBJECT_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MAP_OBJECTS);

        onCreate(db);
    }
}
