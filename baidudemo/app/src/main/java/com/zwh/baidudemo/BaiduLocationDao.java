package com.zwh.baidudemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zwh on 17-9-16.
 */

public class BaiduLocationDao {

    private static BaiduLocationDao sBaiduLocationDao;

    private Context mContext = null;
    private DatabaseHelper mDatabaseHelper = null;
    private SQLiteDatabase mDatabase = null;

    public static BaiduLocationDao get(Context context){
        return sBaiduLocationDao;
    }

    public BaiduLocationDao(Context context) {
        mDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
        mDatabase = mDatabaseHelper.getReadableDatabase();
    }

    public void addLocation(double latitude, double longitude){
        mDatabase.execSQL("insert into table_location (latitude,longitude) values (?,?)", new Object[]{latitude,longitude});
    }

    public int getLocationsCount(){
        Cursor cursor = mDatabase.rawQuery("select * from table_location",null);
        int count = 0;
        while (cursor.moveToNext()){
            count ++ ;
        }
        return count;
    }
}
