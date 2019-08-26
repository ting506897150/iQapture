package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
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
        }else{
            if (s.isFolder()){
                holder.setImageResource(R.id.image,R.mipmap.datasetimg);
            }else{
                holder.setImageResource(R.id.image,R.mipmap.camera);
            }
        }
        holder.setText(R.id.txetx_name,s.getName());
    }
}
