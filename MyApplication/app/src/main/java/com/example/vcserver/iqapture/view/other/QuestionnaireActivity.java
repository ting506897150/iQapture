package com.example.vcserver.iqapture.view.other;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.bean.Questionnaires;
import com.example.vcserver.iqapture.bean.SubmitQuestion;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionApi;
import com.example.vcserver.iqapture.bean.dataset.EditQuestionResult;
import com.example.vcserver.iqapture.bean.dataset.QuestionnaireApi;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.presenter.other.QuestionPresenter;
import com.example.vcserver.iqapture.util.FullyLinearLayoutManager;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.adapter.RecyclerViewAdapter;
import com.example.vcserver.iqapture.view.base.BaseActivity;
import com.example.vcserver.iqapture.view.other.view.IQuestionView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by VCServer on 2018/3/27.
 */

public class QuestionnaireActivity extends BaseActivity<QuestionPresenter> implements IQuestionView, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.text_tab)
    TextView textTab;
    @Bind(R.id.recyclerview_item)
    public RecyclerView recyclerviewItem;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.img_complete)
    ImageView imgComplete;
    @Bind(R.id.img_save)
    ImageView imgSave;

    private String title,addoredit,CreateTime;
    private int filledId;
    private int ParentFolderID;
    public int datasetId;
    private boolean compeleted;
    public int userid, companyid;//用户id，公司id

    private int page = 1;
    private int lastVisibleItem = 0;

    FullyLinearLayoutManager linearLayoutManager;

    private QuestionnaireApi questionnaireApi;
    private EditQuestionApi editQuestionApi;
    SubmitQuestion submitQuestion;
    private List<Questionnaire> questionnaireList = new ArrayList<>();//获取的所有问卷（用来循环判断）
    private List<Questionnaire.Question> editquestion;
    private List<Questionnaire> loadmorequestionnaireList = new ArrayList<>();//每次加载的问卷
    private List<Questionnaire> allquestionnaireList = new ArrayList<>();//获取的所有问卷（用来加载）
    private List<Questionnaire.Question> allquestionlist = new ArrayList<>();//传入adapter适配器的集合

    List<Questionnaire.Question> questionList;//存储节点 leave1
    List<Questionnaire.Question> leave2;//存储节点 leave2
    List<Questionnaire.Question> leave3;//存储节点 leave3
    List<Questionnaire.Question> leave4;//存储节点 leave4
    List<Questionnaire.Question> leave5;//存储节点 leave5

    List<Questionnaire.Question> leaves1 = new ArrayList<>();//存储用来放在leave1节点下的子节点 leave2
    List<Questionnaire.Question> leaves2 = new ArrayList<>();//存储用来放在leave2节点下的子节点 leave3
    List<Questionnaire.Question> leaves3 = new ArrayList<>();//存储用来放在leave3节点下的子节点 leave4
    List<Questionnaire.Question> leaves4 = new ArrayList<>();//存储用来放在leave4节点下的子节点 leave5

    Questionnaire.Question question1;//存储节点 leave1
    Questionnaire.Question question2;//存储节点 leave2
    Questionnaire.Question question3;//存储节点 leave3
    Questionnaire.Question question4;//存储节点 leave4
    Questionnaire.Question question5;//存储节点 leave5
    Questionnaire.Question question;
    Gson gson = new Gson();
    ImageShow imageShow;

    int number = 0; //计算一共循环了多少个图片类型
    int allnumber = 0; //计算一共上传了多少个图片类型
    int uploadnum = 0; //计算一共循环上传了多少个图片
    int allselectpos = 0; //每个图片类型有多少个图片（已有的+要上传的）

    boolean state = false;//判断是否全部加载
    boolean IsLastPage = false;//判断是否加载到最后一页

    RecyclerViewAdapter recyclerViewAdapter;
    boolean isSlidingToLast = false;

    private String questionlist;//接收本地问题列表json字符串
    Handler handler = new Handler();

    private IntentFilter intentFilter;
    private NetworkChangeReceiverQuestion networkChangeReceiver;

    private AlertDialog mDialog;

    private boolean isState = true;//判断是否有必填项未填
    String sql;

    public DisplayMetrics dm;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_questionnaire);
    }

    @Override
    protected void initPresenter() {
        mPresenter = new QuestionPresenter(mContext, this);
    }

    @Override
    protected void init() {

        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        title = getIntent().getStringExtra("title");
        addoredit = getIntent().getStringExtra("addoredit");
        CreateTime = getIntent().getStringExtra("CreateTime");
        filledId = getIntent().getIntExtra("filledId", 0);
        ParentFolderID = getIntent().getIntExtra("ParentFolderID", 0);
        datasetId = getIntent().getIntExtra("datasetId", 0);
        compeleted = getIntent().getBooleanExtra("compeleted", false);
        if (compeleted == true){//完成状态
            imgComplete.setVisibility(View.INVISIBLE);
            imgSave.setVisibility(View.INVISIBLE);
        }
        textTab.setText(title);
        userid = editor.getInt(Preferences.USERID, 0);
        companyid = editor.getInt(Preferences.COMPANYID, 0);

        //设置刷新时动画的颜色，可以设置4个
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            swipeRefreshLayout.setOnRefreshListener(this);
            recyclerviewItem.setEnabled(false);
        }

