package com.example.vcserver.iqapture.view.other.view;

import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionResult;
import com.example.vcserver.iqapture.bean.iQapture;
import com.example.vcserver.iqapture.view.base.IBaseView;

import java.util.List;

/**
 * Created by Kang on 2017/7/12.
 */

public interface IQuestionView extends IBaseView {
    void getQuestion(Questionnaires result);
    void getEditQuestion(EditQuestionResult result);
    void getImageAdd(String result);
    void getImageShow(String result);
}
