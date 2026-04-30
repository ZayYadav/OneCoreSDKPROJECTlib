package com.zcore.core.system;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.zcore.ZCoreCore;
import com.zcore.core.env.AppSystemEnv;
import com.zcore.core.env.BEnvironment;
import com.zcore.core.system.accounts.BAccountManagerService;
import com.zcore.core.system.am.BActivityManagerService;
import com.zcore.core.system.am.BJobManagerService;
import com.zcore.core.system.location.BLocationManagerService;
import com.zcore.core.system.notification.BNotificationManagerService;
import com.zcore.core.system.os.BStorageManagerService;
import com.zcore.core.system.pm.BPackageInstallerService;
import com.zcore.core.system.pm.BPackageManagerService;
import com.zcore.core.system.pm.BXposedManagerService;
import com.zcore.core.system.user.BUserHandle;
import com.zcore.core.system.user.BUserManagerService;
import com.zcore.entity.pm.InstallOption;
import com.zcore.utils.FileUtils;


public class BlackBoxSystem {
    private static BlackBoxSystem sBlackBoxSystem;
    private final List<ISystemService> mServices = new ArrayList<>();
    private final static AtomicBoolean isStartup = new AtomicBoolean(false);

    public static BlackBoxSystem getSystem() {
        if (sBlackBoxSystem == null) {
            synchronized (BlackBoxSystem.class) {
                if (sBlackBoxSystem == null) {
                    sBlackBoxSystem = new BlackBoxSystem();
                }
            }
        }
        return sBlackBoxSystem;
    }
    
    private void initJarEnv() {
        try {
            FileUtils.copyFile(ZCoreCore.getContext().getAssets().open("junit.jar"), BEnvironment.JUNIT_JAR);
            FileUtils.copyFile(ZCoreCore.getContext().getAssets().open("empty.jar"), BEnvironment.EMPTY_JAR);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void startup() {
        if (isStartup.getAndSet(true))
            return;
        BEnvironment.load();

        mServices.add(BPackageManagerService.get());
        mServices.add(BUserManagerService.get());
        mServices.add(BActivityManagerService.get());
        mServices.add(BJobManagerService.get());
        mServices.add(BStorageManagerService.get());
        mServices.add(BPackageInstallerService.get());
        mServices.add(BXposedManagerService.get());
        mServices.add(BProcessManagerService.get());
        mServices.add(BAccountManagerService.get());
        mServices.add(BLocationManagerService.get());
        mServices.add(BNotificationManagerService.get());

        for (ISystemService service : mServices) {
            service.systemReady();
        }

        List<String> preInstallPackages = AppSystemEnv.getPreInstallPackages();
        for (String preInstallPackage : preInstallPackages) {
            try {
                if (!BPackageManagerService.get().isInstalled(preInstallPackage, BUserHandle.USER_ALL)) {
                    PackageInfo packageInfo = ZCoreCore.getPackageManager().getPackageInfo(preInstallPackage, 0);
                    BPackageManagerService.get().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), BUserHandle.USER_ALL);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        //initJarEnv();
    }

}
