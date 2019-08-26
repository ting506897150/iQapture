package com.example.vcserver.iqapture.view.other.view;

import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionResult;
import com.example.vcserver.iqapture.bean.dataset.FilledResult;
import com.example.vcserver.iqapture.bean.iQapture;
import com.example.vcserver.iqapture.view.base.IBaseView;

import java.util.List;

/**
 * Created by Kang on 2017/7/12.
 */

public interface IDatasetView extends IBaseView {
    void getDataset(DatasetResult result);
    void getFilled(FilledResult result);
    void getQuestionmodel(Questionnaires result);
    void getEditQuestion(EditQuestionResult result);
}
