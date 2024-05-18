package com.xunflash.setMiPad5Pointer;

import static de.robv.android.xposed.XposedBridge.getXposedVersion;
import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XResources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    public static final String TAG = "Xunflash";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Hook自身检测模块是否激活
        if ("com.xunflash.setMiPad5Pointer".equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod("com.xunflash.setMiPad5Pointer.MainActivity", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

        // 使用 XSharedPreferences 读取新特性开关状态
        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID, "user");
        pref.reload();
        boolean isNewFeatureEnabled = pref.getBoolean("flag_newpointer", true);
        XposedBridge.log("SetMiPad5Pointer: File Path:"+ (pref.getFile().canRead()?"1":"0"));
        XposedBridge.log("SetMiPad5Pointer: Hook start"+(isNewFeatureEnabled?"1":"0"));

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
                                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
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
