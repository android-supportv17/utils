package io.reactivex.rxjava;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rxjava.reactivex.io.utils.AdService;
import rxjava.reactivex.io.utils.AdSplashActivity;
import rxjava.reactivex.io.utils.PermissionManagement;
import rxjava.reactivex.io.utils.SplashActivity2;

public class MainActivity extends AppCompatActivity {
    PermissionManagement permissionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//          startActivity(new Intent(this, AdSplashActivity.class));
        permissionManagement = new PermissionManagement(this);

        if (permissionManagement.isNeedRequestPermission()) {
            //需要申请权限
        }else {
            startService(new Intent(this, AdService.class));
//            startActivity(new Intent(this, AdActivity.class));
        }

    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startService(new Intent(this, AdService.class));
    }
}
