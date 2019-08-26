package com.example.vcserver.iqapture.bean.dataset;

import com.example.vcserver.iqapture.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Kang on 2018/4/8.
 */

public class FilledApi extends BaseApi {
    private int companyId;
    private int userId;
    private int datasetId;

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

    public FilledApi(){
        setMothed("Intelligence/API/Capture/GetRecord");
    }
    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService service = retrofit.create(HttpPostService.class);
        return service.filled(getCompanyId(),getUserId(),getDatasetId());
    }
}
