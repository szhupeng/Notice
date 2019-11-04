package com.android.lib;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractNoticeReceiver {

    protected final int mScreenWidth, mScreenHeight;

    protected final SparseArray<View> mNoticeViews;

    protected final List<INotice> mCachedNotices;

    public AbstractNoticeReceiver() {
        mNoticeViews = new SparseArray<>(1);
        mCachedNotices = new LinkedList<>();

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public abstract void showNotice(Activity activity, INotice notice);

    public abstract void hideNotice(Activity activity);
}
