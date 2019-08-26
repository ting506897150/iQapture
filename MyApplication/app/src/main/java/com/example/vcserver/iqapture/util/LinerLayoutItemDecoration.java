package com.example.vcserver.iqapture.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kang on 2017/9/26.  Recyclerview的LinerLayout分割线
 */

public class LinerLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    public LinerLayoutItemDecoration(Context context, int drawableResId) {
        mDivider = ContextCompat.getDrawable(context,drawableResId);
    }
    /*
    * 基本操作  留出分割线位置
    * */

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position != 0){
            outRect.top = mDivider.getIntrinsicHeight();
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        int childCount = parent.getChildCount();

        //制定绘制区域
        Rect rect = new Rect();
        rect.left = parent.getPaddingLeft();
        rect.right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 1;i < childCount;i++){
            //分割线的底部是ItemView的头部
            rect.bottom = parent.getChildAt(i).getTop();
            rect.top = rect.bottom - mDivider.getIntrinsicHeight();

            mDivider.setBounds(rect);
            mDivider.draw(canvas);
        }
    }
}
