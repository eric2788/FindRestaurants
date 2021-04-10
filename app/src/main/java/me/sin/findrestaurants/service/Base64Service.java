package me.sin.findrestaurants.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64Service {

    public String resizeBase64Image(String base64image, int width, int height) {
        byte[] encodeByte = Base64.decode(base64image.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);

        if (image.getHeight() <= height && image.getWidth() <= width) {
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, width, height, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public String bitMapToString64(Bitmap bmap) {
        if (bmap == null) return "null";
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            bmap.compress(Bitmap.CompressFormat.PNG,100,bos);
            byte[] bb = bos.toByteArray();
            return  Base64.encodeToString(bb, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Nullable
    public Bitmap convertString64ToImage(@Nullable String base64String) {
        if (base64String == null) return null;
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
