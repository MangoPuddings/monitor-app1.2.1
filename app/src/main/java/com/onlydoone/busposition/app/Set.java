package com.onlydoone.busposition.app;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.service.GuardNettyService;
import com.onlydoone.busposition.service.MyNettyService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhaohui on 2017/4/1.
 */
public class Set extends Activity implements View.OnClickListener {
    //title 返回按钮
    private ImageView titleBreak;
    //title显示内容
    private TextView titleContext;
    //车辆掉线报警开关
    private ImageView ivSwitch;
    //车辆在线率低于**报警设置
    private EditText et_online_rate1, et_online_rate2, et_online_rate3;
    //车辆在线率报警设置完成按钮
    private TextView tv_complete;
    //报警设置layout
    private LinearLayout layout_onlineRate;
    //车辆在线率报警时间设置
    private TextView tv_data_start1, tv_data_start2, tv_data_start3,
            tv_data_end1, tv_data_end2, tv_data_end3;
    //
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set);
        //初始化控件
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        sp = Set.this.getSharedPreferences("login_state", 0);
        titleBreak = (ImageView) findViewById(R.id.title_break);
        titleContext = (TextView) findViewById(R.id.title_context);
        ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
        et_online_rate1 = (EditText) findViewById(R.id.et_online_rate1);
        et_online_rate2 = (EditText) findViewById(R.id.et_online_rate2);
        et_online_rate3 = (EditText) findViewById(R.id.et_online_rate3);
        tv_complete = (TextView) findViewById(R.id.tv_complete);
        layout_onlineRate = (LinearLayout) findViewById(R.id.layout_onlineRate);
        tv_data_start1 = (TextView) findViewById(R.id.tv_data_start1);
        tv_data_start2 = (TextView) findViewById(R.id.tv_data_start2);
        tv_data_start3 = (TextView) findViewById(R.id.tv_data_start3);
        tv_data_end1 = (TextView) findViewById(R.id.tv_data_end1);
        tv_data_end2 = (TextView) findViewById(R.id.tv_data_end2);
        tv_data_end3 = (TextView) findViewById(R.id.tv_data_end3);


        titleContext.setText("设置");

        //初始化车辆掉线开关
        if (sp.getString("switch", "close").equals("close")) {
            ivSwitch.setImageResource(R.mipmap.ic_switch_close);
            layout_onlineRate.setVisibility(View.GONE);
        } else {
            ivSwitch.setImageResource(R.mipmap.ic_switch_open);
            et_online_rate1.setText(sp.getString("onlineRate1", "30"));
            et_online_rate2.setText(sp.getString("onlineRate2", "30"));
            et_online_rate3.setText(sp.getString("onlineRate3", "30"));
            layout_onlineRate.setVisibility(View.VISIBLE);
        }
        //初始化报警时间
        tv_data_start1.setText(sp.getString("startTime1","00:00"));
        tv_data_start2.setText(sp.getString("startTime2","00:00"));
        tv_data_start3.setText(sp.getString("startTime3","00:00"));
        tv_data_end1.setText(sp.getString("endTime1","23:59"));
        tv_data_end2.setText(sp.getString("endTime2","23:59"));
        tv_data_end3.setText(sp.getString("endTime3","23:59"));

        //车辆掉线开关点击事件
        ivSwitch.setOnClickListener(this);
        //title 返回按钮点击事件
        titleBreak.setOnClickListener(this);
        //在线率报警设置输入监听事件
        et_online_rate1.addTextChangedListener(new MyTextWatcher(et_online_rate1));
        et_online_rate2.addTextChangedListener(new MyTextWatcher(et_online_rate2));
        et_online_rate3.addTextChangedListener(new MyTextWatcher(et_online_rate3));
        //车辆在线率报警设置完成按钮点击监听事件
        tv_complete.setOnClickListener(this);
        //时间设置点击事件（车辆在线率报警时间段）
        tv_data_start1.setOnClickListener(this);
        tv_data_start2.setOnClickListener(this);
        tv_data_start3.setOnClickListener(this);
        tv_data_end1.setOnClickListener(this);
        tv_data_end2.setOnClickListener(this);
        tv_data_end3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_break:
                //返回
                finish();
                break;
            case R.id.tv_data_start1:
                dateChange(tv_data_start1);
                break;
            case R.id.tv_data_start2:
                dateChange(tv_data_start2);
                break;
            case R.id.tv_data_start3:
                dateChange(tv_data_start3);
                break;
            case R.id.tv_data_end1:
                dateChange(tv_data_end1);
                break;
            case R.id.tv_data_end2:
                dateChange(tv_data_end2);
                break;
            case R.id.tv_data_end3:
                dateChange(tv_data_end3);
                break;
            case R.id.ivSwitch:
                //车辆报警开关
                if (sp.getString("switch", "close").equals("close")) {
                    editor = sp.edit();
                    editor.putString("switch", "open");
                    editor.commit();
                    et_online_rate1.setText(sp.getString("onlineRate1", "30"));
                    et_online_rate2.setText(sp.getString("onlineRate2", "30"));
                    et_online_rate3.setText(sp.getString("onlineRate3", "30"));
                    ivSwitch.setImageResource(R.mipmap.ic_switch_open);
                    layout_onlineRate.setVisibility(View.VISIBLE);
                    //开启掉线推送服务
                    Intent intent = new Intent(Set.this, MyNettyService.class);
                    startService(intent);

                } else {
                    editor = sp.edit();
                    editor.putString("switch", "close");
                    editor.commit();
                    ivSwitch.setImageResource(R.mipmap.ic_switch_close);
                    layout_onlineRate.setVisibility(View.GONE);
                    //关闭车辆掉线推送服务
                    Intent intent2 = new Intent(Set.this, GuardNettyService.class);
                    stopService(intent2);
                    Intent intent = new Intent(Set.this, MyNettyService.class);
                    stopService(intent);
                }
                break;
            case R.id.tv_complete:
                //车辆在线率报警设置完成按钮
                String et1 = et_online_rate1.getText().toString().trim();
                String et2 = et_online_rate2.getText().toString().trim();
                String et3 = et_online_rate3.getText().toString().trim();
                String startTime1 = tv_data_start1.getText().toString().trim();
                String startTime2 = tv_data_start2.getText().toString().trim();
                String startTime3 = tv_data_start3.getText().toString().trim();
                String endTime1 = tv_data_end1.getText().toString().trim();
                String endTime2 = tv_data_end2.getText().toString().trim();
                String endTime3 = tv_data_end3.getText().toString().trim();
                if (!et1.equals("") || !et2.equals("") || !et3.equals("")) {
                    editor = sp.edit();
                    editor.putString("onlineRate1", et1);
                    editor.putString("onlineRate2", et2);
                    editor.putString("onlineRate3", et3);
                    editor.putString("startTime1", startTime1);
                    editor.putString("startTime2", startTime2);
                    editor.putString("startTime3", startTime3);
                    editor.putString("endTime1", endTime1);
                    editor.putString("endTime2", endTime2);
                    editor.putString("endTime3", endTime3);
                    editor.commit();
                    finish();
                } else {
                    ToastUtils.showToast(Set.this, "在线率不能为空");
                    editor = sp.edit();
                    editor.commit();
                }
                break;
        }
    }

    /**
     * 更改日期
     */
    private void dateChange(final TextView tv) {
        //显示修改日期的dialog
        Calendar c = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        int h = 0;
        int m = 0;
        try {
            Date date = new SimpleDateFormat("HH:mm").parse(tv.getText().toString());
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date);
            h = c1.get(Calendar.HOUR);
            m = c1.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * 实例化一个TimePickerDialog的对象
         * 第二个参数是一个TimePickerDialog.OnTimeSetListener匿名内部类，当用户选择好时间后点击done会调用里面的onTimeset方法
         */
        TimePickerDialog timePickerDialog = new TimePickerDialog(Set.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tv.setText((hourOfDay <= 9 ? "0" + hourOfDay : hourOfDay) + ":" + (minute <= 9 ? "0" + minute : minute));
            }
        }, h, m, true);
        timePickerDialog.show();
    }


/**
 * 重构editText监听事件
 */
public class MyTextWatcher implements TextWatcher {

    private EditText editText;

    public MyTextWatcher(EditText editText) {
        this.editText = editText;
    }

    /**
     * 文本输入改变之前调用（还未改变）
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * 文本改变过程中调用（文本替换动作）
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String onlineRate = String.valueOf(s);
        if (!onlineRate.equals("")) {
            Integer onlineRates = Integer.parseInt(onlineRate);
            if (100 < onlineRates) {
                editText.setText("100");
                //将editText的光标移动到文本最后位置
                editText.setSelection(editText.getText().length());
            }

        }
    }

    /**
     * 文本改标之后调用（文本已经替换完成）
     */
    @Override
    public void afterTextChanged(Editable s) {

    }
}
}
