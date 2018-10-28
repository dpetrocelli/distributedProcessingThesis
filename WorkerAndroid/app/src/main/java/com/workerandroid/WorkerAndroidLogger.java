package com.workerandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class WorkerAndroidLogger extends Handler{

    private final WeakReference<ActivityHome> loggingToActivity;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public WorkerAndroidLogger(ActivityHome loggingToActivity) {

        this.loggingToActivity = new WeakReference<>(loggingToActivity);
    }

    @Override
    public void handleMessage(Message message){
        ActivityHome activity = loggingToActivity.get();
        if (activity!= null){
            activity.updateResults(message.getData().getString("result"));
        }
    }

    public void logToUI (String TAG, String stringToLog) {

        ActivityHome activity = loggingToActivity.get();
        if (activity!= null) {
            Bundle msgBundle = new Bundle();

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            msgBundle.putString("result", sdf.format(timestamp) + " - " + stringToLog);

            Message msg = new Message();
            msg.setData(msgBundle);

            this.sendMessage(msg);

            Log.d(TAG, stringToLog);
        }
    }
}
