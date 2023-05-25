package com.example.androidtvremote.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.androidtvremote.R;

import javax.net.ssl.SSLContext;


public class AutoWork extends Worker {

    public AutoWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        ATVHelper db = new ATVHelper(context);
        String workId = this.getId().toString();
        Task task = db.getTask(workId);
        SharedPreferences sharedPreferences = context.getSharedPreferences("ipManagement",Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString(context.getString(R.string.current_ip_key),"");
        Log.e("work",ip);
        SSLContext sslContext = ATVSSLContext.getInstance(context);
        ATVRemote remote = new ATVRemote(sslContext,ip);
        remote.sendCommand(task.getActionId());
        MainPresenter presenter = MainPresenter.getInstance(null);
        if(presenter != null){
            presenter.scheduledTaskFired(task);
        }
        db.deleteTask(task);
        db.close();
        return Result.success();
    }

    
}
