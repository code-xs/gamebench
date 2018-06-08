package com.lody.virtual.client;

import java.util.ArrayList;

public interface RecognizerCallBack{
    public void startRecognizer();
    public void stopRecognizer();
    public void recognizerResult(ArrayList<XunFeiDataItem> list);
}