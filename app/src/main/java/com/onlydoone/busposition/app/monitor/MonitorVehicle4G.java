package com.onlydoone.busposition.app.monitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.ToastUtils;
import com.onlydoone.busposition.bean.Video4G;

import h264.com.H264View;

import com.example.importtest.VideoPlayFunction;
import com.onlydoone.busposition.classs.ConstantClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.path;


/**
 * 车辆4G视频监控
 * Created by zhaohui on 2017/1/20.
 */
public class MonitorVehicle4G extends Activity implements View.OnClickListener {
    private LinearLayout layoutVideo1, layoutVideo2, layout_h264View1, layout_h264View2,
            layout_h264View3, layout_h264View4;
    TextView title_context;
    private Button startVideo, endVideo, btnStartPlay, btnEndPlay;
    private Chronometer timer;
    String id;

    private H264View h264View1, h264View2, h264View3, h264View4;
    private VideoPlayFunction videoPlayFunction1;
    private VideoPlayFunction videoPlayFunction2;
    private VideoPlayFunction videoPlayFunction3;
    private VideoPlayFunction videoPlayFunction4;
    private Video4G video4G;
    private int videoId = 0;
    private boolean isPlay1 = true;
    private boolean isPlay2 = true;
    private boolean isPlay3 = true;
    private boolean isPlay4 = true;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    h264ViewBackgroudClear();
                    layout_h264View1.setBackgroundResource(R.drawable.cheek);
                    videoId = 1;
                    if (isPlay1) {
                        btnStartPlay.setText("暂停播放");
                    } else {
                        btnStartPlay.setText("开始播放");
                    }
                    break;
                case 2:
                    h264ViewBackgroudClear();
                    layout_h264View2.setBackgroundResource(R.drawable.cheek);
                    videoId = 2;
                    if (isPlay2) {
                        btnStartPlay.setText("暂停播放");
                    } else {
                        btnStartPlay.setText("开始播放");
                    }
                    break;
                case 3:
                    h264ViewBackgroudClear();
                    layout_h264View3.setBackgroundResource(R.drawable.cheek);
                    videoId = 3;
                    if (isPlay3) {
                        btnStartPlay.setText("暂停播放");
                    } else {
                        btnStartPlay.setText("开始播放");
                    }
                    break;
                case 4:
                    h264ViewBackgroudClear();
                    layout_h264View4.setBackgroundResource(R.drawable.cheek);
                    videoId = 4;
                    if (isPlay4) {
                        btnStartPlay.setText("暂停播放");
                    } else {
                        btnStartPlay.setText("开始播放");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //只要activity为可见状态，则保持设备的屏幕打开和常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.monitor_vehicle);

        initView();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        title_context = (TextView) findViewById(R.id.title_context);
        ImageView title_break = (ImageView) findViewById(R.id.title_break);
        startVideo = (Button) findViewById(R.id.startVideo);
        endVideo = (Button) findViewById(R.id.endVideo);
        timer = (Chronometer) findViewById(R.id.timer);
        layoutVideo1 = (LinearLayout) findViewById(R.id.layoutVideo1);
        layoutVideo2 = (LinearLayout) findViewById(R.id.layoutVideo2);
        layout_h264View1 = (LinearLayout) findViewById(R.id.layout_h264View1);
        layout_h264View2 = (LinearLayout) findViewById(R.id.layout_h264View2);
        layout_h264View3 = (LinearLayout) findViewById(R.id.layout_h264View3);
        layout_h264View4 = (LinearLayout) findViewById(R.id.layout_h264View4);

        btnStartPlay = (Button) findViewById(R.id.btnStartPlay);
        btnEndPlay = (Button) findViewById(R.id.btnEndPlay);

        btnEndPlay.setOnClickListener(this);
        btnStartPlay.setOnClickListener(this);


        //title 设置文字
        Bundle bundle = getIntent().getExtras();
        String context = bundle.getString("vehicleid");
        //id = bundle.getString("sim_no");
        video4G = new Video4G(ConstantClass.sim_no);
        //video4G.setKeyid("0" + bundle.getString("sim_no"));
        title_context.setText(context);
        //title 返回点击监听事件
        title_break.setOnClickListener(this);


        //开始播放按钮点击事件
        startVideo.setOnClickListener(this);

        //停止播放按钮点击事件
        endVideo.setOnClickListener(this);

        /**
         * 获取屏幕宽度并且除以4
         */
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / 2;
        int h = dm.heightPixels / 3;

        layoutVideo1.getLayoutParams().height = h;
        layoutVideo2.getLayoutParams().height = h;

        videoPlayFunction1 = new VideoPlayFunction();
        videoPlayFunction2 = new VideoPlayFunction();
        videoPlayFunction3 = new VideoPlayFunction();
        videoPlayFunction4 = new VideoPlayFunction();
        //video4G = new Video4G();

        videoPlayFunction1.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
        videoPlayFunction2.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
        videoPlayFunction3.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
        videoPlayFunction4.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
        float w = (float) (width / 3.6);
        float height = (float) (h / 2.8);
        //通道1
        h264View1 = (H264View) findViewById(R.id.h264View1);
        h264View1.setScree(w, height, 0);
        layout_h264View1.setOnClickListener(this);
        //通道2
        h264View2 = (H264View) findViewById(R.id.h264View2);
        h264View2.setScree(w, height, 0);
        layout_h264View2.setOnClickListener(this);
        //通道3
        h264View3 = (H264View) findViewById(R.id.h264View3);
        h264View3.setScree(w, height, 0);
        layout_h264View3.setOnClickListener(this);
        //通道4
        h264View4 = (H264View) findViewById(R.id.h264View4);
        h264View4.setScree(w, height, 0);
        layout_h264View4.setOnClickListener(this);

        //查询车辆终端手机号
        getHttpId();
    }

