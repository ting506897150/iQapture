package com.example.vcserver.iqapture.view.other;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.login.LoginApi;
import com.example.vcserver.iqapture.bean.login.LoginResult;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.presenter.other.LoginPresenter;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.base.BaseActivity;
import com.example.vcserver.iqapture.view.other.view.ILoginView;
import com.google.gson.reflect.TypeToken;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 欢迎界面
 */

public class WelComeActivity extends BaseActivity<LoginPresenter> implements ILoginView {
    private String username,password;
    private LoginApi loginApi;
    ContentValues cv1;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void initPresenter() {
        mPresenter = new LoginPresenter(mContext,this);
    }

    @Override
    protected void init() {
        username = SharedPreferencesUtil.getsInstances(this).getString(Preferences.USERNAME,"");
        password = SharedPreferencesUtil.getsInstances(this).getString(Preferences.PASSWORD,"");
        if(AppUtil.isNetworkAvailable(this)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)){
                        //登陆接口调用
                        loginApi = new LoginApi();
                        loginApi.setUsername(username);
                        loginApi.setPassword(password);
                        mPresenter.startPost(WelComeActivity.this,loginApi);
                    }else{
                        Intent intent = new Intent(WelComeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            },1000);
        }else{
            db = myDatabaseHelper.getWritableDatabase();
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                //取出登陆返回数据
                Cursor cursor1 = db.query("loginresult", new String[]{"UserID","Username","DefaultCompany","MyCompanies"}, null, null, null, null, null);

                while(cursor1.moveToNext()){
                    UserID = cursor1.getInt(cursor1.getColumnIndex("UserID"));
                    Username = cursor1.getString(cursor1.getColumnIndex("Username"));
                    DefaultCompany = cursor1.getInt(cursor1.getColumnIndex("DefaultCompany"));
                    MyCompanies = cursor1.getString(cursor1.getColumnIndex("MyCompanies"));
                }
                db.close();
                //转化
                Type type = new TypeToken<List<LoginResult.VCCompany>>(){ }.getType();
                vcCompanyList = gson.fromJson(MyCompanies, type);
                vcAccount.setUserID(UserID);
                vcAccount.setUsername(Username);
                vcAccount.setDefaultCompany(DefaultCompany);
                vcAccount.setMyCompanies(vcCompanyList);
                //存储数据到本地跳转到首页
                StorageAndJump();
            }else{
                Intent intent = new Intent(WelComeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    @Override
    public void getLogin(LoginResult result) {
        if (result.getResultCode() == 0){
            //存储账号密码
            //连接数据库
            db = myDatabaseHelper.getWritableDatabase();
            //判断表是否为空，便于每次重新添加数据
            if (db.rawQuery("SELECT * FROM login",null).getCount() > 0){
                myDatabaseHelper.Delete(db);
            }
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("password", password);
            db.insert("login", null, cv);

            //存储登陆返回数据
            vcAccount = result.getResultObject();
            //VCCompany
            vcCompanyList = vcAccount.getMyCompanies();
            inputString = gson.toJson(vcCompanyList);
            //VCAccount
            cv1 = new ContentValues();
            cv1.put("UserID", vcAccount.getUserID());
            cv1.put("Username", vcAccount.getUsername());
            cv1.put("DefaultCompany", vcAccount.getDefaultCompany());
            cv1.put("MyCompanies", inputString);
            db.insert("loginresult", null, cv1);
            db.close();
            cv1 = null;
            //存储数据到本地跳转到首页
            StorageAndJump();
        }else{
            showTip("Account or password error!");
        }
    }

    @Override
    protected void onDestroy() {
        appManager.destory(this);
        super.onDestroy();
    }
}
