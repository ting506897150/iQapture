package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.login.LoginResult;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;

import java.util.List;

/**
 * Created by VCServer on 2018/3/7.
 */

public class MenuAdapter extends CommonAdapter<LoginResult.VCCompany> {
    public MenuAdapter(Context context, int layoutId, List<LoginResult.VCCompany> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, LoginResult.VCCompany menu, int position) {
        if (menu.getIsCurrentCompany() == 1){
            holder.setVisible(R.id.img, true);
        }else{
            holder.setVisible(R.id.img,false);
        }
        holder.setText(R.id.CompanyName,menu.getCompanyName());
    }
}
