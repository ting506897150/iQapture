package com.example.vcserver.iqapture.util;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.dataset.FilledResult;
import com.example.vcserver.iqapture.config.Preferences;
import com.google.gson.Gson;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by VCServer on 2018/11/20.
 */

public class RecordService extends Service {

    //SQLite数据库帮助类
    protected MyRecordDatabaseHelper myDatabaseHelper;
    protected SQLiteDatabase db;
    int userid;
    int companyid;
    int datasetId;

    FilledResult filledResult = new FilledResult();//record返回对象
    List<FilledResult.IQRecord> recordList = new ArrayList<>();//record数据集合
    FilledResult.IQRecord record;//record model类
    Questionnaires questionnaires = new Questionnaires();

    List<Questionnaire> loadmorequestionnaireList = new ArrayList<>();//每次加载的问卷
    List<Questionnaire> allquestionnaireList = new ArrayList<>();//获取的所有问卷（用来加载）
    List<Questionnaire> questionnaireList = new ArrayList<>();//获取的所有问卷（用来循环判断）
    int filledi;
    int filledTotal;
    int pagesize;
    Gson gson = new Gson();
    private int state = 0;
    ContentValues cv1;
    Questionnaire questionnaire;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myDatabaseHelper = new MyRecordDatabaseHelper(this);
        db = myDatabaseHelper.getWritableDatabase();
        //判断表是否为空，便于每次重新添加数据
        if (db.rawQuery("SELECT * FROM recordresult",null).getCount() > 0){
            myDatabaseHelper.Deleterecord(db);
        }
        userid = SharedPreferencesUtil.getsInstances(this).getInt(Preferences.USERID, 0);
        companyid = SharedPreferencesUtil.getsInstances(this).getInt(Preferences.COMPANYID, 0);
        filledi = 0;
        filledTotal = 0;
        pagesize = 1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            datasetId = intent.getIntExtra("datasetId", 0);
        } catch (Exception e) {
//            e.printStackTrace();
            Toast.makeText(this,"Toast提示消息",Toast.LENGTH_SHORT);
        }
        //网络请求
        if (state == 0){//判断service 在什么位置被重启  0 record
            filled(datasetId);//获取所有的filled 如果service重启  则调用重启之前的dataseti继续获取
        }else if (state == 1){//1 question
            pagesize = 1;
            question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);//获取所有的question 如果service重启  则调用重启之前的filledi继续获取
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void filled(int DatasetID) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        //构建FormBody，传入要提交的参数
        FormBody formBody = new FormBody
                .Builder()
                .add("companyId", String.valueOf(companyid))
                .add("userId", String.valueOf(userid))
                .add("datasetId", String.valueOf(DatasetID))
                .build();
        final Request request = new Request.Builder()
                .url(BaseApi.getBaseUrl()+"Intelligence/API/Capture/GetRecord")//http://test.valuechain.com/   http://192.168.1.35:8082/
                .post(formBody) .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                //失败
                Log.i("", "RecordService-filledonFailure: "+e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                filledResult = new FilledResult();
                filledResult = JSON.parseObject(responseStr, FilledResult.class);
                db = myDatabaseHelper.getWritableDatabase();
                if (filledResult.getRows() != null){
                    for (int i = 0; i < filledResult.getRows().size(); i++) {
                        cv1 = new ContentValues();
                        cv1.put("ID", filledResult.getRows().get(i).getID());
                        cv1.put("DatasetID", filledResult.getRows().get(i).getDatasetID());
                        cv1.put("RowNo", filledResult.getRows().get(i).getRowNo());
                        cv1.put("IsCompeleted", filledResult.getRows().get(i).isCompeleted());
                        cv1.put("Creator", filledResult.getRows().get(i).getCreator());
                        cv1.put("CreateTime", filledResult.getRows().get(i).getCreateTime());
                        db.insert("recordresult", null, cv1);
                    }
                }
                cv1 = null;
                //取出filled数据
                String sql = "SELECT * FROM recordresult";
                Cursor cursor = db.rawQuery(sql, null);
                recordList.clear();
                while(cursor.moveToNext()){
                    record = new FilledResult.IQRecord();
                    record.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                    record.setDatasetID(cursor.getInt(cursor.getColumnIndex("DatasetID")));
                    record.setRowNo(cursor.getInt(cursor.getColumnIndex("RowNo")));
                    record.setCompeleted(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("IsCompeleted"))));
                    record.setCreator(cursor.getString(cursor.getColumnIndex("Creator")));
                    record.setCreateTime(cursor.getString(cursor.getColumnIndex("CreateTime")));
                    recordList.add(record);
                    record = null;
                }

                cursor.close();
                filledTotal = recordList.size();
                Log.i("", "RecordService-onResponse1filledTotal: "+filledTotal);
                if (filledTotal > 0){
                    question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);//查询已有的record问题列表
                }
            }
        });
    }

    private void question(int datasetId, int recordId, final int page) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(2000, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        //构建FormBody，传入要提交的参数
        FormBody formBody = new FormBody
                .Builder()
                .add("companyId", String.valueOf(companyid))
                .add("userId", String.valueOf(userid))
                .add("datasetId", String.valueOf(datasetId))
                .add("recordId", String.valueOf(recordId))
                .add("page", String.valueOf(page))
                .build();
        final Request request = new Request.Builder()
                .url(BaseApi.getBaseUrl()+"Intelligence/api/Capture/GetQuestions")////http://test.valuechain.com/   http://192.168.1.35:8082/
                .post(formBody) .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                //失败
                Log.i("", "RecordService-questiondonFailure: "+e.getMessage());
                pagesize = 1;
                question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                questionnaires = JSON.parseObject(responseStr, Questionnaires.class);
                state = 1;
                if (questionnaires != null){
                    db = myDatabaseHelper.getWritableDatabase();
                    loadmorequestionnaireList.clear();
                    if (questionnaires.getSection() != null){
                        loadmorequestionnaireList.addAll(questionnaires.getSection());
                    }
                    //加载父节点
                    if (allquestionnaireList != null && allquestionnaireList.size() != 0) {//判断是不是第一次加载数据，如果不是则进入循环对比数据
                        for (int j = 0; j < loadmorequestionnaireList.size(); j++) {
                            if (allquestionnaireList.get(allquestionnaireList.size() - 1).getID() == loadmorequestionnaireList.get(j).getID()) {//判断新增数据是不是父节点下的数据，如果是就添加到父节点集合
                                if (questionnaireList.get(allquestionnaireList.size() - 1).getQuestions() != null){
                                    questionnaireList.get(allquestionnaireList.size() - 1).getQuestions().addAll(loadmorequestionnaireList.get(j).getQuestions());
                                }else{
                                    questionnaireList.get(allquestionnaireList.size() - 1).setQuestions(loadmorequestionnaireList.get(j).getQuestions());
                                }
                            } else {
                                questionnaire = new Questionnaire();
                                questionnaire.setID(loadmorequestionnaireList.get(j).getID());
                                questionnaire.setName(loadmorequestionnaireList.get(j).getName());
                                questionnaire.setLevel(loadmorequestionnaireList.get(j).getLevel());
                                questionnaire.setQuestions(loadmorequestionnaireList.get(j).getQuestions());
                                questionnaireList.add(questionnaire);
                                questionnaire = null;
                            }
                        }
                    } else {//如果是直接添加数据到集合
                        questionnaireList.addAll(loadmorequestionnaireList);
                    }
                    allquestionnaireList.clear();
                    allquestionnaireList.addAll(questionnaireList);

                    //判断是否加载完（因为要分页，所以判断是否到最后一页）
                    if (questionnaires.isLastPage() == false){//没到最后一页就继续加载
                        int pages = page + 1;
                        question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pages);
                    }else if (questionnaires.isLastPage() == true){//已到最后一页，filledi + 1 继续加载
                        cv1 = new ContentValues();
                        cv1.put("section", gson.toJson(allquestionnaireList));
                        cv1.put("IsLastPage", questionnaires.isLastPage());
                        cv1.put("isCompleted", questionnaires.isCompleted());
                        cv1.put("DatasetID", recordList.get(filledi).getDatasetID());
                        cv1.put("RecordId", recordList.get(filledi).getID());
                        db.insert("record_questionresult", null, cv1);
                        cv1 = null;
                        //当前问题列表加载完了，初始化存储集合
                        allquestionnaireList.clear();
                        questionnaireList = new ArrayList<>();
                        if (filledi >= (filledTotal - 1)){//filled所有项加载完
                            Log.i("", "RecordService-onResponse11: question关闭service ");
                            stopSelf();
                        }else{
                            Log.i("", "RecordService-onResponse11filledi: "+filledi);
                            pagesize = 1;
                            filledi = filledi + 1;
                            question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
                        }
                    }
                }else{
                    if (filledi >= (filledTotal - 1)){//filled所有项加载完
                        Log.i("", "RecordService-onResponse111: question为null关闭service ");
                        stopSelf();
                    }else{
                        Log.i("", "RecordService-onResponse22filledi: "+filledi);
                        filledi = filledi + 1;
                        pagesize = 1;
                        question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i("", "RecordService-onDestroy: 关闭了❎");
        db.close();
        stopSelf();
        super.onDestroy();
    }
}
