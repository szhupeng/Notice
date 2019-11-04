package com.android.lib;

import android.view.View;

public class Notice<T> implements Comparable<Notice<T>> {

    private String mTitle;
    private String mIconUrl;
    private int mIconResId;
    private String mContent;
    private T mExtendedData;
    private long mResidenceTime;
    private int mPriority;
    private INoticeView mNoticeView;
    private NoticeViewListener mNoticeViewListener;

    Notice<T> mNext = null;

    /** 获取标题 */
    public String getTitle() {
        return mTitle;
    }

    /** 获取图标链接 */
    public String getIconUrl() {
        return mIconUrl;
    }

    /** 获取图标资源Id */
    public int getIconResId() {
        return mIconResId;
    }

    /** 获取内容 */
    public String getContent() {
        return mContent;
    }

    /** 扩展数据 */
    public T getExtendedData() {
        return mExtendedData;
    }

    /** 获取站内信通知停留时间（毫秒） */
    public long getResidenceTime() {
        return mResidenceTime;
    }

    /**
     * 获取视图展示优先级
     * 多种视图类型：
     * 优先级相同则顺序展示
     * 优先级高的先展示
     */
    public int getPriority() {
        return mPriority;
    }

    /** 获取站内信通知自定义视图 */
    public INoticeView getNoticeView() {
        return mNoticeView;
    }

    /** 获取站内通知视图监听 */
    public NoticeViewListener getNoticeViewListener() {
        return mNoticeViewListener;
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

    public void setExtendedData(T extendedData) {
        this.mExtendedData = extendedData;
    }

    public void setResidenceTime(long residenceTime) {
        this.mResidenceTime = residenceTime;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }

    public void setNoticeView(INoticeView noticeView) {
        this.mNoticeView = noticeView;
    }

    public void setNoticeViewListener(NoticeViewListener listener) {
        this.mNoticeViewListener = listener;
    }

    @Override
    public int compareTo(Notice<T> o) {
        return getPriority() - o.getPriority();
    }

    public interface NoticeViewListener {
        void onViewCreated(View view, Notice notice);
    }
}
