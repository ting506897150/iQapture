package com.example.vcserver.iqapture.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by VCServer on 2018/11/19.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper{
    public static String DATABASE_NAME = "iqapture.db";//数据库名字
    public static int DATABASE_VERSION = 1;//数据库版本号

    public MyDatabaseHelper(Context context){
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS login("
                + "username varchar,"
                + "password varchar )"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS loginresult("
                + "UserID integer primary key,"
                + "Username varchar,"
                + "DefaultCompany integer,"
                + "MyCompanies varchar )"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS datasetresult("
                + "ID integer primary key unique,"
                + "Name varchar,"
                + "Base64Icon varchar,"
                + "isFolder varchar,"
                + "ParentFolderID integer )"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS filledresult("
                + "ID integer,"
                + "DatasetID integer,"
                + "RowNo integer,"
                + "IsCompeleted varchar,"
                + "Creator varchar,"
                + "CreateTime varchar )"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS questionresult("
                + "section varchar,"
                + "IsLastPage varchar,"
                + "isCompleted varchar,"
                + "DatasetID integer,"
                + "RecordId integer)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS questionmodelresult("
                + "section varchar,"
                + "IsLastPage varchar,"
                + "isCompleted varchar,"
                + "DatasetID integer primary key unique,"
                + "RecordId integer)"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS submitquestion("
                + "questionjson varchar,"
                + "DatasetID integer,"
                + "RecordId integer,"
                + "Creator varchar,"
                + "isCompleted varchar,"
                + "ParentFolderID integer,"
                + "CreateTime varchar)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void Delete(SQLiteDatabase db){
        db.execSQL("Delete from login");
        db.execSQL("Delete from loginresult");
    }

    public void Deletedataset(SQLiteDatabase db){
        db.execSQL("Delete from datasetresult");
        db.execSQL("Delete from filledresult");
        db.execSQL("Delete from questionresult");
    }

    public void Deletesubmitquestion(SQLiteDatabase db){
        db.execSQL("Delete from submitquestion");
    }
}
