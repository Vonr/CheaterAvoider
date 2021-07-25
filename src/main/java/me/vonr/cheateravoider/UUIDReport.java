package me.vonr.cheateravoider;

import java.util.ArrayList;

public class UUIDReport extends Report {
    public String username;

    public UUIDReport(String username, long timestamp, ArrayList<String> reasons) {
        super(timestamp, reasons);
        this.username = username;
    }
}
