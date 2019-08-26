package com.example.vcserver.iqapture.bean;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by VCServer on 2018/11/26.
 */

public class iQapture{
    //IQDataset
    public static class IQDataset{
        // ID
        public int ID;
        // 名称
        public String Name;
        // 是否目录
        public boolean IsFolder;
        // base64图片清晰版
        public String Base64Icon;
        // 父目录编号
        public int ParentFolderID;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public boolean isFolder() {
            return IsFolder;
        }

        public void setFolder(boolean folder) {
            IsFolder = folder;
        }

        public String getBase64Icon() {
            return Base64Icon;
        }

        public void setBase64Icon(String base64Icon) {
            Base64Icon = base64Icon;
        }

        public int getParentFolderID() {
            return ParentFolderID;
        }

        public void setParentFolderID(int parentFolderID) {
            ParentFolderID = parentFolderID;
        }
    }

    //IQRecord
    public static class IQRecord{
        //Filled的ID
        public int ID;
        //DatasetID
        public int DatasetID;
        //就是你们自己搞的那个编号1,2,3显示在filled列表上的
        public int RowNo;
        //是否完成
        public boolean IsCompeleted;
        //创建人或者是最后更新的人？你们filled列表上显示的什么就给我们什么：例如John Smith
        public String Creator;
        //创建时间或者是最后更新时间？你们filled列表上显示的什么就给我们什么：dd/MM/yyyy HH:mm
        public String CreateTime;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getDatasetID() {
            return DatasetID;
        }

        public void setDatasetID(int datasetID) {
            DatasetID = datasetID;
        }

        public int getRowNo() {
            return RowNo;
        }

        public void setRowNo(int rowNo) {
            RowNo = rowNo;
        }

        public boolean isCompeleted() {
            return IsCompeleted;
        }

        public void setCompeleted(boolean compeleted) {
            IsCompeleted = compeleted;
        }

        public String getCreator() {
            return Creator;
        }

        public void setCreator(String creator) {
            Creator = creator;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String createTime) {
            CreateTime = createTime;
        }
    }

    //IQRecordDetail
    public static class IQRecordDetail{
        // IQ中的节，如果第一个问题并不是一个section类型，那么请给我们创建一个默认的节ID设置为0
        public List<Section> section;
        // 当前问题是否最后一页了，比如说一共又3页，现在返回的是第三页的问卷数据那么IsLastPage为true，否则为false
        public boolean IsLastPage;
        // 该Filled是否完成状态
        public boolean isCompleted;

        public List<Section> getSection() {
            return section;
        }

        public void setSection(List<Section> section) {
            this.section = section;
        }

        public boolean isLastPage() {
            return IsLastPage;
        }

        public void setLastPage(boolean lastPage) {
            IsLastPage = lastPage;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public void setCompleted(boolean completed) {
            isCompleted = completed;
        }
    }

    //Section
    public static class Section{
        // 问题编号
        public int ID;
        // 名称，这个名称如果有编号的话，编号一并写入名称中，例如：1. ISO 9001 General Requirements
        public String Name;
        // 级别
        public int level;
        // 当前节点下的问题列表
        public List<Question> Questions;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public List<Question> getQuestions() {
            return Questions;
        }

        public void setQuestions(List<Question> questions) {
            Questions = questions;
        }
    }

    //Question
    public static class Question{
        // 问题ID
        public int ID;
        // 问题编号 例如1.1
        public String No;
        // 问题
        public String Title;
        // 如果是数值类型的时候最大值
        public int Min;
        // 如果是数值类型的时候最小值
        public int Max;
        // 如果是滑块的时候步进值是多少
        public int Step;
        // 问题选项，例如多选单选类型
        public List<Option> Options;
        // 问题类型
        public int Type;
        // 答案，就是用户填写的值，用于编辑的时候需要把用户填写的值赋予这个属性
        public String Answer;
        // 时间正常值
        public String HelpAnswer;
        //提示信息
        private String Hint;
        // 是否必填
        public boolean Required;
        // 衍生问题
        public List<DeriveItem> DeriveItems;
        // 子问题
        public List<Question> Childrens;
        // Activity
        //public Activity Activity;
        private boolean isderivativeshow = false;//是否已经加载衍生问题，自己加的参数
        private boolean ischildshow = false;//是否已经加载子问题，自己加的参数
        private boolean ischecked = false;//是否选中item，自己加的参数
        private boolean iscontrolschecked = false;//是否选中item里面控件，自己加的参数
        private boolean isshow = false;//节点是否展开
        private boolean activityshow = false;//activity是否展开
        private int level;//等级

        public boolean isIsderivativeshow() {
            return isderivativeshow;
        }

        public void setIsderivativeshow(boolean isderivativeshow) {
            this.isderivativeshow = isderivativeshow;
        }

        public boolean isIschildshow() {
            return ischildshow;
        }

        public void setIschildshow(boolean ischildshow) {
            this.ischildshow = ischildshow;
        }

        public boolean isIscontrolschecked() {
            return iscontrolschecked;
        }

        public void setIscontrolschecked(boolean iscontrolschecked) {
            this.iscontrolschecked = iscontrolschecked;
        }

        public boolean isIsshow() {
            return isshow;
        }

