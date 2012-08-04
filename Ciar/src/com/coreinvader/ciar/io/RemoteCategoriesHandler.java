package com.coreinvader.ciar.io;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;

import com.coreinvader.ciar.provider.CiarContract;
import com.coreinvader.ciar.provider.CiarContract.Categories;
import com.coreinvader.ciar.util.HandlerUtils;

public class RemoteCategoriesHandler extends JsonHandler {

    public RemoteCategoriesHandler() {
        super(CiarContract.CONTENT_AUTHORITY);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(JSONArray jsonArray, ContentResolver resolver)
            throws JSONException, IOException {
        final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonLayer = jsonArray.getJSONObject(i);

            JSONArray jsonCategories = jsonLayer.getJSONArray("categories");
            for (int j = 0; j < jsonCategories.length(); j++) {
                JSONObject jsonCategory = jsonCategories.getJSONObject(j);

                final String categoryId = HandlerUtils.sanitizeId(jsonCategory.getString("id"));
                final Uri categoryUri = Categories.buildCategoryUri(categoryId);

                // TODO check category last updated

                batch.add(ContentProviderOperation.newDelete(categoryUri).build());

                final ContentProviderOperation.Builder categoryBuilder = ContentProviderOperation
                        .newInsert(Categories.CONTENT_URI);
                categoryBuilder.withValue(Categories.CATEGORY_ID, categoryId);
                categoryBuilder.withValue(Categories.CATEGORY_NAME, jsonCategory.getString("name"));
                String imageUrl = jsonCategory.getString("image_url");
                if (imageUrl != null && !imageUrl.equals("null")) {
                    categoryBuilder.withValue(Categories.CATEGORY_IMAGE_URL, imageUrl);
                } else {
                    categoryBuilder.withValue(Categories.CATEGORY_IMAGE_URL, "none");
                }
                categoryBuilder.withValue(Categories.CATEGORY_IS_ACTIVE, 1);
                
                batch.add(categoryBuilder.build());
            }
        }

        return batch;
    }
}
