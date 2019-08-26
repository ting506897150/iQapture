package com.example.vcserver.iqapture.view.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vcserver.iqapture.presenter.base.BasePresenter;
import com.example.vcserver.iqapture.util.MyProgressDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;


public abstract class BaseLlayout<P extends BasePresenter> extends LinearLayout implements IBaseView {

	protected P mPresenter;
	protected Context mContext;
	protected RxAppCompatActivity mActivity;
	View mMainView = null;
	Bundle mBundle;
	protected MyProgressDialog progressDialog;

	public BaseLlayout(Context context) {
		// TODO Auto-generated constructor stub
		this(context, null);
	}
	public BaseLlayout(final Context context, final Bundle bundle) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
        mBundle = bundle;
        initLayoutView(getLayoutId());
		initPresenter();
		init();
	}

	protected abstract int getLayoutId();
	protected abstract void initPresenter();
	protected abstract void init();
	private void initLayoutView(int layoutid) {
        mMainView = LayoutInflater.from(getContext()).inflate(layoutid, this);
        ButterKnife.bind(this,mMainView);
    }

	@Override
	public void showTip(String msg) {
		Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showLoadingDialog() {
		if (progressDialog == null) {
			progressDialog = new MyProgressDialog(mContext);
			progressDialog.setCanceledOnTouchOutside(false);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	@Override
	public void closeLoadingDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
