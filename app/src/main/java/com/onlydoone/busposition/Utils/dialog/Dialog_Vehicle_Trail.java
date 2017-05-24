package com.onlydoone.busposition.Utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onlydoone.busposition.R;

import static com.onlydoone.busposition.R.id.dialog_date;

/**
 * Created by zhaohui on 2017/1/22.
 */

public class Dialog_Vehicle_Trail extends Dialog {
    public Dialog_Vehicle_Trail(Context context) {
        super(context);
    }


    public Dialog_Vehicle_Trail(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        //dialog的车牌号信息
        private String vehicle_vehicleid;
        //dialog的内容（日期信息）
        private String context_date;
        //dialog的内容（起始时间信息）
        private String context_start_time;
        //dialog的内容（终止时间信息）
        private String context_end_time;
        //日期
        public TextView vehicleTrailDate;
        //起始时间
        public TextView startTime;
        //终止时间
        public TextView endTime;
        //车牌号输入框
        public EditText dialog_vehicle_vehicleid;
        //dialog的车牌号为空时错误提示
        public LinearLayout dialog_vehicle_vehicleid_isNull;
        //dialog确定按钮
        public String dialog_vehicle_trail_break;
        //dialog取消按钮
        public String dialog_vehicle_trail_cancel;

        private DialogInterface.OnClickListener vehicle_vehicleidClickListener;
        private DialogInterface.OnClickListener context_start_timeClickListener;
        private DialogInterface.OnClickListener context_end_timeClickListener;
        private DialogInterface.OnClickListener context_dateClickListener;
        private DialogInterface.OnClickListener textView_break_onClickListener;
        private DialogInterface.OnClickListener dialog_vehicle_trail_cancelOnClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置dialog中的车辆信息
         */
        public Builder setMessage(String vehicle_vehicleid, String context_date, String context_start_time,
                                  String context_end_time) {
            this.vehicle_vehicleid = vehicle_vehicleid;
            this.context_date = context_date;
            this.context_start_time = context_start_time;
            this.context_end_time = context_end_time;
            return this;
        }

        /**
         * 设置dialog中的车辆信息
         */
        public Builder setVehicleid(String vehicle_vehicleid) {
            this.vehicle_vehicleid = vehicle_vehicleid;
            return this;
        }
        /**
         * 设置dialog日期
         */
        public Builder setContext_Date(int y, int m, int d) {
            context_date = y + "-" + m + "-" + d;
            //textDate.setText(context_date);
            return this;
        }

        /**
         * 设置dialog终止时间的点击事件
         *
         * @param listener
         * @return
         */
        public Builder setContext_end_timeClickListener(
                DialogInterface.OnClickListener listener) {
            this.context_end_timeClickListener = listener;
            return this;
        }

        /**
         * 设置dialog起始时间的点击事件
         *
         * @param listener
         * @return
         */
        public Builder setContext_start_timeClickListener(
                DialogInterface.OnClickListener listener) {
            this.context_start_timeClickListener = listener;
            return this;
        }

        /**
         * 设置dialog车牌号的点击事件
         *
         * @param listener
         * @return
         */
        public Builder setVehicle_vehicleidClickListener(
                DialogInterface.OnClickListener listener) {
            this.vehicle_vehicleidClickListener = listener;
            return this;
        }

        /**
         * 设置dialog日期的点击事件
         */
        public void setContext_date_onClickListener(
                DialogInterface.OnClickListener listener) {
            //this.context_date = date;
            this.context_dateClickListener = listener;
            //return this;
        }

        /**
         * 设置dialog确定按钮的点击事件
         *
         * @param textView_break
         * @param listener
         * @return
         */
        public Builder setdialog_vehicle_trail_cancelOnClickListener(String textView_break,
                                                   DialogInterface.OnClickListener listener) {
            this.dialog_vehicle_trail_cancel = textView_break;
            this.dialog_vehicle_trail_cancelOnClickListener = listener;
            return this;
        }

        /**
         * 设置dialog取消按钮的点击事件
         *
         * @param textView_break
         * @param listener
         * @return
         */
        public Builder setTV_break_onClickListener(String textView_break,
                                                   DialogInterface.OnClickListener listener) {
            this.dialog_vehicle_trail_break = textView_break;
            this.textView_break_onClickListener = listener;
            return this;
        }

        public Dialog_Vehicle create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final Dialog_Vehicle dialog = new Dialog_Vehicle(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_vehicle_trail, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // me the dialog vehicle_title

            //dialog车牌号为空时错误提示
            dialog_vehicle_vehicleid_isNull = (LinearLayout) layout.findViewById(R.id.dialog_vehicle_vehicleid_isnull);

            //dialog确定按钮的点击事件
            ((TextView) layout.findViewById(R.id.dialog_vehicle_trail_break))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textView_break_onClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });

            //dialog取消按钮点击事件
            ((TextView) layout.findViewById(R.id.dialog_vehicle_trail_cancel))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_vehicle_trail_cancelOnClickListener.onClick(dialog,BUTTON_NEGATIVE);
                        }
                    });

            //dialog日期的点击事件
            vehicleTrailDate = ((TextView) layout.findViewById(dialog_date));

            vehicleTrailDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context_dateClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    vehicleTrailDate.setText(context_date);
                }
            });

            //dialog起始时间的点击事件
            startTime = ((TextView) layout.findViewById(R.id.dialog_start_time));
                    startTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context_start_timeClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
            //dialog终止时间的点击事件
            endTime = ((TextView) layout.findViewById(R.id.dialog_end_time));
                    endTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context_end_timeClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
            //dialog车牌号的点击事件
            dialog_vehicle_vehicleid = ((EditText) layout.findViewById(R.id.dialog_vehicle_vehicleid));
                    dialog_vehicle_vehicleid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vehicle_vehicleidClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
            // me the content message
            if (context_date != null || vehicle_vehicleid != null
                    ) {
                dialog_vehicle_vehicleid.setText(vehicle_vehicleid);
                ((TextView) layout.findViewById(dialog_date)).setText(context_date);
                //((TextView) layout.findViewById(dialog_start_time)).setText(context_start_time);
                //((TextView) layout.findViewById(dialog_end_time)).setText(context_end_time);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
