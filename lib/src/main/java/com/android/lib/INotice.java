package com.android.lib;

import android.view.View;

public interface INotice<T> {
    /** 获取图标链接 */
    String getIconUrl();

    /** 获取图标资源 */
    int getIconResId();

    /** 获取标题 */
    String getTitle();

    /** 获取内容 */
    String getContent();

    /** 获取站内信通知自定义视图 */
    INoticeView getNoticeView();

    /** 获取站内通知视图监听 */
    NoticeViewListener getNoticeViewListener();

    /** 获取站内信通知停留时间（毫秒） */
    long getResidenceTime();

    /** 扩展数据 */
    T getExtendedData();

    /**
     * 获取视图展示优先级
     * 多种视图类型：
     * 优先级相同则顺序展示
     * 优先级高的先展示
     */
    int getPriority();

    interface NoticeViewListener {
        void onViewCreated(View view, INotice notice);
    }
}
