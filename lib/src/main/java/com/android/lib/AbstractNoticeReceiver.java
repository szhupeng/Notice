package com.android.lib;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

public abstract class AbstractNoticeReceiver {

    protected final int mScreenWidth, mScreenHeight;

    protected final SparseArray<View> mNoticeViews;

    protected Notice mReadyNotices;

    protected long mResidenceTime;
    protected boolean mShowing = false;

    protected Handler mHandler;

    protected AbstractNoticeReceiver() {
        mResidenceTime = 3 * 1000;

        mNoticeViews = new SparseArray<>(1);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        mHandler = new Handler(Looper.getMainLooper());
    }

    public abstract void showNotice(Activity activity, Notice notice);

    public abstract void hideNotice(Activity activity);

    protected void addNotice(Notice notice) {
        Notice p = mReadyNotices;
        if (null == p || notice.compareTo(p) > 0) {
            notice.mNext = p;
            mReadyNotices = notice;
        } else {
            Notice prev;
            for (; ; ) {
                prev = p;
                p = p.mNext;
                if (p == null || notice.compareTo(p) > 0) {
                    break;
                }
            }
            notice.mNext = p;
            prev.mNext = notice;
        }
    }

    protected Notice getNotice() {
        if (null == mReadyNotices) {
            return null;
        }

        final Notice p = mReadyNotices;
        mReadyNotices = p.mNext;
        p.mNext = null;
        return p;
    }

    protected Notice getNotice(final int viewType) {
        if (null == mReadyNotices) {
            return null;
        }

        Notice p = mReadyNotices;
        int type = p.getViewType();
        if (viewType == type) {
            mReadyNotices = p.mNext;
            p.mNext = null;
            return p;
        }

        Notice prev;
        for (; ; ) {
            prev = p;
            p = p.mNext;
            type = p.getViewType();
            if (p != null && viewType == type) {
                break;
            }
        }

        prev.mNext = p.mNext;
        p.mNext = null;
        return p;
    }

    protected void clearAllNotice() {
        mReadyNotices = null;
    }

    protected void setText(TextView textView, String text) {
        if (null == textView || TextUtils.isEmpty(text)) {
            return;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }
}
