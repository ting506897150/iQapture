package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.iQapture;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;
import com.example.vcserver.iqapture.util.Other;

import java.util.List;

/**
 * Created by VCServer on 2018/3/7.
 */

public class ItemAdapter extends CommonAdapter<DatasetResult.IQDataset> {
    public ItemAdapter(Context context, int layoutId, List<DatasetResult.IQDataset> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, DatasetResult.IQDataset s, final int position) {
        if (!TextUtils.isEmpty(s.getBase64Icon())){
            holder.setImageBitmap(R.id.image,Other.stringtoBitmap(s.getBase64Icon()));

            if (s.isFolder()){
                holder.setViewBackgroundColor(R.id.rlayout_text, Color.parseColor("#5EA8F8"));
            }else{
                holder.setViewBackgroundColor(R.id.rlayout_text, Color.parseColor("#F38302"));
            }
        }else{
            if (s.isFolder()){
                holder.setImageResource(R.id.image,R.mipmap.datasetimg);
                holder.setViewBackgroundColor(R.id.rlayout_text, Color.parseColor("#5EA8F8"));
            }else{
                holder.setImageResource(R.id.image,R.mipmap.iqapture);
                holder.setViewBackgroundColor(R.id.rlayout_text, Color.parseColor("#F38302"));
            }
        }

        if (s.isOffline()){
            holder.setVisible(R.id.text_offline,true);
        }else{
            holder.setVisible(R.id.text_offline,false);
        }
        if (s.isOfflinequestion()){
            holder.setVisible(R.id.text_not,true);
        }else{
            holder.setVisible(R.id.text_not,false);
        }
        holder.setText(R.id.txetx_name,s.getName());
    }
}
