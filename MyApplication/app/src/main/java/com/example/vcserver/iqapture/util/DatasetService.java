package com.example.vcserver.iqapture.util;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

public class DatasetService extends Service {

    //SQLite数据库帮助类
    protected MyDatabaseHelper myDatabaseHelper;
    protected MyRecordDatabaseHelper myRecordDatabaseHelper;
    protected SQLiteDatabase db;
    int userid = 0;
    int companyid = 0;

    List<DatasetResult.IQDataset> datasetResult = new ArrayList<>();
    DatasetResult.IQDataset dataset;//dataset model类
    FilledResult filledResult = new FilledResult();
    List<FilledResult.IQRecord> recordList = new ArrayList<>();
    FilledResult.IQRecord record;//record model类
    Questionnaires questionnaires = new Questionnaires();

    List<Questionnaire> loadmorequestionnaireList = new ArrayList<>();//每次加载的问卷
    List<Questionnaire> allquestionnaireList = new ArrayList<>();//获取的所有问卷（用来加载）
    List<Questionnaire> questionnaireList = new ArrayList<>();//获取的所有问卷（用来循环判断）
    int dataseti = 0;
    int datasetTotal = 0;
    int filledi = 0;
    int filledTotal = 0;
    int pagesize = 1;
    Gson gson = new Gson();
    private int state = 0;
    ContentValues cv1;
    Questionnaire questionnaire;

