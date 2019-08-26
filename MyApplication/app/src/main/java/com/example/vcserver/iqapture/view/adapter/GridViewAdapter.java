package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.other.QuestionnaireActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 添加上传图片适配器
 * <p>
 * 作者： 周旭 on 2017/6/21/0021.
 * 邮箱：374952705@qq.com
 * 博客：http://www.jianshu.com/u/56db5d78044d
 */

public class GridViewAdapter extends android.widget.BaseAdapter {

    QuestionnaireActivity questionnaireActivity;
    private static RequestManager manager;
    private List<LocalMedia> selectList = new ArrayList<>();
    private LayoutInflater inflater;
    private int maxSelectNum = 9;

    ImageShow imageShow;
    public GridViewAdapter(QuestionnaireActivity questionnaireActivity, List<LocalMedia> selectList) {
        this.questionnaireActivity = questionnaireActivity;
        this.selectList = selectList;
        inflater = LayoutInflater.from(questionnaireActivity);
        manager = Glide.with(questionnaireActivity);
    }

    @Override
    public int getCount() {
        //return mList.size() + 1;//因为最后多了一个添加图片的ImageView 
        int count = selectList == null ? 1 : selectList.size() + 1;
        if (count > maxSelectNum) {
            return selectList.size();
        } else {
            return count;
        }
    }

    @Override
    public Object getItem(int position) {
        return selectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_filter_image, parent,false);
        ImageView mImg = convertView.findViewById(R.id.fiv);
        LinearLayout ll_del = convertView.findViewById(R.id.ll_del);
        TextView tv_duration = convertView.findViewById(R.id.tv_duration);
        if (position < selectList.size()) {
            //代表+号之前的需要正常显示图片
            ll_del.setVisibility(View.VISIBLE);

            LocalMedia media = selectList.get(position);
            int mimeType = media.getMimeType();
            int pictureType = PictureMimeType.isPictureType(media.getPictureType());
            tv_duration.setVisibility(pictureType == PictureConfig.TYPE_VIDEO
                    ? View.VISIBLE : View.GONE);
            String path = media.getPath();
            if (mimeType == PictureMimeType.ofAudio()) {
                tv_duration.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(questionnaireActivity, R.drawable.picture_audio);
                StringUtils.modifyTextViewDrawable(tv_duration, drawable, 0);
            } else {
                Drawable drawable = ContextCompat.getDrawable(questionnaireActivity, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(tv_duration, drawable, 0);
            }
            if (mimeType == PictureMimeType.ofAudio()) {
                mImg.setImageResource(R.drawable.audio_placeholder);
            }else if (mimeType == PictureMimeType.ofImage()||mimeType == PictureMimeType.ofVideo()){
                if (path.contains("@")){
                    int end = path.indexOf("@");
                    String filetype = Other.getFileName(path);
                    asyncImageShow(mImg, path.substring(0,end),filetype);
                }else{
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    manager
                            .load(path)
                            .apply(options)
                            .into(mImg);
                }
            }else if (mimeType == 4){
                String filetype = Other.getFileName(path);
                if (filetype.contains(".txt"))
                    mImg.setImageResource(R.mipmap.file_picker_txt);
                else if (filetype.contains(".pdf"))
                    mImg.setImageResource(R.mipmap.file_picker_pdf);
                else if (filetype.contains(".ppt"))
                    mImg.setImageResource(R.mipmap.file_picker_ppt);
                else if (filetype.contains(".word")||filetype.contains(".docx"))
                    mImg.setImageResource(R.mipmap.file_picker_word);
                else if (filetype.contains(".excle"))
                    mImg.setImageResource(R.mipmap.file_picker_excle);
                else
                    mImg.setImageResource(R.mipmap.file_picker_def);
            }
        } else {
            mImg.setImageResource(R.mipmap.add_img);//最后一个显示加号图片
        }
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGridItemClickListener.onGridItemClick(position,getCount(),false);
            }
        });
        ll_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferencesUtil.getsInstances(questionnaireActivity).getBoolean(Preferences.ISCOMPLETED,false) == false){
                    onGridItemClickListener.onGridItemClick(position,getCount(),true);
                }
            }
        });
        return convertView;
    }

    //利用接口回调点击
    private OnGridItemClickListener onGridItemClickListener;//点击


    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }


    public interface OnGridItemClickListener {
        void onGridItemClick(int position,int count,boolean isdelete);
    }

    private void asyncImageShow(ImageView imageView, String path, String filetype){
        imageShow = new ImageShow(imageView,filetype);
        imageShow.execute(BaseApi.getBaseUrl()+"OpenBook/IOSAPI/GetImg?ImgID="+path);
    }

    public static class ImageShow extends AsyncTask<String,String,String> {
        private ImageView imageView;
        String filetype;
        public ImageShow(ImageView imageView, String filetype) {
            this.imageView = imageView;
            this.filetype = filetype;
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s)){
                String path = null;
                    path = "/storage/emulated/0/Android/data/com.example.vcserver.iqapture/"+System.currentTimeMillis() + filetype;
                try {
                    Other.decoderBase64File(s,path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                if (Util.isOnMainThread()){
                    manager
                            .load(path)
                            .apply(options)
                            .into(imageView);
                }
            }
        }
    }

}  
