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
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.TextView;  
import com.aphrome.gamebench.adapters.TabFragmentPagerAdapter;
import com.aphrome.gamebench.fragment.MainFragment;
import com.aphrome.gamebench.R;
import com.aphrome.gamebench.home.DualAppManager;  
public class MainActivity extends ActionBarActivity implements OnClickListener {  

    private ViewPager myViewPager;  
    private List<Fragment> list;  
    private TabFragmentPagerAdapter adapter; 
    private TextView tv_item_one;  
    private TextView tv_item_two;  
    private TextView tv_item_three; 

    public static void goMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        InitView();  
        DualAppManager manager = DualAppManager.getInstance();
        String pkg1 = "com.tencent.tmgp.pubgmhd";
        manager.removeVirtualApp(pkg1, 0);
        tv_item_one.setOnClickListener(this);  
        tv_item_two.setOnClickListener(this);  
        tv_item_three.setOnClickListener(this);  
        myViewPager.setOnPageChangeListener(new MyPagerChangeListener());  

        list = new ArrayList<>();
        list.add(new MainFragment());  
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), list);  
        myViewPager.setAdapter(adapter);  
        myViewPager.setCurrentItem(0);
        tv_item_one.setBackgroundColor(Color.RED);
    }  

    private void InitView() {  
        tv_item_one = (TextView) findViewById(R.id.tv_item_one);  
        tv_item_two = (TextView) findViewById(R.id.tv_item_two);  
        tv_item_three = (TextView) findViewById(R.id.tv_item_three);  
        myViewPager = (ViewPager) findViewById(R.id.myViewPager);  
    }

    @Override  
    public void onClick(View v) {  
        switch (v.getId()) {  
            case R.id.tv_item_one:  
                myViewPager.setCurrentItem(0);  
                tv_item_one.setBackgroundColor(Color.RED);  
                tv_item_two.setBackgroundColor(Color.WHITE);  
                tv_item_three.setBackgroundColor(Color.WHITE);  
            break;  
            case R.id.tv_item_two:  
                myViewPager.setCurrentItem(1);  
                tv_item_one.setBackgroundColor(Color.WHITE);  
                tv_item_two.setBackgroundColor(Color.RED);  
                tv_item_three.setBackgroundColor(Color.WHITE);  
            break;  
            case R.id.tv_item_three:  
                myViewPager.setCurrentItem(2);  
                tv_item_one.setBackgroundColor(Color.WHITE);  
                tv_item_two.setBackgroundColor(Color.WHITE);  
                tv_item_three.setBackgroundColor(Color.RED);  
            break;  
        }  
    }

    public class MyPagerChangeListener implements OnPageChangeListener {  
      
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
        }  
          
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
        }  
          
        @Override  
        public void onPageSelected(int arg0) {  
            switch (arg0) {  
                case 0:  
                    tv_item_one.setBackgroundColor(Color.RED);  
                    tv_item_two.setBackgroundColor(Color.WHITE);  
                    tv_item_three.setBackgroundColor(Color.WHITE);  
                break;  
                case 1:
                    tv_item_one.setBackgroundColor(Color.WHITE);  
                    tv_item_two.setBackgroundColor(Color.RED);  
                    tv_item_three.setBackgroundColor(Color.WHITE);  
                break;  
                case 2:
                    tv_item_one.setBackgroundColor(Color.WHITE);  
                    tv_item_two.setBackgroundColor(Color.WHITE);  
                    tv_item_three.setBackgroundColor(Color.RED);  
                break;
            }  
        }  
    } 

}