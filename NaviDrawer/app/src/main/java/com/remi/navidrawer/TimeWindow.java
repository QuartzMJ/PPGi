package com.remi.navidrawer;

public class TimeWindow
{
    private long windowBegin;
    private long windowEnd;

    public TimeWindow(){}
    public TimeWindow(long start, long end){
        windowBegin = start;
        windowEnd = end;
    }
    public void setWindowBegin(long windowBegin)
    {
        this.windowBegin = windowBegin;
    }

    public void setWindowEnd(long windowEnd){
        this.windowEnd = windowEnd;
    }

    public long getWindowBegin(){
        return windowBegin;
    }

    public long getWindowEnd(){
        return windowEnd;
    }

    public boolean isInWindow(long timing)
    {
        return timing < windowEnd && timing > windowBegin;
    }
}
