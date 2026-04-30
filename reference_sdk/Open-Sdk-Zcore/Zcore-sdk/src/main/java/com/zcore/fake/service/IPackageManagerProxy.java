package com.zcore.fake.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import black.android.app.BRActivityThread;
import black.android.app.BRContextImpl;
import black.android.app.ContextImpl;
//import black.android.content.pm.BRPackageManager;
import com.zcore.ZCoreCore;
import com.zcore.app.BActivityThread;
import com.zcore.core.env.AppSystemEnv;
import com.zcore.core.GmsCore;
//import com.zcore.fake.FakeCore;
import com.zcore.fake.hook.BinderInvocationStub;
import com.zcore.fake.hook.MethodHook;
import com.zcore.fake.hook.ProxyMethod;
import com.zcore.fake.service.base.PkgMethodProxy;
import com.zcore.fake.service.base.ValueMethodProxy;
import com.zcore.utils.MethodParameterUtils;
import com.zcore.utils.Reflector;
import com.zcore.utils.Slog;
import com.zcore.utils.compat.BuildCompat;
import com.zcore.utils.compat.ParceledListSliceCompat;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IPackageManagerProxy extends BinderInvocationStub {
    public static final String TAG = "PackageManagerStub";
    
    public static int asInt(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }


    public IPackageManagerProxy() {
        super(BRActivityThread.get().sPackageManager().asBinder());
    }

    @Override
    protected Object getWho() {
        return BRActivityThread.get().sPackageManager();
    }

     @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRActivityThread.get()._set_sPackageManager(proxyInvocation);
        replaceSystemService("package");
        Object systemContext = BRActivityThread.get(ZCoreCore.mainThread()).getSystemContext();
        PackageManager mPackageManager = BRContextImpl.get(systemContext).mPackageManager();
        if (mPackageManager != null) {
            try {
                Reflector.on("android.app.ApplicationPackageManager").field("mPM").set(mPackageManager, proxyInvocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("addOnPermissionsChangeListener", 0));
        addMethodHook(new ValueMethodProxy("removeOnPermissionsChangeListener", 0));
        addMethodHook(new PkgMethodProxy("shouldShowRequestPermissionRationale"));
        if (BuildCompat.isT()) {
            return;
        }
        addMethodHook(new PkgMethodProxy("clearPackagePreferredActivities"));
    }

    @ProxyMethod("resolveIntent")
    public static class ResolveIntent extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            int flags = MethodParameterUtils.toInt(args[2]);
            ResolveInfo resolveInfo = ZCoreCore.getBPackageManager().resolveIntent(intent, resolvedType, flags, BActivityThread.getUserId());
            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("resolveService")
    public static class ResolveService extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            int flags = MethodParameterUtils.toInt(args[2]);
            ResolveInfo resolveInfo = ZCoreCore.getBPackageManager().resolveService(intent, flags, resolvedType, BActivityThread.getUserId());
            if (resolveInfo != null) {
                return resolveInfo;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setComponentEnabledSetting")
    public static class SetComponentEnabledSetting extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
/*
    @ProxyMethod("getPackageInfo")
    public static class GetPackageInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String packageName = (String) args[0];
            int flags = MethodParameterUtils.toInt(args[1]);
            PackageInfo packageInfo = ZCoreCore.getBPackageManager().getPackageInfo(packageName, flags, BActivityThread.getUserId());
if (packageInfo != null) {
    return packageInfo;
}
if ("com.google.android.webview".equals(packageName) || "com.android.webview".equals(packageName)) {
    try {
        return method.invoke(who, args); // ask real system
    } catch (Throwable t) {
        t.printStackTrace();
    }
}
if (AppSystemEnv.isOpenPackage(packageName)) {
    return method.invoke(who, args);
}
            return null;
        }
    }
    
    @ProxyMethod("getPackageInfo")
public static class GetPackageInfo extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) {
        String packageName = (String) args[0];
        int flags = MethodParameterUtils.toInt(args[1]);

        // ✅ Google / GMS → real system result
        if (GmsCore.isGoogleAppOrService(packageName)) {
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        PackageInfo packageInfo =
                ZCoreCore.getBPackageManager()
                        .getPackageInfo(packageName, flags,
                                BActivityThread.getUserId());

        if (packageInfo != null) {
            return packageInfo;
        }

        try {
            return method.invoke(who, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}*/
    
 @ProxyMethod("getPackageInfo")
public static class GetPackageInfo extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        String pkg = (String) args[0];

        // ✅ Twitter / X / Facebook → REAL SYSTEM
        if ("com.twitter.android".equals(pkg)
                || "com.x.android".equals(pkg)
                || "com.facebook.katana".equals(pkg)) {
            return method.invoke(who, args);
        }

        PackageInfo info = ZCoreCore.getBPackageManager()
                .getPackageInfo(pkg,
                        MethodParameterUtils.toInt(args[1]),
                        BActivityThread.getUserId());

        return info != null ? info : method.invoke(who, args);
    }
}
    
    @ProxyMethod("getPackageUid")
    public static class GetPackageUid extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getProviderInfo")
    public static class GetProviderInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = MethodParameterUtils.toInt(args[1]);
            ProviderInfo providerInfo = ZCoreCore.getBPackageManager().getProviderInfo(componentName, flags, BActivityThread.getUserId());
            if (providerInfo != null)
                return providerInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getReceiverInfo")
    public static class GetReceiverInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = MethodParameterUtils.toInt(args[1]);
            ActivityInfo receiverInfo = ZCoreCore.getBPackageManager().getReceiverInfo(componentName, flags, BActivityThread.getUserId());
            if (receiverInfo != null)
                return receiverInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getActivityInfo")
    public static class GetActivityInfo extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = MethodParameterUtils.toInt(args[1]);
            ActivityInfo activityInfo = ZCoreCore.getBPackageManager().getActivityInfo(componentName, flags, BActivityThread.getUserId());
            if (activityInfo != null)
                return activityInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getServiceInfo")
    public static class GetServiceInfo extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            int flags = MethodParameterUtils.toInt(args[1]);
            ServiceInfo serviceInfo = ZCoreCore.getBPackageManager().getServiceInfo(componentName, flags, BActivityThread.getUserId());
            if (serviceInfo != null)
                return serviceInfo;
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            return null;
        }
    }

    @ProxyMethod("getInstalledApplications")
    public static class GetInstalledApplications extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = MethodParameterUtils.toInt(args[0]);
            List<ApplicationInfo> installedApplications = ZCoreCore.getBPackageManager().getInstalledApplications(flags, BActivityThread.getUserId());
            return ParceledListSliceCompat.create(installedApplications);
        }
    }

    @ProxyMethod("getInstalledPackages")
    public static class GetInstalledPackages extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = MethodParameterUtils.toInt(args[0]);
            List<PackageInfo> installedPackages = ZCoreCore.getBPackageManager().getInstalledPackages(flags, BActivityThread.getUserId());
            return ParceledListSliceCompat.create(installedPackages);
        }
    }

     /*  @ProxyMethod("getApplicationInfo")
public static class GetApplicationInfo extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) {
        String packageName = (String) args[0];
        int flags = MethodParameterUtils.toInt(args[1]);

        // ✅ Google / GMS → real system result
        if (GmsCore.isGoogleAppOrService(packageName)) {
            try {
                return method.invoke(who, args);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        ApplicationInfo applicationInfo =
                ZCoreCore.getBPackageManager()
                        .getApplicationInfo(packageName, flags,
                                BActivityThread.getUserId());

        if (applicationInfo != null) {
            // ✅ Required by many games / social SDKs
            applicationInfo.flags |= ApplicationInfo.FLAG_EXTERNAL_STORAGE;
            return applicationInfo;
        }

        try {
            return method.invoke(who, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}*/
   @ProxyMethod("getApplicationInfo")
