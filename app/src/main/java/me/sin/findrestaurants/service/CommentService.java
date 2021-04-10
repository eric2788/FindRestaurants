package me.sin.findrestaurants.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.sin.findrestaurants.model.Comment;

public class CommentService {

    private final SQLiteDatabase database;

    public CommentService(DatabaseService databaseService) {
        this.database = databaseService.getDatabase();
    }

    public List<Comment> listComments(int restId, int page) {
        String[] limit = DatabaseService.getLimitContext(page);
        try (Cursor cursor = this.database.rawQuery("select * from Comment where rest_id = ? order by date desc limit ?,?", new String[]{String.valueOf(restId), limit[0], limit[1]})) {
            List<Comment> comments = new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String userId = cursor.getString(cursor.getColumnIndex("user_id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                Date date = new Date(cursor.getLong(cursor.getColumnIndex("date")));
                Comment comment = new Comment(id, userId, content, date);
                comments.add(comment);
            }
            return comments;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean createComment(int restId, Comment comment) {
        try (Cursor cursor = this.database.rawQuery("select user_id from Comment where rest_id = ? and user_id = ?", new String[]{String.valueOf(restId), comment.getAuthor()});
             SQLiteStatement stmt = this.database.compileStatement("insert into Comment (rest_id, user_id, content, date) VALUES (?,?,?,?)")) {
            if (cursor.getCount() == 0) {
                stmt.bindDouble(1, restId);
                stmt.bindString(2, comment.getAuthor());
                stmt.bindString(3, comment.getComments());
                stmt.bindLong(4, comment.getDate().getTime());
                return stmt.executeInsert() >= 0;
            }
            return false;
        }

    }

    public boolean deleteComment(int id) {
        String sql = "delete from Comment where id = ?";
        SQLiteStatement stmt = this.database.compileStatement(sql);
        stmt.bindDouble(1, id);
        return stmt.executeUpdateDelete() > 0;
    }
}
