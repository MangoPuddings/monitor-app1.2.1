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

public class Dialog_QrCode extends Dialog {


    public Dialog_QrCode(Context context) {
        super(context);
    }

    public Dialog_QrCode(Context context, int theme) {
        super(context, theme);
    }
    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Dialog_QrCode create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final Dialog_QrCode dialog = new Dialog_QrCode(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_qr_code, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            dialog.setContentView(layout);
            return dialog;
        }
    }
}
