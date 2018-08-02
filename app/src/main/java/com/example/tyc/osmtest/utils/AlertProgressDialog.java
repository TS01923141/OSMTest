package com.example.tyc.osmtest.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.tyc.osmtest.R;

import java.util.Objects;

/**
 * Created by biji on 2018/5/15.
 */

public class AlertProgressDialog {
    private static final String TAG = "AlertProgressDialog";
    private TextView textView_alertprogressdialog_message;
    private AlertDialog alertDialog;

    //    AlertDialog alertDialog;
    public static AlertDialog create(Context context, String title, String message, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(com.example.tyc.osmtest.R.layout.dialog_alertprogressdialog, null);
        TextView textView_alertprogressdialog_title = view.findViewById(R.id.textView_alertprogressdialog_title);
        TextView textView_alertprogressdialog_message = view.findViewById(R.id.textView_alertprogressdialog_message);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(cancelable)
                .create();
        textView_alertprogressdialog_title.setText(title);
        textView_alertprogressdialog_message.setText(message);
        return alertDialog;
    }

    public static AlertDialog show(Context context, String title, String message, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(com.example.tyc.osmtest.R.layout.dialog_alertprogressdialog, null);
        TextView textView_alertprogressdialog_title = view.findViewById(R.id.textView_alertprogressdialog_title);
        TextView textView_alertprogressdialog_message = view.findViewById(R.id.textView_alertprogressdialog_message);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(cancelable)
                .create();
        textView_alertprogressdialog_title.setText(title);
        textView_alertprogressdialog_message.setText(message);
        alertDialog.show();
        return alertDialog;
    }

    public void showWithProgress(Context context, String title, String message, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_alertprogressdialog, null);
        TextView textView_alertprogressdialog_title = view.findViewById(R.id.textView_alertprogressdialog_title);
        textView_alertprogressdialog_message = view.findViewById(R.id.textView_alertprogressdialog_message);
        alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(cancelable)
                .create();
        textView_alertprogressdialog_title.setText(title);
        textView_alertprogressdialog_message.setText(message);
        alertDialog.show();
    }

    public void setMessage(String message) {
        textView_alertprogressdialog_message.setText(message);
    }

    public boolean isShowing(){
        return alertDialog.isShowing();
    }

    public void dismiss() {
        if (!Objects.equals(alertDialog, null)) {
            alertDialog.dismiss();
        } else {
            Log.e(TAG, "Dialog is null");
        }
    }
}
