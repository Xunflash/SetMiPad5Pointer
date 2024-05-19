package com.xunflash.setMiPad5Pointer;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    public static final String TAG = "setMiPad5Pointer";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        // Hook自身检测模块是否激活
        if ("com.xunflash.setMiPad5Pointer".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod("com.xunflash.setMiPad5Pointer.MainActivity", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

        // 使用 XSharedPreferences 读取新特性开关状态
        XSharedPreferences prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID, "user");
        prefs.reload();
        boolean isNewFeatureEnabled = prefs.getBoolean("flag_newpointer", true);
        Log.i(TAG, "XSharedPreferences file Path: " + (prefs.getFile().getAbsolutePath()));
        Log.i(TAG, "isNewFeatureEnabled: " + (isNewFeatureEnabled ? "1" : "0"));

        // HookMiuiMagicPointerService类
        if (("android".equals(lpparam.packageName)) && (lpparam.processName.equals("android"))) {
            try {
                Class<?> PointerImageView = XposedHelpers.findClass("com.android.server.magicpointer.PointerImageView", lpparam.classLoader);
                Class<?> MiuiMagicPointerService = XposedHelpers.findClass("com.android.server.magicpointer.MiuiMagicPointerService", lpparam.classLoader);

                if (isNewFeatureEnabled) {
                    // 新特性部分
                    Class<?> deviceFeatureClass = XposedHelpers.findClass("miui.os.DeviceFeature", lpparam.classLoader);
                    XposedHelpers.setStaticBooleanField(deviceFeatureClass, "IS_SUPPORT_MAGIC_POINTER", false);
                    XposedBridge.log("SetMiPad5Pointer: NewFeature Enabled");
                } else {
                    // 旧版本处理逻辑
                    for (Method method : MiuiMagicPointerService.getDeclaredMethods()) {
                        if (method.getName().equals("init")) {
                            XposedBridge.hookMethod(method, new XC_MethodHook() {
                                // 获取FeedbackView对象并且调用setVisibility方法隐藏背景阴影
                                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                                    Method setVisibility = PointerImageView.getMethod("setVisibility", int.class);
                                    ImageView FeedbackView = (ImageView) getObjectField(methodHookParam.thisObject, "mFeedbackView");
                                    ImageView PointerView = (ImageView) getObjectField(methodHookParam.thisObject, "mPointerView");
                                    PointerView.setBackground(pngPacker.getPng());
                                    setVisibility.invoke(FeedbackView, View.GONE);
                                    XposedBridge.log("SetMiPad5Pointer: Hook end");
                                }
                            });
                        }
                        // 把这两个根据页面显示更新显示指针的方法移除（移除后会影响指针形状改变，暂时不做修改）
                        if (method.getName().equals("updatePointerStyleIfNeed") || method.getName().equals("updatePointerColor")) {
                            XposedBridge.hookMethod(method, new XC_MethodReplacement() {
                                @Override
                                protected Object replaceHookedMethod(MethodHookParam param) {
                                    return null;
                                }
                            });
                        }
                    }
                }
            } catch (Throwable t) {
                XposedBridge.log("SetMiPad5Pointer: " + (isNewFeatureEnabled ? "NewVersion" : "OldVersion") + ":" + t);
            }
        }
    }
}
