package com.example.vcserver.iqapture.view.other;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.FileEntity;
import com.example.vcserver.iqapture.commadapter.OnItemClickListener;
import com.example.vcserver.iqapture.util.LinerLayoutItemDecoration;
import com.example.vcserver.iqapture.view.adapter.MyAdapter;
import com.example.vcserver.iqapture.view.base.BaseActivity;
import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by VCServer on 2018/5/29.
 */

public class FileSelect extends BaseActivity {
    @Bind(R.id.text_url)
    TextView textUrl;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;


    String sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    File currentFile;
    //存储文件
    private List<FileEntity> mList = new ArrayList<>();
    private List<FileEntity> submitList = new ArrayList<>();
    private MyAdapter mAdapter;

    int number;
    int selectnum;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_fileselect);
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void init() {
        number = getIntent().getIntExtra("number",0);
        recyclerview.addItemDecoration(new LinerLayoutItemDecoration(this, R.drawable.item_dirver_listview));//分割线
        currentFile = new File(sdRootPath);
        //显示文件列表
        showFileDir(sdRootPath);
    }

    private void showFileDir(String sdRootPath) {
        textUrl.setText(sdRootPath);
        findAllFiles(sdRootPath);
    }

    private void findAllFiles(String path) {
        mList.clear();
        if(path ==null ||path.equals("")){
            return;
        }
        //过滤隐藏文件
        FileFilter ff = new FileFilter() {
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        };
        File fatherFile = new File(path);
        File[] files = fatherFile.listFiles(ff);
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                FileEntity entity = new FileEntity();
                boolean isDirectory = files[i].isDirectory();
                if(isDirectory ==true){
                    entity.setFileType(FileEntity.Type.FLODER);
                }else{
                    entity.setFileType(FileEntity.Type.FILE);
                }
                entity.setFileName(files[i].getName().toString());
                entity.setFilePath(files[i].getAbsolutePath());
                entity.setFileSize(files[i].length()+"");
                mList.add(entity);
            }
        }
        selectnum = 0;
        mAdapter = new MyAdapter(this,R.layout.item_recyclerview, mList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isChecked()){
                        selectnum++;
                    }
                }
                if (number < selectnum){
                    showTip("The maximum number of options has been reached!");
                }else{
                    final FileEntity entity = mList.get(position);
                    if(entity.getFileType() == FileEntity.Type.FLODER){
                        currentFile = new File(entity.getFilePath());
                        showFileDir(entity.getFilePath());
                    }else if(entity.getFileType() == FileEntity.Type.FILE){
                        if (entity.isChecked()){
                            entity.setChecked(false);
                        }else{
                            entity.setChecked(true);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @OnClick({R.id.submit, R.id.back, R.id.img_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getFileType() == FileEntity.Type.FILE){
                        if (mList.get(i).isChecked()){
                            FileEntity entity = new FileEntity();
                            entity.setFileName(mList.get(i).getFileName());
                            entity.setFilePath(mList.get(i).getFilePath());
                            submitList.add(entity);
                        }
                    }
                }
                if (submitList.size() == 0){
                    showTip("The file cannot be submitted because it has not been selected!");
                }else{
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("i", (Serializable) submitList);
                    //设置返回数据
                    setResult(RESULT_OK, intent);
                    //关闭Activity
                    finish();
                }
                break;
            case R.id.back:
                if(sdRootPath.equals(currentFile.getAbsolutePath())){
                    showTip("Cannot continue to return!");
                    return;
                }
                String parentPath = currentFile.getParent();
                currentFile = new File(parentPath);
                showFileDir(parentPath);
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }


    public void onBackPressed() {
        if(sdRootPath.equals(currentFile.getAbsolutePath())){
            finish();
            return;
        }
        String parentPath = currentFile.getParent();
        currentFile = new File(parentPath);
        showFileDir(parentPath);
    }
}
