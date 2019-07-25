package com.bowtye.decisive.Helpers;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogHelper {

    public static void showErrorDialog(String title, String message, Context context) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
