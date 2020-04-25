package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.graphics.Color;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.dataset.FilledResult;
import com.example.vcserver.iqapture.bean.iQapture;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;
import com.example.vcserver.iqapture.util.Other;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by VCServer on 2018/3/7.
 */

public class Item2Adapter extends CommonAdapter<FilledResult.IQRecord> {
    public Item2Adapter(Context context, int layoutId, List<FilledResult.IQRecord> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, FilledResult.IQRecord s, final int position) {
        if (s.isCompeleted()){
            holder.getView(R.id.list_item).setBackgroundColor(Color.parseColor("#e8e8e8"));
        }

        holder.setText(R.id.text_month, s.getCreateTime());
        holder.setText(R.id.text_name,s.getCreator());
        holder.setText(R.id.text_num,String.valueOf(s.getRowNo()));
    }
}
