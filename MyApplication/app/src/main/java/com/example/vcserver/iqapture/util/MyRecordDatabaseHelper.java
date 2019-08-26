package com.example.vcserver.iqapture.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by VCServer on 2018/11/19.
 */

public class MyRecordDatabaseHelper extends SQLiteOpenHelper{
    public static String DATABASE_NAME = "iqapture_record.db";//数据库名字
    public static int DATABASE_VERSION = 1;//数据库版本号

    public MyRecordDatabaseHelper(Context context){
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public MyRecordDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //单独存储的record和问题列表
        db.execSQL("CREATE TABLE IF NOT EXISTS recordresult("
                + "ID integer unique primary key,"
                + "DatasetID integer,"
                + "RowNo integer,"
                + "IsCompeleted varchar,"
                + "Creator varchar,"
                + "CreateTime varchar )"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS record_questionresult("
                + "section varchar,"
                + "IsLastPage varchar,"
                + "isCompleted varchar,"
                + "DatasetID integer,"
                + "RecordId integer)"
        );
        //当前record的问题列表格式（无数据）
        db.execSQL("CREATE TABLE IF NOT EXISTS record_questionmodelresult("
                + "section varchar,"
                + "IsLastPage varchar,"
                + "isCompleted varchar,"
                + "DatasetID integer,"
                + "RecordId integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void Deleterecord(SQLiteDatabase db){
        db.execSQL("Delete from recordresult");
        db.execSQL("Delete from record_questionresult");
    }

    public void Deleterecord_questionmodel(SQLiteDatabase db){
        db.execSQL("Delete from record_questionmodelresult");
    }
}
