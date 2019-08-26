package com.example.vcserver.iqapture.bean.dataset;

/**
 * Created by VCServer on 2018/3/8.
 */

public class EditQuestionResult {
    // >0 表示成功：如果是新增，新增成功后返回的是RecordID，若是编辑，保存成功返回1
    public int ResultCode;
    // 要返回的信息（错误提示）
    public String ResultText;

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int resultCode) {
        ResultCode = resultCode;
    }

    public String getResultText() {
        return ResultText;
    }

    public void setResultText(String resultText) {
        ResultText = resultText;
    }
}
