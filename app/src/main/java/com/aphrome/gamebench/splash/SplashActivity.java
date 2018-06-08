package com.aphrome.gamebench.splash;

import android.os.Bundle;
import android.view.WindowManager;

import com.lody.virtual.client.core.VirtualCore;

import com.aphrome.gamebench.R;
import com.aphrome.gamebench.VCommends;
import com.aphrome.gamebench.abs.ui.VActivity;
import com.aphrome.gamebench.abs.ui.VUiKit;
import com.aphrome.gamebench.home.FlurryROMCollector;
import com.aphrome.gamebench.home.HomeActivity;
import com.aphrome.gamebench.home.MainActivity;
import jonathanfinerty.once.Once;

public class SplashActivity extends VActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressWarnings("unused")
        boolean enterGuide = !Once.beenDone(Once.THIS_APP_INSTALL, VCommends.TAG_NEW_VERSION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*VUiKit.defer().when(() -> {
            if (!Once.beenDone("collect_flurry")) {
                FlurryROMCollector.startCollect();
                Once.markDone("collect_flurry");
            }
            long time = System.currentTimeMillis();
            doActionInThread();
            time = System.currentTimeMillis() - time;
            long delta = 3000L - time;
            if (delta > 0) {
                VUiKit.sleep(delta);
            }
        }).done((res) -> {
            HomeActivity.goHome(this);
            finish();
        });*/
        MainActivity.goMain(this);
        //HomeActivity.goHome(this);
        finish();
    }


    private void doActionInThread() {
        if (!VirtualCore.get().isEngineLaunched()) {
            VirtualCore.get().waitForEngine();
        }
    }
}
