package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Address;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;

import java.util.List;

/**
 * Created by VCServer on 2018/4/2.
 */

public class PeopleAdapter extends CommonAdapter<Questionnaire.Peoples> {
    public PeopleAdapter(Context context, int layoutId, List<Questionnaire.Peoples> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, Questionnaire.Peoples s, int position) {
        if (s.getIsSelected() == 1){
            holder.setCChecked(R.id.checkbox,true);
        }else{
            holder.setCChecked(R.id.checkbox,false);
        }
        holder.setText(R.id.text_name,s.getUserName());
    }
}
