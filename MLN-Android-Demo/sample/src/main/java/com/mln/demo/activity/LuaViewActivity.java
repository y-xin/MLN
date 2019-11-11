package com.mln.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.immomo.mls.InitData;
import com.immomo.mls.MLSBundleUtils;
import com.immomo.mls.MLSInstance;
import com.mln.demo.R;

import androidx.annotation.Nullable;


/**
 * Created by XiongFangyu on 2018/6/26.
 */
public class LuaViewActivity extends BaseActivity  {

    private static final String TAG = LuaViewActivity.class.getSimpleName();
    private MLSInstance instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final long startTime = System.currentTimeMillis();

        final FrameLayout frameLayout = new FrameLayout(this);
//        frameLayout.setFitsSystemWindows(true);

        setContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        instance = new MLSInstance(this);
        instance.setContainer(frameLayout);
        instance.setBackgroundRes(R.drawable.ic_launcher_background);

        super.onCreate(savedInstanceState);




        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //当layout结束后回调此方法
            @Override
            public void onGlobalLayout() {
                long endTime = System.currentTimeMillis();

                Log.d(TAG, "onGlobalLayout:  layout cast = " + (endTime - startTime));

                //删除监听
                frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);  //api16以上才能用（4.1）

            }
        });




        Intent intent = getIntent();
        if (intent.getExtras() == null || intent.getExtras().getString("LUA_URL") == null) {

            String file = "file://android_asset/MMLuaKitGallery/meilishuo.lua";
            InitData initData = MLSBundleUtils.createInitData(file, false).showLoadingView(true);
            instance.setData(initData);
        } else {
            InitData initData = MLSBundleUtils.parseFromBundle(intent.getExtras()).showLoadingView(true);
            initData.forceDownload();
            instance.setData(initData);
        }

        if (!instance.isValid()) {
            Toast.makeText(this, "something wrong", Toast.LENGTH_SHORT).show();
        }

        storageAndCameraPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        instance.onPause();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() != KeyEvent.ACTION_UP)
                instance.dispatchKeyEvent(event);

            if (!instance.getBackKeyEnabled())
                return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (instance.onActivityResult(requestCode, resultCode, data))
            return;
        super.onActivityResult(requestCode, resultCode, data);
    }
}
