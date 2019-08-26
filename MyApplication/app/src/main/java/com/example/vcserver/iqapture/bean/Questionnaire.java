package com.example.vcserver.iqapture.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by VCServer on 2018/3/28.
 * 问卷返回问题实体类
 */

public class Questionnaire{
    // 主键
    private int ID;
    // 名称，这个名称如果有编号的话，编号一并写入名称中，例如：1. ISO 9001 General Requirements
    private String Name;
    // 级别
    private int level;
    // 当前节点下的问题列表
    private List<Question> Questions;

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

    public static class Question{
        // 问题ID
        public int ID;
        // 问题编号 例如1.1
        public String No;
        // 问题
        public String Title;
        // 如果是数值类型的时候最小值
        public BigDecimal Min;
        // 如果是数值类型的时候最大值
        public BigDecimal Max;
        // 如果是滑块的时候步进值是多少
        public BigDecimal Step;
        // 问题选项，例如多选单选类型
        public List<Options> Options;
        // 问题类型
        public int Type;
        // 答案，就是用户填写的值，用于编辑的时候需要把用户填写的值赋予这个属性
        public String Answer;
        // 时间正常值
        public String HelpAnswer;
        //提示信息
        private String Hint;
        //判断是否是搜索下拉框
        private boolean OnlineSearch;
        // 是否必填
        public boolean Required;
        // Required=true &&OnlyCompleted==true则表示该问题是只有完成时才必填，Required=true &&OnlyCompleted==false 点击保存按钮和完成按钮都必填
        public boolean OnlyCompleted;
        // (checkbox)多选字段的答案限制选择数量的最小值
        public int AnswersLimitMin;
        // (checkbox)多选字段的答案限制选择数量的最大值
        public int AnswersLimitMax;
        // 如果为true，说明该question有一个跟随字段（跟随字段放在Childrens）
        public boolean IsComment;
        // 是否扩展第二个答案 true:是,false:否
        public boolean IsExtendanswer;
        /// 当前字段的扩展值
        /// </summary>
        public List<String> ExtendVals;
        // 衍生问题
        public List<DeriveItem> DeriveItems;
        // 子问题
        public List<Question> Childrens;
        // Activity
        public ActivityValue Activity;
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

        public BigDecimal getMin() {
            return Min;
        }

        public void setMin(BigDecimal min) {
            Min = min;
        }

        public BigDecimal getMax() {
            return Max;
        }

        public void setMax(BigDecimal max) {
            Max = max;
        }

        public BigDecimal getStep() {
            return Step;
        }

        public void setStep(BigDecimal step) {
            Step = step;
        }

        public List<Options> getOptions() {
            return Options;
        }

        public void setOptions(List<Options> options) {
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

        public boolean isOnlineSearch() {
            return OnlineSearch;
        }

        public void setOnlineSearch(boolean onlineSearch) {
            OnlineSearch = onlineSearch;
        }

        public boolean isRequired() {
            return Required;
        }

        public void setRequired(boolean required) {
            Required = required;
        }

        public boolean isOnlyCompleted() {
            return OnlyCompleted;
        }

        public void setOnlyCompleted(boolean onlyCompleted) {
            OnlyCompleted = onlyCompleted;
        }

        public int getAnswersLimitMin() {
            return AnswersLimitMin;
        }

        public void setAnswersLimitMin(int answersLimitMin) {
            AnswersLimitMin = answersLimitMin;
        }

        public int getAnswersLimitMax() {
            return AnswersLimitMax;
        }

        public void setAnswersLimitMax(int answersLimitMax) {
            AnswersLimitMax = answersLimitMax;
        }

        public boolean isComment() {
            return IsComment;
        }

        public void setComment(boolean comment) {
            IsComment = comment;
        }

        public boolean isExtendanswer() {
            return IsExtendanswer;
        }

        public void setExtendanswer(boolean extendanswer) {
            IsExtendanswer = extendanswer;
        }

        public List<String> getExtendVals() {
            return ExtendVals;
        }

        public void setExtendVals(List<String> extendVals) {
            ExtendVals = extendVals;
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

        public ActivityValue getActivity() {
            return Activity;
        }

        public void setActivity(ActivityValue activity) {
            Activity = activity;
        }
    }

    public static class Options {
        // 选项的ID
        public int OptionID;
        // 选项的名称
        public String OptionName;
        // 选项的Score
        public int Score;
        // 选项的Score_Weight
        public int Score_Weight;
        private boolean isChecked;      //是否选中
        // 选项颜色（如：#ffffff），选项选中之后的颜色
        public String OptionColor;

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

        public int getScore() {
            return Score;
        }

        public void setScore(int score) {
            Score = score;
        }

        public int getScore_Weight() {
            return Score_Weight;
        }

        public void setScore_Weight(int score_Weight) {
            Score_Weight = score_Weight;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getOptionColor() {
            return OptionColor;
        }

        public void setOptionColor(String optionColor) {
            OptionColor = optionColor;
        }
    }

    public static class DeriveItem {
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

    public static class Peoples implements Serializable{
        private int USERID;             //ID
        private String Email;           //email
        private String UserName;        //姓名
        private String Phone;
        private int IsMyContact;
        private int IsMyCompanyUser;
        private int IsMyTeam;
        private int JobTitle;
        private int IsSelected;     //是否选中

        public int getUSERID() {
            return USERID;
        }

        public void setUSERID(int USERID) {
            this.USERID = USERID;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String phone) {
            Phone = phone;
        }

        public int getIsMyContact() {
            return IsMyContact;
        }

        public void setIsMyContact(int isMyContact) {
            IsMyContact = isMyContact;
        }

        public int getIsMyCompanyUser() {
            return IsMyCompanyUser;
        }

        public void setIsMyCompanyUser(int isMyCompanyUser) {
            IsMyCompanyUser = isMyCompanyUser;
        }

        public int getIsMyTeam() {
            return IsMyTeam;
        }

        public void setIsMyTeam(int isMyTeam) {
            IsMyTeam = isMyTeam;
        }

        public int getJobTitle() {
            return JobTitle;
        }

        public void setJobTitle(int jobTitle) {
            JobTitle = jobTitle;
        }

        public int getIsSelected() {
            return IsSelected;
        }

        public void setIsSelected(int isSelected) {
            IsSelected = isSelected;
        }
    }

    public static class ActivityValue
    {
        public int ActivityID;
        public String TargetDate;       //时间
        public String UserIds;          //选中用户id
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
