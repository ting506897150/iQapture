package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.IQOptionDetail;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;

import java.util.List;

/**
 * Created by VCServer on 2018/12/27.
 */

public class SpinnerAdapter extends CommonAdapter<Questionnaire.Options> {
    public SpinnerAdapter(Context context, int layoutId, List<Questionnaire.Options> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, Questionnaire.Options options, int position) {
        holder.setText(R.id.TextView,options.getOptionName());
    }
}
