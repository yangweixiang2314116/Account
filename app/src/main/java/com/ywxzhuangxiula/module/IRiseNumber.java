package com.ywxzhuangxiula.module;

/**
 * Created by Administrator on 2016/10/8.
 */
public interface  IRiseNumber {
    public void start();
    public void withNumber(int number);
    public void withNumber(float number);
    public void setFromAndEndNumber(int fromNumber, int endNumber);
    public void setFromAndEndNumber(float fromNumber, float endNumber);
    public void setDuration(long duration);
    public void setOnEndListener(NumberScrollTextView.EndListener callback);
}
