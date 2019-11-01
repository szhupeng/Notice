package com.android.lib;

import android.app.Activity;
import android.view.View;

public abstract class INoticeView {

    /***
     * 创建站内信通知视图
     *
     * @param activity
     * @param notice
     * @param viewType
     * @return
     */
    protected abstract View createView(Activity activity, INotice notice, int viewType);

    /***
     * 获取站内信通知视图类型
     *
     * @param notice
     * @return 返回值从1开始
     */
    protected int getViewType(INotice notice) {
        return 1;
    }
}