    /**
     * 查询车辆终端手机号
     */
    private void getHttpId() {
        videoPlayFunction1.BeginVideo(ConstantClass.sim_no, video4G.getNum1(), h264View1, this);
        videoPlayFunction2.BeginVideo(ConstantClass.sim_no, video4G.getNum2(), h264View2, this);
        videoPlayFunction3.BeginVideo(ConstantClass.sim_no, video4G.getNum3(), h264View3, this);
        videoPlayFunction4.BeginVideo(ConstantClass.sim_no, video4G.getNum4(), h264View4, this);
    }

    private long recordingTime = 0;// 记录下来的总时间


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_break:
                //返回上一个界面
                finish();
                break;
            case R.id.layout_h264View1:
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                break;
            case R.id.layout_h264View2:
                Message msg2 = new Message();
                msg2.what = 2;
                mHandler.sendMessage(msg2);
                break;
            case R.id.layout_h264View3:
                Message msg3 = new Message();
                msg3.what = 3;
                mHandler.sendMessage(msg3);
                break;
            case R.id.layout_h264View4:
                Message msg4 = new Message();
                msg4.what = 4;
                mHandler.sendMessage(msg4);
                break;
            case R.id.btnScreenshot:
                //截屏
                videoPlayFunction1.CapturePic("ymPic");//参数为目录
                saveCurrentImage();
                break;
            case R.id.btnStartPlay:
                if (videoId == 0) {
                    ToastUtils.showToast(this, "请选择暂停的监控通道");
                } else {
                    if (btnStartPlay.getText().equals("暂停播放")) {
                        switch (videoId) {
                            case 1:
                                videoPlayFunction1.EndVideo();
                                btnStartPlay.setText("开始播放");
                                isPlay1 = false;
                                break;
                            case 2:
                                videoPlayFunction2.EndVideo();
                                btnStartPlay.setText("开始播放");
                                isPlay2 = false;
                                break;
                            case 3:
                                videoPlayFunction3.EndVideo();
                                btnStartPlay.setText("开始播放");
                                isPlay3 = false;
                                break;
                            case 4:
                                videoPlayFunction4.EndVideo();
                                btnStartPlay.setText("开始播放");
                                isPlay4 = false;
                                break;
                        }
                    } else if (btnStartPlay.getText().equals("开始播放")) {
                        switch (videoId) {
                            case 1:
                                h264View1.setVisibility(View.VISIBLE);
                                videoPlayFunction1 = new VideoPlayFunction();
                                videoPlayFunction1.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
                                videoPlayFunction1.BeginVideo(ConstantClass.sim_no, video4G.getNum1(), h264View1, this);
                                btnStartPlay.setText("暂停播放");
                                isPlay1 = true;
                                break;
                            case 2:
                                h264View2.setVisibility(View.VISIBLE);
                                videoPlayFunction2 = new VideoPlayFunction();
                                videoPlayFunction2.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
                                videoPlayFunction2.BeginVideo(ConstantClass.sim_no, video4G.getNum2(), h264View2, this);
                                btnStartPlay.setText("暂停播放");
                                isPlay2 = true;
                                break;
                            case 3:
                                h264View3.setVisibility(View.VISIBLE);
                                videoPlayFunction3 = new VideoPlayFunction();
                                videoPlayFunction3.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
                                videoPlayFunction3.BeginVideo(ConstantClass.sim_no, video4G.getNum3(), h264View3, this);
                                btnStartPlay.setText("暂停播放");
                                isPlay3 = true;
                                break;
                            case 4:
                                h264View4.setVisibility(View.VISIBLE);
                                videoPlayFunction4 = new VideoPlayFunction();
                                videoPlayFunction4.Connect(video4G.getIp(), video4G.getPort(), video4G.getUserName(), video4G.getPassWord());
                                videoPlayFunction4.BeginVideo(ConstantClass.sim_no, video4G.getNum4(), h264View4, this);
                                btnStartPlay.setText("暂停播放");
                                isPlay4 = true;
                                break;
                        }
                    }
                }
                break;
            case R.id.btnEndPlay:
                stopVideo();

