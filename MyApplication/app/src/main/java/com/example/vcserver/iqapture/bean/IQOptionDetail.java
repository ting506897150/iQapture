package com.example.vcserver.iqapture.bean;

import java.util.List;

/**
 * Created by VCServer on 2019/1/8.
 */

public class IQOptionDetail {
    public List<Options> options;
    public boolean IsLastPage;

    public List<Options> getOptions() {
        return options;
    }

    public void setOptions(List<Options> options) {
        this.options = options;
    }

    public boolean isLastPage() {
        return IsLastPage;
    }

    public void setLastPage(boolean lastPage) {
        IsLastPage = lastPage;
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
}
