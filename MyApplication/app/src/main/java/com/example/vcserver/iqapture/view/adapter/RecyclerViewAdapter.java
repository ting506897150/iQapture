package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.FileEntity;
import com.example.vcserver.iqapture.bean.IQOptionDetail;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.OnItemClickListener;
import com.example.vcserver.iqapture.commadapter.ViewHolder;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.util.FullyLinearLayoutManager;
import com.example.vcserver.iqapture.util.LinerLayoutItemDecoration;
import com.example.vcserver.iqapture.util.MoneyValueFilter;
import com.example.vcserver.iqapture.util.MyGridView;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.PictureSelectorConfig;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.util.adapter.NumericWheelAdapter;
import com.example.vcserver.iqapture.util.widget.OnWheelChangedListener;
import com.example.vcserver.iqapture.util.widget.WheelView;
import com.example.vcserver.iqapture.view.other.FileSelect;
import com.example.vcserver.iqapture.view.other.PeopleDialog;
import com.example.vcserver.iqapture.view.other.PlusAudioActivity;
import com.example.vcserver.iqapture.view.other.PlusImageActivity;
import com.example.vcserver.iqapture.view.other.PlusVideoActivity;
import com.example.vcserver.iqapture.view.other.QuestionnaireActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.Api.BaseApi;
import com.wzgiceman.rxretrofitlibrary.retrofit_rx.utils.AppUtil;
import com.xujiaji.happybubble.BubbleDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by VCServer on 2018/3/7.
 */

public class RecyclerViewAdapter extends CommonAdapter<Questionnaire.Question>{
    private GridViewAdapter mGridViewAddImgAdapter; //展示上传的图片的适配器
    private List<LocalMedia> selectList;
    private List<String> mPicList; //上传的图片凭证的数据源
    QuestionnaireActivity questionnaireActivity;
    private AlertDialog mDialog;

    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView hour;
    private WheelView mins;
    int itempos;
    private int maxSelectNum = 9;


    List<List<Questionnaire.Question>> alllistquestion;
    List<Questionnaire.Question> question;

    PopupWindow popupWindow;
    SpinnerAdapter spinnerAdapter;
    public int userid, companyid;//用户id，公司id

    IQOptionDetail optionDetails;
    List<Questionnaire.Peoples> peoplesList;
    //利用接口回调点击
    private OnGridItemClickListener onGridItemClickListener;//点击

    int checkboxnum;

    public RecyclerViewAdapter(QuestionnaireActivity context, int layoutId, List<Questionnaire.Question> datas) {
        super(context, layoutId, datas);
        this.questionnaireActivity = context;
        userid = SharedPreferencesUtil.getsInstances(context).getInt(Preferences.USERID, 0);
        companyid = SharedPreferencesUtil.getsInstances(context).getInt(Preferences.COMPANYID, 0);
    }

    @Override
    public void convert(final ViewHolder holder, final Questionnaire.Question s, final int position) {
        if (s.getType() == 0){
            //单行文本输入框
            holder.setViewVisiable(R.id.layout_edit, View.VISIBLE);
            TextView title = holder.getView(R.id.text_edittitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            final EditText edit_single = holder.getView(R.id.edit_single);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
//                edit_single.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                inputManager.showSoftInput(edit_single, 0);
//                            }
//                        },500);
//                    }
//                });
            }else{
                edit_single.setEnabled(false);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_editcheck,R.mipmap.section_check);
//                if (s.isIscontrolschecked()){
//                    edit_single.setFocusable(true);
//                    edit_single.setFocusableInTouchMode(true);
//                    edit_single.requestFocus();
//                    edit_single.requestFocusFromTouch();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputManager.showSoftInput(edit_single, 0);
//                        }
//                    },500);
//                }
//            }else{
//                holder.setImageResource(R.id.image_editcheck,R.mipmap.section_nocheck);
//            }
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                edit_single.setText(s.getAnswer());
                edit_single.setSelection(s.getAnswer().length());//将光标移至文字末尾
            }

            if (s.isComment()){
                childshow(position);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        s.setAnswer("");
                    } else {
                        s.setAnswer(editable.toString());
                    }
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (s.getType() == 1){
            //多行文本输入框
            holder.setViewVisiable(R.id.layout_moreedit, View.VISIBLE);
            TextView title = holder.getView(R.id.text_moreedittitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            final EditText edit_multiline = holder.getView(R.id.edit_multiline);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
//                edit_multiline.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputMethodManager  inputManager = (InputMethodManager)edit_multiline.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                inputManager.showSoftInput(edit_multiline, 0);
//                            }
//                        },500);
//                    }
//                });
            }else{
                edit_multiline.setEnabled(false);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_morecheck,R.mipmap.section_check);
//                if (s.isIscontrolschecked()){
//                    edit_multiline.setFocusable(true);
//                    edit_multiline.setFocusableInTouchMode(true);
//                    edit_multiline.requestFocus();
//                    edit_multiline.requestFocusFromTouch();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager  inputManager = (InputMethodManager)edit_multiline.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputManager.showSoftInput(edit_multiline, 0);
//                        }
//                    },500);
//                }
//            }else{
//                holder.setImageResource(R.id.image_morecheck,R.mipmap.section_nocheck);
//            }
            holder.setIsRecyclable(false);
            if (edit_multiline.getTag() instanceof TextWatcher) {
                edit_multiline.removeTextChangedListener((TextWatcher) edit_multiline.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                edit_multiline.setText(s.getAnswer());
                edit_multiline.setSelection(s.getAnswer().length());//将光标移至文字末尾
            }

            if (s.isComment()){
                childshow(position);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        s.setAnswer("");
                    } else {
                        s.setAnswer(editable.toString());
                    }
                }
            };
            edit_multiline.addTextChangedListener(watcher);
            edit_multiline.setTag(watcher);
        }else if (s.getType() == 2){
            //多选
            holder.setViewVisiable(R.id.layout_checkbox, View.VISIBLE);
            TextView title = holder.getView(R.id.text_checkboxtitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_checkboxcheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_checkboxcheck,R.mipmap.section_nocheck);
//            }
            RecyclerView recyclerview_checkbox = holder.getView(R.id.recyclerview_checkbox);
            // 建立数据源
            final List<Questionnaire.Options> filledValuesList = new ArrayList<>();
            Questionnaire.Options filledValues;
            if (s.getOptions() != null){
                for (int i = 0; i < s.getOptions().size(); i++) {
                    filledValues = new Questionnaire.Options();
                    filledValues.setOptionID(s.getOptions().get(i).getOptionID());
                    filledValues.setOptionName(s.getOptions().get(i).getOptionName());
                    filledValues.setOptionColor(s.getOptions().get(i).getOptionColor());
                    filledValues.setChecked(false);
                    filledValuesList.add(filledValues);
                }
            }

            //加载子问题
            if (s.isComment()){
                childshow(position);
            }

            final CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(mContext,R.layout.item_checkbox,filledValuesList);
            recyclerview_checkbox.setLayoutManager(new FullyLinearLayoutManager(mContext));
            recyclerview_checkbox.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));//分割线
            recyclerview_checkbox.setAdapter(checkBoxAdapter);
            checkBoxAdapter.notifyDataSetChanged();

            checkboxnum = 0;
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                String[] selectpos = s.getAnswer().split("[<|>]");
                for (int i = 0; i < filledValuesList.size(); i++) {
                    for (int j = 0; j < selectpos.length; j++) {
                        if (filledValuesList.get(i).getOptionName().equals(selectpos[j])){
                            filledValuesList.get(i).setChecked(true);
                            mDatas.get(position).getOptions().get(i).setChecked(true);//设为选中.
                            checkboxnum++;
                            //是否加载了衍生问题
                            if (s.isIsderivativeshow() == false){
                                //是否具有衍生问题
                                if (s.getDeriveItems() != null&& s.getDeriveItems().size() > 0){
                                    for (int k = 0; k < s.getDeriveItems().size(); k++) {
                                        //判断默认选中项是否具有衍生问题，如果有就加载
                                        if (s.getDeriveItems().get(k).getValue().equals(selectpos[j])){
                                            //加载衍生问题
                                            if (s.getDeriveItems().get(k).getQuestions() != null && s.getDeriveItems().get(k).getQuestions().size() > 0){
                                                mDatas.get(position).setIsderivativeshow(true);
                                                mDatas.addAll(position+1,mDatas.get(position).getDeriveItems().get(k).getQuestions());
                                                onGridItemClickListener.onGridItemceshiClick(position);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                checkBoxAdapter.setmOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        if (s.getAnswersLimitMax() > 0){
                            if (checkboxnum < (s.getAnswersLimitMax())){
                                //判断是否已选中，如果已选中则取消选中
                                if (filledValuesList.get(pos).isChecked()){
                                    filledValuesList.get(pos).setChecked(false);
                                    mDatas.get(position).getOptions().get(pos).setChecked(false);
                                    checkboxnum--;
                                }else{
                                    filledValuesList.get(pos).setChecked(true);//点击的设为选中.
                                    mDatas.get(position).getOptions().get(pos).setChecked(true);
                                    checkboxnum++;
                                }
                                //加载衍生问题
                                derivative(position,pos,"");
                            }else{
                                if (checkboxnum == s.getAnswersLimitMax()){
                                    //判断是否已选中，如果已选中则取消选中
                                    if (filledValuesList.get(pos).isChecked()){
                                        filledValuesList.get(pos).setChecked(false);
                                        mDatas.get(position).getOptions().get(pos).setChecked(false);
                                        checkboxnum--;
                                        //加载衍生问题
                                        derivative(position,pos,"");
                                    }else{
                                        if (s.getAnswersLimitMax() > 1){
                                            questionnaireActivity.showTip(s.getNo()+"."+s.getTitle()+".Sorry, do not choose more than "+s.getAnswersLimitMax()+" answers.");
                                        }else{
                                            questionnaireActivity.showTip(s.getNo()+"."+s.getTitle()+".Sorry, do not choose more than "+s.getAnswersLimitMax()+" answer.");
                                        }
                                    }
                                }
                            }
                        }else{
                            //判断是否已选中，如果已选中则取消选中
                            if (filledValuesList.get(pos).isChecked()){
                                filledValuesList.get(pos).setChecked(false);
                                mDatas.get(position).getOptions().get(pos).setChecked(false);
                                checkboxnum--;
                            }else{
                                filledValuesList.get(pos).setChecked(true);//点击的设为选中.
                                mDatas.get(position).getOptions().get(pos).setChecked(true);
                                checkboxnum++;
                            }
                            //加载衍生问题
                            derivative(position,pos,"");
                        }
                    }
                });
            }
        }else if (s.getType() == 3){
            //单选
            holder.setViewVisiable(R.id.layout_radiobutton, View.VISIBLE);
            TextView title = holder.getView(R.id.text_radiobuttontitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_radiobuttoncheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_radiobuttoncheck,R.mipmap.section_nocheck);
//            }
            RecyclerView recyclerview_radiobutton = holder.getView(R.id.recyclerview_radiobutton);
            // 建立数据源
            final List<Questionnaire.Options> filledValuesList = new ArrayList<>();
            Questionnaire.Options filledValues;
            if (s.getOptions() != null){
                for (int i = 0; i < s.getOptions().size(); i++) {
                    filledValues = new Questionnaire.Options();
                    filledValues.setOptionID(s.getOptions().get(i).getOptionID());
                    filledValues.setOptionName(s.getOptions().get(i).getOptionName());
                    filledValues.setOptionColor(s.getOptions().get(i).getOptionColor());
                    filledValues.setChecked(false);
                    filledValuesList.add(filledValues);
                }
            }

            //加载子问题
            if (s.isComment()){
                childshow(position);
            }
            final RadiobuttonAdapter radiobuttonAdapter = new RadiobuttonAdapter(mContext,R.layout.item_radiobutton,filledValuesList);
            recyclerview_radiobutton.setLayoutManager(new FullyLinearLayoutManager(mContext));
            recyclerview_radiobutton.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));//分割线
            recyclerview_radiobutton.setAdapter(radiobuttonAdapter);
            radiobuttonAdapter.notifyDataSetChanged();
            //设置默认值
            if (!TextUtils.isEmpty(s.getAnswer())){
                for (int i = 0; i < filledValuesList.size(); i++) {
                    if (filledValuesList.get(i).getOptionName().equals(s.getAnswer())){
                        filledValuesList.get(i).setChecked(true);//设为选中.
                        mDatas.get(position).getOptions().get(i).setChecked(true);
                        //是否加载了衍生问题
                        if (s.isIsderivativeshow() == false){
                            //是否具有衍生问题
                            if (s.getDeriveItems() != null && s.getDeriveItems().size() > 0){
                                for (int j = 0; j < s.getDeriveItems().size(); j++) {
                                    //判断默认选中项是否具有衍生问题，如果有就加载
                                    if (s.getDeriveItems().get(j).getValue().equals(s.getAnswer())){
                                        //加载衍生问题
                                        if (s.getDeriveItems().get(j).getQuestions() != null && s.getDeriveItems().get(j).getQuestions().size() > 0){
                                            mDatas.get(position).setIsderivativeshow(true);
                                            mDatas.addAll(position+1,s.getDeriveItems().get(j).getQuestions());
                                            onGridItemClickListener.onGridItemceshiClick(position);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                radiobuttonAdapter.setmOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        //设置其他选择项全部为未选中
                        for (int i = 0; i < filledValuesList.size(); i++) {
                            filledValuesList.get(i).setChecked(false);
                            mDatas.get(position).getOptions().get(pos).setChecked(false);
                        }
                        filledValuesList.get(pos).setChecked(true);//点击的设为选中.
                        mDatas.get(position).getOptions().get(pos).setChecked(true);
                        mDatas.get(position).setAnswer(filledValuesList.get(pos).getOptionName());
                        //加载衍生问题
                        derivative(position,pos,"");
                    }
                });
            }
        }else if (s.getType() == 4){
            //下拉列表
            holder.setViewVisiable(R.id.layout_spinner, View.VISIBLE);
            TextView title = holder.getView(R.id.text_spinnertitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_spinnercheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_spinnercheck,R.mipmap.section_nocheck);
//            }
            // 初始化控件
            final TextView spinner_down = holder.getView(R.id.spinner_down);
            spinner_down.setText(s.getAnswer());
            // 建立数据源
            final List<Questionnaire.Options> filledValuesList = new ArrayList<>();
            Questionnaire.Options filledValues;
            if (s.getOptions() != null){
                for (int i = 0; i < s.getOptions().size(); i++) {
                    filledValues = new Questionnaire.Options();
                    filledValues.setOptionID(s.getOptions().get(i).getOptionID());
                    filledValues.setOptionName(s.getOptions().get(i).getOptionName());
                    filledValues.setChecked(false);
                    filledValuesList.add(filledValues);
                }
            }
            //加载子问题
            if (s.isComment()){
                childshow(position);
            }
            //设置默认值
            for (int i = 0; i < filledValuesList.size(); i++) {
                if (filledValuesList.get(i).getOptionName().equals(s.getAnswer())){
                    spinner_down.setText(filledValuesList.get(i).getOptionName());
                    //是否加载了衍生问题
                    if (s.isIsderivativeshow() == false){
                        //是否具有衍生问题
                        if (s.getDeriveItems() != null&& s.getDeriveItems().size() > 0){
                            for (int j = 0; j < s.getDeriveItems().size(); j++) {
                                //判断默认选中项是否具有衍生问题，如果有就加载
                                if (s.getDeriveItems().get(j).getValue().equals(s.getAnswer())){
                                    //加载衍生问题
                                    if (s.getDeriveItems().get(j).getQuestions() != null && s.getDeriveItems().get(j).getQuestions().size() > 0){
//                                        s.setIsderivativeshow(true);
//                                        mDatas.addAll(position+1,s.getDeriveItems().get(j).getQuestions());
//                                        notifyData();
                                        mDatas.get(position).setIsderivativeshow(true);
                                        mDatas.addAll(position+1,mDatas.get(position).getDeriveItems().get(j).getQuestions());
                                        onGridItemClickListener.onGridItemceshiClick(position);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // 建立Adapter并且绑定数据源
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                spinner_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View contentView = null;
                        //判断是否是带搜索的下拉框
                        if (s.isOnlineSearch()){
                            //加载带搜索的下拉框布局
                            LayoutInflater inflater = (LayoutInflater) mContext
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            contentView = inflater.inflate(R.layout.spinner_downsearchlist, null);
                            final EditText edit_spinnersearch = contentView.findViewById(R.id.edit_spinnersearch);
                            final RelativeLayout rlayout_prompt = contentView.findViewById(R.id.rlayout_prompt);
                            final RelativeLayout rlayout_recyclerview = contentView.findViewById(R.id.rlayout_recyclerview);
                            final RecyclerView recyclerview = contentView.findViewById(R.id.recyclerview);
                            final TextView text_all = contentView.findViewById(R.id.text_all);
                            final TextView text_prompt = contentView.findViewById(R.id.text_prompt);
                            //搜索框监听用户输入
                            edit_spinnersearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    /*判断是否是“搜索”键*/
                                    if(actionId == EditorInfo.IME_ACTION_SEARCH){
                                        final String key = edit_spinnersearch.getText().toString().trim();
                                        if(TextUtils.isEmpty(key)){
                                            questionnaireActivity.showTip("请输入要搜索的内容");
                                            rlayout_prompt.setVisibility(View.VISIBLE);
                                            text_all.setVisibility(View.VISIBLE);
                                            text_prompt.setText("Please enter 1 or more characters");
                                            rlayout_recyclerview.setVisibility(View.GONE);
                                            return true;
                                        }else{
                                            if (AppUtil.isNetworkAvailable(mContext)) {
                                                //  下面就是大家的业务逻辑
                                                questionnaireActivity.showLoadingDialog();
                                                //获取下拉数据
                                                try {
                                                    optionDetails = searchDrow(s.getID(), key,1);
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (optionDetails != null){
                                                                final List<Questionnaire.Options> xx = new ArrayList<>();
                                                                if (optionDetails.getOptions() != null){
                                                                    for (int i = 0; i < optionDetails.getOptions().size(); i++) {
                                                                        if (optionDetails.getOptions().get(i).getOptionName().toLowerCase().contains(key.toLowerCase())){
                                                                            Questionnaire.Options options = new Questionnaire.Options();
                                                                            options.setOptionName(optionDetails.getOptions().get(i).getOptionName());
                                                                            xx.add(options);
                                                                        }
                                                                    }
                                                                    rlayout_prompt.setVisibility(View.GONE);
                                                                    rlayout_recyclerview.setVisibility(View.VISIBLE);
                                                                    spinnerAdapter = new SpinnerAdapter(mContext,R.layout.item_spinner,xx);
                                                                    recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                                                                    recyclerview.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));//分割线
                                                                    recyclerview.setAdapter(spinnerAdapter);
                                                                    questionnaireActivity.closeLoadingDialog();
                                                                    spinnerAdapter.setmOnItemClickListener(new OnItemClickListener() {
                                                                        @Override
                                                                        public void onItemClick(View view, int pos) {
                                                                            popupWindow.dismiss();
                                                                            spinner_down.setText(xx.get(pos).getOptionName());
                                                                            mDatas.get(position).setAnswer(xx.get(pos).getOptionName());
                                                                            rlayout_recyclerview.setVisibility(View.GONE);
                                                                            derivative(position,pos,"search");
                                                                        }
                                                                    });
                                                                }else{
                                                                    questionnaireActivity.closeLoadingDialog();
                                                                    rlayout_prompt.setVisibility(View.VISIBLE);
                                                                    text_all.setVisibility(View.INVISIBLE);
                                                                    text_prompt.setText("No results found!");
                                                                    rlayout_recyclerview.setVisibility(View.GONE);
                                                                }
                                                            }else{
                                                                questionnaireActivity.closeLoadingDialog();
                                                                rlayout_prompt.setVisibility(View.VISIBLE);
                                                                text_all.setVisibility(View.INVISIBLE);
                                                                text_prompt.setText("No results found!");
                                                                rlayout_recyclerview.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    },1000);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                rlayout_prompt.setVisibility(View.VISIBLE);
                                                text_all.setVisibility(View.INVISIBLE);
                                                text_prompt.setText("There is no network!");
                                                rlayout_recyclerview.setVisibility(View.GONE);
                                            }
                                        }
                                        return false;
                                    }
                                    return false;
                                }
                            });
                            edit_spinnersearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    //内容改变之前调用
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    //内容改变之中

                                }

                                @Override
                                public void afterTextChanged(Editable sss) {
                                    //内容改变之后调用
                                    if (TextUtils.isEmpty(edit_spinnersearch.getText().toString()) && edit_spinnersearch.getText().toString().equals("")) {
                                        rlayout_prompt.setVisibility(View.VISIBLE);
                                        text_all.setVisibility(View.VISIBLE);
                                        text_prompt.setText("Please enter 1 or more characters");
                                        rlayout_recyclerview.setVisibility(View.GONE);
                                    }
                                }
                            });
                            text_all.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtil.isNetworkAvailable(mContext)){
                                        questionnaireActivity.showLoadingDialog();
                                        //获取下拉数据
                                        try {
                                            optionDetails = searchDrow(s.getID(), "",1);
                                            new Handler().postDelayed(new Runnable(){
                                                public void run() {
                                                    if (optionDetails != null){
                                                        final List<Questionnaire.Options> xx = new ArrayList<>();
                                                        if (optionDetails.getOptions() != null){
                                                            for (int i = 0; i < optionDetails.getOptions().size(); i++) {
                                                                Questionnaire.Options options = new Questionnaire.Options();
                                                                options.setOptionName(optionDetails.getOptions().get(i).getOptionName());
                                                                xx.add(options);
                                                            }
                                                            rlayout_prompt.setVisibility(View.GONE);
                                                            rlayout_recyclerview.setVisibility(View.VISIBLE);
                                                            spinnerAdapter = new SpinnerAdapter(mContext,R.layout.item_spinner,xx);
                                                            recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                                                            recyclerview.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));//分割线
                                                            recyclerview.setAdapter(spinnerAdapter);
                                                            questionnaireActivity.closeLoadingDialog();
                                                            spinnerAdapter.setmOnItemClickListener(new OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(View view, int pos) {
                                                                    popupWindow.dismiss();
                                                                    spinner_down.setText(xx.get(pos).getOptionName());
                                                                    mDatas.get(position).setAnswer(xx.get(pos).getOptionName());
                                                                    rlayout_recyclerview.setVisibility(View.GONE);
                                                                    derivative(position,pos,"search");
                                                                }
                                                            });
                                                        }else{
                                                            questionnaireActivity.closeLoadingDialog();
                                                            rlayout_prompt.setVisibility(View.VISIBLE);
                                                            text_all.setVisibility(View.INVISIBLE);
                                                            text_prompt.setText("No results found!");
                                                            rlayout_recyclerview.setVisibility(View.GONE);
                                                        }
                                                    }else{
                                                        questionnaireActivity.closeLoadingDialog();
                                                        rlayout_prompt.setVisibility(View.VISIBLE);
                                                        text_all.setVisibility(View.INVISIBLE);
                                                        text_prompt.setText("No results found!");
                                                        rlayout_recyclerview.setVisibility(View.GONE);
                                                    }
                                                }
                                            }, 1000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        rlayout_prompt.setVisibility(View.VISIBLE);
                                        text_all.setVisibility(View.INVISIBLE);
                                        text_prompt.setText("There is no network!");
                                        rlayout_recyclerview.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            contentView = LayoutInflater.from(mContext).inflate(
                                    R.layout.spinner_downlist, null);
                            final RecyclerView recyclerview = contentView.findViewById(R.id.recyclerview);
                            spinnerAdapter = new SpinnerAdapter(mContext,R.layout.item_spinner,filledValuesList);
                            recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerview.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));//分割线
                            recyclerview.setAdapter(spinnerAdapter);
                            spinnerAdapter.notifyDataSetChanged();
                            spinnerAdapter.setmOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int pos) {
                                    popupWindow.dismiss();
                                    spinner_down.setText(filledValuesList.get(pos).getOptionName());
                                    mDatas.get(position).setAnswer(filledValuesList.get(pos).getOptionName());
                                    derivative(position,pos,"");
                                }
                            });
                        }

                        //实例化popupWindow
                        popupWindow = new PopupWindow(contentView,
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                        int[] location = new int[2];
                        v.getLocationInWindow(location);
                        //设置PopupWindow动画
                        popupWindow.setAnimationStyle(R.style.AnimDown);
                        //设置PopupWindow的视图内容
                        popupWindow.setContentView(contentView);
                        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
                        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                        popupWindow.setOutsideTouchable(true);
                        //设置是否允许PopupWindow的范围超过屏幕范围
                        popupWindow.setClippingEnabled(true);
                        //设置PopupWindow消失监听
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        });

