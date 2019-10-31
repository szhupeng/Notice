package com.android.lib;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * 通知调度器
 */
class NoticeDispatcher implements LifecycleObserver {

    private final List<String> mReceiverTags;
    private INoticeReceiver mReceiver;

    private WeakReference<Activity> mCurrentActivityRef;

    public NoticeDispatcher() {
        this.mReceiverTags = new LinkedList<>();
        this.mReceiver = new NoticeReceiverImpl();
    }

    public void addNoticeReceiver(@NonNull Activity activity) {
        if (activity != null && activity instanceof LifecycleOwner) {
            ((LifecycleOwner) activity).getLifecycle().addObserver(this);
        }

        final String tag = activity.getClass().getName();
        if (!mReceiverTags.contains(tag)) {
            mReceiverTags.add(tag);
        }
    }

    public void removeNoticeReceiver(@NonNull Activity activity) {
        if (activity != null && activity instanceof LifecycleOwner) {
            ((LifecycleOwner) activity).getLifecycle().removeObserver(this);
        }

        final String tag = activity.getClass().getName();
        if (mReceiverTags.contains(tag)) {
            mReceiverTags.remove(tag);
        }
    }

    public void setReceiverVisibility(@NonNull Activity activity, boolean visible) {
        if (visible) {
            mCurrentActivityRef = new WeakReference<>(activity);
        } else {
            mCurrentActivityRef.clear();
        }
    }

    public void dispatch(INotice notice) {
        if (null == notice || null == mReceiver) {
            return;
        }

        mReceiver.accept(mCurrentActivityRef.get(), notice);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(@NonNull LifecycleOwner owner) {
        if (null == owner) {
            return;
        }

        if (owner instanceof Activity) {
            setReceiverVisibility((Activity) owner, true);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(@NonNull LifecycleOwner owner) {
        if (null == owner) {
            return;
        }

        if (owner instanceof Activity) {
            setReceiverVisibility((Activity) owner, false);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (mReceiver != null) {
            mReceiver.refuse();
        }

        if (owner != null && owner instanceof Activity) {
            removeNoticeReceiver((Activity) owner);
        }
    }
}
