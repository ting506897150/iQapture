package com.example.vcserver.iqapture.bean.dataset;

import com.example.vcserver.iqapture.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by VCServer on 2018/3/7.
 */

public class DatasetApi extends BaseApi {
    private int companyId;
    private int userId;
    private int folderId;

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

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public DatasetApi(){
        setMothed("Intelligence/API/Dataset/GetDataset");
    }

    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService service = retrofit.create(HttpPostService.class);
        return service.dataset(getCompanyId(),getUserId(),getFolderId());
    }
}
