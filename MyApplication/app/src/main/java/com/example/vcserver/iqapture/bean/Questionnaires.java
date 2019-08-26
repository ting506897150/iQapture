package com.example.vcserver.iqapture.bean;

import java.util.List;

/**
 * Created by VCServer on 2018/3/28.
 * 问卷返回实体类
 */

public class Questionnaires {
    // IQ中的节，如果第一个问题并不是一个section类型，那么请给我们创建一个默认的节ID设置为0
    public List<Questionnaire> section;
    // 当前问题是否最后一页了，比如说一共又3页，现在返回的是第三页的问卷数据那么IsLastPage为true，否则为false
    public boolean IsLastPage;
    // 该Filled是否完成状态
    public boolean isCompleted;

    public List<Questionnaire> getSection() {
        return section;
    }

    public void setSection(List<Questionnaire> section) {
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