//                        WindowManager wm = (WindowManager) questionnaireActivity.getSystemService(Context.WINDOW_SERVICE);
//                        DisplayMetrics dm = new DisplayMetrics();
//                        wm.getDefaultDisplay().getMetrics(dm);
                        int height = questionnaireActivity.dm.heightPixels;       // 屏幕高度（像素）
                        // 设置好参数之后再show
                        if ((height - location[1] - 96) > (filledValuesList.size() * 96)){
                            popupWindow.showAsDropDown(v);
                        }else{
                            popupWindow.setAnimationStyle(R.style.AnimBottom);
                            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,location[0],location[1] - (filledValuesList.size() * 96));
                        }
                    }
                });
            }
        }else if (s.getType() == 5){
            //数据表格下拉框
        }else if (s.getType() == 6){
            //时间选择 DD/MM/YYYY HH:MM
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            TextView title = holder.getView(R.id.text_datetitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_nocheck);
//            }
            final TextView text_datetime = holder.getView(R.id.text_datetime);
            text_datetime.setText(s.getAnswer());
//            if (!TextUtils.isEmpty(s.getAnswer())){
//                if (s.getAnswer().contains("-")){
//                    if (Other.isValidDate(s.getAnswer(),"dd-MM-yyyy HH:mm") == true){
//                        text_datetime.setText(Other.timedate(Other.getTimeStamp(s.getAnswer(),"dd-MM-yyyy HH:mm"),"dd/MM/yyyy HH:mm"));
//                    }else if (Other.isValidDate(s.getAnswer(),"yyyy-MM-dd HH:mm") == true){
//                        text_datetime.setText(Other.timedate(Other.getTimeStamp(s.getAnswer(),"yyyy-MM-dd HH:mm"),"dd/MM/yyyy HH:mm"));
//                    }
//                }else if (s.getAnswer().contains("/")){
////                    text_datetime.setText(Other.timedate(Other.getTimeStamp(s.getAnswer(),"yyyy/MM/dd"),"dd/MM/yyyy"));
//                    text_datetime.setText(s.getAnswer());
//                }
//            }

            if (s.isComment()){
                childshow(position);
            }

            ImageView image_date = holder.getView(R.id.image_date);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                image_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        View view = v.inflate(mContext, R.layout.datepicker_layout, null);
                        TextView positiveButton = view.findViewById(R.id.positiveButton);
                        TextView negativeButton = view.findViewById(R.id.negativeButton);

                        Calendar c = Calendar.getInstance();
                        int curYear = c.get(Calendar.YEAR);
                        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
                        int curDate = c.get(Calendar.DAY_OF_MONTH);//一月的第几天
                        int curHour = c.get(Calendar.HOUR_OF_DAY);//一天的第几小时
                        int curMin = c.get(Calendar.MINUTE);//一小时的第几分钟
                        year = view.findViewById(R.id.year);
                        year.setVisibility(View.VISIBLE);
                        initYear();
                        month = view.findViewById(R.id.month);
                        month.setVisibility(View.VISIBLE);
                        initMonth();
                        day = view.findViewById(R.id.day);
                        day.setVisibility(View.VISIBLE);
                        initDay(curYear,curMonth);
                        hour = view.findViewById(R.id.hour);
                        hour.setVisibility(View.VISIBLE);
                        initHour();
                        mins = view.findViewById(R.id.mins);
                        mins.setVisibility(View.VISIBLE);
                        initMins();
                        //设置wheelview的默认数据下标
                        year.setCurrentItem(curYear - 1900);
                        month.setCurrentItem(curMonth - 1);
                        day.setCurrentItem(curDate - 1);
                        hour.setCurrentItem(curHour);
                        mins.setCurrentItem(curMin);
                        //设置wheelview的可见条目数
                        year.setVisibleItems(7);
                        month.setVisibleItems(7);
                        day.setVisibleItems(7);
                        hour.setVisibleItems(7);
                        mins.setVisibleItems(7);

                        //为wheelview添加滑动事件
                        year.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                            }
                        });
                        month.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                            }
                        });

                        mDialog = new AlertDialog.Builder(mContext, R.style.dialog)
                                .setView(view)
                                .show();
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String str = String.format(Locale.CHINA,"%02d/%02d/%04d %02d:%02d",day.getCurrentItem()+1,month.getCurrentItem() + 1,year.getCurrentItem() + 1900,hour.getCurrentItem(),mins.getCurrentItem());
                                s.setAnswer(str);
                                text_datetime.setText(str);
