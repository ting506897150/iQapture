package com.example.vcserver.iqapture.view.other;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.SubmitList;
import com.example.vcserver.iqapture.bean.SubmitQuestion;
import com.example.vcserver.iqapture.bean.dataset.DatasetApi;
import com.example.vcserver.iqapture.bean.dataset.DatasetResult;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionApi;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionResult;
import com.example.vcserver.iqapture.bean.dataset.FilledApi;
import com.example.vcserver.iqapture.bean.dataset.FilledResult;
import com.example.vcserver.iqapture.bean.dataset.QuestionnaireApi;
import com.example.vcserver.iqapture.bean.login.LoginResult;
import com.example.vcserver.iqapture.commadapter.OnItemClickListener;
import com.example.vcserver.iqapture.commadapter.OnItemLongClickListener;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.presenter.other.DatasetPresenter;
import com.example.vcserver.iqapture.util.DatasetService;
import com.example.vcserver.iqapture.util.LinerLayoutItemDecoration;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.adapter.Item2Adapter;
import com.example.vcserver.iqapture.view.adapter.ItemAdapter;
import com.example.vcserver.iqapture.view.adapter.MenuAdapter;
import com.example.vcserver.iqapture.view.base.BaseActivity;
import com.example.vcserver.iqapture.view.other.view.IDatasetView;
import com.google.gson.reflect.TypeToken;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by VCServer on 2018/3/5.
 */

