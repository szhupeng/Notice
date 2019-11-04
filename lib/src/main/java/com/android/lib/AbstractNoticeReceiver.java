package com.android.lib;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

public abstract class AbstractNoticeReceiver {

    protected final int mScreenWidth, mScreenHeight;

    protected final SparseArray<View> mNoticeViews;

    protected Notice mReadyNotice;

    public AbstractNoticeReceiver() {
        mNoticeViews = new SparseArray<>(1);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public abstract void showNotice(Activity activity, Notice notice);

    public abstract void hideNotice(Activity activity);

    protected void addNotice(Notice notice) {
        if (null == mReadyNotice) {
            mReadyNotice = notice;
            return;
        }

        Notice last = null;
        Notice current = mReadyNotice;
        Notice next = current.next();
        do {
            if (notice.compareTo(current) < 0 && null == last) {
                notice.link(current);
                mReadyNotice = notice;
                break;
            }

            if (notice.compareTo(current) >= 0 && null == next) {
                current.link(notice);
                break;
            }

            if (notice.compareTo(last) >= 0 && notice.compareTo(current) < 0) {
                notice.link(current);
                last.link(notice);
                break;
            }

            last = current;
            current = last.next();
            next = current.next();
        } while (mReadyNotice.next() != null);
    }

    protected Notice getNotice() {
        final Notice temp = mReadyNotice;
        mReadyNotice = temp.next();
        return temp.unlink();
    }
}