//                                onGridItemClickListener.onGridItemceshiClick(position);
                                mDialog.dismiss();
                            }
                        });
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDialog.dismiss();
                            }
                        });
                    }
                });
            }
        }else if (s.getType() == 7){
            //label
            holder.setViewVisiable(R.id.layout_autoid, View.VISIBLE);
            TextView title = holder.getView(R.id.text_autoidtitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_autoidcheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_autoidcheck,R.mipmap.section_nocheck);
//            }
            TextView text_autoid = holder.getView(R.id.text_autoid);
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                text_autoid.setText(s.getAnswer());
            }

            if (s.isComment()){
                childshow(position);
            }

        }else if (s.getType() == 9){
            //数字输入框
            holder.setViewVisiable(R.id.layout_number, View.VISIBLE);
            TextView title = holder.getView(R.id.text_numbertitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            final EditText edit_single = holder.getView(R.id.edit_number);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
//                edit_single.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                inputManager.showSoftInput(edit_single, 0);
//                            }
//                        },500);
//                    }
//                });
            }else{
                edit_single.setEnabled(false);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_numbercheck,R.mipmap.section_check);
//                if (s.isIscontrolschecked()){
//                    edit_single.setFocusable(true);
//                    edit_single.setFocusableInTouchMode(true);
//                    edit_single.requestFocus();
//                    edit_single.requestFocusFromTouch();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputManager.showSoftInput(edit_single, 0);
//                        }
//                    },500);
//                }
//            }else{
//                holder.setImageResource(R.id.image_numbercheck,R.mipmap.section_nocheck);
//            }
            edit_single.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
            edit_single.setHint("Number");
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                edit_single.setText(s.getAnswer());
                edit_single.setSelection(s.getAnswer().length());//将光标移至文字末尾
            }

            if (s.isComment()){
                childshow(position);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        s.setAnswer("");
                    } else {
                        s.setAnswer(editable.toString());
//                        if (Integer.valueOf(editable.toString()) >= s.getMin().intValue() && Integer.valueOf(editable.toString()) <= s.getMax().intValue()){
//                            s.setAnswer(editable.toString());
//                        }else{
//                            Toast.makeText(questionnaireActivity,"Please enter the correct value!",Toast.LENGTH_LONG).show();
//                        }
                    }
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (s.getType() == 10){
            //货币输入框
            holder.setViewVisiable(R.id.layout_money, View.VISIBLE);
            TextView title = holder.getView(R.id.text_moneytitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            final EditText edit_single = holder.getView(R.id.edit_money);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
//                edit_single.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                inputManager.showSoftInput(edit_single, 0);
//                            }
//                        },500);
//                    }
//                });
            }else{
                edit_single.setEnabled(false);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_moneycheck,R.mipmap.section_check);
//                if (s.isIscontrolschecked()){
//                    edit_single.setFocusable(true);
//                    edit_single.setFocusableInTouchMode(true);
//                    edit_single.requestFocus();
//                    edit_single.requestFocusFromTouch();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputManager.showSoftInput(edit_single, 0);
//                        }
//                    },500);
//                }
//            }else{
//                holder.setImageResource(R.id.image_moneycheck,R.mipmap.section_nocheck);
//            }
            edit_single.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edit_single.setHint("Currency");
            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter()});//默认两位小数
//            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(3)});//手动设置其他位数，例如3
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                edit_single.setText(s.getAnswer());
                edit_single.setSelection(s.getAnswer().length());//将光标移至文字末尾
            }

            if (s.isComment()){
                childshow(position);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        s.setAnswer("");
                    } else {
                        s.setAnswer(editable.toString());
//                        if (Float.valueOf(editable.toString()) >= s.getMin().floatValue() && Float.valueOf(editable.toString()) <= s.getMax().floatValue()){
//                            s.setAnswer(editable.toString());
//                        }else{
//                            Toast.makeText(questionnaireActivity,"Please enter the correct value!",Toast.LENGTH_LONG).show();
//                        }
                    }
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (s.getType() == 11){
            //百分比输入框
            holder.setViewVisiable(R.id.layout_percent, View.VISIBLE);
            TextView title = holder.getView(R.id.text_percenttitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            final EditText edit_single = holder.getView(R.id.edit_percent);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
//                edit_single.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                inputManager.showSoftInput(edit_single, 0);
//                            }
//                        },500);
//                    }
//                });
            }else{
                edit_single.setEnabled(false);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_percentcheck,R.mipmap.section_check);
//                if (s.isIscontrolschecked()){
//                    edit_single.setFocusable(true);
//                    edit_single.setFocusableInTouchMode(true);
//                    edit_single.requestFocus();
//                    edit_single.requestFocusFromTouch();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputManager.showSoftInput(edit_single, 0);
//                        }
//                    },500);
//                }
//            }else{
//                holder.setImageResource(R.id.image_percentcheck,R.mipmap.section_nocheck);
//            }
            edit_single.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edit_single.setHint("Percentage");
            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter()});//默认两位小数
//            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(3)});//手动设置其他位数，例如3
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                edit_single.setText(s.getAnswer());
                edit_single.setSelection(s.getAnswer().length());//将光标移至文字末尾
            }

            if (s.isComment()){
                childshow(position);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        s.setAnswer("");
                    } else {
                        s.setAnswer(editable.toString());
//                        if (Float.valueOf(editable.toString()) >= s.getMin().floatValue() && Float.valueOf(editable.toString()) <= s.getMax().floatValue()){
//                            s.setAnswer(editable.toString());
//                        }else{
//                            Toast.makeText(questionnaireActivity,"Please enter the correct value!",Toast.LENGTH_LONG).show();
//                        }
                    }
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (s.getType() == 12 || s.getType() == 15|| s.getType() == 23){
            //图片上传  文件上传  视频上传
            holder.setViewVisiable(R.id.layout_imageupload, View.VISIBLE);
            TextView title = holder.getView(R.id.text_imageuploadtitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_imageuploadcheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_imageuploadcheck,R.mipmap.section_nocheck);
//            }
            holder.setIsRecyclable(false);
            MyGridView gridview = holder.getView(R.id.gridview_imageupload);

            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                String[] selectpos = s.getAnswer().split("[,]");
                selectList = new ArrayList<>();
                for (int j = 0; j < selectpos.length; j++) {
                    if (selectpos[j].contains(".PNG") || selectpos[j].contains(".png")|| selectpos[j].contains(".JPEG")|| selectpos[j].contains(".jpeg")|| selectpos[j].contains(".GIF")|| selectpos[j].contains(".gif")|| selectpos[j].contains(".JPG")|| selectpos[j].contains(".jpg")|| selectpos[j].contains(".webp")|| selectpos[j].contains(".WEBP")|| selectpos[j].contains(".bmp")){
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath(selectpos[j]);
                        localMedia.setMimeType(1);
                        localMedia.setPictureType("image/jpeg");
                        selectList.add(localMedia);//点击的设为选中.
                    }else if (selectpos[j].contains(".mp4") || selectpos[j].contains(".avi")|| selectpos[j].contains(".3gpp")|| selectpos[j].contains(".3gp")|| selectpos[j].contains(".mov")){
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath(selectpos[j]);
                        localMedia.setMimeType(2);
                        localMedia.setPictureType("video/mp4");
                        selectList.add(localMedia);//点击的设为选中.
                    }else if (selectpos[j].contains(".mp3") || selectpos[j].contains(".amr")|| selectpos[j].contains(".aac")|| selectpos[j].contains(".war")|| selectpos[j].contains(".flac")|| selectpos[j].contains(".lamr")){
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath(selectpos[j]);
                        localMedia.setMimeType(3);
                        localMedia.setPictureType("audio/mpeg");
                        selectList.add(localMedia);//点击的设为选中.
                    }else{
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath(selectpos[j]);
                        localMedia.setMimeType(4);
                        localMedia.setPictureType("text/plain");
                        selectList.add(localMedia);//点击的设为选中.
                    }
                }
            }else{
                selectList = new ArrayList<>();
            }

            if (s.isComment()){
                childshow(position);
            }

            mGridViewAddImgAdapter = new GridViewAdapter(questionnaireActivity,selectList);
            gridview.setAdapter(mGridViewAddImgAdapter);
            mGridViewAddImgAdapter.notifyDataSetChanged();

            mGridViewAddImgAdapter.setOnGridItemClickListener(new GridViewAdapter.OnGridItemClickListener() {
                @Override
                public void onGridItemClick(int pos, int count,boolean isdelete) {
                    itempos = position;
                    SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(questionnaireActivity);
                    editor.putInt(Preferences.ITEMPOSS, itempos);
//                        onGridItemClickListener.onGridItemceshiClick(position);
                    if (!TextUtils.isEmpty(mDatas.get(itempos).getAnswer())){
                        String[] selectpos = mDatas.get(itempos).getAnswer().split("[,]");
                        selectList = new ArrayList<>();
                        for (int j = 0; j < selectpos.length; j++) {
                            if (selectpos[j].contains(".PNG") || selectpos[j].contains(".png")|| selectpos[j].contains(".JPEG")|| selectpos[j].contains(".jpeg")|| selectpos[j].contains(".GIF")|| selectpos[j].contains(".gif")|| selectpos[j].contains(".JPG")|| selectpos[j].contains(".jpg")|| selectpos[j].contains(".webp")|| selectpos[j].contains(".WEBP")|| selectpos[j].contains(".bmp")){
                                LocalMedia localMedia = new LocalMedia();
                                localMedia.setPath(selectpos[j]);
                                localMedia.setMimeType(1);
                                localMedia.setPictureType("image/jpeg");
                                selectList.add(localMedia);//点击的设为选中.
                            }else if (selectpos[j].contains(".mp4") || selectpos[j].contains(".avi")|| selectpos[j].contains(".3gpp")|| selectpos[j].contains(".3gp")|| selectpos[j].contains(".mov")){
                                LocalMedia localMedia = new LocalMedia();
                                localMedia.setPath(selectpos[j]);
                                localMedia.setMimeType(2);
                                localMedia.setPictureType("video/mp4");
                                selectList.add(localMedia);//点击的设为选中.
                            }else if (selectpos[j].contains(".mp3") || selectpos[j].contains(".amr")|| selectpos[j].contains(".aac")|| selectpos[j].contains(".war")|| selectpos[j].contains(".flac")|| selectpos[j].contains(".lamr")){
                                LocalMedia localMedia = new LocalMedia();
                                localMedia.setPath(selectpos[j]);
                                localMedia.setMimeType(3);
                                localMedia.setPictureType("audio/mpeg");
                                selectList.add(localMedia);//点击的设为选中.
                            }else{
                                LocalMedia localMedia = new LocalMedia();
                                localMedia.setPath(selectpos[j]);
                                localMedia.setMimeType(4);
                                localMedia.setPictureType("text/plain");
                                selectList.add(localMedia);//点击的设为选中.
                            }
                        }
                    }else{
                        selectList = new ArrayList<>();
                    }

                    if (isdelete == true){
                        selectList.remove(pos);
                        refreshAdapter(selectList);
                    }else if (isdelete == false){
                        if (pos == count - 1) {
                            //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过上限，才能点击
                            if (selectList.size() == maxSelectNum) {
                                //最多添加5张图片,点击查看大图
                                viewPluImg(pos);
                            } else {
                                if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                                    showPop();
                                }
                            }
                        } else {
                            viewPluImg(pos);
                        }
                    }
                }
            });
        }else if (s.getType() == 13){
            //SeekBar
            holder.setViewVisiable(R.id.layout_seekbar, View.VISIBLE);
            TextView title = holder.getView(R.id.text_seekbartitle);
            final TextView text_seekbarnum = holder.getView(R.id.text_seekbarnum);
            final SeekBar seekbar = holder.getView(R.id.seekbar);

            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            text_seekbarnum.measure(spec, spec);
            int quotaWidth = text_seekbarnum.getMeasuredWidth();

            int spec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            text_seekbarnum.measure(spec2, spec2);

            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_seekbarcheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_seekbarcheck,R.mipmap.section_nocheck);
