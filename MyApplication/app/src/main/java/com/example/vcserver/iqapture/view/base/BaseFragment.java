package com.example.vcserver.iqapture.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vcserver.iqapture.presenter.base.BasePresenter;
import com.example.vcserver.iqapture.util.MyProgressDialog;

import butterknife.ButterKnife;


/**
 * Created by dahei on 2017/9/13.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment{
    protected P mPresenter;
    private Toast toast;
    private MyProgressDialog progress;
    public ViewGroup container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.container=container;
        View myView = getMyView();
        ButterKnife.bind(this, myView);
        return myView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPresenter();
        init();
        initDatas();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        listen();
    }

    //获取fragment的view
    protected abstract View getMyView();
    //初始化操作
    protected abstract void init();
    //处理数据
    protected void initDatas(){}
    //处理后台逻辑
    protected abstract void initPresenter();
    //设置各个控件的监听
    protected abstract void listen();

    public synchronized void showTip(String msg) {
        if(toast==null){
            toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
        }else{
            toast.setText(msg);
            toast.show();
        }
    }

    //显示加载框
    public void showLoadingDialog(){
        if (progress == null) {
            progress = new MyProgressDialog(getContext());
        }
        if (!progress.isShowing()) {
            progress.show();
        }
    }

    //关闭加载框
    public void closeLoadingDialog(){
        if(progress!=null&&progress.isShowing()){
            progress.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progress!=null){
            progress=null;
        }
        ButterKnife.unbind(this);
    }
}