public class MainActivity extends BaseActivity<DatasetPresenter> implements IDatasetView, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.text_tab)
    TextView textTab;
    @Bind(R.id.main_drawer_layout)
    DrawerLayout mainDrawerLayout;
    @Bind(R.id.recyclerview_menu)
    RecyclerView recyclerviewMenu;
    @Bind(R.id.image_CompanyLogo)
    ImageView imageCompanyLogo;
    @Bind(R.id.edit_search)
    EditText editSearch;
    @Bind(R.id.img_back)
    ImageView imgBack;
    @Bind(R.id.recyclerview_item)
    RecyclerView recyclerviewItem;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.img_add)
    ImageView imgAdd;
    @Bind(R.id.AutoRelativeLayout_Filled)
    AutoLinearLayout AutoRelativeLayoutFilled;
    @Bind(R.id.rlayout_nodate)
    RelativeLayout rlayoutNodate;
    @Bind(R.id.searchlayout)
    AutoRelativeLayout searchlayout;
    @Bind(R.id.recyclerview_record_item)
    RecyclerView recyclerviewRecordItem;

    private AlertDialog mDialog;

    private LoginResult.VCAccount vcAccount;        //个人信息model
    private List<LoginResult.VCCompany> vcCompanyList = new ArrayList<>();//公司List集合

    private DatasetApi datasetApi;
    private FilledApi filledApi;
    private int userid, companyid;//用户id，公司id
    private List<DatasetResult.IQDataset> datasetList = new ArrayList<>();//dataset数据集合，用来往适配器里填充的
    private List<FilledResult.IQRecord> filledList = new ArrayList<>();//filled数据集合，用来往适配器里填充的
    private List<DatasetResult.IQDataset> seachdatasetList = new ArrayList<>();//dataset每次访问接口保存的数据集合，用来查询的，保证每次都是从整个数据集合里面查询
    private List<FilledResult.IQRecord> seachfilledList = new ArrayList<>();//filled每次访问接口保存的数据集合，用来查询的，保证每次都是从整个数据集合里面查询
    List<DatasetResult.IQDataset> seachdatasetlist;//dataset查询出来的数据集合
    List<FilledResult.IQRecord> seachfilledlist;//filled查询出来的数据集合
    DatasetResult.IQDataset dataset;//dataset model类
    FilledResult.IQRecord filled;//filled model类

    private int folderid = 0;//dataset的folderid record的datasetId
    private int level = 1;//当前页面层级
    private Map<Integer, Integer> mFolderidMap = new HashMap<>();//存储Folderid（id也存储了）
    private Map<Integer, String> mTitleMap = new HashMap<>(); //存储标题的文字内容
    private Map<Integer, Boolean> mIsDirMap = new HashMap<>(); //存储是否是目录的标识

    private String item;
    ItemAdapter itemAdapter;
    Item2Adapter item2Adapter;
    MenuAdapter menuAdapter;


    List<Questionnaire.Question> allquestionlist = new ArrayList<>();
    int number = 0; //计算一共循环了多少个图片类型
    int allnumber = 0; //计算一共上传了多少个图片类型
    int uploadnum = 0; //计算一共循环上传了多少个图片
    int allselectpos = 0; //每个图片类型有多少个图片（已有的+要上传的）
    ImageShow imageShow;
    SubmitQuestion submitQuestion;
    private EditQuestionApi editQuestionApi;
    private int filledId;
    private int datasetId;
    private boolean isCompleted;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    private QuestionnaireApi questionnaireApi;
    List<Questionnaire> loadmorequestionnaireList = new ArrayList<>();//每次加载的问卷
    List<Questionnaire> allquestionnaireList = new ArrayList<>();//获取的所有问卷（用来加载）
    List<Questionnaire> questionnaireList = new ArrayList<>();//获取的所有问卷（用来循环判断）
    Questionnaire questionnaire;
    int page = 1;
    ContentValues cv1;

    SubmitList submitList;
    List<SubmitList> submitListList = new ArrayList<>();
    int submitnum;
    int submitsize;

    int ParentFolderID = 0;//当前dataset所在文件夹编号
    boolean recordstate = false;//判断是否是长按事件

    int filledi = 0;
    boolean questionmodelstate = false;//判断是否是加载questionmodel

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void initPresenter() {
        mPresenter = new DatasetPresenter(mContext, this);
    }

    @Override
    protected void init() {
        text();
        mFolderidMap.put(level, folderid);
        mTitleMap.put(level, "Directory");
        mIsDirMap.put(level, true);
        vcAccount = (LoginResult.VCAccount) getIntent().getSerializableExtra("vcAccount");
        if (AppUtil.isNetworkAvailable(this)) {//有网络获取dataset
            //打开Service服务
            Intent intent = new Intent(this, DatasetService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
        }
        //侧滑菜单
        initdate();
        //设置刷新时动画的颜色，可以设置4个
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            swipeRefreshLayout.setOnRefreshListener(this);
        }
        editSearchListener();
        mainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (mIsDirMap.get(level) == true) {
                    if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取dataset
                        showLoadingDialog();
                        initdataset(mFolderidMap.get(level));
                    } else {//无网络获取本地dataset
                        locadataset(mFolderidMap.get(level));
                    }
                } else {
                    if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取dataset
                        showLoadingDialog();
                        initFilled(mFolderidMap.get(level));
                    } else {//无网络获取本地dataset
                        locafilled(mFolderidMap.get(level));
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppUtil.isNetworkAvailable(MainActivity.this)) {
                //判断是否已经读取数据库保存的问卷列表  避免重复提交
                if (editor.getBoolean(Preferences.ISSUBMIT, false) == false) {
                    //获取存储的问题列表
                    db = myDatabaseHelper.getWritableDatabase();
                    //判断表是否为空，便于每次重新添加数据
                    if (db.rawQuery("SELECT * FROM submitquestion", null).getCount() > 0) {
                        //取出提交数据
                        String sql = "SELECT * FROM submitquestion";
                        Cursor cursor = db.rawQuery(sql, null);
                        submitListList.clear();
                        while (cursor.moveToNext()) {
                            submitList = new SubmitList();
                            submitList.setQuestionjson(cursor.getString(cursor.getColumnIndex("questionjson")));
                            submitList.setDatasetID(cursor.getInt(cursor.getColumnIndex("DatasetID")));
                            submitList.setRecordID(cursor.getInt(cursor.getColumnIndex("RecordId")));
                            submitList.setParentFolderID(cursor.getInt(cursor.getColumnIndex("ParentFolderID")));
                            submitList.setCompleted(cursor.getString(cursor.getColumnIndex("isCompleted")).equals("1") ? true : false);
                            submitListList.add(submitList);
                            submitList = null;
                        }
                        db.close();
                        submitsize = submitListList.size();
                        submitnum = 0;
                        imageupload(submitnum);
                    } else {
                        db.close();
                    }
                }
                if (mIsDirMap.get(level) == true) {//加载dataset
                    showLoadingDialog();
                    initdataset(mFolderidMap.get(level));
                } else {//加载filled
                    showLoadingDialog();
                    initFilled(mFolderidMap.get(level));
                }
            } else {
                if (mIsDirMap.get(level) == true) {//加载dataset
                    locadataset(mFolderidMap.get(level));
                } else {//加载filled
                    locafilled(mFolderidMap.get(level));
                }
            }
        }
    }

    private void imageupload(int submitnum) {
        number = 0;
        uploadnum = 0;
        allselectpos = 0;
        //上传图片
        filledId = submitListList.get(submitnum).getRecordID();
        datasetId = submitListList.get(submitnum).getDatasetID();
        ParentFolderID = submitListList.get(submitnum).getParentFolderID();
        isCompleted = submitListList.get(submitnum).isCompleted();
        Type type = new TypeToken<List<Questionnaire.Question>>() {
        }.getType();
        allquestionlist = gson.fromJson(submitListList.get(submitnum).getQuestionjson(), type);

        for (int i = 0; i < allquestionlist.size(); i++) {
            if (allquestionlist.get(i).getType() == 12) {
                if (!TextUtils.isEmpty(allquestionlist.get(i).getAnswer()) && allquestionlist.get(i).getAnswer() != "") {
                    number++;
                    String[] selectpos = allquestionlist.get(i).getAnswer().split("[,]");
                    allselectpos = selectpos.length;
                    StringBuffer sb = new StringBuffer();//进行拼接的字段
                    for (int k = 0; k < selectpos.length; k++) {
                        //判断是否包含@符号（包含为已上传图片，不包含为新加图片，只添加新加图片）
                        if (!selectpos[k].toString().contains("@")) {
                            uploadnum++;
                            imageShow = new ImageShow(selectpos[k], i);
                            imageShow.execute(BaseApi.getBaseUrl() + "OpenBook/IOSAPI/AddImg");
                        } else {
                            sb.append(selectpos[k] + ",");
                            allquestionlist.get(i).setAnswer(String.valueOf(sb));
                            sb = null;
                        }
                    }
                }
            }
        }
        if (uploadnum == 0) {
            SubmitQuestion(allquestionlist);
        }
    }

    //获取本地dataset数据
    private void locadataset(final int folderid) {
        if (mIsDirMap.get(level) == false) {
            imgBack.setVisibility(View.VISIBLE);
            textTab.setText(mTitleMap.get(level));
        } else {
            if (level > 1) {
                imgBack.setVisibility(View.VISIBLE);
            } else {
                imgBack.setVisibility(View.GONE);
            }
            imgAdd.setVisibility(View.GONE);
            textTab.setText(mTitleMap.get(level));
            AutoRelativeLayoutFilled.setVisibility(View.GONE);
        }
        db = myDatabaseHelper.getWritableDatabase();
        //取出dataset数据
        String sql = "SELECT * FROM datasetresult where ParentFolderID = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(folderid)});
        if (cursor.getCount() > 0) {
            datasetList.clear();
            seachdatasetList.clear();
            while (cursor.moveToNext()) {
                dataset = new DatasetResult.IQDataset();
                dataset.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                dataset.setName(cursor.getString(cursor.getColumnIndex("Name")));
                dataset.setBase64Icon(cursor.getString(cursor.getColumnIndex("Base64Icon")));
                dataset.setFolder(cursor.getString(cursor.getColumnIndex("isFolder")).equals("1") ? true : false);
                dataset.setParentFolderID(cursor.getInt(cursor.getColumnIndex("ParentFolderID")));
                datasetList.add(dataset);
                seachdatasetList.add(dataset);
                dataset = null;
            }

            cursor.close();
            db.close();

            searchlayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerviewItem.setVisibility(View.VISIBLE);
            recyclerviewRecordItem.setVisibility(View.GONE);
            rlayoutNodate.setVisibility(View.GONE);
            adapter();
        } else {
            searchlayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            rlayoutNodate.setVisibility(View.VISIBLE);
        }
    }

    //获取本地filled数据
    private void locafilled(final int datasetId) {
        imgBack.setVisibility(View.VISIBLE);
        imgAdd.setVisibility(View.VISIBLE);
        textTab.setText(mTitleMap.get(level));
        AutoRelativeLayoutFilled.setVisibility(View.VISIBLE);
        //暂时不显示record
//        searchlayout.setVisibility(View.GONE);
//        swipeRefreshLayout.setVisibility(View.GONE);
//        rlayoutNodate.setVisibility(View.VISIBLE);

        db = myDatabaseHelper.getWritableDatabase();
        //取出record列表
        String sql = "SELECT * FROM filledresult where DatasetID = ? order by RowNo desc limit 50";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(datasetId)});
        if (cursor.getCount() > 0) {
            filledList.clear();
            seachfilledList.clear();
            while (cursor.moveToNext()) {
                filled = new FilledResult.IQRecord();
                filled.setID(cursor.getInt(cursor.getColumnIndex("ID")));
                filled.setDatasetID(cursor.getInt(cursor.getColumnIndex("DatasetID")));
                filled.setRowNo(cursor.getInt(cursor.getColumnIndex("RowNo")));
                filled.setCompeleted(cursor.getString(cursor.getColumnIndex("IsCompeleted")).equals("1") ? true : false);
                filled.setCreator(cursor.getString(cursor.getColumnIndex("Creator")));
                filled.setCreateTime(cursor.getString(cursor.getColumnIndex("CreateTime")));
                filledList.add(filled);
                seachfilledList.add(filled);
                filled = null;
            }
            cursor.close();
            db.close();

            searchlayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            rlayoutNodate.setVisibility(View.GONE);
            recyclerviewRecordItem.setVisibility(View.VISIBLE);
            recyclerviewItem.setVisibility(View.GONE);
            adapter2();
        } else {
            searchlayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
            rlayoutNodate.setVisibility(View.VISIBLE);
        }
    }


    //editSearch的事件
    private void editSearchListener() {
        //搜索框监听用户输入
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //内容改变之前调用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    //搜索框有数据显示清空图片按钮
                    Drawable clear = getResources().getDrawable(R.mipmap.delete);
                    clear.setBounds(0, 0, 40, 40);
                    editSearch.setCompoundDrawables(null, null, clear, null);//图片放在那里
                } else {
                    //清空数据后不显示清空图片按钮
                    editSearch.setCompoundDrawables(null, null, null, null);//图片放在那里
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //内容改变之后调用
                if (!TextUtils.isEmpty(editSearch.getText().toString())) {
                    item = String.valueOf(s);
                    setSeachdate(item);
                } else {
                    if (mIsDirMap.get(level) == true) {
                        datasetList.clear();
                        seachdatasetList.clear();
                        if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取dataset
                            showLoadingDialog();
                            initdataset(folderid);
                        } else {//无网络获取本地dataset
                            locadataset(folderid);
                        }
                    } else {
                        filledList.clear();
                        seachfilledList.clear();
                        if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取filled
                            showLoadingDialog();
                            initFilled(folderid);
                        } else {//无网络获取本地filled
                            locafilled(folderid);
                        }

                    }
                }
            }
        });

        //搜索框右侧清空图片点击事件
        editSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = editSearch.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > editSearch.getWidth() - editSearch.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    editSearch.setText("");
                    editSearch.setFocusable(false);
                    editSearch.setFocusableInTouchMode(false);
                    editSearch.clearFocus();
                    editSearch.findFocus();
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    //清空数据后不显示清空图片按钮
                    editSearch.setCompoundDrawables(null, null, null, null);//图片放在那里
                }
                return false;
            }
        });
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearch.setFocusable(true);
                editSearch.setFocusableInTouchMode(true);
                editSearch.requestFocus();
                editSearch.findFocus();
            }
        });
    }

    //适配器
    private void adapter() {
        itemAdapter = new ItemAdapter(this, R.layout.item_itemfragmentrecyclerview, datasetList);
        if (Other.isPad(mContext)) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                recyclerviewItem.setLayoutManager(new GridLayoutManager(this, 3));
            } else {
                //横屏
                recyclerviewItem.setLayoutManager(new GridLayoutManager(this, 5));
            }
        } else {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                recyclerviewItem.setLayoutManager(new GridLayoutManager(this, 3));
            } else {
                //横屏
                recyclerviewItem.setLayoutManager(new GridLayoutManager(this, 5));
            }
        }
        recyclerviewItem.setAdapter(itemAdapter);
        recyclerviewItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                closeLoadingDialog();
                recyclerviewItem.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        itemAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Other.isFastClick()) {
                    if (datasetList.get(position).isFolder()) {
                        level = level + 1;
                        folderid = datasetList.get(position).getID();
                        mFolderidMap.put(level, folderid);
                        mTitleMap.put(level, datasetList.get(position).getName());
                        mIsDirMap.put(level, datasetList.get(position).isFolder());
                        ParentFolderID = datasetList.get(position).getParentFolderID();
                        if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取dataset
                            showLoadingDialog();
                            initdataset(folderid);
                        } else {//无网络获取本地dataset
                            locadataset(folderid);
                        }
                    } else {
//                        db = myRecordDatabaseHelper.getWritableDatabase();
//                        //判断表是否为空，便于每次重新添加数据
//                        if (db.rawQuery("SELECT * FROM record_questionmodelresult",null).getCount() > 0){
//                            myRecordDatabaseHelper.Deleterecord_questionmodel(db);
//                            db.close();
//                        }

                        level = level + 1;
                        folderid = datasetList.get(position).getID();
                        mFolderidMap.put(level, folderid);
                        mTitleMap.put(level, datasetList.get(position).getName());
                        mIsDirMap.put(level, datasetList.get(position).isFolder());
                        ParentFolderID = datasetList.get(position).getParentFolderID();//保存文件夹编号  保存问卷需要用
                        filledList.clear();
                        seachfilledList.clear();
                        if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取filled
                            showLoadingDialog();
                            initFilled(folderid);
                        } else {//无网络获取本地filled
                            locafilled(folderid);
                        }
                    }
                }
            }
        });

        itemAdapter.setmOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                //判断是不是文件夹
                if (datasetList.get(position).isFolder() == false) {
//                    showTip("你长按了dataset 下一页面是record");
//                    if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取filled
//                        showLoadingDialog();
//                        folderid = datasetList.get(position).getID();
//                        recordstate = true;
//                        initFilled(folderid);
//                    }
                } else {
//                    showTip("你长按了dataset 下一页面还有dataset文件");
                }
            }
        });
    }

    //适配器
    private void adapter2() {
        item2Adapter = new Item2Adapter(this, R.layout.item_itemfragmentchildrecyclerview, filledList);
        recyclerviewRecordItem.setLayoutManager(new LinearLayoutManager(this));
        recyclerviewRecordItem.setAdapter(item2Adapter);
        recyclerviewRecordItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //当前页面加载完成
//                closeLoadingDialog();
                if (AppUtil.isNetworkAvailable(mContext)) {
                    db = myDatabaseHelper.getWritableDatabase();
                    //取出record列表
                    String sql = "SELECT * FROM filledresult where DatasetID = ?";
                    Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(datasetId)});
                    //判断数据库数据和最新数据 最新数据比数据库多 说明有改变 重新保存数据（只能检测到新增数据  web和本地修改都不行）
                    if (filledList.size() > cursor.getCount()) {
                        recordstate = true;
                        initFilled(folderid);
                    }
