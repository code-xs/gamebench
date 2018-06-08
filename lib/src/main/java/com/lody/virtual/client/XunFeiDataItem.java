package com.lody.virtual.client;

import android.util.Log;

class XunFeiDataItem {
    private int mSc = -1;
    private String mGm ;
    private String mWord ;

    public XunFeiDataItem(int sc, String gm, String word){
        mSc = sc;
        mGm = gm;
        mWord = word;
    }

    public int getSc(){
        return mSc;
    }

    public String getGm(){
        return mGm;
    }

    public String getWord(){
        return mWord;
    }

    public String toString(){
        return "{ sc:"+mSc+" gm:"+mGm+" w:"+mWord+"}";
    }
}