//            }

            if (s.getMin().intValue() > 0){
                if (s.getStep().intValue() > 0){
                    seekbar.setMax((s.getMax().intValue() - s.getMin().intValue())%s.getStep().intValue()==0?(s.getMax().intValue() - s.getMin().intValue())/s.getStep().intValue():(s.getMax().intValue() - s.getMin().intValue())/s.getStep().intValue() + 1);
                }else{
                    s.setStep(new BigDecimal(1));
                    seekbar.setMax((s.getMax().intValue() - 1));
                }
            }else{
                if (s.getStep().intValue() > 0){
                    seekbar.setMax(s.getMax().intValue()%s.getStep().intValue()==0?s.getMax().intValue()/s.getStep().intValue():s.getMax().intValue()/s.getStep().intValue() + 1);
                }else{
                    s.setStep(new BigDecimal(1));
                    seekbar.setMax(s.getMax().intValue());
                }
            }
            //判断取出数据是否为空，不为空则赋值
            if (TextUtils.isEmpty(s.getAnswer())){
                if (s.getMin().intValue() > 0){
                    seekbar.setProgress((s.getMin().intValue() - s.getMin().intValue())%s.getStep().intValue()==0?(s.getMin().intValue() - s.getMin().intValue())/s.getStep().intValue():(s.getMin().intValue() - s.getMin().intValue())/s.getStep().intValue() + 1);
                }else{
                    seekbar.setProgress(s.getMin().intValue()%s.getStep().intValue()==0?s.getMin().intValue()/s.getStep().intValue():s.getMin().intValue()/s.getStep().intValue() + 1);
                }
                if (s.getOptions() != null){
                    for (int i = 0; i < s.getOptions().size(); i++) {
                        if (s.getOptions().get(i).getScore() == s.getMin().intValue()){
                            text_seekbarnum.setText(s.getOptions().get(i).getOptionName()+":\n"+s.getOptions().get(i).getScore());//s.getOptions().get(i).getScore()
                            break;
                        }else{
                            if (s.getMin().intValue() > 0){
                                text_seekbarnum.setText("Value:\n"+(s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()));
                            }else{
                                text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue()));
                            }
                        }
                    }
                }else{
                    if (s.getMin().intValue() > 0){
                        text_seekbarnum.setText("Value:\n"+(s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()));
                    }else{
                        text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue()));
                    }
                }
            }else{
                if (s.getMin().intValue() > 0){
                    seekbar.setProgress((Integer.valueOf(s.getAnswer()) - s.getMin().intValue())%s.getStep().intValue()==0?(Integer.valueOf(s.getAnswer()) - s.getMin().intValue())/s.getStep().intValue():(Integer.valueOf(s.getAnswer()) - s.getMin().intValue())/s.getStep().intValue() + 1);
                }else{
                    seekbar.setProgress(Integer.valueOf(s.getAnswer())%s.getStep().intValue()==0?Integer.valueOf(s.getAnswer())/s.getStep().intValue():Integer.valueOf(s.getAnswer())/s.getStep().intValue() + 1);
                }
                if (s.getOptions() != null){
                    for (int i = 0; i < s.getOptions().size(); i++) {
                        if (s.getOptions().get(i).getScore() == (Integer.valueOf(s.getAnswer()) < s.getMax().intValue()?Integer.valueOf(s.getAnswer()):s.getMax().intValue())){
                            text_seekbarnum.setText(s.getOptions().get(i).getOptionName()+":\n"+s.getOptions().get(i).getScore());//s.getOptions().get(i).getScore()
                            break;
                        }else{
                            if (s.getMin().intValue() > 0){
                                text_seekbarnum.setText("Value:\n"+(s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()));
                            }else{
                                text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue()));
                            }
                        }
                    }
                }else{
                    if (s.getMin().intValue() > 0){
                        text_seekbarnum.setText("Value:\n"+(s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()));
                    }else{
                        text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue()));
                    }
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text_seekbarnum.getLayoutParams();
                params.leftMargin = (int) (((double) seekbar.getProgress() / seekbar.getMax()) * 1152 - (double) quotaWidth * seekbar.getProgress() / seekbar.getMax());
                if (seekbar.getProgress() > 0){
                    params.leftMargin = params.leftMargin - ((seekbar.getThumb().getIntrinsicWidth())*2);
                }else if (seekbar.getProgress() == 0){
                    params.leftMargin = params.leftMargin + seekbar.getThumb().getIntrinsicWidth()/2;
                }
                text_seekbarnum.setLayoutParams(params);
            }

            if (s.isComment()){
                childshow(position);
            }
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    // 数值改变
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (s.getMin().intValue() > 0){
                            if (s.getOptions() != null){
                                for (int i = 0; i < s.getOptions().size(); i++) {
                                    if (s.getOptions().get(i).getScore() == ((s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()) < s.getMax().intValue()?(s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()):s.getMax().intValue())){
                                        text_seekbarnum.setText(s.getOptions().get(i).getOptionName()+":\n"+s.getOptions().get(i).getScore());//s.getOptions().get(i).getScore()
                                        break;
                                    }else{
                                        text_seekbarnum.setText("Value:\n"+((s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()) < s.getMax().intValue()?(s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()):s.getMax().intValue()));
                                    }
                                }
                            }else{
                                text_seekbarnum.setText("Value:\n"+((s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()) < s.getMax().intValue()?(s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()):s.getMax().intValue()));
                            }
                        }else{
                            if (s.getOptions() != null){
                                for (int i = 0; i < s.getOptions().size(); i++) {
                                    if (s.getOptions().get(i).getScore() == (seekbar.getProgress() * s.getStep().intValue() < s.getMax().intValue()?seekbar.getProgress() * s.getStep().intValue():s.getMax().intValue())){
                                        text_seekbarnum.setText(s.getOptions().get(i).getOptionName()+":\n"+s.getOptions().get(i).getScore());//s.getOptions().get(i).getScore()
                                        break;
                                    }else{
                                        text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue() < s.getMax().intValue()?seekbar.getProgress() * s.getStep().intValue():s.getMax().intValue()));
                                    }
                                }
                            }else{
                                text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue() < s.getMax().intValue()?seekbar.getProgress() * s.getStep().intValue():s.getMax().intValue()));
                            }
                        }
                        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        text_seekbarnum.measure(spec, spec);
                        int quotaWidth = text_seekbarnum.getMeasuredWidth();

                        int spec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        text_seekbarnum.measure(spec2, spec2);
                        int sbWidth = seekbar.getMeasuredWidth();
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text_seekbarnum.getLayoutParams();
                        params.leftMargin = (int) (((double) seekBar.getProgress() / seekbar.getMax()) * sbWidth - (double) quotaWidth * seekBar.getProgress() / seekbar.getMax());
                        text_seekbarnum.setLayoutParams(params);
                        if (s.getMin().intValue() > 0){
                            s.setAnswer(String.valueOf((s.getMin().intValue() + seekBar.getProgress() * s.getStep().intValue())==0?1:(s.getMin().intValue() + seekBar.getProgress() * s.getStep().intValue())));
                        }else{
                            s.setAnswer(String.valueOf(seekBar.getProgress() * s.getStep().intValue()));
                        }
                    }
                    // 开始拖动
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    // 停止拖动
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        if (s.getMin().intValue() > 0){
//                            if (s.getOptions() != null){
//                                for (int i = 0; i < s.getOptions().size(); i++) {
//                                    if (s.getOptions().get(i).getScore() == ((s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()) < s.getMax().intValue()?(s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()):s.getMax().intValue())){
//                                        text_seekbarnum.setText(s.getOptions().get(i).getOptionName()+":\n"+s.getOptions().get(i).getScore());//s.getOptions().get(i).getScore()
//                                        break;
//                                    }else{
//                                        text_seekbarnum.setText("Value:\n"+((s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()) < s.getMax().intValue()?(s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()):s.getMax().intValue()));
//                                    }
//                                }
//                            }else{
//                                text_seekbarnum.setText("Value:\n"+((s.getMin().intValue() + seekbar.getProgress() * s.getStep().intValue()) < s.getMax().intValue()?(s.getMin().intValue() + seekbar.getProgress()* s.getStep().intValue()):s.getMax().intValue()));
//                            }
//                        }else{
//                            if (s.getOptions() != null){
//                                for (int i = 0; i < s.getOptions().size(); i++) {
//                                    if (s.getOptions().get(i).getScore() == (seekbar.getProgress() * s.getStep().intValue() < s.getMax().intValue()?seekbar.getProgress() * s.getStep().intValue():s.getMax().intValue())){
//                                        text_seekbarnum.setText(s.getOptions().get(i).getOptionName()+":\n"+s.getOptions().get(i).getScore());//s.getOptions().get(i).getScore()
//                                        break;
//                                    }else{
//                                        text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue() < s.getMax().intValue()?seekbar.getProgress() * s.getStep().intValue():s.getMax().intValue()));
//                                    }
//                                }
//                            }else{
//                                text_seekbarnum.setText("Value:\n"+(seekbar.getProgress() * s.getStep().intValue() < s.getMax().intValue()?seekbar.getProgress() * s.getStep().intValue():s.getMax().intValue()));
//                            }
//                        }
//                        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                        text_seekbarnum.measure(spec, spec);
//                        int quotaWidth = text_seekbarnum.getMeasuredWidth();
//
//                        int spec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                        text_seekbarnum.measure(spec2, spec2);
//                        int sbWidth = seekbar.getMeasuredWidth();
//                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text_seekbarnum.getLayoutParams();
//                        params.leftMargin = (int) (((double) seekBar.getProgress() / seekbar.getMax()) * sbWidth - (double) quotaWidth * seekBar.getProgress() / seekbar.getMax());
//                        text_seekbarnum.setLayoutParams(params);
//                        if (s.getMin().intValue() > 0){
//                            s.setAnswer(String.valueOf((s.getMin().intValue() + seekBar.getProgress() * s.getStep().intValue())==0?1:(s.getMin().intValue() + seekBar.getProgress() * s.getStep().intValue())));
//                        }else{
//                            s.setAnswer(String.valueOf(seekBar.getProgress() * s.getStep().intValue()));
//                        }
                    }
                });
            }else{
                seekbar.setEnabled(false);
            }
        }else if (s.getType() == 14){
            //时间选择 HH:MM
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            TextView title = holder.getView(R.id.text_datetitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_nocheck);
//            }
            final TextView text_datetime = holder.getView(R.id.text_datetime);
            text_datetime.setText(s.getAnswer());

            if (s.isComment()){
                childshow(position);
            }

            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                holder.getView(R.id.image_date).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = v.inflate(mContext, R.layout.datepicker_layout, null);
                        TextView positiveButton = view.findViewById(R.id.positiveButton);
                        TextView negativeButton = view.findViewById(R.id.negativeButton);

                        Calendar c = Calendar.getInstance();
                        int curHour = c.get(Calendar.HOUR_OF_DAY);//一天的第几小时
                        int curMin = c.get(Calendar.MINUTE);//一小时的第几分钟
                        hour = view.findViewById(R.id.hour);
                        hour.setVisibility(View.VISIBLE);
                        initHour();
                        mins = view.findViewById(R.id.mins);
                        mins.setVisibility(View.VISIBLE);
                        initMins();
                        //设置wheelview的默认数据下标
                        hour.setCurrentItem(curHour);
                        mins.setCurrentItem(curMin);
                        //设置wheelview的可见条目数
                        hour.setVisibleItems(7);
                        mins.setVisibleItems(7);

                        mDialog = new AlertDialog.Builder(mContext, R.style.dialog)
                                .setView(view)
                                .show();
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String str = String.format(Locale.CHINA,"%02d:%02d",hour.getCurrentItem(),mins.getCurrentItem());
                                s.setAnswer(str);
                                text_datetime.setText(str);
//                                onGridItemClickListener.onGridItemceshiClick(position);
                                mDialog.dismiss();
                            }
                        });
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDialog.dismiss();
                            }
                        });
                    }
                });
            }
        }else if (s.getType() == 16){
            //时间选择 DD/MM/YYYY
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            TextView title = holder.getView(R.id.text_datetitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_nocheck);
//            }
            final TextView text_datetime = holder.getView(R.id.text_datetime);
            text_datetime.setText(s.getAnswer());
//            if (!TextUtils.isEmpty(s.getAnswer())){
//                if (s.getAnswer().contains("-")){
//                    if (Other.isValidDate(s.getAnswer(),"dd-MM-yyyy") == true){
//                        text_datetime.setText(Other.timedate(Other.getTimeStamp(s.getAnswer(),"dd-MM-yyyy"),"dd/MM/yyyy"));
//                    }else if (Other.isValidDate(s.getAnswer(),"yyyy-MM-dd") == true){
//                        text_datetime.setText(Other.timedate(Other.getTimeStamp(s.getAnswer(),"yyyy-MM-dd"),"dd/MM/yyyy"));
//                    }
//                }else if (s.getAnswer().contains("/")){
//                    text_datetime.setText(Other.timedate(Other.getTimeStamp(s.getAnswer(),"yyyy/MM/dd"),"dd/MM/yyyy"));
//                }
//            }

            if (s.isComment()){
                childshow(position);
            }

            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                holder.getView(R.id.image_date).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = v.inflate(mContext, R.layout.datepicker_layout, null);
                        TextView positiveButton = view.findViewById(R.id.positiveButton);
                        TextView negativeButton = view.findViewById(R.id.negativeButton);

                        Calendar c = Calendar.getInstance();
                        int curYear = c.get(Calendar.YEAR);
                        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
                        int curDate = c.get(Calendar.DAY_OF_MONTH);//一月的第几天
                        year = view.findViewById(R.id.year);
                        year.setVisibility(View.VISIBLE);
                        initYear();
                        month = view.findViewById(R.id.month);
                        month.setVisibility(View.VISIBLE);
                        initMonth();
                        day = view.findViewById(R.id.day);
                        day.setVisibility(View.VISIBLE);
                        initDay(curYear,curMonth);
                        //设置wheelview的默认数据下标
                        year.setCurrentItem(curYear - 1900);
                        month.setCurrentItem(curMonth - 1);
                        day.setCurrentItem(curDate - 1);
                        //设置wheelview的可见条目数
                        year.setVisibleItems(7);
                        month.setVisibleItems(7);
                        day.setVisibleItems(7);

                        //为wheelview添加滑动事件
                        year.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                            }
                        });
                        month.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                            }
                        });

                        mDialog = new AlertDialog.Builder(mContext, R.style.dialog)
                                .setView(view)
                                .show();
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String str1 = String.format(Locale.CHINA,"%02d/%02d/%04d",day.getCurrentItem()+1,month.getCurrentItem() + 1,year.getCurrentItem() + 1900);
                                s.setAnswer(str1);
                                s.setHelpAnswer(str1);
                                text_datetime.setText(str1);
                                mDialog.dismiss();
