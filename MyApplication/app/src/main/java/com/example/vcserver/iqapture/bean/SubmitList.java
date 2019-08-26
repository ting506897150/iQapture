package com.example.vcserver.iqapture.bean;

/**
 * Created by VCServer on 2019/2/26.
 */

public class SubmitList {
    // DataSet ID
    public int DatasetID;
    // Filled如果为0表示新建，否则表示编辑
    public int RecordID;
    public int ParentFolderID;
    public boolean isCompleted;
    public String questionjson;

    public int getDatasetID() {
        return DatasetID;
    }

    public void setDatasetID(int datasetID) {
        DatasetID = datasetID;
    }

    public int getRecordID() {
        return RecordID;
    }

    public void setRecordID(int recordID) {
        RecordID = recordID;
    }

    public int getParentFolderID() {
        return ParentFolderID;
    }

    public void setParentFolderID(int parentFolderID) {
        ParentFolderID = parentFolderID;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getQuestionjson() {
        return questionjson;
    }

    public void setQuestionjson(String questionjson) {
        this.questionjson = questionjson;
    }
}
