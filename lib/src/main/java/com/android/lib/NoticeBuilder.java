package com.android.lib;

import java.util.concurrent.TimeUnit;

public class NoticeBuilder {
    protected String mTitle;
    protected String mIconUrl;
    protected int mIconResId;
    protected String mContent;
    protected INoticeView mNoticeView;
    protected long mResidenceTime;
    protected TimeUnit mTimeUnit;
    protected Notice.NoticeViewListener mNoticeViewListener;
    protected boolean mRecycleData;
    protected int mPriority;

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

    public NoticeBuilder setNoticeView(INoticeView view) {
        this.mNoticeView = view;
        return this;
    }

    public NoticeBuilder setResidenceTime(long residenceTime, TimeUnit timeUnit) {
        this.mResidenceTime = residenceTime;
        this.mTimeUnit = timeUnit;

        return this;
    }

    public NoticeBuilder setNoticeViewListener(Notice.NoticeViewListener listener) {
        this.mNoticeViewListener = listener;
        return this;
    }

    public NoticeBuilder setPriority(int priority) {
        this.mPriority = priority;
        return this;
    }

    public Notice build() {
        Notice info = new Notice();
        info.setIconUrl(mIconUrl);
        info.setIconResId(mIconResId);
        info.setContent(mContent);
        info.setNoticeView(mNoticeView);
        info.setTitle(mTitle);
        if (mResidenceTime > 0 && mTimeUnit != null) {
            info.setResidenceTime(mTimeUnit.toMillis(mResidenceTime));
        }
        info.setNoticeViewListener(mNoticeViewListener);
        info.setPriority(mPriority);
        return info;
    }
}
