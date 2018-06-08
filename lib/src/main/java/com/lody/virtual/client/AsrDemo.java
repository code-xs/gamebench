package com.lody.virtual.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.iflytek.sunflower.FlowerCollector;
import com.iflytek.cloud.SpeechUtility;
import com.lody.virtual.client.JsonParser;
import com.lody.virtual.client.FucUtil;
import java.util.ArrayList;

import com.lody.virtual.client.core.VirtualCore;
/**
 * 识别示例
 */
public class AsrDemo {
	private static String TAG = AsrDemo.class.getSimpleName();
	// 语音识别对象
	private SpeechRecognizer mAsr;
	//private Toast mToast;	
	// 缓存
	private SharedPreferences mSharedPreferences;
	// 云端语法文件
	private String mCloudGrammar = null;
	private String mContent;		
	private static final String KEY_GRAMMAR_ABNF_ID = "grammar_abnf_id";
	private static final String GRAMMAR_TYPE_ABNF = "abnf";
	private static final String GRAMMAR_TYPE_BNF = "bnf";

	private String mEngineType = SpeechConstant.TYPE_LOCAL; //SpeechConstant.TYPE_CLOUD;
	private String mLocalGrammar = null;
	private String mLocalLexicon = null;
	private String grmPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";
	private String mResultType = "json";

       private RecognizerCallBack mCallBack = null;

       public static AsrDemo mInstance = null;

        public AsrDemo(Context context){
               SpeechUtility.createUtility(context, "appid=5acdb3ae");
		mAsr = SpeechRecognizer.createRecognizer(context, mInitListener);
               Log.d(TAG, "init  mAsr :"+mAsr);
		mCloudGrammar = FucUtil.readFile(VirtualCore.get().getContext(),"grammar_sample.abnf","utf-8");
               Log.d(TAG, "init  mCloudGrammar :"+mCloudGrammar);
		// \u521d\u59cb\u5316\u8bed\u6cd5\u3001\u547d\u4ee4\u8bcd
		mLocalLexicon = "\u5f20\u6d77\u7f8a\n\u5218\u5a67\n\u738b\u950b\n";
		mLocalGrammar = FucUtil.readFile(VirtualCore.get().getContext(),"call.bnf", "utf-8");
               
		mSharedPreferences = context.getSharedPreferences(context.getPackageName(), context.MODE_PRIVATE);
		//mToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);	            
        }

        public static AsrDemo getInstance(Context context){
            if(mInstance == null)
                mInstance = new AsrDemo(context);
            return mInstance;
        }

        public void setRecoginzerCallBack(RecognizerCallBack callback){
            mCallBack = callback;
        }

