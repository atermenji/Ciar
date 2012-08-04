package com.coreinvader.ciar.ui.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.coreinvader.ciar.R;
import com.coreinvader.ciar.provider.CiarContract.Categories;

public class CategoriesAdapter extends CursorAdapter {

    private static final String TAG = "CategoriesAdapter";
    
    private final LayoutInflater mInflater;
    private final Context mContext;
    
    public CategoriesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.list_item_category, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView categoryImage = (ImageView) view.findViewById(R.id.iv_category_logo);
        TextView categoryName = (TextView) view.findViewById(R.id.tv_category_name);
        CheckBox categoryActive = (CheckBox) view.findViewById(R.id.cb_category_select);
        
        categoryName.setText(cursor.getString(cursor.getColumnIndex(Categories.CATEGORY_NAME)));
        
        String categoryImageUrl = cursor.getString(cursor.getColumnIndex(Categories.CATEGORY_IMAGE_URL));
        //TODO load images
        
        Log.v(TAG, "isActive int value for category: " + cursor.getString(cursor.getColumnIndex(Categories.CATEGORY_NAME))
                + " is : " + cursor.getInt(cursor.getColumnIndex(Categories.CATEGORY_IS_ACTIVE)));
        boolean isActive = (cursor.getInt(cursor.getColumnIndex(Categories.CATEGORY_IS_ACTIVE)) == 1) ? true : false;
        Log.v(TAG, "boolean isActive : " + isActive);
        categoryActive.setChecked(isActive);
        
        final String categoryId = cursor.getString(cursor.getColumnIndex(Categories.CATEGORY_ID));
        categoryActive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int isActive = (isChecked) ? 1 : 0;
                new UpdateCategoryActiveTask(categoryId, isActive).execute();
            }
        });
    }
    
    private class UpdateCategoryActiveTask extends AsyncTask<Void, Void, Integer> {
        
        private String mCategoryId;
        private int mIsActive;
        
        public UpdateCategoryActiveTask(String categoryId, int isActive) {
            mCategoryId = categoryId;
            mIsActive = isActive;
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            ContentValues values = new ContentValues();
            values.put(Categories.CATEGORY_IS_ACTIVE, mIsActive);
            Log.v(TAG, "setting value : " + mIsActive + " for category id : " + mCategoryId);
            return mContext.getContentResolver().update(Categories.buildCategoryUri(mCategoryId), values, null, null);
        }
        
    }
}
