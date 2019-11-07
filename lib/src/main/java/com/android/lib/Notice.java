package com.android.lib;

import android.view.View;

public class Notice<T> implements Comparable<Notice<T>> {

    private Notice() {
    }

    private String title;
    private String iconUrl;
    private int iconResId;
    private String content;
    private T data;
    private long residenceTime;
    private int priority;

    private int viewType = 0;
    private View noticeView;
    private int topMargin;
    private int noticeViewLayoutId;
    private ViewBinder viewBinder;

    Notice<T> next = null;

    /** 回收 */
    public static final Object sPoolSync = new Object();
    private static Notice sPool;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 50;

    /** 获取标题 */
    public String getTitle() {
        return title;
    }

    /** 获取图标链接 */
    public String getIconUrl() {
        return iconUrl;
    }

    /** 获取图标资源Id */
    public int getIconResId() {
        return iconResId;
    }

    /** 获取内容 */
    public String getContent() {
        return content;
    }

    /** 扩展数据 */
    public T getData() {
        return data;
    }

    /** 获取站内信通知停留时间（毫秒） */
    public long getResidenceTime() {
        return residenceTime;
    }

    /**
     * 获取视图展示优先级
     * 多种视图类型：
     * 优先级相同则顺序展示
     * 优先级高的先展示
     */
    public int getPriority() {
        return priority;
    }

    /** 获取站内信通知自定义视图 */
    public View getNoticeView() {
        return noticeView;
    }

    /** 获取站内信通知顶部外边距 */
    public int getTopMargin() {
        return topMargin;
    }

    public int getNoticeViewLayoutId() {
        return noticeViewLayoutId;
    }

    public int getViewType() {
        return viewType;
    }

    /** 获取站内通知视图监听 */
    public ViewBinder getViewBinder() {
        return viewBinder;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setResidenceTime(long residenceTime) {
        this.residenceTime = residenceTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setNoticeView(int viewType, View noticeView) {
        if (noticeView != null && viewType < 1) {
            throw new RuntimeException("The value of viewType must start from 1");
        }
        this.viewType = viewType;
        this.noticeView = noticeView;
    }

    public void setNoticeViewLayoutId(int viewType, int layoutId) {
        if (layoutId != 0 && viewType < 1) {
            throw new RuntimeException("The value of viewType must start from 1");
        }
        this.viewType = viewType;
        this.noticeViewLayoutId = layoutId;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public void setViewBinder(ViewBinder binder) {
        this.viewBinder = binder;
    }

    @Override
    public int compareTo(Notice<T> o) {
        return getPriority() - o.getPriority();
    }

    public static Notice obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Notice m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;
                return m;
            }
        }
        return new Notice();
    }

    void recycle() {
        title = null;
        iconUrl = null;
        iconResId = 0;
        content = null;
        data = null;
        residenceTime = 0;
        priority = 0;
        viewType = 0;
        noticeView = null;
        topMargin = 0;
        noticeViewLayoutId = 0;
        viewBinder = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    void recycleAll() {
        Notice p = this;
        Notice next = p.next;
        while (next != null) {
            p = next;
            next = next.next;
            p.recycle();
        }

        recycle();
    }

    public interface ViewBinder {
        void bindView(View view, Notice notice);
    }
}
