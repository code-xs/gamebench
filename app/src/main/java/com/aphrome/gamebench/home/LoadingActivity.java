package com.aphrome.gamebench.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.ImageView;
import android.widget.TextView;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;

import java.util.Locale;

import com.aphrome.gamebench.R;
import com.aphrome.gamebench.abs.ui.VActivity;
import com.aphrome.gamebench.abs.ui.VUiKit;
import com.aphrome.gamebench.home.models.PackageAppData;
import com.aphrome.gamebench.home.repo.PackageAppDataStorage;
import com.aphrome.gamebench.widgets.EatBeansView;
import android.widget.LinearLayout;
import android.view.View;
import com.lody.virtual.client.FloatSurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.util.Log;
import android.view.KeyEvent;
/**
 * @author Lody
 */

public class LoadingActivity extends VActivity {

    private static final String PKG_NAME_ARGUMENT = "MODEL_ARGUMENT";
    private static final String KEY_INTENT = "KEY_INTENT";
    private static final String KEY_USER = "KEY_USER";
    private PackageAppData appModel;
    private EatBeansView loadingView;
    private LinearLayout mLayout;
    public static void launch(Context context, String packageName, int userId) {
        Intent intent = VirtualCore.get().getLaunchIntent(packageName, userId);
        if (intent != null) {
            Intent loadingPageIntent = new Intent(context, LoadingActivity.class);
            loadingPageIntent.putExtra(PKG_NAME_ARGUMENT, packageName);
            loadingPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loadingPageIntent.putExtra(KEY_INTENT, intent);
            loadingPageIntent.putExtra(KEY_USER, userId);
            context.startActivity(loadingPageIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        loadingView = (EatBeansView) findViewById(R.id.loading_anim);
        int userId = getIntent().getIntExtra(KEY_USER, -1);
        String pkg = getIntent().getStringExtra(PKG_NAME_ARGUMENT);
        appModel = PackageAppDataStorage.get().acquire(pkg);
        ImageView iconView = (ImageView) findViewById(R.id.app_icon);
        iconView.setImageDrawable(appModel.icon);
        TextView nameView = (TextView) findViewById(R.id.app_name);
        nameView.setText(String.format(Locale.ENGLISH, "Opening %s...", appModel.name));
        Intent intent = getIntent().getParcelableExtra(KEY_INTENT);
        if (intent == null) {
            return;
        }
        VirtualCore.get().setUiCallback(intent, mUiCallback);
        if (!appModel.fastOpen) {
            try {
                VirtualCore.get().preOpt(appModel.packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //FloatSurfaceView surfaceView = new FloatSurfaceView(this);
        //ViewGroup rootView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        //rootView.addView(surfaceView);
        //surfaceView.hookPhoneWindowForClickEvent(getWindow());
        //FloatSurfaceView surfaceView = new FloatSurfaceView(this);
        //ViewGroup rootView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        //rootView.addView(surfaceView);

        VActivityManager.get().startActivity(intent, userId);
    }

    private final VirtualCore.UiCallback mUiCallback = new VirtualCore.UiCallback() {

        @Override
        public void onAppOpened(String packageName, int userId) throws RemoteException {
            finish();
            Log.d("LoadingActivity", "onAppOpened  "+packageName+"/"+userId);
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("LoadingActivity", "onKeyUp: keyCode:"+keyCode+"event:"+event);
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Let the focused view and/or our descendants get the key first
        Log.d("LoadingActivity", Log.getStackTraceString(new Throwable()));
        Log.d("LoadingActivity", "dispatchKeyEvent");
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadingView.startAnim();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingView.stopAnim();
    }
}
