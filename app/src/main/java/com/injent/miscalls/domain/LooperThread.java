package com.injent.miscalls.domain;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class LooperThread extends Thread {

    public Handler handler;

    public LooperThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        if (Looper.myLooper() == null)
            Looper.prepare();

        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        Looper.loop();
    }

    public class StartCycle implements Runnable {

        @Override
        public void run() {
            LooperThread thread = new LooperThread(null);
            thread.start();
        }
    }
}