package com.example.vcserver.iqapture.presenter.other;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.vcserver.iqapture.bean.login.LoginResult;
import com.example.vcserver.iqapture.config.BaseModel;
import com.example.vcserver.iqapture.config.BaseModelImp;
import com.example.vcserver.iqapture.presenter.base.BasePresenter;
import com.example.vcserver.iqapture.presenter.base.PVBaseListener;
import com.example.vcserver.iqapture.view.other.view.ILoginView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

import static com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi.loginUrl;

/**
 * Created by Kang on 2017/7/12.
 */

public class LoginPresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model;
    private ILoginView view;

    public LoginPresenter(Context mContext, ILoginView view) {
        super(mContext);
        this.view = view;
        model = new BaseModelImp(this);
    }

    @Override
    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        model.startPost(rxAppCompatActivity, baseApi);
    }

    @Override
    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {

    }

    @Override
    public void onNext(String resulte, String mothead) {
        if (mothead.equals(BaseApi.loginUrl)){//AnalytiQs/Interface/SignIn   api/account/SingIn
            LoginResult result = JSON.parseObject(resulte, LoginResult.class);
            view.getLogin(result);
        }
    }

    @Override
    public void onError(ApiException e) {
        view.closeLoadingDialog();
//        int code = e.getCode();
//        if (code == 4){
//            view.showTip("Sorry,the username or password is incorrect.Please try again");
//        }else{
//            view.showTip(e.getDisplayMessage());
//        }
        view.showTip(e.getDisplayMessage());
    }
}
