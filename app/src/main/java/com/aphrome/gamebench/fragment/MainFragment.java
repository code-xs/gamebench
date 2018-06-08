package com.aphrome.gamebench.fragment;


import android.support.annotation.Nullable;  
import android.support.v4.app.Fragment;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;
import android.widget.GridView;  
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.ImageView;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.content.Context;
import android.hardware.input.InputManager;
import android.view.InputDevice;
import android.widget.TextView;
import android.os.UserHandle;
import android.os.Bundle;

import com.lody.virtual.client.core.VirtualCore;
import com.aphrome.gamebench.R;
import com.aphrome.gamebench.home.DataUtils;
import com.aphrome.gamebench.home.adapters.AppListAdapter;
import com.aphrome.gamebench.home.DualAppManager;
import com.aphrome.gamebench.home.InputDeviceLinkActivity;

import java.util.HashMap;
import java.util.ArrayList;
import android.util.Log;
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener,
    InputManager.InputDeviceListener,View.OnClickListener{

    private final String TAG = "MainFragment";
    private GridView mGridView ;
    private TextView mTextView ;
    private ImageView mImageView;
    private String mKeyboard, mMouse;
    public ArrayList<HashMap<String, Object>> mAppItems = new ArrayList<HashMap<String, Object>>();;
    @Override  
    public View onCreateView(LayoutInflater inflater,  
        ViewGroup container,  Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.fragment_main, null);  
        mGridView = (GridView)view.findViewById(R.id.appManagerGridView);
        mTextView = (TextView)view.findViewById(R.id.context);
        mImageView = (ImageView)view.findViewById(R.id.arrow);
        initData();
        mImageView.setOnClickListener(this);
        mGridView.setAdapter(new AppListAdapter(getActivity(), mAppItems));
        mGridView.setOnItemClickListener(this);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mKeyboard = getDeviceLinkInfo("Keyboard");
        mMouse = getDeviceLinkInfo("Mouse");
        String content = getActivity().getResources().getString(R.string.device_link_no);
        if(mKeyboard != null && mMouse != null){
            content = getActivity().getResources().getString(R.string.device_link_two)+ mKeyboard+", "+mMouse;
        }else if(mKeyboard != null){
            content = getActivity().getResources().getString(R.string.device_link_one)+ mKeyboard;
        }else if(mMouse != null){
            content = getActivity().getResources().getString(R.string.device_link_one)+ mMouse;
        }
        mTextView.setText(content);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String pkg = getPackageName(mAppItems, position);
        DualAppManager manager = DualAppManager.getInstance();
        boolean isInstall = manager.checkInstallVirtuallApp(pkg);
        Log.d(TAG, "onItemClick view["+position+"] to pkg:"+pkg+ " check pkg:"+isInstall);
        if(isInstall){
            manager.launch(getActivity(), pkg, 0);
        }else{
            boolean ret = manager.installOrRemoveApp(getActivity(), pkg);
            Log.d(TAG, "onItemClick install ret:"+ret + " for "+pkg);
            manager.launch(getActivity(), pkg, 0);
        }
    }

    @Override
    public void onClick(View v){
        Log.d(TAG, "onClick v"+v);
        switch(v.getId()){
            case R.id.arrow:{
                InputDeviceLinkActivity.goHere(getActivity(), mKeyboard, mMouse);
                break;
            }
        }
    }

    public void initData() {
        HashMap<String, Object> map = getMapInfo(VirtualCore.get().getContext(), "com.tencent.mm", 0);
        if(map != null)mAppItems.add(map);
        map = getMapInfo(VirtualCore.get().getContext(), "com.tencent.mobileqq", 0);
        if(map != null)mAppItems.add(map);
        map = getMapInfo(VirtualCore.get().getContext(), "com.tencent.tmgp.pubgmhd", 0);
        if(map != null)mAppItems.add(map);
        map = getMapInfo(VirtualCore.get().getContext(), "com.eg.android.AlipayGphone", 0);
        if(map != null)mAppItems.add(map);        
    }

    private HashMap<String, Object> getMapInfo(Context context, String pkg, int flag){
        HashMap<String, Object> map = new HashMap<String, Object>();
        String appName = DataUtils.getAppName(context, pkg, flag);
        Drawable appIcon = DataUtils.getAppIcon(context, pkg, flag);
        Log.d(TAG, " appName:"+appName+" icon:"+appIcon);
        if(appName != null && appIcon != null){
            map.put("icon", appIcon);
            map.put("name", appName); 
            map.put("package", pkg); 
            return map;
        }
        return null;
    }

    private String getPackageName(ArrayList<HashMap<String, Object>> list, int pos){
        HashMap<String, Object> map = list.get(pos);
        return (String)map.get("package");
    }
/*
    private void getDeviceLinkInfo(){
        try {
            Process p=Runtime.getRuntime().exec("cat /proc/bus/input/devices");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while((line = in.readLine())!= null){
                String deviceInfo = line.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    private String getDeviceLinkInfo(String key){
        InputManager mIm = null;
        if(mIm == null) {
            mIm = (InputManager) getActivity().getSystemService(Context.INPUT_SERVICE);
            mIm.registerInputDeviceListener(this, null);
        }

        final int[] devices = InputDevice.getDeviceIds();
        for (int i = 0; i < devices.length; i++) {
            InputDevice device = InputDevice.getDevice(devices[i]);  
            //if (device != null && !device.isVirtual() && device.isExternal()) {
                //if(device.getName().contains("Mouse") || device.getName().contains("Keyboard")) {
            if (device != null && !device.isVirtual()) {
                if(device.getName().contains(key)) {
                    Log.d(TAG, "device.getName()=" + device.getName() + " device.getId() " + device.getId() + " getDescriptor " + device.getDescriptor());
                    return device.getName();
                }
            }
        }
        return null;
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        Log.d("huasong", "onInputDeviceRemoved " + deviceId);
        InputDevice device = InputDevice.getDevice(deviceId);  
        //if (device != null && !device.isVirtual() && (device.isFullKeyboard() || device.isExternal())) {
            //if(device.getName().contains("Mouse") || device.getName().contains("Keyboard")) {
        if (device != null && !device.isVirtual()) {
            if(device.getName().contains("Mouse") || device.getName().contains("Keyboard")) {
                Log.d(TAG, "device.getName()=" + device.getName() + " device.getId() " + device.getId() + " getDescriptor " + device.getDescriptor());
            }
        }
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
    } 
} 
