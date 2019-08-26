package com.example.vcserver.iqapture.view.base;

/**
 * Created by 36483 on 2016/12/19.
 */

public interface IBaseView {
    /**
     * 显示提示信息
     *
     * @param msg
     */
    void showTip(String msg);

    /**
     * 显示加载对话框
     */
    void showLoadingDialog();

    /**
     * 关闭加载对话框
     */
    void closeLoadingDialog();
}
