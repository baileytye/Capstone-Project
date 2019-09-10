package com.bowtye.decisive.utils;

import java.util.Locale;

public class StringUtils {

    public static String convertToTwoDecimals(Double value){
        return String.format(Locale.getDefault(), "%.2f", value);
    }
}
