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
        //Dataset各版本id
        public String DatasetPath;
        //子数据
        private List<IQDataset> Childrens;
        //是否离线过model
        public boolean offline;
        //是否存在未提交过的离线模式添加的问题model
        public boolean offlinequestion;

        public boolean isOffline() {
            return offline;
        }

        public void setOffline(boolean offline) {
            this.offline = offline;
        }

        public boolean isOfflinequestion() {
            return offlinequestion;
        }

        public void setOfflinequestion(boolean offlinequestion) {
            this.offlinequestion = offlinequestion;
        }

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

        public String getDatasetPath() {
            return DatasetPath;
        }

        public void setDatasetPath(String datasetPath) {
            DatasetPath = datasetPath;
        }
    }

}
