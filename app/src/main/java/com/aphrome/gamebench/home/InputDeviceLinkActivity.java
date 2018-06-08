package com.aphrome.gamebench.home;

import java.util.ArrayList;  
import java.util.List;  
import android.graphics.Color;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;  
import android.support.v4.app.Fragment;  
import android.support.v4.view.ViewPager;  
import android.support.v4.view.ViewPager.OnPageChangeListener;  
import android.support.v7.app.ActionBarActivity; 
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View; 
import android.view.MenuItem;
import android.view.View.OnClickListener; 
import android.widget.ListView;
import android.widget.TextView;  
import android.widget.AdapterView;
import android.graphics.drawable.Drawable;
import com.aphrome.gamebench.home.adapters.LinkIntroduceAdapter;
import com.aphrome.gamebench.home.adapters.LinkIntroduceAdapter.LinkIntroduceInfo;
import com.aphrome.gamebench.R;
import android.util.Log;

public class InputDeviceLinkActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, OnClickListener {  

    private ListView mList ;
    private TextView mDeviceText, mDeviceKey, mDeviceMouse;
    ArrayList<LinkIntroduceInfo> mInfoList = new ArrayList<LinkIntroduceInfo>();
    private static String TAG ="InputDeviceLinkActivity"; 
    public static void goHere(Context context, String keyboard, String mouse) {
        Intent intent = new Intent(context, InputDeviceLinkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if(keyboard != null)
            intent.putExtra("keyboard", keyboard);
        if(mouse != null)
            intent.putExtra("mouse", mouse);

        context.startActivity(intent);
    }

    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_device_link); 
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
        mList.setAdapter(new LinkIntroduceAdapter(this, mInfoList));
        mList.setOnItemClickListener(this);
    }

    private void initView(){
        mList = (ListView)findViewById(R.id.introduce_list);
        mDeviceText = (TextView)findViewById(R.id.link_device);
        mDeviceKey = (TextView)findViewById(R.id.device_keybroad);
        mDeviceMouse = (TextView)findViewById(R.id.device_mouse);
    }

    private void initData(){
        String type = getResources().getString(R.string.device_keybroad);
        Drawable icon = getDrawable(R.drawable.various_keyboard);
        Drawable arrow = getDrawable(R.drawable.arrow_down);
        LinkIntroduceInfo keybroadInfo = new LinkIntroduceAdapter.LinkIntroduceInfo(type, icon, arrow);
        mInfoList.add(keybroadInfo);
        type = getResources().getString(R.string.device_mouse);
        icon = getDrawable(R.drawable.various_mouse);
        LinkIntroduceInfo keybroadInfo1 = new LinkIntroduceAdapter.LinkIntroduceInfo(type, icon, arrow);
        mInfoList.add(keybroadInfo1);

        Intent intent = getIntent();
        String keyboard = intent.getStringExtra("keyboard");
        String mouse = intent.getStringExtra("mouse");
        if(keyboard != null && mouse != null){
            mDeviceText.setText(getResources().getString(R.string.link_device_2));
        }else if(keyboard != null || mouse != null){
            mDeviceText.setText(getResources().getString(R.string.link_device_1));
        }else{
            mDeviceText.setText(getResources().getString(R.string.link_device_0));
        }
        if(keyboard != null){
            mDeviceKey.setVisibility(View.VISIBLE);
            mDeviceKey.setText(keyboard);
        }
        if(mouse != null){
            mDeviceMouse.setVisibility(View.VISIBLE);            
            mDeviceMouse.setText(mouse);
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.arrow:{
                
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, " onItemClick:"+view+"/"+parent);
        if(view != null){
            int visibility = view.findViewById(R.id.link_introduce).getVisibility();
            Log.d(TAG, " onItemClick1:"+view+"/"+visibility);
            if(visibility == View.VISIBLE){
                view.findViewById(R.id.link_introduce).setVisibility(View.GONE);
                view.findViewById(R.id.show_arrow).animate().rotation(0);
            }else{
                view.findViewById(R.id.link_introduce).setVisibility(View.VISIBLE);
                view.findViewById(R.id.show_arrow).animate().rotation(180);
            }
        }        
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case android.R.id.home:{
            this.finish(); // back button return true; 
            return true;
        }
    }
    return super.onOptionsItemSelected(item); }
}
    
