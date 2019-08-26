package com.example.vcserver.iqapture.bean.login;

import com.example.vcserver.iqapture.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by VCServer on 2018/3/7.
 */

public class LoginApi extends BaseApi {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginApi(){
        setMothed(loginUrl);
    }//AnalytiQs/Interface/SignIn   api/account/SingIn

    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService service = retrofit.create(HttpPostService.class);
        return service.login(getUsername(),getPassword());
    }
}
