package com.example.vcserver.iqapture.bean.dataset;

import java.io.Serializable;
import java.util.List;

/**
 * Created by VCServer on 2018/3/8.
 */

public class DatasetResult{
    public List<IQDataset> rows;
    public int Total;

    public List<IQDataset> getRows() {
        return rows;
    }

    public void setRows(List<IQDataset> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    //IQDataset
    public static class IQDataset implements Serializable{
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
        //
        private List<IQDataset> Childrens;

        public List<IQDataset> getChildrens() {
            return Childrens;
        }

        public void setChildrens(List<IQDataset> childrens) {
            Childrens = childrens;
        }

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

}
