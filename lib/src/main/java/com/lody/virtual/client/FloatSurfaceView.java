package com.lody.virtual.client;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.KeyEvent;
import android.view.Window;
import android.view.View;
import android.util.Log;  
import android.view.MotionEvent;
import android.graphics.PixelFormat;
import android.os.SystemClock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.io.File;

import com.android.dx.stock.ProxyBuilder;
import android.view.InputQueue;
import com.lody.virtual.client.ClassHack;
import java.lang.reflect.Constructor;
import mirror.android.view.ViewRootImpl.WindowInputEventReceiver;
import mirror.android.view.ViewRootImpl;
import android.view.InputEvent;
import android.os.Looper;
import android.content.pm.ApplicationInfo;
import com.lody.virtual.client.core.VirtualCore;
//import android.view.InputEventReceiver;
//import com.android.internal.policy.PhoneWindow;
public class FloatSurfaceView extends SurfaceView implements Runnable, Callback,  RecognizerCallBack{  
    private SurfaceHolder mHolder; // 用于控制SurfaceView   
    private Thread t; // 声明一条线程   
    private volatile boolean flag; // 线程运行的标识，用于控制线程   
    private Canvas mCanvas; // 声明一张画布   
    private Paint p; // 声明一支画笔    
    private static final String TAG ="FloatSurfaceView";
    private Context mContext;
    private boolean mKeyDownPress = false;
    private int mLastConvertX = -1;
    private int mLastConvertY = -1;
    private Object mFirstInputStageObj = null;
    private Object mWindowInputEventReceiver = null;
    public FloatSurfaceView(Context context) {  
        super(context);  
        mContext = context;
        mWindow = ((Activity)context).getWindow();
        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT); 
        p = new Paint();
        p.setColor(Color.WHITE);
        //setFocusable(true);
        //requestFocus();
        //setFocusableInTouchMode(true);
        //setLongClickable(true);
        Log.d(TAG, " FloatSurfaceView  :"+this);
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
        //NativeEngine.loadHookSo();
        //NativeEngine.loadUrl();
        tryDynamicViewProxy(this);
        //openRecognizer();
        Log.d(TAG, " surfaceCreated    ss:"+holder+" this:"+this);
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
        Log.d(TAG, " surfaceDestroyed holder:"+holder+" this:"+this);
        try{
            Method method = ReflectCommon.sCLASS_VIEW.getDeclaredMethod("getViewRootImpl");
            method.setAccessible(true);
            Object viewRoot = method.invoke(this);
            Log.d(TAG, " surfaceDestroyed mFirstInputStageObj:"+mFirstInputStageObj+", viewRoot:"+viewRoot);
            ReflectCommon.sFIELD_mFirstInputStage_VIEWROOT_IMPL.set(viewRoot, mFirstInputStageObj);
            mFirstInputStageObj = null;
        } catch (Throwable throwable) {
            Log.e(TAG, "error in hook viewrootimpl global.", throwable);
        }        
    }

    @Override  
    public void run() {  
        while (flag) {  
            try {  
                synchronized (mHolder) {  
                    Thread.sleep(1000); // 让线程休息100毫秒   
                    draw(); // 调用自定义画画方法   
                }  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            } finally {  
                if (mCanvas != null) {  
                    mHolder.unlockCanvasAndPost(mCanvas);//结束锁定画图，并提交改变。   
                    break;
                }  
            }  
        }  
    }  
    private String[] mapV = {"A", "B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R", "S", "T","U","V","W","X","Y","Z",
        "TAB","SPC","ENTER",/*"0","1",*/"2"};
    private int[]keyV={29, 30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47, 48,49,50,51,52,53,54,61,62,66/*,7,8*/, 9/*, 10, 11*/};
    private int[]keyMapX={200, 166,138,421,315,292,945,151,127,134,1622,1840,1617,1800,1468,419,1295,1620,  309, 1838,1440,1191,300, 834,1122,1200,
        1119,1820,777, /*1737, 1667,*/ 1595};
    private int[]keyMapY={840, 111,242,821, 216,327,965,355,570,994,842,758,1000,991,347,471,746,808,920, 555,983,365,700,985,986,1017,
        960,81,955, /*194, 207,*/ 297};
    private String[] mapCmd = {"左", "右","趴下","跳起","蹲下","跑","停","向左","向右","前进","后退"};//左|右|爬下|跳起|射击|跑|停|向左|向右|前进|后退;
    private int[] cmdMapKey={37, 39, 42, 40, 41, 9, 9, 51, 47, 29, 32};
    /** Key code constant: '0' key. */
    public static final int KEYCODE_0               = 7;
    /** Key code constant: '1' key. */
    public static final int KEYCODE_1               = 8;
    /** Key code constant: '2' key. */
    public static final int KEYCODE_2               = 9;
    /** Key code constant: '3' key. */
    public static final int KEYCODE_3               = 10;
    /** Key code constant: '4' key. */
    public static final int KEYCODE_4               = 11;
    /** Key code constant: '5' key. */
    public static final int KEYCODE_5               = 12;
    /** Key code constant: '6' key. */
    public static final int KEYCODE_6               = 13;
    /** Key code constant: '7' key. */
    public static final int KEYCODE_7               = 14;
    /** Key code constant: '8' key. */
    public static final int KEYCODE_8               = 15;
    /** Key code constant: '9' key. */
    public static final int KEYCODE_9               = 16;    
    /** Key code constant: 'A' key. */
    public static final int KEYCODE_A               = 29;
    /** Key code constant: 'B' key. */
    public static final int KEYCODE_B               = 30;
    /** Key code constant: 'C' key. */
    public static final int KEYCODE_C               = 31;
    /** Key code constant: 'D' key. */
    public static final int KEYCODE_D               = 32;
    /** Key code constant: 'E' key. */
    public static final int KEYCODE_E               = 33;
    /** Key code constant: 'F' key. */
    public static final int KEYCODE_F               = 34;
    /** Key code constant: 'G' key. */
    public static final int KEYCODE_G               = 35;
    /** Key code constant: 'H' key. */
    public static final int KEYCODE_H               = 36;
    /** Key code constant: 'I' key. */
    public static final int KEYCODE_I               = 37;
    /** Key code constant: 'J' key. */
    public static final int KEYCODE_J               = 38;
    /** Key code constant: 'K' key. */
    public static final int KEYCODE_K               = 39;
    /** Key code constant: 'L' key. */
    public static final int KEYCODE_L               = 40;
    /** Key code constant: 'M' key. */
    public static final int KEYCODE_M               = 41;
    /** Key code constant: 'N' key. */
    public static final int KEYCODE_N               = 42;
    /** Key code constant: 'O' key. */
    public static final int KEYCODE_O               = 43;
    /** Key code constant: 'P' key. */
    public static final int KEYCODE_P               = 44;
    /** Key code constant: 'Q' key. */
    public static final int KEYCODE_Q               = 45;
    /** Key code constant: 'R' key. */
    public static final int KEYCODE_R               = 46;
    /** Key code constant: 'S' key. */
    public static final int KEYCODE_S               = 47;
    /** Key code constant: 'T' key. */
    public static final int KEYCODE_T               = 48;
    /** Key code constant: 'U' key. */
    public static final int KEYCODE_U               = 49;
    /** Key code constant: 'V' key. */
    public static final int KEYCODE_V               = 50;
    /** Key code constant: 'W' key. */
    public static final int KEYCODE_W               = 51;
    /** Key code constant: 'X' key. */
    public static final int KEYCODE_X               = 52;
    /** Key code constant: 'Y' key. */
    public static final int KEYCODE_Y               = 53;
    /** Key code constant: 'Z' key. */
    public static final int KEYCODE_Z               = 54;
    /** Key code constant: Left Alt modifier key. */
    public static final int KEYCODE_ALT_LEFT        = 57;
    /** Key code constant: Right Alt modifier key. */
    public static final int KEYCODE_ALT_RIGHT       = 58;
    /** Key code constant: Left Shift modifier key. */
    public static final int KEYCODE_SHIFT_LEFT      = 59;
    /** Key code constant: Right Shift modifier key. */
    public static final int KEYCODE_SHIFT_RIGHT     = 60;    
    /** Key code constant: Tab key. */
    public static final int KEYCODE_TAB             = 61;
    /** Key code constant: Space key. */
    public static final int KEYCODE_SPACE           = 62;
    
    public static final int KEYCODE_ENTER           = 66;    
    /** Key code constant: Escape key. */
    public static final int KEYCODE_ESCAPE          = 111;
    /** Key code constant: Left Control modifier key. */
    public static final int KEYCODE_CTRL_LEFT       = 113;    
    private int[][] mapPos;
    private void draw() {  
        mCanvas = mHolder.lockCanvas(); 
        Log.d(TAG, " draw:"+mCanvas);        
        if (mCanvas != null) {  
            mCanvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);            
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStrokeWidth(10);
            paint.setTextSize(40);
            paint.setStyle(Style.FILL);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float top = fontMetrics.top;
            float bottom = fontMetrics.bottom;

            for (int i = 0; i < mapV.length; i++){
                    paint.setColor(Color.BLACK);
                    paint.setAlpha(102);                    
                    mCanvas.drawCircle(keyMapX[i],keyMapY[i], 30, paint);
                    int baseLineY = (int) (keyMapY[i] - top/2 - bottom/2);
                    paint.setColor(Color.WHITE);
                    paint.setAlpha(102);                    
                    mCanvas.drawText(mapV[i], keyMapX[i],baseLineY,  paint);
            }
            //mHolder.unlockCanvasAndPost(mCanvas);  
        }  
    }  

    private class Position{
        public int x;
        public int y;
        public Position(int _x, int _y){
            x = _x;
            y = _y;
        }
    }

    private Position convert2XY(int keyCode, int action){
        final int baseX = 302;
        final int baseY = 809;
        final int step = 10;
        for(int i=0; i< keyV.length; i++){
            if(keyCode == keyV[i]){
                int x = keyMapX[i];
                int y= keyMapY[i];
/*
                if(action == MotionEvent.ACTION_DOWN && (keyCode == KEYCODE_W || keyCode == KEYCODE_S ||keyCode == KEYCODE_D ||keyCode == KEYCODE_A)){
                    x = 302;
                    y = 809;
                }

                if(mLastConvertX == -1 && mLastConvertY == -1 
                    && action == MotionEvent.ACTION_MOVE 
                    && (keyCode == KEYCODE_W || keyCode == KEYCODE_S ||keyCode == KEYCODE_D ||keyCode == KEYCODE_A)){
                    mLastConvertX = baseX;
                    mLastConvertY = baseY;
                    Log.d(TAG, " convert2XY reset  Move LastConvertXY:"+mLastConvertX+"-"+mLastConvertY);
                }

                if(action == MotionEvent.ACTION_MOVE){
                    if(keyCode == KEYCODE_W){
                        mLastConvertY -= step;
                        x = mLastConvertX;
                        y = mLastConvertY;
                    }else if(keyCode == KEYCODE_S){
                        mLastConvertY += step;
                        x = mLastConvertX;
                        y = mLastConvertY;
                    }else if(keyCode == KEYCODE_D){
                        mLastConvertX  += step;
                        x = mLastConvertX;
                        y = mLastConvertY;                        
                    }else if(keyCode == KEYCODE_A){
                        mLastConvertX  -= step;
                        x = mLastConvertX;
                        y = mLastConvertY;                        
                    }
                    Log.d(TAG, " convert2XY new convert last XY:"+mLastConvertX+"-"+mLastConvertY +" xy:"+x+"-"+y);
                    if(mLastConvertX >= 600 ||mLastConvertX <= 50 || mLastConvertY >= 1100 || mLastConvertY <= 530){
                        return null;
                    }
                }else if(action == MotionEvent.ACTION_UP){
                    mLastConvertX = -1;
                    mLastConvertY = -1;
                    Log.d(TAG, " convert2XY reset UP  LastConvertXY:"+mLastConvertX+"-"+mLastConvertY);
                }
*/
                Log.d(TAG, " convert2XY Select "+mapV[i]+" convert XY:"+x+"-"+y);
                return new Position(x, y);
            }
        }
        return null;
    }

   @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x=(int) event.getX();
        int y= (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, Log.getStackTraceString(new Throwable()));
                Log.d(TAG, "onTouchEvent: down "+x+"-"+y);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: move"+x+"-"+y);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent up :"+x+"-"+y);
                break;
            default:
                Log.d(TAG, "onTouchEvent other: "+x+"-"+y);
                break;
        }
        
        return false;
    }

  private Window mWindow ;
  public void hookPhoneWindowForClickEvent(Window window) {
    final Window.Callback originCallback = window.getCallback();
    mWindow = window;
    //ViewRootImpl viewRootImpl = window.getDecorView().getViewRootImpl();
    InvocationHandler callbackHandler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("dispatchKeyEvent")) {
          KeyEvent event = (KeyEvent) args[0];
          final int keyCode = event.getKeyCode();
          final int action = event.getAction();
          final boolean isDown = action == KeyEvent.ACTION_DOWN;
          Log.d(TAG, Log.getStackTraceString(new Throwable()));
          Log.i(TAG,"dispatchKeyEvent " + (isDown?" KeyDown ":" KeyUp ")+" keyCode:"+keyCode);
          switch (action) {
            case KeyEvent.ACTION_UP: {
              //onKeyUp(event);
              break;
            }
            case KeyEvent.ACTION_DOWN: {
              //onKeyDown(event);
              break;
            }
          }
        }
        Object obj = null;
        if (originCallback != null) {
          obj = method.invoke(originCallback, args);
        }
        return obj;
      }
    };

    Window.Callback newCallback =
        (Window.Callback) Proxy.newProxyInstance(window.getClass().getClassLoader(),
            new Class[] {Window.Callback.class}, callbackHandler);

    window.setCallback(newCallback);
  }

    private boolean onKeyDown(KeyEvent event) {
        final int keyCode = event.getKeyCode();
        Log.d(TAG, "onKeyDown: keyCode:"+keyCode+"event:"+event);
        Position pos = convert2XY(keyCode, 1);
        if(pos == null)
            return false;
        long downTime = SystemClock.uptimeMillis();
        MotionEvent montionEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, pos.x, pos.y, 0);
        mWindow.getDecorView().dispatchTouchEvent(montionEvent);
        montionEvent.recycle();
        return true;
    }

    private boolean onKeyUp(KeyEvent event) {
        final int keyCode = event.getKeyCode();
        Log.d(TAG, "onKeyUp: keyCode:"+keyCode+"event:"+event);
        Position pos = convert2XY(keyCode, 0);
        if(pos == null)
            return false;        
        long upTime = SystemClock.uptimeMillis();
        MotionEvent montionEvent = MotionEvent.obtain(upTime,upTime, MotionEvent.ACTION_UP, pos.x, pos.y, 0);
        mWindow.getDecorView().dispatchTouchEvent(montionEvent);
        montionEvent.recycle();
        return true;
    }

    private  MotionEvent convertKey2Montion(KeyEvent event) {
        final int action = event.getAction();
        final boolean isDown = action == KeyEvent.ACTION_DOWN;        
        final int keyCode = event.getKeyCode();
        Log.d(TAG, "convertKey2Montion: keyCode:"+keyCode+"event:"+event);

        int motionAction = MotionEvent.ACTION_DOWN;
        if(mKeyDownPress && action == KeyEvent.ACTION_DOWN)
            motionAction = MotionEvent.ACTION_MOVE;
        else if(action == KeyEvent.ACTION_UP){
            motionAction = MotionEvent.ACTION_UP;
        }

        Position pos = convert2XY(keyCode, motionAction);
        if(pos == null)
            return null;

        Log.d(TAG, "convertKey2Montion: mKeyDownPress:"+mKeyDownPress+" action:"+action+", this:"+this);
        //if(mKeyDownPress == isDown)
        //    return null;

        if(action == KeyEvent.ACTION_DOWN)
            mKeyDownPress = true;
        else if(action == KeyEvent.ACTION_UP)
            mKeyDownPress = false;    

        long upTime = SystemClock.uptimeMillis();
        MotionEvent montionEvent = MotionEvent.obtain(upTime,upTime, motionAction, pos.x, pos.y, 0);
        Log.d(TAG, "convertKey2Montion new montionEvent for key:"+keyCode+" mKeyDownPress:"+mKeyDownPress+" to:"+montionEvent+", this:"+this);
        return montionEvent;
    }

    private  MotionEvent convertVoiceCmd2Montion(String cmd, int action) {
        int index;
        for(index=0; index< mapCmd.length; index++){
            Log.d(TAG, "convertVoiceCmd2Montion cmd:"+cmd+" mapCmd[index]:"+mapCmd[index]);
            if(cmd.equals(mapCmd[index])){
                break;
            }
        }
        if(index >= mapCmd.length){
            return null;
        }
        Position pos = convert2XY(cmdMapKey[index], action);
        if(pos != null){
            long upTime = SystemClock.uptimeMillis();
            MotionEvent montionEvent = MotionEvent.obtain(upTime,upTime, action, pos.x, pos.y, 0);
            Log.d(TAG, "convertVoiceCmd2Montion cmd:"+cmd+" action:"+action+", index:"+index+" event:"+montionEvent);
            return montionEvent;
        }
        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Let the focused view and/or our descendants get the key first
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return super.dispatchKeyEvent(event);
    }

    public static Field sFIELD_sDefaultWindowManager_CLASS_WINDOW_MANAGER_GLOBAL;
  
    public void hookViewRootImplForKeyEvent(Context context, Object viewRoot) {
        try {
            Log.d(TAG, " enter  hookViewRootImplForKeyEvent !");
            ReflectCommon.init(context);
            InvocationHandler viewRootProxyHandler = getViewRootImplProxyHandler(null, viewRoot);
            Log.d(TAG, "  hookViewRootImplForKeyEvent  windowManagerProxyHandler:"+viewRootProxyHandler);
            File dir = context.getDir("dx", Context.MODE_PRIVATE);
            Log.d(TAG, "  hookViewRootImplForKeyEvent  dir:"+dir);
            Object proxyViewRootProxy =
                ProxyBuilder.forClass(ReflectCommon.sCLASS_VIEW_ROOT_IMPL)
                    .dexCache(dir)
                    .handler(viewRootProxyHandler)
                    .build();
            Log.d(TAG, "  hookViewRootImplForKeyEvent  proxyWindowManagerGlobal:"+proxyViewRootProxy);
            //viewRoot.set(null, proxyViewRootProxy);

        } catch (Throwable throwable) {
            Log.e(TAG, "error in hook window manager global.", throwable);
        }
        Log.d(TAG, " exit  hookViewRootImplForKeyEvent !");
    }


  //@NonNull
  public InvocationHandler getViewRootImplProxyHandler(final Object inputEventReceiverObject, final Object originObject) {
    return new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //Log.d(TAG, " catch  invoke  method :"+method.getName());

        if("onProcess".equals(method.getName())){
            //Log.d(TAG, Log.getStackTraceString(new Throwable()));
            Object queuedInputEvent =  args[0];
            Field  eventField = ReflectCommon.sCLASS_QUEUED_INPUT_EVENT.getDeclaredField("mEvent");
            eventField.setAccessible(true);
            Object event = (Object) eventField.get(queuedInputEvent);
            Log.d(TAG, " catch invoke get  event:"+event);
            if(event != null && event instanceof KeyEvent){
                KeyEvent keyEvent = (KeyEvent)event;
                int keyCode = keyEvent.getKeyCode();
                if(keyCode != KEYCODE_W && keyCode != KEYCODE_S && keyCode != KEYCODE_D && keyCode != KEYCODE_A){
                    final int action = keyEvent.getAction();
                    final boolean isDown = action == KeyEvent.ACTION_DOWN;
                    MotionEvent motionEvent = convertKey2Montion(keyEvent);
                    if(motionEvent != null){
                        ReflectCommon.sMethod_onInputEvent_WINDOW_INPUT_EVENT_RECEIVER.invoke(inputEventReceiverObject, motionEvent);
                        Log.d(TAG, " catch  invoke  get  keyEvent:"+keyEvent +" motionEvent:" +motionEvent);
                        //Object event1 = (Object) eventField.get(queuedInputEvent);// just debug
                        //Log.d(TAG, " catch invoke get  new event:"+event1);
                  }
              }
          }
        }
        method.setAccessible(true);
        Object ret = method.invoke(originObject, args);        
        return ret;
      }
    };
  }

    public static boolean mDynamicProxyInit = false;
    public void tryDynamicViewProxy(View view){
        if(mFirstInputStageObj != null){
            Log.d(TAG, "mDynamicProxyInit  has hook success !!!");
            return;
        }

        try {
            ClassHack.setup();
            ReflectCommon.init(mContext);
            Method method = ReflectCommon.sCLASS_VIEW.getDeclaredMethod("getViewRootImpl");
            Log.d(TAG, " catch tryDynamicProxy get method:"+method);
            method.setAccessible(true);
            Object viewRoot = method.invoke(view);
            Log.d(TAG, " catch tryDynamicProxy get viewRoot:"+viewRoot);

            mFirstInputStageObj = (Object) ReflectCommon.sFIELD_mFirstInputStage_VIEWROOT_IMPL.get(viewRoot);
            Log.d(TAG, " catch tryDynamicProxy get  firstInputStage:"+mFirstInputStageObj);

            Field traceCounterField = ReflectCommon.sCLASS_NATIVE_PRE_IME_INPUT_STAGE.getSuperclass().getDeclaredField("mTraceCounter");
            Log.d(TAG, " catch tryDynamicProxy get traceCounterField :"+traceCounterField);
            traceCounterField.setAccessible(true);
            Object traceCounter = (Object) traceCounterField.get(mFirstInputStageObj);
            Log.d(TAG, " catch tryDynamicProxy get  traceCounter:"+traceCounter);

            Field nextField = ReflectCommon.sCLASS_NATIVE_PRE_IME_INPUT_STAGE.getSuperclass().getSuperclass().getDeclaredField("mNext");
            Log.d(TAG, " catch tryDynamicProxy get nextField :"+nextField);
            nextField.setAccessible(true);
            Object next = (Object) nextField.get(mFirstInputStageObj);
            Log.d(TAG, " catch tryDynamicProxy get  next:"+next);

            Field  receiverField = ReflectCommon.sCLASS_VIEW_ROOT_IMPL.getDeclaredField("mInputEventReceiver");
            Log.d(TAG, " catch tryDynamicProxy get receiverField :"+receiverField);
            receiverField.setAccessible(true);
            mWindowInputEventReceiver = (Object) receiverField.get(viewRoot);
            Log.d(TAG, " catch tryDynamicProxy get  receiver:"+mWindowInputEventReceiver);

/*
            Field  seqMapField = windowReceiverClass.getSuperclass().getDeclaredField("mSeqMap");
            seqMapField.setAccessible(true);
            
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(seqMapField, seqMapField.getModifiers() & ~Modifier.FINAL);

            Object seqMap = (Object) seqMapField.get(inputEventReceiver);
            Log.d(TAG, " catch tryDynamicProxy get seqMapField :"+seqMapField+" seqMap:"+seqMap);


            Method  onBatchedMethod = windowReceiverClass.getSuperclass().getDeclaredMethod("onBatchedInputEventPending");
            Log.d(TAG, " catch tryDynamicProxy get onBatchedMethod :"+onBatchedMethod);
            onBatchedMethod.setAccessible(true);
            Object onBatchedObj = onBatchedMethod.invoke(inputEventReceiver);
            Log.d(TAG, " catch tryDynamicProxy get  consumeBatchObj:"+onBatchedObj);

            Method  disposeMethod = windowReceiverClass.getSuperclass().getDeclaredMethod("dispose");
            Log.d(TAG, " catch tryDynamicProxy get disposeMethod :"+disposeMethod);
            disposeMethod.setAccessible(true);
            Object disposeObj = disposeMethod.invoke(inputEventReceiver);
            Log.d(TAG, " catch tryDynamicProxy get  disposeObj:"+disposeObj);
*/

            InvocationHandler proxyHandler = getViewRootImplProxyHandler(mWindowInputEventReceiver, mFirstInputStageObj);
            Log.d(TAG, "  hookViewRootImplForKeyEvent  proxyHandler:"+proxyHandler);
            ApplicationInfo info = VirtualCore.get().getUnHookPackageManager().getApplicationInfo(VirtualCore.get().getHostPkg(), 0);
            String path = "";
            if(info != null){
                path = info.dataDir;
            }
            Log.d(TAG, "  hookViewRootImplForKeyEvent  path:"+path);
            File dir = new File(path+"/app_dx");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Log.d(TAG, "  hookViewRootImplForKeyEvent  dir:"+dir);

         Object proxyObject =
             ProxyBuilder.forClass(ReflectCommon.sCLASS_NATIVE_PRE_IME_INPUT_STAGE)
                 .dexCache(dir)
                 .handler(proxyHandler)
                 .constructorArgTypes(new Class[] {ReflectCommon.sCLASS_VIEW_ROOT_IMPL, ReflectCommon.sCLASS_INPUT_STAGE, String.class})
                 .constructorArgValues(new Object[] {viewRoot, next, (String)"test"})
                 .build();
             Log.e(TAG, "set proxyObject."+ proxyObject +" to :"+viewRoot );
             ReflectCommon.sFIELD_mFirstInputStage_VIEWROOT_IMPL.set(viewRoot, proxyObject);
        } catch (Throwable throwable) {
            Log.e(TAG, "error in hook viewrootimpl global.", throwable);
        }
    }

    private void openRecognizer(){
        AsrDemo asr = AsrDemo.getInstance(mContext);
        asr.setRecoginzerCallBack(this);
        boolean ret = asr.startGrammar();
        if(!ret)
            Log.d(TAG, " startGrammar failed !!!");

        ret = asr.startRecognize();
        if(!ret)
            Log.d(TAG, " startRecognize failed !!!");
    }

    @Override
    public void startRecognizer(){
        Log.d(TAG, "  startRecognizer !");
    }

    @Override
    public void stopRecognizer(){
        Log.d(TAG, "  stopRecognizer !");
    }

    @Override
    public void recognizerResult(ArrayList<XunFeiDataItem> list){
        if(list != null){
            XunFeiDataItem item = list.get(0);
            int sc = item.getSc();
            String word = item.getWord();
            MotionEvent downEv = convertVoiceCmd2Montion(word, MotionEvent.ACTION_DOWN);
            forceDeliverMotionEvent(downEv);
            MotionEvent upEv = convertVoiceCmd2Montion(word, MotionEvent.ACTION_UP);
            forceDeliverMotionEvent(upEv);
        }
    }

    private boolean forceDeliverMotionEvent(MotionEvent motionEvent){
        if(mWindowInputEventReceiver != null && motionEvent != null){
            try{
                ReflectCommon.sMethod_onInputEvent_WINDOW_INPUT_EVENT_RECEIVER.invoke(mWindowInputEventReceiver, motionEvent);
                return true;
            }catch (Throwable throwable) {
                Log.e(TAG, "error in forceDeliverMotionEvent ", throwable);
            }
        }else{
            Log.d(TAG, " Param is unvalid!!!  mWindowInputEventReceiver:"+mWindowInputEventReceiver+" motionEvent:"+motionEvent);
        }
        return false;
    }
}