//                    recordstate = true;
//                    initFilled(folderid);
                }

//                RecordService recordService = new RecordService();
//                recordService.stopSelf();
//                if (AppUtil.isNetworkAvailable(mContext)){
//                    //缓存当前页面数据及其下面的所有问题列表。
//                    //打开Service服务
//                    Intent intent1 = new Intent(MainActivity.this, RecordService.class);
//                    intent1.putExtra("datasetId", id);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startService(intent1);
//                    //缓存当前页面问题列表模型
//                    page = 1;
//                    questionmodel(id,0, page);//查看ID是否相同
//                }
                recyclerviewRecordItem.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        item2Adapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Other.isFastClick()) {
                    //跳转问卷页面
                    Intent intent = new Intent(MainActivity.this, QuestionnaireActivity.class);
                    intent.putExtra("title", mTitleMap.get(level));
                    intent.putExtra("filledId", filledList.get(position).getID());
                    intent.putExtra("datasetId", filledList.get(position).getDatasetID());//filledList.get(position).getDatasetID()
                    intent.putExtra("compeleted", filledList.get(position).isCompeleted());
                    intent.putExtra("ParentFolderID", ParentFolderID);
                    startActivity(intent);
                }
            }
        });
    }

    private void questionmodel(int datasetId, int recordId, int page) {
        questionnaireApi = new QuestionnaireApi();
        questionnaireApi.setUserId(userid);
        questionnaireApi.setCompanyId(companyid);
        questionnaireApi.setDatasetId(datasetId);
        questionnaireApi.setRecordId(recordId);
        questionnaireApi.setPage(page);
        questionnaireApi.setRow(10);
        mPresenter.startPost(this, questionnaireApi);
    }

    //搜索
    public void setSeachdate(String item) {
        if (mIsDirMap.get(level) == true) {
            datasetList.clear();
            seachdatasetlist = new ArrayList<>();
            for (int i = 0; i < seachdatasetList.size(); i++) {
                if (seachdatasetList.get(i).getName().toString().toLowerCase().contains(item.toLowerCase())) {
                    dataset = new DatasetResult.IQDataset();
                    dataset.setID(seachdatasetList.get(i).getID());
                    dataset.setParentFolderID(seachdatasetList.get(i).getParentFolderID());
                    dataset.setName(seachdatasetList.get(i).getName());
                    dataset.setBase64Icon(seachdatasetList.get(i).getBase64Icon());
                    dataset.setFolder(seachdatasetList.get(i).isFolder());
                    seachdatasetlist.add(dataset);
                    dataset = null;
                }
            }
            datasetList = seachdatasetlist;
            seachdatasetlist = null;
            if (datasetList != null && datasetList.size() > 0) {
                searchlayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                rlayoutNodate.setVisibility(View.GONE);
                adapter();
            } else {
                searchlayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                rlayoutNodate.setVisibility(View.VISIBLE);
            }
        } else {
            filledList.clear();
            seachfilledlist = new ArrayList<>();
            for (int i = 0; i < seachfilledList.size(); i++) {
                if (String.valueOf(seachfilledList.get(i).getRowNo()).toString().toLowerCase().contains(item.toLowerCase())) {
                    filled = new FilledResult.IQRecord();
                    filled.setRowNo(seachfilledList.get(i).getRowNo());
                    filled.setCreateTime(seachfilledList.get(i).getCreateTime());
                    filled.setCreator(seachfilledList.get(i).getCreator());
                    seachfilledlist.add(filled);
                    filled = null;
                }
            }
            filledList = seachfilledlist;
            seachfilledlist = null;
            if (filledList != null && filledList.size() > 0) {
                searchlayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                rlayoutNodate.setVisibility(View.GONE);
                adapter2();
            } else {
                searchlayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                rlayoutNodate.setVisibility(View.VISIBLE);
            }
        }
    }

    //访问接口
    public void initdataset(int folderId) {
        userid = SharedPreferencesUtil.getsInstances(MainActivity.this).getInt(Preferences.USERID, 0);
        companyid = SharedPreferencesUtil.getsInstances(MainActivity.this).getInt(Preferences.COMPANYID, 0);
        datasetApi = new DatasetApi();
        datasetApi.setUserId(userid);
        datasetApi.setCompanyId(companyid);
        datasetApi.setFolderId(folderId);
        mPresenter.startPost(MainActivity.this, datasetApi);
    }

    //访问接口
    private void initFilled(int datasetId) {
        userid = SharedPreferencesUtil.getsInstances(MainActivity.this).getInt(Preferences.USERID, 0);
        companyid = SharedPreferencesUtil.getsInstances(MainActivity.this).getInt(Preferences.COMPANYID, 0);
        filledApi = new FilledApi();
        filledApi.setUserId(userid);
        filledApi.setCompanyId(companyid);
        filledApi.setDatasetId(datasetId);
        mPresenter.startPost(MainActivity.this, filledApi);
    }

    private void addquestion() {
        //跳转问卷页面
        Intent intent = new Intent(MainActivity.this, QuestionnaireActivity.class);
        intent.putExtra("title", mTitleMap.get(level));
        intent.putExtra("filledId", 0);
        intent.putExtra("datasetId", mFolderidMap.get(level));
        intent.putExtra("ParentFolderID", ParentFolderID);
        startActivity(intent);
    }


    //添加侧滑菜单数据
    private void initdate() {
        //公司列表集合
        vcCompanyList.addAll(vcAccount.getMyCompanies());
        //设置logo图标
        for (int i = 0; i < vcCompanyList.size(); i++) {
            if (vcCompanyList.get(i).getCompanyID() == vcAccount.getDefaultCompany()) {
                imageCompanyLogo.setImageBitmap(Other.stringtoBitmap(vcAccount.getMyCompanies().get(i).getCompanyLogo()));
            }
        }
        //设置adapter
        menuAdapter = new MenuAdapter(this, R.layout.item_menurecyclerview, vcCompanyList);
        recyclerviewMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerviewMenu.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));//分割线
        recyclerviewMenu.setAdapter(menuAdapter);
        menuAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //设置其他选择项全部为未选中
                for (int i = 0; i < vcCompanyList.size(); i++) {
                    vcCompanyList.get(i).setIsCurrentCompany(0);
                }
                vcCompanyList.get(position).setIsCurrentCompany(1);//点击的设为选中.
                SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(MainActivity.this);
                editor.putInt(Preferences.COMPANYID, vcCompanyList.get(position).getCompanyID());//选中的公司id
                imageCompanyLogo.setImageBitmap(Other.stringtoBitmap(vcAccount.getMyCompanies().get(position).getCompanyLogo()));//选中的公司logo
                menuAdapter.notifyDataSetChanged();
                mainDrawerLayout.closeDrawer(Gravity.LEFT);//侧滑关闭
            }
        });
    }


    @OnClick({R.id.img_menu, R.id.back, R.id.layout_exit, R.id.layout_setting, R.id.img_back, R.id.img_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //左划菜单
            case R.id.img_menu:
                mainDrawerLayout.openDrawer(Gravity.LEFT);//侧滑打开
                break;
            case R.id.back:
                mainDrawerLayout.closeDrawer(Gravity.LEFT);//侧滑关闭
                break;
            //注销
            case R.id.layout_exit:
                showAlertDialog();
                break;
            case R.id.layout_setting:
                mainDrawerLayout.closeDrawer(Gravity.LEFT);//侧滑关闭
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.img_back:
                if (level > 1) {
                    folderid = mFolderidMap.get(level - 1);
                    level = level - 1;
                    if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取dataset
                        showLoadingDialog();
                        initdataset(folderid);
                    } else {//无网络获取本地dataset
                        locadataset(folderid);
                    }
                }
                break;
            case R.id.img_add:
                addquestion();
                break;
        }
    }

    // 点击退出弹出提示信息
    public void showAlertDialog() {
        View view = View.inflate(this, R.layout.dialog, null);
        TextView title = view.findViewById(R.id.title);
        TextView positiveButton = view.findViewById(R.id.positiveButton);
        TextView negativeButton = view.findViewById(R.id.negativeButton);
        title.setText("Are you sure you would like to logout?");
        mDialog = new AlertDialog.Builder(this, R.style.dialog)
                .setView(view)
                .show();
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(MainActivity.this);
                editor.putString(Preferences.USERNAME, "");
                editor.putString(Preferences.PASSWORD, "");
                editor.putInt(Preferences.USERID, 0);
                editor.putInt(Preferences.COMPANYID, 0);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    @Override
    public void getDataset(DatasetResult result) {
        if (mIsDirMap.get(level) == false) {
            imgBack.setVisibility(View.VISIBLE);
            textTab.setText(mTitleMap.get(level));
        } else {
            if (level > 1) {
                imgBack.setVisibility(View.VISIBLE);
            } else {
                imgBack.setVisibility(View.GONE);
            }
            imgAdd.setVisibility(View.GONE);
            textTab.setText(mTitleMap.get(level));
            AutoRelativeLayoutFilled.setVisibility(View.GONE);
        }
        if (result.getRows() != null) {
            searchlayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            recyclerviewItem.setVisibility(View.VISIBLE);
            recyclerviewRecordItem.setVisibility(View.GONE);
            rlayoutNodate.setVisibility(View.GONE);
            datasetList.clear();
            seachdatasetList.clear();
            datasetList.addAll(result.getRows());
            seachdatasetList.addAll(result.getRows());
            adapter();
        } else {
            closeLoadingDialog();
            searchlayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            rlayoutNodate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getFilled(FilledResult result) {
        //存储record列表数据 question数据 questionmodel数据
        if (recordstate == true) {
            db = myDatabaseHelper.getWritableDatabase();
            if (result.getRows() != null) {
                for (int i = 0; i < result.getRows().size(); i++) {
                    cv1 = new ContentValues();
                    cv1.put("ID", result.getRows().get(i).getID());
                    cv1.put("DatasetID", result.getRows().get(0).getDatasetID());
                    cv1.put("RowNo", result.getRows().get(i).getRowNo());
                    cv1.put("IsCompeleted", result.getRows().get(i).isCompeleted());
                    cv1.put("Creator", result.getRows().get(i).getCreator());
                    cv1.put("CreateTime", result.getRows().get(i).getCreateTime());
                    db.insert("filledresult", null, cv1);
                }
                db.close();
                cv1 = null;

                filledList.clear();
                seachfilledList.clear();
                filledList.addAll(result.getRows());
                seachfilledList.addAll(result.getRows());
                questionmodel(filledList.get(filledi).getDatasetID(), filledList.get(filledi).getID(), page);
            } else {
                questionmodelstate = true;
                questionmodel(folderid, 0, page);//加载question模板
            }
        } else {//正常加载record列表数据
            imgBack.setVisibility(View.VISIBLE);
            imgAdd.setVisibility(View.VISIBLE);
            textTab.setText(mTitleMap.get(level));
            AutoRelativeLayoutFilled.setVisibility(View.VISIBLE);
            if (result.getRows() != null) {
                searchlayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                recyclerviewItem.setVisibility(View.GONE);
                recyclerviewRecordItem.setVisibility(View.VISIBLE);
                rlayoutNodate.setVisibility(View.GONE);
                filledList.clear();
                seachfilledList.clear();
                filledList.addAll(result.getRows());
                seachfilledList.addAll(result.getRows());
                adapter2();
            } else {
                closeLoadingDialog();
                searchlayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                rlayoutNodate.setVisibility(View.VISIBLE);
                //无数据加载完成
                recordstate = true;
                initFilled(folderid);
            }
        }
    }

    @Override
    public void getQuestionmodel(Questionnaires questionnaires) {
        if (questionnaires != null) {
            loadmorequestionnaireList.clear();
            if (questionnaires.getSection() != null) {
                loadmorequestionnaireList.addAll(questionnaires.getSection());
                //加载父节点
                if (allquestionnaireList != null && allquestionnaireList.size() != 0) {//判断是不是第一次加载数据，如果不是则进入循环对比数据
                    for (int j = 0; j < loadmorequestionnaireList.size(); j++) {
                        if (allquestionnaireList.get(allquestionnaireList.size() - 1).getID() == loadmorequestionnaireList.get(j).getID()) {//判断新增数据是不是父节点下的数据，如果是就添加到父节点集合
                            if (questionnaireList.get(allquestionnaireList.size() - 1).getQuestions() != null) {
                                questionnaireList.get(allquestionnaireList.size() - 1).getQuestions().addAll(loadmorequestionnaireList.get(j).getQuestions());
                            } else {
                                questionnaireList.get(allquestionnaireList.size() - 1).setQuestions(loadmorequestionnaireList.get(j).getQuestions());
                            }
                        } else {
                            questionnaire = new Questionnaire();
                            questionnaire.setID(loadmorequestionnaireList.get(j).getID());
                            questionnaire.setName(loadmorequestionnaireList.get(j).getName());
                            questionnaire.setLevel(loadmorequestionnaireList.get(j).getLevel());
                            questionnaire.setQuestions(loadmorequestionnaireList.get(j).getQuestions());
                            questionnaireList.add(questionnaire);
                            questionnaire = null;
                        }
                    }
                } else {//如果是直接添加数据到集合
                    questionnaireList.addAll(loadmorequestionnaireList);
                }
                allquestionnaireList.clear();
                allquestionnaireList.addAll(questionnaireList);

                //判断是否加载完（因为要分页，所以判断是否到最后一页）
                if (questionnaires.isLastPage() == false) {//没到最后一页就继续加载
                    page = page + 1;
                    if (questionmodelstate == false) {
                        questionmodel(filledList.get(filledi).getDatasetID(), filledList.get(filledi).getID(), page);
                    } else {
                        questionmodel(folderid, 0, page);
                    }
                } else if (questionnaires.isLastPage() == true) {//已到最后一页，filledi + 1 继续加载
                    if (questionmodelstate == false) {
                        db = myDatabaseHelper.getWritableDatabase();
                        cv1 = new ContentValues();
                        cv1.put("section", gson.toJson(allquestionnaireList));
                        cv1.put("IsLastPage", questionnaires.isLastPage());
                        cv1.put("isCompleted", questionnaires.isCompleted());
                        cv1.put("DatasetID", filledList.get(filledi).getDatasetID());
                        cv1.put("RecordId", filledList.get(filledi).getID());
                        db.insert("questionresult", null, cv1);
                        db.close();
                        cv1 = null;

                        if (filledi >= (filledList.size() - 1)) {//filled所有项加载完，切换到下一个dataset的filled继续加载
                            Log.i("", "onResponse: question加载完毕 加载questionmodel");
                            page = 1;
                            questionmodelstate = true;
                            questionmodel(filledList.get(0).getDatasetID(), 0, page);//加载question模板
                        } else {
                            Log.i("", "onResponse: question加载完成 加载下一个filled filledi: " + filledi);
                            page = 1;
                            filledi = filledi + 1;
                            questionmodel(filledList.get(filledi).getDatasetID(), filledList.get(filledi).getID(), page);
                        }
                    } else {
                        db = myDatabaseHelper.getWritableDatabase();
                        cv1 = new ContentValues();
                        cv1.put("section", gson.toJson(allquestionnaireList));
                        cv1.put("IsLastPage", questionnaires.isLastPage());
                        cv1.put("isCompleted", questionnaires.isCompleted());
                        cv1.put("DatasetID", folderid);
                        cv1.put("RecordId", 0);
                        db.insert("questionmodelresult", null, cv1);
                        db.close();
                        cv1 = null;
                        Log.i("", "onResponse getQuestionmodel:  问题列表模板加载完成");
                        recordstate = false;
                        questionmodelstate = false;
                        page = 1;
                        filledi = 0;
                        closeLoadingDialog();
                    }
                    allquestionnaireList.clear();
                    questionnaireList.clear();

                }
            } else {
                if (filledi >= (filledList.size() - 1)) {//filled所有项加载完，切换到下一个dataset的filled继续加载
                    Log.i("", "onResponse: question为null 加载questionmodel");
                    page = 1;
                    questionmodelstate = true;
                    questionmodel(filledList.get(0).getDatasetID(), 0, page);//加载question模板
                } else {
                    Log.i("", "onResponse: question为null 加载下一个filled filledi: " + filledi);
                    filledi = filledi + 1;
                    page = 1;
                    questionmodel(filledList.get(filledi).getDatasetID(), filledList.get(filledi).getID(), page);
                }
            }
        }
    }

    @Override
    public void getEditQuestion(EditQuestionResult result) {
        closeLoadingDialog();
        if (result.getResultCode() == 0) {
            if ((submitnum + 1) < submitsize) {
                submitnum = submitnum + 1;
                imageupload(submitnum);
            } else {
                showTip("Save success!");
                initFilled(mFolderidMap.get(level));
                db = myDatabaseHelper.getWritableDatabase();
                myDatabaseHelper.Deletesubmitquestion(db);
                db.close();
                editor.putBoolean(Preferences.ISSUBMIT, false);
            }
        } else if (result.getResultCode() == 1) {
            if ((submitnum + 1) < submitsize) {
                submitnum = submitnum + 1;
                imageupload(submitnum);
            } else {
                showTip("Save success!");
                initFilled(mFolderidMap.get(level));
                db = myDatabaseHelper.getWritableDatabase();
                myDatabaseHelper.Deletesubmitquestion(db);
                db.close();
                editor.putBoolean(Preferences.ISSUBMIT, false);
            }
        } else if (result.getResultCode() == -1) {
            showTip(result.getResultText());
        } else {
            if ((submitnum + 1) < submitsize) {
                submitnum = submitnum + 1;
                imageupload(submitnum);
            } else {
                showTip("Save success!");
                initFilled(mFolderidMap.get(level));
                db = myDatabaseHelper.getWritableDatabase();
                myDatabaseHelper.Deletesubmitquestion(db);
                db.close();
                editor.putBoolean(Preferences.ISSUBMIT, false);
            }
        }
    }

    //刷新
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsDirMap.get(level) == true) {
                    if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取dataset
                        initdataset(folderid);
                    } else {//无网络获取本地dataset
                        locadataset(folderid);
                    }
                } else {
                    if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取filled
                        initFilled(folderid);
                    } else {//无网络获取本地filled
                        locafilled(folderid);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);// 加载完数据设置为不刷新状态，将下拉进度收起来
            }
        }, 1000);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (level > 1) {
                folderid = mFolderidMap.get(level - 1);
                level = level - 1;
                if (level > 1) {
                    imgBack.setVisibility(View.VISIBLE);
                    imgAdd.setVisibility(View.GONE);
                    textTab.setText(mTitleMap.get(level));
                } else {
                    imgBack.setVisibility(View.GONE);
                    imgAdd.setVisibility(View.GONE);
                    textTab.setText(mTitleMap.get(level));
                }
                if (AppUtil.isNetworkAvailable(mContext)) {//有网络获取dataset
                    showLoadingDialog();
                    initdataset(folderid);
                } else {//无网络获取本地dataset
                    locadataset(folderid);
                }
            } else {
                appManager.AppExit();
//                appManager.destory(this);
                unregisterReceiver(networkChangeReceiver);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //获取权限，创建文件夹（存储图片）
    private void text() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int has = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (has != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                return;
            }
        }
        Other.createPath("/storage/emulated/0/Android/data/com.example.vcserver.iqapture/");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意
                text();
            } else {
                text();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT://竖屏
                Log.i("", "竖屏");
                onRefresh();
                break;
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                Log.i("", "横屏");
                onRefresh();
            default:
                break;
        }
    }

    private class ImageShow extends AsyncTask<String, String, String> {
        String imageurl;
        int i;

        public ImageShow(String string, int i) {
            this.imageurl = string;
            this.i = i;
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = null;
            try {
                body = new FormBody.Builder()
                        .add("UserID", String.valueOf(userid))
                        .add("Image", Other.encodeBase64File(imageurl))//Other.bitmaptoString(getSDCardImg(imageurl))
                        .add("FileName", Other.getFileName(imageurl))
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Request request = new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //返回的String字符串无法进行路径拼接(需要和后台接口商量修改)
            StringBuffer sb = new StringBuffer();//进行拼接的字段
            sb.append(s + "@" + Other.getImageFileName(imageurl) + ",");
            if (!allquestionlist.get(i).getAnswer().contains("@")) {
                allquestionlist.get(i).setAnswer("");
                allquestionlist.get(i).setAnswer(String.valueOf(sb));
            } else {
                allquestionlist.get(i).setAnswer(allquestionlist.get(i).getAnswer() + String.valueOf(sb));
            }

            allnumber = 0;

            for (int l = 0; l < allquestionlist.size(); l++) {
                if (allquestionlist.get(l).getType() == 12) {
                    if (!TextUtils.isEmpty(allquestionlist.get(l).getAnswer()) && allquestionlist.get(l).getAnswer() != "") {
                        allnumber++;
                        String[] selectpos = allquestionlist.get(i).getAnswer().split("[,]");
                        if (selectpos.length == allselectpos) {
                            if (number == allnumber) {
                                SubmitQuestion(allquestionlist);
                            }
                        }
                    }
                }
            }
        }
    }

    private void SubmitQuestion(List<Questionnaire.Question> editquestion) {
        //要提交的集合
        List<SubmitQuestion.QuestionValue> questionValueList = new ArrayList<>();
        SubmitQuestion.QuestionValue questionValues;
        SubmitQuestion.ActivityValue activity;
        for (int i = 0; i < editquestion.size(); i++) {
            if (editquestion.get(i).getType() == 29) {
                if (editquestion.get(i).getActivity() != null) {
                    activity = new SubmitQuestion.ActivityValue();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// HH:mm:ss
                    // 获取当前时间
                    Date date = new Date(System.currentTimeMillis());
                    activity.setActivityID(editquestion.get(i).getActivity().getActivityID());
                    activity.setTargetDate(!TextUtils.isEmpty(editquestion.get(i).getActivity().getTargetDate()) ? editquestion.get(i).getActivity().getTargetDate() : simpleDateFormat.format(date));//Other.timedate(Other.getTimeStamp(allquestionlist.get(i).getActivity().getTargetDate(), "yyyy/MM/dd"), "yyyy-MM-dd")
                    activity.setUserIds(!TextUtils.isEmpty(editquestion.get(i).getActivity().getUserIds()) ? editquestion.get(i).getActivity().getUserIds() : String.valueOf(userid));
                    activity.setDescription(!TextUtils.isEmpty(editquestion.get(i).getActivity().getDescription()) ? editquestion.get(i).getActivity().getDescription() : "");
                    activity.setCommentID(editquestion.get(i).getActivity().getCommentID());
                    activity.setComment(!TextUtils.isEmpty(editquestion.get(i).getActivity().getComment()) ? editquestion.get(i).getActivity().getComment() : "");
                } else {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// HH:mm:ss
                    // 获取当前时间
                    Date date = new Date(System.currentTimeMillis());
                    //新建的问卷activity为null时要如何填写activity数据提交
                    activity = new SubmitQuestion.ActivityValue();
                    activity.setActivityID(0);
                    activity.setTargetDate(simpleDateFormat.format(date));//获取当天时间  dd/mm/yyyy
                    activity.setUserIds(String.valueOf(userid));
                    activity.setDescription("");
                    activity.setComment("");
                }
                questionValues = new SubmitQuestion.QuestionValue();
                questionValues.setID(editquestion.get(i).getID());
                questionValues.setType(editquestion.get(i).getType());
                questionValues.setAnswer(editquestion.get(i).getAnswer());
                questionValues.setActivity(activity);
                questionValueList.add(questionValues);
            } else if (editquestion.get(i).getType() != 29 && editquestion.get(i).getType() != 27) {
                questionValues = new SubmitQuestion.QuestionValue();
                questionValues.setID(editquestion.get(i).getID());
                questionValues.setType(editquestion.get(i).getType());
                if (editquestion.get(i).getType() == 25) {
                    questionValues.setAnswer(editquestion.get(i).getAnswer());
                    questionValues.setHelpAnswer(editquestion.get(i).getHelpAnswer());
                    questionValueList.add(questionValues);
                } else if (editquestion.get(i).getType() == 31) {
                    questionValues.setAnswer(editquestion.get(i).getAnswer());
                    questionValueList.add(questionValues);
                    if (editquestion.get(i).getChildrens() != null) {
                        for (int j = 0; j < editquestion.get(i).getChildrens().size(); j++) {
                            questionValues = new SubmitQuestion.QuestionValue();
                            questionValues.setID(editquestion.get(i).getChildrens().get(j).getID());
                            questionValues.setType(editquestion.get(i).getChildrens().get(j).getType());
                            List<SubmitQuestion.ExtendValue> extendValueList = new ArrayList<>();
                            if (editquestion.get(i).getChildrens().get(j).getExtendVals() != null) {
                                for (int k = 0; k < editquestion.get(i).getChildrens().get(j).getExtendVals().size(); k++) {
                                    SubmitQuestion.ExtendValue extendValue = new SubmitQuestion.ExtendValue();
                                    extendValue.setValue(editquestion.get(i).getChildrens().get(j).getExtendVals().get(k));
                                    if (editquestion.get(i).getChildrens().get(j).getType() == 25) {
                                        if (!TextUtils.isEmpty(editquestion.get(i).getChildrens().get(j).getExtendVals().get(k))) {
                                            String[] selectpos = editquestion.get(i).getChildrens().get(j).getExtendVals().get(k).split("[/]");
                                            String str2 = Other.Timetransformation(Integer.valueOf(selectpos[0].substring(1, selectpos[0].length())) + 1, Integer.valueOf(selectpos[1].substring(1, selectpos[1].length())) + 1, Integer.valueOf(selectpos[2].substring(1, selectpos[2].length())) + 1900);
                                            extendValue.setHelpValue(str2);
                                        } else {
                                            extendValue.setHelpValue("");
                                        }
                                    } else {
                                        extendValue.setHelpValue(editquestion.get(i).getChildrens().get(j).getExtendVals().get(k));
                                    }
                                    extendValueList.add(extendValue);
                                }
                            }
                            questionValues.setExtendVals(extendValueList);
                            if (editquestion.get(i).getChildrens().get(j).getType() == 25) {
                                questionValues.setAnswer(editquestion.get(i).getChildrens().get(j).getAnswer());
                                questionValues.setHelpAnswer(editquestion.get(i).getChildrens().get(j).getHelpAnswer());
                            } else {
                                questionValues.setAnswer(editquestion.get(i).getChildrens().get(j).getAnswer());
                            }
                            questionValueList.add(questionValues);
                        }
                    }
                } else {
                    questionValues.setAnswer(editquestion.get(i).getAnswer());
                    questionValueList.add(questionValues);
                }
            }
        }
        //提交接口
        submitQuestion = new SubmitQuestion();
        submitQuestion.setCompanyID(companyid);
        submitQuestion.setUserID(userid);
        submitQuestion.setCompleted(isCompleted);
        submitQuestion.setDatasetID(datasetId);
        submitQuestion.setRecordID(filledId);
        submitQuestion.setFolderID(ParentFolderID);
        submitQuestion.setValues(questionValueList);

        editor.putBoolean(Preferences.ISSUBMIT, true);

        editQuestionApi = new EditQuestionApi();
        editQuestionApi.setQuestion(gson.toJson(submitQuestion));
        mPresenter.startPost(this, editQuestionApi);//新建问卷filledId=0,也会保存失败，需要和后台协商
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Other.deleteFile(new File("/storage/emulated/0/Android/data/com.example.vcserver.iqapture/"));
        unregisterReceiver(networkChangeReceiver);
    }
}
