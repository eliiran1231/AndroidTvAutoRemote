package com.example.androidtvremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.androidtvremote.logic.AndroidTV;
import com.example.androidtvremote.logic.MainMVP;
import com.example.androidtvremote.logic.MainPresenter;
import com.example.androidtvremote.logic.PagerAdapter;
import com.example.androidtvremote.logic.Task;
import com.example.androidtvremote.logic.TaskAdapter;
import com.example.androidtvremote.ui.VolumeImageView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements MainMVP.View {
    protected final String TAG = "MainActivity";
    private AlertDialog previousDialog;
    private MainPresenter presenter;
    private Button actionButton;
    private Button timeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = MainPresenter.getInstance(this);
        presenter.appCreated();
        TabLayout tab = findViewById(R.id.tab);
        TabItem autoTab = findViewById(R.id.autoTab);
        TabItem remoteTab = findViewById(R.id.remoteTab);
        TabItem settingsTab = findViewById(R.id.settingsTab);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        PagerAdapter pager = new PagerAdapter(getSupportFragmentManager(), getLifecycle(), tab.getTabCount());
        viewPager.setAdapter(pager);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab.selectTab(tab.getTabAt(position));
                Log.e(TAG, "tab " + position);
            }
        });
    }

    public void scanPressed(View view) {
        presenter.scanPressed();
    }

    public void buttonPressed(View view) {
        presenter.buttonPressed(view);
        Log.e(TAG, view.getTag().toString());
    }

    public void addPressed(View view) {
        presenter.addPressed();
    }


    @Override
    public void createLoadingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        ProgressBar loading = new ProgressBar(this);
        alertDialog.setView(loading);
        alertDialog.setTitle(R.string.loading_phase_alert);
        alertDialog.setCancelable(false);
        if (previousDialog != null) previousDialog.dismiss();
        this.previousDialog = alertDialog.create();
        previousDialog.show();
    }

    @Override
    public void createDevicesDialog(ArrayList<AndroidTV> androidTVs, View.OnClickListener listener, int TITLE_TEXT_SIZE) {
        previousDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] names = new CharSequence[androidTVs.size()];
        for (int i = 0; i < names.length; i++) names[i] = androidTVs.get(i).getName();
        builder.setItems(names, (theDialog, which) -> {
            TextView selectedView = new TextView(this);
            AndroidTV tv = androidTVs.get(which);
            selectedView.setTag(tv.getIp());
            selectedView.setText(tv.getName());
            theDialog.dismiss();
            listener.onClick(selectedView);
        });

        TextView title = new TextView(this);
        title.setText(R.string.devices_req);
        title.setGravity(Gravity.START);
        title.setTextSize(TITLE_TEXT_SIZE);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        builder.setCustomTitle(title);
        AlertDialog alertDialog = builder.create();
        this.previousDialog = alertDialog;
        alertDialog.show();
    }

    @Override
    public void createChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialog = layoutInflater.inflate(R.layout.auto_actions_container, null);
        Button timeButton = dialog.findViewById(R.id.timeButton);
        timeButton.setOnClickListener(p -> createTimeDialog(timeButton));
        Button actionButton = dialog.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(q -> createActionDialog(actionButton));
        this.actionButton = actionButton;
        this.timeButton = timeButton;

        builder.setView(dialog);
        builder.setTitle("select Action and time");
        builder.setPositiveButton("save", (dialoge, which) -> presenter.chooseDialogCompleted());
        builder.setNegativeButton("cancel", (c, b) -> {
        });

        builder.create().show();
    }

    @Override
    public void createActionDialog(Button actionButton) {
        AlertDialog.Builder listBuilder = new AlertDialog.Builder(this);
        listBuilder.setTitle("Choose Action");
        String[] actions = {"home", "back", "up", "down", "left", "right", "click", "turn on/off", "volume up", "volume down", "channel up", "channel down"};
        int[] ids = {KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_POWER, KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_CHANNEL_DOWN};
        listBuilder.setItems(actions, (dialog1, which) -> {
            actionButton.setText(actions[which]);
            actionButton.setTag(ids[which]);
        });
        listBuilder.create().show();
    }

    @Override
    public void createTimeDialog(Button timeButton) {
        AtomicInteger hour = new AtomicInteger();
        AtomicInteger minute = new AtomicInteger();
        TimePickerDialog.OnTimeSetListener onTimeSetListener = ((timePicker, selectHour, selectMinute) -> {
            timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", selectHour, selectMinute));
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, selectHour);
            date.set(Calendar.MINUTE, selectMinute);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            timeButton.setTag(date);
            hour.set(selectHour);
            minute.set(selectMinute);
        });
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour.intValue(), minute.intValue(), true);
        timePickerDialog.setTitle("select time");
        timePickerDialog.show();
    }

    @Override
    public void initializeVolume(View.OnTouchListener listener) {
        VolumeImageView volumeUp = this.findViewById(R.id.volume_up);
        VolumeImageView volumeDown = this.findViewById(R.id.volume_down);
        volumeUp.setOnTouchListener(listener);
        volumeDown.setOnTouchListener(listener);
    }

    @Override
    public void createCodeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        EditText codeInput = new EditText(this);
        alertDialog.setTitle(R.string.code_req);
        alertDialog.setMessage("code:");
        alertDialog.setView(codeInput);
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            String code = codeInput.getText().toString();
            presenter.codeDialogClosed(code);
            dialog.dismiss();
        });
        this.previousDialog.dismiss();
        AlertDialog dialog = alertDialog.create();
        this.previousDialog = dialog;
        dialog.show();
    }

    @Override
    public void addTaskItem(TaskAdapter taskAdapter) {
        String action = actionButton.getText().toString();
        Calendar time = (Calendar) timeButton.getTag();
        if (action.equals("action") || timeButton.getText().equals("time")) {
            Toast.makeText(this, R.string.err_noargs, Toast.LENGTH_SHORT).show();
            return;
        }
        if (time.getTimeInMillis() - 60000 <= System.currentTimeMillis()) {
            Toast.makeText(this, "time is too short, try at least 2 minutes ahead!", Toast.LENGTH_SHORT).show();
            return;
        }
        byte actionId = (byte) ((int) actionButton.getTag());
        Task task = new Task(action, time, actionId, null);
        taskAdapter.getTasks().add(task);
        taskAdapter.notifyItemInserted(taskAdapter.getTasks().size() - 1);
        presenter.taskItemAdded(task);
    }

    @Override
    public void removeTaskItem(TaskAdapter taskAdapter, int index) {
        runOnUiThread(() -> {
            taskAdapter.getTasks().remove(index);
            taskAdapter.notifyItemRemoved(index);
        });
    }
}