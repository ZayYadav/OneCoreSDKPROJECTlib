package com.onecore.loader.libhelper;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import com.onecore.loader.utils.FLog;
import java.io.File;
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback;

public class VirtualNativeLoaderCallback extends AppLifecycleCallback {
    private static final String LOADER_NAME = "libbgmi.so";
    private static volatile boolean hostLoaded = false;

    private String getProcessName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        return "pid=" + Process.myPid();
    }

    private void tryLoadFromContext(Context context, String stage, String packageName, int userId) {
        if (context == null) {
            FLog.error("[VLoader] " + stage + " context is null for package=" + packageName);
            return;
        }

        File loaderFile = new File(context.getFilesDir(), "loader/" + LOADER_NAME);
        FLog.info("[VLoader] stage=" + stage + ", process=" + getProcessName() + ", package=" + packageName + ", user=" + userId + ", path=" + loaderFile.getAbsolutePath() + ", exists=" + loaderFile.exists());

        if (!loaderFile.exists()) {
            return;
        }

        if (hostLoaded) {
            FLog.info("[VLoader] already loaded in this process, skipping duplicate System.load");
            return;
        }

        try {
            System.load(loaderFile.getAbsolutePath());
            hostLoaded = true;
            FLog.info("[VLoader] System.load success at stage=" + stage + " path=" + loaderFile.getAbsolutePath());
        } catch (Throwable t) {
            FLog.error("[VLoader] System.load failed at stage=" + stage + " error=" + t.getMessage());
        }
    }

    @Override
    public void beforeCreateApplication(String packageName, String processName, Context context, int userId) {
        tryLoadFromContext(context, "beforeCreateApplication", packageName, userId);
    }

    @Override
    public void beforeApplicationOnCreate(String packageName, String processName, Application application, int userId) {
        tryLoadFromContext(application, "beforeApplicationOnCreate", packageName, userId);
    }

    @Override
    public void beforeMainApplicationAttach(Application application, Context context) {
        tryLoadFromContext(context, "beforeMainApplicationAttach", application != null ? application.getPackageName() : "unknown", 0);
    }
}
