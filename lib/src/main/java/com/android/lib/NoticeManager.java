package com.android.lib;

import android.app.Activity;

public final class NoticeManager {

    private static volatile NoticeManager sInstance;
    private final NoticeDispatcher mDispatcher;

    public static NoticeManager getInstance() {
        if (null == sInstance) {
            synchronized (NoticeManager.class) {
                if (null == sInstance) {
                    sInstance = new NoticeManager();
                }
            }
        }

        return sInstance;
    }

    private NoticeManager() {
        mDispatcher = new NoticeDispatcher();
    }

    public void register(Activity activity) {
        mDispatcher.addNoticeReceiver(activity);
    }

    public void unregister(Activity activity) {
        mDispatcher.removeNoticeReceiver(activity);
    }

    public void resume(Activity activity) {
        mDispatcher.setReceiverVisibility(activity, true);
    }

    public void pause(Activity activity) {
        mDispatcher.setReceiverVisibility(activity, false);
    }

    public void send(INotice notice) {
        send(notice, new NoticeReceiverFactory());
    }

    public void send(INotice notice, ReceiverFactory factory) {
        mDispatcher.dispatch(notice, factory);
    }
}
