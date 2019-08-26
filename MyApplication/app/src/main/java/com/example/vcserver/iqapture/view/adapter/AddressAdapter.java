package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.graphics.Color;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Address;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;

import java.util.List;

/**
 * Created by VCServer on 2018/3/7.
 */

public class AddressAdapter extends CommonAdapter<Address> {
    public AddressAdapter(Context context, int layoutId, List<Address> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, Address address, int position) {
        if (address.isChecked()){
            holder.setViewBackgroundColor(R.id.rlayout, Color.parseColor("#999999"));
        }else{
            holder.setViewBackgroundColor(R.id.rlayout,Color.parseColor("#FFFFFF"));
        }
        holder.setText(R.id.serveraddress,address.getName());
    }
}
