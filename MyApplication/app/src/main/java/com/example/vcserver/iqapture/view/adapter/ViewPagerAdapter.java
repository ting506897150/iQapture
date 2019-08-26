package com.example.vcserver.iqapture.view.adapter;

import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.ScaleImageView;
import com.example.vcserver.iqapture.view.other.PlusImageActivity;
import com.luck.picture.lib.entity.LocalMedia;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import java.io.IOException;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片浏览的适配器
 * <p>
 * 作者： 周旭 on 2017年7月30日 0030.
 * 邮箱：374952705@qq.com
 * 博客：http://www.jianshu.com/u/56db5d78044d
 */

public class ViewPagerAdapter extends PagerAdapter {

    private static RequestManager manager;
    private List<LocalMedia> selectList; //图片的数据源

    private PlusImageActivity plusImageActivity;

    public ViewPagerAdapter(PlusImageActivity context, List<LocalMedia> selectList) {
        this.plusImageActivity = context;
        this.selectList = selectList;
        manager = Glide.with(context);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return selectList.size();
    }

    //判断当前的View 和 我们想要的Object(值为View) 是否一样;返回 true/false
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    //instantiateItem()：将当前view添加到ViewGroup中，并返回当前View
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = getItemView(R.layout.view_pager_img);
        ScaleImageView imageView = itemView.findViewById(R.id.img_iv);

        LocalMedia media = selectList.get(position);
        String path = media.getPath();

        if (path.contains("@")){
            int end = path.indexOf("@");
            String filetype = Other.getFileName(path);
            asyncImageShow(imageView, path.substring(0,end),filetype);
        }else{
            RequestOptions options = new RequestOptions()
//                    .centerInside()
                    .placeholder(R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(plusImageActivity)
                    .load(path)
                    .apply(options)
                    .into(imageView);
        }
        container.addView(itemView);
        return itemView;
    }

    private View getItemView(int layoutId) {
        View itemView = LayoutInflater.from(plusImageActivity).inflate(layoutId, null, false);
        return itemView;
    }

    //destroyItem()：删除当前的View;
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void asyncImageShow(ScaleImageView imageView, String path,String filetype){
        ImageShow imageShow = new ImageShow(imageView,filetype);
        imageShow.execute(BaseApi.getBaseUrl()+"OpenBook/IOSAPI/GetImg?ImgID="+path);
    }

    private class ImageShow extends AsyncTask<String,String,String> {
        private ScaleImageView imageView;
        String filetype;
        public ImageShow(ScaleImageView imageView,String filetype) {
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
                String path = "/storage/emulated/0/Android/data/com.example.vcserver.iqapture/"+System.currentTimeMillis() + filetype;
                try {
                    Other.decoderBase64File(s,path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestOptions options = new RequestOptions()
//                        .centerInside()
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