    private boolean data;
    public static final String COUNTER = "data";
    public static final String ACTION_NAME = "com.example.vcserver.iqapture.COUNTER_ACTION";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myDatabaseHelper = new MyDatabaseHelper(this);
        db = myDatabaseHelper.getWritableDatabase();
        //判断表是否为空，便于每次重新添加数据
        if (db.rawQuery("SELECT * FROM datasetresult",null).getCount() > 0){
            myDatabaseHelper.Deletedataset(db);
        }
        userid = SharedPreferencesUtil.getsInstances(this).getInt(Preferences.USERID, 0);
        companyid = SharedPreferencesUtil.getsInstances(this).getInt(Preferences.COMPANYID, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //从Activity获取data
        data = intent.getBooleanExtra(COUNTER, false);
        //网络请求
//        if (state == 0){//判断service 在什么位置被重启  0 dataset
//            dataset();//获取所有的dataset
//        }else if (state == 1){//1 filled
////            filled(datasetResult.get(dataseti).getID());//获取所有的filled 如果service重启  则调用重启之前的dataseti继续获取
//        }else if (state == 2){//2 question
////            pagesize = 1;
////            question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);//获取所有的question 如果service重启  则调用重启之前的filledi继续获取
//        }else{//2 questionmodel
////            pagesize = 1;
////            questionmodel(recordList.get(filledi).getDatasetID(),pagesize);//获取所有的question 如果service重启  则调用重启之前的filledi继续获取
//        }


        //开启一个线程，对数据进行处理
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (state == 0){//判断service 在什么位置被重启  0 dataset
                        dataset();//获取所有的dataset
                    }
                    Thread.sleep(3000);
                    //耗时操作：数据处理并保存，向Activity发送广播

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void dataset() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        //构建FormBody，传入要提交的参数
        FormBody formBody = new FormBody
                .Builder()
                .add("companyId", String.valueOf(companyid))
                .add("userId", String.valueOf(userid))
                .build();
        final Request request = new Request.Builder()
                .url(BaseApi.getBaseUrl()+"Intelligence/API/Dataset/GetAllDatasets")
                .post(formBody) .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                //失败
                Log.i("", "dataset onFailure: "+e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                datasetResult = JSON.parseArray(responseStr, DatasetResult.IQDataset.class);
                try{
                    if (datasetResult.size() > 0){
                        for (int i = 0; i < datasetResult.size(); i++) {
                            cv1 = new ContentValues();
                            cv1.put("ID", datasetResult.get(i).getID());
                            cv1.put("Name", datasetResult.get(i).getName());
                            cv1.put("Base64Icon", datasetResult.get(i).getBase64Icon() == null?"":datasetResult.get(i).getBase64Icon());
                            cv1.put("isFolder", datasetResult.get(i).isFolder());
                            cv1.put("ParentFolderID", datasetResult.get(i).getParentFolderID());
                            db.replace("datasetresult", null, cv1);
                            cv1 = null;
                        }
                        for (int i = 0; i < datasetResult.size(); i++) {
                            if (datasetResult.get(i).isFolder() == true){
                                if (datasetResult.get(i).getChildrens() != null && datasetResult.get(i).getChildrens().size() > 0){
                                    for (int j = 0; j < datasetResult.get(i).getChildrens().size(); j++) {
                                        cv1 = new ContentValues();
                                        cv1.put("ID", datasetResult.get(i).getChildrens().get(j).getID());
                                        cv1.put("Name", datasetResult.get(i).getChildrens().get(j).getName());
                                        cv1.put("Base64Icon", datasetResult.get(i).getChildrens().get(j).getBase64Icon() == null?"":datasetResult.get(i).getChildrens().get(j).getBase64Icon());
                                        cv1.put("isFolder", datasetResult.get(i).getChildrens().get(j).isFolder());
                                        cv1.put("ParentFolderID", datasetResult.get(i).getChildrens().get(j).getParentFolderID());
                                        db.replace("datasetresult", null, cv1);
                                        cv1 = null;
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < datasetResult.size(); i++) {
                            if (datasetResult.get(i).isFolder() == true){
                                if (datasetResult.get(i).getChildrens() != null && datasetResult.get(i).getChildrens().size() > 0){
                                    for (int j = 0; j < datasetResult.get(i).getChildrens().size(); j++) {
                                        if (datasetResult.get(i).getChildrens().get(j).isFolder() == true){
                                            if (datasetResult.get(i).getChildrens().get(j).getChildrens() != null && datasetResult.get(i).getChildrens().get(j).getChildrens().size() > 0){
                                                for (int k = 0; k < datasetResult.get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                    cv1 = new ContentValues();
                                                    cv1.put("ID", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getID());
                                                    cv1.put("Name", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getName());
                                                    cv1.put("Base64Icon", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getBase64Icon() == null?"":datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getBase64Icon());
                                                    cv1.put("isFolder", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).isFolder());
                                                    cv1.put("ParentFolderID", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getParentFolderID());
                                                    db.replace("datasetresult", null, cv1);
                                                    cv1 = null;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < datasetResult.size(); i++) {
                            if (datasetResult.get(i).isFolder() == true){
                                if (datasetResult.get(i).getChildrens() != null && datasetResult.get(i).getChildrens().size() > 0){
                                    for (int j = 0; j < datasetResult.get(i).getChildrens().size(); j++) {
                                        if (datasetResult.get(i).getChildrens().get(j).isFolder() == true){
                                            if (datasetResult.get(i).getChildrens().get(j).getChildrens() != null && datasetResult.get(i).getChildrens().get(j).getChildrens().size() > 0){
                                                for (int k = 0; k < datasetResult.get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                    if (datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).isFolder() == true){
                                                        if (datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens() != null && datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size() > 0){
                                                            for (int l = 0; l < datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size(); l++) {
                                                                cv1 = new ContentValues();
                                                                cv1.put("ID", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getID());
                                                                cv1.put("Name", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getName());
                                                                cv1.put("Base64Icon", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getBase64Icon() == null?"":datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getBase64Icon());
                                                                cv1.put("isFolder", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).isFolder());
                                                                cv1.put("ParentFolderID", datasetResult.get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getParentFolderID());
                                                                db.replace("datasetresult", null, cv1);
                                                                cv1 = null;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //取出dataset数据
                    String sql = "SELECT * FROM datasetresult";
                    Cursor cursor = db.rawQuery(sql, null);
                    datasetResult.clear();
                    while(cursor.moveToNext()){
                        dataset = new DatasetResult.IQDataset();
                        dataset.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                        dataset.setName(cursor.getString(cursor.getColumnIndex("Name")));
                        dataset.setBase64Icon(cursor.getString(cursor.getColumnIndex("Base64Icon")));
                        dataset.setFolder(cursor.getString(cursor.getColumnIndex("isFolder")).equals("1") ? true : false);
                        dataset.setParentFolderID(cursor.getInt(cursor.getColumnIndex("ParentFolderID")));
                        datasetResult.add(dataset);
                        dataset = null;
                    }

                    cursor.close();
                    db.close();
                    datasetTotal = datasetResult.size();
                    Log.i("", "onResponse datasetTotal: "+datasetTotal);
                    data = true;
                    final Intent mIntent = new Intent();
                    mIntent.setAction(ACTION_NAME);
                    mIntent.putExtra(COUNTER, data);
                    sendBroadcast(mIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("", "onResponse:  DatasetService出错了");
                    data = false;
                    final Intent mIntent = new Intent();
                    mIntent.setAction(ACTION_NAME);
                    mIntent.putExtra(COUNTER, data);
                    sendBroadcast(mIntent);
                }

//                if (datasetTotal > 0){
//                    filled(datasetResult.get(dataseti).getID());
//                }
            }
        });
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
                Log.i("", "onResponse filled onFailure: "+e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                filledResult = JSON.parseObject(responseStr, FilledResult.class);
                state = 1;
                db = myDatabaseHelper.getWritableDatabase();
                if (filledResult.getRows() != null){   //filled不为空
                    for (int i = 0; i < filledResult.getRows().size(); i++) {
                        cv1 = new ContentValues();
                        cv1.put("ID", filledResult.getRows().get(i).getID());
                        cv1.put("DatasetID", filledResult.getRows().get(0).getDatasetID());
                        cv1.put("RowNo", filledResult.getRows().get(i).getRowNo());
                        cv1.put("IsCompeleted", filledResult.getRows().get(i).isCompeleted());
                        cv1.put("Creator", filledResult.getRows().get(i).getCreator());
                        cv1.put("CreateTime", filledResult.getRows().get(i).getCreateTime());
                        db.insert("filledresult", null, cv1);
                    }
                    db.close();
                    cv1 = null;

                    if (dataseti >= (datasetTotal - 1)){//判断dataset是否加载完毕
                        //取出filled数据
                        String sql = "SELECT * FROM filledresult";
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
                        db.close();
                        filledTotal = recordList.size();
                        Log.i("", "onResponse filledTotal: "+filledTotal);
                        if (filledTotal > 0){
                            question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
                        }
                    }else{
                        dataseti = dataseti + 1;
                        filled(datasetResult.get(dataseti).getID());
                    }

                }else{  //防止中间有为空的filled 直接停止,加载剩下的filled
                    if (dataseti >= (datasetTotal - 1)){
                        //取出filled数据
                        String sql = "SELECT * FROM filledresult";
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
                        db.close();
                        filledTotal = recordList.size();
                        Log.i("", "onResponse filled为null filledTotal: "+filledTotal);
                        if (filledTotal > 0){
                            question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
                        }
                    }else{
                        dataseti = dataseti + 1;
                        filled(datasetResult.get(dataseti).getID());
                    }
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
                Log.i("", "onResponse question onFailure: "+e.getMessage());
                pagesize = 1;
                question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                questionnaires = JSON.parseObject(responseStr, Questionnaires.class);
                state = 2;
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
                    }else if (questionnaires.isLastPage() == true){   //已到最后一页，filledi + 1 继续加载
                        cv1 = new ContentValues();
                        cv1.put("section", gson.toJson(allquestionnaireList));
                        cv1.put("IsLastPage", questionnaires.isLastPage());
                        cv1.put("isCompleted", questionnaires.isCompleted());
                        cv1.put("DatasetID", recordList.get(filledi).getDatasetID());
                        cv1.put("RecordId", recordList.get(filledi).getID());
                        db.insert("questionresult", null, cv1);
                        db.close();
                        cv1 = null;
                        //当前问题列表加载完了，初始化存储集合
                        allquestionnaireList.clear();
                        questionnaireList = new ArrayList<>();
                        if (filledi >= (filledTotal - 1)){//filled所有项加载完，切换到下一个dataset的filled继续加载
                            Log.i("", "onResponse question加载完毕 加载questionmodel");
                            stopSelf();
//                            db = myRecordDatabaseHelper.getWritableDatabase();
//                            //判断表是否为空，便于每次重新添加数据
//                            if (db.rawQuery("SELECT * FROM record_questionmodelresult",null).getCount() > 0){
//                                myRecordDatabaseHelper.Deleterecord_questionmodel(db);
//                            }
//                            pagesize = 1;
//                            filledi = 0;
//                            questionmodel(recordList.get(filledi).getDatasetID(),pagesize);//加载question模板
                        }else{
                            Log.i("", "onResponse 加载下一个filled filledi: "+filledi);
                            pagesize = 1;
                            filledi = filledi + 1;
                            question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
                        }
                    }
                }else{
                    if (filledi >= (filledTotal - 1)){//filled所有项加载完，切换到下一个dataset的filled继续加载
                        Log.i("", "onResponse11: question为null加载questionmodel");
                        stopSelf();
//                        pagesize = 1;
//                        questionmodel(recordList.get(0).getDatasetID(),pagesize);//加载question模板
                    }else{
                        Log.i("", "onResponse question为null 加载下一个filled filledi: "+filledi);
                        filledi = filledi + 1;
                        pagesize = 1;
                        question(recordList.get(filledi).getDatasetID(),recordList.get(filledi).getID(),pagesize);
                    }
                }
            }
        });
    }

    private void questionmodel(int datasetId, final int page) {
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
                .add("recordId", String.valueOf(0))
                .add("page", String.valueOf(page))
                .build();
        final Request request = new Request.Builder()
                .url(BaseApi.getBaseUrl()+"Intelligence/api/Capture/GetQuestions")////http://test.valuechain.com/   http://192.168.1.35:8082/
                .post(formBody) .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                //失败
                Log.i("", "onResponse questionmodel onFailure: "+e.getMessage());
                pagesize = 1;
                questionmodel(recordList.get(filledi).getDatasetID(),pagesize);
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                questionnaires = JSON.parseObject(responseStr, Questionnaires.class);
                state = 3;
                if (questionnaires != null){
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
                        questionmodel(recordList.get(filledi).getDatasetID(),pages);
                    }else if (questionnaires.isLastPage() == true){   //已到最后一页，filledi + 1 继续加载
                        db = myRecordDatabaseHelper.getWritableDatabase();
                        cv1 = new ContentValues();
                        cv1.put("section", gson.toJson(allquestionnaireList));
                        cv1.put("IsLastPage", questionnaires.isLastPage());
                        cv1.put("isCompleted", questionnaires.isCompleted());
                        cv1.put("DatasetID", recordList.get(filledi).getDatasetID());
                        cv1.put("RecordId", 0);
                        db.insert("record_questionmodelresult", null, cv1);
                        db.close();
                        cv1 = null;
                        //当前问题列表加载完了，初始化存储集合
                        allquestionnaireList.clear();
                        questionnaireList = new ArrayList<>();

                        if (filledi >= (filledTotal - 1)){//filled所有项加载完，切换到下一个dataset的filled继续加载
                            Log.i("", "onResponse questionmodel关闭service ");
                            stopSelf();
                        }else{
                            Log.i("", "onResponse questionmodel加载下一个dataset的filled");
                            filledi = filledi + 1;
                            for (int i = filledi; i < recordList.size(); i++) {
                                if (recordList.get(filledi).getDatasetID() != recordList.get(filledi - 1).getDatasetID()){
                                    questionmodel(recordList.get(filledi).getDatasetID(),pagesize);//加载question模板
                                }
                            }
                        }

                    }
                }else{
                    if (filledi >= (filledTotal - 1)){//filled所有项加载完，切换到下一个dataset的filled继续加载
                        Log.i("", "onResponse222: questionmodel为null关闭service ");
                        stopSelf();
                    }else{
                        Log.i("", "onResponse222: questionmodel加载下一个dataset的filled");
                        filledi = filledi + 1;
                        for (int i = filledi; i < recordList.size(); i++) {
                            if (recordList.get(filledi).getDatasetID() != recordList.get(filledi - 1).getDatasetID()){
                                questionmodel(recordList.get(filledi).getDatasetID(),pagesize);//加载question模板
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i("", "onDestroy: 关闭了❎");
        stopSelf();
        super.onDestroy();
    }
}
