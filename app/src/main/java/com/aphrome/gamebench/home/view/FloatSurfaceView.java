package com.aphrome.gamebench.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
  
public class FloatSurfaceView extends SurfaceView implements Runnable, Callback {  
    private SurfaceHolder mHolder; // 用于控制SurfaceView   
    private Thread t; // 声明一条线程   
    private volatile boolean flag; // 线程运行的标识，用于控制线程   
    private Canvas mCanvas; // 声明一张画布   
    private Paint p; // 声明一支画笔   
    float m_circle_r = 10;  
  
    public FloatSurfaceView(Context context) {  
        super(context);  
  
        mHolder = getHolder(); // 获得SurfaceHolder对象   
        mHolder.addCallback(this); // 为SurfaceView添加状态监听   
        p = new Paint(); // 创建一个画笔对象   
        p.setColor(Color.WHITE); // 设置画笔的颜色为白色   
        setFocusable(true); // 设置焦点   
    }  
  
    /** 
     * 当SurfaceView创建的时候，调用此函数 
     */  
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
        //if(t == null) {
    	t = new Thread(this); // 创建一个线程对象   
        flag = true; // 把线程运行的标识设置成true   
        t.start(); // 启动线程   
        //}
       }  
  
    /** 
     * 当SurfaceView的视图发生改变的时候，调用此函数 
     */  
    @Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width,  
            int height) {  
    }  
  
    /** 
     * 当SurfaceView销毁的时候，调用此函数 
     */  
    @Override  
    public void surfaceDestroyed(SurfaceHolder holder) {  
        mHolder.removeCallback(this);  
        flag = false;
    }  
  
    @Override  
    public void run() {  
        while (flag) {  
            try {  
                synchronized (mHolder) {  
                    Thread.sleep(100); // 让线程休息100毫秒   
                    draw(); // 调用自定义画画方法   
                }  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            } finally {  
                if (mCanvas != null) {  
                    // mHolder.unlockCanvasAndPost(mCanvas);//结束锁定画图，并提交改变。   
  
                }  
            }  
        }  
    }  
  

    private void draw() {  
        mCanvas = mHolder.lockCanvas(); 
        if (mCanvas != null) {  
        	mCanvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);
        	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);  
            paint.setColor(Color.RED);  
            paint.setStrokeWidth(10);  
            paint.setStyle(Style.FILL);  
            if (m_circle_r >= (getWidth() / 10)) {  
                m_circle_r = 0;  
            } else {  
                m_circle_r++;  
            }    
            for (int i = 0; i < 5; i++)  
                for (int j = 0; j < 8; j++)  
                    mCanvas.drawCircle(  
                            (getWidth() / 5) * i + (getWidth() / 10),  
                            (getHeight() / 8) * j + (getHeight() / 16),  
                            m_circle_r, paint);  
            mHolder.unlockCanvasAndPost(mCanvas);  
        }  
    }  
}
