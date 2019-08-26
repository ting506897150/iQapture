package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;

import java.util.List;

/**
 * Created by VCServer on 2018/4/2.
 */

public class CheckBoxAdapter extends CommonAdapter<Questionnaire.Options> {
    public CheckBoxAdapter(Context context, int layoutId, List<Questionnaire.Options> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, Questionnaire.Options s, int position) {
        if (s.isChecked()){
            holder.setCChecked(R.id.checkbox,true);
            if (!TextUtils.isEmpty(s.getOptionColor())){
                holder.setBGChecked(R.id.checkbox,Color.parseColor(s.getOptionColor()));
            }
        }else{
            holder.setCChecked(R.id.checkbox,false);
        }
        holder.setText(R.id.text_name,s.getOptionName());
    }
}
