package com.example.vcserver.iqapture.presenter.base;

import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

/**
 * Created by Administrator on 2017/1/4/0004.
 */

public interface PVBaseListener {

    /**
     * 成功后回调方法
     *
     * @param resulte
     * @param mothead
     */
    void onNext(String resulte, String mothead);

    /**
     * 失败
     * 失败或者错误方法
     * 自定义异常处理
     *
     * @param e
     */
    void onError(ApiException e);
}
