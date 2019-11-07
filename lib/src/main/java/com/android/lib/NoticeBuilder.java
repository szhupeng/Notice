package com.android.lib;

import android.view.View;

import java.util.concurrent.TimeUnit;

public class NoticeBuilder {
    protected String mTitle;
    protected String mIconUrl;
    protected int mIconResId;
    protected String mContent;
    protected long mResidenceTime;
    protected TimeUnit mTimeUnit;
    protected int mPriority;

    private int mViewType;
    protected View mNoticeView;
    protected int mNoticeViewLayoutId;
    protected int mTopMargin;
    protected Notice.ViewBinder mViewBinder;

    public NoticeBuilder setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public NoticeBuilder setIconUrl(String iconUrl) {
        this.mIconUrl = iconUrl;
        return this;
    }

    public NoticeBuilder setIconResId(int iconResId) {
        this.mIconResId = iconResId;
        return this;
    }

    public NoticeBuilder setContent(String content) {
        this.mContent = content;
        return this;
    }

    public NoticeBuilder setResidenceTime(long residenceTime, TimeUnit timeUnit) {
        this.mResidenceTime = residenceTime;
        this.mTimeUnit = timeUnit;
        return this;
    }

    public NoticeBuilder setPriority(int priority) {
        this.mPriority = priority;
        return this;
    }

    public NoticeBuilder setNoticeView(int viewType, View view) {
        this.mViewType = viewType;
        this.mNoticeView = view;
        return this;
    }

    public NoticeBuilder setNoticeView(int viewType, int layoutId) {
        this.mViewType = viewType;
        this.mNoticeViewLayoutId = layoutId;
        return this;
    }

    public NoticeBuilder setTopMargin(int topMargin) {
        this.mTopMargin = topMargin;
        return this;
    }

    public NoticeBuilder setViewBinder(Notice.ViewBinder binder) {
        this.mViewBinder = binder;
        return this;
    }

    public Notice build() {
        Notice info = new Notice();
        info.setIconUrl(mIconUrl);
        info.setIconResId(mIconResId);
        info.setContent(mContent);
        info.setTitle(mTitle);
        if (mResidenceTime > 0 && mTimeUnit != null) {
            info.setResidenceTime(mTimeUnit.toMillis(mResidenceTime));
        }
        info.setNoticeView(mViewType, mNoticeView);
        info.setNoticeViewLayoutId(mViewType, mNoticeViewLayoutId);
        info.setPriority(mPriority);
        info.setTopMargin(mTopMargin);
        info.setViewBinder(mViewBinder);
        return info;
    }
}
