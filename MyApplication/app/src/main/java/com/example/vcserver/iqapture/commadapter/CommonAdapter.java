package com.example.vcserver.iqapture.commadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
	protected Context mContext;
	protected int mLayoutId;
	protected List<T> mDatas;

	public CommonAdapter(Context context, int layoutId, List<T> datas) {
		mContext = context;
		mLayoutId = layoutId;
		mDatas = datas;
	}

    @Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	@Override
	public int getItemCount() {
		return mDatas.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		convert(holder, mDatas.get(position),position);
		holder.setIsRecyclable(false);
		//条目点击事件
		if (mOnItemClickListener != null){
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mOnItemClickListener.onItemClick(view,position);
				}
			});
		}

		if (mOnItemLongClickListener != null){
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					mOnItemLongClickListener.onItemLongClick(view,position);
					return true;
				}
			});
		}

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		ViewHolder viewHolder = ViewHolder.get(mContext, arg0, mLayoutId);
		return viewHolder;
	}

	public abstract void convert(ViewHolder holder, T t,int position);

	public void setData(List<T> datas){
		this.mDatas=datas;
		this.notifyDataSetChanged();
	}

	//利用接口回调点击
	private OnItemClickListener mOnItemClickListener;


	public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
		this.mOnItemClickListener = mOnItemClickListener;
	}

	//利用接口回调点击（长按）
	private OnItemLongClickListener mOnItemLongClickListener;

	public void setmOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
		this.mOnItemLongClickListener = mOnItemLongClickListener;
	}
}
