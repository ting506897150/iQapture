package com.example.vcserver.iqapture.commadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Kun on 2016/12/14.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description:通用ViewHolder
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;
    private View convertView;
    private Context context;

    public ViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        convertView = itemView;
        views = new SparseArray<>();
    }

    public static ViewHolder createViewHolder(Context context, View itemView)
    {
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    public static ViewHolder get(Context context, ViewGroup viewGroup, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        return new ViewHolder(context, itemView);
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public ViewHolder setloadUrl(int viewId, String url) {
        WebView webView = getView(viewId);
        webView.loadUrl(url);
        return this;
    }

    public ViewHolder setTextColor(int viewId, int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap resId) {
        ImageView view = getView(viewId);
        view.setImageBitmap(resId);
        return this;
    }


    public ViewHolder setViewVisiable(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    public ViewHolder setViewBackgroundResource(int viewId, int resId) {
        getView(viewId).setBackgroundResource(resId);
        return this;
    }

    public ViewHolder setViewBackgroundColor(int viewId, int color) {
        getView(viewId).setBackgroundColor(color);
        return this;
    }

    public ViewHolder setOnclickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible)
    {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public ViewHolder setRChecked(int viewId, boolean checked)
    {
        RadioButton view = getView(viewId);
        view.setChecked(checked);
        return this;
    }
    public ViewHolder setCChecked(int viewId, boolean checked)
    {
        CheckBox view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    @SuppressLint("NewApi")
    public ViewHolder setBGChecked(int viewId, int color)
    {
        CheckBox view = getView(viewId);
        view.setButtonTintList(ColorStateList.valueOf(color));
        return this;
    }
}
