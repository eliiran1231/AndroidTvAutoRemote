package com.example.androidtvremote.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VolumeImageView extends androidx.appcompat.widget.AppCompatImageView {

    public VolumeImageView(@NonNull Context context) {
        super(context);
    }

    public VolumeImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VolumeImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        //vibrate
        return super.performClick();
    }
}
