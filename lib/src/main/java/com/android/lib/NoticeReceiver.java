package com.android.lib;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.android.lib.utils.Assert;

import java.util.List;

public class NoticeReceiver {
    List<INotice> mNoticeCache;

    void accept(AppCompatActivity activity, INotice notice) {
        if (null == notice || null == activity || !activity.getWindow().isActive() || activity.isFinishing()) {
            return;
        }

        final View view = notice.getView(notice);
        Assert.assertNull(view);

        activity.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
