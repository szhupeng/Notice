package com.android.lib;

import android.app.Activity;

public class NoticeManager implements INoticeRegistry {

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

    @Override
    public void register(Activity activity) {
        mDispatcher.addNoticeReceiver(activity);
    }

    @Override
    public void unregister(Activity activity) {
        mDispatcher.removeNoticeReceiver(activity);
    }

    @Override
    public void resume(Activity activity) {
        mDispatcher.setReceiverVisibility(activity, true);
    }

    @Override
    public void pause(Activity activity) {
        mDispatcher.setReceiverVisibility(activity, false);
    }

    public void send(INotice notice) {
        mDispatcher.dispatch(notice);
    }
}
