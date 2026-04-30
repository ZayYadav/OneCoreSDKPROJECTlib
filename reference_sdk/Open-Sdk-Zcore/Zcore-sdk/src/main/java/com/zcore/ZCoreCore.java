package com.zcore;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.content.Context;
import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import black.android.app.Activity;
import black.android.app.BRActivityThread;
import black.android.os.BRUserHandle;
import me.weishu.reflection.Reflection;
import android.content.res.Resources;
import org.lsposed.lsparanoid.Obfuscate;

import com.zcore.app.BActivityThread;
import com.zcore.app.LauncherActivity;
import com.zcore.app.configuration.AppLifecycleCallback;
import com.zcore.app.configuration.ClientConfiguration;
import com.zcore.core.GmsCore;
import com.zcore.core.NativeCore;
import com.zcore.core.env.BEnvironment;
import com.zcore.core.system.DaemonService;
import com.zcore.core.system.ServiceManager;
import com.zcore.core.system.user.BUserHandle;
import com.zcore.core.system.user.BUserInfo;
import com.zcore.entity.pm.InstallOption;
import com.zcore.entity.pm.InstallResult;
import com.zcore.entity.pm.InstalledModule;
import com.zcore.fake.delegate.ContentProviderDelegate;
import com.zcore.fake.frameworks.BActivityManager;
import com.zcore.fake.frameworks.BJobManager;
import com.zcore.fake.frameworks.BPackageManager;
import com.zcore.fake.frameworks.BStorageManager;
import com.zcore.fake.frameworks.BUserManager;
import com.zcore.fake.frameworks.BXposedManager;
import com.zcore.fake.hook.HookManager;
import com.zcore.proxy.ProxyManifest;
import com.zcore.utils.FileUtils;
import com.zcore.utils.ShellUtils;
import com.zcore.utils.Slog;
import com.zcore.utils.compat.BuildCompat;
import com.zcore.utils.compat.BundleCompat;
import com.zcore.utils.compat.XposedParserCompat;
import com.zcore.utils.provider.ProviderCall;
/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@Obfuscate
@SuppressLint({"StaticFieldLeak", "NewApi"})
public class ZCoreCore extends ClientConfiguration {
    public static final String TAG = "ZCoreCore";

    private static final ZCoreCore sZCoreCore = new ZCoreCore();
    private static Context sContext;
    private ProcessType mProcessType;
    private final Map<String, IBinder> mServices = new HashMap<>();
    private Thread.UncaughtExceptionHandler mExceptionHandler;
    private ClientConfiguration mClientConfiguration;
    private final List<AppLifecycleCallback> mAppLifecycleCallbacks = new ArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final int mHostUid = Process.myUid();
    private final int mHostUserId = BRUserHandle.get().myUserId();

    public static ZCoreCore get() {
        return sZCoreCore;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public static PackageManager getPackageManager() {
        return sContext.getPackageManager();
    }

    public static String getHostPkg() {
        return get().getHostPackageName();
    }

    public static int getHostUid() {
        return get().mHostUid;
    }

    public static int getHostUserId() {
        return get().mHostUserId;
    }

    public static Context getContext() {
        return sContext;
    }

    public Thread.UncaughtExceptionHandler getExceptionHandler() {
        return mExceptionHandler;
    }

    public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        mExceptionHandler = exceptionHandler;
    }

    public static Object mainThread() {
        return BRActivityThread.get().currentActivityThread();
    }

    public void startActivity(Intent intent, int userId) {
        if (mClientConfiguration.isEnableLauncherActivity()) {
            LauncherActivity.launch(intent, userId);
        } else {
            getBActivityManager().startActivity(intent, userId);
        }
    }

    public static BJobManager getBJobManager() {
        return BJobManager.get();
    }

    public static BPackageManager getBPackageManager() {
        return BPackageManager.get();
    }

    public static BActivityManager getBActivityManager() {
        return BActivityManager.get();
    }

    public static BStorageManager getBStorageManager() {
        return BStorageManager.get();
    }

    public boolean launchApk(String packageName, int userId) {
        onBeforeMainLaunchApk(packageName,userId);

        Intent launchIntentForPackage = getBPackageManager().getLaunchIntentForPackage(packageName, userId);
        if (launchIntentForPackage == null) {
            return false;
        }
        startActivity(launchIntentForPackage, userId);
        
        bypass();
        
        return true;
    }

    public boolean isInstalled(String packageName, int userId) {
        return getBPackageManager().isInstalled(packageName, userId);
    }

    public void uninstallPackageAsUser(String packageName, int userId) {
        getBPackageManager().uninstallPackageAsUser(packageName, userId);
    }

    public void uninstallPackage(String packageName) {
        getBPackageManager().uninstallPackage(packageName);
    }

