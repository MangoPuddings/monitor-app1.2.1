package com.onlydoone.busposition.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.onlydoone.busposition.R;
import com.onlydoone.busposition.Utils.IsServiceWorkedUtil;
import com.onlydoone.busposition.Utils.SourceTimeUtil;
import com.onlydoone.busposition.app.monitor.MonitorOnLine;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by zhaohui on 2017/3/30.
 */
public class MyNettyService extends Service {
    SharedPreferences sp;
    public static String HOST = "123.57.60.121";
    public static int PORT = 9999;

    public Bootstrap bootstrap = getBootstrap();
    public Channel channel = getChannel(HOST, PORT);
    public ChannelFutureListener channelFutureListener = null;

    /**
     * Notification构造器
     */
    NotificationCompat.Builder mBuilder;

    public NotificationManager mNotificationManager;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int onlineRate = Integer.valueOf(msg.obj.toString());
                    System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String newDate = sdf.format(new Date());

                    //第一个报警时间
                    if (onlineRate < Integer.valueOf(sp.getString("onlineRate1", "30"))) {
                        if (SourceTimeUtil.isInTime(sp.getString("startTime1", "00:00") + "-" + sp.getString("endTime1", "23:59"), newDate)) {
                            mBuilder.setTicker("车辆在线率低于" + sp.getString("onlineRate1", "30") + "%");
                            showNotify(onlineRate);
                        }
                    }
                    //第二个报警时间
                    if (onlineRate < Integer.valueOf(sp.getString("onlineRate2", "30"))) {
                        if (SourceTimeUtil.isInTime(sp.getString("startTime2", "00:00") + "-" + sp.getString("endTime2", "23:59"), newDate)) {
                            mBuilder.setTicker("车辆在线率低于" + sp.getString("onlineRate2", "30") + "%");
                            showNotify(onlineRate);
                        }
                    }
                    //第三个报警时间
                    if (onlineRate < Integer.valueOf(sp.getString("onlineRate3", "30"))) {
                        if (SourceTimeUtil.isInTime(sp.getString("startTime3", "00:00") + "-" + sp.getString("endTime3", "23:59"), newDate)) {
                            mBuilder.setTicker("车辆在线率低于" + sp.getString("onlineRate3", "30") + "%");
                            showNotify(onlineRate);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * Service 第一次启动时执行
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sp = this.getSharedPreferences("login_state", this.MODE_PRIVATE);
        //初始化通知栏
        initNotify();
        String idOwner = sp.getString("id_owner", "");
        if (idOwner.equals("")) {
            idOwner = "-1";
        }

        try {
            sendMsg(idOwner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        //开启子线程守护
        timer();
        return START_STICKY;
    }

    /**
     * 进程守护
     */
    private void timer() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                boolean b = IsServiceWorkedUtil.isServiceWorked(MyNettyService.this, "com.onlydoone.busposition.service.GuardNettyService");
                if(!b) {
                    Intent service = new Intent(MyNettyService.this, GuardNettyService.class);
                    if (sp.getString("switch","close").equals("open")) {
                        //Log.e("GuardNettyService", "重新启动服务 GuardNettyService");
                        //开启服务
                        startService(service);
                    }else {
                        //关闭服务
                        stopService(service);
                        stopSelf();
                    }
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (channel != null){
            channel.close();
        }
        onCreate();
    }

    /**
     * 自定义通知栏
     */
    public void showNotify(Object msg) {
        //先设定RemoteViews
        RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notifications_item);
        //设置对应IMAGEVIEW的ID的资源图片
        view_custom.setImageViewResource(R.id.iv_notification, R.mipmap.ic_icon);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

        //view_custom.setInt(R.id.custom_icon,"setBackgroundResource",R.drawable.icon);
        view_custom.setTextViewText(R.id.iv_notification_title, "车辆掉线警告");
        view_custom.setTextViewText(R.id.iv_notification_context, msg.toString());
        view_custom.setTextViewText(R.id.iv_notification_time, sdf.format(new Date()));
        mBuilder.setContent(view_custom);
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(MyNettyService.this, MonitorOnLine.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyNettyService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }


    /**
     * 初始化通知栏
     */
    private void initNotify() {
        mBuilder = new NotificationCompat.Builder(MyNettyService.this);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                //               .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
                .setTicker("xxx终端掉线")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_icon1);
    }

    /**
     * 初始化Bootstrap
     *
     * @return
     */
    public final Bootstrap getBootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast("handler", new TcpClientHandlers());
            }
        });
        b.option(ChannelOption.SO_KEEPALIVE, true);

        return b;
    }



    public class TcpClientHandlers extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("TcpClientHandler:" + msg);
            Message msgs = new Message();
            msgs.what = 0;
            msgs.obj = msg;
            mHandler.sendMessage(msgs);
        }
    }

    public final Channel getChannel(String host, int port) {
        Channel channel = null;
        try {
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            System.err.println(String.format("连接Server(IP[%s],PORT[%s])失败", host, port));
            e.printStackTrace();
            return null;
        }
        return channel;
    }

    public void sendMsg(String msg) throws Exception {
        if (channel != null) {
            channel.writeAndFlush(msg).sync();
        } else {
            System.out.println("消息发送失败,连接尚未建立!");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
