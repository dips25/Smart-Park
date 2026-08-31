package com.vehicles.parking.mypark.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import com.vehicles.parking.mypark.Models.Park;
import com.vehicles.parking.mypark.Models.Users;

import java.util.ArrayList;

public class UserSqliteDatabase extends SQLiteOpenHelper {


    private static final String TAG = SqliteDatabase.class.getName();

    private static final String DATABASE_NAME = "ParkDB";
    private static final String TABLE_NAME = "Users";

    private static final int DATABASE_VERSION = 4;

    private SQLiteDatabase db;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(id long primary key not null " +
            ", name text not null , " +
            "contact text not null , " +
            "address text not null , " +
            "image text not null);";

    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;




    public UserSqliteDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
        db.execSQL(SqliteDatabase.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_TABLE);
        onCreate(db);

    }

    public boolean insert(Users user) {

        try{

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("id" , user.getId());
            contentValues.put("name" , user.getName());
            contentValues.put("contact" , user.getContact());
            contentValues.put("address" , user.getAddress());
            contentValues.put("image" , user.getImage());

            db.insert(TABLE_NAME , null , contentValues);
            db.close();

            return true;


        }catch(Exception ex) {

            return false;

        }



    }

    public boolean updateUser(Users user) {

        try{

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("id" , user.getId());
            contentValues.put("name" , user.getName());
            contentValues.put("contact" , user.getContact());
            contentValues.put("address" , user.getAddress());
            contentValues.put("image" , user.getImage());

            db.update(TABLE_NAME , contentValues , "contact=?" , new String[]{user.getContact()});
            db.close();

            return true;


        }catch(Exception ex) {

            return false;

        }



    }

    public ArrayList<Park> getAllParks() {

        ArrayList<Park> parkArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query , null);

        cursor.moveToFirst();

        do {

            Park park = new Park();
            park.setId(cursor.getInt(0));
            park.setName(cursor.getString(1));
            park.setTime(cursor.getString(2));
            park.setDate(cursor.getString(3));
            park.setLatitude(cursor.getDouble(4));
            park.setLongitude(cursor.getDouble(5));
            park.setAddress(cursor.getString(6));
            park.setTimeStamp(cursor.getString(7));

            parkArrayList.add(park);

        } while (cursor.moveToNext());

        Log.d(TAG, "getAllParks: " + parkArrayList.size());

        cursor.close();
        db.close();

        return parkArrayList;




    }

    public Users getUser(String contact , long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{

            String query = "select * from " + TABLE_NAME + " where contact = " + contact;

            cursor = db.rawQuery(query , null);

            if (cursor.getCount()>0) {

                cursor.moveToFirst();

                Users users = new Users();
                users.setId(cursor.getLong(0));
                users.setName(cursor.getString(1));
                users.setContact(cursor.getString(2));
                users.setAddress(cursor.getString(3));
                users.setImage(cursor.getString(4));

                cursor.close();
                db.close();

                return users;

            }

        }catch(Exception ex) {

            Log.d(TAG, "getUser: " + ex.getMessage());
            return null;


        }

        return null;

    }



    public Users getUser(String contact) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
//        StringBuilder builder = new StringBuilder();
//        builder.append("\"");
//        builder.append(contact);
//        builder.append("\"");

//        Log.d(TAG, "getUser: " + builder.toString());

        try{

            String query = "select * from " + TABLE_NAME + " where contact = " + contact ;

            cursor = db.rawQuery(query , null);

            cursor.moveToFirst();

                Users user = new Users();
                user.setId(cursor.getLong(0));
                user.setName(cursor.getString(1));
                user.setContact(cursor.getString(2));
                user.setAddress(cursor.getString(3));
                user.setImage(cursor.getString(4));
                return user;


        }catch(Exception ex) {

            Log.d(TAG, "getUser: " + ex.getMessage());


        }finally {

            cursor.close();
            db.close();


        }

        return null;

    }

    public ArrayList<Park> getAllParksByDate(String mdate , int limit , int skip) {

        StringBuilder builder = new StringBuilder();
        builder.append("\"");
        builder.append(mdate);
        builder.append("\"");

        ArrayList<Park> parkArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME + " where date = " + builder.toString() +  " order by id DESC " + "limit " + Integer.toString(limit) + " offset " + Integer.toString(skip);

        Cursor cursor = db.rawQuery(query , null);

        cursor.moveToFirst();

        do {

            Park park = new Park();
            park.setId(cursor.getInt(0));
            park.setName(cursor.getString(1));
            park.setTime(cursor.getString(2));
            park.setDate(cursor.getString(3));
            park.setLatitude(cursor.getDouble(4));
            park.setLongitude(cursor.getDouble(5));
            park.setAddress(cursor.getString(6));
            park.setTimeStamp(cursor.getString(7));

            parkArrayList.add(park);

        } while (cursor.moveToNext());

        Log.d(TAG, "getAllParks: " + parkArrayList.size());

        cursor.close();
        db.close();

        return parkArrayList;

    }

    public long getLastIndex() {

        ArrayList<Park> parkArrayList = new ArrayList<>();

        long id;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query , null);

        if (cursor.getCount() == 0) {

            id = 1;

        } else {

            cursor.moveToLast();
            id = cursor.getLong(0) + 1;

        }

        cursor.close();
        db.close();

        Log.d(TAG, "getLastIndex: " + cursor.getCount());

        return id;

    }

    public void dropTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_TABLE);
        onCreate(db);


    }


}
