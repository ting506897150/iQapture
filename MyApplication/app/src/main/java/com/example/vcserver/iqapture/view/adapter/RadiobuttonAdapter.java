package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;
import com.example.vcserver.iqapture.util.Other;

import java.util.List;

/**
 * Created by VCServer on 2018/4/2.
 */

public class RadiobuttonAdapter extends CommonAdapter<Questionnaire.Options> {
    public RadiobuttonAdapter(Context context, int layoutId, List<Questionnaire.Options> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, Questionnaire.Options s, int position) {
        int strokebgColor = Color.parseColor("#FFFFFF");//默认背景颜色
        int strokeColor = Color.parseColor("#333333");//边框颜色
        int topLeftRadius,topRightRadius,bottomRightRadius,bottomLeftRadius;
        int left,top,right,bottom;
        GradientDrawable whiteDrawable = new GradientDrawable();//创建drawable
        GradientDrawable grayDrawable = new GradientDrawable();//创建drawable
        LayerDrawable layerDrawable;
        if (position == 0){
            topLeftRadius = Other.dip2px(mContext,10);
            topRightRadius = Other.dip2px(mContext,10);
            bottomRightRadius = Other.dip2px(mContext,1);
            bottomLeftRadius = Other.dip2px(mContext,1);

            left = Other.dip2px(mContext,1);
            top = Other.dip2px(mContext,1);
            right = Other.dip2px(mContext,1);
            bottom = Other.dip2px(mContext,0);
        }else if (position == mDatas.size() - 1){
            topLeftRadius = Other.dip2px(mContext,1);
            topRightRadius = Other.dip2px(mContext,1);
            bottomRightRadius = Other.dip2px(mContext,10);
            bottomLeftRadius = Other.dip2px(mContext,10);

            left = Other.dip2px(mContext,1);
            top = Other.dip2px(mContext,0);
            right = Other.dip2px(mContext,1);
            bottom = Other.dip2px(mContext,1);
        }else{
            topLeftRadius = Other.dip2px(mContext,1);
            topRightRadius = Other.dip2px(mContext,1);
            bottomRightRadius = Other.dip2px(mContext,1);
            bottomLeftRadius = Other.dip2px(mContext,1);

            left = Other.dip2px(mContext,1);
            top = Other.dip2px(mContext,0);
            right = Other.dip2px(mContext,1);
            bottom = Other.dip2px(mContext,0);
        }

        whiteDrawable.setColor(strokebgColor);
        whiteDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});

        grayDrawable.setColor(strokeColor);
        grayDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});

        layerDrawable = new LayerDrawable(new Drawable[]{grayDrawable,whiteDrawable});
        layerDrawable.setLayerInset(1,left,top,right,bottom);
        //设置drawable为背景
        ViewCompat.setBackground(holder.getView(R.id.text_name_top),layerDrawable);

        if (s.isChecked()){
            whiteDrawable.setColor(!TextUtils.isEmpty(s.getOptionColor())?Color.parseColor(s.getOptionColor()):Color.parseColor("#E2E5EC"));
            whiteDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});

            grayDrawable.setColor(strokeColor);
            grayDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});

            layerDrawable = new LayerDrawable(new Drawable[]{grayDrawable,whiteDrawable});
            layerDrawable.setLayerInset(1,left,top,right,bottom);
            //设置drawable为背景
            ViewCompat.setBackground(holder.getView(R.id.text_name_top),layerDrawable);
        }else{

            whiteDrawable.setColor(strokebgColor);
            whiteDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});

            grayDrawable.setColor(strokeColor);
            grayDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});

            layerDrawable = new LayerDrawable(new Drawable[]{grayDrawable,whiteDrawable});
            layerDrawable.setLayerInset(1,left,top,right,bottom);
            //设置drawable为背景
            ViewCompat.setBackground(holder.getView(R.id.text_name_top),layerDrawable);
        }
        holder.setText(R.id.text_name_top,s.getOptionName());
    }
}
