package com.zcore.fake.hook;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import com.zcore.fake.service.WebViewFactoryProxy;
import com.zcore.fake.service.WebViewProxy;
import com.zcore.fake.service.IWebViewUpdateServiceProxy;

import com.zcore.ZCoreCore;
import com.zcore.fake.delegate.AppInstrumentation;
import com.zcore.fake.service.HCallbackProxy;
import com.zcore.fake.service.IAccessibilityManagerProxy;
import com.zcore.fake.service.IAccountManagerProxy;
import com.zcore.fake.service.IActivityClientProxy;
import com.zcore.fake.service.IActivityManagerProxy;
import com.zcore.fake.service.IActivityTaskManagerProxy;
import com.zcore.fake.service.IAlarmManagerProxy;
import com.zcore.fake.service.IAppOpsManagerProxy;
import com.zcore.fake.service.IAppWidgetManagerProxy;
import com.zcore.fake.service.IAutofillManagerProxy;
import com.zcore.fake.service.IConnectivityManagerProxy;
import com.zcore.fake.service.IContextHubServiceProxy;
import com.zcore.fake.service.IDeviceIdentifiersPolicyProxy;
import com.zcore.fake.service.IDevicePolicyManagerProxy;
import com.zcore.fake.service.IDisplayManagerProxy;
import com.zcore.fake.service.IFingerprintManagerProxy;
import com.zcore.fake.service.IGraphicsStatsProxy;
import com.zcore.fake.service.IJobServiceProxy;
import com.zcore.fake.service.ILauncherAppsProxy;
import com.zcore.fake.service.ILocaleManagerProxy;
import com.zcore.fake.service.ILocationManagerProxy;
import com.zcore.fake.service.IMediaRouterServiceProxy;
import com.zcore.fake.service.IMediaSessionManagerProxy;
import com.zcore.fake.service.INetworkManagementServiceProxy;
import com.zcore.fake.service.INotificationManagerProxy;
import com.zcore.fake.service.IPackageManagerProxy;
import com.zcore.fake.service.IPermissionManagerProxy;
import com.zcore.fake.service.IPersistentDataBlockServiceProxy;
import com.zcore.fake.service.IPhoneSubInfoProxy;
import com.zcore.fake.service.IPowerManagerProxy;
import com.zcore.fake.service.IShortcutManagerProxy;
import com.zcore.fake.service.IStorageManagerProxy;
import com.zcore.fake.service.IStorageStatsManagerProxy;
import com.zcore.fake.service.ISystemUpdateProxy;
import com.zcore.fake.service.ITelephonyManagerProxy;
import com.zcore.fake.service.ITelephonyRegistryProxy;
import com.zcore.fake.service.IUserManagerProxy;
import com.zcore.fake.service.IVibratorServiceProxy;
import com.zcore.fake.service.IVpnManagerProxy;
import com.zcore.fake.service.IWifiManagerProxy;
import com.zcore.fake.service.IWifiScannerProxy;
import com.zcore.fake.service.IWindowManagerProxy;
import com.zcore.fake.service.context.ContentServiceStub;
import com.zcore.fake.service.context.RestrictionsManagerStub;
import com.zcore.fake.service.libcore.OsStub;
import com.zcore.fake.service.vivo.IVivoPermissionServiceProxy;
import com.zcore.utils.Slog;
import com.zcore.utils.compat.BuildCompat;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class HookManager {
    public static final String TAG = "HookManager";

    private static final HookManager sHookManager = new HookManager();

    private final Map<Class<?>, IInjectHook> mInjectors = new HashMap<>();

    public static HookManager get() {
        return sHookManager;
    }

    public void init() {
        if (ZCoreCore.get().isBlackProcess() || ZCoreCore.get().isServerProcess()) {
            addInjector(new IDisplayManagerProxy());
            addInjector(new OsStub());
            addInjector(new IActivityManagerProxy());
            addInjector(new IPackageManagerProxy());
            addInjector(new ITelephonyManagerProxy());
            addInjector(new HCallbackProxy());
            addInjector(new IAppOpsManagerProxy());
            addInjector(new INotificationManagerProxy());
            addInjector(new IAlarmManagerProxy());
            addInjector(new IAppWidgetManagerProxy());
            addInjector(new ContentServiceStub());
            addInjector(new IWindowManagerProxy());
            addInjector(new IUserManagerProxy());
            addInjector(new RestrictionsManagerStub());
            addInjector(new IMediaSessionManagerProxy());
            addInjector(new ILocationManagerProxy());
            addInjector(new IStorageManagerProxy());
            addInjector(new ILauncherAppsProxy());
            addInjector(new IJobServiceProxy());
            addInjector(new IAccessibilityManagerProxy());
            addInjector(new ITelephonyRegistryProxy());
            addInjector(new IDevicePolicyManagerProxy());
            addInjector(new IAccountManagerProxy());
            addInjector(new IConnectivityManagerProxy());
            addInjector(new IPhoneSubInfoProxy());
            addInjector(new IMediaRouterServiceProxy());
            addInjector(new IPowerManagerProxy());
            addInjector(new IContextHubServiceProxy());
            addInjector(new IVibratorServiceProxy());
            addInjector(new IPersistentDataBlockServiceProxy());
            
            addInjector(AppInstrumentation.get());
            addInjector(new IWifiManagerProxy());
            addInjector(new IWifiScannerProxy());
            
            // 15.0
            if (BuildCompat.isVivo()){
                addInjector(new IVivoPermissionServiceProxy());
            }
            
            // 13.0
            if (BuildCompat.isT()){
                addInjector(new ILocaleManagerProxy());
            }
            
            // 12.0
            if (BuildCompat.isS()) {
                addInjector(new IActivityClientProxy(null));
                addInjector(new IVpnManagerProxy());
            }
            // 11.0
            if (BuildCompat.isR()) {
                addInjector(new IPermissionManagerProxy());
            }
            // 10.0
            if (BuildCompat.isQ()) {
                addInjector(new IActivityTaskManagerProxy());
            }
            // 9.0
            if (BuildCompat.isPie()) {
                addInjector(new ISystemUpdateProxy());
            }
            // 8.0
            if (BuildCompat.isOreo()) {
                addInjector(new IAutofillManagerProxy());
                addInjector(new IDeviceIdentifiersPolicyProxy());
                addInjector(new IStorageStatsManagerProxy());
            }
            // 7.1
            if (BuildCompat.isN_MR1()) {
                addInjector(new IShortcutManagerProxy());
            }
            // 7.0
            if (BuildCompat.isN()) {
                addInjector(new INetworkManagementServiceProxy());
            }
            // 6.0
            if (BuildCompat.isM()) {
                addInjector(new IFingerprintManagerProxy());
                addInjector(new IGraphicsStatsProxy());
            }
            // 5.0
            if (BuildCompat.isL()) {
                addInjector(new IJobServiceProxy());
            }
        }
        injectAll();
    }

    public void checkEnv(Class<?> clazz) {
        IInjectHook iInjectHook = mInjectors.get(clazz);
        if (iInjectHook != null && iInjectHook.isBadEnv()) {
            Log.d(TAG, "checkEnv: " + clazz.getSimpleName() + " is bad env");
            iInjectHook.injectHook();
        }
    }

    public void checkAll() {
        for (Class<?> aClass : mInjectors.keySet()) {
            IInjectHook iInjectHook = mInjectors.get(aClass);
            if (iInjectHook != null && iInjectHook.isBadEnv()) {
                Log.d(TAG, "checkEnv: " + aClass.getSimpleName() + " is bad env");
                iInjectHook.injectHook();
            }
        }
    }

    void addInjector(IInjectHook injectHook) {
        mInjectors.put(injectHook.getClass(), injectHook);
    }

    void injectAll() {
        for (IInjectHook value : mInjectors.values()) {
            try {
                Slog.d(TAG, "hook: " + value);
                value.injectHook();
            } catch (Exception e) {
                Slog.d(TAG, "hook error: " + value);
            }
        }
    }
}
