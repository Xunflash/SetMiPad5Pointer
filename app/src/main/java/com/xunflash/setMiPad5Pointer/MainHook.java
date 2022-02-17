package com.xunflash.setMiPad5Pointer;

import static de.robv.android.xposed.XposedBridge.getXposedVersion;
import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.content.Context;
import android.content.res.XResources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
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
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage,IXposedHookZygoteInit {
    public static final String TAG = "Xunflash";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //Hook自身检测模块是否激活
        if("com.xunflash.setMiPad5Pointer".equals(lpparam.packageName)){
            XposedHelpers.findAndHookMethod("com.xunflash.setMiPad5Pointer.MainActivity", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
        //HookMiuiMagicPointerService类
        if (("android".equals(lpparam.packageName)) && (lpparam.processName.equals("android"))) {

            Class<?> PointerImageView = XposedHelpers.findClass("com.android.server.magicpointer.PointerImageView", lpparam.classLoader);

            hookAllMethods(findClass("com.android.server.magicpointer.MiuiMagicPointerService", lpparam.classLoader),"init", new XC_MethodHook() {
                //获取FeedbackView对象并且调用setVisibility方法隐藏背景阴影
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    Context context = (Context) methodHookParam.args[0];
                    Method setVisibility=PointerImageView.getMethod("setVisibility",int.class);
                    ImageView FeedbackView =(ImageView) getObjectField(methodHookParam.thisObject,"mFeedbackView");
                    setVisibility.invoke(FeedbackView,View.GONE);
                    XposedBridge.log("After Hooking");
                }
            });
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        //Hook鼠标指针资源
        XResources.setSystemWideReplacement("android.miui","drawable","magic_pointer_pointer", new XResources.DrawableLoader() {
            @Override
            public Drawable newDrawable(XResources res, int id) throws Throwable {
                return pngPacker.getPng();
            }
        });
        XResources.setSystemWideReplacement("android.miui","drawable","magic_pointer_spot", new XResources.DrawableLoader() {
            @Override
            public Drawable newDrawable(XResources res, int id) throws Throwable {
                return pngPacker.getPng();
            }
        });
        XResources.setSystemWideReplacement("android.miui","drawable","magic_dark_pointer_spot", new XResources.DrawableLoader() {
            @Override
            public Drawable newDrawable(XResources res, int id) throws Throwable {
                return pngPacker.getPng();
            }
        });
        XResources.setSystemWideReplacement("android.miui","drawable","magic_dark_pointer_pointer", new XResources.DrawableLoader() {
            @Override
            public Drawable newDrawable(XResources res, int id) throws Throwable {
                return pngPacker.getPng();
            }
        });
    }

}