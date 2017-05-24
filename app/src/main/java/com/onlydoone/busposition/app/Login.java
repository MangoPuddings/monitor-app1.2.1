package com.onlydoone.busposition.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.classs.ConstantClass;
import com.onlydoone.busposition.classs.SysApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 登录类
 * Created by Administrator on 2016/12/24 0024.
 */
public class Login extends Activity implements View.OnClickListener, View.OnFocusChangeListener {
    private long exitTime = 0;
    SharedPreferences sp;
    /**
     * 接收子线程传来的数据
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SUCCESS:
                    /**
                     * 获取信息成功后，对该信息进行JSON解析，得到所需要的信息，然后在textView上展示出来。
                     */
                    //Toast.makeText(Login.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("用户信息-----", msg.obj.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String result = jsonObject.getString("result");
                        //Toast.makeText(Login.this, result + userCode, Toast.LENGTH_SHORT).show();
                        if (result.equals("0")) {
                            if (Login_dialog.isShowing()){
                                Login_dialog.cancel();
                            }
                            //如果返回状态码为0，则解析用户信息
                            JSONArray jsonArrayVehicles = jsonObject.getJSONArray("message");
                            JSONObject jsonObjectVehicles = (JSONObject) jsonArrayVehicles.opt(0);

                            //获取用户所属车队权限
                            String id_owner = "";
                            try{
                                id_owner = jsonObjectVehicles.getString("belongUserGroup");
                            } catch (Exception e) {
                                id_owner = "";
                            }

                            //SharedPreferences sp = Login.this.getSharedPreferences("login_state", 0);

                            SharedPreferences.Editor editor;
                            editor = sp.edit();
                            editor.putString("login_info", "true");
                            //如果报警switch开关为空，则设置为close
                            if (sp.getString("switch","close") == null){
                                editor.putString("switch","close");
                            }
                            editor.putString("id_owner", id_owner);
                            editor.putString("username", username_et.getText().toString().trim());
                            editor.putString("userName",jsonObjectVehicles.getString("userName"));
                            editor.putString("password", "");
                            if (radioButton_location.isChecked()){
                                editor.putString("platform","true" );
                            }else {
                                editor.putString("platform", "false");
                            }

                            editor.commit();
                            //如果选择记住密码，则设置username为用户名
                            if (password_remember.isChecked()) {
                                editor.putString("password", password_et.getText().toString().trim());
                                editor.commit();
                            }

                            Intent intent = new Intent(Login.this,Main.class);
                            startActivity(intent);
//                            intent.putExtra("userCode", jsonObjectVehicles.getString("userName"));
//                            setResult(2, intent);
                            finish();
                            ToastUtils.showToast(Login.this, "登录成功");
                        }
                        if (result.equals("-1")) {
                            if (Login_dialog.isShowing()){
                                Login_dialog.cancel();
                            }
                            ToastUtils.showToast(Login.this, "用户或密码错误");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (Login_dialog.isShowing()){
                            Login_dialog.cancel();
                        }
                        ToastUtils.showToast(Login.this, "json解析失败");
                    }
                    break;
                case PASSWORD_REMEMBER:
                    if (password_remember.isChecked()){
                        password_remember.setButtonDrawable(R.mipmap.ic_check);
                    }else {
                        password_remember.setButtonDrawable(R.mipmap.ic_check_no);
                    }
                    break;
                case 3:
                    Login_dialog = new ProgressDialog(Login.this);
                    Login_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    Login_dialog.setMessage("正在登录中···");
                    Login_dialog.setCanceledOnTouchOutside(false);
                    Login_dialog.show();
                    break;
                case ERROR:
                    if (Login_dialog.isShowing()){
                        Login_dialog.cancel();
                    }
                    ToastUtils.showToast(Login.this, "连接失败");
                    break;

            }
        }
    };
    protected static final int SUCCESS = 0;
    protected static final int PASSWORD_REMEMBER = 2;
    protected static final int ERROR = -1;

    /**
     * 记住密码
     */
    private CheckBox password_remember;
    /**
     * 账号输入框
     */
    private EditText username_et;
    /**
     * 账号输入框图标
     */
    private ImageView username_iv;
    /**
     * 密码输入框
     */
    private EditText password_et;
    /**
     * 密码输入框图标
     */
    private ImageView password_iv;
    /**
     * 登录按钮
     */
    private TextView login;
    /**
     * 登录进度条
     */
    private ProgressDialog Login_dialog;
    /**
     * 定位平台，油补平台单选框
     */
    private RadioGroup radioGroup;
    /**
     * 定位平台
     */
    private RadioButton radioButton_location;
    /**
     * 油补平台
     */
    private RadioButton radioButton_oil_fill;

    private int currentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        //初始化控件
        initView();
        SysApplication.getInstance().addActivity(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        sp = Login.this.getSharedPreferences("login_state", 0);


        password_remember = (CheckBox) findViewById(R.id.password_remember);
        password_remember.setButtonDrawable(R.mipmap.ic_check);
        password_remember.setChecked(true);

        username_iv = (ImageView) findViewById(R.id.username_iv);
        username_et = (EditText) findViewById(R.id.username_et);
        password_iv = (ImageView) findViewById(R.id.password_iv);
        password_et = (EditText) findViewById(R.id.password_et);
        login = (TextView) findViewById(R.id.login_tv);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioButton_location = (RadioButton) findViewById(R.id.location);
        radioButton_oil_fill = (RadioButton) findViewById(R.id.oil_fill);

        if (!sp.getString("platform","").equals("")){
            if (!sp.getString("platform","").equals("true")){
                radioButton_location.setChecked(false);
                radioButton_oil_fill.setChecked(true);
                SharedPreferences.Editor editor1;
                editor1 = sp.edit();
                editor1.putString("URL", ConstantClass.oil_fill);
                editor1.commit();
                //ConstantClass.URL= ConstantClass.oil_fill;
            }else {
                radioButton_location.setChecked(true);
                radioButton_oil_fill.setChecked(false);
                SharedPreferences.Editor editor;
                editor = sp.edit();
                editor.putString("URL", ConstantClass.location);
                editor.commit();
                //ConstantClass.URL = ConstantClass.location;
            }
        }else {
            //默认选中定位平台
            radioButton_location.setChecked(true);
            SharedPreferences.Editor editor;
            editor = sp.edit();
            editor.putString("URL", ConstantClass.location);
            editor.commit();
            //ConstantClass.URL = ConstantClass.location;
        }
        //radioGroup   Checked改变监听事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.location:
                        if (radioButton_location.isChecked()){
                            SharedPreferences.Editor editor;
                            editor = sp.edit();
                            editor.putString("URL", ConstantClass.location);
                            editor.commit();
                            //ConstantClass.URL = ConstantClass.location;
                        }
                        break;
                    case R.id.oil_fill:
                        if (radioButton_oil_fill.isChecked()){
                            SharedPreferences.Editor editor;
                            editor = sp.edit();
                            editor.putString("URL", ConstantClass.oil_fill);
                            editor.commit();
                            //ConstantClass.URL= ConstantClass.oil_fill;
                        }
                        break;
                }
            }
        });


        //定位点击事件
        radioButton_location.setOnClickListener(this);
        //油补点击事件
        radioButton_oil_fill.setOnClickListener(this);

        //SharedPreferences sp = Login.this.getSharedPreferences("login_state", 0);

        username_et.setText(sp.getString("username", ""));
        password_et.setText(sp.getString("password", ""));

        //登录按钮点击监听事件
        login.setOnClickListener(Login.this);
        login.setText("登录");

        //记住密码点击监听事件
        password_remember.setOnClickListener(this);
        //账号输入框焦点监听
        username_et.setOnFocusChangeListener(Login.this);
        //密码输入框焦点监听
        password_et.setOnFocusChangeListener(Login.this);
    }

    /**
     * 输入框焦点监听
     */
    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.username_et:
                if (b) {
                    //账号输入框获取焦点
                    username_iv.setImageResource(R.mipmap.ic_username);

                } else {
                    //账号输入框失去焦点
                    username_iv.setImageResource(R.mipmap.ic_username_null);
                }
                break;
            case R.id.password_et:
                if (b) {
                    //密码输入框获取焦点
                    password_iv.setImageResource(R.mipmap.ic_password);

                } else {
                    //密码输入框失去焦点
                    password_iv.setImageResource(R.mipmap.ic_password_null);
                }
                break;

        }
    }

    /**
     * 点击监听事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //登录点击监听事件
            case R.id.login_tv:
                //获取用户输入的账号密码
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();

                if (username.equals("") || password.equals("")) {
                    ToastUtils.showToast(Login.this, "账号或密码不能为空");
                } else {
                    //设置登录进度条
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                    //登录服务器
                    //Logins();
                    findVolley();
                }
                break;
            case R.id.password_remember:
                if (password_remember.isChecked()){
                password_remember.setChecked(true);
            }else {
                password_remember.setChecked(false);
            }
                Message msg = new Message();
                msg.what = PASSWORD_REMEMBER;
                mHandler.sendMessage(msg);
                break;
            case R.id.location:
                radioButton_location.setChecked(true);
                radioButton_oil_fill.setChecked(false);
                break;
            case R.id.oil_fill:
                radioButton_location.setChecked(false);
                radioButton_oil_fill.setChecked(true);
                break;
        }
    }

    /**
     * volley 网络请求
     */
    private void findVolley() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, sp.getString("URL","") + ConstantClass.URL_LOGIN,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Message msg = new Message();
                                msg.obj = s;
                                msg.what = 0;
                                mHandler.sendMessage(msg);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Message msg = new Message();
                        msg.obj = "连接异常";
                        msg.what = -1;
                        mHandler.sendMessage(msg);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        //在这里设置需要post的参数
                        Map<String, String> map = new HashMap<String, String>();
                        String username = username_et.getText().toString();
                        String password = password_et.getText().toString();
                        map.put("userCode", username);
                        map.put("password", password);
                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        }.start();
    }

    /**
     * 登录界面按手机back键退出所有activity
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
//            //Toast.makeText(Login.this, "拦截手机Back键", Toast.LENGTH_SHORT).show();
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showToast(getApplicationContext(),"再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            //退出所有activity
            SysApplication.getInstance().exit();
            System.exit(0);
        }
    }
}
