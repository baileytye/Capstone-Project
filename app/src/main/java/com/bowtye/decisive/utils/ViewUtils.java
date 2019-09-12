package com.bowtye.decisive.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

public class ViewUtils {

    public static final int DIALOG_OK = 1;
    public static final int DIALOG_CANCEL = -1;

    public static void showErrorDialog(String title, String message, Context context) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showWarningDialog(String message, Context context, warningCallback callback){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok",
                        (dialog, id) -> callback.warningClicked(DIALOG_OK))
                .setNegativeButton("cancel",
                        (dialog, id) -> dialog.cancel())
                .show();
    }

    public static void showYesNoDialog(String message, Context context, yesNoCallback callback){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> callback.yesNoResponse(true))
                .setNegativeButton("No",
                        (dialog, id) -> callback.yesNoResponse(false))
                .show();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface warningCallback{
        public void warningClicked(int result);
    }

    public interface yesNoCallback{
        public void yesNoResponse(boolean isYes);
    }
}
