package com.example.vcserver.iqapture.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.vcserver.iqapture.R;


/**
 * Created by Administrator on 2017/2/17.
 */

public class MyProgressDialog extends ProgressDialog {

    private Context context;

    public MyProgressDialog(Context context) {
        super(context, R.style.dialog);
        this.context=context;
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        View view=View.inflate(context, R.layout.progressdialog_jiazai,null);
        setContentView(view);
        view.setClickable(false);
    }
}
