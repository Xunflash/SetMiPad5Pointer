package com.xunflash.setMiPad5Pointer;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        hideIcon();
        MaterialToolbar toolbar=findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void hideIcon(){
        PackageManager pm =getPackageManager();
        final boolean falg = true;
        SwitchMaterial switchMaterial = findViewById(R.id.hide_icon_switch);
        ComponentName componentName=new ComponentName(this,BuildConfig.APPLICATION_ID+".launcher");
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (preferences != null) {
            boolean name = preferences.getBoolean("flag", falg);
            switchMaterial.setChecked(name);
        }
        switchMaterial.setOnCheckedChangeListener(new SwitchMaterial.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    pm.setComponentEnabledSetting(componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                }else {
                    pm.setComponentEnabledSetting(componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("flag", isChecked);
                editor.apply();
            }
        });
    }
}