package com.remi.navidrawer;

import android.util.Log;

import java.util.ArrayList;

public class TimeWindowContainer {
    private ArrayList<TimeWindow> timeWindowArrayList;
    public long start;
    public long end;
    int count = 1;
    public  TimeWindowContainer() {
        timeWindowArrayList = new ArrayList<TimeWindow>();
    }

    public void addTimeWindow(TimeWindow timeWindow) {
        timeWindowArrayList.add(timeWindow);
    }

    public void removeTimeWindow(TimeWindow timeWindow) {
        timeWindowArrayList.remove(timeWindow);
    }

    public boolean isInTimeWindowContainer(long time){
        for(TimeWindow timeWindow : timeWindowArrayList){
            if (timeWindow.isInWindow(time)){
                return true;
            }
        }
        return false;
    }

    public void add(long time){
        if (count ==1)
        {
            this.start = time;
            count++;
        }
        if (count == 2){
            this.end = time;
            Log.d("Inside Container", "Test success");
            timeWindowArrayList.add(new TimeWindow(start,end));
            count = 1;
        }
    }
}