//                                onGridItemClickListener.onGridItemceshiClick(position);//回调，重新加载，改变左侧标识的选中状态
                            }
                        });
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDialog.dismiss();
                            }
                        });
                    }
                });
            }
        }else if (s.getType() == 20){
            //label
            holder.setViewVisiable(R.id.layout_label, View.VISIBLE);
            TextView title = holder.getView(R.id.text_labeltitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_labelcheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_labelcheck,R.mipmap.section_nocheck);
//            }
            TextView text_label = holder.getView(R.id.text_label);
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                text_label.setText(s.getAnswer());
            }

            if (s.isComment()){
                childshow(position);
            }

        }else if (s.getType() == 21){
            //整数输入框
            holder.setViewVisiable(R.id.layout_integer, View.VISIBLE);
            TextView title = holder.getView(R.id.text_integertitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            final EditText edit_single = holder.getView(R.id.edit_integer);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
//                edit_single.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                inputManager.showSoftInput(edit_single, 0);
//                            }
//                        },500);
//                    }
//                });
            }else{
                edit_single.setEnabled(false);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_integercheck,R.mipmap.section_check);
//                if (s.isIscontrolschecked()){
//                    edit_single.setFocusable(true);
//                    edit_single.setFocusableInTouchMode(true);
//                    edit_single.requestFocus();
//                    edit_single.requestFocusFromTouch();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputManager.showSoftInput(edit_single, 0);
//                        }
//                    },500);
//                }
//            }else{
//                holder.setImageResource(R.id.image_integercheck,R.mipmap.section_nocheck);
//            }
            edit_single.setInputType(InputType.TYPE_CLASS_NUMBER);
            edit_single.setHint("Integer");
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                edit_single.setText(s.getAnswer());
                edit_single.setSelection(s.getAnswer().length());//将光标移至文字末尾
            }

            if (s.isComment()){
                childshow(position);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        s.setAnswer("");
                    } else {
                        s.setAnswer(editable.toString());
//                        if (Integer.valueOf(editable.toString()) >= s.getMin().intValue() && Integer.valueOf(editable.toString()) <= s.getMax().intValue()){
//                            s.setAnswer(editable.toString());
//                        }else{
//                            Toast.makeText(questionnaireActivity,"Please enter the correct value!",Toast.LENGTH_LONG).show();
//                        }
                    }
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (s.getType() == 22){
            //Float输入框
            holder.setViewVisiable(R.id.layout_float, View.VISIBLE);
            TextView title = holder.getView(R.id.text_floattitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            final EditText edit_single = holder.getView(R.id.edit_float);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
//                edit_single.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                inputManager.showSoftInput(edit_single, 0);
//                            }
//                        },500);
//                    }
//                });
            }else{
                edit_single.setEnabled(false);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_floatcheck,R.mipmap.section_check);
//                if (s.isIscontrolschecked()){
//                    edit_single.setFocusable(true);
//                    edit_single.setFocusableInTouchMode(true);
//                    edit_single.requestFocus();
//                    edit_single.requestFocusFromTouch();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            InputMethodManager  inputManager = (InputMethodManager)edit_single.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            inputManager.showSoftInput(edit_single, 0);
//                        }
//                    },500);
//                }
//            }else{
//                holder.setImageResource(R.id.image_floatcheck,R.mipmap.section_nocheck);
//            }
            edit_single.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edit_single.setHint("Float");
            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter()});//默认两位小数
//            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter().setDigits(3)});//手动设置其他位数，例如3
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                edit_single.setText(s.getAnswer());
                edit_single.setSelection(s.getAnswer().length());//将光标移至文字末尾
            }

            if (s.isComment()){
                childshow(position);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        s.setAnswer("");
                    } else {
                        s.setAnswer(editable.toString());
//                        if (Float.valueOf(editable.toString()) >= s.getMin().floatValue() && Float.valueOf(editable.toString()) <= s.getMax().floatValue()){
//                            s.setAnswer(editable.toString());
//                        }else{
//                            Toast.makeText(questionnaireActivity,"Please enter the correct value!",Toast.LENGTH_LONG).show();
//                        }
                    }
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (s.getType() == 25){
            //日期输出格式为:W12/D3/Y2015
            //W:第几个周 D:第几天(周一到周日，1-7) Y:年份
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            TextView title = holder.getView(R.id.text_datetitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_datecheck,R.mipmap.section_nocheck);
//            }
            final TextView text_datetime = holder.getView(R.id.text_datetime);
            text_datetime.setText(s.getAnswer());

            if (s.isComment()){
                childshow(position);
            }

            ImageView image_date = holder.getView(R.id.image_date);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                image_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = v.inflate(mContext, R.layout.datepicker_layout, null);
                        TextView positiveButton = view.findViewById(R.id.positiveButton);
                        TextView negativeButton = view.findViewById(R.id.negativeButton);

                        Calendar c = Calendar.getInstance();
                        int curYear = c.get(Calendar.YEAR);
                        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
                        int curDate = c.get(Calendar.DAY_OF_MONTH);//一月的第几天
                        year = view.findViewById(R.id.year);
                        year.setVisibility(View.VISIBLE);
                        initYear();
                        month = view.findViewById(R.id.month);
                        month.setVisibility(View.VISIBLE);
                        initMonth();
                        day = view.findViewById(R.id.day);
                        day.setVisibility(View.VISIBLE);
                        initDay(curYear,curMonth);
                        //设置wheelview的默认数据下标
                        year.setCurrentItem(curYear - 1900);
                        month.setCurrentItem(curMonth - 1);
                        day.setCurrentItem(curDate - 1);
                        //设置wheelview的可见条目数
                        year.setVisibleItems(7);
                        month.setVisibleItems(7);
                        day.setVisibleItems(7);

                        //为wheelview添加滑动事件
                        year.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                            }
                        });
                        month.addChangingListener(new OnWheelChangedListener() {
                            @Override
                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                            }
                        });

