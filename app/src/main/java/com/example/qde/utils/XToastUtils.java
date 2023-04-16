package com.example.qde.utils;

import androidx.annotation.MainThread;
import androidx.annotation.StringRes;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.toast.XToast;

public class XToastUtils {
    private XToastUtils() {
    }

    @MainThread
    public static void toast(String message) {
        XToast.normal(XUI.getContext(), message).show();
    }

    @MainThread
    public static void normal(String message) {
        XToast.normal(XUI.getContext(), message).show();
    }

    @MainThread
    public static void normal(@StringRes int message) {
        XToast.normal(XUI.getContext(), message).show();
    }

    @MainThread
    public static void info(String message) {
        XToast.info(XUI.getContext(), message).show();
    }

    @MainThread
    public static void info(@StringRes int message) {
        XToast.normal(XUI.getContext(), message).show();
    }

    @MainThread
    public static void success(String message) {
        XToast.success(XUI.getContext(), message).show();
    }

    @MainThread
    public static void success(@StringRes int message) {
        XToast.normal(XUI.getContext(), message).show();
    }

    @MainThread
    public static void warning(String message) {
        XToast.warning(XUI.getContext(), message).show();
    }

    @MainThread
    public static void warning(@StringRes int message) {
        XToast.warning(XUI.getContext(), message).show();
    }

    @MainThread
    public static void error(String message) {
        XToast.error(XUI.getContext(), message).show();
    }

    @MainThread
    public static void error(@StringRes int message) {
        XToast.normal(XUI.getContext(), message).show();
    }
}
