package com.example.vcserver.iqapture.config;

import com.example.vcserver.iqapture.presenter.base.PVBaseListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.http.HttpManager;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.listener.HttpOnNextListener;

/**
 * Created by Administrator on 2017/1/4/0004.
 */

public class BaseModelImp implements BaseModel,HttpOnNextListener {

    private PVBaseListener mListenter;

    public BaseModelImp(PVBaseListener mListenter) {
        this.mListenter = mListenter;
    }

    @Override
    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        HttpManager manager = new HttpManager(this, rxAppCompatActivity);
        manager.doHttpDeal(baseApi);
    }


    @Override
    public void onNext(String resulte, String mothead) {
        mListenter.onNext(resulte,mothead);
    }

    @Override
    public void onError(ApiException e) {
        mListenter.onError(e);
    }
}
