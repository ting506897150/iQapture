package com.example.vcserver.iqapture.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Questionnaire;
import com.example.vcserver.iqapture.commadapter.CommonAdapter;
import com.example.vcserver.iqapture.commadapter.ViewHolder;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.util.MoneyValueFilter;
import com.example.vcserver.iqapture.util.Other;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.util.adapter.NumericWheelAdapter;
import com.example.vcserver.iqapture.util.widget.OnWheelChangedListener;
import com.example.vcserver.iqapture.util.widget.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by VCServer on 2018/3/7.
 */

public class TableitemAdapter extends CommonAdapter<Questionnaire.Question> {
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView hour;
    private WheelView mins;
    private AlertDialog mDialog;

    private int pos = 0;
    public TableitemAdapter(Context context, int layoutId, List<Questionnaire.Question> datas) {
        super(context, layoutId, datas);
    }

    public List<Questionnaire.Question> getTableList(){
        return mDatas;
    }

    private Handler handler = new Handler();

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            onGridItemClickListener.onGridItemClick(pos);
        }
    };
    @Override
    public void convert(ViewHolder holder, final Questionnaire.Question question, final int position) {
        if (question.getType() == 0){
            //单行文本输入框
            holder.setViewVisiable(R.id.layout_text, View.VISIBLE);
            holder.setText(R.id.text_texttitle,question.getTitle());
            final EditText edit_single = holder.getView(R.id.edit_text);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }else{
                edit_single.setEnabled(false);
            }
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(question.getAnswer())){
                edit_single.setText(question.getAnswer());
                edit_single.setSelection(question.getAnswer().length());//将光标移至文字末尾
            }else{
                edit_single.setText("");
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
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }

                    if (TextUtils.isEmpty(editable.toString())) {
                        question.setAnswer("");
                    } else {
                        question.setAnswer(editable.toString());
                    }
                    pos = position;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 2000);
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (question.getType() == 1){
            //多行文本输入框
            holder.setViewVisiable(R.id.layout_moreedit, View.VISIBLE);
            holder.setText(R.id.text_moreedittitle,question.getTitle());
            final EditText edit_single = holder.getView(R.id.edit_multiline);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }else{
                edit_single.setEnabled(false);
            }
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(question.getAnswer())){
                edit_single.setText(question.getAnswer());
                edit_single.setSelection(question.getAnswer().length());//将光标移至文字末尾
            }else{
                edit_single.setText("");
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
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }

                    if (TextUtils.isEmpty(editable.toString())) {
                        question.setAnswer("");
                    } else {
                        question.setAnswer(editable.toString());
                    }
                    pos = position;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 1000);
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (question.getType() == 6){
            //时间选择 DD/MM/YYYY HH:MM
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            holder.setText(R.id.text_datetitle,question.getTitle());
            final TextView text_datetime = holder.getView(R.id.text_date);
            if (!TextUtils.isEmpty(question.getAnswer())){
                text_datetime.setText(question.getAnswer());
            }else{
                text_datetime.setText("");
            }
//            text_datetime.setText(Other.timedate(Other.getTimeStamp(question.getAnswer())));
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                text_datetime.setOnClickListener(new View.OnClickListener() {
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
                                text_datetime.setText(str);
                                question.setAnswer(str);
                                mDialog.dismiss();
                                onGridItemClickListener.onGridItemClick(position);
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

        }else if (question.getType() == 9){
            //数字输入框
            holder.setViewVisiable(R.id.layout_number, View.VISIBLE);
            holder.setText(R.id.text_numbertitle,question.getTitle());
            final EditText edit_single = holder.getView(R.id.edit_number);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }else{
                edit_single.setEnabled(false);
            }
            edit_single.setHint("Number");
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(question.getAnswer())){
                edit_single.setText(question.getAnswer());
                edit_single.setSelection(question.getAnswer().length());//将光标移至文字末尾
            }else{
                edit_single.setText("");
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
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }

                    if (TextUtils.isEmpty(editable.toString())) {
                        question.setAnswer("");
                    } else {
                        question.setAnswer(editable.toString());
                    }
                    pos = position;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 1000);
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (question.getType() == 10){
            //货币输入框
            holder.setViewVisiable(R.id.layout_money, View.VISIBLE);
            holder.setText(R.id.text_moneytitle,question.getTitle());
            final EditText edit_single = holder.getView(R.id.edit_money);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }else{
                edit_single.setEnabled(false);
            }
            edit_single.setHint("Currency");
            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter()});//默认两位小数
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(question.getAnswer())){
                edit_single.setText(question.getAnswer());
                edit_single.setSelection(question.getAnswer().length());//将光标移至文字末尾
            }else{
                edit_single.setText("");
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
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }

                    if (TextUtils.isEmpty(editable.toString())) {
                        question.setAnswer("");
                    } else {
                        question.setAnswer(editable.toString());
                    }
                    pos = position;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 1000);
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (question.getType() == 11){
            //百分比输入框
            holder.setViewVisiable(R.id.layout_percent, View.VISIBLE);
            holder.setText(R.id.text_percenttitle,question.getTitle());
            final EditText edit_single = holder.getView(R.id.edit_percent);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }else{
                edit_single.setEnabled(false);
            }
            edit_single.setHint("Percentage");
            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter()});//默认两位小数
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(question.getAnswer())){
                edit_single.setText(question.getAnswer());
                edit_single.setSelection(question.getAnswer().length());//将光标移至文字末尾
            }else{
                edit_single.setText("");
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
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }

                    if (TextUtils.isEmpty(editable.toString())) {
                        question.setAnswer("");
                    } else {
                        question.setAnswer(editable.toString());
                    }
                    pos = position;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 1000);
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (question.getType() == 14){
            //时间选择 HH:MM
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            holder.setText(R.id.text_datetitle,question.getTitle());
            final TextView text_datetime = holder.getView(R.id.text_date);
            if (!TextUtils.isEmpty(question.getAnswer())){
                text_datetime.setText(question.getAnswer());
            }else{
                text_datetime.setText("");
            }
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                text_datetime.setOnClickListener(new View.OnClickListener() {
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
                                text_datetime.setText(str);
                                question.setAnswer(str);
                                question.setHelpAnswer(str);
                                mDialog.dismiss();
                                onGridItemClickListener.onGridItemClick(position);
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

        }else if (question.getType() == 16){
            //时间选择 DD/MM/YYYY
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            holder.setText(R.id.text_datetitle,question.getTitle());
            final TextView text_datetime = holder.getView(R.id.text_date);
            if (!TextUtils.isEmpty(question.getAnswer())){
                text_datetime.setText(question.getAnswer());
            }else{
                text_datetime.setText("");
            }
//            text_datetime.setText(Other.timedate(Other.getTimeStamp(question.getAnswer())));
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                text_datetime.setOnClickListener(new View.OnClickListener() {
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
                                String str = String.format(Locale.CHINA,"%02d/%02d/%04d",day.getCurrentItem()+1,month.getCurrentItem() + 1,year.getCurrentItem() + 1900);
                                text_datetime.setText(str);
                                question.setAnswer(str);
                                question.setHelpAnswer(str);
                                mDialog.dismiss();
                                onGridItemClickListener.onGridItemClick(position);
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

        }else if (question.getType() == 21){
            //整数输入框
            holder.setViewVisiable(R.id.layout_integer, View.VISIBLE);
            holder.setText(R.id.text_integertitle,question.getTitle());
            final EditText edit_single = holder.getView(R.id.edit_integer);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }else{
                edit_single.setEnabled(false);
            }
            edit_single.setHint("Integer");
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(question.getAnswer())){
                edit_single.setText(question.getAnswer());
                edit_single.setSelection(question.getAnswer().length());//将光标移至文字末尾
            }else{
                edit_single.setText("");
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
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }

                    if (TextUtils.isEmpty(editable.toString())) {
                        question.setAnswer("");
                    } else {
                        question.setAnswer(editable.toString());
                    }
                    pos = position;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 1000);
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (question.getType() == 22){
            //Float输入框
            holder.setViewVisiable(R.id.layout_float, View.VISIBLE);
            holder.setText(R.id.text_floattitle,question.getTitle());
            final EditText edit_single = holder.getView(R.id.edit_float);
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){

            }else{
                edit_single.setEnabled(false);
            }
            edit_single.setHint("Float");
            edit_single.setFilters(new InputFilter[]{new MoneyValueFilter()});//默认两位小数
            holder.setIsRecyclable(false);
            if (edit_single.getTag() instanceof TextWatcher) {
                edit_single.removeTextChangedListener((TextWatcher) edit_single.getTag());
            }
            //判断取出数据是否为空，不为空则赋值
            if (!TextUtils.isEmpty(question.getAnswer())){
                edit_single.setText(question.getAnswer());
            }else{
                edit_single.setText("");
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
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }

                    if (TextUtils.isEmpty(editable.toString())) {
                        question.setAnswer("");
                    } else {
                        question.setAnswer(editable.toString());
                    }
                    pos = position;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 1000);
                }
            };
            edit_single.addTextChangedListener(watcher);
            edit_single.setTag(watcher);
        }else if (question.getType() == 25){
            //时间选择 W12/D3/Y2015
            //W:第几个周 D:第几天(周一到周日，1-7) Y:年份
            holder.setViewVisiable(R.id.layout_date, View.VISIBLE);
            holder.setText(R.id.text_datetitle,question.getTitle());
            final TextView text_datetime = holder.getView(R.id.text_date);
            if (!TextUtils.isEmpty(question.getAnswer())){
                text_datetime.setText(question.getAnswer());
            }else{
                text_datetime.setText("");
            }
            if (SharedPreferencesUtil.getsInstances(mContext).getBoolean(Preferences.ISCOMPLETED,false) == false){
                text_datetime.setOnClickListener(new View.OnClickListener() {
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
//                                String str = String.format(Locale.CHINA,"%1s%d/%1s%d/%1s%4d","W",day.getCurrentItem()+1,"D",month.getCurrentItem() + 1,"Y",year.getCurrentItem() + 1900);

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

                                int year = calendar.get(Calendar.YEAR);//哪一年
                                int weekofyear = calendar.get(Calendar.WEEK_OF_YEAR);//一年的第几周
                                int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;//一周的第几天
                                //拼接时间(W19/D3/Y2019)
                                String xxx = "W"+(dayofweek == 0?(weekofyear - 1):weekofyear)+"/D"+(dayofweek == 0?7:dayofweek)+"/Y"+year;

                                question.setAnswer(xxx);
                                //存储时间正常值（dd/mm/yyyy）
//                                String str2 = Other.Timetransformation(day.getCurrentItem()+1,month.getCurrentItem() + 1,year.getCurrentItem() + 1900);
                                question.setHelpAnswer(str1);
                                text_datetime.setText(xxx);
                                mDialog.dismiss();
                                onGridItemClickListener.onGridItemClick(position);
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

        }
    }

    /**
     * 初始化年
     */
    private void initYear() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(mContext,1900, 2100);
        numericWheelAdapter.setLabel("");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        year.setViewAdapter(numericWheelAdapter);
        year.setCyclic(true);
    }

    /**
     * 初始化月
     */
    private void initMonth() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(mContext,1, 12, "%02d");
        numericWheelAdapter.setLabel("");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        month.setViewAdapter(numericWheelAdapter);
        month.setCyclic(true);
    }

    /**
     * 初始化天
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(mContext,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
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



    //利用接口回调点击
    private OnGridItemClickListener onGridItemClickListener;//点击


    public void setOnGridItemClickListener(OnGridItemClickListener onGridItemClickListener) {
        this.onGridItemClickListener = onGridItemClickListener;
    }


    public interface OnGridItemClickListener {
        void onGridItemClick(int position);
    }
}
