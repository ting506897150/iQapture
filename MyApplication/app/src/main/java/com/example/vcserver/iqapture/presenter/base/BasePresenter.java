package com.example.vcserver.iqapture.presenter.base;

import android.content.Context;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

/**
 * Created by Administrator on 2017/1/4/0004.
 */

public abstract class BasePresenter {
    protected Context mContext;
    protected int mState=0;

    public BasePresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * http请求
     * @param rxAppCompatActivity
     * @param baseApi
     */
    public abstract void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi);

    /**
     * http请求
     * @param rxAppCompatActivity
     * @param baseApi
     * @param state
     */
    public abstract void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state);

}
