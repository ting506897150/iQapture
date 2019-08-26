package com.example.vcserver.iqapture.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by VCServer on 2018/3/12.
 * 其它一些需要的工具
 */

public class Other {
    //将 W12/D3/Y2015转换成正常时间 DD/MM/YYYY
    //W:第几个周 D:第几天(周一到周日，1-7) Y:年份
    public static String Timetransformation(int week,int day,int year){
        int month = 0;//当前月份
        int days = 0;//当前天数
        int dayss = ((week - 1) * 7) + day;//当前日期是一年的第多少天
        String time = "";
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        if (dayss > 31){
            month = 1;
            dayss = dayss - 31;
            if (flag == true){//闰年
                if (dayss > 29){
                    month = 2;
                    dayss = dayss - 29;
                }else{
                    month = 2;
                    days = dayss;
                }
            }else{//非闰年
                if (dayss > 28){
                    month = 2;
                    dayss = dayss - 28;
                    if (dayss > 31){
                        month = 3;
                        dayss = dayss - 31;
                        if (dayss > 30){
                            month = 4;
                            dayss = dayss - 30;
                            if (dayss > 31){
                                month = 5;
                                dayss = dayss - 31;
                                if (dayss > 30){
                                    month = 6;
                                    dayss = dayss - 30;
                                    if (dayss > 31){
                                        month = 7;
                                        dayss = dayss - 31;
                                        if (dayss > 31){
                                            month = 8;
                                            dayss = dayss - 31;
                                            if (dayss > 30){
                                                month = 9;
                                                dayss = dayss - 30;
                                                if (dayss > 31){
                                                    month = 10;
                                                    dayss = dayss - 31;
                                                    if (dayss > 30){
                                                        month = 11;
                                                        dayss = dayss - 30;
                                                        if (dayss > 31){
                                                            month = 12;
                                                            dayss = dayss - 31;
                                                        }else{
                                                            month = 12;
                                                            days = dayss;
                                                        }
                                                    }else{
                                                        month = 11;
                                                        days = dayss;
                                                    }
                                                }else{
                                                    month = 10;
                                                    days = dayss;
                                                }
                                            }else{
                                                month = 9;
                                                days = dayss;
                                            }
                                        }else{
                                            month = 8;
                                            days = dayss;
                                        }
                                    }else{
                                        month = 7;
                                        days = dayss;
                                    }
                                }else{
                                    month = 6;
                                    days = dayss;
                                }
                            }else{
                                month = 5;
                                days = dayss;
                            }
                        }else{
                            month = 4;
                            days = dayss;
                        }
                    }else{
                        month = 3;
                        days = dayss;
                    }
                }else{
                    month = 2;
                    days = dayss;
                }
            }
        }else{
            month = 1;
            days = dayss;
        }

        time = days +"/"+ month + "/" + year;
        return time;
    }

    /**
     * 将base64字符串转换成bitmap图片
     * @param string base64字符串
     * @return bitmap
     */
    public static Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    /**
     * 将bitmap图片转换成base64字符串
     * @param  bitmap bitmap图片
     * @return string
     */
    public static String bitmaptoString(Bitmap bitmap){
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }

