package com.example.vcserver.iqapture.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by VCServer on 2018/4/25.
 */

public class SubmitQuestion{
    // 公司编号
    public int CompanyID;
    // 用户编号
    public int UserID;
    // 是完成还是一般保存
    public boolean IsCompleted;
    // DataSet ID
    public int DatasetID;
    // Filled如果为0表示新建，否则表示编辑
    public int RecordID;
    //当前dataset所在文件夹编号
    public int FolderID;
    public List<QuestionValue> Values;

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int companyID) {
        CompanyID = companyID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public boolean isCompleted() {
        return IsCompleted;
    }

    public void setCompleted(boolean completed) {
        IsCompleted = completed;
    }

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

    public int getFolderID() {
        return FolderID;
    }

    public void setFolderID(int folderID) {
        FolderID = folderID;
    }

    public List<QuestionValue> getValues() {
        return Values;
    }

    public void setValues(List<QuestionValue> values) {
        Values = values;
    }

    public static class QuestionValue{
        // 问题编号
        public int ID;
        // 问题类型，这里应该是一个枚举
        public int Type;
        // 答案
        public String Answer;
        // 时间正常值
        public String HelpAnswer;
        public List<ExtendValue> ExtendVals;
        public ActivityValue Activity;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getType() {
            return Type;
        }

        public void setType(int type) {
            Type = type;
        }

        public String getAnswer() {
            return Answer;
        }

        public void setAnswer(String answer) {
            Answer = answer;
        }

        public String getHelpAnswer() {
            return HelpAnswer;
        }

        public void setHelpAnswer(String helpAnswer) {
            HelpAnswer = helpAnswer;
        }

        public List<ExtendValue> getExtendVals() {
            return ExtendVals;
        }

        public void setExtendVals(List<ExtendValue> extendVals) {
            ExtendVals = extendVals;
        }

        public ActivityValue getActivity() {
            return Activity;
        }

        public void setActivity(ActivityValue activity) {
            Activity = activity;
        }
    }

    public static class ExtendValue {
        public String Value;
        public String HelpValue;

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            Value = value;
        }

        public String getHelpValue() {
            return HelpValue;
        }

        public void setHelpValue(String helpValue) {
            HelpValue = helpValue;
        }
    }

    public static class ActivityValue implements Serializable {
        public int ActivityID;
        public String TargetDate;
        public String UserIds;
        public String Description;
        public int CommentID;
        public String Comment;

        public int getActivityID() {
            return ActivityID;
        }

        public void setActivityID(int activityID) {
            ActivityID = activityID;
        }

        public String getTargetDate() {
            return TargetDate;
        }

        public void setTargetDate(String targetDate) {
            TargetDate = targetDate;
        }

        public String getUserIds() {
            return UserIds;
        }

        public void setUserIds(String userIds) {
            UserIds = userIds;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public int getCommentID() {
            return CommentID;
        }

        public void setCommentID(int commentID) {
            CommentID = commentID;
        }

        public String getComment() {
            return Comment;
        }

        public void setComment(String comment) {
            Comment = comment;
        }
    }
}