//                        Calendar c = Calendar.getInstance();
//                        int curYear = c.get(Calendar.YEAR);
//                        int curWeek = c.get(Calendar.WEEK_OF_YEAR);//一年的第几周
//                        int curDayofWeek = c.get(Calendar.DAY_OF_WEEK);//一周的第几天
//                        year = view.findViewById(R.id.year);
//                        year.setVisibility(View.VISIBLE);
//                        initYear();
//                        month = view.findViewById(R.id.month);
//                        month.setVisibility(View.VISIBLE);
//                        initDayofWeek(curYear,curWeek);
//                        day = view.findViewById(R.id.day);
//                        day.setVisibility(View.VISIBLE);
//                        initWeek();
//                        //设置三个wheelview的默认数据下标
//                        year.setCurrentItem(curYear - 1900);
//                        month.setCurrentItem(curDayofWeek - 2);
//                        day.setCurrentItem(curWeek - 1);
//                        //设置三个wheelview的可见条目数
//                        year.setVisibleItems(7);
//                        month.setVisibleItems(7);
//                        day.setVisibleItems(7);
//
//                        //为三个 wheelview添加滑动事件
//                        year.addChangingListener(new OnWheelChangedListener() {
//                            @Override
//                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
//                                initDayofWeek(year.getCurrentItem() + 1900,day.getCurrentItem() + 1);
//                            }
//                        });
//                        day.addChangingListener(new OnWheelChangedListener() {
//                            @Override
//                            public void onChanged(WheelView wheel, int oldValue, int newValue) {
//                                initDayofWeek(year.getCurrentItem() + 1900,day.getCurrentItem() + 1);
//
//                            }
//                        });

                        mDialog = new AlertDialog.Builder(mContext, R.style.dialog)
                                .setView(view)
                                .show();
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                String str1 = String.format(Locale.CHINA,"%1s%d/%1s%d/%1s%4d","W",day.getCurrentItem()+1,"D",month.getCurrentItem() + 1,"Y",year.getCurrentItem() + 1900);
                                String str1 = String.format(Locale.CHINA,"%02d/%02d/%04d",day.getCurrentItem()+1,month.getCurrentItem() + 1,year.getCurrentItem() + 1900);

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = null;
                                try {
                                    date = simpleDateFormat.parse(str1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);

                                int year = calendar.get(Calendar.YEAR);//年
                                int weekofyear = calendar.get(Calendar.WEEK_OF_YEAR);//一年的第几周
                                int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;//一周的第几天
                                //拼接时间(W19/D3/Y2019)
                                String xxx = "W"+(dayofweek == 0?(weekofyear - 1):weekofyear)+"/D"+(dayofweek == 0?7:dayofweek)+"/Y"+year;

                                s.setAnswer(xxx);
                                //存储时间正常值（dd/mm/yyyy）
//                                String str2 = Other.Timetransformation(day.getCurrentItem()+1,month.getCurrentItem() + 1,year.getCurrentItem() + 1900);
                                s.setHelpAnswer(str1);
                                text_datetime.setText(xxx);
//                                onGridItemClickListener.onGridItemceshiClick(position);
                                mDialog.dismiss();
                            }
                        });
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDialog.dismiss();
                            }
                        });
                    }
                });
            }
        }else if (s.getType() == 26){
            //Generate
            holder.setViewVisiable(R.id.layout_generate, View.VISIBLE);
            TextView title = holder.getView(R.id.text_generatetitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
            if (!TextUtils.isEmpty(s.getAnswer())){
                holder.setText(R.id.text_generate,s.getAnswer());
            }
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_generatecheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_generatecheck,R.mipmap.section_nocheck);
//            }

            if (s.isComment()){
                childshow(position);
            }
        }else if (s.getType() == 27){
            holder.setViewVisiable(R.id.layout_group, View.VISIBLE);
            holder.setText(R.id.label_expand_group,s.getTitle());
            if (s.isIsshow()) {
                holder.setImageResource(R.id.expanded,R.mipmap.down);
            } else {
                holder.setImageResource(R.id.expanded,R.mipmap.up);
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_expandablecheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_expandablecheck,R.mipmap.section_nocheck);
//            }

            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                holder.getView(R.id.layout_group).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (s.getLevel() == 1){
                            if(s.isIsshow()){
                                if (mDatas.get(position).getChildrens() != null){
                                    for (int i = 0; i < mDatas.get(position).getChildrens().size(); i++) {
                                        if (mDatas.get(position).getChildrens().get(i).getLevel() == 2){
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow()){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                                if (mDatas.get(position).getChildrens().get(i).getChildrens() != null){
                                                    for (int j = 0; j < mDatas.get(position).getChildrens().get(i).getChildrens().size(); j++) {
                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getLevel() == 3){
                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).isIsshow()){
                                                                mDatas.get(position).getChildrens().get(i).getChildrens().get(j).setIsshow(false);
                                                                mDatas.remove(position+1);
                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                                    for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getLevel() == 4){
                                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).isIsshow()){
                                                                                mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).setIsshow(false);
                                                                                mDatas.remove(position+1);
                                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens() != null){
                                                                                    for (int l = 0; l < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size(); l++) {
                                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getLevel() == 5){
                                                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).isIsshow()){
                                                                                                mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).setIsshow(false);
                                                                                                mDatas.remove(position+1);
                                                                                            }else{
                                                                                                mDatas.remove(position+1);
                                                                                            }
                                                                                        }else{
                                                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).isIsshow()){
                                                                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                                                                mDatas.remove(position+1);
                                                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getChildrens() != null){
                                                                                                    for (int m = 0; m < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getChildrens().size(); m++) {
                                                                                                        mDatas.remove(position+1);
                                                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getChildrens() != null){
                                                                                                            for (int n = 0; n < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().get(l).getChildrens().get(m).getChildrens().size(); n++) {
                                                                                                                mDatas.remove(position+1);
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }else{
                                                                                                mDatas.remove(position+1);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }else{
                                                                                mDatas.remove(position+1);
                                                                            }
                                                                        }else{
                                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).isIsshow()){
                                                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                                                mDatas.remove(position+1);
                                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens() != null){
                                                                                    for (int l = 0; l < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size(); l++) {
                                                                                        mDatas.remove(position+1);
                                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(l).getChildrens() != null){
                                                                                            for (int m = 0; m < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(l).getChildrens().size(); m++) {
                                                                                                mDatas.remove(position+1);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }else{
                                                                                mDatas.remove(position+1);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }else{
                                                                mDatas.remove(position+1);
                                                            }
                                                        }else{
                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).isIsshow()){
                                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                                mDatas.remove(position+1);
                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                                    for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++){
                                                                        mDatas.remove(position+1);
                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens() != null){
                                                                            for (int l = 0; l < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size(); l++) {
                                                                                mDatas.remove(position+1);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }else{
                                                                mDatas.remove(position+1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }else{
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow()){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                                if (mDatas.get(position).getChildrens().get(i).getChildrens() != null){
                                                    for (int j = 0; j < mDatas.get(position).getChildrens().get(i).getChildrens().size(); j++) {
                                                        mDatas.remove(position+1);
                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                            for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                                mDatas.remove(position+1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }
                                    }
                                }
                            }else{
                                mDatas.addAll(position+1,mDatas.get(position).getChildrens());
                            }
                        }else if (s.getLevel() == 2){
                            if(s.isIsshow()){
                                if (mDatas.get(position).getChildrens() != null){
                                    for (int i = 0; i < mDatas.get(position).getChildrens().size(); i++) {
                                        if (mDatas.get(position).getChildrens().get(i).getLevel() == 3){
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow()){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                                if (mDatas.get(position).getChildrens().get(i).getChildrens() != null){
                                                    for (int j = 0; j < mDatas.get(position).getChildrens().get(i).getChildrens().size(); j++) {
                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getLevel() == 4){
                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).isIsshow()){
                                                                mDatas.get(position).getChildrens().get(i).getChildrens().get(j).setIsshow(false);
                                                                mDatas.remove(position+1);
                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                                    for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getLevel() == 5){
                                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).isIsshow()){
                                                                                mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).setIsshow(false);
                                                                                mDatas.remove(position+1);
                                                                            }else{
                                                                                mDatas.remove(position+1);
                                                                            }
                                                                        }else{
                                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).isIsshow()){
                                                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                                                mDatas.remove(position+1);
                                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens() != null){
                                                                                    for (int l = 0; l < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size(); l++) {
                                                                                        mDatas.remove(position+1);
                                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(l).getChildrens() != null){
                                                                                            for (int m = 0; m < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(l).getChildrens().size(); m++) {
                                                                                                mDatas.remove(position+1);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }else{
                                                                                mDatas.remove(position+1);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }else{
                                                                mDatas.remove(position+1);
                                                            }
                                                        }else{
                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).isIsshow()){
                                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                                mDatas.remove(position+1);
                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                                    for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++){
                                                                        mDatas.remove(position+1);
                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens() != null){
                                                                            for (int l = 0; l < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size(); l++) {
                                                                                mDatas.remove(position+1);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }else{
                                                                mDatas.remove(position+1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }else{
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow() ==true){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                                if (mDatas.get(position).getChildrens().get(i).getChildrens() != null){
                                                    for (int j = 0; j < mDatas.get(position).getChildrens().get(i).getChildrens().size(); j++) {
                                                        mDatas.remove(position+1);
                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                            for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                                mDatas.remove(position+1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }
                                    }
                                }
                            }else{
                                mDatas.addAll(position+1,mDatas.get(position).getChildrens());
                            }
                        }else if (s.getLevel() == 3){
                            if(s.isIsshow()){
                                if (mDatas.get(position).getChildrens() != null){
                                    for (int i = 0; i < mDatas.get(position).getChildrens().size(); i++) {
                                        if (mDatas.get(position).getChildrens().get(i).getLevel() == 4){
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow()){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                                if (mDatas.get(position).getChildrens().get(i).getChildrens() != null){
                                                    for (int j = 0; j < mDatas.get(position).getChildrens().get(i).getChildrens().size(); j++) {
                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getLevel() == 5){
                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).isIsshow()){
                                                                mDatas.get(position).getChildrens().get(i).getChildrens().get(j).setIsshow(false);
                                                                mDatas.remove(position+1);
                                                            }else{
                                                                mDatas.remove(position+1);
                                                            }
                                                        }else{
                                                            if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).isIsshow()){
                                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                                mDatas.remove(position+1);
                                                                if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                                    for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++){
                                                                        mDatas.remove(position+1);
                                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens() != null){
                                                                            for (int l = 0; l < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().get(k).getChildrens().size(); l++) {
                                                                                mDatas.remove(position+1);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }else{
                                                                mDatas.remove(position+1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }else{
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow()){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                                if (mDatas.get(position).getChildrens().get(i).getChildrens() != null){
                                                    for (int j = 0; j < mDatas.get(position).getChildrens().get(i).getChildrens().size(); j++) {
                                                        mDatas.remove(position+1);
                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                            for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                                mDatas.remove(position+1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }
                                    }
                                }
                            }else{
                                mDatas.addAll(position+1,mDatas.get(position).getChildrens());
                            }
                        }else if (s.getLevel() == 4){
                            if(s.isIsshow()){
                                if (mDatas.get(position).getChildrens() != null){
                                    for (int i = 0; i < mDatas.get(position).getChildrens().size(); i++) {
                                        if (mDatas.get(position).getChildrens().get(i).getLevel() == 5){
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow()){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }else{
                                            if (mDatas.get(position).getChildrens().get(i).isIsshow()){
                                                mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                                mDatas.remove(position+1);
                                                if (mDatas.get(position).getChildrens().get(i).getChildrens() != null){
                                                    for (int j = 0; j < mDatas.get(position).getChildrens().get(i).getChildrens().size(); j++) {
                                                        mDatas.remove(position+1);
                                                        if (mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens() != null){
                                                            for (int k = 0; k < mDatas.get(position).getChildrens().get(i).getChildrens().get(j).getChildrens().size(); k++) {
                                                                mDatas.remove(position+1);
                                                            }
                                                        }
                                                    }
                                                }
                                            }else{
                                                mDatas.remove(position+1);
                                            }
                                        }
                                    }
                                }
                            }else{
                                mDatas.addAll(position+1,mDatas.get(position).getChildrens());
                            }
                        }else if (s.getLevel() == 5){
                            if(s.isIsshow()){
                                if (mDatas.get(position).getChildrens() != null){
                                    for (int i = 0; i < mDatas.get(position).getChildrens().size(); i++) {
                                        mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                        mDatas.remove(position+1);
                                    }
                                }
                            }else{
                                mDatas.addAll(position+1,mDatas.get(position).getChildrens());
                            }
                        }else{
                            if (s.isIsshow()){
                                if (mDatas.get(position).getChildrens() != null){
                                    for (int i = 0; i < mDatas.get(position).getChildrens().size(); i++) {
                                        mDatas.get(position).getChildrens().get(i).setIsshow(false);
                                        mDatas.remove(position+1);
                                    }
                                }
                            }else{
                                mDatas.addAll(position+1,mDatas.get(position).getChildrens());
                            }
                        }
                        onGridItemClickListener.onGridItemceshiClick(position);
                    }
                });
            }

            if (s.getLevel() == 1){
                holder.setViewBackgroundResource(R.id.rlayout_group,R.drawable.section1);
                holder.setTextColor(R.id.label_expand_group,questionnaireActivity.getResources().getColor(R.color.white));
            }else if (s.getLevel() == 2){
                holder.setViewBackgroundResource(R.id.rlayout_group,R.drawable.section2);
                holder.setTextColor(R.id.label_expand_group,questionnaireActivity.getResources().getColor(R.color.section2_text));
            }else if (s.getLevel() == 3){
                holder.setViewBackgroundResource(R.id.rlayout_group,R.drawable.section3);
                holder.setTextColor(R.id.label_expand_group,questionnaireActivity.getResources().getColor(R.color.white));
            }else if (s.getLevel() == 4){
                holder.setViewBackgroundResource(R.id.rlayout_group,R.drawable.section4);
                holder.setTextColor(R.id.label_expand_group,questionnaireActivity.getResources().getColor(R.color.section4_text));
            }else if (s.getLevel() == 5){
                holder.setViewBackgroundResource(R.id.rlayout_group,R.drawable.section5);
                holder.setTextColor(R.id.label_expand_group,questionnaireActivity.getResources().getColor(R.color.section5_text));
            }else{
                holder.setViewBackgroundResource(R.id.rlayout_group,R.drawable.section1);
                holder.setTextColor(R.id.label_expand_group,questionnaireActivity.getResources().getColor(R.color.white));
            }
        }else if (s.getType() == 29){
            holder.setViewVisiable(R.id.layout_activity, View.VISIBLE);
            TextView title = holder.getView(R.id.text_activitytitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }

            //是否展示
            if (s.isActivityshow() == true){
                holder.setViewVisiable(R.id.rlayout_activitydate, View.VISIBLE);
                holder.setViewVisiable(R.id.rlayout_activitypeople, View.VISIBLE);
                holder.setViewVisiable(R.id.layout_activitydescription, View.VISIBLE);
                holder.setViewVisiable(R.id.layout_activitycomment, View.VISIBLE);
            }else{
                holder.setViewVisiable(R.id.rlayout_activitydate, View.GONE);
                holder.setViewVisiable(R.id.rlayout_activitypeople, View.GONE);
                holder.setViewVisiable(R.id.layout_activitydescription, View.GONE);
                holder.setViewVisiable(R.id.layout_activitycomment, View.GONE);
            }

            //点击显示隐藏
            holder.getView(R.id.btn_activity).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (s.isActivityshow() == false){
                        s.setActivityshow(true);
                        holder.setViewVisiable(R.id.rlayout_activitydate, View.VISIBLE);
                        holder.setViewVisiable(R.id.rlayout_activitypeople, View.VISIBLE);
                        holder.setViewVisiable(R.id.layout_activitydescription, View.VISIBLE);
                        holder.setViewVisiable(R.id.layout_activitycomment, View.VISIBLE);
                    }else{
                        s.setActivityshow(false);
                        holder.setViewVisiable(R.id.rlayout_activitydate, View.GONE);
                        holder.setViewVisiable(R.id.rlayout_activitypeople, View.GONE);
                        holder.setViewVisiable(R.id.layout_activitydescription, View.GONE);
                        holder.setViewVisiable(R.id.layout_activitycomment, View.GONE);
                    }
                    onGridItemClickListener.onGridItemceshiClick(position);
                }
            });

            //设置时间
            final TextView text_activitydatetime = holder.getView(R.id.text_activitydatetime);

            final EditText edit_description = holder.getView(R.id.edit_description);
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getActivity().getDescription())){
                edit_description.setText(s.getActivity().getDescription());
                edit_description.setSelection(s.getActivity().getDescription().length());//将光标移至文字末尾
            }

            //设置人名
            final TextView text_activitypeoplename = holder.getView(R.id.text_activitypeoplename);

            final EditText edit_comment = holder.getView(R.id.edit_comment);
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getActivity().getComment())){
                edit_comment.setText(s.getActivity().getComment());
                edit_comment.setSelection(s.getActivity().getComment().length());//将光标移至文字末尾
            }


            //人名选择
            holder.getView(R.id.image_activitypeople).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!edit_description.getText().toString().equals("")){
                        itempos = position;
                        SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(questionnaireActivity);
                        editor.putInt(Preferences.ITEMPOSS, itempos);
                        try {
                            if (peoplesList == null){
                                peoplesList = ActivityPeople(s.getActivity().ActivityID);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(mContext,PeopleDialog.class);
                                    intent.putExtra("peoplesList", (Serializable) peoplesList);
                                    intent.putExtra("UserIds", s.getActivity().getUserIds());
                                    intent.putExtra("isCompleted", SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false));
                                    questionnaireActivity.startActivityForResult(intent,2);
                                }
                            },1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        questionnaireActivity.showTip("Description cannot be empty");
                    }
                }
            });
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
            //时间选择
            holder.getView(R.id.text_activitydatetime).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = v.inflate(mContext, R.layout.datepicker_layout, null);
                    TextView positiveButton = view.findViewById(R.id.positiveButton);
                    TextView negativeButton = view.findViewById(R.id.negativeButton);

                    Calendar c = Calendar.getInstance();
                    int curYear = c.get(Calendar.YEAR);
                    int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
                    int curDate = c.get(Calendar.DAY_OF_MONTH);//一月的第几天
                    year = view.findViewById(R.id.year);
                    year.setVisibility(View.VISIBLE);
                    initYear();
                    month = view.findViewById(R.id.month);
                    month.setVisibility(View.VISIBLE);
                    initMonth();
                    day = view.findViewById(R.id.day);
                    day.setVisibility(View.VISIBLE);
                    initDay(curYear,curMonth);
                    //设置wheelview的默认数据下标
                    year.setCurrentItem(curYear - 1900);
                    month.setCurrentItem(curMonth - 1);
                    day.setCurrentItem(curDate - 1);
                    //设置wheelview的可见条目数
                    year.setVisibleItems(7);
                    month.setVisibleItems(7);
                    day.setVisibleItems(7);

                    //为wheelview添加滑动事件
                    year.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                        }
                    });
                    month.addChangingListener(new OnWheelChangedListener() {
                        @Override
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                            initDay(year.getCurrentItem() + 1900,month.getCurrentItem() + 1);
                        }
                    });

                    mDialog = new AlertDialog.Builder(mContext, R.style.dialog)
                            .setView(view)
                            .show();
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String str2 = String.format(Locale.CHINA,"%02d/%02d/%04d",day.getCurrentItem()+1,month.getCurrentItem() + 1,year.getCurrentItem() + 1900);
                            s.getActivity().setTargetDate(str2);
                            text_activitydatetime.setText(str2);