        public boolean startGrammar(){
            int ret = 0;
            if(mAsr != null){
                if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
			mContent = new String(mLocalGrammar);
			mAsr.setParameter(SpeechConstant.PARAMS, null);
			// \u8bbe\u7f6e\u6587\u672c\u7f16\u7801\u683c\u5f0f
			mAsr.setParameter(SpeechConstant.TEXT_ENCODING,"utf-8");
			// \u8bbe\u7f6e\u5f15\u64ce\u7c7b\u578b
			mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
			// \u8bbe\u7f6e\u8bed\u6cd5\u6784\u5efa\u8def\u5f84
			mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
			//\u4f7f\u75288k\u97f3\u9891\u7684\u65f6\u5019\u8bf7\u89e3\u5f00\u6ce8\u91ca
//					mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
			// \u8bbe\u7f6e\u8d44\u6e90\u8def\u5f84
			mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
			ret = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, mContent, mCloudGrammarListener);
			if(ret != ErrorCode.SUCCESS){
				showTip("\u8bed\u6cd5\u6784\u5efa\u5931\u8d25,\u9519\u8bef\u7801\uff1a" + ret);
			}
		}
		// \u5728\u7ebf-\u6784\u5efa\u8bed\u6cd5\u6587\u4ef6\uff0c\u751f\u6210\u8bed\u6cd5id
		else {	
			mContent = new String(mCloudGrammar);
			// \u6307\u5b9a\u5f15\u64ce\u7c7b\u578b
			mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
			// \u8bbe\u7f6e\u6587\u672c\u7f16\u7801\u683c\u5f0f
			mAsr.setParameter(SpeechConstant.TEXT_ENCODING,"utf-8");
		    ret = mAsr.buildGrammar(GRAMMAR_TYPE_ABNF, mContent, mCloudGrammarListener);
			if(ret != ErrorCode.SUCCESS)
				showTip("\u8bed\u6cd5\u6784\u5efa\u5931\u8d25,\u9519\u8bef\u7801\uff1a" + ret);
		}
               Log.d(TAG, "startGrammar ret:"+ret);
               if(ret == ErrorCode.SUCCESS)
                   return true;
            }
            return false;
        }

        public boolean startRecognize(){
            if(mAsr != null){
                if (!setParam()) {
                    showTip("请先构建语法。");
                    return false;
                };                
                int ret = mAsr.startListening(mRecognizerListener);
                Log.d(TAG, "startRecognize ret:"+ret);
                if (ret == ErrorCode.SUCCESS){
                    return true;
                }
            }
            return false;
        }

        public boolean stopListening(){
            if(mAsr != null){
                Log.d(TAG, "stopListening!");
                mAsr.stopListening();
                return true;
            }
            return false;
        }
        public boolean cancelListening(){
            if(mAsr != null){
                Log.d(TAG, "cancelListening!");
                mAsr.cancel();
                return true;
            }
            return false;
        }
        
	// 语法、词典临时变量
    /*
    @Override
	public void onClick(View view) {		
    	if( null == mAsr ){
			// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
			this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
			return;
		}
    	
		if(null == mEngineType) {
			showTip("请先选择识别引擎类型");
			return;
		}	
		switch(view.getId())
		{
			case R.id.isr_grammar:
				showTip("上传预设关键词/语法文件");
				// 在线-构建语法文件，生成语法id
				//((EditText)findViewById(R.id.isr_text)).setText(mCloudGrammar);
				mContent = new String(mCloudGrammar);
				//指定引擎类型
				mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
				mAsr.setParameter(SpeechConstant.TEXT_ENCODING,"utf-8");
				ret = mAsr.buildGrammar(GRAMMAR_TYPE_ABNF, mContent, mCloudGrammarListener);
				if(ret != ErrorCode.SUCCESS)
					showTip("语法构建失败,错误码：" + ret);

				break;
			// 开始识别
			case R.id.isr_recognize:
				((EditText)findViewById(R.id.isr_text)).setText(null);// 清空显示内容
				// 设置参数
				if (!setParam()) {
					showTip("请先构建语法。");
					return;
				};
				
				ret = mAsr.startListening(mRecognizerListener);
				if (ret != ErrorCode.SUCCESS) {
					showTip("识别失败,错误码: " + ret);
				}
				break;
			// 停止识别
			case R.id.isr_stop:
				mAsr.stopListening();
				showTip("停止识别");
				break;
			// 取消识别
			case R.id.isr_cancel:
				mAsr.cancel();
				showTip("取消识别");
				break;
		}
	}*/
	
	/**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code+" success code:"+ErrorCode.SUCCESS);
        }
    };
    	
	/**
     * 更新词典监听器。
     */
	private LexiconListener mLexiconListener = new LexiconListener() {
		@Override
		public void onLexiconUpdated(String lexiconId, SpeechError error) {
		        Log.d(TAG, "onLexiconUpdated!lexiconId:"+lexiconId+", error:"+error);
			if(error == null){
				showTip("词典更新成功");
			}else{
				showTip("词典更新失败,错误码："+error.getErrorCode());
			}
		}
	};
	
	/**
     * 云端构建语法监听器。
     */
	private GrammarListener mCloudGrammarListener = new GrammarListener() {
		@Override
		public void onBuildFinish(String grammarId, SpeechError error) {
		        Log.d(TAG, "onBuildFinish!grammarId:"+grammarId+", error:"+error);
			if(error == null){
				String grammarID = new String(grammarId);
				Editor editor = mSharedPreferences.edit();
				if(!TextUtils.isEmpty(grammarId))
					editor.putString(KEY_GRAMMAR_ABNF_ID, grammarID);
				editor.commit();
				showTip("语法构建成功：" + grammarId);
                             startRecognize();
			}else{
				showTip("语法构建失败,错误码：" + error.getErrorCode());
			}
		}
	};

	/**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
        	//Log.d(TAG, "onVolumeChanged:"+data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
        	if (null != result) {
        		Log.d(TAG, "recognizer result:" + result.getResultString());
        		String text  = null;
                       ArrayList<XunFeiDataItem> list  = null;
        		if("cloud".equalsIgnoreCase(mEngineType) || mResultType.equals("json")){
        			list = JsonParser.parseGrammarResult(result.getResultString());
                              if(mCallBack != null)
                                mCallBack.recognizerResult(list);
        		}else {
        			text = JsonParser.parseLocalGrammarResult(result.getResultString());
        		}
        		Log.d(TAG, "recognizer JsonParser result :"+text+" list:"+list);
        	} else {
        		Log.d(TAG, "recognizer result : null");
        	}
               startRecognize();
        }
        
        @Override
        public void onEndOfSpeech() {
               //Log.d(TAG, "onEndOfSpeech");
            showTip("\u7ed3\u675f\u8bf4\u8bdd");
            if(mCallBack != null)
                mCallBack.stopRecognizer();
        }
        
        @Override
        public void onBeginOfSpeech() {
            //Log.d(TAG, "onBeginOfSpeech");
            showTip("\u5f00\u59cb\u8bf4\u8bdd");
            if(mCallBack != null)
                mCallBack.startRecognizer();
        }

	@Override
	public void onError(SpeechError error) {
		showTip("onError Code:"+ error.getErrorCode());
               if(error.getErrorCode() == 10119 ||error.getErrorCode() == 10710 || error.getErrorCode() == 20005){
                    startRecognize();
               }
	}

	@Override
	public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
		// 若使用本地能力，会话id为null
		//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
		//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
		//		Log.d(TAG, "session id =" + sid);
		//	}
	}

    };
    
	private void showTip(final String str) {
            Log.d(TAG, "showTip str:"+str);
	}

	public boolean setParam(){
		boolean result = false;
		// \u6e05\u7a7a\u53c2\u6570
		mAsr.setParameter(SpeechConstant.PARAMS, null);
		// \u8bbe\u7f6e\u8bc6\u522b\u5f15\u64ce
		mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		if("cloud".equalsIgnoreCase(mEngineType))
		{
			String grammarId = mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID, null);
			if(TextUtils.isEmpty(grammarId))
			{
				result =  false;
			}else {
				// \u8bbe\u7f6e\u8fd4\u56de\u7ed3\u679c\u683c\u5f0f
				mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
				// \u8bbe\u7f6e\u4e91\u7aef\u8bc6\u522b\u4f7f\u7528\u7684\u8bed\u6cd5id
				mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
				result =  true;
			}
		}
		else
		{
			// \u8bbe\u7f6e\u672c\u5730\u8bc6\u522b\u8d44\u6e90
			mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
			// \u8bbe\u7f6e\u8bed\u6cd5\u6784\u5efa\u8def\u5f84
			mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
			// \u8bbe\u7f6e\u8fd4\u56de\u7ed3\u679c\u683c\u5f0f
			mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
			// \u8bbe\u7f6e\u672c\u5730\u8bc6\u522b\u4f7f\u7528\u8bed\u6cd5id
			mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");
			// \u8bbe\u7f6e\u8bc6\u522b\u7684\u95e8\u9650\u503c
			mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
			// \u4f7f\u75288k\u97f3\u9891\u7684\u65f6\u5019\u8bf7\u89e3\u5f00\u6ce8\u91ca
//			mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
			result = true;
		}
		
		// \u8bbe\u7f6e\u97f3\u9891\u4fdd\u5b58\u8def\u5f84\uff0c\u4fdd\u5b58\u97f3\u9891\u683c\u5f0f\u652f\u6301pcm\u3001wav\uff0c\u8bbe\u7f6e\u8def\u5f84\u4e3asd\u5361\u8bf7\u6ce8\u610fWRITE_EXTERNAL_STORAGE\u6743\u9650
		// \u6ce8\uff1aAUDIO_FORMAT\u53c2\u6570\u8bed\u8bb0\u9700\u8981\u66f4\u65b0\u7248\u672c\u624d\u80fd\u751f\u6548
		mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/asr.wav");
		return result;
	}
	/**
	 * 参数设置
	 * @return
	 */

      /*
	public boolean setParam(){
		boolean result = false;
		//设置识别引擎
		mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		//设置返回结果为json格式
		mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");

		if("cloud".equalsIgnoreCase(mEngineType))
		{
			String grammarId = mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID, null);
			if(TextUtils.isEmpty(grammarId))
			{
				result =  false;
			}else {
				//设置云端识别使用的语法id
				mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
				result =  true;
			}
		}

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/asr.wav");
                Log.d(TAG, "setParam ret:"+result);
		return result;
	}*/

	public void onDestroy() {
            if( null != mAsr ){
                // 退出时释放连接
                mAsr.cancel();
                mAsr.destroy();
            }
	}
	
	public void onResume() {
		//移动数据统计分析
		//FlowerCollector.onResume(AsrDemo.this);
		//FlowerCollector.onPageStart(TAG);
		//super.onResume();
	}
	
	public void onPause() {
		//移动数据统计分析
		//FlowerCollector.onPageEnd(TAG);
		//FlowerCollector.onPause(AsrDemo.this);
		//super.onPause();
	}

	private String getResourcePath(){
		StringBuffer tempBuffer = new StringBuffer();
		//\u8bc6\u522b\u901a\u7528\u8d44\u6e90
		tempBuffer.append(ResourceUtil.generateResourcePath(VirtualCore.get().getContext(), RESOURCE_TYPE.assets, "common.jet"));
		//\u8bc6\u522b8k\u8d44\u6e90-\u4f7f\u75288k\u7684\u65f6\u5019\u8bf7\u89e3\u5f00\u6ce8\u91ca
//		tempBuffer.append(";");
//		tempBuffer.append(ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, "asr/common_8k.jet"));
		return tempBuffer.toString();
	}    
	
}
