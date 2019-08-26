package com.example.vcserver.iqapture.bean.dataset;

import com.example.vcserver.iqapture.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Kang on 2018/4/8.
 */

public class ImageAddApi extends BaseApi {
    private int UserID;
    private String Image;
    private String FileName;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public ImageAddApi(){
        setMothed("OpenBook/IOSAPI/AddImg");
    }
    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService service = retrofit.create(HttpPostService.class);
        return service.imageadd(getUserID(),getImage(),getFileName());
    }
}