        public void setIsshow(boolean isshow) {
            this.isshow = isshow;
        }

        public boolean isActivityshow() {
            return activityshow;
        }

        public void setActivityshow(boolean activityshow) {
            this.activityshow = activityshow;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public boolean isIschecked() {
            return ischecked;
        }

        public void setIschecked(boolean ischecked) {
            this.ischecked = ischecked;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getNo() {
            return No;
        }

        public void setNo(String no) {
            No = no;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public int getMin() {
            return Min;
        }

        public void setMin(int min) {
            Min = min;
        }

        public int getMax() {
            return Max;
        }

        public void setMax(int max) {
            Max = max;
        }

        public int getStep() {
            return Step;
        }

        public void setStep(int step) {
            Step = step;
        }

        public List<Option> getOptions() {
            return Options;
        }

        public void setOptions(List<Option> options) {
            Options = options;
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

        public String getHint() {
            return Hint;
        }

        public void setHint(String hint) {
            Hint = hint;
        }

        public boolean isRequired() {
            return Required;
        }

        public void setRequired(boolean required) {
            Required = required;
        }

        public List<DeriveItem> getDeriveItems() {
            return DeriveItems;
        }

        public void setDeriveItems(List<DeriveItem> deriveItems) {
            DeriveItems = deriveItems;
        }

        public List<Question> getChildrens() {
            return Childrens;
        }

        public void setChildrens(List<Question> childrens) {
            Childrens = childrens;
        }

//        public Activity getActivity() {
//            return Activity;
//        }
//
//        public void setActivity(Activity activity) {
//            Activity = activity;
//        }
    }

    //Option
    public static class Option{
        // 选项的ID
        public int OptionID;
        // 选项的名称
        public String OptionName;
        // 选项的Score
        public BigDecimal Score;
        // 选项的Score_Weight
        public BigDecimal Score_Weight;

        public int getOptionID() {
            return OptionID;
        }

        public void setOptionID(int optionID) {
            OptionID = optionID;
        }

        public String getOptionName() {
            return OptionName;
        }

        public void setOptionName(String optionName) {
            OptionName = optionName;
        }

        public BigDecimal getScore() {
            return Score;
        }

        public void setScore(BigDecimal score) {
            Score = score;
        }

        public BigDecimal getScore_Weight() {
            return Score_Weight;
        }

        public void setScore_Weight(BigDecimal score_Weight) {
            Score_Weight = score_Weight;
        }
    }

    //DeriveItem
    public static class DeriveItem{
        // 当单选的选项选了何值需要显示后续的问题
        public String Value;
        // 选择此项的后续问题列表
        public List<Question> Questions;

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            Value = value;
        }

        public List<Question> getQuestions() {
            return Questions;
        }

        public void setQuestions(List<Question> questions) {
            Questions = questions;
        }
    }

    //SubmitQuestion
    public static class SubmitQuestion{
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

        public List<QuestionValue> getValues() {
            return Values;
        }

        public void setValues(List<QuestionValue> values) {
            Values = values;
        }
    }

    //QuestionValue
    public static class QuestionValue{
        // 问题编号
        public int ID;
        // 问题类型，这里应该是一个枚举
        public int Type;
        // 答案
        public String Answer;
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

        public ActivityValue getActivity() {
            return Activity;
        }

        public void setActivity(ActivityValue activity) {
            Activity = activity;
        }
    }

    //ActivityValue
    public static class ActivityValue{
        public int ActivityID;
        public String TargetDate;
        public List<Integer> Peoples;
        public String Description;
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

        public List<Integer> getPeoples() {
            return Peoples;
        }

        public void setPeoples(List<Integer> peoples) {
            Peoples = peoples;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getComment() {
            return Comment;
        }

        public void setComment(String comment) {
            Comment = comment;
        }
    }

    public interface IIQaptrueForIOSAndriod {
        // 获取用户的Dataset列表，所有的，需要遍历所有子目录
        // <param name="companyId">公司编号</param>
        // <param name="userId">用户编号</param>
        List<IQDataset> GetDataset(int companyId, int userId);
        // 获取用户的的Dataset列表
        // <param name="companyId">公司编号</param>
        // <param name="userId">用户编号</param>
        // <param name="folderId">目录，为0的时候表示获取一级</param>
        List<IQDataset> GetDataset(int companyId, int userId, int folderId);
        // 获取Filled列表
        // <param name="companyId">公司编号</param>
        // <param name="userId">用户编号</param>
        // <param name="datasetId">dataset编号</param>
        List<IQRecord> GetRecored(int companyId, int userId, int datasetId);
        // 获取Question的问题列表
        // <param name="companyId">公司编号</param>
        // <param name="userId">用户编号</param>
        // <param name="datasetId">dataset编号</param>
        // <param name="recordId">filled编号，如果为0表示新建，那么Question的Answer请给空字符串""而不要让其为null，如果filled不为0，那么Question的Answer需要赋值为上次提交的内容</param>
        // <param name="page">数据页 page = 1</param>
        // <param name="row">一页加载多少行 row = 10</param>
        List<IQRecordDetail> GetQuestions(int companyId, int userId, int datasetId, int recordId, int page, int row);
        // 保存问卷信息
        boolean EditQuestion(SubmitQuestion question);
    }
}
