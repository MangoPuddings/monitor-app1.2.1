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

public class Dialog_Version extends Dialog {
    public Dialog_Version(Context context) {
        super(context);
    }

    public Dialog_Version(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        //dialog最新版本号信息
        private String dialog_update_title;
        //dialog新版本大小
        private String dialog_update_msg_size;
        //dialog版本更新的内容
        private String dialog_update_content;

        //dialog最新版本号信息
        public TextView tv_update_title;
        //dialog新版本大小
        public TextView tv_update_msg_size;
        //dialog版本更新的内容
        public TextView tv_update_content;

        //dialog确定按钮
        public String dialog_version_ok;
        //dialog取消按钮
        public String dialog_version_break;

        private DialogInterface.OnClickListener dialog_version_ok_onClickListener;
        private DialogInterface.OnClickListener dialog_version_break_OnClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置dialog中的新版本信息
         */
        public Dialog_Version.Builder setDialog_update_title(String dialog_update_title) {
            this.dialog_update_title = dialog_update_title;
            return this;
        }

        /**
         * 设置dialog中的新版本信息大小
         */
        public Dialog_Version.Builder setDialog_update_content(String dialog_update_content) {
            this.dialog_update_content = dialog_update_content;
            return this;
        }

        /**
         * 设置dialog中的新版本信息
         */
        public Dialog_Version.Builder setDialog_update_msg_size(String dialog_update_msg_size) {
            this.dialog_update_msg_size = dialog_update_msg_size;
            return this;
        }

        /**
         * 设置dialog确定按钮的点击事件
         *
         * @param
         * @param listener
         * @return
         */
        public Dialog_Version.Builder setDialog_version_ok_onClickListener(
                DialogInterface.OnClickListener listener) {
            this.dialog_version_ok_onClickListener = listener;
            return this;
        }

        /**
         * 设置dialog取消按钮的点击事件
         *
         * @param
         * @param listener
         * @return
         */
        public Dialog_Version.Builder setDialog_version_break_OnClickListener(
                DialogInterface.OnClickListener listener) {
            this.dialog_version_break_OnClickListener = listener;
            return this;
        }

        public Dialog_Version create() {
            //LayoutInflater inflater = LayoutInflater.from(this.context);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final Dialog_Version dialog = new Dialog_Version(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_version_update, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // me the dialog vehicle_title

            //dialog确定按钮的点击事件
            ((TextView) layout.findViewById(R.id.btn_update_id_ok))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog_version_ok_onClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });

            //dialog取消按钮点击事件
            ((TextView) layout.findViewById(R.id.btn_update_id_cancel))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_version_break_OnClickListener.onClick(dialog, BUTTON_NEGATIVE);
                        }
                    });

            // me the content message
            if (dialog_update_title != null || dialog_update_msg_size != null || dialog_update_content != null
                    ) {
                ((TextView) layout.findViewById(R.id.tv_update_title)).setText("最新版本号:" + dialog_update_title);
                ((TextView) layout.findViewById(R.id.tv_update_msg_size)).setText("新版本大小:" + dialog_update_msg_size);
                ((TextView) layout.findViewById(R.id.tv_update_content)).setText(dialog_update_content);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
