package com.example.vcserver.iqapture.view.other;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vcserver.iqapture.R;
import com.example.vcserver.iqapture.bean.Address;
import com.example.vcserver.iqapture.commadapter.OnItemClickListener;
import com.example.vcserver.iqapture.config.Preferences;
import com.example.vcserver.iqapture.util.LinerLayoutItemDecoration;
import com.example.vcserver.iqapture.util.SharedPreferencesUtil;
import com.example.vcserver.iqapture.view.adapter.AddressAdapter;
import com.example.vcserver.iqapture.view.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by VCServer on 2018/3/22.
 */

public class SettingActivity extends BaseActivity {
    @Bind(R.id.text_tab)
    TextView textTab;
    @Bind(R.id.text_determine)
    TextView textDetermine;
    SharedPreferencesUtil editor;

    RecyclerView recyclerview;
    AddressAdapter addressAdapter;

    List<Address> addressList = new ArrayList<>();

    String[] name = new String[]{"https://my.valuechain.com/","https://alpha.valuechain.com/","https://beta.valuechain.com/","https://test.valuechain.com/","Custom IP"};

    String url;
    private AlertDialog mDialog;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void init() {
        editor = SharedPreferencesUtil.getsInstances(SettingActivity.this);
        textTab.setText("Setting");
        textDetermine.setText("Complete");
        recyclerview = findViewById(R.id.recyclerview);
        for (int i = 0; i < name.length; i++) {
            Address address = new Address();
            address.setName(name[i]);
            address.setChecked(false);
            addressList.add(address);
        }
        addressAdapter = new AddressAdapter(mContext,R.layout.item_addressrecyclerview,addressList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new LinerLayoutItemDecoration(mContext, R.drawable.item_dirver_listview));
        recyclerview.setAdapter(addressAdapter);
        addressAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (addressList.get(position).getName().equals("Custom IP")){
                    for (int i = 0; i < addressList.size(); i++) {
                        addressList.get(i).setChecked(false);
                    }
                    showAlertDialog();
                }else{
                    //设置其他选择项全部为未选中
                    for (int i = 0; i < addressList.size(); i++) {
                        addressList.get(i).setChecked(false);
                    }
                    addressList.get(position).setChecked(true);//点击的设为选中.

                    url = addressList.get(position).getName();//选中的url
                }
                addressAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick({R.id.img_back,R.id.text_determine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_determine:
                showAlertDialogs();

                break;
        }
    }

    // 点击自定义ip弹出提示信息
    public void showAlertDialog() {
        View view = View.inflate(this, R.layout.dialog, null);
        TextView title = view.findViewById(R.id.title);
        final EditText edit = view.findViewById(R.id.edit);
        TextView positiveButton = view.findViewById(R.id.positiveButton);
        TextView negativeButton = view.findViewById(R.id.negativeButton);
        title.setText("Customize the IP address");
        edit.setVisibility(View.VISIBLE);
        positiveButton.setText("determine");
        mDialog = new AlertDialog.Builder(this, R.style.dialog)
                .setView(view)
                .show();
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = edit.getText().toString();
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

    // 点击确认弹出提示信息
    public void showAlertDialogs() {
        View view = View.inflate(this, R.layout.dialog, null);
        TextView title = view.findViewById(R.id.title);
        TextView message = view.findViewById(R.id.message);
        TextView positiveButton = view.findViewById(R.id.positiveButton);
        TextView negativeButton = view.findViewById(R.id.negativeButton);
        title.setText("Whether to switch to the following IP");
        message.setVisibility(View.VISIBLE);
        message.setText(url);
        positiveButton.setText("Determine");
        mDialog = new AlertDialog.Builder(this, R.style.dialog)
                .setView(view)
                .show();
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(url)) {
                    //返回登陆页面
                    editor.putString(Preferences.LOGIN, url);
                    editor.putString(Preferences.USERNAME, "");
                    editor.putString(Preferences.PASSWORD, "");
                    editor.putInt(Preferences.USERID, 0);
                    editor.putInt(Preferences.COMPANYID, 0);
                    appManager.finish(MainActivity.class);
                    startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    finish();
                } else{
                    showTip("IP paths cannot be empty!");
                }
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

    @Override
    protected void onDestroy() {
        appManager.destory(this);
        super.onDestroy();
    }
}
