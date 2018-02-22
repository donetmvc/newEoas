package com.eland.android.eoas;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by liuwenbin on 2018/2/22.
 * 虽然青春不在，但不能自我放逐.
 */

public class testActivity extends AppCompatActivity {

    private TextView txtView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new myHandler(this);
        handler.sendMessage(new Message());
    }

    private void setTextView(String text) {
        txtView.setText(text);
    }

    private final static class myHandler extends Handler {
        WeakReference<testActivity> activity = null;

        private myHandler(testActivity act) {
            activity = new WeakReference<testActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            activity.get().setTextView("123");
        }
    }
}
