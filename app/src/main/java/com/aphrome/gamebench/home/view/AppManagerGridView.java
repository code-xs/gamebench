package com.aphrome.gamebench.home.view;

import android.content.Context;
import android.view.View;
import android.util.AttributeSet;
import android.widget.GridView;

public class AppManagerGridView extends GridView{
    private Context mContext; 

    public AppManagerGridView(Context context){
        this(context,null);
    }

    public AppManagerGridView(Context context, AttributeSet attrs) {  
        this(context, attrs,0);
    }

    public AppManagerGridView(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);
    }
}