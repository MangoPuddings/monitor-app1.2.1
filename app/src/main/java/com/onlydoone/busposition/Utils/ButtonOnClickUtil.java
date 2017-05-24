package com.onlydoone.busposition.Utils;

/**
 *      button多次点击监听工具类
 * Created by zhaohui on 2017/3/24.
 */
public class ButtonOnClickUtil {

    private static long lastClickTime = 0;
    private static long DIFF = 5000;
    private static int lastButtonId = -1;

    /**
     * 判断两次点击的间隔，如果小于5000(默认时间间隔)，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(-1, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于5000(默认时间间隔)，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        //记录本次点击的系统
        long time = System.currentTimeMillis();
        //记录本次点击的系统时间与上次点击的系统时间的时间间隔
        long timeD = time - lastClickTime;

        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            //如果id等于上次点击的ID，&& 上次点击的系统时间大于0（不是默认值-1），&&
            // 点击的时间间隔小于设置的时间间隔  则直接返回true
            //Logs.e("isFastDoubleClick", "短时间内按钮多次触发");
            return true;
        }
        //记录上次点击的系统时间
        lastClickTime = time;
        //记录上次点击的按钮id
        lastButtonId = buttonId;
        return false;
    }
}
