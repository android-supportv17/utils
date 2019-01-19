package rxjava.reactivex.io.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PermissionManagement
{
    public static final int MY_PERMISSION_REQUEST_CODE = 1001;
    //检测MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    //系统授权设置的弹框
    private AlertDialog openAppDetDialog = null;
    private AlertDialog openMiuiAppDetDialog = null;
    private Activity activity;

    public PermissionManagement(Activity activity) {
        this.activity = activity;
    }


    // 检查是否需要申请所需权限。 true 需要申请 false 不需要申请
    public boolean isNeedRequestPermission() {
        boolean isNeedPermissions = false;
        //        判断是否是6.0以上的系统
        if (Build.VERSION.SDK_INT >= 23) {
            //
            if (isAllGranted()) {

            } else {

                /**
                 * 第 2 步: 请求权限
                 */
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSION_REQUEST_CODE
                );
                isNeedPermissions = true;
            }
        } else {

        }
        return isNeedPermissions;
    }
    // Activity onRestart事件内， 检查是否需要申请所需权限。 true 需要申请 false 不需要申请
    public boolean activityRestartCheckPermission() {
        boolean isNeedRequest = false;
        if (Build.VERSION.SDK_INT < 23) {

        } else if (!isAllGranted()) {
            //判断基本的应用权限
            openAppDetails();
            isNeedRequest = true;
        } else if (!initMiuiPermission()) {
            //如果基础的应用权限已经授取；切是小米系统，校验小米的授权管理页面的权限
            openMiuiAppDetails();
            isNeedRequest = true;
        } else {
            //都没有问题了，跳转主页

        }
        return isNeedRequest;
    }

    // 检查权限申请结果 。返回true 已经授权所需权限，返回false还有权限没被授权
    public boolean checkGrantResults(int requestCode,int[] grantResults) {
        boolean isAllGranted = true;
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        } else {
            isAllGranted = false;
        }
        return isAllGranted;
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("需要访问 \"设备信息\"、\"定位\" 和 \"外部存储器\",请到 \"应用信息 -> 权限\" 中授予！");
        builder.setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                activity.startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create();
        }
        if (null != openAppDetDialog && !openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openMiuiAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("需要访问 \"设备信息\"、\"定位\" 和 \"外部存储器\",请到 \"应用信息 -> 权限\" 中授予！");
        builder.setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JumpPermissionManagement.GoToSetting(activity);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        if (null == openMiuiAppDetDialog) {
            openMiuiAppDetDialog = builder.create();
        }
        if (null != openMiuiAppDetDialog && !openMiuiAppDetDialog.isShowing()) {
            openMiuiAppDetDialog.show();
        }
    }

    /**
     * 检测权限
     *
     * @return true 所需权限全部授取  false 存在未授权的权限
     */
    public boolean isAllGranted() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );

        return isAllGranted;
    }

    /**
     * 检查手机是否是miui系统
     *
     * @return
     */

    public boolean isMIUI() {
        String device = Build.MANUFACTURER;
        System.out.println("Build.MANUFACTURER = " + device);
        if (device.equals("Xiaomi")) {
            System.out.println("this is a xiaomi device");
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } else {
            return false;
        }
    }

    /**
     * 判断小米MIUI系统中授权管理中对应的权限授取
     *
     * @return false 存在核心的未收取的权限   true 核心权限已经全部授权
     */
    @TargetApi(19)
    public boolean initMiuiPermission() {
        AppOpsManager appOpsManager = (AppOpsManager) activity.getSystemService(Context.APP_OPS_SERVICE);
        String packageName = activity.getPackageName();
        int locationOp = appOpsManager.checkOp(AppOpsManager.OPSTR_FINE_LOCATION, Binder.getCallingUid(),packageName) ;
        if (locationOp == AppOpsManager.MODE_IGNORED) {
            return false;
        }

//        int cameraOp = appOpsManager.checkOp(AppOpsManager.OPSTR_CAMERA, Binder.getCallingUid(), packageName);
//        if (cameraOp == AppOpsManager.MODE_IGNORED) {
//            return false;
//        }

        int phoneStateOp = appOpsManager.checkOp(AppOpsManager.OPSTR_READ_PHONE_STATE, Binder.getCallingUid(), packageName);
        if (phoneStateOp == AppOpsManager.MODE_IGNORED) {
            return false;
        }

        int readSDOp = appOpsManager.checkOp(AppOpsManager.OPSTR_READ_EXTERNAL_STORAGE, Binder.getCallingUid(), packageName);
        if (readSDOp == AppOpsManager.MODE_IGNORED) {
            return false;
        }

        int writeSDOp = appOpsManager.checkOp(AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE, Binder.getCallingUid(), packageName);
        if (writeSDOp == AppOpsManager.MODE_IGNORED) {
            return false;
        }
        return true;
    }

    public static class JumpPermissionManagement {
        /**
         * Build.MANUFACTURER
         */
        private static final String MANUFACTURER_HUAWEI = "Huawei";//华为
        private static final String MANUFACTURER_MEIZU = "Meizu";//魅族
        private static final String MANUFACTURER_XIAOMI = "Xiaomi";//小米
        private static final String MANUFACTURER_SONY = "Sony";//索尼
        private static final String MANUFACTURER_OPPO = "OPPO";
        private static final String MANUFACTURER_LG = "LG";
        private static final String MANUFACTURER_VIVO = "vivo";
        private static final String MANUFACTURER_SAMSUNG = "samsung";//三星
        private static final String MANUFACTURER_LETV = "Letv";//乐视
        private static final String MANUFACTURER_ZTE = "ZTE";//中兴
        private static final String MANUFACTURER_YULONG = "YuLong";//酷派
        private static final String MANUFACTURER_LENOVO = "LENOVO";//联想

        /**
         * 此函数可以自己定义
         *
         * @param activity
         */
        public static void GoToSetting(Activity activity) {
            switch (Build.MANUFACTURER) {
                case MANUFACTURER_HUAWEI:
                    Huawei(activity);
                    break;
                case MANUFACTURER_MEIZU:
                    Meizu(activity);
                    break;
                case MANUFACTURER_XIAOMI:
                    Xiaomi(activity);
                    break;
                case MANUFACTURER_SONY:
                    Sony(activity);
                    break;
                case MANUFACTURER_OPPO:
                    OPPO(activity);
                    break;
                case MANUFACTURER_LG:
                    LG(activity);
                    break;
                case MANUFACTURER_LETV:
                    Letv(activity);
                    break;
                default:
                    ApplicationInfo(activity);
                    Log.e("goToSetting", "目前暂不支持此系统");
                    break;
            }
        }

        public static void Huawei(Activity activity) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        }

        public static void Meizu(Activity activity) {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            activity.startActivity(intent);
        }

        public static void Xiaomi(Activity activity) {
            // 只兼容miui v5/v6 的应用权限设置页面，否则的话跳转应用设置页面（权限设置上一级页面）
            String miuiVersion = getMiuiVersion();
            Intent intent = null;
            if ("V5".equals(miuiVersion)) {
                Uri packageURI = Uri.parse("package:" + activity.getApplicationInfo().packageName);
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            } else if ("V6".equals(miuiVersion) || "V7".equals(miuiVersion)) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", activity.getPackageName());
            } else if ("V8".equals(miuiVersion)) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", activity.getPackageName());
            } else {
            }

            if (null != intent)
                activity.startActivity(intent);
        }

        public static void Sony(Activity activity) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        }

        public static void OPPO(Activity activity) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        }

        public static void LG(Activity activity) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        }

        public static void Letv(Activity activity) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
            activity.startActivity(intent);
        }

        /**
         * 只能打开到自带安全软件
         *
         * @param activity
         */
        public static void _360(Activity activity) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            intent.setComponent(comp);
            activity.startActivity(intent);
        }

        /**
         * 应用信息界面
         *
         * @param activity
         */
        public static void ApplicationInfo(Activity activity) {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
            }
            activity.startActivity(localIntent);
        }

        /**
         * 系统设置界面
         *
         * @param activity
         */
        public static void SystemConfig(Activity activity) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
        }

        public static String getMiuiVersion() {
            String line;
            BufferedReader input = null;
            try {
                Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
                input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
                line = input.readLine();
                input.close();
            } catch (IOException ex) {
                return null;
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
            }
            return line;
        }
    }
}
