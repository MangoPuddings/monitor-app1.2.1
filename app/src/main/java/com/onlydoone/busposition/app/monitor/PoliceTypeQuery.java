package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.adapter.GridViewAdapter;
import com.onlydoone.busposition.bean.PoliceTypeData;
import com.onlydoone.busposition.classs.ConstantClass;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆报警类型
 * Created by zhaohui on 2017/3/15.
 */
public class PoliceTypeQuery extends Activity implements View.OnClickListener {
    private int isSelect1 = -1, isSelect2 = -1, isSelect3 = -1, isSelect4 = -1, isSelect5 = -1,
            isSelect6 = -1, isSelect7 = -1, isSelect8 = -1, isSelect9 = -1, isSelect10 = -1,
            isSelect11 = -1, isSelect12 = -1;
    private LinearLayout layout_police;

    String[] mun;
    String[] types;

    /**
     * 返回按钮
     */
    private ImageView title_break;
    /**
     * title显示内容
     */
    private TextView title_context;
    private TextView police_type1, police_type2, police_type3, police_type4, police_type5,
            police_type6, police_type7, police_type8, police_type9, police_type10,
            police_type11, police_type12;
    /**
     * gridView 适配器
     */
    private GridViewAdapter mGridViewAdapter;
    private GridView mGridView;
    /**
     * 确定按钮
     */
    private TextView tv_police_type_query;
    /**
     * gridView数据源list
     */
    private List<PoliceTypeData> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.police_type_query);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_break = (ImageView) findViewById(R.id.title_break);
        title_context = (TextView) findViewById(R.id.title_context);
        police_type1 = (TextView) findViewById(R.id.police_type1);
        police_type2 = (TextView) findViewById(R.id.police_type2);
        police_type3 = (TextView) findViewById(R.id.police_type3);
        police_type4 = (TextView) findViewById(R.id.police_type4);
        police_type5 = (TextView) findViewById(R.id.police_type5);
        police_type6 = (TextView) findViewById(R.id.police_type6);
        police_type7 = (TextView) findViewById(R.id.police_type7);
        police_type8 = (TextView) findViewById(R.id.police_type8);
        police_type9 = (TextView) findViewById(R.id.police_type9);
        police_type10 = (TextView) findViewById(R.id.police_type10);
        police_type11 = (TextView) findViewById(R.id.police_type11);
        police_type12 = (TextView) findViewById(R.id.police_type12);
        mGridView = (GridView) findViewById(R.id.gridView);
        tv_police_type_query = (TextView) findViewById(R.id.tv_police_type_query);
        layout_police = (LinearLayout) findViewById(R.id.layout_police);
        title_context.setText("报警类型");

        //报警类型点击事件
        police_type1.setOnClickListener(this);
        police_type2.setOnClickListener(this);
        police_type3.setOnClickListener(this);
        police_type4.setOnClickListener(this);
        police_type5.setOnClickListener(this);
        police_type6.setOnClickListener(this);
        police_type7.setOnClickListener(this);
        police_type8.setOnClickListener(this);
        police_type9.setOnClickListener(this);
        police_type10.setOnClickListener(this);
        police_type11.setOnClickListener(this);
        police_type12.setOnClickListener(this);
        //返回按钮点击事件
        title_break.setOnClickListener(this);
        //确定按钮点击事件
        tv_police_type_query.setOnClickListener(this);


        mListData = new ArrayList<PoliceTypeData>();

        mGridViewAdapter = new GridViewAdapter(this, mListData);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (mListData.get(position).getI()) {
                    case 1:
                        isSelect1 = -1;
                        break;
                    case 2:
                        isSelect2 = -1;
                        break;
                    case 3:
                        isSelect3 = -1;
                        break;
                    case 4:
                        isSelect4 = -1;
                        break;
                    case 5:
                        isSelect5 = -1;
                        break;
                    case 6:
                        isSelect6 = -1;
                        break;
                    case 7:
                        isSelect7 = -1;
                        break;
                    case 8:
                        isSelect8 = -1;
                        break;
                    case 9:
                        isSelect9 = -1;
                        break;
                    case 10:
                        isSelect10 = -1;
                        break;
                    case 11:
                        isSelect11 = -1;
                        break;
                    case 12:
                        isSelect12 = -1;
                        break;
                }
                mGridViewAdapter.removeItem(position);
                mGridViewAdapter.notifyDataSetChanged();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if (ConstantClass.num != 0) {
            mun = new String[ConstantClass.num];
            types = new String[ConstantClass.num];
            String type = "";
            for (int i = 0; i < ConstantClass.num; i++) {
                mun[i] = bundle.getString("num" + i);
                types[i] = bundle.getString("type" + i);
            }
            //初始化
            for (int i = 0; i < ConstantClass.num; i++) {
                switch (mun[i]) {
                    case "5":
                        PoliceTypeData data = new PoliceTypeData(police_type1.getText().toString().trim(), 1, "5");
                        mGridViewAdapter.addItem(data);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect1 = 0;
                        break;
                    case "6":
                        PoliceTypeData data2 = new PoliceTypeData(police_type2.getText().toString().trim(), 2, "6");
                        mGridViewAdapter.addItem(data2);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect2 = 0;
                        break;
                    case "4":
                        PoliceTypeData data3 = new PoliceTypeData(police_type3.getText().toString().trim(), 3, "4");
                        mGridViewAdapter.addItem(data3);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect3 = 0;
                        break;
                    case "1":
                        PoliceTypeData data4 = new PoliceTypeData(police_type4.getText().toString().trim(), 4, "1");
                        mGridViewAdapter.addItem(data4);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect4 = 0;
                        break;
                    case "19":
                        PoliceTypeData data5 = new PoliceTypeData(police_type5.getText().toString().trim(), 5, "19");
                        mGridViewAdapter.addItem(data5);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect5 = 0;
                        break;
                    case "11":
                        PoliceTypeData data6 = new PoliceTypeData(police_type6.getText().toString().trim(), 6, "11");
                        mGridViewAdapter.addItem(data6);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect6 = 0;
                        break;
                    case "7":
                        PoliceTypeData data7 = new PoliceTypeData(police_type7.getText().toString().trim(), 7, "7");
                        mGridViewAdapter.addItem(data7);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect7 = 0;
                        break;
                    case "8":
                        PoliceTypeData data8 = new PoliceTypeData(police_type8.getText().toString().trim(), 8, "8");
                        mGridViewAdapter.addItem(data8);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect8 = 0;
                        break;
                    case "9":
                        PoliceTypeData data9 = new PoliceTypeData(police_type9.getText().toString().trim(), 9, "9");
                        mGridViewAdapter.addItem(data9);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect9 = 0;
                        break;
                    case "overSpeedOnTime":
                        PoliceTypeData data10 = new PoliceTypeData(police_type10.getText().toString().trim(), 10, "overSpeedOnTime");
                        mGridViewAdapter.addItem(data10);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect10 = 0;
                        break;
                    case "10":
                        PoliceTypeData data11 = new PoliceTypeData(police_type11.getText().toString().trim(), 11, "10");
                        mGridViewAdapter.addItem(data11);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect11 = 0;
                        break;
                    case "stop":
                        PoliceTypeData data12 = new PoliceTypeData(police_type12.getText().toString().trim(), 12, "stop");
                        mGridViewAdapter.addItem(data12);
                        mGridViewAdapter.notifyDataSetChanged();
                        isSelect12 = 0;
                        break;
                }
            }
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_break:
                //返回
                this.finish();
                break;
            case R.id.tv_police_type_query:
                //确定
                Intent data1 = new Intent();
                int num = mListData.size();
                ConstantClass.num = num;
                for (int i = 0; i < mListData.size(); i++) {
                    data1.putExtra("num" + i, mListData.get(i).getNum().toString().trim());
                    data1.putExtra("type" + i, mListData.get(i).getPoliceType().toString().trim());//数据放到data里面去
                }
                setResult(4, data1);//返回data，2为result，data为intent对象
                //PoliceTypeQuery.this.moveTaskToBack(false);
                finish();//页面销毁
                break;
            case R.id.police_type1:
                if (isSelect1 == -1) {
                    PoliceTypeData data = new PoliceTypeData(police_type1.getText().toString().trim(), 1, "5");
                    mGridViewAdapter.addItem(data);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect1 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type2:
                if (isSelect2 == -1) {
                    PoliceTypeData data2 = new PoliceTypeData(police_type2.getText().toString().trim(), 2, "6");
                    mGridViewAdapter.addItem(data2);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect2 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type3:
                if (isSelect3 == -1) {
                    PoliceTypeData data3 = new PoliceTypeData(police_type3.getText().toString().trim(), 3, "4");
                    mGridViewAdapter.addItem(data3);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect3 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type4:
                if (isSelect4 == -1) {
                    PoliceTypeData data4 = new PoliceTypeData(police_type4.getText().toString().trim(), 4, "1");
                    mGridViewAdapter.addItem(data4);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect4 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type5:
                if (isSelect5 == -1) {
                    PoliceTypeData data5 = new PoliceTypeData(police_type5.getText().toString().trim(), 5, "19");
                    mGridViewAdapter.addItem(data5);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect5 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type6:
                if (isSelect6 == -1) {
                    PoliceTypeData data6 = new PoliceTypeData(police_type6.getText().toString().trim(), 6, "11");
                    mGridViewAdapter.addItem(data6);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect6 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type7:
                if (isSelect7 == -1) {
                    PoliceTypeData data7 = new PoliceTypeData(police_type7.getText().toString().trim(), 7, "7");
                    mGridViewAdapter.addItem(data7);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect7 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type8:
                if (isSelect8 == -1) {
                    PoliceTypeData data8 = new PoliceTypeData(police_type8.getText().toString().trim(), 8, "8");
                    mGridViewAdapter.addItem(data8);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect8 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type9:
                if (isSelect9 == -1) {
                    PoliceTypeData data9 = new PoliceTypeData(police_type9.getText().toString().trim(), 9, "9");
                    mGridViewAdapter.addItem(data9);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect9 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type10:
                if (isSelect10 == -1) {
                    PoliceTypeData data10 = new PoliceTypeData(police_type10.getText().toString().trim(), 10, "overSpeedOnTime");
                    mGridViewAdapter.addItem(data10);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect10 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type11:
                if (isSelect11 == -1) {
                    PoliceTypeData data11 = new PoliceTypeData(police_type11.getText().toString().trim(), 11, "10");
                    mGridViewAdapter.addItem(data11);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect11 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
            case R.id.police_type12:
                if (isSelect12 == -1) {
                    PoliceTypeData data12 = new PoliceTypeData(police_type12.getText().toString().trim(), 12, "stop");
                    mGridViewAdapter.addItem(data12);
                    mGridViewAdapter.notifyDataSetChanged();
                    isSelect12 = 0;
                } else {
                    ToastUtils.showToast(this, "不能重复选择");
                }
                break;
        }
    }

}
