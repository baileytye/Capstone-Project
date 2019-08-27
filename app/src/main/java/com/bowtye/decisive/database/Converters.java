package com.bowtye.decisive.database;

import androidx.room.TypeConverter;

import com.bowtye.decisive.models.Requirement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    @TypeConverter
    public static Requirement.Type toType(int type){
        return Requirement.Type.values()[type];
    }

    @TypeConverter
    public static int toInt(Requirement.Type type){
        return type.ordinal();
    }

    @TypeConverter
    public static Requirement.Importance toImportance(int importance){
        return Requirement.Importance.values()[importance];
    }

    @TypeConverter
    public static int toInt(Requirement.Importance importance){
        return importance.ordinal();
    }


    @TypeConverter
    public static List<Double> fromStringDouble(String value){
        Type listType = new TypeToken<List<Double>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromListDouble(List<Double> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
