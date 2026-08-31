package com.vehicles.parking.mypark.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vehicles.parking.mypark.Models.Park;

import java.util.ArrayList;

public class SqliteDatabase extends SQLiteOpenHelper {

    private static final String TAG = SqliteDatabase.class.getName();

    private static final String DATABASE_NAME = "ParkDB";
    public static final String TABLE_NAME = "Park";

    private static final int DATABASE_VERSION = 4;

    private SQLiteDatabase db;

    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(id long primary key not null " +
            ", uid text not null " +
            ", name text not null , " +
            "time text not null , " +
            "date text not null ," +
            " latitude double not null ," +
            " longitude double not null ," +
            " address text not null , " +
            "timeStamp text not null , " +
            "FOREIGN KEY (uid) REFERENCES Users(contact));";

    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;




    public SqliteDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //db.execSQL(DROP_TABLE);
        //onCreate(db);

    }

    public void insert(Park park) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id" , park.getId());
        contentValues.put("uid" , park.getUid());
        contentValues.put("name" , park.getName());
        contentValues.put("time" , park.getTime());
        contentValues.put("date" , park.getDate());
        contentValues.put("latitude" , park.getLatitude());
        contentValues.put("longitude" , park.getLongitude());
        contentValues.put("address" , park.getAddress());
        contentValues.put("timeStamp" , park.getTimeStamp());

        db.insert(TABLE_NAME , null , contentValues);
        db.close();

    }

    public ArrayList<Park> getAllParks(String uid) {

        ArrayList<Park> parkArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME + " where uid = " + uid;

        Cursor cursor = db.rawQuery(query , null);

        cursor.moveToFirst();

        do {

            Park park = new Park();
            park.setId(cursor.getInt(0));
            park.setUid(cursor.getString(1));
            park.setName(cursor.getString(2));
            park.setTime(cursor.getString(3));
            park.setDate(cursor.getString(4));
            park.setLatitude(cursor.getDouble(5));
            park.setLongitude(cursor.getDouble(6));
            park.setAddress(cursor.getString(7));
            park.setTimeStamp(cursor.getString(8));

            parkArrayList.add(park);

        } while (cursor.moveToNext());

        Log.d(TAG, "getAllParks: " + parkArrayList.size());

        cursor.close();
        db.close();

        return parkArrayList;




    }

    public ArrayList<Park> getAllParks(int limit , int skip , String uid) {

        ArrayList<Park> parkArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME + " where uid = " + uid + " order by id DESC " + "limit " + Integer.toString(limit) + " offset " + Integer.toString(skip);

        Cursor cursor = db.rawQuery(query , null);

        cursor.moveToFirst();

        try{

            do {

                Park park = new Park();
                park.setId(cursor.getLong(0));
                park.setUid(cursor.getString(1));
                park.setName(cursor.getString(2));
                park.setTime(cursor.getString(3));
                park.setDate(cursor.getString(4));
                park.setLatitude(cursor.getDouble(5));
                park.setLongitude(cursor.getDouble(6));
                park.setAddress(cursor.getString(7));
                park.setTimeStamp(cursor.getString(8));

                parkArrayList.add(park);

            } while (cursor.moveToNext());


        }catch (CursorIndexOutOfBoundsException ex) {

            Log.d(TAG, "getAllParks: " + ex.getMessage());


        }



        Log.d(TAG, "getAllParks: " + parkArrayList.size());

        cursor.close();
        db.close();

        return parkArrayList;

    }

    public ArrayList<Park> getAllParksByDate(String mdate , int limit , int skip , String uid) {

        Log.d(TAG, "getAllParksByDate: "+mdate);

        ArrayList<Park> parkArrayList = new ArrayList<>();

//        try{
//
//            StringBuilder builder = new StringBuilder();
//            builder.append("\"");
//            builder.append(mdate);
//            builder.append("\"");
//
//
//
//            SQLiteDatabase db = this.getReadableDatabase();
//
//            String query = "select * from " + TABLE_NAME + " where date = " + builder.toString() + " AND uid = " + uid + " order by id DESC " + "limit " + Integer.toString(limit) + " offset " + Integer.toString(skip);
//
//            Cursor cursor = db.rawQuery(query , null);
//
//            cursor.moveToFirst();
//
//            do {
//
//                Park park = new Park();
//                park.setId(cursor.getLong(0));
//                park.setUid(cursor.getString(1));
//                park.setName(cursor.getString(2));
//                park.setTime(cursor.getString(3));
//                park.setDate(cursor.getString(4));
//                park.setLatitude(cursor.getDouble(5));
//                park.setLongitude(cursor.getDouble(6));
//                park.setAddress(cursor.getString(7));
//                park.setTimeStamp(cursor.getString(8));
//
//                parkArrayList.add(park);
//
//            } while (cursor.moveToNext());
//
//            Log.d(TAG, "getAllParks: " + parkArrayList.size());
//
//            cursor.close();
//            db.close();
//
//
//        }catch (Exception ex) {
//
//            ex.printStackTrace();
//
//
//        }

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE date = ? AND uid = ?" +
                    " ORDER BY id DESC LIMIT ? OFFSET ?";

            Cursor cursor = db.rawQuery(
                    query,
                    new String[]{
                            mdate,
                            uid,
                            String.valueOf(limit),
                            String.valueOf(skip)
                    }
            );

            if (cursor.moveToFirst()) {

                do {

                    Park park = new Park();

                    park.setId(cursor.getLong(0));
                    park.setUid(cursor.getString(1));
                    park.setName(cursor.getString(2));
                    park.setTime(cursor.getString(3));
                    park.setDate(cursor.getString(4));
                    park.setLatitude(cursor.getDouble(5));
                    park.setLongitude(cursor.getDouble(6));
                    park.setAddress(cursor.getString(7));
                    park.setTimeStamp(cursor.getString(8));

                    parkArrayList.add(park);

                } while (cursor.moveToNext());
            }

            Log.d(TAG, "getAllParks: " + parkArrayList.size());

            cursor.close();
            db.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return parkArrayList;

    }

    public long getLastIndex() {

        ArrayList<Park> parkArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query , null);

        long id;



        Log.d(TAG, "getLastIndex: " + cursor.getCount());

        if (cursor.getCount() == 0) {

            id = 1;

        } else {

            cursor.moveToLast();
            id = cursor.getLong(0) + 1;

        }

        cursor.close();
        db.close();
        return id;



    }

    public void deletePark(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "delete from " + TABLE_NAME + " where id = " + id;
        db.execSQL(deleteQuery);
        db.close();



    }

    public void dropTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_TABLE);
        onCreate(db);


    }


}
