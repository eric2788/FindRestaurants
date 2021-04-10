package me.sin.findrestaurants.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import me.sin.findrestaurants.model.Restaurant;
import me.sin.findrestaurants.model.RestaurantView;

public class RestaurantService {

    private final SQLiteDatabase database;


    public RestaurantService(DatabaseService databaseService) {
        this.database = databaseService.getDatabase();
    }

    public void createRestaurant(Restaurant restaurant) {
        this.database.insert("Restaurant", null, toContentValues(restaurant));
    }


    private ContentValues toContentValues(Restaurant restaurant) {
        ContentValues values = new ContentValues();
        values.put("name", restaurant.getTitle());
        values.put("website", restaurant.getWebsite());
        values.put("phone", restaurant.getPhone());
        values.put("category", restaurant.getCategory());
        values.put("photo", restaurant.getImageBase64());
        values.put("joined_date", restaurant.getJoinDate().getTime());
        values.put("address", restaurant.getAddress());
        values.put("owner", restaurant.getOwner());
        return values;
    }

    public void updateRestaurant(Restaurant restaurant) {
        String sql = "UPDATE Restaurant SET name=?, website=?, phone=?, category=?, photo=?, address=? WHERE id = ?";
        SQLiteStatement stmt = this.database.compileStatement(sql);
        stmt.bindString(1, restaurant.getTitle());
        stmt.bindString(2, restaurant.getWebsite());
        stmt.bindDouble(3, restaurant.getPhone());
        stmt.bindString(4, restaurant.getCategory());
        stmt.bindString(5, restaurant.getImageBase64());
        stmt.bindString(6, restaurant.getAddress());
        stmt.bindDouble(7, restaurant.getId());
        stmt.executeUpdateDelete();
    }

    public boolean deleteRestaurant(int id) {
        String sql = "DELETE FROM Restaurant WHERE id = ?";
        SQLiteStatement stmt = this.database.compileStatement(sql);
        stmt.bindDouble(1, id);
        return stmt.executeUpdateDelete() > 0;
    }

    @Nullable
    public Restaurant getRestaurant(int id) {
        String[] selectionId = new String[]{id + ""};
        try (Cursor restCur = this.database.rawQuery("select * from Restaurant where id = ?", selectionId);
             Cursor commentCur = this.database.rawQuery("select COUNT(*) from Comment where rest_id = ?", selectionId);
             Cursor ratingCur = this.database.rawQuery("select AVG(rating) from Rating where rest_id = ?", selectionId)) {
            if (restCur.moveToNext()) {
                String website = restCur.getString(restCur.getColumnIndex("website"));
                String title = restCur.getString(restCur.getColumnIndex("name"));
                int phone = restCur.getInt(restCur.getColumnIndex("phone"));
                String date = restCur.getString(restCur.getColumnIndex("joined_date"));
                String address = restCur.getString(restCur.getColumnIndex("address"));
                String owner = restCur.getString(restCur.getColumnIndex("owner"));
                String image64 = restCur.getString(restCur.getColumnIndex("photo"));
                String category = restCur.getString(restCur.getColumnIndex("category"));
                long timestamp = restCur.getLong(restCur.getColumnIndex("joined_date"));
                Date d = new Date(timestamp);
                float averageRating = ratingCur.moveToNext() ? (float) ratingCur.getDouble(0) : 0.0f;
                int totalComments = commentCur.moveToNext() ? commentCur.getInt(0) : 0;
                return new Restaurant(id, title, averageRating, website, phone, image64, address, totalComments, category, d, owner);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    public List<RestaurantView> listRestaurants(int page) {

        try (Cursor cursor = this.database.rawQuery("select name, photo, id, category from Restaurant order by joined_date desc limit ?,?", DatabaseService.getLimitContext(page))) {
            List<RestaurantView> views = new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("name"));
                String photo = cursor.getString(cursor.getColumnIndex("photo"));
                String category = cursor.getString(cursor.getColumnIndex("category"));
                Cursor ratingCur = this.database.rawQuery("select AVG(rating) from Rating where rest_id = ?", new String[]{String.valueOf(id)});
                float avgRating = ratingCur.moveToNext() ? (float) ratingCur.getDouble(0) : 0.0f;
                views.add(new RestaurantView(title, avgRating, photo, id, category));
                ratingCur.close();
            }
            return views;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public List<RestaurantView> searchRestaurants(String query, String[] category, int page) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < category.length; i++) {
            builder.append("'").append(category[i]).append("'");
            if (i != category.length - 1) {
                builder.append(", ");
            }
        }
        String selected = builder.toString();
        Logger.getGlobal().info("search selected: " + selected);
        Logger.getGlobal().info("search string: " + query);
        String[] contextLimit = DatabaseService.getLimitContext(page);
        String sql = String.format("select name, photo, id, joined_date, category from Restaurant where name like %s and category in (%s) order by joined_date desc limit ?,?", "'%" + query + "%'", selected);
        try (Cursor cursor = this.database.rawQuery(sql, new String[]{contextLimit[0], contextLimit[1]})) {
            List<RestaurantView> views = new ArrayList<>();
            Logger.getGlobal().info("searched result counts: " + cursor.getCount());
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("name"));
                String photo = cursor.getString(cursor.getColumnIndex("photo"));
                String cate = cursor.getString(cursor.getColumnIndex("category"));
                Cursor ratingCur = this.database.rawQuery("select AVG(rating) from Rating where rest_id = ?", new String[]{String.valueOf(id)});
                float avgRating = ratingCur.moveToNext() ? (float) ratingCur.getDouble(0) : 0.0f;
                views.add(new RestaurantView(title, avgRating, photo, id, cate));
                ratingCur.close();
            }
            return views;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
