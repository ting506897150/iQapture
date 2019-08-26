package com.example.vcserver.iqapture.bean.dataset;

import com.example.vcserver.iqapture.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by VCServer on 2018/3/7.
 */

public class QuestionnaireApi extends BaseApi {
    private int userId;
    private int companyId;
    private int datasetId;
    private int recordId;
    private int page;
    private int row;

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(int datasetId) {
        this.datasetId = datasetId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public QuestionnaireApi(){
        setMothed("Intelligence/api/Capture/GetQuestions");
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService service = retrofit.create(HttpPostService.class);
        return service.question(getUserId(),getCompanyId(),getDatasetId(),getRecordId(),getPage(),getRow());
    }
}
