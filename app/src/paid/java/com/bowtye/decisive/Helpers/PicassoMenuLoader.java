package com.bowtye.decisive.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PicassoMenuLoader implements Target {

    MenuItem mMenuItem;
    Context mContext;

    public PicassoMenuLoader(MenuItem mMenuItem, Context context) {
        this.mMenuItem = mMenuItem;
        mContext = context;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        mMenuItem.setIcon(roundedBitmapDrawable);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
