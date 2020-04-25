package com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api;

import retrofit2.Retrofit;
import rx.Observable;
import rx.functions.Func1;

/**
 * 请求数据统一封装类
 * Created by WZG on 2016/7/16.
 */
public abstract class BaseApi<T> implements Func1<T, String> {
    /*是否能取消加载框*/
    private boolean cancel=false;
    /*是否显示加载框*/
    private boolean showProgress=true;
    /*基础url*/
    public static String baseUrl="http://test.valuechain.com/";//http://local.valuechain.com/  http://192.168.1.101:6689/  http://test.valuechain.com/
    public static String loginUrl="cip/register/LoginByMobile";//AnalytiQs/Interface/SignIn   api/account/SingIn
    /*是否需要缓存处理*/
    private boolean cache=false;
    /*方法-如果需要缓存必须设置这个参数；不需要不用設置*/
    private String mothed;
    /*超时时间-默认6秒*/
    private int connectionTime = 60;
    /*有网情况下的本地缓存时间默认60秒*/
    private int cookieNetWorkTime=60;
    /*无网络的情况下本地缓存时间默认30天*/
    private int cookieNoNetWorkTime=24*60*60*30;

    /**
     * 设置参数
     *
     * @param retrofit
     * @return
     */
    public abstract Observable getObservable(Retrofit retrofit);



    public int getCookieNoNetWorkTime() {
        return cookieNoNetWorkTime;
    }

    public void setCookieNoNetWorkTime(int cookieNoNetWorkTime) {
        this.cookieNoNetWorkTime = cookieNoNetWorkTime;
    }

    public int getCookieNetWorkTime() {
        return cookieNetWorkTime;
    }

    public void setCookieNetWorkTime(int cookieNetWorkTime) {
        this.cookieNetWorkTime = cookieNetWorkTime;
    }

    public String getMothed() {
        return mothed;
    }

    public int getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(int connectionTime) {
        this.connectionTime = connectionTime;
    }

    public void setMothed(String mothed) {
        this.mothed = mothed;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        BaseApi.baseUrl = baseUrl;
    }

    public String getUrl() {
        return baseUrl+mothed;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public boolean isCancel() {
         return cancel;
     }

     public void setCancel(boolean cancel) {
         this.cancel = cancel;
     }

    @Override
    public String call(T httpResult) {
//        BaseResultEntity baseResulte= JSONObject.parseObject(httpResult.toString(),BaseResultEntity.class);
//        if (baseResulte.getRet() == 0) {
//            throw new HttpTimeException(baseResulte.getMsg());
//        }
        return httpResult.toString();
    }
}
