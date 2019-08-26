package com.example.vcserver.iqapture.view.other;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.dataset.FilledResult;
import com.example.vcserver.iqapture.bean.login.LoginApi;
import com.example.vcserver.iqapture.bean.login.LoginResult;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.presenter.other.LoginPresenter;
import com.example.vcserver.iqapture.util.DatasetService;
import com.example.vcserver.iqapture.util.MyDatabaseHelper;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.base.BaseActivity;
import com.example.vcserver.iqapture.view.other.view.ILoginView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {

    @Bind(R.id.edit_Username)
    EditText editUsername;
    @Bind(R.id.edit_Password)
    EditText editPassword;
    @Bind(R.id.cb_passwprd)
    CheckBox cbPasswprd;
    @Bind(R.id.text_version)
    TextView textVersion;

    TextView text_url;
    TextView text_ip;

    private String UserName, PassWord;
    private LoginApi loginApi;
    Gson gson = new Gson();
    ContentValues cv1;
    SharedPreferencesUtil editor;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initPresenter() {
        mPresenter = new LoginPresenter(mContext, this);
    }

    @Override
    protected void init() {
        editor = SharedPreferencesUtil.getsInstances(this);
        textVersion.setText("v"+ Other.getAppVersionName(mContext));
        BaseApi.setBaseUrl(editor.getString(Preferences.LOGIN,BaseApi.baseUrl));
        text_url = findViewById(R.id.text_url);
        text_ip = findViewById(R.id.text_ip);
        text_url.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
//        text_ip.setText("当前IP是："+BaseApi.getBaseUrl());
    }

    @OnClick({R.id.text_login, R.id.img_url,R.id.text_url})
    public void onViewClicked(View view) {
        UserName = editUsername.getText().toString();
        PassWord = editPassword.getText().toString();
        switch (view.getId()) {
            case R.id.text_login:
                if(AppUtil.isNetworkAvailable(this)){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(UserName) && !TextUtils.isEmpty(PassWord)) {
                                if (cbPasswprd.isChecked()) {
                                    //存储用户名密码
                                    editor.putString(Preferences.USERNAME, UserName);
                                    editor.putString(Preferences.PASSWORD, PassWord);
                                }
                                showLoadingDialog();
                                //登陆接口调用
                                loginApi = new LoginApi();
                                loginApi.setUsername(UserName);
                                loginApi.setPassword(PassWord);
                                mPresenter.startPost(LoginActivity.this, loginApi);
                            } else if (TextUtils.isEmpty(UserName)) {
                                showTip("The user name cannot be empty!");
                            } else if (TextUtils.isEmpty(PassWord)) {
                                showTip("The password cannot be empty!");
                            }
                        }
                    }, 1000);
                }else{
                    db = myDatabaseHelper.getWritableDatabase();
                    if (!TextUtils.isEmpty(UserName) && !TextUtils.isEmpty(PassWord)) {
                        if (cbPasswprd.isChecked()) {
                            //存储用户名密码
                            editor.putString(Preferences.USERNAME, UserName);
                            editor.putString(Preferences.PASSWORD, PassWord);
                        }
                        //判断表是否为空
                        if (db.query("loginresult", new String[]{"UserID","Username","DefaultCompany","MyCompanies"}, null, null, null, null, null).getCount() > 0){
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
                            showTip("No network and no data!");
                        }
                    } else if (TextUtils.isEmpty(UserName)) {
                        showTip("The user name cannot be empty!");
                    } else if (TextUtils.isEmpty(PassWord)) {
                        showTip("The password cannot be empty!");
                    }
                }
                break;
            case R.id.img_url:
                Uri uri = Uri.parse("https://my.valuechain.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.text_url:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

    @Override
    public void getLogin(LoginResult result) {
        closeLoadingDialog();
        if (result.getResultCode() == 0) {
            //存储账号密码
            //连接数据库
            db = myDatabaseHelper.getWritableDatabase();
            //判断表是否为空，便于每次重新添加数据
            if (db.rawQuery("SELECT * FROM login",null).getCount() > 0){
                myDatabaseHelper.Delete(db);
            }
            ContentValues cv = new ContentValues();
            cv.put("username", UserName);
            cv.put("password", PassWord);
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
        } else {
            showTip("Account or password error!");
        }
    }


    @Override
    protected void onDestroy() {
        appManager.destory(this);
        super.onDestroy();
    }
}