    /** * 根据手机分辨率从 dp 单位 转成 px(像素) */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /** * 将文件转成base64 字符串 * @param path 文件路径 * @return * @throws Exception */
    public static String encodeBase64File(String path) throws Exception {
        String string = null;
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        string = Base64.encodeToString(buffer,Base64.DEFAULT);
        return string;
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件). <br/>
     * @author guhaizhou@126.com
     * @param base64Code 编码后的字串
     * @param savePath  文件保存路径
     * @throws Exception
     * @since JDK 1.6
     */
    public static void decoderBase64File(String base64Code,String savePath) throws Exception {
        byte[] buffer =Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

    /**
     *
     创建目录文件
     */
    public static void createPath(String path) {
        File file = new File(path);
        //判断文件是否存在
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     *
     删除文件夹所有内容
     *
     */
    public static void deleteFile(File file) {
        if (file.exists()) {//判断文件是否存在
            if (file.isFile()) {//判断是否是文件
                file.delete();//delete()方法 你应该知道 是删除的意思;
            }else if (file.isDirectory()) {//否则如果它是一个目录
                File files[] = file.listFiles(); //声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { //遍历目录下所有的文件
                    deleteFile(files[i]);//把每个文件 用这个方法进行迭代
                }
            }
//            file.delete();//删除文件夹的
        }
        if(file.isFile()){
            file.delete();
            return;
        }
    }

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    //向指定的文件中写入指定的数据
    public static void writeFileData(Context mContext,String filename, String content){
        try {
            FileOutputStream fos = mContext.openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream
            //将要写入的字符串转换为byte数组
            byte[]  bytes = content.getBytes();
            fos.write(bytes);//将byte数组写入文件
            fos.close();//关闭文件输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打开指定文件，读取其数据，返回字符串对象
    public static String readFileData(Context mContext,String fileName){
        String result="";
        try{
            FileInputStream fis = mContext.openFileInput(fileName);
            //获取文件长度
            int lenght = fis.available();
            byte[] buffer = new byte[lenght];
            fis.read(buffer);
            //将byte数组转换成指定格式的字符串
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

    /**
     * 判断时间格式
     *
     * @param date 时间 输入的字符串
     * @param format 时间
     * @return boolean 判断值 true，false
     */
    public static boolean isValidDate(String date,String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date d = null;
        try{
            d = df.parse(date);
        }catch(Exception e){
            //如果不能转换,肯定是错误格式
            return false;
        }
        String s1 = df.format(d);
        // 转换后的日期再转换回String,如果不等,逻辑错误.如format为"yyyy-MM-dd",date为"2006-02-31",转换为日期后再转换回字符串为"2006-03-03",说明格式虽然对,但日期逻辑上不对.
        return date.equals(s1);
    }

    /**
     * 时间转换为时间戳
     *
     * @param timeStr 时间 例如: 2016-03-09 输入的时间格式要和SimpleDateFormat格式一样
     * @return String 时间戳 例如:1402733340
     */
    public static long getTimeStamp(String timeStr,String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);//"yyyy/MM/dd"
        Date date;
        long timeStamp;
        try {
            date = simpleDateFormat.parse(timeStr);
            timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** 时间戳转换时间（格式不一样）
     * @param time 例如:1402733340
     * @return dd/MM/yyyy SimpleDateFormat是想转换的格式
     */
    public static String timedate(long time,String format) {
        SimpleDateFormat sdr = new SimpleDateFormat(format);//"dd/MM/yyyy"
        String times = sdr.format(new Date(time));
        return times;
    }


    //1、Video 对于视频，取第一帧作为缩略图，也就是如何从filePath获得一个Bitmap对象。
    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch(IllegalArgumentException ex){
            ex.printStackTrace();
        } catch(RuntimeException ex){
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch(RuntimeException ex){
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    //打开文件
    public static void openFile(Context context, String filePath){
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, "com.example.vcserver.iqapture.provider", new File(filePath));
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //获取文件file的MIME类型
        String type = getMIMEType(filePath);
        //设置intent的data和Type属性。
        intent.setDataAndType(uri, type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "找不到打开此文件的应用！", Toast.LENGTH_SHORT).show();
        }
    }

    /***根据文件后缀回去MIME类型****/
    private static String getMIMEType(String file) {
        String type="*/*";
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = file.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名*/
        String end = file.substring(dotIndex,file.length()).toLowerCase();
        if(end == "")
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i < MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private static final String[][] MIME_MapTable = {
            // {后缀名，MIME类型}
            { ".3gp", "video/3gpp" },
            { ".apk", "application/vnd.android.package-archive" },
            { ".asf", "video/x-ms-asf" },
            { ".avi", "video/x-msvideo" },
            { ".bin", "application/octet-stream" },
            { ".bmp", "image/bmp" },
            { ".c", "text/plain" },
            { ".class", "application/octet-stream" },
            { ".conf", "text/plain" },
            { ".cpp", "text/plain" },
            { ".doc", "application/msword" },
            { ".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
            { ".xls", "application/vnd.ms-excel" },
            { ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
            { ".exe", "application/octet-stream" },
            { ".gif", "image/gif" },
            { ".gtar", "application/x-gtar" },
            { ".gz", "application/x-gzip" },
            { ".h", "text/plain" },
            { ".htm", "text/html" },
            { ".html", "text/html" },
            { ".jar", "application/java-archive" },
            { ".java", "text/plain" },
            { ".jpeg", "image/jpeg" },
            { ".jpg", "image/jpeg" },
            { ".js", "application/x-javascript" },
            { ".log", "text/plain" },
            { ".m3u", "audio/x-mpegurl" },
            { ".m4a", "audio/mp4a-latm" },
            { ".m4b", "audio/mp4a-latm" },
            { ".m4p", "audio/mp4a-latm" },
            { ".m4u", "video/vnd.mpegurl" },
            { ".m4v", "video/x-m4v" },
            { ".mov", "video/quicktime" },
            { ".mp2", "audio/x-mpeg" },
            { ".mp3", "audio/x-mpeg" },
            { ".mp4", "video/mp4" },
            { ".mpc", "application/vnd.mpohun.certificate" },
            { ".mpe", "video/mpeg" },
            { ".mpeg", "video/mpeg" },
            { ".mpg", "video/mpeg" },
            { ".mpg4", "video/mp4" },
            { ".mpga", "audio/mpeg" },
            { ".msg", "application/vnd.ms-outlook" },
            { ".ogg", "audio/ogg" },
            { ".pdf", "application/pdf" },
            { ".png", "image/png" },
            { ".pps", "application/vnd.ms-powerpoint" },
            { ".ppt", "application/vnd.ms-powerpoint" },
            { ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
            { ".prop", "text/plain" },
            { ".rc", "text/plain" },
            { ".rmvb", "audio/x-pn-realaudio" },
            { ".rtf", "application/rtf" },
            { ".sh", "text/plain" },
            { ".tar", "application/x-tar" },
            { ".tgz", "application/x-compressed" },
            { ".txt", "text/plain" },
            { ".wav", "audio/x-wav" },
            { ".wma", "audio/x-ms-wma" },
            { ".wmv", "audio/x-ms-wmv" },
            { ".wps", "application/vnd.ms-works" },
            { ".xml", "text/plain" },
            { ".z", "application/x-compress" },
            { ".zip", "application/x-zip-compressed" },
            { "", "*/*" }
    };

    //根据图片路径获取图片格式
    public static String getFileName(String pathandname){
        int start = pathandname.lastIndexOf(".");
        if(start!=-1){
            return pathandname.substring(start,pathandname.length());
        }else{
            return null;
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片 或者SDCard中的图片
     * @param imagePath 图片在SDCard中的路径搜索
     * @return
     */
    public static Bitmap getSDCardImg(String imagePath)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        return BitmapFactory.decodeFile(imagePath, opt);
    }

    //根据图片路径获取图片名+格式
    public static String getImageFileName(String pathandname){
        int start = pathandname.lastIndexOf("/");
        if(start!=-1){
            return pathandname.substring(start+1,pathandname.length());
        }else{
            return null;
        }
    }

    /**
     * 判断是否为平板
     *
     * @return
     */
    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.0) {
            return true;
        }
        return false;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}
