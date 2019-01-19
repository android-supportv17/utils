package rxjava.reactivex.io.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ssp.sdk.platform.ui.ActivityWeb;

import java.util.Timer;
import java.util.TimerTask;

import rxjava.reactivex.io.screen.ScreenManager;
import rxjava.reactivex.io.screen.ScreenReceiverUtil;
import rxjava.reactivex.io.service.DaemonService;

/**
 * Created by xueqili on 2019/1/9.
 */

public class AdService extends Service {
    private int timeSec;
    private int timeMin;
    private int timeHour;
    private ScreenManager mScreenManager;
    private ScreenReceiverUtil mScreenListener;


    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;
    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();

            ActivityManager.finish();

            SystemUtils.onBack();
            SystemUtils.cancelAll(getApplicationContext());

        }

        @Override
        public void onSreenOff() {
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mScreenListener = new ScreenReceiverUtil(this);
        mScreenManager = ScreenManager.getScreenManagerInstance(this);
        mScreenListener.setScreenReceiverListener(mScreenListenerer);
        // 2. 启动系统任务
        mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        mJobManager.startJobScheduler();


//        // 3. 启动前台Service
//        startDaemonService();
        // 4. 启动播放音乐Service
        startPlayMusicService();
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(this, PlayerMusicService.class);
        startService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(this, DaemonService.class);
        startService(intent);
    }


    private void stopDaemonService() {
        Intent intent = new Intent(this, DaemonService.class);
        stopService(intent);
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(this, PlayerMusicService.class);
        stopService(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
