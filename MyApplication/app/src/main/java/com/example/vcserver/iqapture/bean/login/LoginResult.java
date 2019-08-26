package com.example.vcserver.iqapture.bean.login;

import java.io.Serializable;
import java.util.List;

/**
 * Created by VCServer on 2018/3/8.
 */

public class LoginResult {
    public int ResultCode;
    public VCAccount ResultObject;
    public String OtherResult;

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int resultCode) {
        ResultCode = resultCode;
    }

    public VCAccount getResultObject() {
        return ResultObject;
    }

    public void setResultObject(VCAccount resultObject) {
        ResultObject = resultObject;
    }

    public String getOtherResult() {
        return OtherResult;
    }

    public void setOtherResult(String otherResult) {
        OtherResult = otherResult;
    }


    public static class VCAccount implements Serializable{
        public int UserID;      //用户id
        public String Username;     //用户名
        public int DefaultCompany;      //默认公司id
        public List<VCCompany> MyCompanies;     //公司列表

        public int getUserID() {
            return UserID;
        }

        public void setUserID(int userID) {
            UserID = userID;
        }

        public String getUsername() {
            return Username;
        }

        public void setUsername(String username) {
            Username = username;
        }

        public int getDefaultCompany() {
            return DefaultCompany;
        }

        public void setDefaultCompany(int defaultCompany) {
            DefaultCompany = defaultCompany;
        }

        public List<VCCompany> getMyCompanies() {
            return MyCompanies;
        }

        public void setMyCompanies(List<VCCompany> myCompanies) {
            MyCompanies = myCompanies;
        }

    }
    public static class VCCompany implements Serializable {
        public int CompanyID;   //公司id
        public String CompanyName;      //公司名称
        public byte[] CompanyLogoContent;       //公司logo图片
        public String CompanyLogo;
        private int IsCurrentCompany;      //是否选中

        public int getIsCurrentCompany() {
            return IsCurrentCompany;
        }

        public void setIsCurrentCompany(int isCurrentCompany) {
            IsCurrentCompany = isCurrentCompany;
        }

        public int getCompanyID() {
            return CompanyID;
        }

        public void setCompanyID(int companyID) {
            CompanyID = companyID;
        }

        public String getCompanyName() {
            return CompanyName;
        }

        public void setCompanyName(String companyName) {
            CompanyName = companyName;
        }

        public byte[] getCompanyLogoContent() {
            return CompanyLogoContent;
        }

        public void setCompanyLogoContent(byte[] companyLogoContent) {
            CompanyLogoContent = companyLogoContent;
        }

        public String getCompanyLogo() {
            return CompanyLogo;
        }

        public void setCompanyLogo(String companyLogo) {
            CompanyLogo = companyLogo;
        }
    }
}
