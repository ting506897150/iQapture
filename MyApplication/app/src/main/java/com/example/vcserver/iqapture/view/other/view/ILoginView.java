package com.example.vcserver.iqapture.view.other.view;

import com.example.vcserver.iqapture.bean.login.LoginResult;
import com.example.vcserver.iqapture.view.base.IBaseView;

/**
 * Created by Kang on 2017/7/12.
 */

public interface ILoginView extends IBaseView {
    void getLogin(LoginResult result);
}
