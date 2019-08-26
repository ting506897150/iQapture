package com.example.vcserver.iqapture.bean.dataset;
import java.io.Serializable;
import java.util.List;

/**
 * Created by VCServer on 2018/3/8.
 */

public class FilledResult{
    public List<IQRecord> rows;
    public int total;

    public List<IQRecord> getRows() {
        return rows;
    }

    public void setRows(List<IQRecord> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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

}
