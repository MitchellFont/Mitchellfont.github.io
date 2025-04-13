package com.example.eventtrackerfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LoginDatabase extends SQLiteOpenHelper {

    public static final String DBNAME = "LoginDB";

    public LoginDatabase(@Nullable Context context) {
        super(context, "LoginDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table that will have users and passwords
        db.execSQL("create Table users(username TEXT primary key, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop Table if exists users");
    }


    // Add new users and passwords
    public Boolean insertData(String user, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues Values = new ContentValues();
        Values.put("username", user);
        Values.put("password", pass);
        long result = db.insert("users", null, Values);
        if (result == -1) return false;
        else
            return true;

    }

    // Look for existing username
    public Boolean checkUsername(String user){
        SQLiteDatabase db = this.getWritableDatabase();
        // Check database fo the username
        Cursor cursor = db.rawQuery("Select * from users where username = ?", new String[] {user});
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    // Look to see if the username and password are both correct
    public Boolean checkUsernamePassword(String user, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from users where username = ? and password = ?", new String[] {user, pass});
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }
}
