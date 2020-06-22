package com.sapientia.catchit.dao;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class StringArrayListConverter
{
    @TypeConverter
    public static List<Integer> fromString(String value)
    {
        if (value == null)
        {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<Integer> list)
    {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        Log.d("DEBUG-salCONV","String size from ArrayList: "+ json.length());
        return json;
    }
}
