package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.onlydoone.busposition.R;
import com.onlydoone.busposition.fragment.miles.Date;
import com.onlydoone.busposition.fragment.miles.Month;

/**
 * 车辆行驶里程统计
 * Created by zhaohui on 2017/2/17.
 */
public class Mileage extends Activity implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private LinearLayout miles_dates;
    /**
     * fragment
     */
    public Date date;
    /**
     * fragment
     */
    public Month month;
    /**
     * 返回按鈕,搜索按钮
     */
    private ImageView title_break,title_search;
    /**
     * title顯示的內容
     */
    private TextView title_context;
    /**
     * 按月查詢，按天查詢
     */
    private TextView miles_month, miles_date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mileage);
        //初始化控件
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_break = (ImageView) findViewById(R.id.title_break);
        title_context = (TextView) findViewById(R.id.title_context);
        miles_date = (TextView) findViewById(R.id.miles_date);
        miles_month = (TextView) findViewById(R.id.miles_month);
        miles_dates = (LinearLayout) findViewById(R.id.miles_dates);
        title_search = (ImageView) findViewById(R.id.title_search);

        //title_search.setVisibility(View.VISIBLE);

        // 开启一个Fragment事务
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();

        //默认显示的fragment为inquiry_fragment
        date = new Date();
        transaction.add(R.id.miles_dates, date);
        transaction.commit();

        title_context.setText("车辆行驶里程统计");

        //返回按钮点击事件
        title_break.setOnClickListener(this);
        //按天查询点击事件
        miles_date.setOnClickListener(this);
        //按月查询点击事件
        miles_month.setOnClickListener(this);
        //搜索按钮点击事件
        title_search.setOnClickListener(this);
    }

    /**
     * 接收MilesSearch.class界面返回来的数据
     * requestCode 请求的标识(请求码）
     * resultCode 第二个页面返回的标识（返回码）
     * data 第二个页面（MilesSearch.class）回传回来的数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录成功，返回数据并显示退出当前账号按钮
        if (requestCode == 1 && resultCode == 2) {
            title_context.setText(data.getStringExtra("vehicleNo"));
            getHttpVehicleMiles(data.getStringExtra("vehicleNo"));
        }

    }

    /**
     * 查询服务器车辆里程信息
     */
    private void getHttpVehicleMiles(String vehicleNo) {

    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_break:      //返回按钮点击事件
                finish();
                break;
            case R.id.miles_date:       //按天查询按钮点击事件
                // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
                hideFragments(transaction);
                setFragment(0);
                break;
            case R.id.miles_month:    //按月查询按钮点击事件
                // 每次选中之前先清楚掉上次的选中状态
                clearSelection();
                // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
                hideFragments(transaction);
                setFragment(1);
                break;
            case R.id.title_search:   //搜索按钮点击事件
                //查询指定车辆里程信息
                //findVehicleNo();
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 0表示inquiry，1表示content，2表示set。
     */
    private void setFragment(int index) {
        // 开启一个Fragment事务
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();

        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);

        //根据传进来的index，来切换fragment
        switch (index) {
            case 0:
                // 每次选中之前先清楚掉上次的选中状态
                clearSelection();

                miles_date.setBackgroundColor(Color.parseColor("#15B2DB"));
                if (date == null) {
                    // 如果content_fragment为空，则创建一个content_fragment并添加到界面上
                    date = new Date();
                    transaction.add(R.id.miles_dates, date);


                } else {
                    //如果content_fragment不为空，则直接将它显示出来
                    transaction.show(date);
                }
                break;
            case 1:
                // 每次选中之前先清楚掉上次的选中状态
                clearSelection();
                miles_month.setBackgroundColor(Color.parseColor("#15B2DB"));
                if (month == null) {
                    // 如果content_fragment为空，则创建一个content_fragment并添加到界面上
                    month = new Month();
                    transaction.add(R.id.miles_dates, month);

                } else {
                    //如果content_fragment不为空，则直接将它显示出来
                    transaction.show(month);
                }
                break;
        }
        transaction.commit();

    }

    /**
     * 清除所有菜单的选中状态
     */
    private void clearSelection() {

        miles_date.setBackgroundColor(Color.parseColor("#F0F0F0"));
        miles_month.setBackgroundColor(Color.parseColor("#F0F0F0"));
    }

    /**
     * 隐藏掉所有的fragment
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {

        if (date != null) {
            transaction.hide(date);
        }
        if (month != null) {
            transaction.hide(month);
        }
    }

    /**
     * 用户按返回键时销毁界面
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //写下你希望按下返回键达到的效果代码，不写则不会有反应
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
