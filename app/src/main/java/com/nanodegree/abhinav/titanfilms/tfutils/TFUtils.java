package com.nanodegree.abhinav.titanfilms.tfutils;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * This class is used for creating some static functions which can be used throughout app.
 *
 * Created by Abhinav Puri.
 */
public class TFUtils {

    /**
     * Gson object required.
     */
    private static Gson gson;

    /**
     * Statically initialized
     */
    static {
        gson = new Gson();
    }

    /**
     * Does conversion from dp to px.
     * @param resources
     * @param dp
     * @return float equivalent of pixels
     */
    public static float dpToPx(Resources resources, int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    /**
     * Sets status bar color.
     * @param window
     * @param color
     */
    public static void setStatusBarColor(Window window, int color){
        window.setStatusBarColor(color);
    }

    /**
     * Sets Roboto Light font for a given text view.
     * @param textView
     * @param assetManager
     */
    public static void setRobotoLight(TextView textView,AssetManager assetManager){
        textView.setTypeface(Typeface.createFromAsset(assetManager, "font/Roboto-Light.ttf"));
    }

    /**
     * Sets Roboto Light Italic font for a given text view.
     * @param textView
     * @param assetManager
     */
    public static void setRobotoLightItalic(TextView textView,AssetManager assetManager){
        textView.setTypeface(Typeface.createFromAsset(assetManager, "font/Roboto-LightItalic.ttf"));
    }

    /**
     * Returns application saved data.
     * @param titanFilmsApplication
     * @return application saved data
     */
    public static ApplicationSavedData populateApplicationSavedData(TitanFilmsApplication titanFilmsApplication){
        return titanFilmsApplication.applicationSavedData;
    }

    /**
     * Returns String value converted by Gson from integer array
     *
     * @param array
     * @return
     */
    public static String intArrayToGsonString(int[] array){
        return gson.toJson(array);
    }

    /**
     * Returns integer array from given Gson String.
     *
     * @param value
     * @return
     */
    public static int[] GsonStringToIntArray(String value){
        return gson.fromJson(value, int[].class);
    }

    /**
     * Returns string from given hash map.
     *
     * @param map
     * @return
     */
    public static String hashMapToGsonString(HashMap<String,String> map){
        return gson.toJson(map);
    }

    /**
     * Returns hash map from given Gson String of hash map.
     *
     * @param value
     * @return
     */
    public static HashMap<String,String> GsonStringToHashMap(String value){
        return gson.fromJson(value, new TypeToken<HashMap<String,String>>() {}.getType());
    }

    /**
     * Converts bitmap to byte array
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Converts bitmap from byte array
     *
     * @param image
     * @return
     */
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