    public InstallResult installPackageAsUser(String packageName, int userId) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            return getBPackageManager().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), userId);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new InstallResult().installError(e.getMessage());
        }
    }

    public InstallResult installPackageAsUser(File apk, int userId) {
        return getBPackageManager().installPackageAsUser(apk.getAbsolutePath(), InstallOption.installByStorage(), userId);
    }

    public InstallResult installPackageAsUser(Uri apk, int userId) {
        return getBPackageManager().installPackageAsUser(apk.toString(), InstallOption.installByStorage().makeUriFile(), userId);
    }

    public InstallResult installXPModule(File apk) {
        return getBPackageManager().installPackageAsUser(apk.getAbsolutePath(), InstallOption.installByStorage().makeXposed(), BUserHandle.USER_XPOSED);
    }

    public InstallResult installXPModule(Uri apk) {
        return getBPackageManager().installPackageAsUser(apk.toString(), InstallOption.installByStorage().makeXposed().makeUriFile(), BUserHandle.USER_XPOSED);
    }

    public InstallResult installXPModule(String packageName) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            String path = packageInfo.applicationInfo.sourceDir;
            return getBPackageManager().installPackageAsUser(path, InstallOption.installBySystem().makeXposed(), BUserHandle.USER_XPOSED);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new InstallResult().installError(e.getMessage());
        }
    }

    public void uninstallXPModule(String packageName) {
        uninstallPackage(packageName);
    }

    public boolean isXPEnable() {
        return BXposedManager.get().isXPEnable();
    }

    public void setXPEnable(boolean enable) {
        BXposedManager.get().setXPEnable(enable);
    }

    public boolean isXposedModule(File file) {
        return XposedParserCompat.isXPModule(file.getAbsolutePath());
    }

    public boolean isInstalledXposedModule(String packageName) {
        return isInstalled(packageName, BUserHandle.USER_XPOSED);
    }

    public boolean isModuleEnable(String packageName) {
        return BXposedManager.get().isModuleEnable(packageName);
    }

    public void setModuleEnable(String packageName, boolean enable) {
        BXposedManager.get().setModuleEnable(packageName, enable);
    }

    public List<InstalledModule> getInstalledXPModules() {
        return BXposedManager.get().getInstalledModules();
    }

    public List<ApplicationInfo> getInstalledApplications(int flags, int userId) {
        return getBPackageManager().getInstalledApplications(flags, userId);
    }

    public List<PackageInfo> getInstalledPackages(int flags, int userId) {
        return getBPackageManager().getInstalledPackages(flags, userId);
    }

    public void clearPackage(String packageName, int userId) {
        BPackageManager.get().clearPackage(packageName, userId);
    }

    public void stopPackage(String packageName, int userId) {
        BPackageManager.get().stopPackage(packageName, userId);
    }

    public List<BUserInfo> getUsers() {
        return BUserManager.get().getUsers();
    }

    public BUserInfo createUser(int userId) {
        return BUserManager.get().createUser(userId);
    }

    public void deleteUser(int userId) {
        BUserManager.get().deleteUser(userId);
    }

    public List<AppLifecycleCallback> getAppLifecycleCallbacks() {
        return mAppLifecycleCallbacks;
    }

    public void removeAppLifecycleCallback(AppLifecycleCallback appLifecycleCallback) {
        mAppLifecycleCallbacks.remove(appLifecycleCallback);
    }

    public void addAppLifecycleCallback(AppLifecycleCallback appLifecycleCallback) {
        mAppLifecycleCallbacks.add(appLifecycleCallback);
    }

    public boolean isSupportGms() {
        return GmsCore.isSupportGms();
    }

    public boolean isInstallGms(int userId) {
        return GmsCore.isInstalledGoogleService(userId);
    }

    public InstallResult installGms(int userId) {
        return GmsCore.installGApps(userId);
    }

    public boolean uninstallGms(int userId) {
        GmsCore.uninstallGApps(userId);
        return !GmsCore.isInstalledGoogleService(userId);
    }

    public IBinder getService(String name) {
        IBinder binder = mServices.get(name);
        if (binder != null && binder.isBinderAlive()) {
            return binder;
        }
        Bundle bundle = new Bundle();
        bundle.putString("_B_|_server_name_", name);
        Bundle vm = ProviderCall.callSafely(ProxyManifest.getBindProvider(), "VM", null, bundle);
        binder = BundleCompat.getBinder(vm, "_B_|_server_");
        Slog.d(TAG, "getService: " + name + ", " + binder);
        mServices.put(name, binder);
        return binder;
    }

    /**
     * Process type
     */
    private enum ProcessType {
        /**
         * Server process
         */
        Server,
        /**
         * Black app process
         */
        BAppClient,
        /**
         * Main process
         */
        Main,
    }

    public boolean isBlackProcess() {
        return mProcessType == ProcessType.BAppClient;
    }

    public boolean isMainProcess() {
        return mProcessType == ProcessType.Main;
    }

    public boolean isServerProcess() {
        return mProcessType == ProcessType.Server;
    }
    

    //@Override
    public boolean isHideRoot() {
        return mClientConfiguration.isHideRoot();
    }

   // @Override
    public boolean isHideXposed() {
        return mClientConfiguration.isHideXposed();
    }

    @Override
    public String getHostPackageName() {
        return mClientConfiguration.getHostPackageName();
    }

    @Override
    public boolean requestInstallPackage(File file, int userId) {
        return mClientConfiguration.requestInstallPackage(file, userId);
    }

    private void startLogcat() {
        new Thread(() -> {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getContext().getPackageName() + "_logcat.txt");
            FileUtils.deleteDir(file);
            ShellUtils.execCommand("logcat -c", false);
            ShellUtils.execCommand("logcat -f " + file.getAbsolutePath(), false);
        }).start();
    }

    private static String getProcessName(Context context) {
        int myPid = Process.myPid();
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == myPid) {
                processName = info.processName;
                break;
            }
        }
        if (processName == null) {
            throw new RuntimeException("processName = null");
        }
        return processName;
    }

    public static boolean is64Bit() {
        if (BuildCompat.isM()) {
            return Process.is64Bit();
        } else {
            return Build.CPU_ABI.equals("arm64-v8a");
        }
    }

    private void initNotificationManager() {
        NotificationManager nm = (NotificationManager) ZCoreCore.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ONE_ID = ZCoreCore.getContext().getPackageName() + ".ZcoreRiyaz_core";
        String CHANNEL_ONE_NAME = "ZcoreRiyaz_core";
        if (BuildCompat.isOreo()) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(notificationChannel);
        }
    }
    
    public void onBeforeMainLaunchApk(String packageName,int userid) {
        for (AppLifecycleCallback appLifecycleCallback : ZCoreCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.beforeMainLaunchApk(packageName,userid);
        }
    }
    public void onBeforeMainApplicationAttach(Application app, Context context) {
        for (AppLifecycleCallback appLifecycleCallback : ZCoreCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.beforeMainApplicationAttach(app, context);
        }
    }
    public void onAfterMainApplicationAttach(Application app, Context context) {
        for (AppLifecycleCallback appLifecycleCallback : ZCoreCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.afterMainApplicationAttach(app, context);
        }
    }
    public void onBeforeMainActivityOnCreate(android.app.Activity activity) {
        for (AppLifecycleCallback appLifecycleCallback : ZCoreCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.beforeMainActivityOnCreate(activity);
        }
    }
    public void onAfterMainActivityOnCreate(android.app.Activity activity) {
        for (AppLifecycleCallback appLifecycleCallback : ZCoreCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.afterMainActivityOnCreate(activity);
        }
    }
    
    
    // Inside ZCoreCore.java
