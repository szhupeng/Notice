package com.android.lib;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

class WindowNoticeReceiverImpl implements INoticeReceiver {

    @Override
    public void accept(Activity activity, INotice notice) {
        WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

        final int viewType = notice.getNoticeView().getViewType(notice);
        final View noticeView = notice.getNoticeView().createView(activity, notice, viewType);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.gravity = Gravity.CENTER | Gravity.TOP;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        manager.addView(noticeView, params);
    }

    @Override
    public void refuse(Activity activity) {
        WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        manager.removeView(null);
    }
}