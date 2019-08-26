package com.example.vcserver.iqapture.view.other.view;

import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.dataset.FilledResult;
import com.example.vcserver.iqapture.view.base.IBaseView;

import java.util.List;

/**
 * Created by Kang on 2017/7/12.
 */

public interface IFilledView extends IBaseView {
    void getFilled(FilledResult result);
}
