package com.example.vcserver.iqapture.view.other;

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

import java.util.List;

/**
 * Created by VCServer on 2018/5/30.
 */

public class PlusFileActivity extends BaseActivity<QuestionPresenter> implements IQuestionView {
    String path;
    ImageShowApi imageShowApi;
    String filetype;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_plus_file);
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
            Other.openFile(this,path);
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
        Other.openFile(this,path);
    }
}
