package me.sin.findrestaurants.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.sin.findrestaurants.model.Rating;

public class RatingService {

    private final SQLiteDatabase database;

    public RatingService(DatabaseService databaseService) {
        this.database = databaseService.getDatabase();
    }

    public List<Rating> listRatings(int restId, int page){
        String[] limit = DatabaseService.getLimitContext(page);
        try(Cursor cursor = this.database.rawQuery("select * from Rating where rest_id = ? limit ?,?", new String[]{String.valueOf(restId), limit[0], limit[1]})){
            List<Rating> ratings = new ArrayList<>();
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String userId = cursor.getString(cursor.getColumnIndex("user_id"));
                String review = cursor.getString(cursor.getColumnIndex("review"));
                float ratingPoint = (float)cursor.getDouble(cursor.getColumnIndex("rating"));
                Rating rating = new Rating(id, userId, ratingPoint, review);
                ratings.add(rating);
            }
            return ratings;
        }
    }

    public boolean createRating(int restId, Rating rating) {
        if (rating.getRating() < 0.0f || rating.getRating() > 5.0f){
            throw new IllegalStateException("the rating star should between 0.0 and 0.5");
        }
       try(Cursor cursor = this.database.rawQuery("select user_id from Rating where rest_id = ? and user_id = ?", new String[]{String.valueOf(restId), rating.getAuthor()});
           SQLiteStatement stmt = this.database.compileStatement("insert into Rating (rest_id, user_id, review, rating) VALUES (?,?,?,?)")){
           if (cursor.getCount() == 0){
               stmt.bindDouble(1, restId);
               stmt.bindString(2, rating.getAuthor());
               stmt.bindString(3, rating.getReview());
               stmt.bindDouble(4, rating.getRating());
               return stmt.executeInsert() >= 0;
           }
           return false;
       }

    }

    public boolean deleteRating(int id) {
        String sql = "delete from Rating where id = ?";
        SQLiteStatement stmt = this.database.compileStatement(sql);
        stmt.bindDouble(1, id);
        return stmt.executeUpdateDelete() > 0;
    }


}
