package com.onlydoone.busposition.Utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onlydoone.busposition.R;

/**
 * Created by Administrator on 2017/1/17 0017.
 */

public class Dialog_Vehicle extends Dialog {

    public Dialog_Vehicle(Context context) {
        super(context);
    }

    public Dialog_Vehicle(Context context, int theme) {
        super(context, theme);
    }
    public static class Builder {
        private Context context;
        //dialog的标题
        private String vehicle_title;
        //dialog的内容（经纬度信息）
        private String context_lat_lon;
        //dialog的内容（车辆角度信息）
        private String context_angle;
        //dialog的内容（车辆在线信息）
        private String context_icon_type;
        //dialog的内容（车辆车速信息）
        private String context_speed;
        //dialog的内容（车辆行驶里程信息）
        private String context_miles;
        //dialog的内容（车辆gps定位时间信息）
        private String context_gps_time;
        //dialog关闭按钮
        private String textView_break;

        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private DialogInterface.OnClickListener textView_break_onClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置dialog中的车辆信息
         * @param context_lat_lon
         * @return
         */
        public Builder setMessage(String vehicle_title,String context_lat_lon,String context_angle,String context_icon_type,
                                  String context_speed,String context_miles,String context_gps_time) {
            this.vehicle_title = vehicle_title;
            this.context_lat_lon = context_lat_lon;
            this.context_angle = context_angle;
            this.context_icon_type = context_icon_type;
            this.context_speed = context_speed;
            this.context_miles = context_miles;
            this.context_gps_time = context_gps_time;
            return this;
        }

        /**
         * Me the Dialog vehicle_title from resource
         *
         * @param vehicle_title
         * @return
         */
        public Builder setTitle(int vehicle_title) {
            this.vehicle_title = (String) context.getText(vehicle_title);
            return this;
        }

        /**
         * Me the Dialog vehicle_title from String
         *
         * @param vehicle_title
         * @return
         */

        public Builder setTitle(String vehicle_title) {
            this.vehicle_title = vehicle_title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Me the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setTV_break_onClickListener(String textView_break,
                                         DialogInterface.OnClickListener listener) {
            this.textView_break = textView_break;
            this.textView_break_onClickListener = listener;
            return this;
        }

        public Dialog_Vehicle create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final Dialog_Vehicle dialog = new Dialog_Vehicle(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_vehicle, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // me the dialog vehicle_title
            ((TextView) layout.findViewById(R.id.vehicle_title)).setText(vehicle_title);
            // me the confirm button
            ((TextView) layout.findViewById(R.id.textView_break))
                    .setText(textView_break);
            ((TextView) layout.findViewById(R.id.textView_break))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textView_break_onClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
            // me the content message
            if (vehicle_title!= null|| context_lat_lon!= null|| context_angle!= null|| context_icon_type!= null||
                     context_speed!= null|| context_miles!= null|| context_gps_time!= null) {
                ((TextView) layout.findViewById(R.id.vehicle_title)).setText(vehicle_title);
                ((TextView) layout.findViewById(R.id.context_lat_lon)).setText(context_lat_lon);
                ((TextView) layout.findViewById(R.id.context_angle)).setText(context_angle);
                ((TextView) layout.findViewById(R.id.context_icon_type)).setText(context_icon_type);
                ((TextView) layout.findViewById(R.id.context_speed)).setText(context_speed);
                ((TextView) layout.findViewById(R.id.context_miles)).setText(context_miles);
                ((TextView) layout.findViewById(R.id.context_gps_time)).setText(context_gps_time);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