public static class GetApplicationInfo extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        String pkg = (String) args[0];

        // ✅ Twitter / X / Facebook → REAL SYSTEM
        if ("com.twitter.android".equals(pkg)
                || "com.x.android".equals(pkg)
                || "com.facebook.katana".equals(pkg)) {
            return method.invoke(who, args);
        }

        ApplicationInfo info = ZCoreCore.getBPackageManager()
                .getApplicationInfo(pkg,
                        MethodParameterUtils.toInt(args[1]),
                        BActivityThread.getUserId());

        return info != null ? info : method.invoke(who, args);
    }
}

    @ProxyMethod("queryContentProviders")
    public static class QueryContentProviders extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int flags = MethodParameterUtils.toInt(args[2]);
            List<ProviderInfo> providers = ZCoreCore.getBPackageManager().
                    queryContentProviders(BActivityThread.getAppProcessName(), BActivityThread.getBUid(), flags, BActivityThread.getUserId());
            return ParceledListSliceCompat.create(providers);
        }
    }

    @ProxyMethod("queryIntentReceivers")
    public static class QueryBroadcastReceivers extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = MethodParameterUtils.getFirstParam(args, Intent.class);
            String type = MethodParameterUtils.getFirstParam(args, String.class);
            Integer flags = MethodParameterUtils.getFirstParam(args, Integer.class);
            List<ResolveInfo> resolves = ZCoreCore.getBPackageManager().queryBroadcastReceivers(intent, flags, type, BActivityThread.getUserId());
            Slog.d(TAG, "queryIntentReceivers: " + resolves);

            // http://androidxref.com/7.0.0_r1/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#872
            if (BuildCompat.isN()) {
                return ParceledListSliceCompat.create(resolves);
            }

            // http://androidxref.com/6.0.1_r10/xref/frameworks/base/core/java/android/app/ApplicationPackageManager.java#699
            return resolves;
        }
    }

    @ProxyMethod("resolveContentProvider")
    public static class ResolveContentProvider extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String authority = (String) args[0];
            int flags = MethodParameterUtils.toInt(args[1]);
            ProviderInfo providerInfo = ZCoreCore.getBPackageManager().resolveContentProvider(authority, flags, BActivityThread.getUserId());
            if (providerInfo == null) {
                return method.invoke(who, args);
            }
            return providerInfo;
        }
    }

    @ProxyMethod("canRequestPackageInstalls")
    public static class CanRequestPackageInstalls extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getPackagesForUid")
    public static class GetPackagesForUid extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int uid = (Integer) args[0];
            if (uid == ZCoreCore.getHostUid()) {
                args[0] = BActivityThread.getBUid();
                uid = (int) args[0];
            }
            String[] packagesForUid = ZCoreCore.getBPackageManager().getPackagesForUid(uid);
            Slog.d(TAG, args[0] + " , " + BActivityThread.getAppProcessName() + " GetPackagesForUid: " + Arrays.toString(packagesForUid));
            return packagesForUid;
        }
    }

   @ProxyMethod("getInstallerPackageName")
