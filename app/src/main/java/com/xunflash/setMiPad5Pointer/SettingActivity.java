package com.xunflash.setMiPad5Pointer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingActivity extends AppCompatActivity {
    public boolean NewFeatureEnabled = false;
    private static final String TAG = "setMiPad5Pointer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        hideIcon();
        New_Feature(); // 确保初始化 new_version_switch 的状态
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    public void hideIcon() {
        PackageManager pm = getPackageManager();
        final boolean flag = true;
        SwitchMaterial switchMaterial = findViewById(R.id.hide_icon_switch);
        ComponentName componentName = new ComponentName(this, BuildConfig.APPLICATION_ID + ".launcher");
        @SuppressLint("WorldReadableFiles")
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        if (preferences != null) {
            boolean name = preferences.getBoolean("flag_hideicon", flag);
            switchMaterial.setChecked(name);
        }
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                pm.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } else {
                pm.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
            @SuppressLint("WorldReadableFiles")
            SharedPreferences preferences1 = getSharedPreferences("user", Context.MODE_WORLD_READABLE);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putBoolean("flag_hideicon", isChecked);
            editor.apply();
        });
    }

    public void New_Feature() {
        final boolean flag = true;
        SwitchMaterial switchMaterial = findViewById(R.id.new_version_switch);
        @SuppressLint("WorldReadableFiles")
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean name = preferences.getBoolean("flag_newpointer", flag);
        switchMaterial.setChecked(name);
        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NewFeatureEnabled = isChecked;
            editor.putBoolean("flag_newpointer", isChecked);
            Log.d(TAG, "onCheckedChanged: " + (NewFeatureEnabled ? "1" : "0"));
            editor.apply();
        });
    }
}


