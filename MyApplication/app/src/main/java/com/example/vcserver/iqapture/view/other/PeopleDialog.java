package com.example.vcserver.iqapture.view.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.OnItemClickListener;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.util.FullyLinearLayoutManager;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.adapter.PeopleAdapter;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VCServer on 2018/5/10.
 */

public class PeopleDialog extends Activity implements View.OnClickListener{
    EditText editSearch;
    RecyclerView recyclerPeople;
    AutoLinearLayout layoutClose;

    //展示的数据集合
    List<Questionnaire.Peoples> peoplesList = new ArrayList<>();
    //总数据集合
    List<Questionnaire.Peoples> seachList = new ArrayList<>();
    //查询出来的数据集合
    List<Questionnaire.Peoples> seachlist;

    Questionnaire.Peoples peoples;
    PeopleAdapter peopleAdapter;
    public int userid;
    public String UserIds;
    public boolean isCompleted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_layout);
        peoplesList = (List<Questionnaire.Peoples>) getIntent().getSerializableExtra("peoplesList");//存储人员列表的数据集
        UserIds = getIntent().getStringExtra("UserIds");
        userid = SharedPreferencesUtil.getsInstances(this).getInt(Preferences.USERID, 0);
        isCompleted = SharedPreferencesUtil.getsInstances(this).getBoolean(Preferences.ISCOMPLETED, false);
        //当什么都没有选中的时候  需要默认选中当前用户
        if (TextUtils.isEmpty(UserIds)){
            for (int i = 0; i < peoplesList.size(); i++) {
                if (peoplesList.get(i).getUSERID() == userid){
                    peoplesList.get(i).setIsSelected(1);
                }
            }
        }else{
            //根据userids选中用户
            for (int i = 0; i < peoplesList.size(); i++) {
                String[] selectpos = UserIds.split("[,]");
                for (int j = 0; j < selectpos.length; j++) {
                    if (String.valueOf(peoplesList.get(i).getUSERID()).equals(selectpos[j])){
                        peoplesList.get(i).setIsSelected(1);
                    }
                }
            }
        }

        seachList.addAll(peoplesList);
        initView();
        adapter(peoplesList);
    }

    protected void initView() {
        editSearch = findViewById(R.id.edit_search);
        recyclerPeople = findViewById(R.id.recycler_people);
        layoutClose = findViewById(R.id.layout_close);
        layoutClose.setOnClickListener(this);
        //设置搜索图标
        Drawable seach = this.getResources().getDrawable(R.mipmap.check);
        seach.setBounds(0, 0, 80, 80);
        editSearch.setCompoundDrawables(seach, null, null, null);//图片放在那里

        //搜索框监听用户输入
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //内容改变之前调用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //内容改变中
                Drawable clear = getResources().getDrawable(R.mipmap.delete);
                Drawable seach = getResources().getDrawable(R.mipmap.check);
                clear.setBounds(0, 0, 40, 40);
                seach.setBounds(0, 0, 80, 80);
                editSearch.setCompoundDrawables(seach, null, clear, null);//图片放在那里
            }

            @Override
            public void afterTextChanged(Editable s) {
                //内容改变之后调用
                if (!TextUtils.isEmpty(editSearch.getText().toString())) {
                    seachlist = new ArrayList<>();
                    for (int i = 0; i < seachList.size(); i++) {
                        if (seachList.get(i).getUserName().toString().toLowerCase().contains(String.valueOf(s).toLowerCase())) {
                            peoples = new Questionnaire.Peoples();
                            peoples.setUSERID(seachList.get(i).getUSERID());
                            peoples.setUserName(seachList.get(i).getUserName());
                            peoples.setIsSelected(seachList.get(i).getIsSelected());
                            seachlist.add(peoples);
                        }
                    }
                    adapter(seachlist);
                }else{
                    adapter(seachList);
                    //无数据时只显示左边图标
                    Drawable seach = getResources().getDrawable(R.mipmap.check);
                    seach.setBounds(0, 0, 80, 80);
                    editSearch.setCompoundDrawables(seach, null, null, null);//图片放在那里
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
                    //清空数据时只显示左边图标
                    Drawable seach = getResources().getDrawable(R.mipmap.check);
                    seach.setBounds(0, 0, 80, 80);
                    editSearch.setCompoundDrawables(seach, null, null, null);//图片放在那里
                }
                return false;
            }
        });

        //获取焦点事件
        editSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            }
        });
    }

    private void adapter(final List<Questionnaire.Peoples> peoplesList) {
        peopleAdapter = new PeopleAdapter(this,R.layout.item_people, peoplesList);
        recyclerPeople.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerPeople.setAdapter(peopleAdapter);
        peopleAdapter.notifyDataSetChanged();
        if (isCompleted == false){
            peopleAdapter.setmOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int pos) {

                    int xxx = 0;
                    for (int i = 0; i < peoplesList.size(); i++) {
                        if (peoplesList.get(i).getIsSelected() == 1){
                            xxx++;
                        }
                    }

                    //判断是否已选中，如果已选中则取消选中
                    if (peoplesList.get(pos).getIsSelected() == 1){
                        if (xxx == 1){
                            //提示 选择不能少于一个
                        }else{
                            peoplesList.get(pos).setIsSelected(0);
                        }
                    }else{
                        peoplesList.get(pos).setIsSelected(1);//点击的设为选中.
                    }
                    peopleAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCompleted == false){
            //数据是使用Intent返回
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra("i", (Serializable) peoplesList);
            //设置返回数据
            setResult(RESULT_OK, intent);
            finish();//点击窗口外部区域 弹窗消失
        }else{
            finish();//点击窗口外部区域 弹窗消失
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (isCompleted == false){
            //数据是使用Intent返回
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra("i", (Serializable) peoplesList);
            //设置返回数据
            setResult(RESULT_OK, intent);
            finish();//点击窗口外部区域 弹窗消失
        }else{
            finish();//点击窗口外部区域 弹窗消失
        }
    }
}
