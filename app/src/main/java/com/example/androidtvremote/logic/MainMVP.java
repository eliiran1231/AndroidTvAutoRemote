package com.example.androidtvremote.logic;

import android.widget.Button;

import java.util.ArrayList;

public interface MainMVP {
    interface View{
        void createLoadingDialog();
        void createCodeDialog();
        void createDevicesDialog(ArrayList<AndroidTV> androidTVs, android.view.View.OnClickListener listener, int TITLE_TEXT_SIZE);
        void initializeVolume(android.view.View.OnTouchListener listener);
        void createChooseDialog();
        void createActionDialog(Button actionButton);
        void createTimeDialog(Button timeButton);
        void addTaskItem(TaskAdapter taskAdapter);
        void removeTaskItem(TaskAdapter taskAdapter, int index);
    }
    interface Presenter {
        void scanPressed();
        void buttonPressed(android.view.View view);
        void scheduledTaskFired(Task task);
        void codeDialogClosed(String code);
        void operationStarted();
        void DevicesScannerFinished(ArrayList<AndroidTV> androidTVs, android.view.View.OnClickListener listener, int TITLE_TEXT_SIZE);
        void PairingMangerSetupFinished();
        void ATVRemoteFinished(ATVRemote remote);
        void addPressed();
        void chooseDialogCompleted();
        void taskItemAdded(Task task);
        void AppPressed(App app);
        void appCreated();
        void pairingFinished(String ip);
    }
}
