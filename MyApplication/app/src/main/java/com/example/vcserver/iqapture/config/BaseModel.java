package com.example.vcserver.iqapture.config;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

/**
 * Created by Administrator on 2017/1/4/0004.
 */

public interface BaseModel {
    void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi);
}
