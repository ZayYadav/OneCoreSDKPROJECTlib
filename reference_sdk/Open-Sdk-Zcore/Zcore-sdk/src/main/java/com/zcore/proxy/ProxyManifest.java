package com.zcore.proxy;

import java.util.Locale;

import com.zcore.ZCoreCore;

/**
 * Created by Milk on 4/1/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ProxyManifest {
    public static final int FREE_COUNT = 50;

    public static boolean isProxy(String msg) {
        return getBindProvider().equals(msg) || msg.contains("proxy_content_provider_");
    }

    public static String getBindProvider() {
        return ZCoreCore.getHostPkg() + ".ZcoreRiyaz.SystemCallProvider";
    }

    public static String getProxyAuthorities(int index) {
        return String.format(Locale.CHINA, "%s.proxy_content_provider_%d", ZCoreCore.getHostPkg(), index);
    }

    public static String getProxyPendingActivity(int index) {
        return String.format(Locale.CHINA, "com.zcore.proxy.ProxyPendingActivity$P%d", index);
    }

    public static String getProxyActivity(int index) {
        return String.format(Locale.CHINA, "com.zcore.proxy.ProxyActivity$P%d", index);
    }

    public static String TransparentProxyActivity(int index) {
        return String.format(Locale.CHINA, "com.zcore.proxy.TransparentProxyActivity$P%d", index);
    }

    public static String getProxyService(int index) {
        return String.format(Locale.CHINA, "com.zcore.proxy.ProxyService$P%d", index);
    }

    public static String getProxyJobService(int index) {
        return String.format(Locale.CHINA, "com.zcore.proxy.ProxyJobService$P%d", index);
    }

    public static String getProxyFileProvider() {
        return ZCoreCore.getHostPkg() + ".ZcoreRiyaz.FileProvider";
    }

    public static String getProxyReceiver() {
        return ZCoreCore.getHostPkg() + ".stub_receiver";
    }

    public static String getProcessName(int bPid) {
        return ZCoreCore.getHostPkg() + ":p" + bPid;
    }
}
