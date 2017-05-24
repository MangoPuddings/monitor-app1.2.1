package com.onlydoone.busposition.fragment.monitor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.onlydoone.busposition.R;
import com.onlydoone.busposition.app.monitor.Mileage;
import com.onlydoone.busposition.app.monitor.MonitorOnLine;
import com.onlydoone.busposition.app.monitor.MonitorOwnerSearch;
import com.onlydoone.busposition.app.monitor.MonitorVehiclePolice;
import com.onlydoone.busposition.app.monitor.MonitorVehicleTrail;
import com.onlydoone.busposition.bean.ADInfo;
import com.onlydoone.busposition.fragment.monitor.util.CycleViewPager;
import com.onlydoone.busposition.fragment.monitor.util.ViewFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhaohui on 2017/2/10.
 */
public class Monitor_ extends Fragment implements View.OnClickListener{

    private LinearLayout layout_vehicle_trail,layout_vehicle_monitor,layout_more,
            layout_vehicle_miles,layout_vehicle_4GVideo,layout_vehicle_police;
    private View view;

    private List<ImageView> views = new ArrayList<ImageView>();
    private List<ADInfo> infos = new ArrayList<ADInfo>();
    private CycleViewPager cycleViewPager;

    private String[] imageUrls = {"http://47.93.114.174:15000/upload/viewPage1.jpg",
            "http://47.93.114.174:15000/upload/viewPage2.jpg",
            "http://47.93.114.174:15000/upload/viewPage3.jpg",
            "http://47.93.114.174:15000/upload/viewPage4.jpg"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.monitor_, container, false);
        //初始化控件
        initView();
        //初始化imageLoader
        configImageLoader();
        initialize();
        return view;
    }

    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
            if (cycleViewPager.isCycle()) {
                position = position - 1;
                Toast.makeText(getActivity(),
                        "" + position, Toast.LENGTH_SHORT)
                        .show();
            }

        }

    };

    @SuppressLint("NewApi")
    private void initialize() {

        cycleViewPager = (CycleViewPager) getChildFragmentManager().findFragmentById(R.id.fragment);

        for(int i = 0; i < imageUrls.length; i ++){
            ADInfo info = new ADInfo();
            info.setUrl(imageUrls[i]);
            info.setContent("图片-->" + i );
            infos.add(info);
        }

        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(getActivity(), infos.get(infos.size() - 1).getUrl()));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(getActivity(), infos.get(i).getUrl()));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(getActivity(), infos.get(0).getUrl()));

        // 设置循环，在调用setData方法前调用
        cycleViewPager.setCycle(true);

        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, infos, mAdCycleViewListener);
        //设置轮播
        cycleViewPager.setWheel(true);

        // 设置轮播时间，默认5000ms
        cycleViewPager.setTime(2000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }

    /**
     * 配置ImageLoder
     */
    private void configImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        layout_vehicle_trail = (LinearLayout) view.findViewById(R.id.layout_vehicle_trail);
        layout_vehicle_miles = (LinearLayout) view.findViewById(R.id.layout_vehicle_miles);
        layout_vehicle_monitor = (LinearLayout) view.findViewById(R.id.layout_vehicle_monitor);
        layout_vehicle_4GVideo = (LinearLayout) view.findViewById(R.id.layout_vehicle_4GVideo);
        layout_more = (LinearLayout) view.findViewById(R.id.layout_more);
        layout_vehicle_police = (LinearLayout) view.findViewById(R.id.layout_vehicle_police);

        layout_vehicle_trail.setOnClickListener(this);
        layout_vehicle_miles.setOnClickListener(this);
        layout_vehicle_monitor.setOnClickListener(this);
        layout_vehicle_4GVideo.setOnClickListener(this);
        layout_more.setOnClickListener(this);
        layout_vehicle_police.setOnClickListener(this);

    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_vehicle_trail:
                //车辆轨迹
                Intent intent = new Intent(getActivity(), MonitorVehicleTrail.class);
                Bundle bundle = new Bundle();
                bundle.putString("vehicleid", "车辆轨迹回放");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.layout_vehicle_miles:
                //车辆行驶里程
                Intent intent3 = new Intent(getActivity(), Mileage.class);
                startActivity(intent3);
                break;
            case R.id.layout_vehicle_monitor:
                //在线车辆监控
                Intent intent2 = new Intent(getActivity(), MonitorOnLine.class);
                startActivity(intent2);
                break;
            case R.id.layout_vehicle_4GVideo:
                //4G视频监控
                Intent intent1 = new Intent(getActivity(), MonitorOwnerSearch.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("class","Monitor");
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case R.id.layout_more:
                //更多
                break;
            case R.id.layout_vehicle_police:
                //车辆报警
                Intent intent4= new Intent(getActivity(), MonitorVehiclePolice.class);
                startActivity(intent4);
                break;
        }
    }
}
