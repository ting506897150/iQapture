package com.example.vcserver.iqapture.presenter.other;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionResult;
import com.example.vcserver.iqapture.bean.dataset.FilledResult;
import com.example.vcserver.iqapture.bean.iQapture;
import com.example.vcserver.iqapture.config.BaseModel;
import com.example.vcserver.iqapture.config.BaseModelImp;
import com.example.vcserver.iqapture.presenter.base.BasePresenter;
import com.example.vcserver.iqapture.presenter.base.PVBaseListener;
import com.example.vcserver.iqapture.view.other.view.IDatasetView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.exception.ApiException;

import java.util.List;

/**
 * Created by Kang on 2017/7/12.
 */

public class DatasetPresenter extends BasePresenter implements PVBaseListener {
    private BaseModel model;
    private IDatasetView view;

    public DatasetPresenter(Context mContext, IDatasetView view) {
        super(mContext);
        this.view = view;
        model = new BaseModelImp(this);
    }

    @Override
    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi) {
        model.startPost(rxAppCompatActivity, baseApi);
    }

    @Override
    public void startPost(RxAppCompatActivity rxAppCompatActivity, BaseApi baseApi, int state) {

    }

    @Override
    public void onNext(String resulte, String mothead) {
        if (mothead.equals("Intelligence/API/Dataset/GetDataset")){
            DatasetResult result = JSON.parseObject(resulte, DatasetResult.class);
            view.getDataset(result);
        }else if (mothead.equals("Intelligence/API/Capture/GetRecord")){
            FilledResult result = JSON.parseObject(resulte, FilledResult.class);
            view.getFilled(result);
        }else if (mothead.equals("Intelligence/api/capture/EditQuestion")){
            EditQuestionResult result = JSON.parseObject(resulte, EditQuestionResult.class);
            view.getEditQuestion(result);
        }else if (mothead.equals("Intelligence/api/Capture/GetQuestions")){
            Questionnaires result = JSON.parseObject(resulte, Questionnaires.class);
            view.getQuestionmodel(result);
        }
    }

    @Override
    public void onError(ApiException e) {
        view.closeLoadingDialog();
        view.showTip(e.getDisplayMessage());
    }
}