//                            onGridItemClickListener.onGridItemceshiClick(position);
                            mDialog.dismiss();
                        }
                    });
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog.dismiss();
                        }
                    });
                }
            });



                if (edit_description.getTag() instanceof TextWatcher) {
                    edit_description.removeTextChangedListener((TextWatcher) edit_description.getTag());
                }

                TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (TextUtils.isEmpty(editable.toString())) {
                            s.getActivity().setDescription("");
                        } else {
                            s.getActivity().setDescription(editable.toString());
                        }
                    }
                };
                edit_description.addTextChangedListener(watcher);
                edit_description.setTag(watcher);

                if (edit_comment.getTag() instanceof TextWatcher) {
                    edit_comment.removeTextChangedListener((TextWatcher) edit_comment.getTag());
                }
                TextWatcher watcher1 = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (TextUtils.isEmpty(editable.toString())) {
                            s.getActivity().setComment("");
                        } else {
                            s.getActivity().setComment(editable.toString());
                        }
                    }
                };
                edit_comment.addTextChangedListener(watcher1);
                edit_comment.setTag(watcher1);
            }else{
                edit_description.setEnabled(false);
                edit_comment.setEnabled(false);
            }

            if (!TextUtils.isEmpty(s.getActivity().getTargetDate())){
                text_activitydatetime.setText(s.getActivity().getTargetDate());
            }else{
                //时间为空显示今天日期
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");// HH:mm:ss
                // 获取当前时间
                Date date = new Date(System.currentTimeMillis());
                text_activitydatetime.setText(simpleDateFormat.format(date));
            }

            if (!TextUtils.isEmpty(s.getActivity().getUserIds())){
                String[] selectpos = s.getActivity().getUserIds().split("[,]");
                if (selectpos.length > 1){
                    for (int i = 0; i < selectpos.length; i++) {
                        if (selectpos[i].equals(String.valueOf(userid))){
                            text_activitypeoplename.setText("Me + "+(selectpos.length - 1));
                            return;
                        }else{
                            text_activitypeoplename.setText(String.valueOf((selectpos.length)));
                        }
                    }
                }else{
                    if (selectpos[0].equals(String.valueOf(userid))){
                        text_activitypeoplename.setText("Me");
                    }else{
                        text_activitypeoplename.setText(String.valueOf((selectpos.length)));
                    }
                }
            }else{
                text_activitypeoplename.setText("Me");
            }


            if (s.isComment()){
                childshow(position);
            }

        }else if (s.getType() == 30){
            //Properties
            holder.setViewVisiable(R.id.layout_properties, View.VISIBLE);
            TextView title = holder.getView(R.id.text_propertiestitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }
//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_propertiescheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_propertiescheck,R.mipmap.section_nocheck);
//            }
            TextView text_label = holder.getView(R.id.text_properties);
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(s.getAnswer())){
                text_label.setText(s.getAnswer());
            }

            if (s.isComment()){
                childshow(position);
            }
        }else if (s.getType() == 31){
            holder.setViewVisiable(R.id.layout_table,View.VISIBLE);
            TextView title = holder.getView(R.id.text_tabletitle);
            if (!TextUtils.isEmpty(s.getNo()) && s.getNo() != null){
                title.setText(s.getNo()+". "+s.getTitle());
            }else{
                title.setText(s.getTitle());
            }
            //判断必填项和提示是否存在
            if (s.isRequired() && s.getHint() != null && !s.getHint().equals("")){//都存在
                addimage(title,3,s.getHint());
            }else if (s.isRequired() || s.getHint() != null && !s.getHint().equals("")){//存在一个
                if (s.isRequired()){
                    addimage(title,2,"");
                }else if (s.getHint() != null && !s.getHint().equals("")){
                    addimage(title,1,s.getHint());
                }
            }

//            if (s.isIschecked()){
//                holder.setImageResource(R.id.image_tablecheck,R.mipmap.section_check);
//            }else{
//                holder.setImageResource(R.id.image_tablecheck,R.mipmap.section_nocheck);
//            }
            table(s);
            final SwipeMenuRecyclerView recyclerview_table = holder.getView(R.id.recyclerview_table);
            //设置适配器
            TableAdapter tableAdapter = new TableAdapter(mContext,R.layout.item_table,alllistquestion);
            recyclerview_table.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerview_table.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));//分割线
            recyclerview_table.setItemViewSwipeEnabled(true);// 开启滑动删除。默认关闭。
            // 创建菜单：
            SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
           /* SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
            // 各种文字和图标属性设置。
            leftMenu.addMenuItem(deleteItem); // 在Item左侧添加一个菜单。*/


                    // 在Item右侧添加一个菜单。
                    // 1.编辑
                    // 各种文字和图标属性设置。
                    SwipeMenuItem modifyItem = new SwipeMenuItem(mContext)
                            .setBackgroundColor(mContext.getResources().getColor(R.color.loginbtn))
                            .setText("编辑")
                            .setTextColor(Color.BLACK)
                            .setTextSize(15) // 文字大小。
                            .setWidth(140)
                            .setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                    rightMenu.addMenuItem(modifyItem);
                    // 2 删除
                    SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                    deleteItem.setText("删除")
                            .setBackgroundColor(mContext.getResources().getColor(R.color.red))
                            .setTextColor(Color.WHITE) // 文字颜色。
                            .setTextSize(15) // 文字大小。
                            .setWidth(140)
                            .setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

                    rightMenu.addMenuItem(deleteItem);

                    // 注意：哪边不想要菜单，那么不要添加即可。
                }
            };
            // 设置监听器。
            recyclerview_table.setSwipeMenuCreator(mSwipeMenuCreator);

            SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
                @Override
                public void onItemClick(SwipeMenuBridge menuBridge) {
                    // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                    menuBridge.closeMenu();
                    int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
                    int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                    int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
                    Toast.makeText(mContext, direction + " " + adapterPosition + " " + menuPosition, Toast.LENGTH_SHORT).show();
                }
            };

            // 菜单点击监听。
            recyclerview_table.setSwipeMenuItemClickListener(mMenuItemClickListener);

            recyclerview_table.setAdapter(tableAdapter);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                tableAdapter.setOnGridItemClickListener(new TableAdapter.OnGridItemClickListener() {
                    @Override
                    public void onGridItemClick(int pos1, int pos2,String xxx) {
                        //重新获取当前table的值
                        table(s);

                        alllistquestion.get(pos1).get(pos2).setAnswer(xxx);

                        //需要循环把其它的值存到child里
                        for (int i = 0; i < alllistquestion.size(); i++) {
                            if (i > 0){
                                for (int j = 0; j < alllistquestion.get(i).size(); j++) {
                                    List<String> sss = new ArrayList<>();
                                    if (alllistquestion.get(0).get(j).getExtendVals() != null){
                                        for (int k = 0; k < alllistquestion.get(0).get(j).getExtendVals().size(); k++) {
                                            if (!TextUtils.isEmpty(alllistquestion.get(0).get(j).getExtendVals().get(k))){
                                                sss.add(alllistquestion.get(0).get(j).getExtendVals().get(k));
                                            }else{
                                                sss.add("");
                                            }
                                        }
                                    }else{
                                        if (!TextUtils.isEmpty(alllistquestion.get(i).get(j).getAnswer())){
                                            sss.add(alllistquestion.get(i).get(j).getAnswer());
                                        }else{
                                            sss.add("");
                                        }
                                    }

                                    if (!TextUtils.isEmpty(alllistquestion.get(i).get(j).getAnswer())){
                                        sss.set((i - 1),alllistquestion.get(i).get(j).getAnswer());
                                    }else{
                                        sss.set((i - 1),"");
                                    }
                                    alllistquestion.get(0).get(j).setExtendVals(sss);
                                }
                            }
                        }
                        mDatas.get(position).setChildrens(alllistquestion.get(0));
                        onGridItemClickListener.onGridItemceshiClick(position);
                    }
                });

                holder.getView(R.id.image_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < s.getChildrens().size(); i++) {
                            List<String> sss = new ArrayList<>();
                            if (s.getChildrens().get(i).getExtendVals() != null){
                                for (int j = 0; j < s.getChildrens().get(i).getExtendVals().size(); j++) {
                                    sss.add(s.getChildrens().get(i).getExtendVals().get(j));
                                }
                                sss.add("");
                            }else{
                                sss.add("");
                            }
                            s.getChildrens().get(i).setExtendVals(sss);
                        }
                        onGridItemClickListener.onGridItemceshiClick(position);
                    }
                });
            }
        }
    }

    //底部选择弹出框
    private void showPop() {
        View bottomView = View.inflate(questionnaireActivity, R.layout.layout_bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_audio);
        TextView mOther = bottomView.findViewById(R.id.tv_other);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);
        mDialog = new AlertDialog.Builder(questionnaireActivity, R.style.dialog)
                .setView(bottomView)
                .show();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //相册
                        selectPic(0,maxSelectNum - selectList.size());
                        break;
                    case R.id.tv_audio:
                        //音频
                        selectPic(3,maxSelectNum - selectList.size());
                        break;
                    case R.id.tv_other:
                        //其他文件
                        Intent intent = new Intent(questionnaireActivity,FileSelect.class);
                        intent.putExtra("number",maxSelectNum - selectList.size());
                        questionnaireActivity.startActivityForResult(intent,3);
                        break;
                    case R.id.tv_cancel:
                        //取消
                        //closePopupWindow();
                        break;
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mOther.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 打开相册或者照相机选择凭证图片，最多5张
     * @param maxTotal 最多选择的图片的数量
     */
    private void selectPic(int type,int maxTotal) {
        PictureSelectorConfig.initMultiConfig(type,questionnaireActivity, maxTotal);
    }

    //查看大图
    public void viewPluImg(int position) {
        LocalMedia media = selectList.get(position);
        String pictureType = media.getPictureType();
        switch (pictureType){
            case "image/jpeg":
                // 预览图片
                Intent intent = new Intent(questionnaireActivity, PlusImageActivity.class);
                intent.putParcelableArrayListExtra("selectList", (ArrayList<? extends Parcelable>) selectList);
                intent.putExtra("position", position);
                questionnaireActivity.startActivity(intent);
                break;
            case "video/mp4":
                // 预览视频
                Intent intent1 = new Intent(questionnaireActivity, PlusVideoActivity.class);
                intent1.putExtra("path", media.getPath());
                questionnaireActivity.startActivity(intent1);
                break;
            case "audio/mpeg":
                // 预览音频
                Intent intent2 = new Intent(questionnaireActivity, PlusAudioActivity.class);
                intent2.putExtra("path", media.getPath());
                questionnaireActivity.startActivity(intent2);
                break;
            case "text/plain":
                // 预览txt文档
                String filetype = Other.getFileName(media.getPath());
                if (media.getPath().contains("@")) {
                    int end = media.getPath().indexOf("@");
                    ImageShow imageShow = new ImageShow(questionnaireActivity,filetype);
                    imageShow.execute(BaseApi.getBaseUrl()+"OpenBook/IOSAPI/GetImg?ImgID="+media.getPath().substring(0, end));
                } else {
                    Other.openFile(questionnaireActivity,media.getPath());
                }
                break;
        }
    }

    // 添加选择的照片的地址
    public void refreshAdapter(final List<LocalMedia> picList) {
        // 例如 LocalMedia 里面返回三种path
        // 1.media.getPath(); 为原图path
        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
        // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
        mPicList = new ArrayList<>();
        for (LocalMedia localMedia : picList) {
            //被压缩后的图片路径
            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                mPicList.add(compressPath);
            }else{
                String Path = localMedia.getPath(); //未压缩路径
                mPicList.add(Path);
            }
        }

        StringBuffer sb = new StringBuffer();//进行拼接的字段
        if (mPicList.size() != 0){
            for (int i = 0; i < mPicList.size(); i++) {
                if ((i + 1) < mPicList.size()){
                    sb.append(mPicList.get(i)+",");
                }else{
                    sb.append(mPicList.get(i));
                }
                mDatas.get(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0)).setAnswer(String.valueOf(sb));
            }
        }else{
            mDatas.get(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0)).setAnswer("");
        }
        onGridItemClickListener.onGridItemceshiClick(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0));
//        notifyItemChanged(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 所有的选择结果回调
                    if (!TextUtils.isEmpty(mDatas.get(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0)).getAnswer())){
                        String[] selectpos = mDatas.get(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0)).getAnswer().split("[,]");
                        selectList = new ArrayList<>();
                        for (int i = 0; i < selectpos.length; i++) {
                            LocalMedia localMedia = new LocalMedia();
                            localMedia.setPath(selectpos[i]);
                            selectList.add(localMedia);//点击的设为选中.
                        }
                    }
                    selectList.addAll(PictureSelector.obtainMultipleResult(data));
                    refreshAdapter(selectList);
                    break;
                case 2:
                    peoplesList = (List<Questionnaire.Peoples>) data.getSerializableExtra("i");
                    // 所有的选择结果回调
                    List<String> useridlist = new ArrayList<>();
                    for (int i = 0; i < peoplesList.size(); i++){
                        if (peoplesList.get(i).getIsSelected() == 1){
                            useridlist.add(String.valueOf(peoplesList.get(i).getUSERID()));
                        }
                    }

                    StringBuffer sb = new StringBuffer();//进行拼接的字段
                    if (useridlist.size() != 0){
                        for (int i = 0; i < useridlist.size(); i++) {
                            if ((i + 1) < useridlist.size()){
                                sb.append(useridlist.get(i)+",");
                            }else{
                                sb.append(useridlist.get(i));
                            }
                            mDatas.get(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0)).getActivity().setUserIds(String.valueOf(sb));
                        }
                    }else{
                        mDatas.get(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0)).getActivity().setUserIds("");
                    }
                    onGridItemClickListener.onGridItemceshiClick(SharedPreferencesUtil.getsInstances(questionnaireActivity).getInt(Preferences.ITEMPOSS,0));
                    break;
                case 3:
                    List<FileEntity> fileEntityList = (List<FileEntity>) data.getSerializableExtra("i");
                    for (int i = 0; i < fileEntityList.size(); i++) {
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath(fileEntityList.get(i).getFilePath());
                        selectList.add(localMedia);
                    }
                    refreshAdapter(selectList);
                    break;
//                case PictureConfig.REQUEST_CAMERA:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 12:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 13:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 21:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 22:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 23:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 31:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 32:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;
//                case 33:
//                    expandableListAdapter.onActivityResult(requestCode, resultCode, data);
//                    break;

            }
        }
    }

    /**
     * 初始化年
     */
    private void initYear() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(mContext,1900, 2100);
        numericWheelAdapter.setLabel("");
//        numericWheelAdapter.setTextSize(15);  设置字体大小
        year.setViewAdapter(numericWheelAdapter);
        year.setCyclic(true);
    }

    /**
     * 初始化月
     */
    private void initMonth() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(mContext,1, 12, "%02d");
        numericWheelAdapter.setLabel("");
//        numericWheelAdapter.setTextSize(15);  设置字体大小
        month.setViewAdapter(numericWheelAdapter);
        month.setCyclic(true);
    }

    /**
     * 初始化天
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(mContext,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("");
//        numericWheelAdapter.setTextSize(15);  设置字体大小
        day.setViewAdapter(numericWheelAdapter);
        day.setCyclic(true);
    }

    /**
     * 初始化周
     */
    private void initWeek() {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(mContext,1, 53, "%02d");
        numericWheelAdapter.setLabel("");
//        numericWheelAdapter.setTextSize(15);  设置字体大小
        day.setViewAdapter(numericWheelAdapter);
        day.setCyclic(true);
    }

    /**
     * 初始化一周有几天
     */
    private void initDayofWeek(int arg1,int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(mContext,1, getDayofWeek(arg1,arg2), "%d");
        numericWheelAdapter.setLabel("");
//        numericWheelAdapter.setTextSize(15);  设置字体大小
        month.setViewAdapter(numericWheelAdapter);
        month.setCyclic(true);
        month.setVisibleItems(getDayofWeek(arg1,arg2));
    }

    /**
     * 初始化时
     */
    private void initHour() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(mContext,0, 23, "%02d");
        numericWheelAdapter.setLabel("");
//        numericWheelAdapter.setTextSize(15);  设置字体大小
        hour.setViewAdapter(numericWheelAdapter);
        hour.setCyclic(true);
    }

    /**
     * 初始化分
     */
    private void initMins() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(mContext,0, 59, "%02d");
        numericWheelAdapter.setLabel("");
