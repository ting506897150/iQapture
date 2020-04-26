package com.example.vcserver.iqapture.view.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.login.LoginResult;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.presenter.base.BasePresenter;
import com.example.vcserver.iqapture.util.AppManager;
import com.example.vcserver.iqapture.util.DatasetService;
import com.example.vcserver.iqapture.util.MyDatabaseHelper;
import com.example.vcserver.iqapture.util.MyProgressDialog;
import com.example.vcserver.iqapture.util.MyRecordDatabaseHelper;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.other.MainActivity;
import com.example.vcserver.iqapture.view.other.QuestionnaireActivity;
import com.google.gson.Gson;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/1/4/0004.
 */

/**
 * Created by 36483 on 2016/12/19.
 */

public abstract class BaseActivity<P extends BasePresenter> extends RxAppCompatActivity implements IBaseView {
    protected P mPresenter;

    protected MyProgressDialog progressDialog;

    protected Context mContext;
    protected AppManager appManager;

    //SQLite数据库帮助类
    protected MyDatabaseHelper myDatabaseHelper;
//    protected MyRecordDatabaseHelper myRecordDatabaseHelper;
    protected SQLiteDatabase db;
    //写入和读出的数据信息
    protected String inputString = "";
    //登陆返回参数
    protected int UserID = 0;
    protected String Username = "";
    protected int DefaultCompany = 0;
    protected String MyCompanies = "";
    //登陆返回数据对象、集合
    protected List<LoginResult.VCCompany> vcCompanyList = new ArrayList<>();
    protected LoginResult.VCAccount vcAccount = new LoginResult.VCAccount();

    protected SharedPreferencesUtil editor;
    protected Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BaseActivity", "onCreate: ");
        editor = SharedPreferencesUtil.getsInstances(this);
        BaseApi.setBaseUrl(editor.getString(Preferences.LOGIN,BaseApi.baseUrl));
        myDatabaseHelper = new MyDatabaseHelper(this);
//        myRecordDatabaseHelper = new MyRecordDatabaseHelper(this);
        appManager = AppManager.getInstant();
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
        mContext=this;
        initView();
        ButterKnife.bind(this);
        initPresenter();
        init();
        appManager.addActivity(this);

        ViewGroup decorViewGroup = (ViewGroup) getWindow().getDecorView();
        View statusBarView = new View(getWindow().getContext());
        int statusBarHeight = getStatusBarHeight(getWindow().getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.parseColor("#1B1B1B"));
        decorViewGroup.addView(statusBarView);

    }

    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    protected abstract void initView();

    protected abstract void initPresenter();

    protected abstract void init();

    @Override
    public void showTip(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new MyProgressDialog(this);
        }
        if (!isFinishing() && !progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void closeLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    //Android 界面点击其他部分隐藏弹出框
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void StorageAndJump() {
        //存储用户id，公司id
        editor = SharedPreferencesUtil.getsInstances(this);
        editor.putString(Preferences.USERNAMES, vcAccount.getUsername());
        editor.putInt(Preferences.USERID, vcAccount.getUserID());
        editor.putInt(Preferences.COMPANYID, vcAccount.getDefaultCompany());

        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("vcAccount", vcAccount);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
