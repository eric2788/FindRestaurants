package me.sin.findrestaurants.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private final static int DB_VERSION = 2;
    private final static String DB_NAME = "rests.db";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS User(" +
                        "`id` varchar(10) primary key NOT NULL, " +
                        "`password` longtext NOT NULL)");
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Restaurant ( " +
                        "`id` INTEGER primary key autoincrement, " +
                        "`name` varchar(10) NOT NULL, " +
                        "`website` varchar(40), " +
                        "`phone` int(8), " +
                        "`category` tinytext NOT NULL," +
                        "`photo` longtext, " +
                        "`joined_date` long NOT NULL," +
                        "`address` longtext NOT NULL," +
                        "`owner` varchar(10) REFERENCES User(id) NOT NULL)");
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Comment(" +
                        "`id` INTEGER primary key autoincrement," +
                        "`rest_id` int REFERENCES Restaurant(id)," +
                        "`user_id` varchar(10) REFERENCES User(id)," +
                        "`date` long NOT NULL," +
                        "`content` longtext NOT NULL)");
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Rating(" +
                        "`id` INTEGER primary key autoincrement," +
                        "`rest_id` int REFERENCES Restaurant(id)," +
                        "`user_id` varchar(10) REFERENCES User(id)," +
                        "`review` tinytext default NULL," +
                        "`rating` double NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.reset(db);
    }

    public void reset(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Restaurant");
        db.execSQL("DROP TABLE IF EXISTS Comment");
        db.execSQL("DROP TABLE IF EXISTS Rating");
    }
}
