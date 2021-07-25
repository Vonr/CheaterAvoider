package me.vonr.cheateravoider;

import java.util.ArrayList;

public class Report {
    public long timestamp;
    public ArrayList<String> reasons;

    public Report(long timestamp, ArrayList<String> reasons) {
        this.timestamp = timestamp;
        this.reasons = reasons;
    }
}
