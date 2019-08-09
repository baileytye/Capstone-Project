package com.bowtye.decisive.Helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bowtye.decisive.R;

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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface warningCallback{
        public void warningClicked(int result);
    }
}
