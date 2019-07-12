package com.bowtye.decisive.Database;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.bowtye.decisive.Models.Requirement;

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
}