public static class GetInstallerPackageName extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) {

        String pkg = (args != null && args.length > 0) ? (String) args[0] : null;

        // ❌ Twitter / Facebook → DO NOT FAKE
        if ("com.twitter.android".equals(pkg)
                || "com.x.android".equals(pkg)
                || "com.facebook.katana".equals(pkg)) {
            try {
                return method.invoke(who, args);
            } catch (Throwable ignored) {
                return null;
            }
        }

        // ✅ Google apps only
        if (pkg != null && GmsCore.isGoogleAppOrService(pkg)) {
            return GmsCore.VENDING_PKG;
        }

        try {
            Object installer = method.invoke(who, args);
            return (installer instanceof String && ((String) installer).isEmpty())
                    ? null : installer;
        } catch (Throwable e) {
            return null;
        }
    }
}
    /*
    @ProxyMethod("queryIntentActivities")
public static class QueryIntentActivities extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        Intent intent = (Intent) args[0];
        String pkg = intent != null ? intent.getPackage() : null;

        // ✅ Twitter / Facebook → REAL SYSTEM
        if (pkg != null && (
                pkg.contains("twitter")
                || pkg.contains("facebook")
                || pkg.contains(".x"))) {
            return method.invoke(who, args);
        }

        return ParceledListSliceCompat.create(
                ZCoreCore.getBPackageManager()
                        .queryIntentActivities(
                                intent,
                                MethodParameterUtils.toInt(args[2]),
                                (String) args[1],
                                BActivityThread.getUserId()
                        )
        );
    }
}*/
    @ProxyMethod("queryIntentActivities")
public static class QueryIntentActivities extends MethodHook {

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        Intent intent = (Intent) args[0];

        // ❌ BLOCK twitter native app
        if (intent != null && intent.getPackage() != null
                && intent.getPackage().contains("twitter")) {
            return ParceledListSliceCompat.create(
                    ZCoreCore.getBPackageManager()
                            .queryIntentActivities(
                                    intent,
                                    MethodParameterUtils.toInt(args[2]),
                                    (String) args[1],
                                    BActivityThread.getUserId()
                            )
            );
        }

        return method.invoke(who, args);
    }
}
    
    @ProxyMethod("getSharedLibraries")
    public static class GetSharedLibraries extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // todo
            return ParceledListSliceCompat.create(new ArrayList<>());
        }
    }

    @ProxyMethod("getComponentEnabledSetting")
    public static class getComponentEnabledSetting extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            String packageName = componentName.getPackageName();
            
            ApplicationInfo applicationInfo = ZCoreCore.getBPackageManager().getApplicationInfo(packageName,0, BActivityThread.getUserId());
            
            if(applicationInfo != null){
                return PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
            }
            if (AppSystemEnv.isOpenPackage(componentName)) {
                return method.invoke(who, args);
            }
            throw new IllegalArgumentException();
        }
    }
}
