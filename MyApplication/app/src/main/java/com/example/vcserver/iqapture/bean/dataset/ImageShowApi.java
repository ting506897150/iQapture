package com.example.vcserver.iqapture.bean.dataset;

import com.example.vcserver.iqapture.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Kang on 2018/4/8.
 */

public class ImageShowApi extends BaseApi {
    private String ImgID;

    public String getImgID() {
        return ImgID;
    }

    public void setImgID(String imgID) {
        ImgID = imgID;
    }

    public ImageShowApi(){
        setMothed("OpenBook/IOSAPI/GetImg");
    }
    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService service = retrofit.create(HttpPostService.class);
        return service.imageshow(getImgID());
    }
}