                break;
            case R.id.btnFindVideo:
                //查看录像
                break;
            case R.id.startVideo:
                //开始录制
                timer.setVisibility(View.VISIBLE);
                //timer.setBase(SystemClock.elapsedRealtime());//计时器清零
//                int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
//                timer.setFormat("0"+String.valueOf(hour)+":%s");
                if (startVideo.getText().equals("开始录制")) {
                    startVideo.setText("暂停录制");
                    videoPlayFunction1.BeginRecord();
                    videoPlayFunction1.BeginRecord("h264");//参数为MP4名称
                    onRecordStart();
                } else {
                    startVideo.setText("开始录制");
                    onRecordPause();
                }
                break;
            case R.id.endVideo:
                //停止录制
                timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                startVideo.setText("开始录制");
                onRecordStop();
                break;
        }
    }

    /**
     * 停止播放
     */
    private void stopVideo() {
        switch (videoId) {
            case 1:
                videoPlayFunction1.EndVideo();
                h264View1.setVisibility(View.INVISIBLE);
                layout_h264View1.setBackgroundResource(R.drawable.cheek);
                isPlay1 = false;
                btnStartPlay.setText("开始播放");
                break;
            case 2:
                videoPlayFunction2.EndVideo();
                h264View2.setVisibility(View.INVISIBLE);
                layout_h264View2.setBackgroundResource(R.drawable.cheek);
                isPlay2 = false;
                btnStartPlay.setText("开始播放");
                break;
            case 3:
                videoPlayFunction3.EndVideo();
                h264View3.setVisibility(View.INVISIBLE);
                layout_h264View3.setBackgroundResource(R.drawable.cheek);
                isPlay3 = false;
                btnStartPlay.setText("开始播放");
                break;
            case 4:
                videoPlayFunction4.EndVideo();
                h264View4.setVisibility(View.INVISIBLE);
                layout_h264View4.setBackgroundResource(R.drawable.cheek);
                isPlay4 = false;
                btnStartPlay.setText("开始播放");
                break;
            default:
                ToastUtils.showToast(this, "请选择关闭的监控通道");
                break;

        }

    }

    /**
     * 开始播放
     *
     * @param vf
     */
    private void startVideo(VideoPlayFunction vf, int num, H264View h264View) {

    }

    /**
     * 停止播放
     */
    private void endVideo(VideoPlayFunction vf) {
        vf.EndVideo();
        btnStartPlay.setText("开始播放");
    }

    private void h264ViewBackgroudClear() {
        layout_h264View1.setBackgroundResource(R.drawable.cheek_no);
        layout_h264View2.setBackgroundResource(R.drawable.cheek_no);
        layout_h264View3.setBackgroundResource(R.drawable.cheek_no);
        layout_h264View4.setBackgroundResource(R.drawable.cheek_no);
    }

    //这种方法状态栏是空白，显示不了状态栏的信息
    private void saveCurrentImage() {
        //获取当前屏幕的大小
        /**
         * 获取屏幕宽度并且除以4
         */
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        View view = MonitorVehicle4G.this.getWindow().getDecorView();
//      Enables or disables the drawing cache
        view.setDrawingCacheEnabled(true);
//      will draw the view in a bitmap
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));

        saveImageToGallery(this, bitmap);
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    //开启定时器
    public void onRecordStart() {
//        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
//        timer.setFormat("0" + String.valueOf(hour) + ":%s");
        timer.setBase(SystemClock.elapsedRealtime() - recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
        timer.start();
    }

    //暂停定时器
    public void onRecordPause() {
        timer.stop();
        recordingTime = SystemClock.elapsedRealtime() - timer.getBase();// 保存这次记录了的时间
    }

    //停止定时器
    public void onRecordStop() {
        recordingTime = 0;
        timer.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayFunction1.EndVideo();
        videoPlayFunction2.EndVideo();
        videoPlayFunction3.EndVideo();
        videoPlayFunction4.EndVideo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) {
            title_context.setText(data.getStringExtra("vehicleid"));
        }
    }
}
