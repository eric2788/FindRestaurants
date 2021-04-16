package me.sin.findrestaurants.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import me.sin.findrestaurants.data.SQLiteHelper;

public class DatabaseService {

    private SQLiteHelper sqLiteHelper;

    public static String[] getLimitContext(int page){
        return new String[]{(20 * (page - 1) + (page > 1 ? 1 : 0) + ""), (page * 20 + "")};
    }

    public void initializeDatabase(Context context){
        this.sqLiteHelper = new SQLiteHelper(context);
    }

    public SQLiteDatabase getDatabase(){
        return sqLiteHelper.getWritableDatabase();
    }

    public void dropTables(){
        sqLiteHelper.reset(sqLiteHelper.getWritableDatabase());
    }

    public void createTables(){
        sqLiteHelper.onCreate(sqLiteHelper.getWritableDatabase());
    }

    private void reset(){
        this.dropTables();
        this.createTables();
    }
}