//        LoadMore();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiverQuestion();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    class NetworkChangeReceiverQuestion extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AppUtil.isNetworkAvailable(QuestionnaireActivity.this)){
                page = 1;
                state = false;
                allquestionlist.clear();
                questionnaireList.clear();
                allquestionnaireList.clear();
                showLoadingDialog();
                initquestion(page);
                LoadMore();
            }else{
                if (addoredit.equals("add")){
                    db = myDatabaseHelper.getWritableDatabase();
                    //取出问题列表
                    sql = "select * from questionmodelresult where DatasetID = ? and RecordId = ?";
                    Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(datasetId),String.valueOf(filledId)});
                    if (cursor.getCount() > 0){
                        allquestionlist.clear();
                        allquestionnaireList.clear();
                        while(cursor.moveToNext()){
                            questionlist = cursor.getString(cursor.getColumnIndex("section"));
                            SharedPreferencesUtil.getsInstances(QuestionnaireActivity.this).putBoolean(Preferences.ISCOMPLETED,cursor.getString(cursor.getColumnIndex("isCompleted")).equals("1")?true:false);
                        }
                        //转化
                        Type type = new TypeToken<List<Questionnaire>>(){ }.getType();
                        allquestionnaireList = gson.fromJson(questionlist, type);
                        cursor.close();
                        db.close();
                        //加载数据
                        loaddata();
                    }else{
                        adapter();
                    }
                }else if (addoredit.equals("edit")){
                    locaquestion();
                }
            }
        }
    }

    private void adapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(this, R.layout.item_type_recyclerview, allquestionlist);
        linearLayoutManager = new FullyLinearLayoutManager(mContext);
        recyclerviewItem.setLayoutManager(linearLayoutManager);
        recyclerviewItem.setAdapter(recyclerViewAdapter);
        recyclerviewItem.smoothScrollToPosition(lastVisibleItem);
        recyclerviewItem.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override	public void onGlobalLayout() {
                closeLoadingDialog();
            }
        });
        recyclerViewAdapter.setOnGridItemClickListener(new RecyclerViewAdapter.OnGridItemClickListener() {
            @Override
            public void onGridItemceshiClick(int position) {
                editquestion = new ArrayList<>();
                editquestion.addAll(recyclerViewAdapter.resultObjects());//填写修改后的数据集合
                allquestionlist.clear();
                allquestionlist.addAll(editquestion);
                for (int i = 0; i < allquestionlist.size(); i++) {
                    allquestionlist.get(i).setIschecked(false);
                    allquestionlist.get(i).setIschildshow(false);
                }

                //节点的展开收缩
                if (allquestionlist.get(position).isIsshow()) {
                    allquestionlist.get(position).setIsshow(false);
                } else {
                    allquestionlist.get(position).setIsshow(true);
                }

//                //activity的展开收缩
//                if (allquestionlist.get(position).isActivityshow()) {
//                    allquestionlist.get(position).setActivityshow(false);
//                } else {
//                    allquestionlist.get(position).setActivityshow(true);
//                }
                allquestionlist.get(position).setIschecked(true);
                showLoadingDialog();
                //更新数据
                updatalist();
            }
        });
    }

    private void updatalist() {
        if (recyclerviewItem.isComputingLayout()) {
            // 延时递归处理。
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //要执行的操作
                    updatalist();
                }
            }, 1000);
        }else{
            recyclerViewAdapter.setData(allquestionlist);
            closeLoadingDialog();
        }
    }

    private void locaquestion() {
        db = myDatabaseHelper.getWritableDatabase();
        //取出问题列表
        sql = "select * from submitquestion where DatasetID = ? and CreateTime = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(datasetId),String.valueOf(Other.getTimeStamp(CreateTime,"dd/MM/yyyy HH:mm:ss"))});
        if (cursor.getCount() > 0){
            allquestionlist.clear();
            allquestionnaireList.clear();
            while(cursor.moveToNext()){
                questionlist = cursor.getString(cursor.getColumnIndex("questionjson"));
                SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,cursor.getString(cursor.getColumnIndex("isCompleted")).equals("1")?true:false);
            }
            //转化
            Type type = new TypeToken<List<Questionnaire.Question>>(){ }.getType();
//            allquestionnaireList = gson.fromJson(questionlist, type);
            allquestionlist = gson.fromJson(questionlist, type);
            cursor.close();
            db.close();
            //加载数据
//            loaddata();
            //获取本地存储的数据加载到适配器
            IsLastPage = true;
            adapter();
        }else{
            adapter();
        }
    }

    private void loaddata() {
        for (int i = 0; i < allquestionnaireList.size(); i++) {
            if (allquestionnaireList.get(i).getLevel() == 0) {
                allquestionlist.addAll(allquestionnaireList.get(i).getQuestions());
            } else if (allquestionnaireList.get(i).getLevel() == 1){
                leaves1 = new ArrayList<>();
                questionList = new ArrayList<>();
                question1 = new Questionnaire.Question();
                question1.setType(27);
                question1.setIsshow(true);
                question1.setLevel(allquestionnaireList.get(i).getLevel());
                question1.setTitle(allquestionnaireList.get(i).getName());
                question1.setChildrens(allquestionnaireList.get(i).getQuestions());
                questionList.add(question1);
                allquestionlist.addAll(allquestionlist.size(),questionList);
                if (allquestionnaireList.get(i).getQuestions() != null){
                    allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                    leaves1.addAll(allquestionnaireList.get(i).getQuestions());
                }else{
                    if (allquestionnaireList.get(i+1).getLevel() == 2){
                        leave2 = new ArrayList<>();
                        question2 = new Questionnaire.Question();
                        question2.setType(27);
                        question2.setIsshow(true);
                        question2.setLevel(allquestionnaireList.get(i+1).getLevel());
                        question2.setTitle(allquestionnaireList.get(i+1).getName());
                        question2.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                        leave2.add(question2);
                        question1.setChildrens(leave2);
                    }
                }
            }else if (allquestionnaireList.get(i).getLevel() == 2){
                leaves2 = new ArrayList<>();
                leave2 = new ArrayList<>();
                question2 = new Questionnaire.Question();
                question2.setType(27);
                question2.setIsshow(true);
                question2.setLevel(allquestionnaireList.get(i).getLevel());
                question2.setTitle(allquestionnaireList.get(i).getName());
                question2.setChildrens(allquestionnaireList.get(i).getQuestions());
                leave2.add(question2);
                leaves1.add(question2);
                allquestionlist.addAll(allquestionlist.size(),leave2);
                if (allquestionnaireList.get(i).getQuestions() != null){
                    allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                    leaves2.addAll(allquestionnaireList.get(i).getQuestions());
                }else{
                    if (allquestionnaireList.get(i+1).getLevel() == 3){
                        leave3 = new ArrayList<>();
                        question3 = new Questionnaire.Question();
                        question3.setType(27);
                        question3.setIsshow(true);
                        question3.setLevel(allquestionnaireList.get(i+1).getLevel());
                        question3.setTitle(allquestionnaireList.get(i+1).getName());
                        question3.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                        leave3.add(question3);
                        question2.setChildrens(leave3);
                    }
                }
            }else if (allquestionnaireList.get(i).getLevel() == 3){
                leaves3 = new ArrayList<>();
                leave3 = new ArrayList<>();
                question3 = new Questionnaire.Question();
                question3.setType(27);
                question3.setIsshow(true);
                question3.setLevel(allquestionnaireList.get(i).getLevel());
                question3.setTitle(allquestionnaireList.get(i).getName());
                question3.setChildrens(allquestionnaireList.get(i).getQuestions());
                leave3.add(question3);
                leaves2.add(question3);
                allquestionlist.addAll(allquestionlist.size(),leave3);
                if (allquestionnaireList.get(i).getQuestions() != null){
                    allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                    leaves3.addAll(allquestionnaireList.get(i).getQuestions());
                }else{
                    if (allquestionnaireList.get(i+1).getLevel() == 4){
                        leave4 = new ArrayList<>();
                        question4 = new Questionnaire.Question();
                        question4.setType(27);
                        question4.setIsshow(true);
                        question4.setLevel(allquestionnaireList.get(i+1).getLevel());
                        question4.setTitle(allquestionnaireList.get(i+1).getName());
                        question4.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                        leave4.add(question4);
                        question3.setChildrens(leave4);
                    }
                }
            }else if (allquestionnaireList.get(i).getLevel() == 4){
                leaves4 = new ArrayList<>();
                leave4 = new ArrayList<>();
                question4 = new Questionnaire.Question();
                question4.setType(27);
                question4.setIsshow(true);
                question4.setLevel(allquestionnaireList.get(i).getLevel());
                question4.setTitle(allquestionnaireList.get(i).getName());
                question4.setChildrens(allquestionnaireList.get(i).getQuestions());
                leave4.add(question4);
                leaves3.add(question4);
                allquestionlist.addAll(allquestionlist.size(),leave4);
                if (allquestionnaireList.get(i).getQuestions() != null){
                    allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                    leaves4.addAll(allquestionnaireList.get(i).getQuestions());
                }else{
                    if (allquestionnaireList.get(i+1).getLevel() == 5){
                        leave5 = new ArrayList<>();
                        question5 = new Questionnaire.Question();
                        question5.setType(27);
                        question5.setIsshow(true);
                        question5.setLevel(allquestionnaireList.get(i+1).getLevel());
                        question5.setTitle(allquestionnaireList.get(i+1).getName());
                        question5.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                        leave5.add(question5);
                        question4.setChildrens(leave5);
                    }
                }
            }else if (allquestionnaireList.get(i).getLevel() == 5){
                leave5 = new ArrayList<>();
                question5 = new Questionnaire.Question();
                question5.setType(27);
                question5.setIsshow(true);
                question5.setLevel(allquestionnaireList.get(i).getLevel());
                question5.setTitle(allquestionnaireList.get(i).getName());
                question5.setChildrens(allquestionnaireList.get(i).getQuestions());
                leave5.add(question5);
                leaves4.add(question5);
                allquestionlist.addAll(allquestionlist.size(),leave5);
                if (allquestionnaireList.get(i).getQuestions() != null){
                    allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                }
            }
        }

        if (leaves1 != null && leaves1.size() > 0)
            question1.setChildrens(leaves1);//leave1的子项数据
        if (leaves2 != null && leaves2.size() > 0)
            question2.setChildrens(leaves2);//leave2的子项数据
        if (leaves3 != null && leaves3.size() > 0)
            question3.setChildrens(leaves3);//leave3的子项数据
        if (leaves4 != null && leaves4.size() > 0)
            question4.setChildrens(leaves4);//leave4的子项数据
        //获取本地存储的数据加载到适配器
        IsLastPage = true;
        adapter();
    }


    private void initquestion(int page) {
        questionnaireApi = new QuestionnaireApi();
        questionnaireApi.setUserId(userid);
        questionnaireApi.setCompanyId(companyid);
        questionnaireApi.setDatasetId(datasetId);
        questionnaireApi.setRecordId(filledId);
        questionnaireApi.setPage(page);
        questionnaireApi.setRow(10);
        mPresenter.startPost(this, questionnaireApi);
    }

    //刷新
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //网络判断
                if (AppUtil.isNetworkAvailable(QuestionnaireActivity.this)) {
                    page = 1;
                    state = false;
                    questionnaireList.clear();
                    allquestionnaireList.clear();
                    initquestion(page);
                    swipeRefreshLayout.setRefreshing(false);// 加载完数据设置为不刷新状态，将下拉进度收起来
                } else {
                    swipeRefreshLayout.setRefreshing(false);//结束刷新状态
                    showTip("Please check the Internet connection!");
                }
            }
        }, 1000);
    }

    //加载更多
    public void LoadMore() {
        recyclerviewItem.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();//当前item位置
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == linearLayoutManager.getItemCount() && isSlidingToLast) {// && lastVisibleItem + 1 == linearLayoutManager.getItemCount()
                    if (IsLastPage == false) {
                        page = page + 1;
                        questionList = new ArrayList<>();
                        leaves1 = new ArrayList<>();
                        leave2 = new ArrayList<>();
                        leaves2 = new ArrayList<>();
                        leave3 = new ArrayList<>();
                        leaves3 = new ArrayList<>();
                        leave4 = new ArrayList<>();
                        leaves4 = new ArrayList<>();
                        leave5 = new ArrayList<>();
                        showLoadingDialog();
                        initquestion(page);
                    }else{
//                        showTip("The data is all loaded!");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    //大于0表示正在向上滚动
                    isSlidingToLast = true;
                } else {
                    //小于等于0表示停止或向下滚动
                    isSlidingToLast = false;
                }
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();//当前item位置
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
    }

    @Override
    public void getQuestion(Questionnaires result) {
        if (result.getSection() != null){
            IsLastPage = result.isLastPage();


        SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,result.isCompleted());
        loadmorequestionnaireList.clear();
        loadmorequestionnaireList.addAll(result.getSection());
        //加载父节点
        if (allquestionnaireList != null && allquestionnaireList.size() != 0) {//判断是不是第一次加载数据，如果不是则进入循环对比数据
            for (int j = 0; j < loadmorequestionnaireList.size(); j++) {
                if (allquestionnaireList.get(allquestionnaireList.size() - 1).getID() == loadmorequestionnaireList.get(j).getID()) {//判断新增数据是不是父节点下的数据，如果是就添加到父节点集合
                    if (questionnaireList.get(allquestionnaireList.size() - 1).getQuestions() != null){
                        questionnaireList.get(allquestionnaireList.size() - 1).getQuestions().addAll(loadmorequestionnaireList.get(j).getQuestions());
                    }else{
                        questionnaireList.get(allquestionnaireList.size() - 1).setQuestions(loadmorequestionnaireList.get(j).getQuestions());
                    }
                } else {
                    Questionnaire questionnaire = new Questionnaire();
                    questionnaire.setID(loadmorequestionnaireList.get(j).getID());
                    questionnaire.setName(loadmorequestionnaireList.get(j).getName());
                    questionnaire.setLevel(loadmorequestionnaireList.get(j).getLevel());
                    questionnaire.setQuestions(loadmorequestionnaireList.get(j).getQuestions());
                    questionnaireList.add(questionnaire);
                }
            }
        } else {//如果是直接添加数据到集合
            questionnaireList.addAll(loadmorequestionnaireList);
        }
        allquestionlist.clear();
        allquestionnaireList.clear();
        allquestionnaireList.addAll(questionnaireList);

        if (state == false) {//判断是否是全部加载
            for (int i = 0; i < allquestionnaireList.size(); i++) {
                if (allquestionnaireList.get(i).getLevel() == 0) {
                    allquestionlist.addAll(allquestionnaireList.get(i).getQuestions());
                } else if (allquestionnaireList.get(i).getLevel() == 1){
                    leaves1 = new ArrayList<>();
                    questionList = new ArrayList<>();
                    question1 = new Questionnaire.Question();
                    question1.setType(27);
                    question1.setIsshow(true);
                    question1.setLevel(allquestionnaireList.get(i).getLevel());
                    question1.setTitle(allquestionnaireList.get(i).getName());
                    question1.setChildrens(allquestionnaireList.get(i).getQuestions());
                    questionList.add(question1);
                    allquestionlist.addAll(allquestionlist.size(),questionList);
                    if (allquestionnaireList.get(i).getQuestions() != null){
                        allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                        leaves1.addAll(allquestionnaireList.get(i).getQuestions());
                    }else{
                        if (i + 1 < allquestionnaireList.size()){
                            if (allquestionnaireList.get(i+1).getLevel() == 2){
                                leave2 = new ArrayList<>();
                                question2 = new Questionnaire.Question();
                                question2.setType(27);
                                question2.setIsshow(true);
                                question2.setLevel(allquestionnaireList.get(i+1).getLevel());
                                question2.setTitle(allquestionnaireList.get(i+1).getName());
                                question2.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                                leave2.add(question2);
                                question1.setChildrens(leave2);
                            }
                        }
                    }
                }else if (allquestionnaireList.get(i).getLevel() == 2){
                    leaves2 = new ArrayList<>();
                    leave2 = new ArrayList<>();
                    question2 = new Questionnaire.Question();
                    question2.setType(27);
                    question2.setIsshow(true);
                    question2.setLevel(allquestionnaireList.get(i).getLevel());
                    question2.setTitle(allquestionnaireList.get(i).getName());
                    question2.setChildrens(allquestionnaireList.get(i).getQuestions());
                    leave2.add(question2);
                    leaves1.add(question2);
                    allquestionlist.addAll(allquestionlist.size(),leave2);
                    if (allquestionnaireList.get(i).getQuestions() != null){
                        allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                        leaves2.addAll(allquestionnaireList.get(i).getQuestions());
                    }else{
                        if (i + 1 < allquestionnaireList.size()){
                            if (allquestionnaireList.get(i+1).getLevel() == 3){
                                leave3 = new ArrayList<>();
                                question3 = new Questionnaire.Question();
                                question3.setType(27);
                                question3.setIsshow(true);
                                question3.setLevel(allquestionnaireList.get(i+1).getLevel());
                                question3.setTitle(allquestionnaireList.get(i+1).getName());
                                question3.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                                leave3.add(question3);
                                question2.setChildrens(leave3);
                            }
                        }
                    }
                }else if (allquestionnaireList.get(i).getLevel() == 3){
                    leaves3 = new ArrayList<>();
                    leave3 = new ArrayList<>();
                    question3 = new Questionnaire.Question();
                    question3.setType(27);
                    question3.setIsshow(true);
                    question3.setLevel(allquestionnaireList.get(i).getLevel());
                    question3.setTitle(allquestionnaireList.get(i).getName());
                    question3.setChildrens(allquestionnaireList.get(i).getQuestions());
                    leave3.add(question3);
                    leaves2.add(question3);
                    allquestionlist.addAll(allquestionlist.size(),leave3);
                    if (allquestionnaireList.get(i).getQuestions() != null){
                        allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                        leaves3.addAll(allquestionnaireList.get(i).getQuestions());
                    }else{
                        if (i + 1 < allquestionnaireList.size()){
                            if (allquestionnaireList.get(i+1).getLevel() == 4){
                                leave4 = new ArrayList<>();
                                question4 = new Questionnaire.Question();
                                question4.setType(27);
                                question4.setIsshow(true);
                                question4.setLevel(allquestionnaireList.get(i+1).getLevel());
                                question4.setTitle(allquestionnaireList.get(i+1).getName());
                                question4.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                                leave4.add(question4);
                                question3.setChildrens(leave4);
                            }
                        }
                    }
                }else if (allquestionnaireList.get(i).getLevel() == 4){
                    leaves4 = new ArrayList<>();
                    leave4 = new ArrayList<>();
                    question4 = new Questionnaire.Question();
                    question4.setType(27);
                    question4.setIsshow(true);
                    question4.setLevel(allquestionnaireList.get(i).getLevel());
                    question4.setTitle(allquestionnaireList.get(i).getName());
                    question4.setChildrens(allquestionnaireList.get(i).getQuestions());
                    leave4.add(question4);
                    leaves3.add(question4);
                    allquestionlist.addAll(allquestionlist.size(),leave4);
                    if (allquestionnaireList.get(i).getQuestions() != null){
                        allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                        leaves4.addAll(allquestionnaireList.get(i).getQuestions());
                    }else{
                        if (i + 1 < allquestionnaireList.size()){
                            if (allquestionnaireList.get(i+1).getLevel() == 5){
                                leave5 = new ArrayList<>();
                                question5 = new Questionnaire.Question();
                                question5.setType(27);
                                question5.setIsshow(true);
                                question5.setLevel(allquestionnaireList.get(i+1).getLevel());
                                question5.setTitle(allquestionnaireList.get(i+1).getName());
                                question5.setChildrens(allquestionnaireList.get(i+1).getQuestions());
                                leave5.add(question5);
                                question4.setChildrens(leave5);
                            }
                        }
                    }
                }else if (allquestionnaireList.get(i).getLevel() == 5){
                    leave5 = new ArrayList<>();
                    question5 = new Questionnaire.Question();
                    question5.setType(27);
                    question5.setIsshow(true);
                    question5.setLevel(allquestionnaireList.get(i).getLevel());
                    question5.setTitle(allquestionnaireList.get(i).getName());
                    question5.setChildrens(allquestionnaireList.get(i).getQuestions());
                    leave5.add(question5);
                    leaves4.add(question5);
                    allquestionlist.addAll(allquestionlist.size(),leave5);
                    if (allquestionnaireList.get(i).getQuestions() != null){
                        allquestionlist.addAll(allquestionlist.size(),allquestionnaireList.get(i).getQuestions());
                    }
                }
            }

            if (leaves1 != null && leaves1.size() > 0)
                question1.setChildrens(leaves1);//leave1的子项数据
            if (leaves2 != null && leaves2.size() > 0)
                question2.setChildrens(leaves2);//leave2的子项数据
            if (leaves3 != null && leaves3.size() > 0)
                question3.setChildrens(leaves3);//leave3的子项数据
            if (leaves4 != null && leaves4.size() > 0)
                question4.setChildrens(leaves4);//leave4的子项数据
            for (int i = 0; i < allquestionlist.size(); i++) {
                allquestionlist.get(i).setComment(true);
                allquestionlist.get(i).setIsderivativeshow(false);
            }
            adapter();
        } else {
//            for (int i = 0; i < questionnaireList.size(); i++) {
//                for (int j = 0; j < questionnaireList.get(i).getQuestions().size(); j++) {
//                    questionList = new ArrayList<>();
//                    question = new Questionnaire.Question();
//                    question.setID(questionnaireList.get(i).getQuestions().get(j).getID());
//                    question.setType(questionnaireList.get(i).getQuestions().get(j).getType());
//                    question.setAnswer(questionnaireList.get(i).getQuestions().get(j).getAnswer());
////                    question.setActivity(questionnaireList.get(i).getQuestions().get(j).getActivity());
//                    questionList.add(question);
//                    allquestionlist.addAll(questionList);
//                    if (questionnaireList.get(i).getQuestions().get(j).getChildrens() != null && questionnaireList.get(i).getQuestions().get(j).getChildrens().size() != 0 && questionnaireList.get(i).getQuestions().get(j).getType() != 31) {
//                        for (int k = 0; k < questionnaireList.get(i).getQuestions().get(j).getChildrens().size(); k++) {
//                            questionList = new ArrayList<>();
//                            question = new Questionnaire.Question();
//                            question.setID(questionnaireList.get(i).getQuestions().get(j).getChildrens().get(k).getID());
//                            question.setType(questionnaireList.get(i).getQuestions().get(j).getChildrens().get(k).getType());
//                            question.setTitle(questionnaireList.get(i).getQuestions().get(j).getChildrens().get(k).getTitle());
//                            question.setAnswer(questionnaireList.get(i).getQuestions().get(j).getChildrens().get(k).getAnswer());
////                            question.setActivity(questionnaireList.get(i).getQuestions().get(j).getChildrens().get(k).getActivity());
//                            questionList.add(question);
//                            allquestionlist.addAll(questionList);
//                        }
//                    }
//                }
//            }
//
//            for (int i = 0; i < editquestion.size(); i++) {
//                if (editquestion.get(i).getType() == 27) {
//                    for (int j = 0; j < editquestion.get(i).getChildrens().size(); j++) {
//                        allquestionlist.get(i).setAnswer(editquestion.get(i).getChildrens().get(j).getAnswer());
//                    }
//                } else {
//                    allquestionlist.get(i).setAnswer(editquestion.get(i).getAnswer());
//                }
//            }
//            imageupload();
        }
        }
    }

    private void imageupload() {
        number = 0;
        uploadnum = 0;
        allselectpos = 0;
        //上传图片
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
                            imageShow = new ImageShow(selectpos[k], i , k, selectpos.length);
                            imageShow.execute(BaseApi.getBaseUrl() + "OpenBook/IOSAPI/AddImg");
                        } else {
                            if ((k + 1) < selectpos.length){
                                sb.append(selectpos[k] + ",");
                            }else{
                                sb.append(selectpos[k]);
                            }
                            allquestionlist.get(i).setAnswer(String.valueOf(sb));
                        }
                    }
                }
            }
        }

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(QuestionnaireActivity.this);
                } else {
                    Toast.makeText(QuestionnaireActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void getEditQuestion(EditQuestionResult result) {
        closeLoadingDialog();
        if (result.getResultCode() == 0) {
            showTip("Save successfully!");
            finish();
        } else if (result.getResultCode() == 1){
            showTip("Save successfully!");
            finish();
        }else if (result.getResultCode() == -1){
            showAlertDialog(result.getResultText());
        }else{
            showTip("Save successfully!");
            finish();
        }
    }

    // 保存失败提示信息
    public void showAlertDialog(String resultText) {
        View view = View.inflate(this, R.layout.savefail, null);
        TextView title = view.findViewById(R.id.title);
        TextView positiveButton = view.findViewById(R.id.positiveButton);
        title.setText(resultText);
        mDialog = new AlertDialog.Builder(this, R.style.dialog)
                .setView(view)
                .show();
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    @Override
    public void getImageAdd(String result) {

    }

    @Override
    public void getImageShow(String result) {

    }

    @OnClick({R.id.img_back, R.id.img_save, R.id.img_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_save://保存
                SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,false);
                submit();
                break;
            case R.id.img_complete://完成
                SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,true);
                submit();
                break;
        }
    }

    private void submit() {
        isState = true;
        showLoadingDialog();
        if (addoredit.equals("add")){
            submitQuestion();
        }else{
            submitQuestion();
        }
//        if (filledId == 0) {//新建保存
//            submitQuestion();
////            editquestion = new ArrayList<>();
////            editquestion.addAll(recyclerViewAdapter.resultObjects());//填写修改后的数据集合
////            showLoadingDialog();
////            state = true;
////            questionnaireList.clear();
////            allquestionnaireList.clear();
////            initquestion(1, count);//查询所有数据  暂时不可用
//        } else {//修改保存
//            submitQuestion();
//        }
    }

    private void submitQuestion() {
        editquestion = new ArrayList<>();
        editquestion.addAll(recyclerViewAdapter.resultObjects());//填写修改后的数据集合

        //完成
        if (SharedPreferencesUtil.getsInstances(this).getBoolean(Preferences.ISCOMPLETED,false)){//判断点击的是保存还是完成 true是完成  false是保存
            for (int i = 0; i < editquestion.size(); i++) {
                if ((editquestion.get(i).isRequired() == true && editquestion.get(i).isOnlyCompleted() == true)|| (editquestion.get(i).isRequired() == true && editquestion.get(i).isOnlyCompleted() == false)){//是否有问题需要必填
                    if (TextUtils.isEmpty(editquestion.get(i).getAnswer())){
                        showTip("Mandatory question(s) is/are empty.");
                        isState = false;
                        closeLoadingDialog();
                        SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,false);
                        break;
                    }else{
                        for (int j = 0; j < editquestion.size(); j++) {
                            if (editquestion.get(j).getType() == 2){
                                if (editquestion.get(j).getAnswer().equals("")){
                                    if (editquestion.get(j).getAnswersLimitMin() > 0){
                                        showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answer(s).");
                                        isState = false;
                                        closeLoadingDialog();
                                        SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,false);
                                        break;
                                    }else{
                                        isState = true;
                                    }
                                }else{
                                    String[] selectpos = editquestion.get(j).getAnswer().split("[<|>]");
                                    if (editquestion.get(j).getAnswersLimitMin() > 0){
                                        if (selectpos.length < editquestion.get(j).getAnswersLimitMin()){
                                            showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answer(s).");
                                            isState = false;
                                            closeLoadingDialog();
                                            SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,false);
                                            break;
                                        }else{
                                            isState = true;
                                        }
                                    }else{
                                        isState = true;
                                    }
                                }
                            }
                        }
                    }
                }else{
                    if ((i + 1) == editquestion.size()){
                        //判断多选最少选择数量
                        for (int j = 0; j < editquestion.size(); j++) {
                            if (editquestion.get(j).getType() == 2){
                                if (editquestion.get(j).getAnswer().equals("")){
                                    if (editquestion.get(j).getAnswersLimitMin() > 0){
                                        showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answers.");
                                        isState = false;
                                        closeLoadingDialog();
                                        SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,false);
                                        break;
                                    }else{
                                        isState = true;
                                    }
                                }else{
                                    String[] selectpos = editquestion.get(j).getAnswer().split("[<|>]");
                                    if (editquestion.get(j).getAnswersLimitMin() > 0){
                                        if (selectpos.length < editquestion.get(j).getAnswersLimitMin()){
                                            showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answers.");
                                            isState = false;
                                            closeLoadingDialog();
                                            SharedPreferencesUtil.getsInstances(this).putBoolean(Preferences.ISCOMPLETED,false);
                                            break;
                                        }else{
                                            isState = true;
                                        }
                                    }else{
                                        isState = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (isState){
                if (AppUtil.isNetworkAvailable(this)){//有网络
                    imageupload();
                    if (uploadnum == 0) {
                        SubmitQuestion(editquestion);
                    }
                }else{//无网络存储到本地
                    storagequestion();
                }
            }
        }else{//保存
            for (int i = 0; i < editquestion.size(); i++) {
                if ((editquestion.get(i).isRequired() == true && editquestion.get(i).isOnlyCompleted() == false)){//是否有问题需要必填
                    if (TextUtils.isEmpty(editquestion.get(i).getAnswer())){
                        showTip("Mandatory question(s) is/are empty.");
                        isState = false;
                        closeLoadingDialog();
                        break;
                    }else{
                        for (int j = 0; j < editquestion.size(); j++) {
                            if (editquestion.get(j).getType() == 2){
                                if (editquestion.get(j).getAnswer().equals("")){
                                    if (editquestion.get(j).getAnswersLimitMin() > 0){
                                        showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answers.");
                                        isState = false;
                                        closeLoadingDialog();
                                        break;
                                    }else{
                                        isState = true;
                                    }
                                }else{
                                    String[] selectpos = editquestion.get(j).getAnswer().split("[<|>]");
                                    if (selectpos.length < editquestion.get(j).getAnswersLimitMin()){
                                        showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answers.");
                                        isState = false;
                                        closeLoadingDialog();
                                        break;
                                    }else{
                                        isState = true;
                                    }
                                }
                            }
                        }
                    }
                }else{
                    if ((i + 1) == editquestion.size()){
                        //判断多选最少选择数量
                        for (int j = 0; j < editquestion.size(); j++) {
                            if (editquestion.get(j).getType() == 2){
                                if (editquestion.get(j).getAnswer().equals("")){
                                    if (editquestion.get(j).getAnswersLimitMin() > 0){
                                        showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answers.");
                                        isState = false;
                                        closeLoadingDialog();
                                        break;
                                    }else{
                                        isState = true;
                                    }
                                }else{
                                    String[] selectpos = editquestion.get(j).getAnswer().split("[<|>]");
                                    if (selectpos.length < editquestion.get(j).getAnswersLimitMin()){
                                        showTip(editquestion.get(j).getNo()+"."+editquestion.get(j).getTitle()+".Sorry, please select at least "+editquestion.get(j).getAnswersLimitMin()+" answers.");
                                        isState = false;
                                        closeLoadingDialog();
                                        break;
                                    }else{
                                        isState = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (isState){
                if (AppUtil.isNetworkAvailable(this)){//有网络
                    imageupload();
                    if (uploadnum == 0) {
                        SubmitQuestion(editquestion);
                    }
                }else{//无网络存储到本地
                    storagequestion();
                }
            }
        }
    }

    private void storagequestion() {
        if (addoredit.equals("add")){
            db = myDatabaseHelper.getWritableDatabase();
            ContentValues cv1 = new ContentValues();
            cv1.put("questionjson", gson.toJson(editquestion));
            cv1.put("DatasetID", datasetId);
            cv1.put("RecordId", filledId);
            cv1.put("Creator", editor.getString(Preferences.USERNAMES, ""));
            cv1.put("ParentFolderID", ParentFolderID);
            cv1.put("isCompleted", SharedPreferencesUtil.getsInstances(this).getBoolean(Preferences.ISCOMPLETED,false));
            cv1.put("CreateTime", getTime());
            db.insert("submitquestion", null, cv1);
            db.close();
        }else if (addoredit.equals("edit")){
            db = myDatabaseHelper.getWritableDatabase();
            ContentValues cv1 = new ContentValues();
            cv1.put("questionjson", gson.toJson(editquestion));
            cv1.put("DatasetID", datasetId);
            cv1.put("RecordId", filledId);
            cv1.put("Creator", editor.getString(Preferences.USERNAMES, ""));
            cv1.put("ParentFolderID", ParentFolderID);
            cv1.put("isCompleted", SharedPreferencesUtil.getsInstances(this).getBoolean(Preferences.ISCOMPLETED,false));
            cv1.put("CreateTime", getTime());
            db.update("submitquestion", cv1, "CreateTime=?",new String[] {String.valueOf(Other.getTimeStamp(CreateTime,"dd/MM/yyyy HH:mm:ss"))});
            db.close();
        }

        finish();
    }

    private String getTime() {
        long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date d1 = new Date(time);//Date格式时间
        String t1 = format.format(d1);//String时间字符串
        String t3 = String.valueOf(time).substring(0,10)+"000";//String时间戳精确到秒 最后三位是毫秒 1000毫秒 = 1秒
        return t3;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerViewAdapter.onActivityResult(requestCode, resultCode, data);
    }

    private class ImageShow extends AsyncTask<String, String, String> {
        String imageurl;
        int i,k,selectposnum;

        public ImageShow(String string, int i,int k, int selectposnum) {
            this.imageurl = string;
            this.i = i;
            this.k = k;
            this.selectposnum = selectposnum;
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
            if ((k + 1) < selectposnum){
                sb.append(s + "@" + Other.getImageFileName(imageurl) + ",");
            }else{
                sb.append(s + "@" + Other.getImageFileName(imageurl));
            }
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
                    activity.setUserIds(!TextUtils.isEmpty(editquestion.get(i).getActivity().getUserIds())?editquestion.get(i).getActivity().getUserIds():String.valueOf(userid));
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
                    }else if (editquestion.get(i).getType() == 31){
                        questionValues.setAnswer(editquestion.get(i).getAnswer());
                        questionValueList.add(questionValues);
                        if (editquestion.get(i).getChildrens() != null){
                            for (int j = 0; j < editquestion.get(i).getChildrens().size(); j++) {
                                questionValues = new SubmitQuestion.QuestionValue();
                                questionValues.setID(editquestion.get(i).getChildrens().get(j).getID());
                                questionValues.setType(editquestion.get(i).getChildrens().get(j).getType());
                                List<SubmitQuestion.ExtendValue> extendValueList = new ArrayList<>();
                                if (editquestion.get(i).getChildrens().get(j).getExtendVals() != null){
                                    for (int k = 0; k < editquestion.get(i).getChildrens().get(j).getExtendVals().size(); k++) {
                                        SubmitQuestion.ExtendValue extendValue = new SubmitQuestion.ExtendValue();
                                        extendValue.setValue(editquestion.get(i).getChildrens().get(j).getExtendVals().get(k));
                                        if (editquestion.get(i).getChildrens().get(j).getType() == 25){
                                            if (!TextUtils.isEmpty(editquestion.get(i).getChildrens().get(j).getExtendVals().get(k))){
                                                String[] selectpos = editquestion.get(i).getChildrens().get(j).getExtendVals().get(k).split("[/]");
                                                String str2 = Other.Timetransformation(Integer.valueOf(selectpos[0].substring(1,selectpos[0].length()))+1,Integer.valueOf(selectpos[1].substring(1,selectpos[1].length())) + 1,Integer.valueOf(selectpos[2].substring(1,selectpos[2].length())) + 1900);
                                                extendValue.setHelpValue(str2);
                                            }else{
                                                extendValue.setHelpValue("");
                                            }
                                        }else{
                                            extendValue.setHelpValue(editquestion.get(i).getChildrens().get(j).getExtendVals().get(k));
                                        }
                                        extendValueList.add(extendValue);
                                    }
                                }
                                questionValues.setExtendVals(extendValueList);
                                if (editquestion.get(i).getChildrens().get(j).getType() == 25) {
                                    questionValues.setAnswer(editquestion.get(i).getChildrens().get(j).getAnswer());
                                    questionValues.setHelpAnswer(editquestion.get(i).getChildrens().get(j).getHelpAnswer());
                                }else {
                                    questionValues.setAnswer(editquestion.get(i).getChildrens().get(j).getAnswer());
                                }
                                questionValueList.add(questionValues);
                            }
                        }
                    }else {
                        questionValues.setAnswer(editquestion.get(i).getAnswer());
                        questionValueList.add(questionValues);
                    }
                }
        }
        //提交接口
        submitQuestion = new SubmitQuestion();
        submitQuestion.setCompanyID(companyid);
        submitQuestion.setUserID(userid);
        submitQuestion.setCompleted(SharedPreferencesUtil.getsInstances(this).getBoolean(Preferences.ISCOMPLETED,false));
        submitQuestion.setDatasetID(datasetId);
        submitQuestion.setRecordID(filledId);
        submitQuestion.setFolderID(ParentFolderID);
        submitQuestion.setValues(questionValueList);

        editQuestionApi = new EditQuestionApi();
        editQuestionApi.setQuestion(gson.toJson(submitQuestion));
        mPresenter.startPost(QuestionnaireActivity.this, editQuestionApi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appManager.destory(this);
//        Other.deleteFile(new File("/storage/emulated/0/Android/data/com.example.vcserver.iqapture/"));
        unregisterReceiver(networkChangeReceiver);
    }
}
