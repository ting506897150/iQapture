package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.OnItemClickListener;
import com.example.vcserver.iqapture.commadapter.ViewHolder;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.util.FullyGridLayoutManager;
import com.example.vcserver.iqapture.util.LinerLayoutItemDecoration;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.util.adapter.NumericWheelAdapter;
import com.example.vcserver.iqapture.util.widget.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VCServer on 2018/3/7.
 */

public class TableAdapter extends CommonAdapter<List<Questionnaire.Question>> {
    TableitemAdapter tableitemAdapter;
    public TableAdapter(Context context, int layoutId, List<List<Questionnaire.Question>> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, List<Questionnaire.Question> questions, final int position) {
        RecyclerView item_recyclerview = holder.getView(R.id.item_recyclerview);
        //设置适配器
        tableitemAdapter = new TableitemAdapter(mContext,R.layout.item_item_table,questions);
        item_recyclerview.setLayoutManager(new FullyGridLayoutManager(mContext,2));
        item_recyclerview.setAdapter(tableitemAdapter);
        tableitemAdapter.setOnGridItemClickListener(new TableitemAdapter.OnGridItemClickListener() {
            @Override
            public void onGridItemClick(int pos) {
                onGridItemClickListener.onGridItemClick(position,pos,mDatas.get(position).get(pos).getAnswer());
            }
        });
    }

    //利用接口回调点击
    private OnGridItemClickListener onGridItemClickListener;//点击
    private OnGridLongItemClickListener onGridItemLongClickListener;//点击


    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }

    public void setOnGridItemLongClickListener(OnGridLongItemClickListener onGridItemLongClickListener) {
        this.onGridItemLongClickListener = onGridItemLongClickListener;
    }


    public interface OnGridItemClickListener {
        void onGridItemClick(int pos1,int pos2,String xxx);
    }

    public interface OnGridLongItemClickListener {
        void onGridLongItemClick(int pos1);
    }
}
