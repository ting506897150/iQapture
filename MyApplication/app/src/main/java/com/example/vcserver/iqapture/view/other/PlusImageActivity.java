package com.example.vcserver.iqapture.view.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.view.adapter.ViewPagerAdapter;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;

public class PlusImageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager viewPager; //展示图片的ViewPager
    private TextView positionTv; //图片的位置，第几张图片
    private ArrayList<LocalMedia> selectList; //图片的数据源
    private int mPosition; //第几张图片
    private ViewPagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_image);

        selectList = getIntent().getParcelableArrayListExtra("selectList");
        mPosition = getIntent().getIntExtra("position", 0);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        positionTv = findViewById(R.id.position_tv);
        findViewById(R.id.back_iv).setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);

        mAdapter = new ViewPagerAdapter(this, selectList);
        viewPager.setAdapter(mAdapter);
        positionTv.setText(mPosition + 1 + "/" + selectList.size());
        viewPager.setCurrentItem(mPosition);
    }

    //返回上一个页面
    private void back() {
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        positionTv.setText(position + 1 + "/" + selectList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                //返回
                back();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //按下了返回键
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
