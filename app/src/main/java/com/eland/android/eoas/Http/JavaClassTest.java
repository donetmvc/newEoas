package com.eland.android.eoas.Http;

import android.os.Handler;
import android.os.Message;

/**
 * Created by liuwenbin on 2017/12/7.
 * 虽然青春不在，但不能自我放逐.
 */

public class JavaClassTest {

    private final static String a = "123";

    class JavaClassChild {

        public void print() {
            System.out.println(a);
        }
    }

    private final static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(a == "3") {

            }
        }
    }
}
