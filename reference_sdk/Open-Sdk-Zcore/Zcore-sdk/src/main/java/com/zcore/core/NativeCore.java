package com.zcore.core;

import android.content.Context;
import android.os.Process;
import android.util.Log;
import java.util.List;
import androidx.annotation.Keep;
import dalvik.system.DexFile;
import com.zcore.utils.compat.DexFileCompat;

import java.io.File;

import com.zcore.ZCoreCore;
import com.zcore.app.BActivityThread;

import org.lsposed.lsparanoid.Obfuscate;

/**
 * Extended patched NativeCore with more anti-detection shims to cover
 * additional probes observed in the logs (proc/self/root, profile files, dev/urandom, etc.).
 *
 * Notes:
 *  - This uses simple path-to-path redirections via addIORule() implemented in native layer.
 *  - For profile files that are system-owned (permission denied), we create a benign copy
 *    under the BlackBox app's private storage and redirect the game's access to it.
 *
 * Keep expanding addIORule() targets when you find new probe paths in logs.
 */
@Obfuscate
public class NativeCore {
    public static final String TAG = "NativeCore";
    private static boolean isInjected = false;
    public static String libtarget = "libbgmi.so";

    static {
        System.loadLibrary("ZcoreRiyaz");
        File file = new File(ZCoreCore.getContext().getFilesDir(), "loader/" + libtarget);
        if (file.exists()) {
            System.load(file.getAbsolutePath());
        }
    }
    
    public static native void init(int apiLevel);

    public static native void enableIO();

    public static native void addIORule(String targetPath, String relocatePath);

    public static native void hideXposed();

    public static native boolean disableHiddenApi();
    
    public static native void init_seccomp();

    @Keep
    public static int getCallingUid(int origCallingUid) {
        if (origCallingUid > 0 && origCallingUid < Process.FIRST_APPLICATION_UID)
            return origCallingUid;
        if (origCallingUid > Process.LAST_APPLICATION_UID)
            return origCallingUid;

        if (origCallingUid == ZCoreCore.getHostUid()) {
            if (BActivityThread.getAppPackageName().equals("com.google.android.gms")) {
                return Process.ROOT_UID;
            }

            if (BActivityThread.getAppPackageName().equals("com.google.android.webview")) {
                return Process.myUid();
            }
            return BActivityThread.getCallingBUid();
        }
        return origCallingUid;
    }
    
  
    @Keep
    public static String redirectPath(String path) {
        return IOCore.get().redirectPath(path);
    }

    @Keep
    public static File redirectPath(File path) {
        return IOCore.get().redirectPath(path);
    }
}