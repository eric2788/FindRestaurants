package me.sin.findrestaurants.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.logging.Logger;

public class AuthService {

    private final SQLiteDatabase database;

    private String userId;

    public AuthService(DatabaseService service){
        this.database = service.getDatabase();
    }

    public boolean login(String username, String password){
       try(Cursor cursor = database.rawQuery("select id from User where id = ? and password = ?", new String[]{username, password})){
           if (cursor.moveToNext()){
               this.userId = cursor.getString(cursor.getColumnIndex("id"));
               return true;
           }else{
               return false;
           }
       }
    }

    public boolean register(String username, String password){

        try(Cursor cursor = database.rawQuery("select id from User where id = ?", new String[]{username})){
            if (cursor.moveToNext()){
                return false;
            }else{
                SQLiteStatement stmt = database.compileStatement("INSERT INTO User (id, password) VALUES (?, ?)");
                stmt.bindString(1, username);
                stmt.bindString(2, password);
                if (stmt.executeInsert() > 0){
                    this.userId = username;
                    return true;
                }else{
                    return false;
                }
            }
        }
    }

    public void logout(){
        this.userId = null;
    }

    @Nullable
    public String getUserId() {
        return userId;
    }
}