//        numericWheelAdapter.setTextSize(15);  设置字体大小
        mins.setViewAdapter(numericWheelAdapter);
        mins.setCyclic(true);
    }

    /**
     *根据当前年份、月份获取当前月份的天数
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    /**
     *根据当前年份获取当前周的天数
     * @param year
     * @return
     */
    private int getDayofWeek(int year,int week) {
        int day = 7;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (week) {
            case 53:
                day = flag ? 2 : 1;
                break;
            default:
                day = 7;
                break;
        }
        return day;
    }


    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }


    public interface OnGridItemClickListener {
        void onGridItemceshiClick(int position);
    }

    public static class ImageShow extends AsyncTask<String,String,String> {
        private QuestionnaireActivity questionnaireActivity;
        String filetype;
        public ImageShow(QuestionnaireActivity questionnaireActivity, String filetype) {
            this.questionnaireActivity = questionnaireActivity;
            this.filetype = filetype;
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
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

            if (!TextUtils.isEmpty(s)){
                String path = "/storage/emulated/0/Android/data/com.example.vcserver.iqapture/" + System.currentTimeMillis() + filetype;
                try {
                    Other.decoderBase64File(s, path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Other.openFile(questionnaireActivity,path);

            }
        }
    }
    public List<Questionnaire.Question> resultObjects(){
        return mDatas;
    }

    //加载了子问题
    public void childshow(int position){
        //加载子问题
        if (mDatas.get(position).getChildrens() != null && mDatas.get(position).getChildrens().size() > 0){
            mDatas.get(position).setComment(false);
            mDatas.addAll(position+1,mDatas.get(position).getChildrens());
            onGridItemClickListener.onGridItemceshiClick(position);
        }
    }

    //加载衍生问题
    public void derivative(int position,int pos,String search){
        if (mDatas.get(position).getType() == 2){
            //多选
            int xx = 0;
            if (mDatas.get(position).getDeriveItems() != null&& mDatas.get(position).getDeriveItems().size() > 0){
                for (int i = 0; i < mDatas.get(position).getDeriveItems().size(); i++){
                    if (i < pos){//判断所选衍生问题item之前有几个item，累加衍生问题个数，方便按顺序添加删除
                        xx = xx + mDatas.get(position).getDeriveItems().get(i).getQuestions().size();
                    }else{
                        if (mDatas.get(position).getAnswer().contains(mDatas.get(position).getDeriveItems().get(pos).getValue())){//判断所选问题项答案是否包含所点击子item，包含则删除，不包含添加
                            mDatas.get(position).setIsderivativeshow(false);
                            for (int k = 0; k < mDatas.get(position).getDeriveItems().get(pos).getQuestions().size(); k++) {
                                mDatas.remove(position + 1 + xx);
                            }
                        }else{
                            mDatas.get(position).setIsderivativeshow(true);
                            mDatas.addAll(position + 1 + xx,mDatas.get(position).getDeriveItems().get(pos).getQuestions());

                            List<String> selected = new ArrayList<>();
                            //遍历答案集合，找到选中的选项添加到新的集合
                            for (int j = 0; j < mDatas.get(position).getOptions().size(); j++) {
                                if (mDatas.get(position).getOptions().get(j).isChecked()){
                                    selected.add(mDatas.get(position).getOptions().get(j).getOptionName());
                                }
                            }
                            //遍历选中选项的集合，组成字符串，添加到数据集中
                            StringBuffer sb = new StringBuffer();
                            for (int j = 0; j < selected.size(); j++) {
                                sb.append(selected.get(j)+"<|>");
                            }

                            mDatas.get(position).setAnswer(String.valueOf(sb));
                        }
                    }
                }
            }else{
                List<String> selected = new ArrayList<>();
                //遍历答案集合，找到选中的选项添加到新的集合
                for (int j = 0; j < mDatas.get(position).getOptions().size(); j++) {
                    if (mDatas.get(position).getOptions().get(j).isChecked()){
                        selected.add(mDatas.get(position).getOptions().get(j).getOptionName());
                    }
                }
                //遍历选中选项的集合，组成字符串，添加到数据集中
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < selected.size(); j++) {
                    sb.append(selected.get(j)+"<|>");
                }
                mDatas.get(position).setAnswer(String.valueOf(sb));
            }

        }else if (mDatas.get(position).getType() == 3){
            //是否具有衍生问题(单选)
            if (mDatas.get(position).getDeriveItems() != null&& mDatas.get(position).getDeriveItems().size() > 0){
                for (int i = 0; i < mDatas.get(position).getDeriveItems().size(); i++) {
                    if (mDatas.get(position).getDeriveItems().get(i).getValue().equals(mDatas.get(position).getAnswer())){
                        if (mDatas.get(position).isIsderivativeshow() == true){
                            if (mDatas.get(position).getDeriveItems().get(i).getQuestions() != null || mDatas.get(position).getDeriveItems().get(i).getQuestions().size() > 0){
                                for (int k = 0; k < mDatas.get(position).getDeriveItems().get(i).getQuestions().size(); k++) {
                                    mDatas.remove((position+1));
                                }
                                mDatas.get(position).setIsderivativeshow(false);
                            }
                        }
                        mDatas.get(position).setIsderivativeshow(true);
                        mDatas.addAll(position+1,mDatas.get(position).getDeriveItems().get(i).getQuestions());
                        mDatas.get(position).setAnswer(mDatas.get(position).getOptions().get(pos).getOptionName());
                        break;
                    }else{
                        if ((i + 1) <= mDatas.get(position).getDeriveItems().size()){
                            if (mDatas.get(position).isIsderivativeshow() == true){
                                for (int k = 0; k < mDatas.get(position).getDeriveItems().get(i).getQuestions().size(); k++) {
                                    mDatas.remove((position+1));
                                }
                                mDatas.get(position).setIsderivativeshow(false);
                            }
                        }
                    }
                }
            }else{
                mDatas.get(position).setAnswer(mDatas.get(position).getOptions().get(pos).getOptionName());
            }
        }else if (mDatas.get(position).getType() == 4){
            //是否具有衍生问题(下拉)
            if (mDatas.get(position).getDeriveItems() != null&& mDatas.get(position).getDeriveItems().size() > 0){
                for (int i = 0; i < mDatas.get(position).getDeriveItems().size(); i++) {
                        if (mDatas.get(position).getDeriveItems().get(i).getValue().equals(mDatas.get(position).getAnswer())){
                            if (mDatas.get(position).isIsderivativeshow() == true){
                                if (mDatas.get(position).getDeriveItems().get(i).getQuestions() != null || mDatas.get(position).getDeriveItems().get(i).getQuestions().size() > 0){
                                    for (int k = 0; k < mDatas.get(position).getDeriveItems().get(i).getQuestions().size(); k++) {
                                        mDatas.remove((position+1));
                                    }
                                    mDatas.get(position).setIsderivativeshow(false);
                                }
                            }
                            mDatas.get(position).setIsderivativeshow(true);
                            mDatas.addAll(position+1,mDatas.get(position).getDeriveItems().get(i).getQuestions());
                            if (search.equals("search")){
                                mDatas.get(position).setAnswer(optionDetails.getOptions().get(pos).getOptionName());//当下拉列表带搜索框时  mDatas.get(position).getOptions()里面衍生问题列表不存在
                            }else{
                                mDatas.get(position).setAnswer(mDatas.get(position).getOptions().get(pos).getOptionName());
                            }
                            break;
                        }else{
                            if ((i + 1) <= mDatas.get(position).getDeriveItems().size()){
                                if (mDatas.get(position).isIsderivativeshow() == true){
                                    for (int k = 0; k < mDatas.get(position).getDeriveItems().get(i).getQuestions().size(); k++) {
                                        mDatas.remove((position+1));
                                    }
                                    mDatas.get(position).setIsderivativeshow(false);
                                }
                            }
                        }
                }
            }else{
                mDatas.get(position).setAnswer(mDatas.get(position).getOptions().get(pos).getOptionName());
            }
        }
        onGridItemClickListener.onGridItemceshiClick(position);
    }

    private void addimage(final TextView text, final int size, final String hint) {
        text.post(new Runnable() {
            @Override
            public void run() {
//                //获取第一行的宽度
//                float lineWidth = text.getLayout().getLineWidth(0);
//                //获取第一行最后一个字符的下标
//                int lineEnd = text.getLayout().getLineEnd(0);
//                //计算每个字符占的宽度
//                float widthPerChar = lineWidth / (lineEnd + 1);
//                //计算TextView一行能够放下多少个字符
//                int numberPerLine = (int) Math.floor(text.getWidth() / widthPerChar);
//                //在原始字符串中插入一个空格，插入的位置为numberPerLine - 1
//                StringBuilder stringBuilder = new StringBuilder(text.getText().toString()).insert(numberPerLine - 1, " ");
                //注意此处showText后+ " "主要是为了占位
                SpannableString ss = new SpannableString(text.getText().toString() + "  ");
                ClickableSpan clickableSpan;
                if (size == 1){//提示
                    clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            View xxx = LayoutInflater.from(questionnaireActivity).inflate(R.layout.test, null);
                            TextView x = xxx.findViewById(R.id.xx);
                            x.setText(hint);
                            new BubbleDialog(questionnaireActivity)
                                    .addContentView(xxx)
                                    .setClickedView(text)
                                    .setPosition(BubbleDialog.Position.TOP, BubbleDialog.Position.RIGHT)
                                    .setTransParentBackground()
                                    .calBar(true)
                                    .setOffsetY(15)
                                    .show();
                        }
                    };
                    //添加位图图片
                    Drawable d = ContextCompat.getDrawable(mContext, (R.mipmap.warning));
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    //构建ImageSpan
                    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span, ss.length() - 2, ss.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    ss.setSpan(clickableSpan, ss.length() - 2, ss.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else if (size == 2){//必填
                    //添加位图图片
                    Drawable d = ContextCompat.getDrawable(mContext, (R.mipmap.qc_required));
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    //构建ImageSpan
                    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span, ss.length() - 1, ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }else if (size == 3){//所有
                    clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            View xxx = LayoutInflater.from(questionnaireActivity).inflate(R.layout.test, null);
                            TextView x = xxx.findViewById(R.id.xx);
                            x.setText(hint);
                            new BubbleDialog(questionnaireActivity)
                                    .addContentView(xxx)
                                    .setClickedView(text)
                                    .setPosition(BubbleDialog.Position.TOP, BubbleDialog.Position.RIGHT)
                                    .setTransParentBackground()
                                    .calBar(true)
                                    .setOffsetY(15)
                                    .show();
                        }
                    };
                    //添加位图图片 提示
                    Drawable d = ContextCompat.getDrawable(mContext, (R.mipmap.warning));
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    //构建ImageSpan
                    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span, ss.length() - 2, ss.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    ss.setSpan(clickableSpan, ss.length() - 2, ss.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    //必填
                    Drawable d1 = ContextCompat.getDrawable(mContext, (R.mipmap.qc_required));
                    d1.setBounds(0, 0, d1.getIntrinsicWidth(), d1.getIntrinsicHeight());
                    //构建ImageSpan
                    ImageSpan span1 = new ImageSpan(d1, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span1, ss.length() - 1, ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                text.setText(ss);
                text.setMovementMethod(LinkMovementMethod.getInstance());//ClickableSpan 触发点击事件 必加
                text.setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));//方法重新设置文字背景为透明色。
            }
        });
    }

    private IQOptionDetail searchDrow(int id, String searchValue, int page) throws Exception {
        optionDetails = new IQOptionDetail();
        OkHttpClient client = new OkHttpClient(); //构建FormBody，传入要提交的参数
        FormBody formBody;
        if (!TextUtils.isEmpty(searchValue) && !searchValue.equals("")){
            formBody = new FormBody
                    .Builder()
                    .add("companyId", String.valueOf(companyid))
                    .add("userId", String.valueOf(userid))
                    .add("fieldId", String.valueOf(id))
                    .add("searchValue", searchValue)
                    .add("page", String.valueOf(page))
                    .add("row", String.valueOf(10))
                    .build();
        }else{
            formBody = new FormBody
                    .Builder()
                    .add("companyId", String.valueOf(companyid))
                    .add("userId", String.valueOf(userid))
                    .add("fieldId", String.valueOf(id))
                    .add("page", String.valueOf(page))
                    .add("row", String.valueOf(10))
                    .build();
        }
        final Request request = new Request.Builder()
                .url(BaseApi.getBaseUrl()+"Intelligence/API/Capture/GetFieldItems")//http://test.valuechain.com/   http://192.168.1.35:8082/
                .post(formBody) .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                //失败
                Log.i("", "OptionDetailFailure: "+e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                optionDetails = JSON.parseObject(responseStr, IQOptionDetail.class);
                Log.i("", "onResponse: "+optionDetails);
            }
        });
        Log.i("", "onResponse: "+optionDetails);
        return optionDetails;
    }

    private List<Questionnaire.Peoples> ActivityPeople(int activityid) throws Exception {
        peoplesList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient(); //构建FormBody，传入要提交的参数
        FormBody formBody;
        formBody = new FormBody
                .Builder()
                .add("atcid", String.valueOf(activityid))
                .add("datasetId", String.valueOf(questionnaireActivity.datasetId))
                .add("uid", String.valueOf(userid))
                .add("cid", String.valueOf(companyid))
                .build();
        final Request request = new Request.Builder()
                .url(BaseApi.getBaseUrl()+"Intelligence/api/Capture/GetActivityPeople")
                .post(formBody) .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                //失败
                Log.i("", "peoplesListFailure: "+e.getMessage());
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                peoplesList = JSON.parseArray(responseStr, Questionnaire.Peoples.class);
                Log.i("", "onResponse: "+optionDetails);
            }
        });
        return peoplesList;
    }

    public void table(Questionnaire.Question s){
        alllistquestion = new ArrayList<>();
        alllistquestion.add(s.getChildrens());
        int xx = 0;//table item的个数
        for (int i = 0; i < s.getChildrens().size(); i++) {
            if (s.getChildrens().get(i).getExtendVals() != null){
                if (s.getChildrens().get(i).getExtendVals().size() > xx){
                    xx = s.getChildrens().get(i).getExtendVals().size();
                }
            }
        }
        for (int i = 0; i < xx; i++) {
            question = new ArrayList<>();
            for (int j = 0; j < s.getChildrens().size(); j++) {
                Questionnaire.Question question1 = new Questionnaire.Question();
                question1.setID(s.getChildrens().get(j).getID());
                question1.setTitle(s.getChildrens().get(j).getTitle());
                question1.setType(s.getChildrens().get(j).getType());
                if (s.getChildrens().get(j).getExtendVals() != null){
                    question1.setAnswer(s.getChildrens().get(j).getExtendVals().get(i));
                }else{
                    question1.setAnswer("");
                }
                question.add(question1);
            }
            alllistquestion.add(question);
        }
    }
}
