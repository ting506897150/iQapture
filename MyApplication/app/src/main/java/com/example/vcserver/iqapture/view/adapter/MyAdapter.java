package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.FileEntity;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;
import com.example.vcserver.iqapture.util.Other;

import java.io.File;
import java.util.List;

/**
 * Created by VCServer on 2018/5/24.
 */

public class MyAdapter extends CommonAdapter<FileEntity> {
    public MyAdapter(Context context, int layoutId, List<FileEntity> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, FileEntity fileEntity, int position) {
        if (fileEntity.getFileType() == FileEntity.Type.FLODER){
            holder.setImageResource(R.id.image,R.mipmap.file_picker_folder);
            holder.setText(R.id.text_name,fileEntity.getFileName());
        }else{
            if (fileEntity.getFilePath().contains(".PNG") || fileEntity.getFilePath().contains(".png")|| fileEntity.getFilePath().contains(".JPEG")|| fileEntity.getFilePath().contains(".jpeg")|| fileEntity.getFilePath().contains(".GIF")|| fileEntity.getFilePath().contains(".gif")|| fileEntity.getFilePath().contains(".JPG")|| fileEntity.getFilePath().contains(".jpg")|| fileEntity.getFilePath().contains(".webp")|| fileEntity.getFilePath().contains(".WEBP")|| fileEntity.getFilePath().contains(".bmp")){
                Glide.with(mContext).load(new File(fileEntity.getFilePath())).into((ImageView) holder.getView(R.id.image));
            }else if (fileEntity.getFilePath().contains(".txt")){
                holder.setImageResource(R.id.image,R.mipmap.file_picker_txt);
            }else if (fileEntity.getFilePath().contains(".pdf")){
                holder.setImageResource(R.id.image,R.mipmap.file_picker_pdf);
            }else if (fileEntity.getFilePath().contains(".ppt")){
                holder.setImageResource(R.id.image,R.mipmap.file_picker_ppt);
            }else if (fileEntity.getFilePath().contains(".word")||fileEntity.getFilePath().contains(".docx")){
                holder.setImageResource(R.id.image,R.mipmap.file_picker_word);
            }else if (fileEntity.getFilePath().contains(".excle")){
                holder.setImageResource(R.id.image,R.mipmap.file_picker_excle);
            }else if (fileEntity.getFilePath().contains(".mp4") || fileEntity.getFilePath().contains(".avi")|| fileEntity.getFilePath().contains(".3gpp")|| fileEntity.getFilePath().contains(".3gp")|| fileEntity.getFilePath().contains(".mov")){
                Glide.with(mContext).load(new File(fileEntity.getFilePath())).into((ImageView) holder.getView(R.id.image));
            }else if (fileEntity.getFilePath().contains(".mp3") || fileEntity.getFilePath().contains(".amr")|| fileEntity.getFilePath().contains(".aac")|| fileEntity.getFilePath().contains(".war")|| fileEntity.getFilePath().contains(".flac")|| fileEntity.getFilePath().contains(".lamr")){
                Glide.with(mContext).load(new File(fileEntity.getFilePath())).into((ImageView) holder.getView(R.id.image));
            }else {
                holder.setImageResource(R.id.image,R.mipmap.file_picker_def);
            }
            holder.setText(R.id.text_name,fileEntity.getFileName());
            if (fileEntity.isChecked()){
                holder.setViewVisiable(R.id.image_checked, View.VISIBLE);
                holder.setImageResource(R.id.image_checked,R.mipmap.file_choice);
            }else{
                holder.setViewVisiable(R.id.image_checked, View.GONE);
                holder.setImageResource(R.id.image_checked,R.mipmap.file_no_selection);
            }
        }
    }
}
