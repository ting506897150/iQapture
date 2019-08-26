package com.example.vcserver.iqapture.view.other;

import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
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
import com.luck.picture.lib.tools.DateUtils;

import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by VCServer on 2018/5/22.
 */

public class PlusAudioActivity extends BaseActivity<QuestionPresenter> implements IQuestionView {
    @Bind(R.id.tv_musicStatus)
    TextView tvMusicStatus;
    @Bind(R.id.tv_musicTime)
    TextView tvMusicTime;
    @Bind(R.id.musicSeekBar)
    SeekBar musicSeekBar;
    @Bind(R.id.tv_musicTotal)
    TextView tvMusicTotal;
    @Bind(R.id.tv_PlayPause)
    TextView tvPlayPause;
    MediaPlayer mediaPlayer;

    String path;
    ImageShowApi imageShowApi;
    boolean isPlayAudio = false;
    String filetype;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_plus_audio);
    }

    @Override
    protected void initPresenter() {
        mPresenter = new QuestionPresenter(mContext, this);
    }

    @Override
    protected void init() {
        path = getIntent().getStringExtra("path");
        filetype = Other.getFileName(path);
        if (path.contains("@")) {
            int end = path.indexOf("@");
            imageShowApi = new ImageShowApi();
            imageShowApi.setImgID(path.substring(0, end));
            mPresenter.startPost(this, imageShowApi);
        } else {
            initPlayer(path);
        }
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //  通过 Handler 更新 UI 上的组件状态
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    tvMusicTime.setText(DateUtils.timeParse(mediaPlayer.getCurrentPosition()));
                    musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    musicSeekBar.setMax(mediaPlayer.getDuration());
                    tvMusicTotal.setText(DateUtils.timeParse(mediaPlayer.getDuration()));
                    handler.postDelayed(runnable, 200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 初始化音频播放组件
     *
     * @param path
     */
    private void initPlayer(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            playAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     */
    private void playAudio() {
        if (mediaPlayer != null) {
            musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            musicSeekBar.setMax(mediaPlayer.getDuration());
        }
        String ppStr = tvPlayPause.getText().toString();
        if (ppStr.equals(getString(com.luck.picture.lib.R.string.picture_play_audio))) {
            tvPlayPause.setText(getString(com.luck.picture.lib.R.string.picture_pause_audio));
            tvMusicStatus.setText(getString(com.luck.picture.lib.R.string.picture_play_audio));
            playOrPause();
        } else {
            tvPlayPause.setText(getString(com.luck.picture.lib.R.string.picture_play_audio));
            tvMusicStatus.setText(getString(com.luck.picture.lib.R.string.picture_pause_audio));
            playOrPause();
        }
        if (isPlayAudio == false) {
            handler.post(runnable);
            isPlayAudio = true;
        }
    }

    /**
     * 停止播放
     *
     * @param path
     */
    public void stop(String path) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停播放
     */
    public void playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        path = "/storage/emulated/0/Android/data/com.example.vcserver.iqapture/" + System.currentTimeMillis() + filetype;
        try {
            Other.decoderBase64File(result, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initPlayer(path);
    }

    @OnClick({R.id.tv_PlayPause, R.id.tv_Stop, R.id.tv_Quit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_PlayPause:
                playAudio();
                break;
            case R.id.tv_Stop:
                tvMusicStatus.setText(getString(com.luck.picture.lib.R.string.picture_stop_audio));
                tvPlayPause.setText(getString(com.luck.picture.lib.R.string.picture_play_audio));
                stop(path);
                break;
            case R.id.tv_Quit:
                handler.removeCallbacks(runnable);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop(path);
                    }
                }, 30);
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && handler != null) {
            handler.removeCallbacks(runnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
        appManager.destory(this);
    }
}
