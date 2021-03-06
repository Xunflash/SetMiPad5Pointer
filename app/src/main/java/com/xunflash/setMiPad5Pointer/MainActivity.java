package com.xunflash.setMiPad5Pointer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.gyf.immersionbar.ImmersionBar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "setMiPad5Pointer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(this.getApplicationContext().getResources().getConfiguration().uiMode == 4129) {
            ImmersionBar.with(this)
                    .statusBarColor("#FF2E3133")
                    .autoDarkModeEnable(false)
                    .navigationBarColor("#FF2E3133")
                    .fitsSystemWindows(true)
                    .init();
        }
        else {
            ImmersionBar.with(this)
                    .statusBarColor("#FFFFFFFF")
                    .autoDarkModeEnable(false)
                    .statusBarDarkFont(true)
                    .navigationBarColor("#FFFFFFFF")
                    .navigationBarDarkIcon(true)
                    .fitsSystemWindows(true)
                    .init();
        }
        setContentView(R.layout.activity_main);
        if(isModuleActive()){
            LinearLayout linearLayout=(LinearLayout) findViewById(R.id.module_status_layout);
            TextView textView=findViewById(R.id.module_status_text);
            View icon = findViewById(R.id.icon_status);
            linearLayout.setBackgroundResource(R.drawable.dark_blue_background);
            textView.setText(R.string.module_active_text);
            icon.setBackgroundResource(R.drawable.ic_round_sentiment_satisfied_24);
        }
        TextView version_text_view=findViewById(R.id.version_text_view);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
            version_text_view.setText("??????:"+packInfo.versionName+"\n"+"??????:"+packInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        toAboutActivity();
    }
    public void toSettingActivity(View view){
        Intent intent=new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }
    public void toAboutActivity(){
        MaterialToolbar toolbar=findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.more) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                return MainActivity.super.onOptionsItemSelected(item);
            }
        });
    }
    public static boolean isModuleActive(){
        return false;
    }
}