// Inside ZCoreCore.java
private void copyRawToInternal(Context context) {
    String fileName = "temp"; // name used later by runant/excpp
    File dataDir;

    // Prefer using the context's data dir (API 24+). If sContext exists and is the same app context, you can replace context with sContext.
    try {
        dataDir = context.getDataDir();
    } catch (NoSuchMethodError e) {
        // Fallback for older APIs: derive data dir from filesDir parent
        dataDir = context.getFilesDir().getParentFile();
    }

    File outDir = new File(dataDir, "ZcoreRiyaz/cache");
    if (!outDir.exists()) {
        if (!outDir.mkdirs()) {
            Log.e("CopyFile", "Failed to create directory: " + outDir.getAbsolutePath());
            // still try to continue — might fail later when writing file
        }
    }

    File outFile = new File(outDir, fileName);

    if (!outFile.exists()) {
        try (InputStream in = context.getResources().openRawResource(R.raw.temp);
             OutputStream out = new FileOutputStream(outFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();

            // Set permissions so exec can run it (owner and group/others)
            // Note: File.setExecutable(boolean, boolean) second arg = ownerOnly; set to false to allow all users (if necessary)
            outFile.setExecutable(true, false);
            outFile.setReadable(true, false);
            outFile.setWritable(true, false);

            Log.d("CopyFile", "File copied to: " + outFile.getAbsolutePath());
        } catch (Resources.NotFoundException rnfe) {
            Log.e("CopyFile", "Raw resource not found: " + rnfe.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CopyFile", "Error copying file: " + e.getMessage());
        }
    } else {
        Log.d("CopyFile", "File already exists at: " + outFile.getAbsolutePath());
    }
}




void runant(final String nf) {
    excpp("/ZcoreRiyaz/cache/" + nf);
}

private void ExecuteElf(String shell) {
    try {
        Runtime.getRuntime().exec(shell);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void excpp(String path) {
    try {
        // Full path → /data/data/<package>/ZcoreRiyaz/cache/<file>
        String fullPath = sContext.getDataDir() + path;

        // Normal exec
        ExecuteElf("chmod 777 " + fullPath);
        ExecuteElf(fullPath);

        // Root exec (optional, if normal fails)
      //  ExecuteElf("su -c chmod 777 " + fullPath);
     //   ExecuteElf("su -c " + fullPath);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void bypass() {
    Handler handler = new Handler(Looper.getMainLooper());

    // After 15 sec
    handler.postDelayed(() -> {
        runant("temp 992");

        // After another 30 sec
        handler.postDelayed(() -> {
            runant("temp 992");

            // After another 38 sec
            handler.postDelayed(() -> {
                runant("temp 992");
            }, 38_000);

        }, 30_000);

    }, 15_000);
}


}
