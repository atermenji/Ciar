package com.coreinvader.ciar.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class CiarContract {

    public interface CategoriesColumns {
        public static final String CATEGORY_ID = "category_id";
        public static final String CATEGORY_NAME = "category_name";
        public static final String CATEGORY_IMAGE_URL = "category_image_url";
        public static final String CATEGORY_IS_ACTIVE = "category_is_active";
    }

    public interface MapObjectsColumns {
        public static final String MAP_OBJECT_ID = "map_object_id";
        public static final String MAP_OBJECT_NAME = "map_object_name";
        public static final String MAP_OBJECT_IMAGE_URL = "map_object_image_url";
        public static final String MAP_OBJECT_ADDRESS = "map_object_address";
        public static final String MAP_OBJECT_DISCTANCE = "map_object_distance";
        public static final String MAP_OBJECT_DESCRIPTION = "map_object_description";
        public static final String MAP_OBJECT_CATEGORY_NAME = "map_object_category_name";
        public static final String MAP_OBJECT_LATITUDE = "map_object_latitude";
        public static final String MAP_OBJECT_LONGITUDE = "map_object_longitude";
    }

    public static final String CONTENT_AUTHORITY = "com.coreinvader.ciar";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_CATEGORIES = "categories";
    private static final String PATH_ACTIVE = "is_active";
    private static final String PATH_MAP_OBJECTS = "map_objects";

    public static class Categories implements CategoriesColumns, BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.coreinvader.ciar.category";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.coreinvader.ciar.category";

        /** Build {@link Uri} for requested {@link #CATEGORY_ID}. */
        public static Uri buildCategoryUri(String categoryId) {
            return CONTENT_URI.buildUpon().appendPath(categoryId).build();
        }
        
        /**
         * Build {@link Uri} that references any {@link MapObjects} associated with
         * the requested {@link #CATEGORY_ID}.
        */
        public static Uri buildMapObjectsUri(String categoryId) {
            return CONTENT_URI.buildUpon().appendPath(categoryId).appendPath(PATH_MAP_OBJECTS).build();
        }
        
        /**
         * Build {@link Uri} that references any {@link MapObjects} assiciated to {@link Category}
         * field {@link #CATEGORY_IS_ACTIVE}.
        */
        public static Uri buildMapObjectsIsActiveCategoryUri(int isActive) {
            return CONTENT_URI.buildUpon().appendPath(PATH_ACTIVE)
                    .appendPath(Integer.toString(isActive)).appendPath(PATH_MAP_OBJECTS).build();
        }

        /** Read {@link #CATEGORY_ID} from {@link Categories} {@link Uri}. */
        public static String getCategoryId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        
        /** Read {@link #CATEGORY_IS_ACTIVE} from {@link Categories} {@link Uri} */
        public static String getCategoryIsActive(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    public static class MapObjects implements MapObjectsColumns, BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MAP_OBJECTS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.coreinvader.ciar.map_object";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.coreinvader.ciar.map_object";
        
        /** {@link Categories#CATEGORY_ID} that this map object belongs to. */
        public static final String CATEGORY_ID = "category_id";

        /** Build {@link Uri} for requested {@link #MAP_OBJECT_ID}. */
        public static Uri buildMapObjectUri(String mapObjectId) {
            return CONTENT_URI.buildUpon().appendPath(mapObjectId).build();
        }

        /** Read {@link #MAP_OBJECT_ID} from {@link MapObjects} {@link Uri}. */
        public static String getMapObjectId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    private CiarContract() {
    }
}
