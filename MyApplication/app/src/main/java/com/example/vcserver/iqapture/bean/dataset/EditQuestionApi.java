package com.example.vcserver.iqapture.bean.dataset;

import com.example.vcserver.iqapture.bean.SubmitQuestion;
import com.example.vcserver.iqapture.bean.iQapture;
import com.example.vcserver.iqapture.config.HttpPostService;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by Kang on 2018/4/8.
 */

public class EditQuestionApi extends BaseApi {
    private String question;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public EditQuestionApi(){
        setMothed("Intelligence/api/capture/EditQuestion");
    }
    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService service = retrofit.create(HttpPostService.class);
        return service.editquestion(getQuestion());
    }
}
