package com.example.vcserver.iqapture.view.other;

import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionResult;
import com.example.vcserver.iqapture.bean.dataset.ImageShowApi;
import com.example.vcserver.iqapture.bean.iQapture;
import com.example.vcserver.iqapture.presenter.other.QuestionPresenter;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.view.base.BaseActivity;
import com.example.vcserver.iqapture.view.other.view.IQuestionView;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by VCServer on 2018/5/8.
 */

public class PlusVideoActivity extends BaseActivity<QuestionPresenter> implements IQuestionView {
    @Bind(R.id.videoview)
    VideoView videoview;
    @Bind(R.id.img_play)
    ImageView imgPlay;

    String path;
    int intPositionWhenPause;
    MediaController mediaController;
    ImageShowApi imageShowApi;
    String filetype;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_plus_video);
    }

    @Override
    protected void initPresenter() {
        mPresenter = new QuestionPresenter(mContext, this);
    }

    @Override
    protected void init() {
        path = getIntent().getStringExtra("path");
        filetype = Other.getFileName(path);
        if (path.contains("@")){
            int end = path.indexOf("@");
            imageShowApi = new ImageShowApi();
            imageShowApi.setImgID(path.substring(0,end));
            mPresenter.startPost(this, imageShowApi);
        }else{
            VideoPlay();
        }
    }

    private void VideoPlay() {
        //初始化videoview控制条
        mediaController = new MediaController(this);
        //设置videoview的控制条
        videoview.setMediaController(mediaController);
        //设置显示控制条
        mediaController.show(0);
        videoview.setVideoPath(path);
        videoview.start();
        setVideoViewLayoutParams(1);
        //播放完成的监听
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoview.stopPlayback();
                imgPlay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setVideoViewLayoutParams(int paramsType) {
        if(1 == paramsType) {
            //设置充满整个父布局
            RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            //设置相对于父布局四边对齐
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //为VideoView添加属性
            videoview.setLayoutParams(LayoutParams);
        }
    }

    @OnClick({R.id.img_play, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_play:
                imgPlay.setVisibility(View.GONE);
                VideoPlay();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    /**
     * 页面暂停效果处理
     */
    @Override
    protected  void onPause() {
        super.onPause();
        //如果当前页面暂停则保存当前播放位置，全局变量保存
        intPositionWhenPause = videoview.getCurrentPosition();
        //停止回放视频文件
        videoview.stopPlayback();
    }

    /**
     * 页面从暂停中恢复
     */
    @Override
    protected void onResume() {
        super.onResume();
        //跳转到暂停时保存的位置
        if(intPositionWhenPause>=0){
            videoview.seekTo(intPositionWhenPause);
            //初始播放位置
            intPositionWhenPause = -1;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        imgPlay.setVisibility(View.GONE);
        videoview.start();
        videoview.setFocusable(true);
    }

    @Override
    public void getQuestion(Questionnaires result) {

    }

    @Override
    public void getEditQuestion(EditQuestionResult result) {

    }

    @Override
    public void getImageAdd(String result) {

    }

    @Override
    public void getImageShow(String result) {
        path = "/storage/emulated/0/Android/data/com.example.vcserver.iqapture/"+System.currentTimeMillis() + filetype;
        try {
            Other.decoderBase64File(result,path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appManager.destory(this);
    }
}
