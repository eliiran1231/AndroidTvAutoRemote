package com.example.androidtvremote.logic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.androidtvremote.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

public class MainPresenter implements MainMVP.Presenter {
    private static MainPresenter instance;
    private final MainMVP.View view;
    private PairingManager pairingManager;
    private final TaskAdapter taskAdapter;
    private final AppAdapter appAdapter;

    private MainPresenter(MainMVP.View View) {
        this.view = View;
        ATVHelper db = new ATVHelper((Context) View);
        App[] apps = db.getApps();
        this.taskAdapter = new TaskAdapter(new ArrayList<>());
        this.appAdapter = new AppAdapter(apps, this::AppPressed);
        db.close();
    }

    public static MainPresenter getInstance(MainMVP.View context) {
        if (instance == null) {
            instance = new MainPresenter(context);
        }
        return instance;
    }

    @Override
    public void scanPressed() {
        scanAndroidTVs((selectedDevice) -> {
            String ip = selectedDevice.getTag().toString();
            pair(ip);
        });
    }

    @Override
    public void buttonPressed(View view) {
        if (pairingManager == null) return;
        ATVRemote remote = pairingManager.getRemote();
        remote.sendCommand((byte) Integer.parseInt(view.getTag().toString()));
    }

    @Override
    public void addPressed() {
        view.createChooseDialog();
    }

    @Override
    public void chooseDialogCompleted() {
        view.addTaskItem(taskAdapter);
    }

    @Override
    public void taskItemAdded(Task task) {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(AutoWork.class)
                .setInitialDelay(task.getTime().getTimeInMillis() - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();
        WorkManager workManager = WorkManager.getInstance();
        String workRequestId = oneTimeWorkRequest.getId().toString();
        task.setId(workRequestId);
        workManager.enqueue(oneTimeWorkRequest);
        ATVHelper db = new ATVHelper((Context) view);
        db.addTask(task);
        db.close();
    }

    @Override
    public void AppPressed(App app) {
        if (pairingManager == null) return;
        pairingManager.getRemote().openApp(app.getContent());
    }

    @Override
    public void appCreated() {
        ATVHelper db = new ATVHelper((Context) view);
        ArrayList<Task> arrayList = db.getTasks();
        for (int i = 0; i < arrayList.size(); i++) {
            taskAdapter.getTasks().add(arrayList.get(i));
            taskAdapter.notifyItemInserted(i);
        }
    }

    @Override
    public void pairingFinished(String ip) {
        Context context = ((Context) this.view);
        SharedPreferences sharedPreferences = context.getSharedPreferences("ipManagement", Context.MODE_PRIVATE);
        String list = sharedPreferences.getString(context.getString(R.string.ip_list),"");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.current_ip_key), ip);
        editor.putString(context.getString(R.string.ip_list),list+"("+ip+") ");
        editor.apply();
    }

    @Override
    public void scheduledTaskFired(Task task) {
        int index = taskAdapter.getTasks().indexOf(task);
        view.removeTaskItem(taskAdapter, index);
    }

    @Override
    public void codeDialogClosed(String code) {
        pairingManager.finishPairing(code);
    }

    @Override
    public void operationStarted() {
        view.createLoadingDialog();
    }

    @Override
    public void DevicesScannerFinished(ArrayList<AndroidTV> androidTVs, View.OnClickListener listener, int TITLE_TEXT_SIZE) {
        view.createDevicesDialog(androidTVs, listener, TITLE_TEXT_SIZE);
    }

    @Override
    public void PairingMangerSetupFinished() {
        view.createCodeDialog();
    }

    @Override
    public void ATVRemoteFinished(ATVRemote remote) {
        view.initializeVolume((v, event) -> {
            v.performClick();
            remote.volume(event, (byte) Integer.parseInt(v.getTag().toString()));
            return true;
        });
    }

    private void scanAndroidTVs(View.OnClickListener onDeviceSelectedListener) {
        DevicesScanner scanner = new DevicesScanner(this, onDeviceSelectedListener);
        scanner.execute((Activity) view);
    }

    private void pair(String ip) {
        Context context = ((Context) this.view);
        SharedPreferences sharedPreferences = context.getSharedPreferences("ipManagement", Context.MODE_PRIVATE);
        String list = sharedPreferences.getString(context.getString(R.string.ip_list),"");
        SSLContext sslContext = ATVSSLContext.getInstance(context);
        this.pairingManager = new PairingManager(this, sslContext, ip,list);
        pairingManager.execute(ATVSSLContext.getCertificate());
    }

    public TaskAdapter getTaskAdapter() {
        return taskAdapter;
    }

    public AppAdapter getAppAdapter() {
        return appAdapter;
    }
}
