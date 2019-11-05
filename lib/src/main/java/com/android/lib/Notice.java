package com.android.lib;

import android.view.View;

import androidx.annotation.NonNull;

public class Notice<T> implements Comparable<Notice<T>> {

    private String mTitle;
    private String mIconUrl;
    private int mIconResId;
    private String mContent;
    private T mExtendedData;
    private long mResidenceTime;
    private int mPriority;

    private int mViewType = 0;
    private View mNoticeView;
    private int mNoticeViewLayoutId;
    private ViewBinder mViewBinder;

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
    public View getNoticeView() {
        return mNoticeView;
    }

    public int getNoticeViewLayoutId() {
        return mNoticeViewLayoutId;
    }

    public int getViewType() {
        return mViewType;
    }

    /** 获取站内通知视图监听 */
    public ViewBinder getViewBinder() {
        return mViewBinder;
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

    public void setNoticeView(int viewType, View noticeView) {
        if (noticeView != null && viewType < 1) {
            throw new RuntimeException("The value of viewType must start from 1");
        }
        this.mViewType = viewType;
        this.mNoticeView = noticeView;
    }

    public void setNoticeViewLayoutId(int viewType, int layoutId) {
        if (layoutId != 0 && viewType < 1) {
            throw new RuntimeException("The value of viewType must start from 1");
        }
        this.mViewType = viewType;
        this.mNoticeViewLayoutId = layoutId;
    }

    public void setViewBinder(ViewBinder binder) {
        this.mViewBinder = binder;
    }

    @Override
    public int compareTo(Notice<T> o) {
        return getPriority() - o.getPriority();
    }

    @NonNull
    @Override
    public String toString() {
        if (mNext != null) {
            return mContent + "\n" + mNext.toString();
        }

        return mContent;
    }

    public interface ViewBinder {
        void bindView(View view, Notice notice);
    }
}
