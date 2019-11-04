package com.android.lib;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

public abstract class AbstractNoticeReceiver {

    protected final int mScreenWidth, mScreenHeight;

    protected final SparseArray<View> mNoticeViews;

    protected Notice mReadyNotices;

    public AbstractNoticeReceiver() {
        mNoticeViews = new SparseArray<>(1);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public abstract void showNotice(Activity activity, Notice notice);

    public abstract void hideNotice(Activity activity);

    protected void addNotice(Notice notice) {
        Notice p = mReadyNotices;
        if (p == null || notice.compareTo(p) < 0) {
            notice.mNext = p;
            mReadyNotices = notice;
        } else {
            Notice prev;
            for (; ; ) {
                prev = p;
                p = p.mNext;
                if (p == null || notice.compareTo(p) < 0) {
                    break;
                }
            }
            notice.mNext = p;
            prev.mNext = notice;
        }
    }

    protected Notice getNotice() {
        final Notice temp = mReadyNotices;
        mReadyNotices = temp.mNext;
        temp.mNext = null;
        return temp;
    }
}
