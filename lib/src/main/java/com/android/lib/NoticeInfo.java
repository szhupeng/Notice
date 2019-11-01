package com.android.lib;

class NoticeInfo implements INotice {

    private String mTitle;
    private String mIconUrl;
    private int mIconResId;
    private String mContent;
    private INoticeView mNoticeView;
    private long mResidenceTime;
    private int mPriority;
    private NoticeViewListener mNoticeViewListener;

    @Override
    public String getIconUrl() {
        return mIconUrl;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getContent() {
        return mContent;
    }

    @Override
    public INoticeView getNoticeView() {
        return mNoticeView;
    }

    @Override
    public long getResidenceTime() {
        return mResidenceTime;
    }

    @Override
    public int getIconResId() {
        return mIconResId;
    }

    @Override
    public NoticeViewListener getNoticeViewListener() {
        return mNoticeViewListener;
    }

    @Override
    public Object getExtendedData() {
        return null;
    }

    @Override
    public int getPriority() {
        return mPriority;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setIconUrl(String iconUrl) {
        this.mIconUrl = iconUrl;
    }

    public void setIconResId(int iconResId) {
        this.mIconResId = iconResId;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public void setNoticeView(INoticeView noticeView) {
        this.mNoticeView = noticeView;
    }

    public void setResidenceTime(long residenceTime) {
        this.mResidenceTime = residenceTime;
    }

    public void setNoticeViewListener(NoticeViewListener listener) {
        this.mNoticeViewListener = listener;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }
}
